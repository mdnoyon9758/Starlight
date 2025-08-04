"""Rate limiting middleware."""

import time
from typing import Callable

from fastapi import HTTPException, Request, Response, status
from starlette.middleware.base import BaseHTTPMiddleware
from starlette.types import ASGIApp

from app.core.cache import cache
from app.core.config import get_settings
from app.core.logging import get_logger

settings = get_settings()
logger = get_logger(__name__)


class RateLimitMiddleware(BaseHTTPMiddleware):
    """Rate limiting middleware using Redis."""
    
    def __init__(self, app: ASGIApp):
        super().__init__(app)
    
    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        """Apply rate limiting to requests."""
        client_ip = request.client.host if request.client else "unknown"
        
        # Skip rate limiting for health checks
        if request.url.path in ["/health", "/"]:
            return await call_next(request)
        
        # Create rate limit key
        current_minute = int(time.time() // 60)
        rate_limit_key = f"rate_limit:{client_ip}:{current_minute}"
        
        try:
            # Get current request count
            current_requests = await cache.get(rate_limit_key) or 0
            
            if current_requests >= settings.rate_limit_per_minute:
                logger.warning(
                    "Rate limit exceeded",
                    client_ip=client_ip,
                    requests=current_requests,
                    limit=settings.rate_limit_per_minute
                )
                raise HTTPException(
                    status_code=status.HTTP_429_TOO_MANY_REQUESTS,
                    detail="Rate limit exceeded. Please try again later.",
                    headers={"Retry-After": "60"}
                )
            
            # Increment request count
            await cache.set(rate_limit_key, current_requests + 1, expire=60)
            
            response = await call_next(request)
            
            # Add rate limit headers
            response.headers["X-RateLimit-Limit"] = str(settings.rate_limit_per_minute)
            response.headers["X-RateLimit-Remaining"] = str(
                max(0, settings.rate_limit_per_minute - current_requests - 1)
            )
            response.headers["X-RateLimit-Reset"] = str((current_minute + 1) * 60)
            
            return response
            
        except HTTPException:
            raise
        except Exception as e:
            logger.error("Rate limiting error", error=str(e), client_ip=client_ip)
            # Continue without rate limiting if there's an error
            return await call_next(request)
