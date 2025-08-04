"""Celery application setup."""

from celery import Celery
from app.core.config.settings import get_settings

settings = get_settings()

celery_app = Celery(
    "starlight",
    broker=settings.celery_broker_url,
    backend=settings.celery_result_backend,
)

celery_app.conf.update(
    task_serializer=settings.celery_task_serializer,
    result_serializer=settings.celery_result_serializer,
    accept_content=settings.celery_accept_content,
    timezone=settings.celery_timezone,
    enable_utc=settings.celery_enable_utc,
    task_annotations={
        '*': {
            'rate_limit': '10/m',
            'time_limit': 300,
            'soft_time_limit': 240,
            'max_retries': 3,
            'default_retry_delay': 60,
        },
    },
)
