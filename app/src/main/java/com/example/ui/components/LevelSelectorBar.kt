package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Difficulty
import com.example.data.model.Level
import com.example.data.model.LevelCatalog
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonRose
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Level carousel bar allowing players to select from 10 progressive stages in each difficulty
 * (Easy, Medium, and Hard). Displays level number, name, star goals, and current active status.
 */
@Composable
fun LevelSelectorBar(
    difficulty: Difficulty,
    currentLevelNumber: Int,
    maxUnlockedLevel: Int,
    onSelectLevel: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val levels = LevelCatalog.getLevelsForDifficulty(difficulty)
    val currentLevel = LevelCatalog.getLevel(difficulty, currentLevelNumber)

    val themeColor = when (difficulty) {
        Difficulty.EASY -> Color(0xFFF59E0B) // Classic Wood amber
        Difficulty.MEDIUM -> NeonCyan // Minimalist Neon cyber cyan
        Difficulty.HARD -> Color(0xFF10B981) // Nature emerald
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(20.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Active Level Header Info
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
                        .clip(RoundedCornerShape(8.dp))
                        .background(themeColor.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "LEVEL $currentLevelNumber / ${levels.size}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = themeColor,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = currentLevel.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            // Target Par info
            Text(
                text = "Par: ${currentLevel.parMoves} moves",
                style = MaterialTheme.typography.bodySmall,
                color = TextMuted,
                fontSize = 11.sp
            )
        }

        // Horizontal Scrollable Level Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("level_selector_row"),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            items(levels, key = { it.levelNumber }) { level ->
                val isSelected = level.levelNumber == currentLevelNumber
                val isUnlocked = level.levelNumber <= maxUnlockedLevel

                LevelChip(
                    level = level,
                    isSelected = isSelected,
                    isUnlocked = isUnlocked,
                    themeColor = themeColor,
                    onClick = {
                        if (isUnlocked) onSelectLevel(level.levelNumber)
                    }
                )
            }
        }
    }
}

@Composable
private fun LevelChip(
    level: Level,
    isSelected: Boolean,
    isUnlocked: Boolean,
    themeColor: Color,
    onClick: () -> Unit
) {
    val bg = when {
        isSelected -> themeColor
        isUnlocked -> SurfaceCardElevated
        else -> SurfaceDark.copy(alpha = 0.6f)
    }

    val textColor = when {
        isSelected -> if (themeColor == NeonCyan) Color.Black else Color.White
        isUnlocked -> TextPrimary
        else -> TextMuted
    }

    val border = when {
        isSelected -> 1.5.dp to Color.White.copy(alpha = 0.8f)
        isUnlocked -> 1.dp to SurfaceBorder
        else -> 1.dp to SurfaceBorder.copy(alpha = 0.3f)
    }

    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(border.first, border.second, RoundedCornerShape(14.dp))
            .clickable(enabled = isUnlocked) { onClick() }
            .testTag("level_chip_${level.levelNumber}"),
        contentAlignment = Alignment.Center
    ) {
        if (!isUnlocked) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked Level",
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
        } else if (level.isTutorial) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${level.levelNumber}",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp,
                    color = textColor
                )
                Text(
                    text = "TUT",
                    fontWeight = FontWeight.Bold,
                    fontSize = 8.sp,
                    color = if (isSelected) textColor else NeonAmber
                )
            }
        } else {
            Text(
                text = "${level.levelNumber}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 15.sp,
                color = textColor
            )
        }
    }
}
