package com.example

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.*
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    private val viewModel: CalculatorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by viewModel.themeMode.collectAsState()
            MyApplicationTheme(themeMode = themeMode) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.safeDrawing),
                    containerColor = MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    CalculatorScreen(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    modifier: Modifier = Modifier
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val isTablet = configuration.screenWidthDp >= 600

    val expression by viewModel.expression.collectAsState()
    val liveResult by viewModel.liveResult.collectAsState()
    val isScientificExpanded by viewModel.isScientificExpanded.collectAsState()
    val isDegreeMode by viewModel.isDegreeMode.collectAsState()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Live clock for Nothing UI status bar element
    var sysTime by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            sysTime = sdf.format(Date())
            kotlinx.coroutines.delay(10000)
        }
    }

    // Toggle Dialogs trigger
    if (showSettingsDialog) {
        NothingSettingsDialog(
            onDismiss = { showSettingsDialog = false },
            viewModel = viewModel
        )
    }

    if (showHistoryDialog) {
        NothingHistoryDialog(
            onDismiss = { showHistoryDialog = false },
            viewModel = viewModel
        )
    }

    if (showThemeDialog) {
        NothingThemeDialog(
            onDismiss = { showThemeDialog = false },
            viewModel = viewModel
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // TOP STATUS BAR (Nothing OS Style)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Programmatic dot-matrix clock
            if (sysTime.isNotEmpty()) {
                DotMatrixText(
                    text = sysTime,
                    dotSize = 1.2.dp,
                    dotSpacing = 0.4.dp,
                    activeColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.9f)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // DEG vs RAD slider button in standard Nothing pills
            Row(
                modifier = Modifier
                    .border(BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)), RoundedCornerShape(24.dp))
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.03f))
                    .clickable { viewModel.toggleAngleMode() }
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isDegreeMode) "DEG" else "RAD",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(NothingRed, CircleShape) // Red dot detail
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 3-dot menu button to trigger dialog settings
            Box {
                IconButton(
                    onClick = { dropdownExpanded = true },
                    modifier = Modifier.testTag("menu_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options menu",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                DropdownMenu(
                    expanded = dropdownExpanded,
                    onDismissRequest = { dropdownExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                "HISTORY",
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            dropdownExpanded = false
                            showHistoryDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "SETTINGS",
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            dropdownExpanded = false
                            showSettingsDialog = true
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                "THEME",
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp
                            )
                        },
                        onClick = {
                            dropdownExpanded = false
                            showThemeDialog = true
                        }
                    )
                }
            }
        }

        // OUTPUT DISPLAY AREA
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            // Formula Input text (with implicit parentheses closes)
            Text(
                text = expression.ifEmpty { "0" },
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                fontSize = if (expression.length > 20) 18.sp else 24.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.End,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .testTag("expression_display")
            )

            // Huge result text (auto-adjust fits or preview mode)
            val activeResult = when {
                liveResult.isNotEmpty() -> liveResult
                expression.isEmpty() -> "0"
                else -> ""
            }

            if (activeResult.isNotEmpty()) {
                AutoScaleText(
                    text = activeResult,
                    style = TextStyle(
                        fontFamily = FontFamily.Serif, // Classy Serif typography
                        fontWeight = FontWeight.Light,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.End
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("result_display")
                )
            }
        }

        // SLIDER TOGGLE ARROW (Only shown in Portrait Mobile because landscape has both side-by-side)
        if (!isLandscape && !isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                // Clicking this toggles additional buttons
                val rotationAngle by animateFloatAsState(
                    targetValue = if (isScientificExpanded) 180f else 0f,
                    animationSpec = spring(stiffness = 200f)
                )

                IconButton(
                    onClick = { viewModel.toggleScientific() },
                    modifier = Modifier.testTag("science_toggle")
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Toggle scientific functions",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.rotate(rotationAngle)
                    )
                }
            }
        }

        // BUTTONS INTERACTIVE KEYPAD
        // Adaptive layout is applied here:
        // - Wide screens (Tablet/Landscape) pin scientific panel to left and standard numbers to right side-by-side
        // - Portrait mobile reveals/collapses scientific panel depending on toggle state
        if (isLandscape || isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Left Panel: Scientific
                Box(modifier = Modifier.weight(0.45f)) {
                    ScientificKeypad(viewModel = viewModel)
                }
                // Right Panel: Standard Numbers
                Box(modifier = Modifier.weight(0.55f)) {
                    StandardKeypad(viewModel = viewModel)
                }
            }
        } else {
            // Mobile Portrait: standard and slide animated scientific
            Column(modifier = Modifier.fillMaxWidth()) {
                AnimatedVisibility(
                    visible = isScientificExpanded,
                    enter = expandVertically(spring()) + fadeIn(),
                    exit = shrinkVertically(spring()) + fadeOut()
                ) {
                    Box(modifier = Modifier.padding(bottom = 12.dp)) {
                        ScientificKeypad(viewModel = viewModel)
                    }
                }

                StandardKeypad(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AutoScaleText(
    text: String,
    style: TextStyle,
    modifier: Modifier = Modifier,
    minFontSize: TextUnit = 24.sp,
    maxFontSize: TextUnit = 56.sp
) {
    var fontSize by remember(text) { mutableStateOf(maxFontSize) }
    Text(
        text = text,
        style = style.copy(fontSize = fontSize),
        maxLines = 1,
        modifier = modifier,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.hasVisualOverflow) {
                val nextSize = (fontSize.value * 0.9f).sp
                if (nextSize >= minFontSize && fontSize != nextSize) {
                    fontSize = nextSize
                }
            }
        }
    )
}

@Composable
fun StandardKeypad(viewModel: CalculatorViewModel) {
    // 4 x 5 Grid mapping the exact layouts in the prompt
    val keys = listOf(
        "C", "+/-", "%", "÷",
        "1", "2", "3", "×",
        "4", "5", "6", "-",
        "7", "8", "9", "+",
        "←", "0", ".", "="
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
        userScrollEnabled = false
    ) {
        items(keys) { key ->
            val isOperator = key in listOf("C", "+/-", "%", "÷", "×", "-", "+", "=")
            CalculatorButton(
                text = key,
                backgroundColor = if (isOperator) NothingBlue else MaterialTheme.colorScheme.surfaceVariant,
                textColor = if (isOperator) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                onClick = { viewModel.onKeyPress(key) }
            )
        }
    }
}

@Composable
fun ScientificKeypad(viewModel: CalculatorViewModel) {
    // Scientific scientific calculations grid keys (3 columns x 5 rows)
    val scientificKeys = listOf(
        "sin", "cos", "tan",
        "asin", "acos", "atan",
        "ln", "log", "√",
        "π", "e", "^",
        "(", ")", "!"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
        userScrollEnabled = false
    ) {
        items(scientificKeys) { key ->
            // Outlined elegant monochrome buttons for scientific keys
            IconButtonScientific(
                text = key,
                onClick = { viewModel.onKeyPress(key) }
            )
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    // Buttons are forced circular using ratio constraints
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .aspectRatio(1f) // Keep guaranteed absolute 100% circles!
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick() }
            .testTag("btn_$text")
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = if (text.length > 2) 16.sp else 24.sp,
            fontFamily = FontFamily.Serif, // Beautiful slab-serif typography like prompt
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun IconButtonScientific(
    text: String,
    onClick: () -> Unit
) {
    // Scientific keys have subtle, thin borders and clean monospace typographies
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .height(50.dp)
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f)),
                shape = RoundedCornerShape(25.dp)
            )
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.02f))
            .clickable { onClick() }
            .testTag("science_btn_$text")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f),
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Normal
            )
            // Accent red dot exclusively on factorial or sqrt for that Nothing flair
            if (text == "!" || text == "√") {
                Spacer(modifier = Modifier.width(3.dp))
                Box(
                    modifier = Modifier
                        .size(3.dp)
                        .background(NothingRed, CircleShape)
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", color = Color.White, modifier = modifier)
}

