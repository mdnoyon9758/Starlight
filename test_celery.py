#!/usr/bin/env python3
"""Test Celery tasks functionality."""

def test_celery_tasks():
    """Test Celery tasks."""
    try:
        print("🔄 Testing Celery Tasks...")
        
        from app.infrastructure.tasks.celery_app import celery_app
        from app.infrastructure.tasks.tasks import send_email, process_data
        
        # Test Celery app configuration
        print(f"✓ Celery app name: {celery_app.main}")
        print(f"✓ Celery broker: {celery_app.conf.broker_url}")
        print(f"✓ Celery backend: {celery_app.conf.result_backend}")
        
        # Test task registration
        registered_tasks = list(celery_app.tasks.keys())
        print(f"✓ Registered tasks: {len(registered_tasks)}")
        
        # Check if our tasks are registered
        task_names = [
            "app.infrastructure.tasks.tasks.send_email",
            "app.infrastructure.tasks.tasks.process_data"
        ]
        
        for task_name in task_names:
            if task_name in registered_tasks:
                print(f"✓ Task registered: {task_name}")
            else:
                print(f"⚠️  Task not found: {task_name}")
        
        # Test task execution (without running worker)
        print("\n📧 Testing email task function...")
        result = send_email.run("Test Subject", "test@example.com", "Test Body")
        print(f"✓ Email task result: {result}")
        
        print("\n📊 Testing data processing task function...")
        test_data = {"name": "john", "status": "active"}
        result = process_data.run(test_data)
        print(f"✓ Data processing result: {result}")
        
        print("\n✅ Celery tests completed successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Celery test failed: {e}")
        import traceback
        traceback.print_exc()
        return False

if __name__ == "__main__":
    test_celery_tasks()
