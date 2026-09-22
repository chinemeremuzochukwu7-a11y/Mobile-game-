package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.db.LeaderboardEntity
import com.example.data.model.Difficulty
import com.example.ui.theme.BronzeMedal
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SilverMedal
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LeaderboardDialog(
    isOpen: Boolean,
    activeDifficulty: Difficulty,
    isShowingOnlyUserScores: Boolean,
    scores: List<LeaderboardEntity>,
    onFilterChange: (Difficulty) -> Unit,
    onToggleUserScores: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.88f)
                .clip(RoundedCornerShape(28.dp))
                .border(1.5.dp, SurfaceBorder, RoundedCornerShape(28.dp)),
            color = SurfaceDark
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(NeonIndigo.copy(alpha = 0.2f))
                                .border(1.dp, NeonIndigo, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = GoldTrophy,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Global Leaderboard",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Top speed solvers worldwide",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_leaderboard_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Difficulty Tabs
                TabRow(
                    selectedTabIndex = activeDifficulty.ordinal,
                    containerColor = SurfaceCard,
                    contentColor = TextPrimary,
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(14.dp)),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[activeDifficulty.ordinal]),
                            color = NeonIndigo,
                            height = 3.dp
                        )
                    },
                    divider = {}
                ) {
                    Difficulty.values().forEach { diff ->
                        val selected = diff == activeDifficulty
                        Tab(
                            selected = selected,
                            onClick = { onFilterChange(diff) },
                            text = {
                                Text(
                                    text = "${diff.title} (${diff.gridSize}x${diff.gridSize})",
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = if (selected) TextPrimary else TextSecondary
                                )
                            },
                            modifier = Modifier.testTag("tab_${diff.name.lowercase()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Global vs My Scores Chip
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = isShowingOnlyUserScores,
                        onClick = onToggleUserScores,
                        label = {
                            Text(
                                text = if (isShowingOnlyUserScores) "Showing: My Scores" else "Showing: All Global",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (isShowingOnlyUserScores) Icons.Default.Person else Icons.Default.Public,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonIndigo.copy(alpha = 0.25f),
                            selectedLabelColor = NeonIndigo,
                            selectedLeadingIconColor = NeonIndigo
                        ),
                        modifier = Modifier.testTag("toggle_user_scores")
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of Scores
                if (scores.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Leaderboard,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = if (isShowingOnlyUserScores) "No personal records yet." else "No records found.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Complete a puzzle to post your high score!",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("leaderboard_list"),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(bottom = 8.dp)
                    ) {
                        itemsIndexed(scores, key = { _, item -> item.id }) { index, item ->
                            val rank = index + 1
                            LeaderboardRowItem(rank = rank, item = item)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LeaderboardRowItem(
    rank: Int,
    item: LeaderboardEntity
) {
    val isPodium = rank <= 3
    val rankBadgeColor = when (rank) {
        1 -> GoldTrophy
        2 -> SilverMedal
        3 -> BronzeMedal
        else -> TextMuted
    }

    val containerBg = when {
        item.isUser -> NeonIndigo.copy(alpha = 0.18f)
        isPodium -> SurfaceCardElevated
        else -> SurfaceCard
    }

    val borderStroke = when {
        item.isUser -> 1.5.dp to NeonIndigo
        rank == 1 -> 1.5.dp to GoldTrophy.copy(alpha = 0.6f)
        else -> 1.dp to SurfaceBorder
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(containerBg)
            .border(borderStroke.first, borderStroke.second, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Rank & Player Info
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f)
        ) {
            // Rank Badge
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (isPodium) rankBadgeColor.copy(alpha = 0.2f)
                        else SurfaceDark
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (rank == 1) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "1st",
                        tint = GoldTrophy,
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Text(
                        text = "#$rank",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = rankBadgeColor
                    )
                }
            }

            // Flag and Name
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = item.countryFlag, fontSize = 16.sp)
                    Text(
                        text = item.playerName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isUser) NeonCyan else TextPrimary,
                        maxLines = 1
                    )
                    if (item.isUser) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonIndigo)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "YOU",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                fontSize = 9.sp
                            )
                        }
                    }
                }

                // Moves and Date
                Text(
                    text = "${item.moves} moves",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        // Time and Score
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = item.formattedTime,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = NeonCyan
            )
            Text(
                text = "${item.score} pts",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold,
                color = NeonAmber,
                fontSize = 11.sp
            )
        }
    }
}
