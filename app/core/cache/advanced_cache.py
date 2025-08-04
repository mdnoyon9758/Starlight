"""Advanced caching layer with Redis clustering and distributed cache support."""

import json
import pickle
import hashlib
from typing import Any, Optional, Union, List
from datetime import datetime, timedelta
import asyncio
import structlog
from redis.asyncio import Redis, RedisCluster
from redis.asyncio.retry import Retry
from redis.asyncio.backoff import ExponentialBackoff

logger = structlog.get_logger(__name__)


class AdvancedCacheManager:
    """Advanced cache manager with clustering and distributed cache support."""
    
    def __init__(
        self,
        redis_urls: Union[str, List[str]] = None,
        cluster_mode: bool = False,
        default_ttl: int = 3600,
        max_retries: int = 3,
        retry_delay: float = 0.1
    ):
        self.redis_urls = redis_urls or ["redis://localhost:6379"]
        self.cluster_mode = cluster_mode
        self.default_ttl = default_ttl
        self.max_retries = max_retries
        self.retry_delay = retry_delay
        self._redis = None
        self._stats = {
            "hits": 0,
            "misses": 0,
            "sets": 0,
            "deletes": 0,
            "errors": 0
        }
    
    async def initialize(self):
        """Initialize Redis connection."""
        try:
            retry = Retry(ExponentialBackoff(), self.max_retries)
            
            if self.cluster_mode and isinstance(self.redis_urls, list):
                startup_nodes = [{"host": url.split("://")[1].split(":")[0], 
                                "port": int(url.split(":")[-1])} 
                               for url in self.redis_urls]
                self._redis = RedisCluster(
                    startup_nodes=startup_nodes,
                    decode_responses=False,
                    retry=retry,
                    health_check_interval=30
                )
            else:
                url = self.redis_urls[0] if isinstance(self.redis_urls, list) else self.redis_urls
                self._redis = Redis.from_url(
                    url,
                    decode_responses=False,
                    retry=retry,
                    health_check_interval=30
                )
            
            # Test connection
            await self._redis.ping()
            logger.info("Advanced cache initialized successfully", 
                       cluster_mode=self.cluster_mode)
            
        except Exception as e:
            logger.error("Failed to initialize cache", error=str(e))
            raise
    
    async def get(self, key: str, default: Any = None) -> Any:
        """Get value from cache."""
        try:
            cache_key = self._generate_key(key)
            value = await self._redis.get(cache_key)
            
            if value is None:
                self._stats["misses"] += 1
                return default
            
            self._stats["hits"] += 1
            return self._deserialize(value)
            
        except Exception as e:
            self._stats["errors"] += 1
            logger.error("Cache get error", key=key, error=str(e))
            return default
    
    async def set(
        self, 
        key: str, 
        value: Any, 
        ttl: Optional[int] = None,
        tags: Optional[List[str]] = None
    ) -> bool:
        """Set value in cache with optional tags."""
        try:
            cache_key = self._generate_key(key)
            serialized_value = self._serialize(value)
            ttl = ttl or self.default_ttl
            
            # Set the main value
            await self._redis.setex(cache_key, ttl, serialized_value)
            
            # Handle tags for cache invalidation
            if tags:
                await self._set_tags(cache_key, tags, ttl)
            
            self._stats["sets"] += 1
            return True
            
        except Exception as e:
            self._stats["errors"] += 1
            logger.error("Cache set error", key=key, error=str(e))
            return False
    
    async def delete(self, key: str) -> bool:
        """Delete value from cache."""
        try:
            cache_key = self._generate_key(key)
            result = await self._redis.delete(cache_key)
            
            if result:
                self._stats["deletes"] += 1
            
            return bool(result)
            
        except Exception as e:
            self._stats["errors"] += 1
            logger.error("Cache delete error", key=key, error=str(e))
            return False
    
    async def invalidate_by_tag(self, tag: str) -> int:
        """Invalidate all cache entries with a specific tag."""
        try:
            tag_key = f"tag:{tag}"
            cache_keys = await self._redis.smembers(tag_key)
            
            if cache_keys:
                # Delete all keys associated with the tag
                deleted = await self._redis.delete(*cache_keys)
                # Delete the tag set itself
                await self._redis.delete(tag_key)
                
                logger.info("Invalidated cache by tag", 
                           tag=tag, deleted_keys=deleted)
                return deleted
            
            return 0
            
        except Exception as e:
            self._stats["errors"] += 1
            logger.error("Cache tag invalidation error", tag=tag, error=str(e))
            return 0
    
    async def get_stats(self) -> dict:
        """Get cache statistics."""
        try:
            redis_info = await self._redis.info("memory")
            
            return {
                **self._stats,
                "hit_rate": self._stats["hits"] / max(1, self._stats["hits"] + self._stats["misses"]),
                "redis_memory_used": redis_info.get("used_memory_human", "N/A"),
                "redis_memory_peak": redis_info.get("used_memory_peak_human", "N/A"),
                "redis_connected_clients": redis_info.get("connected_clients", 0)
            }
        except Exception as e:
            logger.error("Failed to get cache stats", error=str(e))
            return self._stats
    
    async def clear_all(self) -> bool:
        """Clear all cache entries."""
        try:
            await self._redis.flushdb()
            logger.warning("All cache entries cleared")
            return True
        except Exception as e:
            logger.error("Failed to clear cache", error=str(e))
            return False
    
    def _generate_key(self, key: str) -> str:
        """Generate cache key with namespace."""
        return f"starlight:cache:{key}"
    
    def _serialize(self, value: Any) -> bytes:
        """Serialize value for storage."""
        if isinstance(value, (str, int, float, bool)):
            return json.dumps(value).encode()
        else:
            return pickle.dumps(value)
    
    def _deserialize(self, value: bytes) -> Any:
        """Deserialize value from storage."""
        try:
            # Try JSON first for simple types
            return json.loads(value.decode())
        except (json.JSONDecodeError, UnicodeDecodeError):
            # Fall back to pickle for complex types
            return pickle.loads(value)
    
    async def _set_tags(self, cache_key: str, tags: List[str], ttl: int):
        """Set tags for cache key."""
        for tag in tags:
            tag_key = f"tag:{tag}"
            await self._redis.sadd(tag_key, cache_key)
            await self._redis.expire(tag_key, ttl)


# Singleton instance
cache_manager = AdvancedCacheManager()


# Decorator for caching function results
def cached(
    key_prefix: str = "",
    ttl: Optional[int] = None,
    tags: Optional[List[str]] = None
):
    """Decorator to cache function results."""
    def decorator(func):
        async def async_wrapper(*args, **kwargs):
            # Generate cache key from function name and arguments
            key_parts = [key_prefix or func.__name__]
            key_parts.extend([str(arg) for arg in args])
            key_parts.extend([f"{k}:{v}" for k, v in sorted(kwargs.items())])
            
            cache_key = hashlib.md5(":".join(key_parts).encode()).hexdigest()
            
            # Try to get from cache
            result = await cache_manager.get(cache_key)
            if result is not None:
                return result
            
            # Execute function and cache result
            result = await func(*args, **kwargs)
            await cache_manager.set(cache_key, result, ttl, tags)
            
            return result
        
        def sync_wrapper(*args, **kwargs):
            return asyncio.run(async_wrapper(*args, **kwargs))
        
        return async_wrapper if asyncio.iscoroutinefunction(func) else sync_wrapper
    
    return decorator
