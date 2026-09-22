package com.example.ui

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.Difficulty
import com.example.data.model.TileTheme
import com.example.ads.AdMobBannerView
import com.example.ui.components.AdLoadingOverlay
import com.example.ui.components.DifficultySelector
import com.example.ui.components.GameControls
import com.example.ui.components.GameSplashScreen
import com.example.ui.components.HowToPlayDialog
import com.example.ui.components.LeaderboardDialog
import com.example.ui.components.LevelOneTutorialBanner
import com.example.ui.components.LevelSelectorBar
import com.example.ui.components.PuzzleBoard
import com.example.ui.components.SettingsSidebar
import com.example.ui.components.StatsBar
import com.example.ui.components.VictoryDialog
import androidx.compose.material.icons.filled.Settings
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.GoldTrophy
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PuzzleScreen(
    viewModel: PuzzleViewModel = viewModel()
) {
    val difficulty by viewModel.difficulty.collectAsStateWithLifecycle()
    val currentLevelNumber by viewModel.currentLevelNumber.collectAsStateWithLifecycle()
    val maxUnlockedLevel by viewModel.maxUnlockedLevel.collectAsStateWithLifecycle()
    val isHowToPlayOpen by viewModel.isHowToPlayOpen.collectAsStateWithLifecycle()
    val isTutorialRecognized by viewModel.isTutorialRecognized.collectAsStateWithLifecycle()

    val board by viewModel.board.collectAsStateWithLifecycle()
    val moves by viewModel.moves.collectAsStateWithLifecycle()
    val timeElapsedMillis by viewModel.timeElapsedMillis.collectAsStateWithLifecycle()
    val gameState by viewModel.gameState.collectAsStateWithLifecycle()
    val tileTheme by viewModel.tileTheme.collectAsStateWithLifecycle()
    val hintIndex by viewModel.hintIndex.collectAsStateWithLifecycle()
    val isPeeking by viewModel.isPeeking.collectAsStateWithLifecycle()
    val canUndo by viewModel.canUndo.collectAsStateWithLifecycle()
    val bestRecord by viewModel.bestRecord.collectAsStateWithLifecycle()

    val isLeaderboardOpen by viewModel.isLeaderboardOpen.collectAsStateWithLifecycle()
    val leaderboardFilter by viewModel.leaderboardFilter.collectAsStateWithLifecycle()
    val isShowingOnlyUserScores by viewModel.isShowingOnlyUserScores.collectAsStateWithLifecycle()
    val leaderboardScores by viewModel.leaderboardScores.collectAsStateWithLifecycle()

    val isVictoryDialogOpen by viewModel.isVictoryDialogOpen.collectAsStateWithLifecycle()
    val victoryScore by viewModel.victoryScore.collectAsStateWithLifecycle()
    val victoryStars by viewModel.victoryStars.collectAsStateWithLifecycle()
    val lastPlayerName by viewModel.lastPlayerName.collectAsStateWithLifecycle()
    val isScoreSubmitted by viewModel.isScoreSubmitted.collectAsStateWithLifecycle()
    val isAdLoading by viewModel.isAdLoading.collectAsStateWithLifecycle()
    val adLoadingMessage by viewModel.adLoadingMessage.collectAsStateWithLifecycle()

    val isSplashFinished by viewModel.isSplashFinished.collectAsStateWithLifecycle()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsStateWithLifecycle()
    val isSoundOn by viewModel.isSoundEnabled.collectAsStateWithLifecycle()
    val soundVolumePercent by viewModel.volumePercent.collectAsStateWithLifecycle()
    val isVibrationOn by viewModel.isVibrationEnabled.collectAsStateWithLifecycle()
    val isAnimationsOn by viewModel.isAnimationsEnabled.collectAsStateWithLifecycle()
    val isAdsRemoved by viewModel.billingHelper.isAdsRemoved.collectAsStateWithLifecycle()
    val billingMessage by viewModel.billingHelper.billingStatusMessage.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DeepNavy
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .statusBarsPadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 480.dp)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Top Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Logo & App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonIndigo)
                                .border(1.dp, NeonCyan, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Extension,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "Puzzle Master",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = TextPrimary
                            )
                            Text(
                                text = "Speed Slider Challenge",
                                style = MaterialTheme.typography.labelSmall,
                                color = NeonCyan,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Header Controls: How to play, Settings sidebar & Leaderboard
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // How to play / directions
                        IconButton(
                            onClick = { viewModel.openHowToPlayDialog() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceCardElevated)
                                .border(1.dp, SurfaceBorder, CircleShape)
                                .testTag("top_how_to_play_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.HelpOutline,
                                contentDescription = "How to Play",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Settings & Audio Sidebar button
                        IconButton(
                            onClick = { viewModel.openSettings() },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(SurfaceCardElevated)
                                .border(1.dp, SurfaceBorder, CircleShape)
                                .testTag("top_settings_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings and Audio",
                                tint = NeonCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Global Leaderboard Button
                        IconButton(
                            onClick = { viewModel.openLeaderboard(difficulty) },
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(GoldTrophy.copy(alpha = 0.15f))
                                .border(1.dp, GoldTrophy.copy(alpha = 0.6f), CircleShape)
                                .testTag("top_leaderboard_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Global Leaderboard",
                                tint = GoldTrophy,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Difficulty Selector (Easy 3x3, Medium 4x4, Hard 5x5)
                DifficultySelector(
                    selectedDifficulty = difficulty,
                    onDifficultySelected = { newDiff ->
                        viewModel.selectDifficulty(newDiff)
                    }
                )

                // Level Selector Bar (10 progressive stages per difficulty)
                LevelSelectorBar(
                    difficulty = difficulty,
                    currentLevelNumber = currentLevelNumber,
                    maxUnlockedLevel = maxUnlockedLevel,
                    onSelectLevel = { lvl -> viewModel.selectLevel(lvl) }
                )

                // Level 1 Tutorial Guidance & Recognition Banner
                if (difficulty == Difficulty.EASY && currentLevelNumber == 1) {
                    LevelOneTutorialBanner(
                        isTutorialRecognized = isTutorialRecognized,
                        onContinueAfterTutorial = { viewModel.completeTutorialAndPlay() }
                    )
                }

                // Stats Bar (Live Timer, Moves count, Best time, Pause)
                StatsBar(
                    timeElapsedMillis = timeElapsedMillis,
                    moves = moves,
                    gameState = gameState,
                    bestRecord = bestRecord,
                    onTogglePause = { viewModel.togglePause() }
                )

                // Interactive Puzzle Board
                PuzzleBoard(
                    board = board,
                    difficulty = difficulty,
                    gameState = gameState,
                    tileTheme = tileTheme,
                    hintIndex = hintIndex,
                    isPeeking = isPeeking,
                    onTileClick = { index -> viewModel.onTileClick(index) },
                    onResumeClick = { viewModel.resumeGame() }
                )

                // Game Controls (New Game, Undo, Hint, Solved Peek, Theme)
                GameControls(
                    canUndo = canUndo,
                    tileTheme = tileTheme,
                    isPeeking = isPeeking,
                    onNewGame = { viewModel.startNewGame() },
                    onUndo = { viewModel.undoMove() },
                    onHint = { viewModel.requestHint() },
                    onTogglePeek = { viewModel.setPeeking(!isPeeking) },
                    onCycleTheme = {
                        val allThemes = TileTheme.values()
                        val nextIndex = (tileTheme.ordinal + 1) % allThemes.size
                        viewModel.setTileTheme(allThemes[nextIndex])
                    }
                )

                // Google AdMob Banner (shown if ads not removed)
                if (!isAdsRemoved) {
                    AdMobBannerView(
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        val context = LocalContext.current
        val activity = context as? Activity

        // Settings & Audio Sidebar
        SettingsSidebar(
            isOpen = isSettingsOpen,
            onClose = { viewModel.closeSettings() },
            isSoundOn = isSoundOn,
            onToggleSound = { viewModel.setSoundEnabled(it) },
            volumePercent = soundVolumePercent,
            onVolumeChanged = { viewModel.setSoundVolume(it) },
            isVibrationOn = isVibrationOn,
            onToggleVibration = { viewModel.setVibrationEnabled(it) },
            isAnimationsOn = isAnimationsOn,
            onToggleAnimations = { viewModel.setAnimationsEnabled(it) },
            isHapticsOn = isVibrationOn,
            onToggleHaptics = { viewModel.setVibrationEnabled(it) },
            selectedTheme = tileTheme,
            onSelectTheme = { viewModel.setTileTheme(it) },
            isAdsRemoved = isAdsRemoved,
            onPurchaseRemoveAds = {
                activity?.let { act -> viewModel.billingHelper.purchaseRemoveAds(act) }
            },
            onPurchaseProPack = {
                activity?.let { act -> viewModel.billingHelper.purchaseProPack(act) }
            },
            onRestorePurchases = { viewModel.billingHelper.restorePurchases() },
            onOpenHowToPlay = {
                viewModel.closeSettings()
                viewModel.openHowToPlayDialog()
            },
            billingMessage = billingMessage
        )

        // How to Play / Directions Dialog
        HowToPlayDialog(
            isOpen = isHowToPlayOpen,
            onStartGame = {
                viewModel.closeHowToPlayDialog()
                viewModel.selectDifficulty(Difficulty.EASY)
                viewModel.selectLevel(1)
            },
            onDismiss = { viewModel.closeHowToPlayDialog() }
        )

        // Global Leaderboard Dialog
        LeaderboardDialog(
            isOpen = isLeaderboardOpen,
            activeDifficulty = leaderboardFilter,
            isShowingOnlyUserScores = isShowingOnlyUserScores,
            scores = leaderboardScores,
            onFilterChange = { newDiff -> viewModel.setLeaderboardFilter(newDiff) },
            onToggleUserScores = { viewModel.toggleUserScoresOnly() },
            onDismiss = { viewModel.closeLeaderboard() }
        )

        // Victory Celebration Dialog
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
            onPlayAgain = {
                viewModel.dismissVictoryDialog()
                viewModel.startNewGame()
            },
            onNextLevel = {
                if (!isAdsRemoved && activity != null) {
                    viewModel.setAdLoading(true, "Connecting next puzzle challenge...")
                    viewModel.adMobHelper.showInterstitial(activity) {
                        viewModel.setAdLoading(false)
                        viewModel.playNextLevel()
                    }
                } else {
                    viewModel.playNextLevel()
                }
            },
            onViewLeaderboard = {
                viewModel.dismissVictoryDialog()
                viewModel.openLeaderboard(difficulty)
            },
            onDismiss = { viewModel.dismissVictoryDialog() }
        )

        // Interstitial Ad Loading State Overlay (only in composition when active)
        if (isAdLoading) {
            AdLoadingOverlay(
                isLoading = true,
                message = adLoadingMessage,
                subMessage = "Synchronizing next level...",
                onDismiss = { viewModel.setAdLoading(false) }
            )
        }

        // App Initial 1 to 100 Splash Loader (only in composition before finish)
        if (!isSplashFinished) {
            GameSplashScreen(
                isCompleted = false,
                onFinishLoading = { viewModel.finishSplashLoading() }
            )
        }
    }
}
