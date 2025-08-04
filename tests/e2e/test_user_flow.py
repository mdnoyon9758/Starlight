"""End-to-end tests for user flows."""

import pytest
from httpx import AsyncClient


class TestUserFlow:
    """Test complete user flows."""

    @pytest.mark.asyncio
    async def test_complete_user_registration_and_login_flow(self, async_client: AsyncClient):
        """Test complete user registration and login flow."""
        # Step 1: Register a new user
        user_data = {
            "username": "e2euser",
            "email": "e2e@example.com",
            "password": "e2epassword123"
        }
        
        register_response = await async_client.post("/api/v1/auth/register", json=user_data)
        assert register_response.status_code == 200
        
        user_info = register_response.json()
        assert user_info["username"] == user_data["username"]
        assert user_info["email"] == user_data["email"]
        
        # Step 2: Login with the registered user
        login_data = {
            "username": user_data["username"],
            "password": user_data["password"]
        }
        
        login_response = await async_client.post("/api/v1/auth/login", json=login_data)
        assert login_response.status_code == 200
        
        token_data = login_response.json()
        assert "access_token" in token_data
        assert token_data["token_type"] == "bearer"
        
        # Step 3: Verify we can't register the same user again
        duplicate_response = await async_client.post("/api/v1/auth/register", json=user_data)
        assert duplicate_response.status_code == 400
