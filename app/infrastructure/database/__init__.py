"""Database module."""

from sqlalchemy.ext.asyncio import AsyncSession, create_async_engine
from sqlalchemy.orm import sessionmaker, declarative_base

from app.core.config.settings import get_settings

settings = get_settings()

# Create async engine
engine = create_async_engine(settings.database_url, echo=settings.database_echo)

# Create async session
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine, class_=AsyncSession)

# Base class for declarative models
Base = declarative_base()

async def get_db() -> AsyncSession:
    """Provide a transactional scope for database session."""
    async with SessionLocal() as session:
        yield session
