"""Database models."""

from sqlalchemy import Column, Integer, String, Boolean

from . import Base


class User(Base):
    """User table model."""

    __tablename__ = "users"

    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True, index=True, nullable=False)
    email = Column(String, unique=True, index=True, nullable=False)
    hashed_password = Column(String, nullable=True)  # Allow null for OAuth users
    full_name = Column(String, nullable=True)
    is_active = Column(Boolean, default=True)
    is_superuser = Column(Boolean, default=False)
    oauth_provider = Column(String, nullable=True)  # e.g., 'google', 'github'
    oauth_id = Column(String, nullable=True)  # OAuth provider user ID
