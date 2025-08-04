"""Authentication request schemas."""

from pydantic import BaseModel, EmailStr, Field


class UserCreate(BaseModel):
    """User creation schema."""
    
    username: str = Field(..., min_length=3, max_length=50)
    email: EmailStr
    password: str = Field(..., min_length=8, max_length=100)


class UserLogin(BaseModel):
    """User login schema."""
    
    username: str
    password: str


class Token(BaseModel):
    """Token response schema."""
    
    access_token: str
    token_type: str
