package com.barcodereader.ui.history

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.outlined.OpenInBrowser
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barcodereader.R
import com.barcodereader.data.HistoryStorage
import com.barcodereader.data.ScanHistory
import com.barcodereader.ui.components.EmptyState
import com.barcodereader.util.ExportUtils
import com.barcodereader.util.FormatUtils

@Composable
fun HistoryScreen(
    storage: HistoryStorage,
    onNavigateToScan: () -> Unit
) {
    var entries by remember { mutableStateOf(storage.getAllEntries()) }
    var showDeleteConfirm by remember { mutableStateOf<ScanHistory?>(null) }
    var showExportMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with export button
            Box(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("Scan History", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }

            // Export button (only show if there are entries)
            if (entries.isNotEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Box {
                        IconButton(onClick = { showExportMenu = true }) {
                            Icon(
                                Icons.Filled.FileDownload,
                                contentDescription = "Export History",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        DropdownMenu(
                            expanded = showExportMenu,
                            onDismissRequest = { showExportMenu = false }
                        ) {
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
                    icon = R.drawable.ic_history_empty,
                    title = "No Scans Yet",
                    subtitle = "Your scan history will appear here",
                    actionLabel = "Start Scanning",
                    onAction = onNavigateToScan
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
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
                                when (entry.displayType) {
                                    "URL" -> FormatUtils.openInBrowser(context, entry.content)
                                    else -> {}
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirm = null }) {
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
    val context = LocalContext.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    entry.formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                
                Row {
                    IconButton(onClick = onFavoriteToggle) {
                        Icon(
                            if (entry.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (entry.isFavorite) "Remove from favorites" else "Add to favorites",
                            tint = if (entry.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    IconButton(onClick = onDelete) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                entry.content,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    IconButton(onClick = onCopy) {
                        Icon(Icons.Filled.ContentCopy, "Copy")
                    }
                    
                    IconButton(onClick = onShare) {
                        Icon(Icons.Filled.Share, "Share")
                    }
                    
                    if (entry.displayType == "URL") {
                        IconButton(onClick = onAction) {
                            Icon(Icons.Outlined.OpenInBrowser, "Open in Browser")
                        }
                    }
                }
                
                Text(
                    entry.displayType,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
