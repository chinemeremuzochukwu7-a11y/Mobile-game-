package com.example.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.BotanicalFoliageSprig
import com.example.ui.components.GameMenuDialog
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.LevelsSectionDialog
import com.example.ui.components.SettingsSidebar
import com.example.ui.components.VictoryDialog
import com.example.ui.components.WoodBottomControls
import com.example.ui.components.WoodHeaderWidget
import com.example.ui.components.WoodPlankBackground
import com.example.ui.components.WoodPuzzleBoard

/**
 * Main Game Interface designed to match the user's reference image exactly:
 * - Natural vertical honey-wood plank background
 * - Arched sculpted wooden top header with undo button, moves counter, level ribbon, and parchment record
 * - Chunky rounded wooden puzzle board with carved numbers, crimson target digits, and brushed steel tile with screws
 * - Four circular wooden bottom action buttons (💡 Hint, ↻ Reset, 🔍 Peek, ⏭ Next)
 * - Fixed, non-scrolling native mobile game viewport (no up/down scrolling)
 * - Levels and instructions separated cleanly into dedicated overlay sections
 */
@Composable
fun PuzzleScreen(
    viewModel: PuzzleViewModel = viewModel()
) {
    val difficulty by viewModel.difficulty.collectAsStateWithLifecycle()
    val currentLevelNumber by viewModel.currentLevelNumber.collectAsStateWithLifecycle()
    val maxUnlockedLevel by viewModel.maxUnlockedLevel.collectAsStateWithLifecycle()

    val board by viewModel.board.collectAsStateWithLifecycle()
    val moves by viewModel.moves.collectAsStateWithLifecycle()
    val timeElapsedMillis by viewModel.timeElapsedMillis.collectAsStateWithLifecycle()
    val hintIndex by viewModel.hintIndex.collectAsStateWithLifecycle()
    val isPeeking by viewModel.isPeeking.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
    val bestRecord by viewModel.bestRecord.collectAsStateWithLifecycle()

    // Modals and dedicated sections
    val isLevelsSectionOpen by viewModel.isLevelsSectionOpen.collectAsStateWithLifecycle()
    val isInstructionsSectionOpen by viewModel.isInstructionsSectionOpen.collectAsStateWithLifecycle()
    val isMenuOpen by viewModel.isMenuOpen.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isLeaderboardOpen by viewModel.isLeaderboardOpen.collectAsStateWithLifecycle()
    val leaderboardFilter by viewModel.leaderboardFilter.collectAsStateWithLifecycle()
    val isShowingOnlyUserScores by viewModel.isShowingOnlyUserScores.collectAsStateWithLifecycle()
    val leaderboardScores by viewModel.leaderboardScores.collectAsStateWithLifecycle()

    val isSoundOn by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val soundVolumePercent by viewModel.volumePercent.collectAsStateWithLifecycle()
    val isVibrationOn by viewModel.isVibrationEnabled.collectAsStateWithLifecycle()
    val isAnimationsOn by viewModel.isAnimationsEnabled.collectAsStateWithLifecycle()

    val isVictoryDialogOpen by viewModel.isVictoryDialogOpen.collectAsStateWithLifecycle()
    val victoryScore by viewModel.victoryScore.collectAsStateWithLifecycle()
    val victoryStars by viewModel.victoryStars.collectAsStateWithLifecycle()
    val lastPlayerName by viewModel.lastPlayerName.collectAsStateWithLifecycle()
    val isScoreSubmitted by viewModel.isScoreSubmitted.collectAsStateWithLifecycle()

    WoodPlankBackground {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                contentAlignment = Alignment.Center
            ) {
                // Fixed, Non-scrolling Game Viewport
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 480.dp)
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // 1. Top Arched Sculpted Wooden Bar (Undo, MOVES, LEVEL 1, Hourglass, Parchment, Menu)
                    WoodHeaderWidget(
                        moves = moves,
                        levelNumber = currentLevelNumber,
                        timeElapsedMillis = timeElapsedMillis,
                        bestRecord = bestRecord,
                        onUndoClick = {
                            if (canUndo) viewModel.undoMove()
                        },
                        onLevelClick = { viewModel.openLevelsSection() },
                        onRecordClick = { viewModel.openLeaderboard(difficulty) },
                        onMenuClick = { viewModel.openMenu() }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 2. Center Wooden Board Frame with Chunky Wooden & Metal Tiles
                    Box(
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        WoodPuzzleBoard(
                            board = board,
                            gridSize = difficulty.gridSize,
                            hintIndex = hintIndex,
                            isPeeking = isPeeking,
                            onTileClick = { index -> viewModel.onTileClick(index) }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. Bottom 4 Circular Wooden Buttons with Decorative Foliage
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        // Soft botanical leaf sprig in the bottom-left corner
                        BotanicalFoliageSprig(
                            modifier = Modifier
                                .offset(x = (-6).dp, y = 14.dp)
                                .scale(1.1f)
                        )

                        // 4 Interactive Wooden Action Buttons (💡, ↻, 🔍, ⏭)
                        WoodBottomControls(
                            onHintClick = { viewModel.requestHint() },
                            onRestartClick = { viewModel.restartCurrentLevel() },
                            onPeekClick = { viewModel.setPeeking(!isPeeking) },
                            onNextLevelClick = { viewModel.playNextLevel() }
                        )
                    }
                }

                // --- DEDICATED OVERLAY SECTIONS ---

                // Levels & Stages Section Modal
                LevelsSectionDialog(
                    isOpen = isLevelsSectionOpen,
                    currentDifficulty = difficulty,
                    currentLevelNumber = currentLevelNumber,
                    maxUnlockedLevel = maxUnlockedLevel,
                    onSelectDifficulty = { diff -> viewModel.selectDifficulty(diff) },
                    onSelectLevel = { lvl -> viewModel.selectLevel(lvl) },
                    onDismiss = { viewModel.closeLevelsSection() }
                )

                // How to Play & Instructions Modal
                HowToPlayDialog(
                    isOpen = isInstructionsSectionOpen,
                    onStartGame = { viewModel.closeInstructionsSection() },
                    onDismiss = { viewModel.closeInstructionsSection() }
                )

                // Top Options Menu Modal
                GameMenuDialog(
                    isOpen = isMenuOpen,
                    onOpenLevels = { viewModel.openLevelsSection() },
                    onOpenInstructions = { viewModel.openInstructionsSection() },
                    onOpenLeaderboard = { viewModel.openLeaderboard(difficulty) },
                    onOpenSettings = { viewModel.openSettings() },
                    onRestartLevel = {
                        viewModel.restartCurrentLevel()
                        viewModel.closeMenu()
                    },
                    onDismiss = { viewModel.closeMenu() }
                )

                // Settings Sidebar (with Horizontal Line Volume Control & Recommended 80% Notch)
                SettingsSidebar(
                    isOpen = isSettingsOpen,
                    isSoundOn = isSoundOn,
                    volumePercent = soundVolumePercent,
                    isVibrationOn = isVibrationOn,
                    isAnimationsOn = isAnimationsOn,
                    onToggleSound = { viewModel.toggleSound() },
                    onVolumeChanged = { vol -> viewModel.setSoundVolume(vol) },
                    onToggleVibration = { viewModel.toggleHaptics() },
                    onToggleAnimations = { viewModel.setAnimationsEnabled(!isAnimationsOn) },
                    onOpenHowToPlay = {
                        viewModel.closeSettings()
                        viewModel.openInstructionsSection()
                    },
                    onClose = { viewModel.closeSettings() }
                )

                // Leaderboard Dialog
                LeaderboardDialog(
                    isOpen = isLeaderboardOpen,
                    activeDifficulty = leaderboardFilter,
                    isShowingOnlyUserScores = isShowingOnlyUserScores,
                    scores = leaderboardScores,
                    onFilterChange = { diff -> viewModel.setLeaderboardFilter(diff) },
                    onToggleUserScores = { viewModel.toggleUserScoresOnly() },
                    onDismiss = { viewModel.closeLeaderboard() }
                )

                // Victory Dialog
                VictoryDialog(
                    isOpen = isVictoryDialogOpen,
                    difficulty = difficulty,
                    currentLevelNumber = currentLevelNumber,
                    timeElapsedMillis = timeElapsedMillis,
                    moves = moves,
                    score = victoryScore,
                    stars = victoryStars,
                    initialPlayerName = lastPlayerName,
                    isScoreSubmitted = isScoreSubmitted,
                    onSubmitScore = { name -> viewModel.submitVictoryScore(name) },
                    onPlayAgain = { viewModel.restartCurrentLevel() },
                    onNextLevel = { viewModel.playNextLevel() },
                    onViewLeaderboard = {
                        viewModel.dismissVictoryDialog()
                        viewModel.openLeaderboard(difficulty)
                    },
                    onDismiss = { viewModel.dismissVictoryDialog() }
                )
            }
        }
    }
}
