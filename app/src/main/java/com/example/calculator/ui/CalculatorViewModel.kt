package com.example.calculator.ui

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.data.CalculationDao
import com.example.calculator.data.CalculationEntity
import com.example.calculator.data.CalculatorDatabase
import com.example.calculator.domain.ExpressionEvaluator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class CalculatorViewModel(application: Application) : AndroidViewModel(application) {

    private val db = CalculatorDatabase.getDatabase(application)
    private val dao = db.calculationDao()
    private val prefs = application.getSharedPreferences("calculator_prefs", Context.MODE_PRIVATE)

    // UI Input states
    private val _expression = MutableStateFlow("")
    val expression: StateFlow<String> = _expression.asStateFlow()

    private val _liveResult = MutableStateFlow("")
    val liveResult: StateFlow<String> = _liveResult.asStateFlow()

    // Panel states
    private val _isScientificExpanded = MutableStateFlow(false)
    val isScientificExpanded: StateFlow<Boolean> = _isScientificExpanded.asStateFlow()

    // Configuration / settings states
    private val _isDegreeMode = MutableStateFlow(true) // Deg by default
    val isDegreeMode: StateFlow<Boolean> = _isDegreeMode.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(prefs.getBoolean("haptics_enabled", true))
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _vibrationIntensity = MutableStateFlow(prefs.getString("vibration_intensity", "Medium") ?: "Medium") // "Weak", "Medium", "Strong"
    val vibrationIntensity: StateFlow<String> = _vibrationIntensity.asStateFlow()

    private val _decimalPrecision = MutableStateFlow(prefs.getInt("decimal_precision", 8)) // Default 8 places
    val decimalPrecision: StateFlow<Int> = _decimalPrecision.asStateFlow()

    // Theme states: "Light", "Dark", "System Default"
    private val _themeMode = MutableStateFlow(prefs.getString("theme_mode", "System Default") ?: "System Default")
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    // History log
    val historyList: StateFlow<List<CalculationEntity>> = dao.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Automatically compute expression whenever input expression or calculation mode changes
        _expression
            .combine(_isDegreeMode) { expr, isDeg -> expr to isDeg }
            .debounce(100)
            .onEach { (expr, isDeg) ->
                if (expr.isEmpty()) {
                    _liveResult.value = ""
                } else {
                    _liveResult.value = calculateLive(expr, isDeg)
                }
            }
            .launchIn(viewModelScope)
    }

    fun onKeyPress(key: String) {
        triggerFeedback()
        val current = _expression.value

        when (key) {
            "C" -> {
                _expression.value = ""
                _liveResult.value = ""
            }
            "←" -> {
                if (current.isNotEmpty()) {
                    // If it is a word function, backspace the entire word
                    val matchFunc = listOf("sin(", "cos(", "tan(", "asin(", "acos(", "atan(", "ln(", "log(", "sqrt(")
                    var deleted = false
                    for (func in matchFunc) {
                        if (current.endsWith(func)) {
                            _expression.value = current.dropLast(func.length)
                            deleted = true
                            break
                        }
                    }
                    if (!deleted) {
                        _expression.value = current.dropLast(1)
                    }
                }
            }
            "=" -> {
                evaluateResultAndSave()
            }
            "+/-" -> {
                if (current.isEmpty()) {
                    _expression.value = "(-"
                } else {
                    // Try to toggle negation on current number
                    if (current.endsWith("(-")) {
                        _expression.value = current.dropLast(2)
                    } else if (current.last() in "+-*/^(") {
                        _expression.value = current + "(-"
                    } else {
                        // Standard negation by pre-pending or attaching minus inside a parenthesis
                        _expression.value = current + "×(-"
                    }
                }
            }
            // Functions
            "sin", "cos", "tan", "asin", "acos", "atan", "ln", "log" -> {
                _expression.value = current + "$key("
            }
            "√" -> {
                _expression.value = current + "√("
            }
            else -> {
                // Formatting custom operator multipliers
                _expression.value = current + key
            }
        }
    }

    private fun calculateLive(expr: String, isDeg: Boolean): String {
        if (expr.isEmpty()) return ""
        return try {
            val evaluator = ExpressionEvaluator(isDeg)
            val res = evaluator.evaluate(expr)
            formatResult(res)
        } catch (e: Exception) {
            "" // Keep live output blank on partial syntaxes
        }
    }

    private fun evaluateResultAndSave() {
        val expr = _expression.value
        if (expr.isEmpty()) return

        val isDeg = _isDegreeMode.value
        try {
            val evaluator = ExpressionEvaluator(isDeg)
            val res = evaluator.evaluate(expr)
            val formatted = formatResult(res)

            _expression.value = formatted
            _liveResult.value = ""

            // Save to database asynchronously
            viewModelScope.launch {
                dao.insertCalculation(
                    CalculationEntity(
                        expression = expr,
                        result = formatted
                    )
                )
                // Keep it clean. Prune history to last 50 entries
                dao.pruneHistoryToLimit(50)
            }
        } catch (e: Exception) {
            _liveResult.value = "Error"
        }
    }

    private fun formatResult(value: Double): String {
        if (value.isNaN()) return "Error"
        if (value.isInfinite()) return "Overflow"

        // Ensure we don't display scientific notation for small integer values
        val intVal = value.toLong()
        if (value == intVal.toDouble()) {
            return intVal.toString()
        }

        // Decimal precision rounding
        val prec = _decimalPrecision.value
        val formatStr = "%.${prec}f".format(value).trimEnd('0').trimEnd('.')
        
        // If everything rounds to empty or zero, but value is not zero, fallback to scientific
        if ((formatStr == "0" || formatStr == "-0") && value != 0.0) {
            return "%.4e".format(value)
        }
        
        return formatStr
    }

    fun toggleScientific() {
        triggerFeedback()
        _isScientificExpanded.value = !_isScientificExpanded.value
    }

    fun setScientificExpanded(expanded: Boolean) {
        _isScientificExpanded.value = expanded
    }

    fun toggleAngleMode() {
        triggerFeedback()
        _isDegreeMode.value = !_isDegreeMode.value
    }

    fun setThemeMode(mode: String) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode).apply()
        triggerFeedback()
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        prefs.edit().putBoolean("haptics_enabled", enabled).apply()
        if (enabled) {
            triggerFeedback(force = true)
        }
    }

    fun setVibrationIntensity(intensity: String) {
        _vibrationIntensity.value = intensity
        prefs.edit().putString("vibration_intensity", intensity).apply()
        triggerFeedback(force = true)
    }

    fun setDecimalPrecision(precision: Int) {
        _decimalPrecision.value = precision
        prefs.edit().putInt("decimal_precision", precision).apply()
        triggerFeedback()
    }

    fun clearHistory() {
        triggerFeedback()
        viewModelScope.launch {
            dao.clearHistory()
        }
    }

    fun retrieveHistoryItem(item: CalculationEntity) {
        triggerFeedback()
        _expression.value = item.expression
    }

    // Direct hardware feedback simulation
    private fun triggerFeedback(force: Boolean = false) {
        if (!_hapticsEnabled.value && !force) return

        val context = getApplication<Application>().applicationContext
        val intensity = _vibrationIntensity.value
        val duration = when (intensity) {
            "Weak" -> 8L
            "Strong" -> 30L
            else -> 16L // Medium
        }
        val amplitude = when (intensity) {
            "Weak" -> 60
            "Strong" -> 230
            else -> 140 // Medium
        }

        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (vibrator != null && vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(duration, amplitude))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(duration)
                }
            }
        } catch (_: Exception) {
            // Safe fallback when permissions are not configured or vib engine fails
        }
    }
}
