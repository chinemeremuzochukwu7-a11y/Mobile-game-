package com.example.ui.components

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.Difficulty
import com.example.data.model.LevelCatalog

/**
 * Dedicated Levels & Stages Section Dialog.
 * Provides free access to all 105 levels across Easy (3x3), Medium (4x4), Hard (5x5), and Expert (6x6),
 * displaying earned 1-3 star ratings on completed levels.
 */
@Composable
fun LevelsSectionDialog(
    isOpen: Boolean,
    currentDifficulty: Difficulty,
    currentLevelNumber: Int,
    maxUnlockedLevel: Int,
    levelStarsMap: Map<Int, Int> = emptyMap(),
    totalSolvedCount: Int = 0,
    onSelectDifficulty: (Difficulty) -> Unit,
    onSelectLevel: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    val levels = LevelCatalog.getLevelsForDifficulty(currentDifficulty)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .clip(RoundedCornerShape(26.dp))
                .border(2.5.dp, Color(0xFF8F4111), RoundedCornerShape(26.dp)),
            color = Color(0xFF381604)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with title and close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "105 LEVELS & STAGES",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFFBE5C5),
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = "All stages open • $totalSolvedCount puzzles solved",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5A250B))
                            .border(1.dp, Color(0xFF8F4111), CircleShape)
                            .testTag("close_levels_section_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Levels",
                            tint = Color(0xFFFBE5C5),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Difficulty Tab Row (Easy 3x3, Medium 4x4, Hard 5x5, Expert 6x6)
                ScrollableTabRow(
                    selectedTabIndex = currentDifficulty.ordinal,
                    containerColor = Color(0xFF260D02),
                    contentColor = Color(0xFFFBE5C5),
                    edgePadding = 4.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[currentDifficulty.ordinal]),
                            color = Color(0xFFE5943B),
                            height = 3.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    Difficulty.values().forEach { diff ->
                        val selected = diff == currentDifficulty
                        Tab(
                            selected = selected,
                            onClick = { onSelectDifficulty(diff) },
                            text = {
                                Text(
                                    text = "${diff.title} (${diff.gridSize}x${diff.gridSize})",
                                    fontWeight = if (selected) FontWeight.ExtraBold else FontWeight.Medium,
                                    color = if (selected) Color(0xFFFBE5C5) else Color(0xFF9E7247),
                                    fontSize = 12.sp
                                )
                            }
                        )
                    }
                }

                // Grid of Levels (All Unlocked for Free Play!)
                LazyVerticalGrid(
                    columns = GridCells.Fixed(5),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(levels, key = { it.levelNumber }) { level ->
                        val isSelected = level.levelNumber == currentLevelNumber
                        val earnedStars = levelStarsMap[level.levelNumber] ?: 0
                        val isCleared = earnedStars > 0 || level.levelNumber < maxUnlockedLevel

                        val bg = when {
                            isSelected -> Brush.verticalGradient(listOf(Color(0xFFE5943B), Color(0xFFBF6F20)))
                            isCleared -> Brush.verticalGradient(listOf(Color(0xFF7D3B10), Color(0xFF522209)))
                            else -> Brush.verticalGradient(listOf(Color(0xFF5E290D), Color(0xFF431A06)))
                        }

                        val textColor = if (isSelected) Color(0xFF381604) else Color(0xFFFBE5C5)

                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(bg)
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    when {
                                        isSelected -> Color(0xFFFFF0D2)
                                        earnedStars > 0 -> Color(0xFFFFD464).copy(alpha = 0.7f)
                                        else -> Color(0xFF8F4111)
                                    },
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onSelectLevel(level.levelNumber)
                                    onDismiss()
                                }
                                .testTag("level_section_chip_${level.levelNumber}"),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${level.levelNumber}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = textColor
                                )

                                if (earnedStars > 0) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                        repeat(earnedStars) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = null,
                                                tint = if (isSelected) Color(0xFF381604) else Color(0xFFFFD464),
                                                modifier = Modifier.size(9.dp)
                                            )
                                        }
                                    }
                                } else {
                                    Text(
                                        text = "${level.parMoves}m",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 8.sp,
                                        color = textColor.copy(alpha = 0.75f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
