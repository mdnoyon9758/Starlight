"""Sample Celery tasks."""

from .celery_app import celery_app

@celery_app.task(bind=True)
def send_email(self, subject: str, recipient: str, body: str) - str:
    """Send an email."""
    try:
        # Implement email sending logic
        return f"Email sent to {recipient} with subject '{subject}'"
    except Exception as e:
        self.retry(exc=e)

@celery_app.task(bind=True)
def process_data(self, data: dict) - dict:
    """Process data asynchronously."""
    try:
        # Complex data processing logic
        return {k: v.upper() if isinstance(v, str) else v for k, v in data.items()}
    except Exception as e:
        self.retry(exc=e)

@celery_app.task(bind=True, max_retries=5, countdown=60)
def file_processing_workflow(self, file_path: str, user_id: int) - dict:
    """Complex file processing workflow with retry logic."""
    try:
        # File validation
        if not file_path:
            raise ValueError("File path cannot be empty")
        
        # Simulate file processing
        result = {
            "file_path": file_path,
            "user_id": user_id,
            "status": "processed",
            "size": 1024  # Simulated file size
        }
        
        return result
    except Exception as e:
        if self.request.retries < self.max_retries:
            self.retry(exc=e, countdown=60 * (2 ** self.request.retries))
        raise

@celery_app.task(bind=True)
def cleanup_task(self, resource_id: str) - dict:
    """Clean up resources after processing."""
    try:
        # Cleanup logic
        return {"resource_id": resource_id, "status": "cleaned"}
    except Exception as e:
        self.retry(exc=e, countdown=30)

@celery_app.task
def notification_task(user_id: int, message: str, notification_type: str = "info") - dict:
    """Send notification to user."""
    # Notification logic
    return {
        "user_id": user_id,
        "message": message,
        "type": notification_type,
        "sent": True
    }

# Task workflow example
def complete_file_workflow(file_path: str, user_id: int):
    """Complete file processing workflow chain."""
    from celery import chain
    
    workflow = chain(
        file_processing_workflow.s(file_path, user_id),
        cleanup_task.s(),
        notification_task.s(user_id, "File processing completed")
    )
    
    return workflow.apply_async()

