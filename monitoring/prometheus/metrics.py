"""Prometheus metrics configuration."""

from prometheus_client import Counter, Histogram, Info, start_http_server

# Application info
app_info = Info('starlight_app_info', 'Application information')
app_info.info({
    'version': '0.1.0',
    'name': 'Starlight API'
})

# Request metrics
request_count = Counter(
    'starlight_requests_total',
    'Total number of HTTP requests',
    ['method', 'endpoint', 'status_code']
)

request_duration = Histogram(
    'starlight_request_duration_seconds',
    'HTTP request duration in seconds',
    ['method', 'endpoint']
)

# Database metrics
db_connection_pool = Histogram(
    'starlight_db_connection_pool_size',
    'Database connection pool size'
)

db_query_duration = Histogram(
    'starlight_db_query_duration_seconds',
    'Database query duration in seconds',
    ['query_type']
)

# Cache metrics
cache_hits = Counter(
    'starlight_cache_hits_total',
    'Total number of cache hits'
)

cache_misses = Counter(
    'starlight_cache_misses_total',
    'Total number of cache misses'
)

# Authentication metrics
auth_attempts = Counter(
    'starlight_auth_attempts_total',
    'Total number of authentication attempts',
    ['result']
)

# Error metrics
error_count = Counter(
    'starlight_errors_total',
    'Total number of errors',
    ['error_type', 'endpoint']
)

# Celery metrics
celery_task_duration = Histogram(
    'starlight_celery_task_duration_seconds',
    'Celery task duration in seconds',
    ['task_name']
)

celery_task_count = Counter(
    'starlight_celery_tasks_total',
    'Total number of Celery tasks',
    ['task_name', 'status']
)

# File upload metrics
file_upload_size = Histogram(
    'starlight_file_upload_size_bytes',
    'File upload size in bytes',
    ['file_type']
)

file_upload_count = Counter(
    'starlight_file_uploads_total',
    'Total number of file uploads',
    ['file_type', 'status']
)

# OAuth metrics
oauth_requests = Counter(
    'starlight_oauth_requests_total',
    'Total number of OAuth requests',
    ['provider', 'action']
)

# Health check metrics
health_check_status = Counter(
    'starlight_health_checks_total',
    'Total number of health checks',
    ['service', 'status']
)


def start_metrics_server(port: int = 8090) -> None:
    """Start Prometheus metrics server."""
    start_http_server(port)
    print(f"Metrics server started on port {port}")


def record_request(method: str, endpoint: str, status_code: int, duration: float):
    """Record HTTP request metrics."""
    request_count.labels(method=method, endpoint=endpoint, status_code=status_code).inc()
    request_duration.labels(method=method, endpoint=endpoint).observe(duration)


def record_auth_attempt(result: str):
    """Record authentication attempt."""
    auth_attempts.labels(result=result).inc()


def record_error(error_type: str, endpoint: str):
    """Record error occurrence."""
    error_count.labels(error_type=error_type, endpoint=endpoint).inc()


def record_cache_hit():
    """Record cache hit."""
    cache_hits.inc()


def record_cache_miss():
    """Record cache miss."""
    cache_misses.inc()


def record_celery_task(task_name: str, status: str, duration: float = None):
    """Record Celery task metrics."""
    celery_task_count.labels(task_name=task_name, status=status).inc()
    if duration is not None:
        celery_task_duration.labels(task_name=task_name).observe(duration)


def record_file_upload(file_type: str, size: int, status: str):
    """Record file upload metrics."""
    file_upload_count.labels(file_type=file_type, status=status).inc()
    if status == 'success':
        file_upload_size.labels(file_type=file_type).observe(size)


def record_oauth_request(provider: str, action: str):
    """Record OAuth request metrics."""
    oauth_requests.labels(provider=provider, action=action).inc()


def record_health_check(service: str, status: str):
    """Record health check metrics."""
    health_check_status.labels(service=service, status=status).inc()
