"""Integration tests for API endpoints."""

import pytest
from httpx import AsyncClient


class TestAPIIntegration:
    """Test API integration."""

    @pytest.mark.asyncio
    async def test_root_endpoint(self, async_client: AsyncClient):
        """Test root endpoint."""
        response = await async_client.get("/")
        assert response.status_code == 200
        
        data = response.json()
        assert "message" in data
        assert "version" in data
        assert "docs" in data

    @pytest.mark.asyncio
    async def test_health_check(self, async_client: AsyncClient):
        """Test health check endpoint."""
        response = await async_client.get("/health")
        assert response.status_code == 200
        
        data = response.json()
        assert data["status"] == "healthy"

    @pytest.mark.asyncio
    async def test_openapi_docs(self, async_client: AsyncClient):
        """Test OpenAPI documentation endpoint."""
        response = await async_client.get("/api/v1/openapi.json")
        assert response.status_code == 200
        
        data = response.json()
        assert "openapi" in data
        assert "info" in data
        assert "paths" in data
