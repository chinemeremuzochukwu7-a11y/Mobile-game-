package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Interactive tutorial instruction banner showing clear directions on how to play on Level 1 and Level 2.
 * - Level 1: Introduces sliding mechanics into the empty spot and initial tile arrangement.
 * - Level 2: Teaches strategic row-by-row solving, green target indicators, and speed tips.
 */
@Composable
fun LevelInstructionsBanner(
    levelNumber: Int,
    isDismissedOrCompleted: Boolean,
    onDismissOrStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isDismissedOrCompleted) return

    val infiniteTransition = rememberInfiniteTransition(label = "instr_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    if (levelNumber == 1) {
                        Brush.horizontalGradient(listOf(Color(0xFF1E1B4B), Color(0xFF312E81)))
                    } else {
                        Brush.horizontalGradient(listOf(Color(0xFF0F2E3A), Color(0xFF134E5E)))
                    }
                )
                .border(
                    1.5.dp,
                    if (levelNumber == 1) NeonCyan else NeonAmber,
                    RoundedCornerShape(18.dp)
                )
                .padding(14.dp)
                .testTag("level_${levelNumber}_instructions_banner")
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Header with Badge & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .scale(pulseScale)
                                .clip(CircleShape)
                                .background(
                                    (if (levelNumber == 1) NeonCyan else NeonAmber).copy(alpha = 0.2f)
                                )
                                .border(
                                    1.dp,
                                    if (levelNumber == 1) NeonCyan else NeonAmber,
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (levelNumber == 1) Icons.Default.TouchApp else Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = if (levelNumber == 1) NeonCyan else NeonAmber,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (levelNumber == 1) "LEVEL 1 INSTRUCTIONS: THE BASICS" else "LEVEL 2 INSTRUCTIONS: STRATEGY",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (levelNumber == 1) NeonAmber else NeonCyan,
                                letterSpacing = 0.6.sp
                            )
                            Text(
                                text = if (levelNumber == 1) "How to slide & arrange tiles" else "Row-by-row solving tactics",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismissOrStart,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss Instructions",
                            tint = TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Instruction body
                if (levelNumber == 1) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InstructionBullet(
                            number = "1",
                            text = "Tap the glowing tile adjacent to the empty spot to slide it into position."
                        )
                        InstructionBullet(
                            number = "2",
                            text = "Arrange numbers in sequential order: 1, 2, 3 on top row, 4, 5, 6 in middle."
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        InstructionBullet(
                            number = "1",
                            text = "Lock Row 1 first! Solve 1, 2, and 3 in the top row so you never have to move them again."
                        )
                        InstructionBullet(
                            number = "2",
                            text = "Look for the green dots on tiles—they signal the tile is already in its home position!"
                        )
                        InstructionBullet(
                            number = "3",
                            text = "Beat the par time (20s) and par moves (8 moves) to unlock 3 gold stars."
                        )
                    }
                }

                // Action button to start or continue
                Button(
                    onClick = onDismissOrStart,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (levelNumber == 1) NeonCyan else NeonAmber
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                        .testTag("level_${levelNumber}_got_it_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (levelNumber == 1) "Got It, Let's Slide!" else "Ready, Play Level 2!",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun InstructionBullet(
    number: String,
    text: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = number,
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            lineHeight = 16.sp,
            fontSize = 12.sp
        )
    }
}
