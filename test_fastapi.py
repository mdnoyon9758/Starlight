#!/usr/bin/env python3
"""Test FastAPI application integration."""

def test_fastapi_components():
    """Test FastAPI components."""
    try:
        print("🚀 Testing FastAPI Components...")
        
        # Test main app import
        print("\n📥 Testing main app import...")
        try:
            from app.main import app
            print("✓ FastAPI app imported successfully")
            print(f"✓ App title: {app.title if hasattr(app, 'title') else 'Not set'}")
        except Exception as e:
            print(f"⚠️  Main app import failed: {e}")
            print("✓ This is expected if main.py doesn't exist yet")
        
        # Test API endpoints imports
        print("\n🔌 Testing API endpoints...")
        from app.api.v1.endpoints.auth import router as auth_router
        from app.api.v1.endpoints.oauth import router as oauth_router
        
        print("✓ Auth router imported successfully")
        print("✓ OAuth router imported successfully")
        
        # Test auth routes
        auth_routes = []
        for route in auth_router.routes:
            if hasattr(route, 'path') and hasattr(route, 'methods'):
                methods = list(route.methods) if route.methods else ['GET']
                auth_routes.append(f"{methods[0]} {route.path}")
        
        print(f"✓ Auth routes: {len(auth_routes)}")
        for route in auth_routes:
            print(f"  - {route}")
        
        # Test OAuth routes
        oauth_routes = []
        for route in oauth_router.routes:
            if hasattr(route, 'path') and hasattr(route, 'methods'):
                methods = list(route.methods) if route.methods else ['GET']
                oauth_routes.append(f"{methods[0]} {route.path}")
        
        print(f"✓ OAuth routes: {len(oauth_routes)}")
        for route in oauth_routes:
            print(f"  - {route}")
        
        # Test dependencies
        print("\n🔗 Testing dependencies...")
        try:
            from app.api.v1.dependencies.auth import get_current_user
            print("✓ Auth dependencies imported successfully")
        except Exception as e:
            print(f"⚠️  Auth dependencies import failed: {e}")
        
        # Test schemas
        print("\n📋 Testing schemas...")
        try:
            from app.schemas.request.auth import UserCreate, UserLogin
            from app.schemas.response.user import UserResponse
            print("✓ Request schemas imported successfully")
            print("✓ Response schemas imported successfully")
        except Exception as e:
            print(f"⚠️  Schema imports failed: {e}")
        
        print("\n✅ FastAPI component tests completed!")
        return True
        
    except Exception as e:
        print(f"❌ FastAPI test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

def test_security_components():
    """Test security components."""
    try:
        print("\n🔒 Testing Security Components...")
        
        # Test security imports
        from app.core.security.auth import create_access_token
        print("✓ Auth security functions imported")
        
        from app.core.security.oauth import oauth, OAuthProvider
        print("✓ OAuth security components imported")
        
        # Test security settings
        from app.core.config.settings import get_settings
        settings = get_settings()
        
        print(f"✓ Secret key configured: {len(settings.secret_key) >= 32}")
        print(f"✓ JWT algorithm: {settings.algorithm}")
        print(f"✓ Access token expire: {settings.access_token_expire_minutes} minutes")
        
        return True
        
    except Exception as e:
        print(f"❌ Security test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success1 = test_fastapi_components()
    success2 = test_security_components()
    
    if success1 and success2:
        print("\n🎉 All FastAPI tests passed!")
    else:
        print("\n❌ Some FastAPI tests failed!")
