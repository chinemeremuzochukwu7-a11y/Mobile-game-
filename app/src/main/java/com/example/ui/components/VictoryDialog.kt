package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Difficulty
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun VictoryDialog(
    isOpen: Boolean,
    difficulty: Difficulty,
    currentLevelNumber: Int = 1,
    timeElapsedMillis: Long,
    moves: Int,
    score: Int,
    stars: Int,
    initialPlayerName: String,
    isScoreSubmitted: Boolean,
    onSubmitScore: (String) -> Unit,
    onPlayAgain: () -> Unit,
    onNextLevel: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    var playerName by remember(initialPlayerName) { mutableStateOf(initialPlayerName) }

    val totalSeconds = timeElapsedMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val tenths = (timeElapsedMillis % 1000) / 100
    val formattedTime = String.format("%02d:%02d.%d", minutes, seconds, tenths)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(28.dp))
                .border(2.dp, GoldTrophy.copy(alpha = 0.5f), RoundedCornerShape(28.dp)),
            color = SurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Trophy Icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(GoldTrophy.copy(alpha = 0.15f))
                        .border(1.5.dp, GoldTrophy, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Victory Trophy",
                        tint = GoldTrophy,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Title & Subtitle
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PUZZLE SOLVED!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${difficulty.title} Level $currentLevelNumber (${difficulty.gridSize}x${difficulty.gridSize}) Completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NeonCyan,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Stars rating
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (i in 1..3) {
                        Icon(
                            imageVector = if (i <= stars) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = "Star $i",
                            tint = if (i <= stars) GoldTrophy else TextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Performance Stats Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceCard)
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Time
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "TIME", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = formattedTime,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = NeonCyan
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(SurfaceBorder)
                    )

                    // Moves
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "MOVES", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = "$moves",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonAmber
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(36.dp)
                            .background(SurfaceBorder)
                    )

                    // Score
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "SCORE", style = MaterialTheme.typography.labelSmall, color = TextMuted)
                        Text(
                            text = "$score",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = NeonEmerald
                        )
                    }
                }

                // Leaderboard Submission Section
                if (!isScoreSubmitted) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = playerName,
                            onValueChange = { if (it.length <= 16) playerName = it },
                            label = { Text("Your Player Name") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonIndigo,
                                unfocusedBorderColor = SurfaceBorder,
                                focusedLabelColor = NeonIndigo,
                                cursorColor = NeonIndigo
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("player_name_input")
                        )

                        Button(
                            onClick = { onSubmitScore(playerName) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("submit_score_button"),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonIndigo),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Post to Global Leaderboard",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonEmerald.copy(alpha = 0.15f))
                            .border(1.dp, NeonEmerald, RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Success",
                            tint = NeonEmerald,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Score posted to Global Leaderboard!",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = NeonEmerald
                        )
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilledTonalButton(
                        onClick = onViewLeaderboard,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SurfaceCardElevated,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("victory_view_leaderboard")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Leaderboard,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Ranks", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }

                    FilledTonalButton(
                        onClick = onPlayAgain,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = SurfaceCardElevated,
                            contentColor = TextSecondary
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("victory_play_again")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Replay",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onNextLevel,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(48.dp)
                            .testTag("victory_next_level")
                    ) {
                        Text(
                            text = if (currentLevelNumber < 10) "Next Level" else "Complete!",
                            color = Color.Black,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}
