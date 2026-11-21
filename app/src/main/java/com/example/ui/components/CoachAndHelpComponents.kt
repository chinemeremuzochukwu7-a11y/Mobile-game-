package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.game.CoachState
import com.example.game.CoachTone
import com.example.game.GuidedHelpMove
import com.example.game.SlideDirection

/**
 * Horizontal Coach Bar displayed at the top of the game interface:
 * - Shows a horizontal progress line reflecting whether the player is solving the numbers accurately
 * - Acts as a live coach encouraging effort ("Great move!", "Keep trying", or "Moving away")
 * - Provides a direct "Get Helped" pill button that guides the user step-by-step
 */
@Composable
fun CoachHorizontalBar(
    coachState: CoachState,
    isGuidedModeActive: Boolean,
    guidedHelpMove: GuidedHelpMove?,
    onGetHelpedClick: () -> Unit,
    onAutoStepClick: () -> Unit,
    onStopGuideClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = coachState.progressFraction.coerceIn(0.02f, 1f),
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "coach_line_progress"
    )

    val lineColor by animateColorAsState(
        targetValue = when (coachState.tone) {
            CoachTone.EXCELLENT, CoachTone.SOLVED -> Color(0xFF4ADE80) // Crisp Emerald
            CoachTone.TRYING, CoachTone.READY -> Color(0xFFFFB74D)     // Warm Honey Gold
            CoachTone.OFF_TRACK -> Color(0xFFFF6B5B)                   // Alert Coral-Red
        },
        animationSpec = tween(durationMillis = 260),
        label = "coach_line_color"
    )

    val statusDotColor = when (coachState.tone) {
        CoachTone.EXCELLENT, CoachTone.SOLVED -> Color(0xFF22C55E)
        CoachTone.TRYING, CoachTone.READY -> Color(0xFFF59E0B)
        CoachTone.OFF_TRACK -> Color(0xFFEF4444)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF5A250B), Color(0xFF3A1604))
                )
            )
            .border(1.5.dp, Color(0xFF9C4D18), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 7.dp)
            .testTag("coach_horizontal_bar"),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        // Row 1: Coach status text + correct counter + "Get Helped" button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(statusDotColor)
                        .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape)
                )

                Text(
                    text = if (isGuidedModeActive && guidedHelpMove != null) {
                        "Guide: Move Tile ${guidedHelpMove.tileValue} ${guidedHelpMove.direction.arrow}"
                    } else {
                        coachState.message
                    },
                    color = Color(0xFFFFF1DC),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Solved fraction badge (e.g. 5/8)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2B1003))
                    .border(1.dp, Color(0xFF7D3B10), RoundedCornerShape(8.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${coachState.correctTiles}/${coachState.totalNumberedTiles}",
                    color = lineColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            if (isGuidedModeActive && guidedHelpMove != null) {
                // Quick Auto-Step + Stop Guide buttons
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF22C55E))
                            .clickable { onAutoStepClick() }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("coach_auto_step_button")
                    ) {
                        Text(
                            text = "Step",
                            color = Color(0xFF0A2E14),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF6B2F0B))
                            .border(1.dp, Color(0xFFB85E1E), RoundedCornerShape(10.dp))
                            .clickable { onStopGuideClick() }
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                            .testTag("coach_stop_guide_button")
                    ) {
                        Text(
                            text = "Done",
                            color = Color(0xFFFBE5C5),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                // "Get Helped" action pill right on the coach bar
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFFE5943B), Color(0xFFC46B18))
                            )
                        )
                        .border(1.dp, Color(0xFFFFD89C), RoundedCornerShape(10.dp))
                        .clickable { onGetHelpedClick() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("get_helped_button"),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                        contentDescription = "Get Helped",
                        tint = Color(0xFF381604),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Get Helped",
                        color = Color(0xFF381604),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Row 2: The Horizontal Coach Line Track
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            val trackWidth = maxWidth
            val knobSize = 10.dp
            val knobOffset = ((trackWidth - knobSize) * animatedProgress).coerceIn(0.dp, trackWidth - knobSize)

            // Recessed horizontal track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF240D03))
                    .border(0.8.dp, Color(0xFF70320B), RoundedCornerShape(3.dp))
            )

            // Active horizontal fill line
            Box(
                modifier = Modifier
                    .fillMaxWidth(animatedProgress)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(lineColor.copy(alpha = 0.7f), lineColor)
                        )
                    )
            )

            // Coach progress indicator bead
            Box(
                modifier = Modifier
                    .offset(x = knobOffset)
                    .size(knobSize)
                    .clip(CircleShape)
                    .background(Color(0xFFFFF7E6))
                    .border(2.dp, lineColor, CircleShape)
            )
        }
    }
}

