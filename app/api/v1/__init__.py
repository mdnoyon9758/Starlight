"""API v1 module."""

from fastapi import APIRouter

from .endpoints import auth_router

api_router = APIRouter()
api_router.include_router(auth_router, prefix="/auth", tags=["authentication"])
