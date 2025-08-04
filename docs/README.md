# Starlight API Documentation

## Overview

Starlight is an enterprise-grade FastAPI REST API framework that provides a comprehensive foundation for building modern, scalable web applications. It incorporates industry best practices in API development, security, observability, and DevOps.

## Features

### Core Features
- **High-Performance API**: Built on FastAPI with async/await support
- **Comprehensive Authentication**: JWT, OAuth2 (Google, GitHub), and API keys
- **Advanced Security**: Rate limiting, security headers, OWASP compliance
- **Structured Logging**: JSON logging with correlation IDs
- **Caching**: Redis-based multi-tier caching
- **Background Tasks**: Celery integration with monitoring
- **Database**: PostgreSQL with async SQLAlchemy and migrations
- **Testing**: Complete test suite with pytest
- **CI/CD**: GitHub Actions with automated testing and deployment
- **Monitoring**: Prometheus metrics and Grafana dashboards
- **Documentation**: Auto-generated API docs with Swagger/ReDoc

### Production Ready
- **Docker**: Multi-stage builds with security scanning
- **Kubernetes**: Complete deployment manifests
- **Observability**: Distributed tracing with OpenTelemetry
- **Error Handling**: Comprehensive exception handling
- **Security**: HTTPS, CSRF protection, input validation

## Quick Start

### Prerequisites
- Python 3.12+
- Docker and Docker Compose
- PostgreSQL (or use Docker)
- Redis (or use Docker)

### Installation

1. Clone the repository:
```bash
git clone https://github.com/your-org/starlight.git
cd starlight
```

2. Create environment file:
```bash
cp .env.example .env
# Edit .env with your configuration
```

3. Install dependencies:
```bash
pip install -e ".[dev]"
```

4. Run with Docker Compose:
```bash
docker-compose up --build
```

5. Access the API:
- API: http://localhost:8000
- Documentation: http://localhost:8000/api/v1/docs
- Metrics: http://localhost:8090/metrics

## API Endpoints

### Authentication
- `POST /api/v1/auth/register` - Register new user
- `POST /api/v1/auth/login` - Login user
- `GET /api/v1/auth/oauth/{provider}` - OAuth login

### System
- `GET /` - API information
- `GET /health` - Health check
- `GET /api/v1/docs` - Interactive API documentation

## Development

### Setup Development Environment

1. Install pre-commit hooks:
```bash
pre-commit install
```

2. Run tests:
```bash
pytest tests/ -v --cov=app
```

3. Run linting:
```bash
ruff check .
ruff format .
mypy app/
```

### Database Migrations

```bash
# Create migration
alembic revision --autogenerate -m "description"

# Apply migrations
alembic upgrade head
```

## Deployment

### Docker

```bash
# Build image
docker build -t starlight:latest .

# Run container
docker run -p 8000:80 --env-file .env starlight:latest
```

### Kubernetes

```bash
# Apply manifests
kubectl apply -f deployment/kubernetes/

# Check status
kubectl get pods -n starlight
```

## Monitoring

### Metrics

Prometheus metrics are available at `/metrics` endpoint:
- Request rate and duration
- Error rates
- Database connection pool
- Cache hit rates
- Authentication attempts

### Logging

Structured JSON logs with:
- Correlation IDs for request tracing
- Performance metrics
- Error tracking
- Security events

### Health Checks

Health endpoint (`/health`) provides:
- Overall system status
- Database connectivity
- Cache connectivity
- Service dependencies

## Security

### Authentication Methods
1. **JWT Tokens**: Short-lived access tokens with refresh tokens
2. **OAuth2**: Google and GitHub integration
3. **API Keys**: For service-to-service communication

### Security Features
- Password hashing with Argon2
- Rate limiting (60 requests/minute)
- Security headers (CSP, HSTS, etc.)
- Input validation and sanitization
- CORS configuration
- Request/response logging

## Configuration

Key environment variables:

```env
# Application
APP_NAME=Starlight API
ENVIRONMENT=development
SECRET_KEY=your-secret-key

# Database
DATABASE_URL=postgresql+asyncpg://user:pass@localhost/db

# Redis
REDIS_URL=redis://localhost:6379/0

# Security
ACCESS_TOKEN_EXPIRE_MINUTES=15
RATE_LIMIT_PER_MINUTE=60

# OAuth
GOOGLE_CLIENT_ID=your-google-client-id
GITHUB_CLIENT_ID=your-github-client-id
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Run the test suite
6. Submit a pull request

### Code Style

- Use `ruff` for linting and formatting
- Follow PEP 8 guidelines
- Add type hints
- Write comprehensive tests
- Update documentation

## License

MIT License - see LICENSE file for details.

## Support

- Documentation: [docs/](docs/)
- Issues: GitHub Issues
- Discussions: GitHub Discussions
