package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.NeonRose
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DifficultySelector(
    selectedDifficulty: Difficulty,
    onDifficultySelected: (Difficulty) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceCard)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(16.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Difficulty.values().forEach { difficulty ->
            val isSelected = difficulty == selectedDifficulty
            val activeColor = when (difficulty) {
                Difficulty.EASY -> Color(0xFFF59E0B) // Classic Wood amber
                Difficulty.MEDIUM -> NeonCyan // Minimalist Neon cyber cyan
                Difficulty.HARD -> Color(0xFF10B981) // Nature emerald
            }

            val themeName = when (difficulty) {
                Difficulty.EASY -> "Classic Wood"
                Difficulty.MEDIUM -> "Minimalist Neon"
                Difficulty.HARD -> "Nature"
            }

            val bgColor by animateColorAsState(
                targetValue = if (isSelected) SurfaceCardElevated else Color.Transparent,
                animationSpec = tween(250),
                label = "diff_bg"
            )

            val borderColor by animateColorAsState(
                targetValue = if (isSelected) activeColor else Color.Transparent,
                animationSpec = tween(250),
                label = "diff_border"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgColor)
                    .border(if (isSelected) 1.5.dp else 0.dp, borderColor, RoundedCornerShape(12.dp))
                    .clickable { onDifficultySelected(difficulty) }
                    .padding(vertical = 8.dp, horizontal = 6.dp)
                    .testTag("difficulty_${difficulty.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = difficulty.title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) TextPrimary else TextSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${difficulty.gridSize}x${difficulty.gridSize}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isSelected) activeColor else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = themeName,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) activeColor.copy(alpha = 0.9f) else TextMuted.copy(alpha = 0.8f),
                        fontSize = 9.sp,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
