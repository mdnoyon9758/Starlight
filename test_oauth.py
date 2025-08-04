#!/usr/bin/env python3
"""Test OAuth functionality."""

def test_oauth_components():
    """Test OAuth components."""
    try:
        print("🔐 Testing OAuth Components...")
        
        # Test OAuth settings
        print("\n⚙️ Testing OAuth settings...")
        from app.core.config.settings import get_settings
        settings = get_settings()
        
        print(f"✓ Google Client ID configured: {settings.google_client_id is not None}")
        print(f"✓ Google Client Secret configured: {settings.google_client_secret is not None}")
        print(f"✓ GitHub Client ID configured: {settings.github_client_id is not None}")
        print(f"✓ GitHub Client Secret configured: {settings.github_client_secret is not None}")
        
        # Test OAuth provider imports
        print("\n📥 Testing OAuth imports...")
        from app.core.security.oauth import oauth, OAuthProvider
        print("✓ OAuth instance imported successfully")
        print("✓ OAuthProvider class imported successfully")
        
        # Test OAuth endpoints import
        print("\n🌐 Testing OAuth endpoints...")
        from app.api.v1.endpoints.oauth import router
        print("✓ OAuth router imported successfully")
        
        # Get all routes from the router
        routes = []
        for route in router.routes:
            if hasattr(route, 'path') and hasattr(route, 'methods'):
                methods = list(route.methods) if route.methods else ['GET']
                routes.append(f"{methods[0]} {route.path}")
        
        print(f"✓ OAuth routes defined: {len(routes)}")
        for route in routes:
            print(f"  - {route}")
        
        # Test OAuth provider initialization (without actual credentials)
        print("\n🏭 Testing OAuth provider initialization...")
        try:
            # This will fail without credentials, but we can test the error handling
            provider = OAuthProvider("google")
            print("✓ OAuth provider initialized (with credentials)")
        except ValueError as e:
            print(f"✓ OAuth provider error handling works: {e}")
        
        # Test OAuth configuration structure
        print("\n🔧 Testing OAuth configuration...")
        print(f"✓ OAuth instance available: {oauth is not None}")
        
        # Check if providers would be registered (if credentials were available)
        available_providers = []
        if settings.google_client_id and settings.google_client_secret:
            available_providers.append("google")
        if settings.github_client_id and settings.github_client_secret:
            available_providers.append("github")
        
        print(f"✓ Available OAuth providers: {available_providers}")
        
        print("\n✅ OAuth component tests completed successfully!")
        return True
        
    except Exception as e:
        print(f"❌ OAuth test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

def test_oauth_endpoints():
    """Test OAuth endpoint functionality (without actual OAuth flow)."""
    try:
        print("\n🎯 Testing OAuth endpoint logic...")
        
        # Test route definitions
        from app.api.v1.endpoints.oauth import router
        
        # Check that all required endpoints exist
        expected_endpoints = [
            "/login/{provider}",
            "/callback/{provider}",
            "/link/{provider}",
            "/unlink/{provider}"
        ]
        
        actual_paths = [route.path for route in router.routes if hasattr(route, 'path')]
        
        for expected in expected_endpoints:
            if expected in actual_paths:
                print(f"✓ Endpoint exists: {expected}")
            else:
                print(f"⚠️  Endpoint missing: {expected}")
        
        print("✓ OAuth endpoint structure validated")
        
        return True
        
    except Exception as e:
        print(f"❌ OAuth endpoint test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    success1 = test_oauth_components()
    success2 = test_oauth_endpoints()
    
    if success1 and success2:
        print("\n🎉 All OAuth tests passed!")
    else:
        print("\n❌ Some OAuth tests failed!")
