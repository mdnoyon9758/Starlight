"""OAuth endpoints."""

from typing import Optional
from fastapi import APIRouter, Depends, HTTPException, Request, status
from fastapi.responses import RedirectResponse
from sqlalchemy.ext.asyncio import AsyncSession

from app.core.config.settings import get_settings
from app.core.security.auth import create_access_token
from app.core.security.oauth import oauth, OAuthProvider
from app.infrastructure.database import get_db
from app.infrastructure.database.models import User
from app.api.v1.dependencies.auth import get_current_user
from app.schemas.response.user import UserResponse

router = APIRouter()
settings = get_settings()


@router.get("/login/{provider}")
async def oauth_login(request: Request, provider: str):
    """Initiate OAuth login flow."""
    if provider not in ["google", "github"]:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Unsupported OAuth provider"
        )
    
    client = getattr(oauth, provider, None)
    if not client:
        raise HTTPException(
            status_code=status.HTTP_501_NOT_IMPLEMENTED,
            detail=f"OAuth provider {provider} not configured"
        )
    
    redirect_uri = request.url_for('oauth_callback', provider=provider)
    return await client.authorize_redirect(request, redirect_uri)


@router.get("/callback/{provider}")
async def oauth_callback(
    request: Request, 
    provider: str,
    db: AsyncSession = Depends(get_db)
):
    """Handle OAuth callback."""
    if provider not in ["google", "github"]:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Unsupported OAuth provider"
        )
    
    try:
        oauth_provider = OAuthProvider(provider)
        client = oauth_provider.client
        
        # Get token from callback
        token = await client.authorize_access_token(request)
        
        # Get user info from provider
        user_info = await oauth_provider.get_user_info(token)
        
        if not user_info or not user_info.get('email'):
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Unable to get user information from OAuth provider"
            )
        
        # Check if user exists
        result = await db.execute(
            "SELECT * FROM users WHERE email = :email",
            {"email": user_info['email']}
        )
        user = result.fetchone()
        
        if not user:
            # Create new user
            new_user = User(
                username=user_info['email'],  # Use email as username
                email=user_info['email'],
                full_name=user_info.get('name'),
                is_active=True,
                oauth_provider=provider,
                oauth_id=user_info['id']
            )
            db.add(new_user)
            await db.commit()
            await db.refresh(new_user)
            user = new_user
        else:
            # Update OAuth info if not set
            if not user.oauth_provider:
                user.oauth_provider = provider
                user.oauth_id = user_info['id']
                await db.commit()
        
        # Create access token
        access_token = create_access_token(subject=user.username)
        
        # Redirect to frontend with token (adjust URL as needed)
        frontend_url = f"{settings.backend_cors_origins[0]}/auth/callback"
        return RedirectResponse(
            url=f"{frontend_url}?token={access_token}&provider={provider}"
        )
    
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"OAuth authentication failed: {str(e)}"
        )


@router.post("/link/{provider}")
async def link_oauth_account(
    provider: str,
    request: Request,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Link OAuth account to existing user."""
    if provider not in ["google", "github"]:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Unsupported OAuth provider"
        )
    
    try:
        oauth_provider = OAuthProvider(provider)
        client = oauth_provider.client
        
        # Get token from request (this would typically come from frontend)
        # For now, we'll assume the token is passed in the request
        token = await client.authorize_access_token(request)
        user_info = await oauth_provider.get_user_info(token)
        
        if not user_info:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="Unable to get user information from OAuth provider"
            )
        
        # Check if OAuth account is already linked to another user
        result = await db.execute(
            "SELECT * FROM users WHERE oauth_provider = :provider AND oauth_id = :oauth_id",
            {"provider": provider, "oauth_id": user_info['id']}
        )
        existing_user = result.fetchone()
        
        if existing_user and existing_user.id != current_user.id:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail="OAuth account already linked to another user"
            )
        
        # Link OAuth account to current user
        current_user.oauth_provider = provider
        current_user.oauth_id = user_info['id']
        await db.commit()
        
        return {"message": f"Successfully linked {provider} account"}
    
    except Exception as e:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=f"Failed to link OAuth account: {str(e)}"
        )


@router.delete("/unlink/{provider}")
async def unlink_oauth_account(
    provider: str,
    current_user: User = Depends(get_current_user),
    db: AsyncSession = Depends(get_db)
):
    """Unlink OAuth account from user."""
    if current_user.oauth_provider != provider:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="OAuth provider not linked to this account"
        )
    
    current_user.oauth_provider = None
    current_user.oauth_id = None
    await db.commit()
    
    return {"message": f"Successfully unlinked {provider} account"}
