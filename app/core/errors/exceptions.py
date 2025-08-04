"""Define custom exceptions for the application."""

class UnauthorizedException(Exception):
    """Exception raised for unauthorized access."""
    pass

class ServiceUnavailableException(Exception):
    """Exception raised for service unavailability."""
    pass

class InvalidRequestException(Exception):
    """Exception raised for invalid request data."""
    pass
