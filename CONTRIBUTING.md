# 🤝 Contributing to Starlight

Thank you for your interest in contributing to Starlight! This guide will help you get started with contributing to this enterprise FastAPI framework.

## 🚀 Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:
- Python 3.12+
- Docker and Docker Compose
- Git
- Your favorite code editor

### Setting Up Development Environment

1. **Fork and clone the repository**
```bash
git clone https://github.com/YOUR_USERNAME/starlight.git
cd starlight
```

2. **Create a virtual environment**
```bash
python -m venv venv
source venv/bin/activate  # On Windows: venv\Scripts\activate
```

3. **Install development dependencies**
```bash
pip install -e ".[dev,test]"
```

4. **Install pre-commit hooks**
```bash
pre-commit install
```

5. **Set up environment**
```bash
cp .env.example .env
# Edit .env with your test configuration
```

6. **Run the test suite**
```bash
pytest
```

## 📋 Development Guidelines

### Code Style

We follow strict code style guidelines to maintain consistency:

- **Python Style**: Follow PEP 8
- **Import Sorting**: Use `isort` with Black profile
- **Formatting**: Use `Black` for code formatting
- **Linting**: Use `Ruff` for fast linting
- **Type Hints**: Use type hints throughout the codebase

### Pre-commit Checks

Before committing, ensure your code passes all checks:

```bash
# Format code
black app/ tests/
isort app/ tests/

# Lint code
ruff check app/ tests/

# Type checking
mypy app/

# Run tests
pytest

# Security check
bandit -r app/
```

### Commit Messages

Follow conventional commit format:

```
type(scope): description

[optional body]

[optional footer]
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, etc.)
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

**Examples:**
```
feat(auth): add OAuth2 Google integration
fix(cache): resolve Redis connection timeout
docs(api): update authentication documentation
```

## 🧪 Testing

### Test Structure

We use a comprehensive testing approach:

```
tests/
├── unit/              # Unit tests
├── integration/       # Integration tests
├── e2e/              # End-to-end tests
└── conftest.py       # Test fixtures
```

### Writing Tests

1. **Unit Tests**: Test individual functions/methods
2. **Integration Tests**: Test component interactions
3. **End-to-End Tests**: Test complete user workflows

### Test Guidelines

- Write descriptive test names
- Use appropriate fixtures from `conftest.py`
- Mock external dependencies
- Aim for high test coverage (>85%)
- Include both positive and negative test cases

### Running Tests

```bash
# Run all tests
pytest

# Run specific test types
pytest -m unit
pytest -m integration
pytest -m e2e

# Run with coverage
pytest --cov=app --cov-report=html

# Run specific test file
pytest tests/unit/test_auth.py

# Run tests matching pattern
pytest -k "test_user"
```

## 🔧 Making Changes

### Branching Strategy

1. Create a feature branch from `main`:
```bash
git checkout -b feature/your-feature-name
```

2. Make your changes following the guidelines
3. Write/update tests
4. Update documentation if needed
5. Commit your changes
6. Push to your fork
7. Create a Pull Request

### Pull Request Process

1. **Before submitting:**
   - Ensure all tests pass
   - Update documentation
   - Add changelog entry if applicable
   - Rebase on latest `main` if needed

2. **PR Description should include:**
   - Clear description of changes
   - Motivation for the change
   - Any breaking changes
   - Screenshots (for UI changes)
   - Testing instructions

3. **PR Requirements:**
   - All CI checks must pass
   - At least one maintainer approval
   - Up-to-date with `main` branch
   - No merge conflicts

## 📝 Documentation

### Types of Documentation

1. **Code Documentation**: Docstrings for all public functions
2. **API Documentation**: OpenAPI/Swagger docs (auto-generated)
3. **User Documentation**: MkDocs in `/docs` folder
4. **README**: Project overview and quick start

### Writing Documentation

- Use clear, concise language
- Include code examples
- Keep documentation up-to-date with code changes
- Use proper Markdown formatting

### Building Documentation Locally

```bash
# Install docs dependencies
pip install -e ".[docs]"

# Serve docs locally
mkdocs serve

# Build static docs
mkdocs build
```

## 🐛 Reporting Issues

### Bug Reports

When reporting bugs, please include:

1. **Description**: Clear description of the issue
2. **Steps to Reproduce**: Detailed steps
3. **Expected Behavior**: What should happen
4. **Actual Behavior**: What actually happens
5. **Environment**: OS, Python version, etc.
6. **Logs**: Relevant error messages/logs

### Feature Requests

When requesting features:

1. **Problem**: Describe the problem you're trying to solve
2. **Solution**: Propose a solution
3. **Alternatives**: Any alternative solutions considered
4. **Use Cases**: Real-world use cases

## 🏷️ Labeling

We use the following labels for issues and PRs:

**Type:**
- `bug`: Something isn't working
- `enhancement`: New feature or request
- `documentation`: Documentation improvements
- `question`: Further information is requested

**Priority:**
- `priority:high`: High priority
- `priority:medium`: Medium priority
- `priority:low`: Low priority

**Status:**
- `status:needs-review`: Needs review
- `status:in-progress`: Currently being worked on
- `status:blocked`: Blocked by other issues

## 🎯 Areas for Contribution

We welcome contributions in these areas:

### 🔧 Core Features
- Authentication improvements
- Performance optimizations
- New middleware
- Database enhancements

### 📚 Documentation
- API documentation
- Tutorial improvements
- Code examples
- Video tutorials

### 🧪 Testing
- Test coverage improvements
- New test utilities
- Performance tests
- Load testing

### 🐳 DevOps
- Docker improvements
- CI/CD enhancements
- Monitoring setup
- Deployment guides

### 🎨 Frontend (if applicable)
- Admin interface
- API documentation themes
- Dashboard improvements

## 💬 Community

### Getting Help

- **GitHub Discussions**: For questions and general discussion
- **GitHub Issues**: For bug reports and feature requests
- **Discord/Slack**: Real-time chat (if available)

### Code of Conduct

Please note that this project is released with a [Code of Conduct](CODE_OF_CONDUCT.md). By participating in this project you agree to abide by its terms.

## 🏆 Recognition

Contributors will be recognized in:
- `CONTRIBUTORS.md` file
- Release notes
- GitHub contributors section
- Documentation acknowledgments

## 📚 Resources

### Useful Links
- [FastAPI Documentation](https://fastapi.tiangolo.com/)
- [SQLAlchemy Documentation](https://docs.sqlalchemy.org/)
- [Celery Documentation](https://docs.celeryproject.org/)
- [Pytest Documentation](https://docs.pytest.org/)

### Learning Resources
- [Python Type Hints](https://docs.python.org/3/library/typing.html)
- [Async/Await in Python](https://docs.python.org/3/library/asyncio.html)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)

## ❓ Questions?

If you have any questions about contributing, please:

1. Check existing documentation
2. Search existing issues/discussions
3. Create a new discussion
4. Reach out to maintainers

Thank you for contributing to Starlight! 🌟
