"""Request timing middleware for performance monitoring."""

import time
from typing import Callable

from fastapi import Request, Response
from starlette.middleware.base import BaseHTTPMiddleware

from app.core.logging.logger import get_logger
from monitoring.prometheus.metrics import record_request

logger = get_logger(__name__)


class TimingMiddleware(BaseHTTPMiddleware):
    """Middleware to track request timing and performance."""
    
    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        """Process request and track timing."""
        start_time = time.time()
        
        # Add request start time to state
        request.state.start_time = start_time
        
        # Process request
        response = await call_next(request)
        
        # Calculate duration
        duration = time.time() - start_time
        
        # Log slow requests
        if duration > 1.0:  # Log requests taking more than 1 second
            logger.warning(
                f"Slow request detected",
                extra={
                    "method": request.method,
                    "url": str(request.url),
                    "duration": duration,
                    "status_code": response.status_code
                }
            )
        
        # Record metrics
        record_request(
            method=request.method,
            endpoint=request.url.path,
            status_code=response.status_code,
            duration=duration
        )
        
        # Add timing headers
        response.headers["X-Process-Time"] = str(duration)
        response.headers["X-Timestamp"] = str(int(start_time))
        
        return response
