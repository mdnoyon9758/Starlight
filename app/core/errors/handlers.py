"""Advanced error handlers."""

import traceback
from typing import Any, Dict

from fastapi import HTTPException, Request, status
from fastapi.responses import JSONResponse
from sqlalchemy.exc import IntegrityError, SQLAlchemyError

from app.core.logging.logger import get_logger
from monitoring.prometheus.metrics import record_error

logger = get_logger(__name__)


class ErrorResponse:
    """Standardized error response format."""
    
    def __init__(
        self,
        error_code: str,
        message: str,
        details: Dict[str, Any] = None,
        status_code: int = 500
    ):
        self.error_code = error_code
        self.message = message
        self.details = details or {}
        self.status_code = status_code
    
    def to_dict(self) -> Dict[str, Any]:
        """Convert error response to dictionary."""
        return {
            "error": {
                "code": self.error_code,
                "message": self.message,
                "details": self.details
            }
        }


async def database_error_handler(request: Request, exc: SQLAlchemyError) -> JSONResponse:
    """Handle database errors."""
    error_id = id(exc)
    
    logger.error(
        f"Database error {error_id}",
        extra={
            "error_type": type(exc).__name__,
            "error_details": str(exc),
            "request_url": str(request.url),
            "request_method": request.method
        }
    )
    
    record_error("database_error", str(request.url.path))
    
    if isinstance(exc, IntegrityError):
        error_response = ErrorResponse(
            error_code="INTEGRITY_ERROR",
            message="Data integrity constraint violation",
            details={"error_id": error_id},
            status_code=status.HTTP_400_BAD_REQUEST
        )
    else:
        error_response = ErrorResponse(
            error_code="DATABASE_ERROR",
            message="Database operation failed",
            details={"error_id": error_id},
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
        )
    
    return JSONResponse(
        status_code=error_response.status_code,
        content=error_response.to_dict()
    )


async def validation_error_handler(request: Request, exc: Exception) -> JSONResponse:
    """Handle validation errors."""
    error_id = id(exc)
    
    logger.warning(
        f"Validation error {error_id}",
        extra={
            "error_type": type(exc).__name__,
            "error_details": str(exc),
            "request_url": str(request.url),
            "request_method": request.method
        }
    )
    
    record_error("validation_error", str(request.url.path))
    
    error_response = ErrorResponse(
        error_code="VALIDATION_ERROR",
        message="Request validation failed",
        details={
            "error_id": error_id,
            "validation_errors": getattr(exc, 'errors', [])
        },
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY
    )
    
    return JSONResponse(
        status_code=error_response.status_code,
        content=error_response.to_dict()
    )


async def general_error_handler(request: Request, exc: Exception) -> JSONResponse:
    """Handle general application errors."""
    error_id = id(exc)
    
    logger.error(
        f"Unhandled error {error_id}",
        extra={
            "error_type": type(exc).__name__,
            "error_details": str(exc),
            "traceback": traceback.format_exc(),
            "request_url": str(request.url),
            "request_method": request.method
        }
    )
    
    record_error("general_error", str(request.url.path))
    
    error_response = ErrorResponse(
        error_code="INTERNAL_ERROR",
        message="An internal server error occurred",
        details={"error_id": error_id},
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR
    )
    
    return JSONResponse(
        status_code=error_response.status_code,
        content=error_response.to_dict()
    )
