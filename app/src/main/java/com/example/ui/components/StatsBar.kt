package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.LeaderboardEntity
import com.example.ui.GameState
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRose
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StatsBar(
    timeElapsedMillis: Long,
    moves: Int,
    gameState: GameState,
    bestRecord: LeaderboardEntity?,
    onTogglePause: () -> Unit,
    isTimerWarningActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val totalSeconds = timeElapsedMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (timeElapsedMillis % 1000) / 100
    val formattedTime = String.format("%02d:%02d.%d", minutes, seconds, tenths)

    val timerColor = if (isTimerWarningActive) NeonRose else NeonCyan

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(
                1.dp,
                if (isTimerWarningActive) NeonRose.copy(alpha = 0.8f) else SurfaceBorder,
                RoundedCornerShape(20.dp)
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Live Timer
            StatCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = timerColor,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = if (isTimerWarningActive) "TIME (HURRY!)" else "TIME",
                value = formattedTime,
                valueColor = timerColor,
                modifier = Modifier.weight(1f),
                testTag = "timer_display"
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Move Counter
            StatCard(
                icon = {
                    Icon(
                        imageVector = Icons.Default.TouchApp,
                        contentDescription = "Moves",
                        tint = NeonAmber,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = "MOVES",
                value = "$moves",
                valueColor = NeonAmber,
                modifier = Modifier.weight(0.85f),
                testTag = "moves_display"
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Pause / Play Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (gameState == GameState.PAUSED) NeonAmber.copy(alpha = 0.2f)
                        else SurfaceCardElevated
                    )
                    .border(
                        1.dp,
                        if (gameState == GameState.PAUSED) NeonAmber else SurfaceBorder,
                        CircleShape
                    )
            ) {
                IconButton(
                    onClick = onTogglePause,
                    enabled = gameState == GameState.PLAYING || gameState == GameState.PAUSED,
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("pause_resume_button")
                ) {
                    Icon(
                        imageVector = if (gameState == GameState.PAUSED) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (gameState == GameState.PAUSED) "Resume" else "Pause",
                        tint = if (gameState == GameState.PAUSED) NeonAmber else TextPrimary
                    )
                }
            }
        }

        // Best Record Pill (if exists)
        if (bestRecord != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SurfaceCardElevated)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Best Score",
                        tint = GoldTrophy,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Personal Best",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary
                    )
                }
                Text(
                    text = "${bestRecord.formattedTime} • ${bestRecord.moves} moves (${bestRecord.score} pts)",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldTrophy
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    icon: @Composable () -> Unit,
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier,
    testTag: String
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceCardElevated)
            .border(1.dp, SurfaceBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon()
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
