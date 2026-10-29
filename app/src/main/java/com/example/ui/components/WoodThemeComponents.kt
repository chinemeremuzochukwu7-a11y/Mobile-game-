package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.LeaderboardEntity

// Authentic Wood and Organic Color Palette matching user's reference image
val WoodPlankLight = Color(0xFFE5943B)
val WoodPlankMid = Color(0xFFCE7826)
val WoodPlankDark = Color(0xFFBF6F20)
val WoodSeam = Color(0xFF8B4410)

val WoodBorderDark = Color(0xFF5E270A)
val WoodBorderMid = Color(0xFF8A3E12)
val WoodBorderLight = Color(0xFFB85E1E)

val WoodTrayBg = Color(0xFF451B06)
val TileWoodFace = Color(0xFFDF8E3B)
val TileWoodLightEdge = Color(0xFFFFB660)
val TileWoodDarkEdge = Color(0xFF8B4510)

val TileNumberBrown = Color(0xFF4A1F06)
val TileNumberRed = Color(0xFF870E12)

val MetalPlateBase = Color(0xFFCBD2D8)
val MetalPlateTop = Color(0xFFE2E7EC)
val MetalPlateBottom = Color(0xFFA6AFB8)
val MetalRivet = Color(0xFF8B949D)
val MetalRivetSlot = Color(0xFF555D65)
val MetalNumberSlate = Color(0xFF333E47)

val ParchmentBg = Color(0xFFFBE7C3)
val ParchmentBorder = Color(0xFFD6BA8E)
val LeafGreenDark = Color(0xFF2C643E)
val LeafGreenLight = Color(0xFF4E9861)

/**
 * Background rendering authentic vertical wood planks with realistic plank divider seams.
 */
@Composable
fun WoodPlankBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.horizontalGradient(
                    listOf(
                        WoodPlankDark,
                        WoodPlankLight,
                        WoodPlankMid,
                        WoodPlankLight,
                        WoodPlankDark
                    )
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val plankWidth = size.width / 5f
            for (i in 1..4) {
                val x = i * plankWidth
                // Dark seam
                drawLine(
                    color = WoodSeam.copy(alpha = 0.55f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 2.5f
                )
                // Highlight line next to seam
                drawLine(
                    color = Color(0xFFFFD59E).copy(alpha = 0.25f),
                    start = Offset(x + 2f, 0f),
                    end = Offset(x + 2f, size.height),
                    strokeWidth = 1.5f
                )
            }
        }

        // Content on top
        content()
    }
}

/**
 * Decorative Botanical Leaves sprig illustrated with Canvas vectors.
 */
@Composable
fun BotanicalFoliageSprig(
    modifier: Modifier = Modifier,
    scale: Float = 1f
) {
    Canvas(modifier = modifier.size((70 * scale).dp, (70 * scale).dp)) {
        val w = size.width
        val h = size.height

        // Stem
        val stemPath = Path().apply {
            moveTo(w * 0.1f, h * 0.9f)
            quadraticTo(w * 0.45f, h * 0.55f, w * 0.85f, h * 0.15f)
        }
        drawPath(
            path = stemPath,
            color = LeafGreenDark,
            style = Stroke(width = 3.5f, cap = StrokeCap.Round)
        )

        // Multiple gentle leaves
        val leafPositions = listOf(
            Offset(w * 0.25f, h * 0.72f) to -40f,
            Offset(w * 0.45f, h * 0.52f) to 45f,
            Offset(w * 0.60f, h * 0.38f) to -30f,
            Offset(w * 0.75f, h * 0.22f) to 50f,
            Offset(w * 0.88f, h * 0.12f) to 15f
        )

        for ((pos, rot) in leafPositions) {
            drawOval(
                brush = Brush.radialGradient(
                    colors = listOf(LeafGreenLight, LeafGreenDark),
                    center = pos,
                    radius = 18f
                ),
                topLeft = Offset(pos.x - 12f, pos.y - 7f),
                size = Size(24f, 14f)
            )
        }
    }
}

/**
 * Arched sculpted wooden top header bar matching the user's reference image exactly:
 * - Left: Circular wooden undo button (↶)
 * - Center: Raised arched medallion with "MOVES", large moves count, "LEVEL 1" ribbon, hanging hourglass timer tab
 * - Right: Pinned parchment note with crown (👑) and record
 * - Top-right: Three dots menu button (⋮)
 */
