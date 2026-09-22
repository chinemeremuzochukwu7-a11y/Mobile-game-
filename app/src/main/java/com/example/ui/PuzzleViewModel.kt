package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ads.AdMobHelper
import com.example.billing.GoogleBillingHelper
import com.example.data.db.AppDatabase
import com.example.data.db.LeaderboardEntity
import com.example.data.model.Difficulty
import com.example.data.model.Level
import com.example.data.model.LevelCatalog
import com.example.data.model.TileTheme
import com.example.data.repository.LeaderboardRepository
import com.example.game.FeedbackManager
import com.example.game.PuzzleEngine
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class GameState {
    IDLE,
    PLAYING,
    PAUSED,
    SOLVED
}

class PuzzleViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("puzzle_master_prefs", Context.MODE_PRIVATE)
    private val repository: LeaderboardRepository
    val feedbackManager: FeedbackManager = FeedbackManager(application)
    val adMobHelper: AdMobHelper = AdMobHelper(application)
    val billingHelper: GoogleBillingHelper = GoogleBillingHelper(application)

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = LeaderboardRepository(database.leaderboardDao())
        adMobHelper.initialize()
        billingHelper.initialize()
    }

    // App Initial Splash Loading (1 to 100%)
    private val _isSplashFinished = MutableStateFlow(false)
    val isSplashFinished: StateFlow<Boolean> = _isSplashFinished.asStateFlow()

    // Settings Sidebar state
    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Audio & Vibration & Animation states persisted
    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("is_sound_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _volumePercent = MutableStateFlow(prefs.getInt("sound_volume_percent", 80))
    val volumePercent: StateFlow<Int> = _volumePercent.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(prefs.getBoolean("is_vibration_enabled", true))
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _isAnimationsEnabled = MutableStateFlow(prefs.getBoolean("is_animations_enabled", true))
    val isAnimationsEnabled: StateFlow<Boolean> = _isAnimationsEnabled.asStateFlow()

    private val _difficulty = MutableStateFlow(Difficulty.EASY)
    val difficulty: StateFlow<Difficulty> = _difficulty.asStateFlow()

    private val _currentLevelNumber = MutableStateFlow(1)
    val currentLevelNumber: StateFlow<Int> = _currentLevelNumber.asStateFlow()

    private val _maxUnlockedLevel = MutableStateFlow(
        prefs.getInt("max_unlocked_level_${Difficulty.EASY.name}", 1)
    )
    val maxUnlockedLevel: StateFlow<Int> = _maxUnlockedLevel.asStateFlow()

    // First time onboarding dialog: True on fresh install
    private val _isHowToPlayOpen = MutableStateFlow(
        prefs.getBoolean("is_first_install", true)
    )
    val isHowToPlayOpen: StateFlow<Boolean> = _isHowToPlayOpen.asStateFlow()

    // Level 1 tutorial recognition tracking
    private val _isTutorialRecognized = MutableStateFlow(false)
    val isTutorialRecognized: StateFlow<Boolean> = _isTutorialRecognized.asStateFlow()

    private val _board = MutableStateFlow<List<Int>>(emptyList())
    val board: StateFlow<List<Int>> = _board.asStateFlow()

    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> = _moves.asStateFlow()

    private val _timeElapsedMillis = MutableStateFlow(0L)
    val timeElapsedMillis: StateFlow<Long> = _timeElapsedMillis.asStateFlow()

    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _tileTheme = MutableStateFlow(
        try {
            TileTheme.valueOf(prefs.getString("selected_tile_theme", Difficulty.EASY.defaultTheme().name) ?: Difficulty.EASY.defaultTheme().name)
        } catch (_: Exception) {
            Difficulty.EASY.defaultTheme()
        }
    )
    val tileTheme: StateFlow<TileTheme> = _tileTheme.asStateFlow()

    private val _hintIndex = MutableStateFlow<Int?>(null)
    val hintIndex: StateFlow<Int?> = _hintIndex.asStateFlow()

    private val _isPeeking = MutableStateFlow(false)
    val isPeeking: StateFlow<Boolean> = _isPeeking.asStateFlow()

    private val _isLeaderboardOpen = MutableStateFlow(false)
    val isLeaderboardOpen: StateFlow<Boolean> = _isLeaderboardOpen.asStateFlow()

    private val _leaderboardFilter = MutableStateFlow(Difficulty.MEDIUM)
    val leaderboardFilter: StateFlow<Difficulty> = _leaderboardFilter.asStateFlow()

    private val _isShowingOnlyUserScores = MutableStateFlow(false)
    val isShowingOnlyUserScores: StateFlow<Boolean> = _isShowingOnlyUserScores.asStateFlow()

    private val _isVictoryDialogOpen = MutableStateFlow(false)
    val isVictoryDialogOpen: StateFlow<Boolean> = _isVictoryDialogOpen.asStateFlow()

    private val _victoryScore = MutableStateFlow(0)
    val victoryScore: StateFlow<Int> = _victoryScore.asStateFlow()

    private val _victoryStars = MutableStateFlow(0)
    val victoryStars: StateFlow<Int> = _victoryStars.asStateFlow()

    private val _lastPlayerName = MutableStateFlow("Player 1")
    val lastPlayerName: StateFlow<String> = _lastPlayerName.asStateFlow()

    private val _isScoreSubmitted = MutableStateFlow(false)
    val isScoreSubmitted: StateFlow<Boolean> = _isScoreSubmitted.asStateFlow()

    // Loading state for interstitial/rewarded ad fetching transitions
    private val _isAdLoading = MutableStateFlow(false)
    val isAdLoading: StateFlow<Boolean> = _isAdLoading.asStateFlow()

    private val _adLoadingMessage = MutableStateFlow("Loading next challenge...")
    val adLoadingMessage: StateFlow<String> = _adLoadingMessage.asStateFlow()

    private val undoStack = mutableListOf<List<Int>>()
    private val _canUndo = MutableStateFlow(false)
    val canUndo: StateFlow<Boolean> = _canUndo.asStateFlow()

    private var timerJob: Job? = null

    // Reactive best score for the current difficulty
    val bestRecord: StateFlow<LeaderboardEntity?> = _difficulty
        .flatMapLatest { diff -> repository.getBestUserScore(diff) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Reactive leaderboard scores filtered by tab and user toggle
    val leaderboardScores: StateFlow<List<LeaderboardEntity>> = combine(
        _leaderboardFilter,
        _isShowingOnlyUserScores
    ) { filter, onlyUser ->
        Pair(filter, onlyUser)
    }.flatMapLatest { (filter, onlyUser) ->
        if (onlyUser) {
            repository.getUserScores()
        } else {
            repository.getScoresByDifficulty(filter)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        startLevel(_currentLevelNumber.value)
    }

    fun selectDifficulty(newDifficulty: Difficulty) {
        if (_difficulty.value != newDifficulty) {
            _difficulty.value = newDifficulty
            _leaderboardFilter.value = newDifficulty
            val savedMax = prefs.getInt("max_unlocked_level_${newDifficulty.name}", 1)
            _maxUnlockedLevel.value = savedMax
            _currentLevelNumber.value = 1
            _isTutorialRecognized.value = false
            // Switch visual theme to the difficulty's tailored theme
            val themeForDifficulty = newDifficulty.defaultTheme()
            _tileTheme.value = themeForDifficulty
            prefs.edit().putString("selected_tile_theme", themeForDifficulty.name).apply()
            startLevel(1)
        }
    }

    fun selectLevel(levelNumber: Int) {
        if (levelNumber <= _maxUnlockedLevel.value) {
            _currentLevelNumber.value = levelNumber
            startLevel(levelNumber)
            feedbackManager.playClick()
        }
    }

    fun startLevel(levelNumber: Int) {
        timerJob?.cancel()
        _moves.value = 0
        _timeElapsedMillis.value = 0L
        _gameState.value = GameState.IDLE
        _hintIndex.value = null
        _isVictoryDialogOpen.value = false
        _isScoreSubmitted.value = false
        undoStack.clear()
        _canUndo.value = false

        val level = LevelCatalog.getLevel(_difficulty.value, levelNumber)
        val newBoard = PuzzleEngine.generateBoardForLevel(_difficulty.value, level.shuffleSteps)
        _board.value = newBoard

        // For Level 1, automatically highlight initial hint to guide the player
        if (_difficulty.value == Difficulty.EASY && levelNumber == 1) {
            _isTutorialRecognized.value = false
            val firstHint = PuzzleEngine.findHintMove(newBoard, _difficulty.value.gridSize)
            _hintIndex.value = firstHint
        }
    }

    fun startNewGame() {
        startLevel(_currentLevelNumber.value)
    }

    fun onTileClick(index: Int) {
        if (_gameState.value == GameState.SOLVED || _gameState.value == GameState.PAUSED) return

        val currentBoard = _board.value
        val nextBoard = PuzzleEngine.tryMove(currentBoard, index, _difficulty.value.gridSize) ?: return

        // Push to undo stack
        undoStack.add(currentBoard)
        _canUndo.value = true

        _board.value = nextBoard
        _moves.value += 1
        _hintIndex.value = null
        feedbackManager.playMoveFeedback()

        // Check if user is on Level 1 tutorial and moved the tile successfully
        if (_difficulty.value == Difficulty.EASY && _currentLevelNumber.value == 1 && !_isTutorialRecognized.value) {
            _isTutorialRecognized.value = true
        }

        if (_gameState.value == GameState.IDLE) {
            _gameState.value = GameState.PLAYING
            startTimer()
        }

        // Check victory
        if (PuzzleEngine.isSolved(nextBoard)) {
            handleVictory()
        }
    }

    fun completeTutorialAndPlay() {
        _isTutorialRecognized.value = true
        feedbackManager.playClick()
    }

    fun closeHowToPlayDialog() {
        _isHowToPlayOpen.value = false
        prefs.edit().putBoolean("is_first_install", false).apply()
        feedbackManager.playClick()
    }

    fun openHowToPlayDialog() {
        _isHowToPlayOpen.value = true
        feedbackManager.playClick()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - _timeElapsedMillis.value
            while (isActive && _gameState.value == GameState.PLAYING) {
                delay(100)
                _timeElapsedMillis.value = System.currentTimeMillis() - startTime
            }
        }
    }

    fun pauseGame() {
        if (_gameState.value == GameState.PLAYING) {
            _gameState.value = GameState.PAUSED
            timerJob?.cancel()
        }
    }

    fun resumeGame() {
        if (_gameState.value == GameState.PAUSED) {
            _gameState.value = GameState.PLAYING
            startTimer()
        }
    }

    fun togglePause() {
        if (_gameState.value == GameState.PLAYING) {
            pauseGame()
        } else if (_gameState.value == GameState.PAUSED) {
            resumeGame()
        }
    }

    fun undoMove() {
        if (undoStack.isNotEmpty() && _gameState.value != GameState.SOLVED) {
            val previousBoard = undoStack.removeAt(undoStack.size - 1)
            _board.value = previousBoard
            _canUndo.value = undoStack.isNotEmpty()
            _moves.value = maxOf(0, _moves.value - 1)
            feedbackManager.playClick()
        }
    }

    fun requestHint() {
        if (_gameState.value == GameState.SOLVED || _gameState.value == GameState.PAUSED) return
        val hint = PuzzleEngine.findHintMove(_board.value, _difficulty.value.gridSize)
        _hintIndex.value = hint
        feedbackManager.playClick()
    }

    fun setPeeking(peek: Boolean) {
        _isPeeking.value = peek
    }

    fun setTileTheme(theme: TileTheme) {
        _tileTheme.value = theme
        prefs.edit().putString("selected_tile_theme", theme.name).apply()
        feedbackManager.playClick()
    }

    private fun handleVictory() {
        _gameState.value = GameState.SOLVED
        timerJob?.cancel()
        feedbackManager.playVictoryFeedback()

        val seconds = (_timeElapsedMillis.value / 1000).toInt()
        val score = _difficulty.value.calculateScore(_moves.value, seconds)
        val stars = _difficulty.value.calculateStars(_moves.value, seconds)

        // Unlock next level if currently completed level was the furthest
        val currentLvl = _currentLevelNumber.value
        if (currentLvl >= _maxUnlockedLevel.value && currentLvl < 10) {
            val nextLvl = currentLvl + 1
            _maxUnlockedLevel.value = nextLvl
            prefs.edit().putInt("max_unlocked_level_${_difficulty.value.name}", nextLvl).apply()
        }

        _victoryScore.value = score
        _victoryStars.value = stars
        _isScoreSubmitted.value = false
        _isVictoryDialogOpen.value = true
    }

    fun playNextLevel() {
        val nextLevel = _currentLevelNumber.value + 1
        _isVictoryDialogOpen.value = false
        _isAdLoading.value = false

        if (nextLevel <= 10) {
            _currentLevelNumber.value = nextLevel
            startLevel(nextLevel)
        } else {
            startLevel(_currentLevelNumber.value)
        }
    }

    fun setAdLoading(loading: Boolean, message: String = "Loading sponsor ad...") {
        _adLoadingMessage.value = message
        _isAdLoading.value = loading
        if (loading) {
            // Safety watchdog: prevent ad overlay from hanging if network drops or SDK is slow in emulator
            viewModelScope.launch {
                delay(3000)
                if (_isAdLoading.value) {
                    _isAdLoading.value = false
                }
            }
        }
    }

    fun submitVictoryScore(playerName: String) {
        if (_isScoreSubmitted.value) return
        val trimmed = playerName.trim().ifEmpty { "Player" }
        _lastPlayerName.value = trimmed

        viewModelScope.launch {
            repository.submitScore(
                playerName = trimmed,
                difficulty = _difficulty.value,
                timeMillis = _timeElapsedMillis.value,
                moves = _moves.value
            )
            _isScoreSubmitted.value = true
        }
    }

    fun openLeaderboard(tabDifficulty: Difficulty? = null) {
        tabDifficulty?.let { _leaderboardFilter.value = it }
        _isLeaderboardOpen.value = true
        feedbackManager.playClick()
    }

    fun closeLeaderboard() {
        _isLeaderboardOpen.value = false
    }

    fun setLeaderboardFilter(difficulty: Difficulty) {
        _leaderboardFilter.value = difficulty
        feedbackManager.playClick()
    }

    fun toggleUserScoresOnly() {
        _isShowingOnlyUserScores.value = !_isShowingOnlyUserScores.value
        feedbackManager.playClick()
    }

    fun dismissVictoryDialog() {
        _isVictoryDialogOpen.value = false
    }

    fun finishSplashLoading() {
        _isSplashFinished.value = true
        feedbackManager.playStartupChime()
    }

    fun openSettings() {
        _isSettingsOpen.value = true
        feedbackManager.playClick()
    }

    fun closeSettings() {
        _isSettingsOpen.value = false
    }

    fun setSoundEnabled(enabled: Boolean) {
        _isSoundEnabled.value = enabled
        feedbackManager.isSoundEnabled = enabled
        prefs.edit().putBoolean("is_sound_enabled", enabled).apply()
        if (enabled) {
            feedbackManager.playClick()
        }
    }

    fun setSoundVolume(volume: Int) {
        val clamped = volume.coerceIn(0, 100)
        _volumePercent.value = clamped
        feedbackManager.volumePercent = clamped
        prefs.edit().putInt("sound_volume_percent", clamped).apply()
    }

    fun setVibrationEnabled(enabled: Boolean) {
        _isVibrationEnabled.value = enabled
        feedbackManager.isHapticsEnabled = enabled
        prefs.edit().putBoolean("is_vibration_enabled", enabled).apply()
        if (enabled) {
            feedbackManager.playClick()
        }
    }

    fun setAnimationsEnabled(enabled: Boolean) {
        _isAnimationsEnabled.value = enabled
        feedbackManager.isAnimationsEnabled = enabled
        prefs.edit().putBoolean("is_animations_enabled", enabled).apply()
        feedbackManager.playClick()
    }

    fun toggleSound() {
        setSoundEnabled(!_isSoundEnabled.value)
    }

    fun toggleHaptics() {
        setVibrationEnabled(!_isVibrationEnabled.value)
    }

    override fun onCleared() {
        super.onCleared()
        feedbackManager.release()
    }
}
