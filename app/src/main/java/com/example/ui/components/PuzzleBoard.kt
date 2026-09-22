package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.data.model.TileTheme
import com.example.game.PuzzleEngine
import com.example.ui.GameState
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRose
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PuzzleBoard(
    board: List<Int>,
    difficulty: Difficulty,
    gameState: GameState,
    tileTheme: TileTheme,
    hintIndex: Int?,
    isPeeking: Boolean,
    onTileClick: (Int) -> Unit,
    onResumeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gridSize = difficulty.gridSize
    val displayBoard = if (isPeeking) PuzzleEngine.createSolvedBoard(difficulty) else board

    val boardBorderColor = when (tileTheme) {
        TileTheme.CLASSIC_WOOD -> Color(0xFF78350F)
        TileTheme.MINIMALIST_NEON -> NeonCyan.copy(alpha = 0.6f)
        TileTheme.NATURE -> Color(0xFF065F46)
    }

    val boardBg = when (tileTheme) {
        TileTheme.CLASSIC_WOOD -> Brush.radialGradient(listOf(Color(0xFF2E1C0C), Color(0xFF1B1007)))
        TileTheme.MINIMALIST_NEON -> Brush.radialGradient(listOf(Color(0xFF0F172A), Color(0xFF090D16)))
        TileTheme.NATURE -> Brush.radialGradient(listOf(Color(0xFF062B1D), Color(0xFF03160E)))
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(24.dp))
            .background(boardBg)
            .border(2.dp, boardBorderColor, RoundedCornerShape(24.dp))
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        val spacing = 6.dp
        val totalSpacing = spacing * (gridSize - 1)
        val availableWidth = maxWidth - totalSpacing
        val tileSize = availableWidth / gridSize

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(spacing)
        ) {
            for (row in 0 until gridSize) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(spacing)
                ) {
                    for (col in 0 until gridSize) {
                        val index = row * gridSize + col
                        val tileValue = displayBoard.getOrNull(index) ?: 0
                        val isCorrect = !isPeeking && tileValue != 0 && PuzzleEngine.isTileInCorrectPosition(board, index)
                        val isHinted = !isPeeking && hintIndex == index

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (tileValue != 0) {
                                PuzzleTile(
                                    value = tileValue,
                                    totalTiles = difficulty.totalTiles,
                                    isCorrect = isCorrect,
                                    isHinted = isHinted,
                                    tileTheme = tileTheme,
                                    gridSize = gridSize,
                                    onClick = { onTileClick(index) }
                                )
                            } else {
                                // Empty slot
                                EmptySlot(gridSize = gridSize, tileTheme = tileTheme)
                            }
                        }
                    }
                }
            }
        }

        // Peeking Overlay Banner
        if (isPeeking) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(NeonCyan.copy(alpha = 0.9f))
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "TARGET SOLVED PREVIEW",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    letterSpacing = 1.sp
                )
            }
        }

        // Paused Frosted Overlay
        AnimatedVisibility(
            visible = gameState == GameState.PAUSED,
            enter = fadeIn(tween(200)),
            exit = fadeOut(tween(200))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceDark.copy(alpha = 0.92f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onResumeClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PauseCircle,
                        contentDescription = "Game Paused",
                        tint = NeonAmber,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "GAME PAUSED",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "Timer is paused. Tap anywhere to continue.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                    Button(
                        onClick = onResumeClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonAmber),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.testTag("resume_overlay_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "  Resume Game",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzleTile(
    value: Int,
    totalTiles: Int,
    isCorrect: Boolean,
    isHinted: Boolean,
    tileTheme: TileTheme,
    gridSize: Int,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "hint_pulse")
    val pulseScale by if (isHinted) {
        infiniteTransition.animateFloat(
            initialValue = 0.96f,
            targetValue = 1.04f,
            animationSpec = infiniteRepeatable(
                animation = tween(400, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        )
    } else {
        remember { androidx.compose.runtime.mutableStateOf(1f) }
    }

    val brush = getTileBrush(value, totalTiles, tileTheme)
    val cornerRadius = when (gridSize) {
        3 -> 16.dp
        4 -> 12.dp
        else -> 8.dp
    }

    val fontSize = when (gridSize) {
        3 -> 34.sp
        4 -> 24.sp
        else -> 17.sp
    }

    val defaultBorderColor = when (tileTheme) {
        TileTheme.CLASSIC_WOOD -> Color(0xFFFDE68A).copy(alpha = 0.35f) // Warm golden bevel edge
        TileTheme.MINIMALIST_NEON -> NeonCyan.copy(alpha = 0.5f) // Cyberpunk electric cyan edge
        TileTheme.NATURE -> Color(0xFFA7F3D0).copy(alpha = 0.4f) // Fresh botanical mint edge
    }

    val borderColor = when {
        isHinted -> NeonAmber
        isCorrect -> when (tileTheme) {
            TileTheme.CLASSIC_WOOD -> Color(0xFFFBBF24)
            TileTheme.MINIMALIST_NEON -> NeonCyan
            TileTheme.NATURE -> Color(0xFF34D399)
        }
        else -> defaultBorderColor
    }

    val borderWidth = when {
        isHinted -> 2.5.dp
        isCorrect -> 2.dp
        else -> 1.5.dp
    }

    val textColor = when (tileTheme) {
        TileTheme.CLASSIC_WOOD -> Color(0xFFFEF3C7) // Parchment cream
        TileTheme.MINIMALIST_NEON -> Color.White // Bright white neon
        TileTheme.NATURE -> Color(0xFFF0FDF4) // Dewdrop white
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .scale(pulseScale)
            .shadow(4.dp, RoundedCornerShape(cornerRadius))
            .clip(RoundedCornerShape(cornerRadius))
            .background(brush)
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
            .clickable { onClick() }
            .testTag("tile_$value"),
        contentAlignment = Alignment.Center
    ) {
        // Tile Number
        Text(
            text = "$value",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = fontSize,
            fontWeight = FontWeight.ExtraBold,
            color = textColor,
            fontFamily = FontFamily.SansSerif
        )

        // Correct Position Indicator
        if (isCorrect) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 5.dp, top = 5.dp)
                    .size(if (gridSize <= 3) 8.dp else 6.dp)
                    .clip(CircleShape)
                    .background(NeonEmerald)
            )
        }

        // Hint Highlight
        if (isHinted) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(NeonAmber)
                    .padding(horizontal = 6.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "MOVE",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun EmptySlot(gridSize: Int, tileTheme: TileTheme) {
    val cornerRadius = when (gridSize) {
        3 -> 16.dp
        4 -> 12.dp
        else -> 8.dp
    }

    val emptyBg = when (tileTheme) {
        TileTheme.CLASSIC_WOOD -> Color(0xFF1E1107).copy(alpha = 0.6f)
        TileTheme.MINIMALIST_NEON -> Color(0xFF070B14).copy(alpha = 0.7f)
        TileTheme.NATURE -> Color(0xFF03160E).copy(alpha = 0.65f)
    }

    val emptyBorder = when (tileTheme) {
        TileTheme.CLASSIC_WOOD -> Color(0xFF451A03).copy(alpha = 0.4f)
        TileTheme.MINIMALIST_NEON -> NeonCyan.copy(alpha = 0.2f)
        TileTheme.NATURE -> Color(0xFF047857).copy(alpha = 0.25f)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(cornerRadius))
            .background(emptyBg)
            .border(1.dp, emptyBorder, RoundedCornerShape(cornerRadius))
    )
}

private fun getTileBrush(value: Int, totalTiles: Int, theme: TileTheme): Brush {
    val progress = (value - 1).toFloat() / maxOf(1, totalTiles - 2).toFloat()

    return when (theme) {
        TileTheme.CLASSIC_WOOD -> {
            // Handcrafted rich mahogany, dark walnut, and golden honey teak woodgrain gradient
            val topWood = when {
                progress < 0.33f -> Color(0xFFB45309) // Honey amber cedar
                progress < 0.66f -> Color(0xFF9A3412) // Warm mahogany
                else -> Color(0xFF7C2D12) // Deep burnished walnut
            }
            val bottomWood = when {
                progress < 0.33f -> Color(0xFF78350F) // Rich timber
                progress < 0.66f -> Color(0xFF5A1E08) // Dark rosewood
                else -> Color(0xFF451A03) // Deep espresso bark
            }
            Brush.verticalGradient(listOf(topWood, bottomWood))
        }

        TileTheme.MINIMALIST_NEON -> {
            // Sleek electric cyberpunk hues: Electric Cyan -> Ultraviolet Purple -> Hot Neon Rose
            val c1 = when {
                progress < 0.33f -> Color(0xFF00F0FF) // Electric Cyber Cyan
                progress < 0.66f -> Color(0xFF818CF8) // Electric Indigo
                else -> Color(0xFFF43F5E) // Neon Rose Red
            }
            val c2 = when {
                progress < 0.33f -> Color(0xFF0284C7) // Deep Cyan
                progress < 0.66f -> Color(0xFF6D28D9) // Midnight Violet
                else -> Color(0xFFBE123C) // Dark Magenta
            }
            Brush.linearGradient(listOf(c1, c2))
        }

        TileTheme.NATURE -> {
            // Lush botanical palette: Fresh Mint Leaf -> Emerald Rainforest -> Deep Jade
            val topGreen = when {
                progress < 0.33f -> Color(0xFF34D399) // Mint / Spring Leaf
                progress < 0.66f -> Color(0xFF10B981) // Pure Emerald
                else -> Color(0xFF059669) // Forest Pine
            }
            val bottomGreen = when {
                progress < 0.33f -> Color(0xFF059669) // Forest Pine
                progress < 0.66f -> Color(0xFF047857) // Deep Jungle Jade
                else -> Color(0xFF064E3B) // Dark Moss / Evergreen
            }
            Brush.linearGradient(listOf(topGreen, bottomGreen))
        }
    }
}
