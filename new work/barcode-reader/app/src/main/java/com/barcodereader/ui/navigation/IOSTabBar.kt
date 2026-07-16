package com.barcodereader.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.barcodereader.ui.theme.TabBarBackground
import com.barcodereader.ui.theme.TabBarBackgroundDark
import com.barcodereader.ui.theme.TabBarSelected
import com.barcodereader.ui.theme.TabBarUnselected

sealed class TabItem(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    data object Scan : TabItem(
        route = "scan",
        title = "Scan",
        selectedIcon = Icons.Filled.CameraAlt,
        unselectedIcon = Icons.Outlined.CameraAlt
    )
    data object History : TabItem(
        route = "history",
        title = "History",
        selectedIcon = Icons.Filled.History,
        unselectedIcon = Icons.Outlined.History
    )
    data object Generate : TabItem(
        route = "generate",
        title = "Generate",
        selectedIcon = Icons.Filled.QrCode,
        unselectedIcon = Icons.Outlined.QrCode
    )
}

@Composable
fun IOSTabBar(
    tabs: List<TabItem>,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    isDarkTheme: Boolean = false
) {
    val backgroundColor = if (isDarkTheme) TabBarBackgroundDark else TabBarBackground
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .border(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isSelected = selectedTab == tab.route
                val color = if (isSelected) TabBarSelected else TabBarUnselected
                
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab.route) }
                        .padding(vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                        contentDescription = tab.title,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = tab.title,
                        color = color,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
