package com.barcodereader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import com.barcodereader.R

@Composable
fun ScanCorner(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    size: Dp = 24.dp,
    strokeWidth: Dp = 4.dp
) {
    Box(
        modifier = modifier
            .size(size)
    ) {
        // Top left lines
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(strokeWidth, size / 3f)
                .background(color)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .size(size / 3f, strokeWidth)
                .background(color)
        )
        
        // Top right lines
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(strokeWidth, size / 3f)
                .background(color)
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(size / 3f, strokeWidth)
                .background(color)
        )
        
        // Bottom left lines
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(strokeWidth, size / 3f)
                .background(color)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .size(size / 3f, strokeWidth)
                .background(color)
        )
        
        // Bottom right lines
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(strokeWidth, size / 3f)
                .background(color)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(size / 3f, strokeWidth)
                .background(color)
        )
    }
}

@Composable
fun ScanLine(
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    height: Dp = 2.dp
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(color)
    )
}

@Composable
fun ScanOverlay(
    modifier: Modifier = Modifier,
    cornerColor: Color = Color.White,
    lineColor: Color = Color.White,
    cornerSize: Dp = 24.dp,
    cornerStroke: Dp = 4.dp,
    lineHeight: Dp = 2.dp,
    scanAreaSize: Dp = 220.dp
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
    ) {
        // Center scan area
        Box(
            modifier = Modifier
                .size(scanAreaSize)
                .align(Alignment.Center)
        ) {
            // Four corners
            ScanCorner(
                modifier = Modifier.align(Alignment.TopStart),
                color = cornerColor,
                size = cornerSize,
                strokeWidth = cornerStroke
            )
            ScanCorner(
                modifier = Modifier.align(Alignment.TopEnd),
                color = cornerColor,
                size = cornerSize,
                strokeWidth = cornerStroke
            )
            ScanCorner(
                modifier = Modifier.align(Alignment.BottomStart),
                color = cornerColor,
                size = cornerSize,
                strokeWidth = cornerStroke
            )
            ScanCorner(
                modifier = Modifier.align(Alignment.BottomEnd),
                color = cornerColor,
                size = cornerSize,
                strokeWidth = cornerStroke
            )
            // Animated scan line
            ScanLine(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                color = lineColor,
                height = lineHeight
            )
        }
    }
}

@Composable
fun ResultCard(
    title: String,
    content: String,
    type: String,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onAction: (() -> Unit)? = null,
    actionLabel: String = "",
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Text(
                content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 5,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onCopy) {
                    Icon(Icons.Filled.ContentCopy, "Copy")
                }
                IconButton(onClick = onShare) {
                    Icon(Icons.Filled.Share, "Share")
                }
                if (onAction != null && actionLabel.isNotBlank()) {
                    Spacer(modifier = Modifier.weight(1f))
                    OutlinedButton(onClick = onAction) {
                        Text(actionLabel)
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState(
    icon: Int,
    title: String,
    subtitle: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
        if (actionLabel != null && onAction != null) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onAction,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(actionLabel)
            }
        }
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}