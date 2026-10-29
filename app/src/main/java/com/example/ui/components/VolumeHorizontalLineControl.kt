package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.game.SoundEffectManager
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Horizontal line for volume increase and decrease with recommended setting marker and quick snap.
 */
@Composable
fun VolumeHorizontalLineControl(
    volumePercent: Int,
    isSoundOn: Boolean,
    onVolumeChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val recommended = SoundEffectManager.RECOMMENDED_VOLUME_PERCENT
    val isAtRecommended = volumePercent == recommended

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title row with volume readout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = when {
                        !isSoundOn || volumePercent == 0 -> Icons.Default.VolumeMute
                        volumePercent < 50 -> Icons.Default.VolumeDown
                        else -> Icons.Default.VolumeUp
                    },
                    contentDescription = "Volume Icon",
                    tint = if (isSoundOn) NeonCyan else TextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = "Sound Volume",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isSoundOn) TextPrimary else TextMuted
                    )
                    Text(
                        text = if (isAtRecommended) "Optimal Recommended Balance" else "Custom Audio Level",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAtRecommended) NeonEmerald else TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            // Current percentage badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSoundOn) NeonCyan.copy(alpha = 0.15f) else SurfaceCardElevated)
                    .border(1.dp, if (isSoundOn) NeonCyan.copy(alpha = 0.5f) else SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$volumePercent%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSoundOn) NeonCyan else TextMuted
                )
            }
        }

        // Horizontal Line Slider Row: [Decrease -] ====== [Line & Recommended Marker] ====== [+ Increase]
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Decrease Button (-)
            IconButton(
                onClick = {
                    if (isSoundOn) {
                        onVolumeChanged((volumePercent - 10).coerceAtLeast(0))
                    }
                },
                enabled = isSoundOn && volumePercent > 0,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardElevated)
                    .border(1.dp, SurfaceBorder, CircleShape)
                    .testTag("volume_decrease_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Decrease Volume",
                    tint = if (isSoundOn && volumePercent > 0) NeonCyan else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Interactive Horizontal Line with Recommended Notch
            BoxWithConstraints(
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp),
                contentAlignment = Alignment.Center
            ) {
                val totalWidth = maxWidth

                // Track and Gesture Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(SurfaceBorder.copy(alpha = 0.7f))
                        .pointerInput(isSoundOn) {
                            if (!isSoundOn) return@pointerInput
                            detectTapGestures { offset ->
                                val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                                onVolumeChanged((fraction * 100).toInt())
                            }
                        }
                        .pointerInput(isSoundOn) {
                            if (!isSoundOn) return@pointerInput
                            detectDragGestures { change, _ ->
                                change.consume()
                                val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                                onVolumeChanged((fraction * 100).toInt())
                            }
                        }
                        .testTag("volume_horizontal_line")
                ) {
                    // Active filled portion of horizontal line
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(fraction = (volumePercent / 100f).coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(5.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(NeonIndigo, NeonCyan)
                                )
                            )
                    )

                    // Vertical tick/notch for RECOMMENDED setting (80%)
                    val recommendedFraction = recommended / 100f
                    Box(
                        modifier = Modifier
                            .offset(x = totalWidth * recommendedFraction - 2.dp)
                            .width(4.dp)
                            .fillMaxHeight()
                            .background(NeonAmber)
                    )
                }

                // Recommended Indicator Pin / Thumb on top of the notch
                val recommendedFraction = recommended / 100f
                Box(
                    modifier = Modifier
                        .offset(x = totalWidth * recommendedFraction - 7.dp)
                        .size(14.dp)
                        .clip(CircleShape)
                        .background(NeonAmber)
                        .border(1.5.dp, Color.White, CircleShape)
                        .clickable(enabled = isSoundOn) {
                            onVolumeChanged(recommended)
                        }
                )
            }

            // Increase Button (+)
            IconButton(
                onClick = {
                    if (isSoundOn) {
                        onVolumeChanged((volumePercent + 10).coerceAtMost(100))
                    }
                },
                enabled = isSoundOn && volumePercent < 100,
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(SurfaceCardElevated)
                    .border(1.dp, SurfaceBorder, CircleShape)
                    .testTag("volume_increase_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Increase Volume",
                    tint = if (isSoundOn && volumePercent < 100) NeonCyan else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Recommended Setting Quick Snap & Legend
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonAmber)
                )
                Text(
                    text = "Recommended: $recommended%",
                    style = MaterialTheme.typography.labelSmall,
                    color = NeonAmber,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp
                )
            }

            // One-tap reset to recommended button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isAtRecommended) NeonEmerald.copy(alpha = 0.2f) else SurfaceCardElevated)
                    .border(
                        1.dp,
                        if (isAtRecommended) NeonEmerald else SurfaceBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable(enabled = isSoundOn && !isAtRecommended) {
                        onVolumeChanged(recommended)
                    }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .testTag("recommended_volume_chip")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = if (isAtRecommended) Icons.Default.CheckCircle else Icons.Default.Star,
                        contentDescription = null,
                        tint = if (isAtRecommended) NeonEmerald else NeonAmber,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = if (isAtRecommended) "Recommended Active" else "Set Recommended ($recommended%)",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAtRecommended) NeonEmerald else TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
