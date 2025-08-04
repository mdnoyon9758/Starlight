"""Advanced caching utilities."""

import functools
import hashlib
import json
from typing import Any, Callable, Optional
from datetime import timedelta

from app.core.cache.redis import cache


def cache_key_generator(*args, **kwargs) -> str:
    """Generate a cache key from function arguments."""
    key_data = json.dumps({"args": args, "kwargs": kwargs}, sort_keys=True, default=str)
    return hashlib.md5(key_data.encode()).hexdigest()


def cached(
    ttl: int = 3600,
    key_prefix: str = "",
    skip_if: Optional[Callable] = None
):
    """
    Decorator for caching function results.
    
    Args:
        ttl: Time to live in seconds
        key_prefix: Prefix for cache keys
        skip_if: Function that returns True if caching should be skipped
    """
    def decorator(func: Callable) -> Callable:
        @functools.wraps(func)
        async def wrapper(*args, **kwargs):
            # Skip caching if condition is met
            if skip_if and skip_if(*args, **kwargs):
                return await func(*args, **kwargs)
            
            # Generate cache key
            func_key = f"{func.__module__}.{func.__name__}"
            arg_key = cache_key_generator(*args, **kwargs)
            cache_key = f"{key_prefix}:{func_key}:{arg_key}" if key_prefix else f"{func_key}:{arg_key}"
            
            # Try to get from cache
            cached_result = await cache.get(cache_key)
            if cached_result is not None:
                return json.loads(cached_result)
            
            # Execute function and cache result
            result = await func(*args, **kwargs)
            await cache.set(cache_key, json.dumps(result, default=str), ttl)
            
            return result
        return wrapper
    return decorator


class CacheManager:
    """Advanced cache management utilities."""
    
    @staticmethod
    async def invalidate_pattern(pattern: str) -> int:
        """Invalidate all cache keys matching a pattern."""
        if not cache.redis:
            return 0
            
        keys = []
        async for key in cache.redis.scan_iter(match=pattern):
            keys.append(key)
        
        if keys:
            return await cache.redis.delete(*keys)
        return 0
    
    @staticmethod
    async def get_cache_stats() -> dict:
        """Get cache statistics."""
        if not cache.redis:
            return {}
            
        info = await cache.redis.info()
        return {
            "used_memory": info.get("used_memory_human", "0"),
            "keyspace_hits": info.get("keyspace_hits", 0),
            "keyspace_misses": info.get("keyspace_misses", 0),
            "connected_clients": info.get("connected_clients", 0),
        }
    
    @staticmethod
    async def clear_all_cache() -> bool:
        """Clear all cache data."""
        if not cache.redis:
            return False
            
        await cache.redis.flushall()
        return True