/**
 * Interactive "Get Helped" Assistant Modal.
 * Diagnoses the player's current puzzle board, tells them the exact next move & direction,
 * and lets them tap "Direct Me on Board" so the game visually directs them step-by-step.
 */
@Composable
fun GetHelpedDialog(
    isOpen: Boolean,
    coachState: CoachState,
    guidedHelpMove: GuidedHelpMove?,
    onDirectMeOnBoard: () -> Unit,
    onAutoMoveOneStep: () -> Unit,
    onOpenFullGuide: () -> Unit,
    onDismiss: () -> Unit
) {
    if (!isOpen) return

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(26.dp))
                .border(2.5.dp, Color(0xFFB85E1E), RoundedCornerShape(26.dp)),
            color = Color(0xFF381604)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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
                                .background(Color(0xFFE5943B))
                                .border(1.5.dp, Color(0xFFFFE0B2), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color(0xFF381604),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "PUZZLE COACH HELP",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFFBE5C5),
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = "Live guidance & next-move direction",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFFD6A068)
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF5A250B))
                            .border(1.dp, Color(0xFF8F4111), CircleShape)
                            .testTag("close_get_helped_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Help",
                            tint = Color(0xFFFBE5C5),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Current Coach Assessment Card
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF4D1E07))
                        .border(1.dp, Color(0xFF8F4111), RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "${coachState.correctTiles} of ${coachState.totalNumberedTiles} tiles in home spot",
                            color = Color(0xFFFFE6C2),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = coachState.message,
                            color = Color(0xFFD6A068),
                            fontSize = 11.sp
                        )
                    }

                    val pct = (coachState.progressFraction * 100).toInt().coerceIn(0, 100)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF2B1003))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "$pct%",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                    }
                }

                // Exact Guided Move Recommendation
                if (guidedHelpMove != null) {
                    val dirIcon = when (guidedHelpMove.direction) {
                        SlideDirection.UP -> Icons.Default.ArrowUpward
                        SlideDirection.DOWN -> Icons.Default.ArrowDownward
                        SlideDirection.LEFT -> Icons.AutoMirrored.Filled.ArrowBack
                        SlideDirection.RIGHT -> Icons.AutoMirrored.Filled.ArrowForward
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF692D0C), Color(0xFF4A1D06))
                                )
                            )
                            .border(1.5.dp, Color(0xFFE5943B), RoundedCornerShape(16.dp))
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Mini Tile + Direction Preview
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(TileWoodLightEdge, TileWoodFace)
                                        )
                                    )
                                    .border(2.dp, Color(0xFF7A370A), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${guidedHelpMove.tileValue}",
                                    color = TileNumberBrown,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp
                                )
                            }

                            Icon(
                                imageVector = dirIcon,
                                contentDescription = guidedHelpMove.direction.label,
                                tint = Color(0xFF4ADE80),
                                modifier = Modifier.size(26.dp)
                            )

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "RECOMMENDED NEXT MOVE",
                                    color = Color(0xFFFFD464),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.8.sp
                                )
                                Text(
                                    text = guidedHelpMove.instructionText,
                                    color = Color(0xFFFFF6EA),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    lineHeight = 17.sp
                                )
                                guidedHelpMove.estimatedStepsRemaining?.let { steps ->
                                    Text(
                                        text = "Optimal solution is $steps move${if (steps == 1) "" else "s"} away",
                                        color = Color(0xFF4ADE80),
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Strategy tip
                        Text(
                            text = guidedHelpMove.strategyTip,
                            color = Color(0xFFF3D5A5),
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                } else {
                    Text(
                        text = "This puzzle is already solved! Tap Next Level to keep playing.",
                        color = Color(0xFF4ADE80),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                // Direct Me on Board Primary Button
                Button(
                    onClick = onDirectMeOnBoard,
                    enabled = guidedHelpMove != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("direct_me_on_board_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE5943B),
                        contentColor = Color(0xFF381604)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Direct Me on Board (Live Arrow)",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                }

                // Auto-Move 1 Step + Full Rules Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onAutoMoveOneStep,
                        enabled = guidedHelpMove != null,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("auto_step_help_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFF4ADE80))
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color(0xFF4ADE80),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Auto-Step",
                            color = Color(0xFF4ADE80),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            onDismiss()
                            onOpenFullGuide()
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 48.dp)
                            .testTag("open_full_rules_from_help"),
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD6A068))
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color(0xFFFBE5C5),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Game Rules",
                            color = Color(0xFFFBE5C5),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }
        }
    }
}
