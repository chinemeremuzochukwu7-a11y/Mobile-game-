package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun GameControls(
    canUndo: Boolean,
    tileTheme: TileTheme,
    isPeeking: Boolean,
    onNewGame: () -> Unit,
    onUndo: () -> Unit,
    onHint: () -> Unit,
    onTogglePeek: () -> Unit,
    onCycleTheme: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Main action row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // New Game / Shuffle Button
            Button(
                onClick = onNewGame,
                modifier = Modifier
                    .weight(1.3f)
                    .height(48.dp)
                    .testTag("new_game_button"),
                colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "New Game",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            // Undo Button
            ControlButton(
                icon = Icons.Default.Undo,
                label = "Undo",
                enabled = canUndo,
                onClick = onUndo,
                modifier = Modifier.weight(1f),
                testTag = "undo_button"
            )

            // Hint Button
            ControlButton(
                icon = Icons.Default.Lightbulb,
                label = "Hint",
                enabled = true,
                onClick = onHint,
                tint = NeonAmber,
                modifier = Modifier.weight(1f),
                testTag = "hint_button"
            )
        }

        // Secondary controls: Target Peek & Theme switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Peek Target Button
            FilledTonalButton(
                onClick = onTogglePeek,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = if (isPeeking) NeonCyan.copy(alpha = 0.25f) else SurfaceCardElevated,
                    contentColor = if (isPeeking) NeonCyan else TextSecondary
                ),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("peek_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = "Peek Target",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isPeeking) "Hide Solved" else "Peek Solved",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Theme Switcher Button
            FilledTonalButton(
                onClick = onCycleTheme,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = SurfaceCardElevated,
                    contentColor = TextSecondary
                ),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("theme_switcher_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = "Change Theme",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tileTheme.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ControlButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = TextPrimary,
    testTag: String
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = SurfaceCardElevated,
            contentColor = tint,
            disabledContainerColor = SurfaceCardElevated.copy(alpha = 0.4f),
            disabledContentColor = TextMuted
        ),
        modifier = modifier
            .height(48.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
