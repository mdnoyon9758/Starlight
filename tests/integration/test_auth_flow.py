"""Integration tests for authentication flow."""

import pytest
from httpx import AsyncClient
from fastapi.testclient import TestClient

from app.main import app
from app.core.config import get_settings
from app.infrastructure.database import get_db


@pytest.fixture
async def client():
    """Create test client."""
    async with AsyncClient(app=app, base_url="http://test") as ac:
        yield ac


@pytest.fixture
def settings():
    """Get test settings."""
    return get_settings()


class TestAuthenticationFlow:
    """Test authentication flow integration."""

    async def test_user_registration_flow(self, client: AsyncClient):
        """Test complete user registration flow."""
        # Test user registration
        user_data = {
            "username": "testuser",
            "email": "test@example.com",
            "password": "TestPassword123!"
        }
        
        response = await client.post("/api/v1/auth/register", json=user_data)
        assert response.status_code == 200
        user = response.json()
        
        assert user["username"] == user_data["username"]
        assert user["email"] == user_data["email"]
        assert "id" in user

    async def test_user_login_flow(self, client: AsyncClient):
        """Test user login flow."""
        # First register a user
        user_data = {
            "username": "loginuser",
            "email": "login@example.com",
            "password": "TestPassword123!"
        }
        
        await client.post("/api/v1/auth/register", json=user_data)
        
        # Then login
        login_data = {
            "username": "loginuser",
            "password": "TestPassword123!"
        }
        
        response = await client.post("/api/v1/auth/login", json=login_data)
        assert response.status_code == 200
        
        token_data = response.json()
        assert "access_token" in token_data
        assert token_data["token_type"] == "bearer"

    async def test_protected_endpoint_access(self, client: AsyncClient):
        """Test accessing protected endpoints with token."""
        # Register and login to get token
        user_data = {
            "username": "protecteduser",
            "email": "protected@example.com",
            "password": "TestPassword123!"
        }
        
        await client.post("/api/v1/auth/register", json=user_data)
        
        login_response = await client.post("/api/v1/auth/login", json={
            "username": "protecteduser",
            "password": "TestPassword123!"
        })
        
        token = login_response.json()["access_token"]
        
        # Access protected endpoint
        headers = {"Authorization": f"Bearer {token}"}
        response = await client.get("/api/v1/users/me", headers=headers)
        
        # Should be successful with valid token
        assert response.status_code == 200

    async def test_invalid_token_access(self, client: AsyncClient):
        """Test accessing protected endpoints with invalid token."""
        headers = {"Authorization": "Bearer invalid_token"}
        response = await client.get("/api/v1/users/me", headers=headers)
        
        assert response.status_code == 401
