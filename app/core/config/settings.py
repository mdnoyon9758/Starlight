"""Application configuration settings."""

from functools import lru_cache
from typing import Any, Dict, List, Optional, Union

from pydantic import (
    AnyHttpUrl,
    EmailStr,
    Field,
    PostgresDsn,
    field_validator,
)
from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    """Application settings."""

    model_config = SettingsConfigDict(
        env_file=".env",
        env_file_encoding="utf-8",
        case_sensitive=False,
        extra="ignore",
    )

    # Application Settings
    app_name: str = Field(default="Starlight API", description="Application name")
    app_version: str = Field(default="0.1.0", description="Application version")
    app_description: str = Field(
        default="Enterprise FastAPI REST API Framework",
        description="Application description",
    )
    environment: str = Field(default="development", description="Environment name")
    debug: bool = Field(default=False, description="Debug mode")
    api_v1_str: str = Field(default="/api/v1", description="API v1 prefix")
    
    # Security Settings
    secret_key: str = Field(
        default="change-this-super-secret-key-in-production",
        description="Secret key for JWT",
        min_length=32,
    )
    access_token_expire_minutes: int = Field(
        default=15, description="Access token expiration in minutes"
    )
    refresh_token_expire_minutes: int = Field(
        default=10080, description="Refresh token expiration in minutes (7 days)"
    )
    algorithm: str = Field(default="HS256", description="JWT algorithm")
    
    # Password Settings
    password_min_length: int = Field(default=8, description="Minimum password length")
    password_require_uppercase: bool = Field(
        default=True, description="Require uppercase in password"
    )
    password_require_lowercase: bool = Field(
        default=True, description="Require lowercase in password"
    )
    password_require_numbers: bool = Field(
        default=True, description="Require numbers in password"
    )
    password_require_special: bool = Field(
        default=True, description="Require special characters in password"
    )
    max_login_attempts: int = Field(
        default=5, description="Maximum login attempts before lockout"
    )
    lockout_duration_minutes: int = Field(
        default=30, description="Account lockout duration in minutes"
    )
    
    # Database Settings
    database_url: str = Field(
        default="sqlite+aiosqlite:///./starlight.db",
        description="Async database URL (PostgreSQL recommended for production)",
    )
    database_url_sync: str = Field(
        default="sqlite:///./starlight.db",
        description="Sync database URL for Alembic (auto-derived from database_url if not set)",
    )
    database_pool_size: int = Field(default=20, description="Database pool size")
    database_max_overflow: int = Field(
        default=0, description="Database max overflow"
    )
    database_pool_pre_ping: bool = Field(
        default=True, description="Database pool pre-ping"
    )
    database_echo: bool = Field(default=False, description="Database echo SQL")
    
    # Redis Settings
    redis_url: str = Field(
        default="redis://localhost:6379/0", description="Redis URL"
    )
    redis_cache_ttl: int = Field(
        default=3600, description="Redis cache TTL in seconds"
    )
    redis_session_ttl: int = Field(
        default=86400, description="Redis session TTL in seconds"
    )
    
    # Celery Settings
    celery_broker_url: str = Field(
        default="redis://localhost:6379/1", description="Celery broker URL"
    )
    celery_result_backend: str = Field(
        default="redis://localhost:6379/1", description="Celery result backend"
    )
    celery_task_serializer: str = Field(
        default="json", description="Celery task serializer"
    )
    celery_result_serializer: str = Field(
        default="json", description="Celery result serializer"
    )
    celery_accept_content: List[str] = Field(
        default=["json"], description="Celery accept content"
    )
    celery_timezone: str = Field(default="UTC", description="Celery timezone")
    celery_enable_utc: bool = Field(default=True, description="Celery enable UTC")
    
    # CORS Settings
    backend_cors_origins: List[AnyHttpUrl] = Field(
        default=[
            "http://localhost:3000",
            "http://localhost:8080",
            "http://localhost:8000",
        ],
        description="Backend CORS origins",
    )
    
    @field_validator("backend_cors_origins", mode="before")
    @classmethod
    def assemble_cors_origins(cls, v: Union[str, List[str]]) -> Union[List[str], str]:
        if isinstance(v, str) and not v.startswith("["):
            return [i.strip() for i in v.split(",")]
        elif isinstance(v, (list, str)):
            return v
        raise ValueError(v)
    
    # Email Settings
    smtp_tls: bool = Field(default=True, description="SMTP TLS")
    smtp_port: Optional[int] = Field(default=587, description="SMTP port")
    smtp_host: Optional[str] = Field(default=None, description="SMTP host")
    smtp_user: Optional[EmailStr] = Field(default=None, description="SMTP user")
    smtp_password: Optional[str] = Field(default=None, description="SMTP password")
    emails_from_email: Optional[EmailStr] = Field(
        default=None, description="From email"
    )
    emails_from_name: Optional[str] = Field(default=None, description="From name")
    
    # Rate Limiting
    rate_limit_per_minute: int = Field(
        default=60, description="Rate limit per minute"
    )
    rate_limit_burst: int = Field(default=10, description="Rate limit burst")
    
    # File Upload
    max_upload_size: int = Field(
        default=10485760, description="Max upload size in bytes (10MB)"
    )
    allowed_file_extensions: List[str] = Field(
        default=["jpg", "jpeg", "png", "gif", "pdf", "doc", "docx"],
        description="Allowed file extensions",
    )
    upload_folder: str = Field(default="./uploads", description="Upload folder")
    
    # Monitoring & Logging
    log_level: str = Field(default="INFO", description="Log level")
    log_format: str = Field(default="json", description="Log format")
    enable_metrics: bool = Field(default=True, description="Enable metrics")
    metrics_port: int = Field(default=8090, description="Metrics port")
    sentry_dsn: Optional[str] = Field(default=None, description="Sentry DSN")
    datadog_api_key: Optional[str] = Field(
        default=None, description="Datadog API Key"
    )
    newrelic_license_key: Optional[str] = Field(
        default=None, description="New Relic License Key"
    )
    prometheus_url: str = Field(default="http://prometheus:9090", description="Prometheus server URL")
    grafana_url: str = Field(default="http://grafana:3000", description="Grafana server URL")
    flower_basic_auth: str = Field(default="user:password", description="Flower basic auth credentials")
    celery_worker_concurrency: int = Field(default=4, description="Default celery worker concurrency")

    jaeger_endpoint: Optional[str] = Field(
        default=None, description="Jaeger endpoint"
    )

    # OAuth Settings
    google_client_id: Optional[str] = Field(
        default=None, description="Google OAuth client ID"
    )
    google_client_secret: Optional[str] = Field(
        default=None, description="Google OAuth client secret"
    )
    github_client_id: Optional[str] = Field(
        default=None, description="GitHub OAuth client ID"
    )
    github_client_secret: Optional[str] = Field(
        default=None, description="GitHub OAuth client secret"
    )
    
    # AWS Settings
    aws_access_key_id: Optional[str] = Field(
        default=None, description="AWS access key ID"
    )
    aws_secret_access_key: Optional[str] = Field(
        default=None, description="AWS secret access key"
    )
    aws_default_region: str = Field(
        default="us-east-1", description="AWS default region"
    )
    s3_bucket_name: Optional[str] = Field(default=None, description="S3 bucket name")
    
    # Testing
    test_database_url: str = Field(
        default="sqlite+aiosqlite:///./test.db", description="Test database URL"
    )
    test_redis_url: str = Field(
        default="redis://localhost:6379/15", description="Test Redis URL"
    )
    
    @property
    def is_development(self) -> bool:
        """Check if running in development mode."""
        return self.environment.lower() == "development"
    
    @property
    def is_production(self) -> bool:
        """Check if running in production mode."""
        return self.environment.lower() == "production"
    
    @property
    def is_testing(self) -> bool:
        """Check if running in testing mode."""
        return self.environment.lower() == "testing"


@lru_cache()
def get_settings() -> Settings:
    """Get cached settings instance."""
    return Settings()
