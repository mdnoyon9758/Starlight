"""Main FastAPI application."""

import asyncio
from contextlib import asynccontextmanager

from fastapi import FastAPI
from fastapi.exceptions import RequestValidationError
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from starlette.exceptions import HTTPException
from starlette.middleware.sessions import SessionMiddleware

from app.api.middleware import LoggingMiddleware, RateLimitMiddleware
from app.api.v1 import api_router
from app.core.cache import cache
from app.core.config import get_settings
from app.core.exceptions import (
    StarLightException,
    general_exception_handler,
    http_exception_handler,
    starlight_exception_handler,
    validation_exception_handler,
)
from app.core.logging import setup_logging
from app.core.security.security_headers import SecurityHeadersMiddleware
from monitoring.prometheus.metrics import start_metrics_server

settings = get_settings()


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifespan events."""
    # Startup
    setup_logging()
    await cache.connect()
    
    if settings.enable_metrics:
        # Start metrics server in background
        asyncio.create_task(
            asyncio.to_thread(start_metrics_server, settings.metrics_port)
        )
    
    yield
    
    # Shutdown
    await cache.disconnect()


# Create FastAPI application
app = FastAPI(
    title=settings.app_name,
    version=settings.app_version,
    description=settings.app_description,
    openapi_url=f"{settings.api_v1_str}/openapi.json",
    docs_url=f"{settings.api_v1_str}/docs",
    redoc_url=f"{settings.api_v1_str}/redoc",
    lifespan=lifespan,
)

# Add exception handlers
app.add_exception_handler(StarLightException, starlight_exception_handler)
app.add_exception_handler(HTTPException, http_exception_handler)
app.add_exception_handler(RequestValidationError, validation_exception_handler)
app.add_exception_handler(Exception, general_exception_handler)

# Add middleware (order matters!)
app.add_middleware(SecurityHeadersMiddleware)
app.add_middleware(LoggingMiddleware)
app.add_middleware(RateLimitMiddleware)

app.add_middleware(
    CORSMiddleware,
    allow_origins=[str(origin) for origin in settings.backend_cors_origins],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.add_middleware(
    SessionMiddleware,
    secret_key=settings.secret_key,
)

app.add_middleware(
    TrustedHostMiddleware,
    allowed_hosts=["*"] if settings.is_development else ["api.starlight.com"],
)

# Include routers
app.include_router(api_router, prefix=settings.api_v1_str)


@app.get("/")
async def root():
    """Root endpoint."""
    return {
        "message": "Welcome to Starlight API",
        "version": settings.app_version,
        "environment": settings.environment,
        "docs": f"{settings.api_v1_str}/docs",
        "redoc": f"{settings.api_v1_str}/redoc",
        "metrics": f"http://localhost:{settings.metrics_port}/metrics" if settings.enable_metrics else None,
    }


@app.get("/health")
async def health_check():
    """Health check endpoint."""
    # Check database connection
    try:
        from app.infrastructure.database import engine
        async with engine.connect() as conn:
            await conn.execute("SELECT 1")
        db_status = "healthy"
    except Exception:
        db_status = "unhealthy"
    
    # Check Redis connection
    try:
        await cache.redis.ping() if cache.redis else None
        cache_status = "healthy" if cache.redis else "disconnected"
    except Exception:
        cache_status = "unhealthy"
    
    return {
        "status": "healthy" if db_status == "healthy" and cache_status == "healthy" else "degraded",
        "version": settings.app_version,
        "environment": settings.environment,
        "services": {
            "database": db_status,
            "cache": cache_status,
        },
    }

