package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TileTheme
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Modern drawer sidebar for Game Settings, Volume Control, Toggles, and Google In-App Purchases.
 */
@Composable
fun SettingsSidebar(
    isOpen: Boolean,
    onClose: () -> Unit,
    isSoundOn: Boolean,
    onToggleSound: (Boolean) -> Unit,
    volumePercent: Int,
    onVolumeChanged: (Int) -> Unit,
    isVibrationOn: Boolean,
    onToggleVibration: (Boolean) -> Unit,
    isAnimationsOn: Boolean,
    onToggleAnimations: (Boolean) -> Unit,
    isHapticsOn: Boolean,
    onToggleHaptics: (Boolean) -> Unit,
    selectedTheme: TileTheme,
    onSelectTheme: (TileTheme) -> Unit,
    isAdsRemoved: Boolean,
    onPurchaseRemoveAds: () -> Unit,
    onPurchaseProPack: () -> Unit,
    onRestorePurchases: () -> Unit,
    onOpenHowToPlay: () -> Unit,
    billingMessage: String,
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
        // Drawer Card
        Surface(
            modifier = Modifier
                .fillMaxHeight()
                .width(320.dp)
                .align(Alignment.CenterEnd)
                .clickable(enabled = false) { /* Stop propagation */ }
                .testTag("settings_sidebar_panel"),
            color = SurfaceDark,
            shadowElevation = 24.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Game Settings",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Audio, controls & purchases",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(SurfaceCardElevated)
                            .border(1.dp, SurfaceBorder, CircleShape)
                            .testTag("close_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Settings",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 1: Audio & Volume
                SettingsSectionHeader(title = "AUDIO & VOLUME")

                Spacer(modifier = Modifier.height(10.dp))

                // Sound On/Off Switch
                SettingsToggleRow(
                    title = "Game Sound Effects",
                    subtitle = "Tile sliding clicks and chimes",
                    icon = if (isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                    checked = isSoundOn,
                    onCheckedChange = onToggleSound
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Volume Slider (0 - 100%)
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (volumePercent > 50) Icons.Default.VolumeUp else Icons.Default.VolumeDown,
                                contentDescription = "Volume",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sound Volume",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "$volumePercent%",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                    }

                    Slider(
                        value = volumePercent.toFloat(),
                        onValueChange = { onVolumeChanged(it.toInt()) },
                        valueRange = 0f..100f,
                        enabled = isSoundOn,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = SurfaceBorder
                        ),
                        modifier = Modifier.testTag("volume_slider")
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 2: Haptics & Animations
                SettingsSectionHeader(title = "HAPTICS & PERFORMANCE")

                Spacer(modifier = Modifier.height(10.dp))

                SettingsToggleRow(
                    title = "Vibration & Haptics",
                    subtitle = "Tactile feedback when tiles snap",
                    icon = Icons.Default.Vibration,
                    checked = isVibrationOn,
                    onCheckedChange = onToggleVibration
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    title = "Smooth Animations",
                    subtitle = "Fluids neon tile translation physics",
                    icon = Icons.Default.Animation,
                    checked = isAnimationsOn,
                    onCheckedChange = onToggleAnimations
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Section 3: Recommended Store & In-App Purchases
                SettingsSectionHeader(title = "RECOMMENDED & STORE")

                Spacer(modifier = Modifier.height(10.dp))

                // Remove Ads Card
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(
                            1.5.dp,
                            if (isAdsRemoved) Brush.linearGradient(listOf(NeonEmerald, NeonCyan))
                            else Brush.linearGradient(listOf(GoldTrophy, NeonIndigo)),
                            RoundedCornerShape(16.dp)
                        ),
                    color = SurfaceCard,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Paid,
                                    contentDescription = "Store",
                                    tint = GoldTrophy,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isAdsRemoved) "Ads Removed (VIP)" else "Remove All Ads",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                            if (isAdsRemoved) {
                                Text(
                                    text = "ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = NeonEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isAdsRemoved) "You have an ad-free premium experience!"
                            else "Permanently removes all banners, interstitial popups, and ad delays.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        if (!isAdsRemoved) {
                            ElevatedButton(
                                onClick = onPurchaseRemoveAds,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(38.dp)
                                    .testTag("purchase_remove_ads_button"),
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = GoldTrophy,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text(
                                    text = "Unlock Ad-Free ($1.99)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pro Solver Pack
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp)),
                    color = SurfaceCard
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Pro Solver Pass",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Unlimited instant puzzle hints",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        OutlinedButton(
                            onClick = onPurchaseProPack,
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("purchase_pro_pack_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "$0.99",
                                color = NeonCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Restore Purchases
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onRestorePurchases() }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Restore",
                        tint = TextMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Restore Previous Purchases",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 4: Visual Themes
                SettingsSectionHeader(title = "PUZZLE THEMES")

                Spacer(modifier = Modifier.height(10.dp))

                TileTheme.entries.forEach { theme ->
                    val isSelected = theme == selectedTheme
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonIndigo.copy(alpha = 0.2f) else SurfaceCard)
                            .border(
                                1.dp,
                                if (isSelected) NeonCyan else SurfaceBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectTheme(theme) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = theme.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) NeonCyan else TextPrimary
                        )

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Active",
                                tint = NeonCyan,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 5: Help & About
                SettingsSectionHeader(title = "SUPPORT & GUIDE")

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(SurfaceCard)
                        .clickable { onOpenHowToPlay() }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = "Help",
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "How to Play & Winning Tactics",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Status message
                if (billingMessage.isNotEmpty()) {
                    Text(
                        text = billingMessage,
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
        color = NeonIndigo,
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
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) NeonCyan else TextMuted,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = NeonCyan,
                checkedTrackColor = NeonIndigo,
                uncheckedThumbColor = TextMuted,
                uncheckedTrackColor = SurfaceBorder
            )
        )
    }
}