@Composable
fun WoodHeaderWidget(
    moves: Int,
    levelNumber: Int,
    timeElapsedMillis: Long,
    bestRecord: LeaderboardEntity?,
    onUndoClick: () -> Unit,
    onLevelClick: () -> Unit,
    onRecordClick: () -> Unit,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSeconds = timeElapsedMillis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val formattedTime = String.format("%02d:%02d", minutes, seconds)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        // Decorative foliage leaves peeking behind the top crest
        BotanicalFoliageSprig(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-14).dp)
                .scale(1.4f)
        )

        // Main Sculpted Wooden Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp)
                .height(82.dp)
                .clip(RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(WoodBorderLight, WoodBorderMid, WoodBorderDark)
                    )
                )
                .border(2.5.dp, Color(0xFF6B2F0B), RoundedCornerShape(bottomStart = 26.dp, bottomEnd = 26.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left circular wooden Undo Button
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFBC6724), Color(0xFF7A360C))
                        )
                    )
                    .border(2.dp, Color(0xFF542407), CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onUndoClick() }
                    .testTag("wooden_undo_button"),
                contentAlignment = Alignment.Center
            ) {
                // Inner recessed ring
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF6A2D09), Color(0xFF8F4111))
                            )
                        )
                        .border(1.5.dp, Color(0xFF4A1E05), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Undo,
                        contentDescription = "Undo Move",
                        tint = Color(0xFFFBE5C5),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Right Pinned Parchment Note with Crown
            Box(
                modifier = Modifier
                    .width(78.dp)
                    .height(64.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(ParchmentBg, Color(0xFFF3D5A5))
                        )
                    )
                    .border(1.5.dp, ParchmentBorder, RoundedCornerShape(6.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onRecordClick() }
                    .padding(horizontal = 4.dp, vertical = 6.dp)
                    .testTag("wooden_parchment_record"),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Gold Crown
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Best Record",
                        tint = Color(0xFFD49216),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = bestRecord?.let { "${it.moves}m" } ?: "------",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF6B431D),
                        fontSize = 11.sp,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = bestRecord?.formattedTime ?: "------",
                        style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8F6034),
                        fontSize = 9.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Top-right dark circle Options Menu button (⋮)
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF262626).copy(alpha = 0.85f))
                    .border(1.dp, Color(0xFF4A4A4A), CircleShape)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onMenuClick() }
                    .testTag("wooden_top_menu_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Game Menu",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Center Medallion (Moves Counter & Level Ribbon)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-4).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Circular Wooden Crest (MOVES / count)
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(Color(0xFFB85E1E), Color(0xFF6B2F0B), Color(0xFF421B05))
                        )
                    )
                    .border(3.dp, Color(0xFF964612), CircleShape)
                    .shadow(elevation = 6.dp, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Inner dark face
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF4A1F07), Color(0xFF2E1204))
                            )
                        )
                        .border(1.5.dp, Color(0xFF381504), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "MOVES",
                            color = Color(0xFFFBE5C5),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "$moves",
                            color = Color(0xFFFFF6EA),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 28.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 30.sp
                        )
                    }
                }
            }

            // LEVEL 1 Wooden Ribbon Banner
            Box(
                modifier = Modifier
                    .offset(y = (-8).dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF9E4915), Color(0xFF612808))
                        )
                    )
                    .border(1.5.dp, Color(0xFFB85E1E), RoundedCornerShape(10.dp))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onLevelClick() }
                    .padding(horizontal = 14.dp, vertical = 3.dp)
                    .testTag("wooden_level_ribbon"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "LEVEL $levelNumber",
                    color = Color(0xFFFBE5C5),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
            }

            // Hanging Hourglass Timer Tab
            Box(
                modifier = Modifier
                    .offset(y = (-6).dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF7A360C), Color(0xFF451B06))
                        )
                    )
                    .border(1.dp, Color(0xFFB85E1E).copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
                    .testTag("wooden_timer_tab"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassBottom,
                        contentDescription = "Timer",
                        tint = Color(0xFFFBE5C5),
                        modifier = Modifier.size(11.dp)
                    )
                    Text(
                        text = formattedTime,
                        color = Color(0xFFFFF6EA),
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

/**
 * 3x3 (or NxN) Wooden Board Tray and Tiles matching the user's reference image:
 * - Thick rounded wooden frame with 3D beveled bevels
 * - Recessed dark wood tray
 * - Chunky wooden tiles with carved numbers:
 *    - Standard tiles: deep dark brown wood numbers
 *    - Correct/target position tiles: deep crimson/red numbers (tiles 2 and 6 in picture)
 *    - Special Brushed Steel Metal Tile (tile 3 in picture) with 4 metallic screws and stamped number
 */
@Composable
fun WoodPuzzleBoard(
    board: List<Int>,
    gridSize: Int,
    hintIndex: Int?,
    isPeeking: Boolean,
    onTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(32.dp))
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(WoodBorderLight, WoodBorderMid, WoodBorderDark)
                )
            )
            .border(5.dp, Color(0xFF8F4111), RoundedCornerShape(32.dp))
            .padding(12.dp)
            .testTag("wooden_puzzle_board")
    ) {
        // Inner recessed dark wood tray
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF5A250B), WoodTrayBg, Color(0xFF2C1003))
                    )
                )
                .border(2.dp, Color(0xFF2C1003), RoundedCornerShape(22.dp))
                .padding(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                for (row in 0 until gridSize) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0 until gridSize) {
                            val index = row * gridSize + col
                            val tileValue = board.getOrNull(index) ?: 0
                            val targetValue = index + 1
                            val isCorrectSpot = tileValue == targetValue && tileValue != 0
                            val isHint = hintIndex == index

                            // Tile 3 in the user's picture is the special brushed steel metal plate
                            val isSpecialMetalTile = (tileValue == 3) || isHint

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (tileValue == 0) {
                                    // Empty slot: dark recessed cavity with drop shadow
                                    EmptySlotCavity()
                                } else {
                                    if (isSpecialMetalTile) {
                                        // Brushed Steel Metal Plate with 4 screws (like tile '3' in screenshot)
                                        BrushedMetalTile(
                                            number = tileValue,
                                            onClick = { onTileClick(index) }
                                        )
                                    } else {
                                        // Rich Warm Wooden Tile
                                        ChunkyWoodTile(
                                            number = tileValue,
                                            isCorrectSpot = isCorrectSpot,
                                            onClick = { onTileClick(index) }
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
}

/**
 * Chunky 3D wooden tile with carved numbers:
 * - Standard tiles: deep dark brown wood engraved numbers
 * - Correctly matched tiles: deep crimson / dark red engraved numbers (like '2' and '6' in picture)
 */
@Composable
fun ChunkyWoodTile(
    number: Int,
    isCorrectSpot: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val numberColor = if (isCorrectSpot) TileNumberRed else TileNumberBrown

    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        TileWoodLightEdge,
                        TileWoodFace,
                        Color(0xFFC97626),
                        TileWoodDarkEdge
                    )
                )
            )
            .border(2.5.dp, Color(0xFF7A370A), RoundedCornerShape(16.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .testTag("wood_tile_$number"),
        contentAlignment = Alignment.Center
    ) {
        // Subtle horizontal wood grain lines on the tile
        Canvas(modifier = Modifier.fillMaxSize()) {
            val h = size.height
            val w = size.width
            drawLine(
                color = Color(0xFF9E4B11).copy(alpha = 0.25f),
                start = Offset(0f, h * 0.35f),
                end = Offset(w, h * 0.35f),
                strokeWidth = 1f
            )
            drawLine(
                color = Color(0xFF9E4B11).copy(alpha = 0.25f),
                start = Offset(0f, h * 0.65f),
                end = Offset(w, h * 0.65f),
                strokeWidth = 1f
            )
        }

        // Engraved 3D Number with bottom highlight and inner shadow
        Text(
            text = "$number",
            color = numberColor,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Brushed Steel Metallic Tile (exactly like tile '3' in the user's reference image):
 * - Brushed silver/metal plate with horizontal & vertical specular sheen
 * - 4 metallic rivets/screws with cross-slots in the four corners
 * - Stamped metallic slate/charcoal number
 */
@Composable
fun BrushedMetalTile(
    number: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        MetalPlateTop,
                        MetalPlateBase,
                        MetalPlateBottom
                    )
                )
            )
            .border(2.dp, Color(0xFF86929B), RoundedCornerShape(16.dp))
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .testTag("metal_tile_$number"),
        contentAlignment = Alignment.Center
    ) {
        // Brushed metal vertical grain & 4 corner screws
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // 4 corner screws
            val screwRadius = 6.5f
            val cornerOffset = 14f

            val screwPositions = listOf(
                Offset(cornerOffset, cornerOffset),
                Offset(w - cornerOffset, cornerOffset),
                Offset(cornerOffset, h - cornerOffset),
                Offset(w - cornerOffset, h - cornerOffset)
            )

            for (pos in screwPositions) {
                // Outer screw rim
                drawCircle(
                    color = Color(0xFF5D666E),
                    radius = screwRadius + 1f,
                    center = pos
                )
                // Screw head
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFFE8ECEF), MetalRivet, Color(0xFF6E7881)),
                        center = pos,
                        radius = screwRadius
                    ),
                    radius = screwRadius,
                    center = pos
                )
                // Cross slot
                drawLine(
                    color = MetalRivetSlot,
                    start = Offset(pos.x - 3.5f, pos.y),
                    end = Offset(pos.x + 3.5f, pos.y),
                    strokeWidth = 1.5f
                )
                drawLine(
                    color = MetalRivetSlot,
                    start = Offset(pos.x, pos.y - 3.5f),
                    end = Offset(pos.x, pos.y + 3.5f),
                    strokeWidth = 1.5f
                )
            }
        }

        // Stamped dark metallic number
        Text(
            text = "$number",
            color = MetalNumberSlate,
            fontSize = 38.sp,
            fontWeight = FontWeight.Black,
            fontFamily = FontFamily.SansSerif,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Recessed cavity for empty slot inside the wooden board
 */
@Composable
fun EmptySlotCavity(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF331404))
            .border(2.dp, Color(0xFF220C02), RoundedCornerShape(16.dp))
    )
}

