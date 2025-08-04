"""API versioning and content negotiation middleware."""

from typing import Callable
from fastapi import Request, Response
from starlette.middleware.base import BaseHTTPMiddleware
import structlog

logger = structlog.get_logger(__name__)


class APIVersioningMiddleware(BaseHTTPMiddleware):
    """Middleware for API versioning and content negotiation."""
    
    def __init__(self, app, supported_versions: list[str] = None):
        super().__init__(app)
        self.supported_versions = supported_versions or ["v1", "v2"]
        self.default_version = "v1"
    
    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        """Process API versioning."""
        # Extract version from header, query param, or URL
        api_version = self._extract_version(request)
        
        # Validate version
        if api_version not in self.supported_versions:
            api_version = self.default_version
            logger.warning(
                "Unsupported API version requested, falling back to default",
                requested_version=request.headers.get("API-Version"),
                default_version=self.default_version
            )
        
        # Add version to request state
        request.state.api_version = api_version
        
        # Process request
        response = await call_next(request)
        
        # Add version info to response headers
        response.headers["API-Version"] = api_version
        response.headers["Supported-Versions"] = ",".join(self.supported_versions)
        
        return response
    
    def _extract_version(self, request: Request) -> str:
        """Extract API version from request."""
        # Check header first
        if "API-Version" in request.headers:
            return request.headers["API-Version"]
        
        # Check query parameter
        if "version" in request.query_params:
            return request.query_params["version"]
        
        # Check URL path
        if request.url.path.startswith("/api/"):
            path_parts = request.url.path.split("/")
            if len(path_parts) > 2 and path_parts[2].startswith("v"):
                return path_parts[2]
        
        return self.default_version


class ContentNegotiationMiddleware(BaseHTTPMiddleware):
    """Middleware for content negotiation and compression."""
    
    def __init__(self, app, supported_formats: list[str] = None):
        super().__init__(app)
        self.supported_formats = supported_formats or ["json", "xml", "yaml"]
    
    async def dispatch(self, request: Request, call_next: Callable) -> Response:
        """Process content negotiation."""
        # Extract preferred format
        preferred_format = self._extract_format(request)
        
        # Add format to request state
        request.state.response_format = preferred_format
        
        # Process request
        response = await call_next(request)
        
        # Add format info to response headers
        response.headers["Content-Format"] = preferred_format
        
        return response
    
    def _extract_format(self, request: Request) -> str:
        """Extract preferred response format."""
        # Check Accept header
        accept_header = request.headers.get("Accept", "application/json")
        
        if "application/xml" in accept_header:
            return "xml"
        elif "application/yaml" in accept_header:
            return "yaml"
        else:
            return "json"
