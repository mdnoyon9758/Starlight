"""User response schemas."""

from pydantic import BaseModel, EmailStr, ConfigDict


class UserBase(BaseModel):
    """Base user schema."""
    
    username: str
    email: EmailStr
    is_active: bool = True
    is_superuser: bool = False


class UserResponse(UserBase):
    """User response schema."""
    
    id: int
    
    model_config = ConfigDict(from_attributes=True)
