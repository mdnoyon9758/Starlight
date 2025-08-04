"""Exception handlers."""

from typing import Union

from fastapi import HTTPException, Request, status
from fastapi.exceptions import RequestValidationError
from fastapi.responses import JSONResponse
from starlette.exceptions import HTTPException as StarletteHTTPException

from app.core.logging import get_logger
from monitoring.prometheus.metrics import error_count

logger = get_logger(__name__)


class StarLightException(Exception):
    """Base exception for StarLight application."""
    
    def __init__(self, message: str, status_code: int = 500, details: dict = None):
        self.message = message
        self.status_code = status_code
        self.details = details or {}
        super().__init__(self.message)


class AuthenticationError(StarLightException):
    """Authentication related errors."""
    
    def __init__(self, message: str = "Authentication failed", details: dict = None):
        super().__init__(message, status.HTTP_401_UNAUTHORIZED, details)


class AuthorizationError(StarLightException):
    """Authorization related errors."""
    
    def __init__(self, message: str = "Insufficient permissions", details: dict = None):
        super().__init__(message, status.HTTP_403_FORBIDDEN, details)


class ValidationError(StarLightException):
    """Validation related errors."""
    
    def __init__(self, message: str = "Validation failed", details: dict = None):
        super().__init__(message, status.HTTP_422_UNPROCESSABLE_ENTITY, details)


class NotFoundError(StarLightException):
    """Resource not found errors."""
    
    def __init__(self, message: str = "Resource not found", details: dict = None):
        super().__init__(message, status.HTTP_404_NOT_FOUND, details)


class ConflictError(StarLightException):
    """Conflict errors."""
    
    def __init__(self, message: str = "Resource conflict", details: dict = None):
        super().__init__(message, status.HTTP_409_CONFLICT, details)


async def starlight_exception_handler(
    request: Request, exc: StarLightException
) -> JSONResponse:
    """Handle StarLight custom exceptions."""
    error_count.labels(
        error_type=exc.__class__.__name__,
        endpoint=request.url.path
    ).inc()
    
    logger.error(
        "StarLight exception occurred",
        exception=exc.__class__.__name__,
        message=exc.message,
        status_code=exc.status_code,
        path=request.url.path,
        method=request.method,
        details=exc.details
    )
    
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "error": {
                "type": exc.__class__.__name__,
                "message": exc.message,
                "details": exc.details,
                "path": request.url.path,
                "timestamp": logger._context.get("timestamp")
            }
        }
    )


async def http_exception_handler(
    request: Request, exc: Union[HTTPException, StarletteHTTPException]
) -> JSONResponse:
    """Handle HTTP exceptions."""
    error_count.labels(
        error_type="HTTPException",
        endpoint=request.url.path
    ).inc()
    
    logger.warning(
        "HTTP exception occurred",
        status_code=exc.status_code,
        detail=exc.detail,
        path=request.url.path,
        method=request.method
    )
    
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "error": {
                "type": "HTTPException",
                "message": exc.detail,
                "path": request.url.path,
                "status_code": exc.status_code
            }
        }
    )


async def validation_exception_handler(
    request: Request, exc: RequestValidationError
) -> JSONResponse:
    """Handle validation exceptions."""
    error_count.labels(
        error_type="ValidationError",
        endpoint=request.url.path
    ).inc()
    
    logger.warning(
        "Validation error occurred",
        errors=exc.errors(),
        path=request.url.path,
        method=request.method
    )
    
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content={
            "error": {
                "type": "ValidationError",
                "message": "Request validation failed",
                "details": exc.errors(),
                "path": request.url.path
            }
        }
    )


async def general_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """Handle general exceptions."""
    error_count.labels(
        error_type=exc.__class__.__name__,
        endpoint=request.url.path
    ).inc()
    
    logger.error(
        "Unhandled exception occurred",
        exception=exc.__class__.__name__,
        message=str(exc),
        path=request.url.path,
        method=request.method,
        exc_info=True
    )
    
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content={
            "error": {
                "type": "InternalServerError",
                "message": "An internal server error occurred",
                "path": request.url.path
            }
        }
    )
