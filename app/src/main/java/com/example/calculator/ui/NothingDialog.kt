package com.example.calculator.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.platform.testTag
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.calculator.data.CalculationEntity

// Custom programmatic Dotted Divider mimicking NothingOS's iconic dot-matrix design
@Composable
fun DottedDivider(
    modifier: Modifier = Modifier,
    color: Color = Color.White.copy(alpha = 0.15f),
    dotRadius: Dp = 1.dp,
    spacing: Dp = 3.dp
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(4.dp)
    ) {
        val rPx = dotRadius.toPx()
        val sPx = spacing.toPx()
        val step = (rPx * 2) + sPx
        var currentX = 0f
        while (currentX < size.width) {
            drawCircle(
                color = color,
                radius = rPx,
                center = Offset(currentX + rPx, size.height / 2f)
            )
            currentX += step
        }
    }
}

@Composable
fun NothingSettingsDialog(
    onDismiss: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()
    val vibrationIntensity by viewModel.vibrationIntensity.collectAsState()
    val decimalPrecision by viewModel.decimalPrecision.collectAsState()
    val currentTheme by viewModel.themeMode.collectAsState()
    val isDark = when (currentTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val dialogBg = if (isDark) Color(0xFF121212) else Color(0xFFF2F2F7)
    val txtColor = if (isDark) Color.White else Color.Black
    val subTxtColor = if (isDark) Color.Gray else Color.DarkGray
    val strokeColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f)
    val buttonBg = if (isDark) Color.White else Color.Black
    val buttonTxtColor = if (isDark) Color.Black else Color.White

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.dp, strokeColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Title with iconic Red Accent dot
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    DotMatrixText(
                        text = "SETTINGS",
                        dotSize = 1.5.dp,
                        dotSpacing = 0.5.dp,
                        activeColor = txtColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    // Iconic red indicator dot (Nothing signature element)
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF0055), shape = CircleShape)
                    )
                }

                Text(
                    text = "Configure calculator features & haptics",
                    color = subTxtColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                DottedDivider(
                    color = strokeColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Item 1: Vibration Toggles
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HAPTIC FEEDBACK",
                            color = txtColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Vibrate on button clicks",
                            color = subTxtColor,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Switch(
                        checked = hapticsEnabled,
                        onCheckedChange = { viewModel.setHapticsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = if (isDark) Color.White else Color.Black,
                            checkedTrackColor = Color(0xFF3271F6), // Nothing Blue accent
                            uncheckedThumbColor = Color.Gray,
                            uncheckedTrackColor = if (isDark) Color.Black else Color.LightGray
                        )
                    )
                }

                if (hapticsEnabled) {
                    DottedDivider(
                        color = strokeColor,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    // Item 2: Haptic strength selection
                    Column(modifier = Modifier.padding(vertical = 12.dp)) {
                        Text(
                            text = "HAPTIC STRENGTH",
                            color = txtColor,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("Weak", "Medium", "Strong").forEach { strength ->
                                val isSelected = vibrationIntensity == strength
                                val itemBorderColor = if (isSelected) {
                                    if (isDark) Color.White else Color.Black
                                } else {
                                    if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)
                                }
                                val itemBg = if (isSelected) {
                                    if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)
                                } else {
                                    Color.Transparent
                                }
                                val itemTxtColor = if (isSelected) {
                                    if (isDark) Color.White else Color.Black
                                } else {
                                    Color.Gray
                                }
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .weight(1f)
                                        .border(
                                            BorderStroke(1.dp, itemBorderColor),
                                            shape = RoundedCornerShape(24.dp)
                                        )
                                        .background(itemBg, shape = RoundedCornerShape(24.dp))
                                        .clickable { viewModel.setVibrationIntensity(strength) }
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = strength.uppercase(),
                                        color = itemTxtColor,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }

                DottedDivider(
                    color = strokeColor,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Item 3: Rounding accuracy
                Column(modifier = Modifier.padding(vertical = 12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "DECIMAL ACCURACY",
                                color = txtColor,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = "Rounding precision limit",
                                color = subTxtColor,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        Text(
                            text = "$decimalPrecision PLACES",
                            color = Color(0xFF3271F6),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Slider(
                        value = decimalPrecision.toFloat(),
                        onValueChange = { viewModel.setDecimalPrecision(it.toInt()) },
                        valueRange = 2f..10f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            thumbColor = if (isDark) Color.White else Color.Black,
                            activeTrackColor = if (isDark) Color.White else Color.Black,
                            inactiveTrackColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)
                        ),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Close Pill button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBg,
                        contentColor = buttonTxtColor
                    ),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "CLOSE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun NothingHistoryDialog(
    onDismiss: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val historyList by viewModel.historyList.collectAsState()
    val currentTheme by viewModel.themeMode.collectAsState()
    val isDark = when (currentTheme) {
        "Light" -> false
        "Dark" -> true
        else -> isSystemInDarkTheme()
    }

    val dialogBg = if (isDark) Color(0xFF111111) else Color(0xFFF2F2F7)
    val txtColor = if (isDark) Color.White else Color.Black
    val subTxtColor = if (isDark) Color.Gray else Color.DarkGray
    val strokeColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.1f)
    val buttonBg = if (isDark) Color.White else Color.Black
    val buttonTxtColor = if (isDark) Color.Black else Color.White

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.dp, strokeColor),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxSize()
            ) {
                // Header with iconic Red Dot and Delete All Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DotMatrixText(
                        text = "HISTORY",
                        dotSize = 1.5.dp,
                        dotSpacing = 0.5.dp,
                        activeColor = txtColor
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    if (historyList.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearHistory() }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear all history",
                                tint = Color(0xFFFF0055) // Red tone for destructive clear
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(Color(0xFFFF0055), shape = CircleShape)
                    )
                }

                Text(
                    text = "Tap any entry to load it back into the board",
                    color = subTxtColor,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DottedDivider(
                    color = strokeColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (historyList.isEmpty()) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "[:EMPTY LOG:]",
                                color = txtColor.copy(alpha = 0.25f),
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter equations above to store logs",
                                color = txtColor.copy(alpha = 0.15f),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        items(historyList) { item ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(txtColor.copy(alpha = 0.04f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.retrieveHistoryItem(item)
                                        onDismiss()
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = item.expression,
                                    color = txtColor.copy(alpha = 0.8f),
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "= ${item.result}",
                                    color = Color(0xFF3271F6), // Blue accent for historic calculations
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                DottedDivider(
                    color = strokeColor,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = buttonBg,
                        contentColor = buttonTxtColor
                    ),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "CLOSE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun NothingThemeDialog(
    onDismiss: () -> Unit,
    viewModel: CalculatorViewModel
) {
    val currentTheme by viewModel.themeMode.collectAsState()
    val isDark = isSystemInDarkTheme() || currentTheme == "Dark"
    
    // Choose theme colors dynamically based on active system theme setting
    val dialogBg = if (isDark) Color(0xFF1C1C1E) else Color(0xFFF2F2F7)
    val itemBg = if (isDark) Color(0xFF2C2C2E) else Color(0xFFFFFFFF)
    val txtColor = if (isDark) Color.White else Color.Black
    val subTxtColor = if (isDark) Color.Gray else Color.DarkGray
    val strokeColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.08f)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = dialogBg),
            border = BorderStroke(1.dp, strokeColor),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("theme_selection_dialog")
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth()
            ) {
                // Header
                Text(
                    text = "App Theme",
                    color = txtColor,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                Text(
                    text = "Select your preferred app theme",
                    color = subTxtColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // List of Theme options
                val options = listOf("Light", "Dark", "System Default")
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    options.forEach { option ->
                        val isSelected = currentTheme == option
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(68.dp)
                                .background(itemBg, shape = RoundedCornerShape(18.dp))
                                .border(
                                    BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) {
                                            if (isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.15f)
                                        } else {
                                            Color.Transparent
                                        }
                                    ),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clip(RoundedCornerShape(18.dp))
                                .clickable {
                                    viewModel.setThemeMode(option)
                                }
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = option,
                                color = txtColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.SansSerif
                            )

                            // Clean Custom Radio Button matching the provided design
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.size(22.dp)
                            ) {
                                val radioStrokeColor = if (isSelected) {
                                    if (isDark) Color.White else Color.Black
                                } else {
                                    if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.3f)
                                }
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    drawCircle(
                                        color = radioStrokeColor,
                                        radius = size.minDimension / 2f,
                                        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                                    )
                                    if (isSelected) {
                                        drawCircle(
                                            color = if (isDark) Color.White else Color.Black,
                                            radius = size.minDimension / 4.4f
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Done Pill Button
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color.White else Color.Black,
                        contentColor = if (isDark) Color.Black else Color.White
                    ),
                    shape = RoundedCornerShape(30.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "DONE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
