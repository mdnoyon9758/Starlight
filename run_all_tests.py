#!/usr/bin/env python3
"""Master test runner for all Starlight components."""

import sys
import time
from datetime import datetime

def print_header(title):
    """Print a formatted header."""
    print("\n" + "="*80)
    print(f" {title}")
    print("="*80)

def print_section(title):
    """Print a formatted section header."""
    print(f"\n{'─'*60}")
    print(f" {title}")
    print("─"*60)

def run_test_module(module_name, test_description):
    """Run a test module and return success status."""
    try:
        print_section(f"Running {test_description}")
        
        # Import and run the test
        if module_name == "test_imports":
            from test_imports import test_imports
            success = test_imports()
        elif module_name == "test_celery":
            from test_celery import test_celery_tasks
            success = test_celery_tasks()
        elif module_name == "test_file_handler":
            from test_file_handler import test_file_handler
            success = test_file_handler()
        elif module_name == "test_oauth":
            import test_oauth
            success1 = test_oauth.test_oauth_components()
            success2 = test_oauth.test_oauth_endpoints()
            success = success1 and success2
        elif module_name == "test_database":
            import test_database
            success1 = test_database.test_database_components()
            success2 = test_database.test_user_model()
            success = success1 and success2
        elif module_name == "test_fastapi":
            import test_fastapi
            success1 = test_fastapi.test_fastapi_components()
            success2 = test_fastapi.test_security_components()
            success = success1 and success2
        else:
            print(f"❌ Unknown test module: {module_name}")
            return False
        
        if success:
            print(f"✅ {test_description} - PASSED")
        else:
            print(f"❌ {test_description} - FAILED")
        
        return success
        
    except Exception as e:
        print(f"❌ {test_description} - ERROR: {e}")
        return False

def main():
    """Run all tests."""
    print_header("🌟 STARLIGHT COMPLETE TEST SUITE 🌟")
    print(f"Started at: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    
    # Define all test modules
    test_modules = [
        ("test_imports", "Basic Import Tests"),
        ("test_database", "Database & Models Tests"),
        ("test_celery", "Celery Tasks Tests"),
        ("test_file_handler", "File Handler Tests"),
        ("test_oauth", "OAuth Components Tests"),
        ("test_fastapi", "FastAPI Integration Tests"),
    ]
    
    # Track results
    results = {}
    total_tests = len(test_modules)
    passed_tests = 0
    
    start_time = time.time()
    
    # Run each test module
    for module_name, description in test_modules:
        try:
            success = run_test_module(module_name, description)
            results[description] = success
            if success:
                passed_tests += 1
        except KeyboardInterrupt:
            print("\n\n⚠️  Tests interrupted by user")
            break
        except Exception as e:
            print(f"❌ Unexpected error running {description}: {e}")
            results[description] = False
    
    end_time = time.time()
    duration = end_time - start_time
    
    # Print comprehensive results
    print_header("📊 TEST RESULTS SUMMARY")
    
    print(f"\n📈 Overall Statistics:")
    print(f"   • Total Tests: {total_tests}")
    print(f"   • Passed: {passed_tests}")
    print(f"   • Failed: {total_tests - passed_tests}")
    print(f"   • Success Rate: {(passed_tests/total_tests)*100:.1f}%")
    print(f"   • Duration: {duration:.2f} seconds")
    
    print(f"\n📋 Detailed Results:")
    for description, success in results.items():
        status = "✅ PASSED" if success else "❌ FAILED"
        print(f"   • {description}: {status}")
    
    # Component-specific summary
    print_section("🔧 COMPONENT STATUS")
    
    components = {
        "⚙️  Core Configuration": results.get("Basic Import Tests", False),
        "🗄️  Database & Models": results.get("Database & Models Tests", False),
        "🔄 Celery Tasks": results.get("Celery Tasks Tests", False),
        "📁 File Storage": results.get("File Handler Tests", False),
        "🔐 OAuth System": results.get("OAuth Components Tests", False),
        "🚀 FastAPI Integration": results.get("FastAPI Integration Tests", False),
    }
    
    for component, status in components.items():
        icon = "✅" if status else "❌"
        print(f"   {icon} {component}")
    
    # Final status
    if passed_tests == total_tests:
        print_header("🎉 ALL TESTS PASSED! STARLIGHT IS READY! 🎉")
        print("🚀 Your Starlight application is fully tested and ready for deployment!")
        print("\n📝 Next Steps:")
        print("   1. Run 'docker-compose up' to start all services")
        print("   2. Test the API endpoints manually")
        print("   3. Configure OAuth credentials for production")
        print("   4. Set up your cloud storage (S3) if needed")
        return 0
    else:
        print_header("⚠️  SOME TESTS FAILED")
        print("🔧 Please review the failed tests above and fix any issues.")
        print("💡 Most failures are likely due to missing dependencies or configuration.")
        return 1

if __name__ == "__main__":
    exit_code = main()
    sys.exit(exit_code)
