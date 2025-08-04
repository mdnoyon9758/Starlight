"""Structured logging configuration."""

import logging
import sys
from typing import Any, Dict

import structlog
from structlog.types import EventDict, Processor

from app.core.config.settings import get_settings

settings = get_settings()


def add_correlation_id(logger: Any, method_name: str, event_dict: EventDict) -> EventDict:
    """Add correlation ID to log entries."""
    # This would be populated by middleware in a real application
    correlation_id = getattr(structlog.contextvars, "correlation_id", None)
    if correlation_id:
        event_dict["correlation_id"] = correlation_id.get()
    return event_dict


def add_request_info(logger: Any, method_name: str, event_dict: EventDict) -> EventDict:
    """Add request information to log entries."""
    request_info = getattr(structlog.contextvars, "request_info", None)
    if request_info:
        event_dict.update(request_info.get({}))
    return event_dict


def setup_logging() -> None:
    """Configure structured logging."""
    timestamper = structlog.processors.TimeStamper(fmt="ISO")
    
    shared_processors: list[Processor] = [
        structlog.contextvars.merge_contextvars,
        structlog.stdlib.filter_by_level,
        structlog.stdlib.add_logger_name,
        structlog.stdlib.add_log_level,
        structlog.stdlib.PositionalArgumentsFormatter(),
        timestamper,
        structlog.processors.StackInfoRenderer(),
        structlog.processors.format_exc_info,
        add_correlation_id,
        add_request_info,
    ]
    
    if settings.log_format == "json":
        shared_processors.append(structlog.processors.JSONRenderer())
    else:
        shared_processors.append(structlog.dev.ConsoleRenderer())
    
    structlog.configure(
        processors=shared_processors,
        wrapper_class=structlog.stdlib.BoundLogger,
        logger_factory=structlog.stdlib.LoggerFactory(),
        context_class=dict,
        cache_logger_on_first_use=True,
    )
    
    # Configure standard library logging
    logging.basicConfig(
        format="%(message)s",
        stream=sys.stdout,
        level=getattr(logging, settings.log_level.upper()),
    )


def get_logger(name: str) -> structlog.BoundLogger:
    """Get a configured logger instance."""
    return structlog.get_logger(name)
