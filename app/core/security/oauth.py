"""OAuth2 integration."""

from typing import Optional

from authlib.integrations.starlette_client import OAuth
from fastapi import HTTPException, status

from app.core.config.settings import get_settings
from app.core.logging.logger import get_logger

settings = get_settings()
logger = get_logger(__name__)

# Initialize OAuth
oauth = OAuth()

# Configure OAuth providers
if settings.google_client_id and settings.google_client_secret:
    oauth.register(
        name='google',
        client_id=settings.google_client_id,
        client_secret=settings.google_client_secret,
        server_metadata_url='https://accounts.google.com/.well-known/openid_configuration',
        client_kwargs={
            'scope': 'openid email profile'
        }
    )

if settings.github_client_id and settings.github_client_secret:
    oauth.register(
        name='github',
        client_id=settings.github_client_id,
        client_secret=settings.github_client_secret,
        access_token_url='https://github.com/login/oauth/access_token',
        authorize_url='https://github.com/login/oauth/authorize',
        api_base_url='https://api.github.com/',
        client_kwargs={'scope': 'user:email'},
    )

if settings.twitter_client_id and settings.twitter_client_secret:
    oauth.register(
        name='twitter',
        client_id=settings.twitter_client_id,
        client_secret=settings.twitter_client_secret,
        request_token_params={'scope': 'read:user'},
        base_url='https://api.twitter.com/2/',
        request_token_url='https://api.twitter.com/oauth/request_token',
        access_token_method='POST',
        access_token_url='https://api.twitter.com/oauth/access_token',
        authorize_url='https://api.twitter.com/oauth/authenticate'
    )

if settings.linkedin_client_id and settings.linkedin_client_secret:
    oauth.register(
        name='linkedin',
        client_id=settings.linkedin_client_id,
        client_secret=settings.linkedin_client_secret,
        access_token_url='https://www.linkedin.com/oauth/v2/accessToken',
        authorize_url='https://www.linkedin.com/oauth/v2/authorization',
        api_base_url='https://api.linkedin.com/v2/',
        client_kwargs={'scope': 'r_liteprofile r_emailaddress'}
    )


class OAuthProvider:
    """OAuth provider interface."""
    
    def __init__(self, provider_name: str):
        self.provider_name = provider_name
        self.client = getattr(oauth, provider_name, None)
        
        if not self.client:
            raise ValueError(f"OAuth provider {provider_name} not configured")
    
    async def get_user_info(self, token: dict) -> Optional[dict]:
        """Get user information from OAuth provider."""
        try:
            if self.provider_name == 'google':
                user_info = await self.client.parse_id_token(token)
                return {
                    'id': user_info['sub'],
                    'email': user_info['email'],
                    'name': user_info['name'],
                    'picture': user_info.get('picture')
                }
            
            elif self.provider_name == 'github':
                resp = await self.client.get('user', token=token)
                user_data = resp.json()
                return {
                    'id': str(user_data['id']),
                    'email': user_data.get('email'),
                    'name': user_data.get('name') or user_data.get('login'),
                    'picture': user_data.get('avatar_url')
                }
            
            return None
            
        except Exception as e:
            logger.error(f"Error getting user info from {self.provider_name}", error=str(e))
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Error getting user info from {self.provider_name}"
            )
