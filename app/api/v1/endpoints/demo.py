"""Demo endpoints for showcasing Starlight features."""

from fastapi import APIRouter, Depends, HTTPException
from typing import List
import datetime
from sqlalchemy.ext.asyncio import AsyncSession

from app.infrastructure.database import get_db
from app.api.v1.dependencies.auth import get_current_user_optional
from app.infrastructure.database.models import User

router = APIRouter()


@router.get("/hello")
async def hello_world():
    """Simple hello world endpoint."""
    return {
        "message": "Hello from Starlight! 🌟",
        "timestamp": datetime.datetime.now().isoformat(),
        "framework": "FastAPI",
        "version": "0.1.0"
    }


@router.get("/features")
async def list_features():
    """List Starlight features."""
    return {
        "features": [
            "🔐 JWT Authentication",
            "📊 Auto-generated API docs", 
            "🗄️ Database integration",
            "❤️ Health checks",
            "📝 Request logging",
            "🔒 Security headers",
            "⚡ Async/await support",
            "🧪 Comprehensive testing",
            "🐳 Docker ready",
            "📈 Monitoring & metrics"
        ],
        "status": "production-ready"
    }


@router.get("/profile")
async def get_profile(current_user: User = Depends(get_current_user_optional)):
    """Get user profile (authentication demo)."""
    if not current_user:
        return {
            "message": "Not authenticated",
            "hint": "Register at /api/v1/auth/register or login at /api/v1/auth/login"
        }
    
    return {
        "user": {
            "id": current_user.id,
            "username": current_user.username,
            "email": current_user.email,
            "is_active": current_user.is_active,
            "created_at": current_user.created_at.isoformat() if current_user.created_at else None
        },
        "message": f"Welcome back, {current_user.username}! 👋"
    }


@router.get("/stats")
async def get_stats(db: AsyncSession = Depends(get_db)):
    """Get basic application statistics."""
    from sqlalchemy import text
    
    try:
        # Count total users
        result = await db.execute(text("SELECT COUNT(*) as count FROM users"))
        user_count = result.fetchone()
        total_users = user_count.count if user_count else 0
        
        # Count active users
        result = await db.execute(text("SELECT COUNT(*) as count FROM users WHERE is_active = true"))
        active_result = result.fetchone()
        active_users = active_result.count if active_result else 0
        
        return {
            "database": {
                "total_users": total_users,
                "active_users": active_users,
                "engine": "SQLite" if "sqlite" in str(db.bind.url) else "PostgreSQL"
            },
            "api": {
                "version": "v1",
                "docs_url": "/api/v1/docs",
                "health_url": "/health"
            },
            "uptime": "Ready ✅"
        }
    except Exception as e:
        return {
            "error": "Could not fetch stats",
            "hint": "Database might not be initialized yet",
            "details": str(e)
        }


@router.post("/echo")
async def echo_request(data: dict):
    """Echo back the request data (useful for testing)."""
    return {
        "echo": data,
        "received_at": datetime.datetime.now().isoformat(),
        "message": "This is your data echoed back!"
    }
