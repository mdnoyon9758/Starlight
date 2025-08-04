"""Profiler for monitoring performance timing."""

from contextlib import ContextDecorator
import time

class Profiler(ContextDecorator):
    def __init__(self, name: str, logger=None):
        self.name = name
        self.logger = logger

    def __enter__(self):
        self.start = time.time()
        return self

    def __exit__(self, *exc):
        end = time.time()
        duration = end - self.start
        message = f"[PROFILER] {self.name}: {duration:.4f} seconds"
        
        if self.logger:
            self.logger.info(message)
        else:
            print(message)


# Decorator usage:
# with Profiler("database_query"):
#     execute_your_query()
