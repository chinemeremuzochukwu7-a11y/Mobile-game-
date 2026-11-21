package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Animation
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TileTheme
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Settings sidebar for Audio, Volume Control (horizontal line with recommended setting),
 * Zen Mode, Board Material Themes, Vibration, Animations, and Free Premium Edition info.
 */
@Composable
fun SettingsSidebar(
    isOpen: Boolean,
    isSoundOn: Boolean,
    volumePercent: Int,
    isVibrationOn: Boolean,
    isAnimationsOn: Boolean,
    isZenMode: Boolean,
    selectedTheme: TileTheme,
    onToggleSound: (Boolean) -> Unit,
    onVolumeChanged: (Int) -> Unit,
    onToggleVibration: (Boolean) -> Unit,
    onToggleAnimations: (Boolean) -> Unit,
    onToggleZenMode: (Boolean) -> Unit,
    onSelectTheme: (TileTheme) -> Unit,
    onOpenHowToPlay: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!isOpen) return

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepNavy.copy(alpha = 0.75f))
            .clickable { onClose() }
            .testTag("settings_sidebar_backdrop")
    ) {
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.86f)
                .align(Alignment.CenterEnd)
                .clickable(enabled = false) { /* Consume clicks */ },
            color = Color(0xFF381604),
            shadowElevation = 16.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Settings & Premium",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFBE5C5)
                    )

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5A250B))
                            .border(1.dp, Color(0xFF8F4111), CircleShape)
                            .testTag("close_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = Color(0xFFFBE5C5),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 1: Audio & Volume Control
                SettingsSectionHeader(title = "AUDIO & VOLUME")

                Spacer(modifier = Modifier.height(10.dp))

                // Sound On/Off Switch
                SettingsToggleRow(
                    title = "Sound Effects",
                    subtitle = "Tile sliding, victory fanfare, & timer warning",
                    icon = if (isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                    checked = isSoundOn,
                    onCheckedChange = onToggleSound
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Volume Horizontal Line Control with increase/decrease buttons and recommended setting
                VolumeHorizontalLineControl(
                    volumePercent = volumePercent,
                    isSoundOn = isSoundOn,
                    onVolumeChanged = onVolumeChanged
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Premium Board Themes (Unlocked Free)
                SettingsSectionHeader(title = "BOARD MATERIAL THEMES (UNLOCKED)")

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TileTheme.values().forEach { theme ->
                        val isSelected = theme == selectedTheme
                        val chipColor = when (theme) {
                            TileTheme.CLASSIC_WOOD -> Color(0xFFE5943B)
                            TileTheme.DARK_WALNUT -> Color(0xFF8D5524)
                            TileTheme.MINIMALIST_NEON -> NeonCyan
                            TileTheme.NATURE -> NeonEmerald
                            TileTheme.STEEL_INDUSTRIAL -> Color(0xFFCBD5E1)
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (isSelected) Color(0xFF6B2F0B) else Color(0xFF4B1E07)
                                )
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) chipColor else Color(0xFF7A370A),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectTheme(theme) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("theme_chip_${theme.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(chipColor)
                                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape)
                            )
                            Text(
                                text = theme.displayName,
                                color = if (isSelected) Color(0xFFFFF6EA) else Color(0xFFD6A068),
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Gameplay, Zen Mode & Haptics
                SettingsSectionHeader(title = "GAMEPLAY & HAPTICS")

                Spacer(modifier = Modifier.height(10.dp))

                SettingsToggleRow(
                    title = "Zen Mode (Relaxed Play)",
                    subtitle = "No countdown warning beeps — solve at your own pace",
                    icon = Icons.Default.SelfImprovement,
                    checked = isZenMode,
                    onCheckedChange = onToggleZenMode
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsToggleRow(
                    title = "Vibration & Haptics",
                    subtitle = "Tactile feedback when blocks move",
                    icon = Icons.Default.Vibration,
                    checked = isVibrationOn,
                    onCheckedChange = onToggleVibration
                )

                Spacer(modifier = Modifier.height(10.dp))

                SettingsToggleRow(
                    title = "Smooth Animations",
                    subtitle = "High frame rate sliding visuals",
                    icon = Icons.Default.Animation,
                    checked = isAnimationsOn,
                    onCheckedChange = onToggleAnimations
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 4: Play Store Free + Premium Features Unlocked
                SettingsSectionHeader(title = "PLAY STORE FREE EDITION")

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NeonEmerald.copy(alpha = 0.65f), RoundedCornerShape(14.dp)),
                    color = Color(0xFF4B1E07)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NeonEmerald,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Free Game • All Premium Features Open",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFFFBE5C5)
                            )
                        }

                        Text(
                            text = "100% Free with zero ads. Enjoy all 105 levels (3x3 to 6x6), Live Coach horizontal progress line, step-by-step Get Helped arrows, swipe controls, Zen Mode, and 5 board themes!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFD6A068),
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // How to Play button
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, Color(0xFF8F4111), RoundedCornerShape(14.dp))
                        .clickable { onOpenHowToPlay() },
                    color = Color(0xFF4B1E07)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = Color(0xFFE5943B),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Game Rules & How to Play",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFFBE5C5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.ExtraBold,
        color = Color(0xFFE5943B),
        letterSpacing = 1.sp,
        fontSize = 11.sp
    )
}

@Composable
private fun SettingsToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Color(0xFF7A370A), RoundedCornerShape(14.dp)),
        color = Color(0xFF4B1E07)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (checked) Color(0xFF4ADE80) else Color(0xFFA0724B),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFBE5C5)
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD6A068),
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF381604),
                    checkedTrackColor = Color(0xFFE5943B),
                    uncheckedThumbColor = Color(0xFFD6A068),
                    uncheckedTrackColor = Color(0xFF2B1003)
                )
            )
        }
    }
}
