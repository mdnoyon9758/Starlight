package com.barcodereader.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness6
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.barcodereader.R
import com.barcodereader.config.BrandingConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val themeMode by viewModel.themeMode.collectAsState()
    val scanSound by viewModel.scanSound.collectAsState()
    val hapticFeedback by viewModel.hapticFeedback.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Appearance Section
            SettingsSection(title = stringResource(R.string.settings_appearance)) {
                SettingsSwitchItem(
                    icon = Icons.Filled.Brightness6,
                    title = stringResource(R.string.settings_theme),
                    subtitle = when (themeMode) {
                        "LIGHT" -> stringResource(R.string.settings_theme_light)
                        "DARK" -> stringResource(R.string.settings_theme_dark)
                        else -> stringResource(R.string.settings_theme_system)
                    },
                    checked = false,
                    onCheckedChange = { /* Handle theme change */ }
                )
            }

            // Scanner Section
            SettingsSection(title = stringResource(R.string.settings_scanner)) {
                SettingsSwitchItem(
                    icon = Icons.Filled.Notifications,
                    title = stringResource(R.string.settings_scan_sound),
                    subtitle = if (scanSound) "On" else "Off",
                    checked = scanSound,
                    onCheckedChange = { viewModel.setScanSound(it) }
                )
                SettingsSwitchItem(
                    icon = Icons.Filled.Fingerprint,
                    title = stringResource(R.string.settings_haptic_feedback),
                    subtitle = if (hapticFeedback) "On" else "Off",
                    checked = hapticFeedback,
                    onCheckedChange = { viewModel.setHapticFeedback(it) }
                )
            }

            // Security Section
            SettingsSection(title = stringResource(R.string.settings_security)) {
                SettingsNavItem(
                    icon = Icons.Filled.Lock,
                    title = stringResource(R.string.settings_app_lock),
                    subtitle = stringResource(R.string.settings_lock_type),
                    onClick = { /* Navigate to security settings */ }
                )
            }

            // About Section
            SettingsSection(title = stringResource(R.string.settings_about)) {
                SettingsNavItem(
                    icon = Icons.Filled.Star,
                    title = stringResource(R.string.settings_rate_app),
                    subtitle = "",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BrandingConfig.PLAY_STORE_URL))
                        context.startActivity(intent)
                    }
                )
                SettingsNavItem(
                    icon = Icons.Filled.Share,
                    title = stringResource(R.string.settings_share_app),
                    subtitle = "",
                    onClick = {
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, "Check out ${BrandingConfig.APP_NAME}: ${BrandingConfig.SHARE_URL}")
                        }
                        context.startActivity(Intent.createChooser(intent, "Share"))
                    }
                )
                SettingsNavItem(
                    icon = Icons.Filled.PrivacyTip,
                    title = stringResource(R.string.settings_privacy_policy),
                    subtitle = "",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BrandingConfig.PRIVACY_POLICY_URL))
                        context.startActivity(intent)
                    }
                )
                SettingsNavItem(
                    icon = Icons.Filled.OpenInBrowser,
                    title = stringResource(R.string.settings_terms),
                    subtitle = "",
                    onClick = {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BrandingConfig.TERMS_URL))
                        context.startActivity(intent)
                    }
                )
            }

            // Version
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "${stringResource(R.string.settings_version)} ${BrandingConfig.APP_VERSION}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}

@Composable
fun SettingsNavItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun SettingsSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle.isNotBlank()) {
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.primary,
                checkedTrackColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}