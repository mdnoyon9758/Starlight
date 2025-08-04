# 🌟 Starlight - Enterprise FastAPI Framework

<div align="center">

![Starlight Logo](https://img.shields.io/badge/Starlight-Enterprise%20FastAPI-blue?style=for-the-badge&logo=fastapi)

[![Python Version](https://img.shields.io/badge/python-3.12+-blue.svg)](https://python.org)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.115+-green.svg)](https://fastapi.tiangolo.com)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Build Status](https://img.shields.io/github/actions/workflow/status/mdnoyon9758/Starlight/ci.yml?branch=main)](https://github.com/mdnoyon9758/Starlight/actions)
[![Coverage](https://img.shields.io/codecov/c/github/mdnoyon9758/Starlight)](https://codecov.io/gh/mdnoyon9758/Starlight)
[![Docker](https://img.shields.io/badge/docker-ready-blue)](https://hub.docker.com/r/mdnoyon9758/starlight)

**A production-ready, enterprise-grade FastAPI framework with built-in authentication, background tasks, file storage, and comprehensive monitoring.**

[🚀 Quick Start](#-quick-start) •
[📖 Documentation](#-documentation) •
[🔧 Features](#-features) •
[💻 Development](#-development) •
[🐳 Deployment](#-deployment)

</div>

## ✨ Overview

Starlight is a comprehensive FastAPI framework designed for enterprise applications. It provides a solid foundation with all the essential components needed for modern web applications, including authentication, background processing, file management, and observability.

## 🔧 Features

### 🔐 **Authentication & Security**
- **JWT Authentication** with refresh tokens
- **OAuth2 Integration** (Google, GitHub)
- **Account Linking** for multiple OAuth providers
- **Role-based Access Control**
- **Security Headers** middleware
- **Rate Limiting** protection

### ⚡ **Performance & Scalability**
- **Async/Await** throughout the codebase
- **Redis Caching** with advanced cache management
- **Database Connection Pooling**
- **Request/Response Compression**
- **Performance Profiling** utilities
- **Load Balancing** with NGINX

### 🔄 **Background Processing**
- **Celery Integration** for async tasks
- **Flower Monitoring** dashboard
- **Task Retry Logic** and error handling
- **Scheduled Tasks** support
- **Progress Tracking** for long-running tasks

### 📁 **File Management**
- **Local & Cloud Storage** (S3 compatible)
- **Image Processing** with PIL
- **File Validation** and size limits
- **Secure Upload** handling
- **CDN Integration** ready

### 📊 **Monitoring & Observability**
- **Prometheus Metrics** integration
- **Grafana Dashboards** ready
- **Structured Logging** with Structlog
- **Error Tracking** with Sentry
- **Health Checks** endpoints
- **Request Tracing** with correlation IDs

### 🏗️ **Architecture**
- **Clean Architecture** principles
- **Dependency Injection** pattern
- **Modular Design** for easy extension
- **Database Migrations** with Alembic
- **Comprehensive Testing** suite
- **Type Safety** with mypy

## 🚀 Quick Start

### Prerequisites
- Python 3.12+
- Docker & Docker Compose
- PostgreSQL (or use Docker)
- Redis (or use Docker)

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/mdnoyon9758/Starlight.git
cd starlight
```

2. **Install dependencies**
```bash
pip install -e ".[dev]"
```

3. **Set up environment**
```bash
cp .env.example .env
# Edit .env with your configuration
```

4. **Run with Docker Compose**
```bash
docker-compose up -d
```

5. **Access the application**
- API Documentation: http://localhost:8000/api/v1/docs
- Flower (Celery): http://localhost:5555
- Metrics: http://localhost:8090/metrics

## 📖 Documentation

- **[Full Documentation](https://mdnoyon9758.github.io/Starlight)**
- **[API Reference](https://mdnoyon9758.github.io/Starlight/api-reference/)**
- **[Getting Started Guide](https://mdnoyon9758.github.io/Starlight/getting-started/)**
- **[Deployment Guide](https://mdnoyon9758.github.io/Starlight/deployment/)**

## 💻 Development

### Setup Development Environment

```bash
# Install development dependencies
pip install -e ".[dev,test]"

# Install pre-commit hooks
pre-commit install

# Run tests
pytest

# Run with coverage
pytest --cov=app --cov-report=html

# Format code
black app/
isort app/

# Type checking
mypy app/

# Linting
ruff check app/
```

### Project Structure

```
starlight/
├── app/                          # Application code
│   ├── api/                      # API layer
│   │   ├── middleware/           # Custom middleware
│   │   └── v1/                   # API v1
│   │       ├── dependencies/     # Route dependencies
│   │       └── endpoints/        # API endpoints
│   ├── core/                     # Core functionality
│   │   ├── cache/                # Caching utilities
│   │   ├── config/               # Configuration
│   │   ├── errors/               # Error handling
│   │   ├── logging/              # Logging setup
│   │   ├── performance/          # Performance utilities
│   │   └── security/             # Security utilities
│   ├── infrastructure/           # Infrastructure layer
│   │   ├── database/             # Database models
│   │   ├── storage/              # File storage
│   │   └── tasks/                # Background tasks
│   └── schemas/                  # Pydantic schemas
├── tests/                        # Test suite
├── docs/                         # Documentation
├── monitoring/                   # Monitoring setup
└── deployment/                   # Deployment configs
```

### Running Tests

```bash
# Run all tests
pytest

# Run specific test categories
pytest -m unit
pytest -m integration
pytest -m e2e

# Run with specific coverage
pytest --cov=app --cov-fail-under=85
```

## 🐳 Deployment

### Docker Deployment

```bash
# Build and run with Docker Compose
docker-compose up -d

# Scale services
docker-compose up -d --scale web=3

# Production deployment
docker-compose -f docker-compose.prod.yml up -d
```

### Environment Variables

Key environment variables for deployment:

```bash
# Application
ENVIRONMENT=production
SECRET_KEY=your-super-secret-key
DEBUG=false

# Database
DATABASE_URL=postgresql://user:pass@host:5432/dbname

# Redis
REDIS_URL=redis://host:6379/0

# OAuth (optional)
GOOGLE_CLIENT_ID=your-google-client-id
GOOGLE_CLIENT_SECRET=your-google-client-secret

# AWS S3 (optional)
AWS_ACCESS_KEY_ID=your-access-key
AWS_SECRET_ACCESS_KEY=your-secret-key
S3_BUCKET_NAME=your-bucket-name
```

### Production Checklist

- [ ] Set strong `SECRET_KEY`
- [ ] Configure production database
- [ ] Set up Redis for caching
- [ ] Configure OAuth providers
- [ ] Set up S3 for file storage
- [ ] Configure monitoring (Prometheus/Grafana)
- [ ] Set up error tracking (Sentry)
- [ ] Configure HTTPS/SSL
- [ ] Set up backup strategy
- [ ] Configure log aggregation

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guide](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

### Development Guidelines

- Follow PEP 8 style guidelines
- Write comprehensive tests
- Add docstrings to all public functions
- Update documentation for new features
- Ensure all CI checks pass

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- [FastAPI](https://fastapi.tiangolo.com/) - The amazing web framework
- [Celery](https://docs.celeryproject.org/) - Distributed task queue
- [SQLAlchemy](https://www.sqlalchemy.org/) - Database toolkit
- [Pydantic](https://pydantic-docs.helpmanual.io/) - Data validation
- [Redis](https://redis.io/) - In-memory data structure store

## 🔗 Links

- **Documentation**: https://mdnoyon9758.github.io/Starlight
- **Docker Hub**: https://hub.docker.com/r/mdnoyon9758/starlight
- **Issue Tracker**: https://github.com/mdnoyon9758/Starlight/issues
- **Discussions**: https://github.com/mdnoyon9758/Starlight/discussions

---

<div align="center">

**Made with ❤️ by the Starlight Team**

[![GitHub stars](https://img.shields.io/github/stars/mdnoyon9758/Starlight?style=social)](https://github.com/mdnoyon9758/Starlight)
[![GitHub forks](https://img.shields.io/github/forks/mdnoyon9758/Starlight?style=social)](https://github.com/mdnoyon9758/Starlight)

</div>
