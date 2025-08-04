#!/usr/bin/env python3
"""Test imports to verify modules work correctly."""

def test_imports():
    """Test all critical imports."""
    # Test settings import
    from app.core.config.settings import get_settings
    settings = get_settings()
    assert settings.app_name is not None
    
    # Test Celery import
    from app.infrastructure.tasks.celery_app import celery_app
    assert celery_app is not None
    
    # Test tasks import
    from app.infrastructure.tasks.tasks import send_email, process_data
    assert send_email is not None
    assert process_data is not None
    
    # Test file handler import
    from app.infrastructure.storage.file_handler import FileHandler
    assert FileHandler is not None

if __name__ == "__main__":
    test_imports()
