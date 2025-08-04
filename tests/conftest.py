"""Test configuration and fixtures."""

import asyncio
import os
import tempfile
from typing import AsyncGenerator, Generator
from unittest.mock import Mock, patch

import pytest
import pytest_asyncio
from fastapi.testclient import TestClient
from httpx import AsyncClient
from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker
from sqlalchemy.pool import StaticPool

from app.core.config.settings import get_settings
from app.infrastructure.database import Base, get_db
from app.infrastructure.database.models import User
from app.core.security.auth import get_password_hash
from app.main import app

# Test database URL
TEST_DATABASE_URL = "sqlite+aiosqlite:///./test.db"

# Create test engine
test_engine = create_async_engine(
    TEST_DATABASE_URL,
    connect_args={"check_same_thread": False},
    poolclass=StaticPool,
)

# Create test session
TestSessionLocal = sessionmaker(
    autocommit=False, autoflush=False, bind=test_engine, class_=AsyncSession
)


@pytest_asyncio.fixture(scope="session")
def event_loop():
    """Create an instance of the default event loop for the test session."""
    loop = asyncio.get_event_loop_policy().new_event_loop()
    yield loop
    loop.close()


@pytest_asyncio.fixture(scope="session")
async def setup_database():
    """Set up test database."""
    async with test_engine.begin() as conn:
        await conn.run_sync(Base.metadata.create_all)
    yield
    async with test_engine.begin() as conn:
        await conn.run_sync(Base.metadata.drop_all)


@pytest_asyncio.fixture
async def db_session(setup_database) -> AsyncGenerator[AsyncSession, None]:
    """Create a test database session."""
    async with TestSessionLocal() as session:
        yield session


async def override_get_db() -> AsyncGenerator[AsyncSession, None]:
    """Override database dependency for testing."""
    async with TestSessionLocal() as session:
        yield session


app.dependency_overrides[get_db] = override_get_db


@pytest.fixture
def client() -> Generator[TestClient, None, None]:
    """Create a test client."""
    with TestClient(app) as c:
        yield c


@pytest_asyncio.fixture
async def async_client() -> AsyncGenerator[AsyncClient, None]:
    """Create an async test client."""
    from fastapi.testclient import TestClient
    from httpx import ASGITransport
    
    transport = ASGITransport(app=app)
    async with AsyncClient(transport=transport, base_url="http://test") as ac:
        yield ac


# User fixtures
@pytest_asyncio.fixture
async def test_user(db_session: AsyncSession) -> User:
    """Create a test user."""
    user = User(
        username="testuser",
        email="test@example.com",
        hashed_password=get_password_hash("testpassword123"),
        full_name="Test User",
        is_active=True
    )
    db_session.add(user)
    await db_session.commit()
    await db_session.refresh(user)
    return user


@pytest_asyncio.fixture
async def test_superuser(db_session: AsyncSession) -> User:
    """Create a test superuser."""
    user = User(
        username="admin",
        email="admin@example.com",
        hashed_password=get_password_hash("adminpassword123"),
        full_name="Admin User",
        is_active=True,
        is_superuser=True
    )
    db_session.add(user)
    await db_session.commit()
    await db_session.refresh(user)
    return user


@pytest_asyncio.fixture
async def oauth_user(db_session: AsyncSession) -> User:
    """Create a test OAuth user."""
    user = User(
        username="oauthuser",
        email="oauth@example.com",
        full_name="OAuth User",
        is_active=True,
        oauth_provider="google",
        oauth_id="12345"
    )
    db_session.add(user)
    await db_session.commit()
    await db_session.refresh(user)
    return user


# Authentication fixtures
@pytest.fixture
def auth_headers(test_user: User) -> dict:
    """Create authentication headers for test user."""
    from app.core.security.auth import create_access_token
    token = create_access_token(subject=test_user.username)
    return {"Authorization": f"Bearer {token}"}


@pytest.fixture
def admin_headers(test_superuser: User) -> dict:
    """Create authentication headers for admin user."""
    from app.core.security.auth import create_access_token
    token = create_access_token(subject=test_superuser.username)
    return {"Authorization": f"Bearer {token}"}


# Mock fixtures
@pytest.fixture
def mock_redis():
    """Mock Redis connection."""
    with patch('app.core.cache.redis.Redis') as mock:
        mock_instance = Mock()
        mock.return_value = mock_instance
        yield mock_instance


@pytest.fixture
def mock_celery():
    """Mock Celery tasks."""
    with patch('app.infrastructure.tasks.celery_app.celery_app') as mock:
        yield mock


@pytest.fixture
def mock_s3():
    """Mock S3 client."""
    with patch('boto3.client') as mock:
        yield mock


@pytest.fixture
def temp_file():
    """Create a temporary file for testing."""
    with tempfile.NamedTemporaryFile(delete=False) as tmp:
        tmp.write(b"test file content")
        tmp.flush()
        yield tmp.name
    os.unlink(tmp.name)


@pytest.fixture
def sample_image():
    """Create a sample image for testing."""
    from PIL import Image
    import io
    
    img = Image.new('RGB', (100, 100), color='red')
    img_bytes = io.BytesIO()
    img.save(img_bytes, format='JPEG')
    img_bytes.seek(0)
    return img_bytes


# Environment fixtures
@pytest.fixture(autouse=True)
def test_environment():
    """Set test environment variables."""
    os.environ.update({
        'ENVIRONMENT': 'testing',
        'DATABASE_URL': TEST_DATABASE_URL,
        'REDIS_URL': 'redis://localhost:6379/15',
        'SECRET_KEY': 'test-secret-key-for-testing-only-32-chars',
        'CELERY_BROKER_URL': 'redis://localhost:6379/14',
        'CELERY_RESULT_BACKEND': 'redis://localhost:6379/14',
    })
    yield
    # Cleanup
    for key in ['ENVIRONMENT', 'DATABASE_URL', 'REDIS_URL', 'SECRET_KEY', 
                'CELERY_BROKER_URL', 'CELERY_RESULT_BACKEND']:
        os.environ.pop(key, None)
