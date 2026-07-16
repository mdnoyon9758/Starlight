package com.barcodereader.ui.history

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.barcodereader.data.HistoryStorage
import com.barcodereader.data.ScanHistory
import com.barcodereader.ui.components.EmptyState
import com.barcodereader.ui.theme.*
import com.barcodereader.util.ExportUtils
import com.barcodereader.util.FormatUtils

private fun getTypeColor(type: String): Color {
    return when (type.uppercase()) {
        "URL" -> UrlColor
        "EMAIL" -> EmailColor
        "PHONE" -> PhoneColor
        "WIFI" -> WiFiColor
        "PRODUCT" -> GradientGreen
        "LOCATION" -> LocationColor
        else -> GradientPurple
    }
}

private fun getTypeIcon(type: String): ImageVector {
    return when (type.uppercase()) {
        "URL" -> Icons.Filled.OpenInBrowser
        "EMAIL" -> Icons.Filled.Email
        "PHONE" -> Icons.Filled.Phone
        "WIFI" -> Icons.Filled.Wifi
        else -> Icons.Filled.QrCode
    }
}

private fun getTypeLabel(type: String): String {
    return when (type.uppercase()) {
        "URL" -> "Website"
        "EMAIL" -> "Email"
        "PHONE" -> "Phone"
        "WIFI" -> "WiFi"
        "PRODUCT" -> "Product"
        "LOCATION" -> "Location"
        "VCARD" -> "Contact"
        else -> "Text"
    }
}

@Composable
fun HistoryScreen(
    storage: HistoryStorage,
    onNavigateToScan: () -> Unit
) {
    var entries by remember { mutableStateOf(storage.getAllEntries()) }
    var showDeleteConfirm by remember { mutableStateOf<ScanHistory?>(null) }
    var showExportMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Scan History", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            }

            // Export button
            if (entries.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterEnd) {
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(
                                Icons.Filled.Share,
                                contentDescription = "Export",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        DropdownMenu(expanded = showExportMenu, onDismissRequest = { showExportMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Export as CSV") },
                                onClick = {
                                    showExportMenu = false
                                    ExportUtils.exportToCsv(context, entries)?.let { uri ->
                                        ExportUtils.shareExport(context, uri, ExportUtils.ExportFormat.CSV)
                                    }
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Export as JSON") },
                                onClick = {
                                    showExportMenu = false
                                    ExportUtils.exportToJson(context, entries)?.let { uri ->
                                        ExportUtils.shareExport(context, uri, ExportUtils.ExportFormat.JSON)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            if (entries.isEmpty()) {
                EmptyState(
                    icon = com.barcodereader.R.drawable.ic_history_empty,
                    title = "No Scans Yet",
                    subtitle = "Your scan history will appear here",
                    actionLabel = "Start Scanning",
                    onAction = onNavigateToScan
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(entries) { entry ->
                        HistoryItem(
                            entry = entry,
                            onCopy = { FormatUtils.copyToClipboard(context, entry.content) },
                            onShare = { FormatUtils.shareText(context, entry.content) },
                            onDelete = { showDeleteConfirm = entry },
                            onFavoriteToggle = {
                                storage.toggleFavorite(entry.id)
                                entries = storage.getAllEntries()
                            },
                            onAction = {
                                when (entry.type.uppercase()) {
                                    "URL" -> {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(entry.content))
                                        context.startActivity(intent)
                                    }
                                    "PHONE" -> {
                                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${entry.content}"))
                                        context.startActivity(intent)
                                    }
                                    "EMAIL" -> {
                                        val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${entry.content}"))
                                        context.startActivity(intent)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    showDeleteConfirm?.let { entry ->
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = null },
            title = { Text("Delete Scan") },
            text = { Text("Are you sure you want to delete this scan?") },
            confirmButton = {
                Button(
                    onClick = {
                        storage.deleteEntry(entry.id)
                        entries = storage.getAllEntries()
                        showDeleteConfirm = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Error),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = null }, shape = RoundedCornerShape(12.dp)) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HistoryItem(
    entry: ScanHistory,
    onCopy: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onAction: () -> Unit
) {
    val typeColor = getTypeColor(entry.type)
    val typeIcon = getTypeIcon(entry.type)
    val typeLabel = getTypeLabel(entry.type)
    val hasAction = entry.type.uppercase() in listOf("URL", "PHONE", "EMAIL")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Top row: type badge + favorite + delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type badge
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(typeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(typeIcon, null, modifier = Modifier.size(14.dp), tint = typeColor)
                    Text(typeLabel, style = MaterialTheme.typography.labelSmall, color = typeColor, fontWeight = FontWeight.Medium)
                }
                
                Row {
                    IconButton(onClick = onFavoriteToggle, modifier = Modifier.size(36.dp)) {
                        Icon(
                            if (entry.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = if (entry.isFavorite) Error else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Filled.Delete, contentDescription = "Delete", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Content
            Text(
                entry.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Date
            Text(
                entry.formattedDate,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (hasAction) {
                    Button(
                        onClick = onAction,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = typeColor),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(typeIcon, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            when (entry.type.uppercase()) {
                                "URL" -> "Open"
                                "PHONE" -> "Call"
                                "EMAIL" -> "Send"
                                else -> "Open"
                            },
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
                
                IconButton(onClick = onCopy, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                    Icon(Icons.Filled.ContentCopy, "Copy", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface)
                }
                
                IconButton(onClick = onShare, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.surfaceVariant)) {
                    Icon(Icons.Filled.Share, "Share", modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
