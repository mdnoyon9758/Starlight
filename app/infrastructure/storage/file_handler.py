"""File storage handler with cloud storage support."""

import os
import uuid
from abc import ABC, abstractmethod
from io import BytesIO
from pathlib import Path
from typing import Optional, Tuple, BinaryIO
from PIL import Image
import aiofiles
import boto3
from botocore.exceptions import ClientError, NoCredentialsError

from app.core.config.settings import get_settings
from app.core.logging.logger import get_logger

settings = get_settings()
logger = get_logger(__name__)


class StorageBackend(ABC):
    """Abstract base class for storage backends."""
    
    @abstractmethod
    async def save_file(self, file_content: bytes, filename: str, content_type: str = None) -> str:
        """Save file and return the file URL."""
        pass
    
    @abstractmethod
    async def delete_file(self, file_path: str) -> bool:
        """Delete file and return success status."""
        pass
    
    @abstractmethod
    async def get_file_url(self, file_path: str) -> str:
        """Get public URL for a file."""
        pass


class LocalStorageBackend(StorageBackend):
    """Local file system storage backend."""
    
    def __init__(self, upload_folder: str = None):
        self.upload_folder = Path(upload_folder or settings.upload_folder)
        self.upload_folder.mkdir(parents=True, exist_ok=True)
    
    async def save_file(self, file_content: bytes, filename: str, content_type: str = None) -> str:
        """Save file to local filesystem."""
        file_path = self.upload_folder / filename
        
        async with aiofiles.open(file_path, 'wb') as f:
            await f.write(file_content)
        
        return str(file_path)
    
    async def delete_file(self, file_path: str) -> bool:
        """Delete file from local filesystem."""
        try:
            path = Path(file_path)
            if path.exists():
                path.unlink()
                return True
            return False
        except Exception as e:
            logger.error(f"Error deleting file {file_path}", error=str(e))
            return False
    
    async def get_file_url(self, file_path: str) -> str:
        """Get local file URL."""
        # In a real app, this would be a proper URL served by your web server
        return f"/uploads/{Path(file_path).name}"


class S3StorageBackend(StorageBackend):
    """Amazon S3 storage backend."""
    
    def __init__(self):
        if not all([settings.aws_access_key_id, settings.aws_secret_access_key, settings.s3_bucket_name]):
            raise ValueError("AWS credentials and S3 bucket name must be configured")
        
        self.s3_client = boto3.client(
            's3',
            aws_access_key_id=settings.aws_access_key_id,
            aws_secret_access_key=settings.aws_secret_access_key,
            region_name=settings.aws_default_region
        )
        self.bucket_name = settings.s3_bucket_name
    
    async def save_file(self, file_content: bytes, filename: str, content_type: str = None) -> str:
        """Save file to S3."""
        try:
            extra_args = {}
            if content_type:
                extra_args['ContentType'] = content_type
            
            self.s3_client.put_object(
                Bucket=self.bucket_name,
                Key=filename,
                Body=file_content,
                **extra_args
            )
            
            return f"s3://{self.bucket_name}/{filename}"
        
        except (ClientError, NoCredentialsError) as e:
            logger.error(f"Error uploading file to S3", error=str(e))
            raise
    
    async def delete_file(self, file_path: str) -> bool:
        """Delete file from S3."""
        try:
            # Extract key from S3 path
            key = file_path.replace(f"s3://{self.bucket_name}/", "")
            
            self.s3_client.delete_object(Bucket=self.bucket_name, Key=key)
            return True
        
        except ClientError as e:
            logger.error(f"Error deleting file from S3", error=str(e))
            return False
    
    async def get_file_url(self, file_path: str) -> str:
        """Get S3 file URL."""
        key = file_path.replace(f"s3://{self.bucket_name}/", "")
        return f"https://{self.bucket_name}.s3.{settings.aws_default_region}.amazonaws.com/{key}"


class FileHandler:
    """Main file handler with image processing capabilities."""
    
    def __init__(self, storage_backend: StorageBackend = None):
        self.storage_backend = storage_backend or self._get_default_backend()
    
    def _get_default_backend(self) -> StorageBackend:
        """Get default storage backend based on configuration."""
        if settings.s3_bucket_name and settings.aws_access_key_id:
            try:
                return S3StorageBackend()
            except ValueError:
                logger.warning("S3 not properly configured, falling back to local storage")
        
        return LocalStorageBackend()
    
    def _validate_file(self, filename: str, file_size: int) -> Tuple[bool, str]:
        """Validate file based on extension and size."""
        # Check file size
        if file_size > settings.max_upload_size:
            return False, f"File size exceeds maximum allowed size of {settings.max_upload_size} bytes"
        
        # Check file extension
        file_extension = Path(filename).suffix.lower().lstrip('.')
        if file_extension not in settings.allowed_file_extensions:
            return False, f"File extension '{file_extension}' not allowed"
        
        return True, ""
    
    def _generate_unique_filename(self, original_filename: str) -> str:
        """Generate unique filename while preserving extension."""
        file_extension = Path(original_filename).suffix.lower()
        unique_id = str(uuid.uuid4())
        return f"{unique_id}{file_extension}"
    
    async def process_image(self, file_content: bytes, max_width: int = 1920, max_height: int = 1080) -> bytes:
        """Process and resize image."""
        try:
            image = Image.open(BytesIO(file_content))
            
            # Convert RGBA to RGB if necessary
            if image.mode == 'RGBA':
                image = image.convert('RGB')
            
            # Resize if needed
            if image.width > max_width or image.height > max_height:
                image.thumbnail((max_width, max_height), Image.Resampling.LANCZOS)
            
            # Save processed image
            output = BytesIO()
            format = 'JPEG' if image.format != 'PNG' else 'PNG'
            image.save(output, format=format, quality=85, optimize=True)
            
            return output.getvalue()
        
        except Exception as e:
            logger.error(f"Error processing image", error=str(e))
            raise ValueError(f"Error processing image: {str(e)}")
    
    async def save_file(
        self, 
        file_content: bytes, 
        filename: str, 
        content_type: str = None,
        process_image: bool = False
    ) -> Tuple[str, str]:
        """
        Save file with optional image processing.
        
        Returns:
            Tuple[str, str]: (file_path, file_url)
        """
        # Validate file
        is_valid, error_message = self._validate_file(filename, len(file_content))
        if not is_valid:
            raise ValueError(error_message)
        
        # Generate unique filename
        unique_filename = self._generate_unique_filename(filename)
        
        # Process image if requested and file is an image
        if process_image and content_type and content_type.startswith('image/'):
            try:
                file_content = await self.process_image(file_content)
            except ValueError:
                # If image processing fails, save original file
                logger.warning(f"Image processing failed for {filename}, saving original")
        
        # Save file using storage backend
        file_path = await self.storage_backend.save_file(file_content, unique_filename, content_type)
        file_url = await self.storage_backend.get_file_url(file_path)
        
        return file_path, file_url
    
    async def delete_file(self, file_path: str) -> bool:
        """Delete file using storage backend."""
        return await self.storage_backend.delete_file(file_path)
    
    async def get_file_url(self, file_path: str) -> str:
        """Get file URL using storage backend."""
        return await self.storage_backend.get_file_url(file_path)


# Global file handler instance
file_handler = FileHandler()