/**
 * 4 Circular Wooden Action Buttons sitting on the deck at the bottom:
 * 1. Hint (💡)
 * 2. Restart / Reset (↻)
 * 3. Inspect / Peek (🔍)
 * 4. Next Level (⏭)
 */
@Composable
fun WoodBottomControls(
    onHintClick: () -> Unit,
    onRestartClick: () -> Unit,
    onPeekClick: () -> Unit,
    onNextLevelClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Hint Button (Lightbulb)
        WoodRoundButton(
            icon = Icons.Default.Lightbulb,
            contentDescription = "Hint",
            iconColor = Color(0xFFFFD464),
            onClick = onHintClick,
            testTag = "wooden_hint_button"
        )

        // 2. Restart Button (↻)
        WoodRoundButton(
            icon = Icons.Default.Refresh,
            contentDescription = "Restart Level",
            iconColor = Color(0xFFFBE5C5),
            onClick = onRestartClick,
            testTag = "wooden_restart_button"
        )

        // 3. Inspect / Peek Button (🔍)
        WoodRoundButton(
            icon = Icons.Default.Search,
            contentDescription = "Inspect Solved Board",
            iconColor = Color(0xFFFBE5C5),
            onClick = onPeekClick,
            testTag = "wooden_peek_button"
        )

        // 4. Next Level Button (⏭)
        WoodRoundButton(
            icon = Icons.Default.SkipNext,
            contentDescription = "Next Level",
            iconColor = Color(0xFFFBE5C5),
            onClick = onNextLevelClick,
            testTag = "wooden_next_button"
        )
    }
}

/**
 * Individual circular wooden carved button with concentric bevel rings.
 */
@Composable
fun WoodRoundButton(
    icon: ImageVector,
    contentDescription: String,
    iconColor: Color,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(56.dp)
            .shadow(elevation = 5.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFFBC6724), Color(0xFF7A360C))
                )
            )
            .border(2.5.dp, Color(0xFF542407), CircleShape)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        // Inner carved ring
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF6A2D09), Color(0xFF8F4111))
                    )
                )
                .border(1.5.dp, Color(0xFF4A1E05), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
