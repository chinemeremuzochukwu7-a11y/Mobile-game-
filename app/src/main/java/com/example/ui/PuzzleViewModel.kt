package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.db.LeaderboardEntity
import com.example.data.model.Difficulty
import com.example.data.model.LevelCatalog
import com.example.data.model.TileTheme
import com.example.data.repository.LeaderboardRepository
import com.example.game.CoachState
import com.example.game.FeedbackManager
import com.example.game.GuidedHelpMove
import com.example.game.PuzzleCoach
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

    // Dedicated Sections modals: Levels & Stages, Instructions, Get Helped, Settings, Top Menu
    private val _isLevelsSectionOpen = MutableStateFlow(false)
    val isLevelsSectionOpen: StateFlow<Boolean> = _isLevelsSectionOpen.asStateFlow()

    private val _isInstructionsSectionOpen = MutableStateFlow(false)
    val isInstructionsSectionOpen: StateFlow<Boolean> = _isInstructionsSectionOpen.asStateFlow()

    private val _isHelpDialogOpen = MutableStateFlow(false)
    val isHelpDialogOpen: StateFlow<Boolean> = _isHelpDialogOpen.asStateFlow()

    private val _isMenuOpen = MutableStateFlow(false)
    val isMenuOpen: StateFlow<Boolean> = _isMenuOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Audio, Vibration, Animation, Zen Mode & Theme states persisted
    private val _isSoundEnabled = MutableStateFlow(prefs.getBoolean("is_sound_enabled", true))
    val isSoundEnabled: StateFlow<Boolean> = _isSoundEnabled.asStateFlow()

    private val _volumePercent = MutableStateFlow(prefs.getInt("sound_volume_percent", 80))
    val volumePercent: StateFlow<Int> = _volumePercent.asStateFlow()

    private val _isVibrationEnabled = MutableStateFlow(prefs.getBoolean("is_vibration_enabled", true))
    val isVibrationEnabled: StateFlow<Boolean> = _isVibrationEnabled.asStateFlow()

    private val _isAnimationsEnabled = MutableStateFlow(prefs.getBoolean("is_animations_enabled", true))
    val isAnimationsEnabled: StateFlow<Boolean> = _isAnimationsEnabled.asStateFlow()

    private val _isZenMode = MutableStateFlow(prefs.getBoolean("is_zen_mode", false))
    val isZenMode: StateFlow<Boolean> = _isZenMode.asStateFlow()

    private val _totalSolvedCount = MutableStateFlow(prefs.getInt("total_solved_count", 0))
    val totalSolvedCount: StateFlow<Int> = _totalSolvedCount.asStateFlow()

    private val _difficulty = MutableStateFlow(Difficulty.EASY)
    val difficulty: StateFlow<Difficulty> = _difficulty.asStateFlow()

    private val _currentLevelNumber = MutableStateFlow(1)
    val currentLevelNumber: StateFlow<Int> = _currentLevelNumber.asStateFlow()

    private val _maxUnlockedLevel = MutableStateFlow(
        prefs.getInt("max_unlocked_level_${Difficulty.EASY.name}", 1)
    )
    val maxUnlockedLevel: StateFlow<Int> = _maxUnlockedLevel.asStateFlow()

    private val _levelStarsMap = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val levelStarsMap: StateFlow<Map<Int, Int>> = _levelStarsMap.asStateFlow()

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

    private val _tileTheme = MutableStateFlow(loadSavedTheme())
    val tileTheme: StateFlow<TileTheme> = _tileTheme.asStateFlow()

    private val _hintIndex = MutableStateFlow<Int?>(null)
    val hintIndex: StateFlow<Int?> = _hintIndex.asStateFlow()

    private val _isGuidedModeActive = MutableStateFlow(false)
    val isGuidedModeActive: StateFlow<Boolean> = _isGuidedModeActive.asStateFlow()

    private val _guidedHelpMove = MutableStateFlow<GuidedHelpMove?>(null)
    val guidedHelpMove: StateFlow<GuidedHelpMove?> = _guidedHelpMove.asStateFlow()

    private val _coachState = MutableStateFlow(CoachState())
    val coachState: StateFlow<CoachState> = _coachState.asStateFlow()

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

    private var baselineManhattan: Int = 10
    private var lastEmptyIndex: Int? = null
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
        feedbackManager.isSoundEnabled = _isSoundEnabled.value
        feedbackManager.volumePercent = _volumePercent.value
        feedbackManager.isHapticsEnabled = _isVibrationEnabled.value
        feedbackManager.isAnimationsEnabled = _isAnimationsEnabled.value

        if (!restoreSavedSessionIfAvailable()) {
            loadLevelStarsForDifficulty(_difficulty.value)
            startLevel(_currentLevelNumber.value)
        }
    }

    private fun loadSavedTheme(): TileTheme {
        val savedName = prefs.getString("selected_tile_theme", TileTheme.CLASSIC_WOOD.name)
        return TileTheme.values().firstOrNull { it.name == savedName } ?: TileTheme.CLASSIC_WOOD
    }

    private fun loadLevelStarsForDifficulty(diff: Difficulty) {
        val levels = LevelCatalog.getLevelsForDifficulty(diff)
        val map = mutableMapOf<Int, Int>()
        for (lvl in levels) {
            val stars = prefs.getInt("level_stars_${diff.name}_${lvl.levelNumber}", 0)
            if (stars > 0) {
                map[lvl.levelNumber] = stars
            }
        }
        _levelStarsMap.value = map
    }

    fun selectDifficulty(newDifficulty: Difficulty) {
        if (_difficulty.value != newDifficulty) {
            _difficulty.value = newDifficulty
            _leaderboardFilter.value = newDifficulty
            val savedMax = prefs.getInt("max_unlocked_level_${newDifficulty.name}", 1)
            _maxUnlockedLevel.value = savedMax
            loadLevelStarsForDifficulty(newDifficulty)
            _currentLevelNumber.value = 1
            startLevel(1)
        }
    }

    /**
     * Allows the user to select and jump to ANY level freely (all levels accessible).
     */
    fun selectLevel(levelNumber: Int) {
        val total = LevelCatalog.getLevelsForDifficulty(_difficulty.value).size
        val target = levelNumber.coerceIn(1, total)
        _currentLevelNumber.value = target
        startLevel(target)
        feedbackManager.playClick()
    }

    fun startLevel(levelNumber: Int) {
        timerJob?.cancel()
        _moves.value = 0
        _timeElapsedMillis.value = 0L
        _gameState.value = GameState.IDLE
        _hintIndex.value = null
        _isGuidedModeActive.value = false
        _guidedHelpMove.value = null
        _isVictoryDialogOpen.value = false
        _isScoreSubmitted.value = false
        _isTimerWarningActive.value = false
        _isPeeking.value = false
        undoStack.clear()
        _canUndo.value = false
        lastEmptyIndex = null

        val level = LevelCatalog.getLevel(_difficulty.value, levelNumber)
        val newBoard = PuzzleEngine.generateBoardForLevel(_difficulty.value, level.shuffleSteps)
        _board.value = newBoard

        val gridSize = _difficulty.value.gridSize
        baselineManhattan = maxOf(1, PuzzleCoach.computeManhattanDistance(newBoard, gridSize))
        _coachState.value = PuzzleCoach.createInitialState(newBoard, gridSize, baselineManhattan)

        // For Level 1, automatically compute initial hint move to guide player immediately
        if (_difficulty.value == Difficulty.EASY && levelNumber == 1) {
            val firstHelp = PuzzleCoach.findGuidedHelp(newBoard, gridSize, lastEmptyIndex)
            _hintIndex.value = firstHelp?.tileIndex
            _guidedHelpMove.value = firstHelp
        }

        clearSavedActiveSession()
    }

    fun restartCurrentLevel() {
        startLevel(_currentLevelNumber.value)
        feedbackManager.playClick()
    }

    fun onTileClick(index: Int) {
        if (_gameState.value == GameState.SOLVED || _gameState.value == GameState.PAUSED) return

        val currentBoard = _board.value
        val gridSize = _difficulty.value.gridSize
        val nextBoard = PuzzleEngine.tryMove(currentBoard, index, gridSize) ?: return
        val movedTileValue = currentBoard.getOrNull(index) ?: 0

        // Push to undo stack
        undoStack.add(currentBoard)
        _canUndo.value = true
        lastEmptyIndex = currentBoard.indexOf(0)

        _board.value = nextBoard
        _moves.value += 1
        _hintIndex.value = null
        feedbackManager.playMoveFeedback()

        if (_gameState.value == GameState.IDLE) {
            _gameState.value = GameState.PLAYING
            startTimer()
        }

        // Update Live Coach Horizontal Bar
        _coachState.value = PuzzleCoach.evaluateMove(
            previousBoard = currentBoard,
            newBoard = nextBoard,
            movedTileValue = movedTileValue,
            gridSize = gridSize,
            baselineManhattan = baselineManhattan,
            previousOffTrackStreak = _coachState.value.offTrackStreak
        )

        // Check victory
        if (PuzzleEngine.isSolved(nextBoard)) {
            _isGuidedModeActive.value = false
            _guidedHelpMove.value = null
            handleVictory()
        } else {
            // Keep directing user if Active Guided Mode is turned on
            if (_isGuidedModeActive.value) {
                refreshGuidedDirection(nextBoard, gridSize)
            }
            saveActiveSession()
        }
    }

    private fun refreshGuidedDirection(boardState: List<Int>, gridSize: Int) {
        val nextHelp = PuzzleCoach.findGuidedHelp(boardState, gridSize, lastEmptyIndex)
        _guidedHelpMove.value = nextHelp
        _hintIndex.value = nextHelp?.tileIndex
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

                if (_isZenMode.value) {
                    _isTimerWarningActive.value = false
                    continue
                }

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
            saveActiveSession()
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
            lastEmptyIndex = null
            val gridSize = _difficulty.value.gridSize
            _coachState.value = PuzzleCoach.createInitialState(previousBoard, gridSize, baselineManhattan)
                .copy(message = "Coach: Move undone — try a new angle!")
            if (_isGuidedModeActive.value) {
                refreshGuidedDirection(previousBoard, gridSize)
            }
            feedbackManager.playClick()
            saveActiveSession()
        }
    }

    fun requestHint() {
        if (_gameState.value == GameState.SOLVED || _gameState.value == GameState.PAUSED) return
        val gridSize = _difficulty.value.gridSize
        val help = PuzzleCoach.findGuidedHelp(_board.value, gridSize, lastEmptyIndex)
        _guidedHelpMove.value = help
        _hintIndex.value = help?.tileIndex
        feedbackManager.playClick()
    }

    // --- GET HELPED / DIRECTING SYSTEM ---

    fun openHelpCenter() {
        if (_gameState.value != GameState.SOLVED) {
            _guidedHelpMove.value = PuzzleCoach.findGuidedHelp(
                _board.value,
                _difficulty.value.gridSize,
                lastEmptyIndex
            )
        }
        _isHelpDialogOpen.value = true
        _isMenuOpen.value = false
        feedbackManager.playClick()
    }

    fun closeHelpCenter() {
        _isHelpDialogOpen.value = false
    }

    fun startGuidedDirection() {
        if (_gameState.value == GameState.SOLVED) return
        val gridSize = _difficulty.value.gridSize
        val help = PuzzleCoach.findGuidedHelp(_board.value, gridSize, lastEmptyIndex)
        _guidedHelpMove.value = help
        _hintIndex.value = help?.tileIndex
        _isGuidedModeActive.value = help != null
        _isHelpDialogOpen.value = false
        feedbackManager.playClick()
    }

    fun stopGuidedDirection() {
        _isGuidedModeActive.value = false
        _hintIndex.value = null
        feedbackManager.playClick()
    }

    fun executeGuidedAutoStep() {
        if (_gameState.value == GameState.SOLVED) return
        val gridSize = _difficulty.value.gridSize
        val help = _guidedHelpMove.value ?: PuzzleCoach.findGuidedHelp(_board.value, gridSize, lastEmptyIndex)
        if (help != null) {
            _isGuidedModeActive.value = true
            onTileClick(help.tileIndex)
        }
    }

    fun setPeeking(peek: Boolean) {
        _isPeeking.value = peek
        feedbackManager.playClick()
    }

    private fun handleVictory() {
        _gameState.value = GameState.SOLVED
        timerJob?.cancel()
        clearSavedActiveSession()
        feedbackManager.playVictoryFeedback()

        val seconds = (_timeElapsedMillis.value / 1000).toInt()
        val score = _difficulty.value.calculateScore(_moves.value, seconds)
        val stars = _difficulty.value.calculateStars(_moves.value, seconds)

        val currentDiff = _difficulty.value
        val currentLvl = _currentLevelNumber.value
        val totalLevels = LevelCatalog.getLevelsForDifficulty(currentDiff).size
        if (currentLvl >= _maxUnlockedLevel.value && currentLvl < totalLevels) {
            val nextLvl = currentLvl + 1
            _maxUnlockedLevel.value = nextLvl
            prefs.edit().putInt("max_unlocked_level_${currentDiff.name}", nextLvl).apply()
        }

        // Persist best per-level stars & moves
        val starKey = "level_stars_${currentDiff.name}_$currentLvl"
        val prevStars = prefs.getInt(starKey, 0)
        if (stars > prevStars) {
            prefs.edit().putInt(starKey, stars).apply()
            loadLevelStarsForDifficulty(currentDiff)
        }

        val nextSolvedTotal = _totalSolvedCount.value + 1
        _totalSolvedCount.value = nextSolvedTotal
        prefs.edit().putInt("total_solved_count", nextSolvedTotal).apply()

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

    fun setZenMode(enabled: Boolean) {
        _isZenMode.value = enabled
        prefs.edit().putBoolean("is_zen_mode", enabled).apply()
        if (enabled) {
            _isTimerWarningActive.value = false
        }
        feedbackManager.playClick()
    }

    fun setTileTheme(theme: TileTheme) {
        _tileTheme.value = theme
        prefs.edit().putString("selected_tile_theme", theme.name).apply()
        feedbackManager.playClick()
    }

    fun toggleSound() {
        setSoundEnabled(!_isSoundEnabled.value)
    }

    fun toggleHaptics() {
        setVibrationEnabled(!_isVibrationEnabled.value)
    }

    // --- AUTO-SAVE & RESUME ACTIVE SESSION ---

    private fun saveActiveSession() {
        val currentBoard = _board.value
        if (currentBoard.isEmpty() || PuzzleEngine.isSolved(currentBoard)) {
            clearSavedActiveSession()
            return
        }
        prefs.edit()
            .putString("saved_board", currentBoard.joinToString(","))
            .putString("saved_difficulty", _difficulty.value.name)
            .putInt("saved_level", _currentLevelNumber.value)
            .putInt("saved_moves", _moves.value)
            .putLong("saved_time_ms", _timeElapsedMillis.value)
            .putInt("saved_baseline_manhattan", baselineManhattan)
            .apply()
    }

    private fun restoreSavedSessionIfAvailable(): Boolean {
        val boardStr = prefs.getString("saved_board", null) ?: return false
        val diffName = prefs.getString("saved_difficulty", null) ?: return false
        val diff = Difficulty.values().firstOrNull { it.name == diffName } ?: return false
        val parsed = boardStr.split(",").mapNotNull { it.toIntOrNull() }
        if (parsed.size != diff.totalTiles || PuzzleEngine.isSolved(parsed)) return false

        val savedLvl = prefs.getInt("saved_level", 1)
        val savedMoves = prefs.getInt("saved_moves", 0)
        val savedTime = prefs.getLong("saved_time_ms", 0L)
        val savedBaseline = prefs.getInt("saved_baseline_manhattan", 10).coerceAtLeast(1)

        _difficulty.value = diff
        _leaderboardFilter.value = diff
        _maxUnlockedLevel.value = prefs.getInt("max_unlocked_level_${diff.name}", 1)
        loadLevelStarsForDifficulty(diff)
        _currentLevelNumber.value = savedLvl
        _board.value = parsed
        _moves.value = savedMoves
        _timeElapsedMillis.value = savedTime
        baselineManhattan = savedBaseline
        _gameState.value = GameState.IDLE
        _coachState.value = PuzzleCoach.createInitialState(parsed, diff.gridSize, baselineManhattan)
        return true
    }

    private fun clearSavedActiveSession() {
        prefs.edit()
            .remove("saved_board")
            .remove("saved_difficulty")
            .remove("saved_level")
            .remove("saved_moves")
            .remove("saved_time_ms")
            .remove("saved_baseline_manhattan")
            .apply()
    }

    override fun onCleared() {
        super.onCleared()
        saveActiveSession()
        feedbackManager.release()
    }
}
