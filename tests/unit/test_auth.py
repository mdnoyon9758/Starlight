"""Test authentication functionality."""

import pytest
from fastapi.testclient import TestClient
from httpx import AsyncClient

from app.core.security import get_password_hash, verify_password


class TestPasswordHashing:
    """Test password hashing functions."""

    def test_password_hashing(self):
        """Test password hashing and verification."""
        password = "testpassword123"
        hashed = get_password_hash(password)
        
        assert hashed != password
        assert verify_password(password, hashed)
        assert not verify_password("wrongpassword", hashed)


class TestAuthEndpoints:
    """Test authentication endpoints."""
    pytestmark = pytest.mark.usefixtures("setup_database")

    @pytest.mark.asyncio
    async def test_register_user(self, async_client: AsyncClient):
        """Test user registration."""
        user_data = {
            "username": "testuser",
            "email": "test@example.com",
            "password": "testpassword123"
        }
        
        response = await async_client.post("/api/v1/auth/register", json=user_data)
        assert response.status_code == 200
        
        data = response.json()
        assert data["username"] == user_data["username"]
        assert data["email"] == user_data["email"]
        assert "id" in data

    @pytest.mark.asyncio
    async def test_login_user(self, async_client: AsyncClient):
        """Test user login."""
        # First register a user
        user_data = {
            "username": "loginuser",
            "email": "login@example.com",
            "password": "testpassword123"
        }
        
        await async_client.post("/api/v1/auth/register", json=user_data)
        
        # Then try to login
        login_data = {
            "username": "loginuser",
            "password": "testpassword123"
        }
        
        response = await async_client.post("/api/v1/auth/login", json=login_data)
        assert response.status_code == 200
        
        data = response.json()
        assert "access_token" in data
        assert data["token_type"] == "bearer"

    @pytest.mark.asyncio
    async def test_login_invalid_credentials(self, async_client: AsyncClient):
        """Test login with invalid credentials."""
        login_data = {
            "username": "nonexistent",
            "password": "wrongpassword"
        }
        
        response = await async_client.post("/api/v1/auth/login", json=login_data)
        assert response.status_code == 401
