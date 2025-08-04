#!/usr/bin/env python3
"""Test database functionality."""

def test_database_components():
    """Test database components."""
    try:
        print("🗄️ Testing Database Components...")
        
        # Test database imports
        print("\n📥 Testing database imports...")
        from app.infrastructure.database import Base, get_db, engine, SessionLocal
        print("✓ Database Base imported successfully")
        print("✓ Database engine imported successfully") 
        print("✓ SessionLocal imported successfully")
        print("✓ get_db function imported successfully")
        
        # Test models import
        print("\n📋 Testing models...")
        from app.infrastructure.database.models import User
        print("✓ User model imported successfully")
        
        # Test model structure
        print(f"✓ User table name: {User.__tablename__}")
        
        # Check User model fields
        user_columns = []
        for column in User.__table__.columns:
            column_info = f"{column.name} ({column.type})"
            if column.nullable:
                column_info += " - nullable"
            if column.primary_key:
                column_info += " - primary key"
            if column.unique:
                column_info += " - unique"
            user_columns.append(column_info)
        
        print("✓ User model columns:")
        for col in user_columns:
            print(f"  - {col}")
        
        # Test settings integration
        print("\n⚙️ Testing database settings...")
        from app.core.config.settings import get_settings
        settings = get_settings()
        
        print(f"✓ Database URL: {settings.database_url}")
        print(f"✓ Database URL (sync): {settings.database_url_sync}")
        print(f"✓ Database echo: {settings.database_echo}")
        print(f"✓ Database pool size: {settings.database_pool_size}")
        
        # Test engine configuration
        print(f"✓ Engine URL: {engine.url}")
        print(f"✓ Engine echo: {engine.echo}")
        
        print("\n✅ Database component tests completed successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Database test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

def test_user_model():
    """Test User model functionality."""
    try:
        print("\n👤 Testing User model...")
        
        from app.infrastructure.database.models import User
        
        # Test model creation (without database)
        user_data = {
            'username': 'testuser',
            'email': 'test@example.com',
            'hashed_password': 'hashedpassword123',
            'full_name': 'Test User',
            'is_active': True,
            'oauth_provider': 'google',
            'oauth_id': '12345'
        }
        
        # Create user instance
        user = User(**user_data)
        
        print(f"✓ User created: {user.username}")
        print(f"✓ User email: {user.email}")
        print(f"✓ User active: {user.is_active}")
        print(f"✓ OAuth provider: {user.oauth_provider}")
        print(f"✓ OAuth ID: {user.oauth_id}")
        
        # Test OAuth fields
        assert hasattr(user, 'oauth_provider'), "OAuth provider field missing"
        assert hasattr(user, 'oauth_id'), "OAuth ID field missing"
        assert hasattr(user, 'full_name'), "Full name field missing"
        
        print("✓ All OAuth fields present")
        print("✓ User model structure validated")
        
        return True
        
    except Exception as e:
        print(f"❌ User model test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success1 = test_database_components()
    success2 = test_user_model()
    
    if success1 and success2:
        print("\n🎉 All database tests passed!")
    else:
        print("\n❌ Some database tests failed!")
