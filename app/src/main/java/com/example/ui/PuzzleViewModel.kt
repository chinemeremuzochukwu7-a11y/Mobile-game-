package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = LeaderboardRepository(database.leaderboardDao())
    }

    // Direct landing on main game interface
    private val _isSplashFinished = MutableStateFlow(true)
    val isSplashFinished: StateFlow<Boolean> = _isSplashFinished.asStateFlow()

    // Dedicated Sections modals: Levels & Stages, Instructions, Settings, Top Menu
    private val _isLevelsSectionOpen = MutableStateFlow(false)
    val isLevelsSectionOpen: StateFlow<Boolean> = _isLevelsSectionOpen.asStateFlow()

    private val _isInstructionsSectionOpen = MutableStateFlow(false)
    val isInstructionsSectionOpen: StateFlow<Boolean> = _isInstructionsSectionOpen.asStateFlow()

    private val _isMenuOpen = MutableStateFlow(false)
    val isMenuOpen: StateFlow<Boolean> = _isMenuOpen.asStateFlow()

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

    private val _isTimerWarningActive = MutableStateFlow(false)
    val isTimerWarningActive: StateFlow<Boolean> = _isTimerWarningActive.asStateFlow()

    private val _board = MutableStateFlow<List<Int>>(emptyList())
    val board: StateFlow<List<Int>> = _board.asStateFlow()

    private val _moves = MutableStateFlow(0)
    val moves: StateFlow<Int> = _moves.asStateFlow()

    private val _timeElapsedMillis = MutableStateFlow(0L)
    val timeElapsedMillis: StateFlow<Long> = _timeElapsedMillis.asStateFlow()

    private val _gameState = MutableStateFlow(GameState.IDLE)
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private val _tileTheme = MutableStateFlow(TileTheme.CLASSIC_WOOD)
    val tileTheme: StateFlow<TileTheme> = _tileTheme.asStateFlow()

    private val _hintIndex = MutableStateFlow<Int?>(null)
    val hintIndex: StateFlow<Int?> = _hintIndex.asStateFlow()

    private val _isPeeking = MutableStateFlow(false)
    val isPeeking: StateFlow<Boolean> = _isPeeking.asStateFlow()

    private val _isLeaderboardOpen = MutableStateFlow(false)
    val isLeaderboardOpen: StateFlow<Boolean> = _isLeaderboardOpen.asStateFlow()

    private val _leaderboardFilter = MutableStateFlow(Difficulty.EASY)
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
        _isTimerWarningActive.value = false
        undoStack.clear()
        _canUndo.value = false

        val level = LevelCatalog.getLevel(_difficulty.value, levelNumber)
        val newBoard = PuzzleEngine.generateBoardForLevel(_difficulty.value, level.shuffleSteps)
        _board.value = newBoard

        // For Level 1, automatically compute initial hint move to guide player immediately
        if (_difficulty.value == Difficulty.EASY && levelNumber == 1) {
            val firstHint = PuzzleEngine.findHintMove(newBoard, _difficulty.value.gridSize)
            _hintIndex.value = firstHint
        }
    }

    fun restartCurrentLevel() {
        startLevel(_currentLevelNumber.value)
        feedbackManager.playClick()
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

        if (_gameState.value == GameState.IDLE) {
            _gameState.value = GameState.PLAYING
            startTimer()
        }

        // Check victory
        if (PuzzleEngine.isSolved(nextBoard)) {
            handleVictory()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            val startTime = System.currentTimeMillis() - _timeElapsedMillis.value
            val level = LevelCatalog.getLevel(_difficulty.value, _currentLevelNumber.value)
            val parMillis = level.parSeconds * 1000L
            val warningStartMillis = maxOf(5000L, parMillis - 10_000L)
            var lastWarningSecond = -1

            while (isActive && _gameState.value == GameState.PLAYING) {
                delay(100)
                val elapsed = System.currentTimeMillis() - startTime
                _timeElapsedMillis.value = elapsed

                val currentSecond = (elapsed / 1000).toInt()
                if (elapsed in warningStartMillis until parMillis) {
                    _isTimerWarningActive.value = true
                    if (currentSecond != lastWarningSecond) {
                        lastWarningSecond = currentSecond
                        feedbackManager.playTimerWarning()
                    }
                } else if (elapsed >= parMillis) {
                    _isTimerWarningActive.value = true
                    if (currentSecond % 5 == 0 && currentSecond != lastWarningSecond) {
                        lastWarningSecond = currentSecond
                        feedbackManager.playTimerWarning()
                    }
                } else {
                    _isTimerWarningActive.value = false
                }
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

    private fun handleVictory() {
        _gameState.value = GameState.SOLVED
        timerJob?.cancel()
        feedbackManager.playVictoryFeedback()

        val seconds = (_timeElapsedMillis.value / 1000).toInt()
        val score = _difficulty.value.calculateScore(_moves.value, seconds)
        val stars = _difficulty.value.calculateStars(_moves.value, seconds)

        val currentLvl = _currentLevelNumber.value
        val totalLevels = LevelCatalog.getLevelsForDifficulty(_difficulty.value).size
        if (currentLvl >= _maxUnlockedLevel.value && currentLvl < totalLevels) {
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
        val totalLevels = LevelCatalog.getLevelsForDifficulty(_difficulty.value).size
        _isVictoryDialogOpen.value = false

        if (nextLevel <= totalLevels) {
            _currentLevelNumber.value = nextLevel
            startLevel(nextLevel)
        } else {
            startLevel(_currentLevelNumber.value)
        }
        feedbackManager.playClick()
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

    // Modal section openers and closers
    fun openLevelsSection() {
        _isLevelsSectionOpen.value = true
        _isMenuOpen.value = false
        feedbackManager.playClick()
    }

    fun closeLevelsSection() {
        _isLevelsSectionOpen.value = false
    }

    fun openInstructionsSection() {
        _isInstructionsSectionOpen.value = true
        _isMenuOpen.value = false
        feedbackManager.playClick()
    }

    fun closeInstructionsSection() {
        _isInstructionsSectionOpen.value = false
    }

    fun openMenu() {
        _isMenuOpen.value = true
        feedbackManager.playClick()
    }

    fun closeMenu() {
        _isMenuOpen.value = false
    }

    fun openLeaderboard(tabDifficulty: Difficulty? = null) {
        tabDifficulty?.let { _leaderboardFilter.value = it }
        _isLeaderboardOpen.value = true
        _isMenuOpen.value = false
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

    fun openSettings() {
        _isSettingsOpen.value = true
        _isMenuOpen.value = false
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
        feedbackManager.playClick()
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
