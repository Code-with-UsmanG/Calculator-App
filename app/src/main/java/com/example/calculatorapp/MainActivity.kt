package com.example.calculatorapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ArrowBack
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.compose.animation.*
import androidx.compose.ui.text.font.FontWeight

class MainActivity : ComponentActivity() {
    private var numberClickSound: MediaPlayer? = null
    private var operationSound: MediaPlayer? = null
    private var equalsSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Set instance for companion object
        instance = this
        
        // Initialize MediaPlayers
        numberClickSound = MediaPlayer.create(this, R.raw.operation_click)
        operationSound = MediaPlayer.create(this, R.raw.number_click)
        equalsSound = MediaPlayer.create(this, R.raw.equals_click)

        setContent {
            MaterialTheme(
                colorScheme = darkColorScheme(
                    background = Color.Black,
                    surface = Color(0xFF1C1C1C),
                    primary = Color(0xFF0D47A1),
                    secondary = Color(0xFFFFAB91),
                    onBackground = Color.White,
                    onSurface = Color.White,
                    onPrimary = Color.White
                )
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        numberClickSound?.release()
        operationSound?.release()
        equalsSound?.release()
        instance = null  // Clear instance reference
    }

    companion object {
        private var instance: MainActivity? = null

        fun playButtonClickSound() {
            instance?.numberClickSound?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            }
        }

        fun playOperationSound() {
            instance?.operationSound?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            }
        }

        fun playEqualsSound() {
            instance?.equalsSound?.let { player ->
                if (player.isPlaying) {
                    player.seekTo(0)
                } else {
                    player.start()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var currentInput by remember { mutableStateOf("") }
    var operation by remember { mutableStateOf("") }
    var firstNumber by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    var isNewOperation by remember { mutableStateOf(true) }
    var showSettings by remember { mutableStateOf(false) }
    var isSoundEnabled by remember { mutableStateOf(false) }
    var expression by remember { mutableStateOf("") }

    AnimatedVisibility(
        visible = !showSettings,
        enter = fadeIn() + slideInHorizontally(),
        exit = fadeOut() + slideOutHorizontally()
    ) {
        // Main Calculator Screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Top Navigation Bar with improved spacing
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Calculator",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = { showSettings = true },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(14.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Add separator line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
            )

            // Display Area with improved styling
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f)
                    .padding(vertical = 16.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = expression,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = if (result.isNotEmpty()) result else currentInput.ifEmpty { "0" },
                        fontSize = 48.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 1,
                        softWrap = false
                    )
                }
            }

            // Keypad with improved spacing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Clear Buttons Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CalculatorButton(
                        text = "AC",
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFE57373),
                        isSoundEnabled = isSoundEnabled
                    ) {
                        currentInput = ""
                        operation = ""
                        firstNumber = ""
                        result = ""
                        isNewOperation = true
                        expression = ""
                    }
                    CalculatorButton(
                        text = "C",
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color(0xFFEF9A9A),
                        isSoundEnabled = isSoundEnabled
                    ) {
                        currentInput = ""
                        isNewOperation = true
                        expression = ""
                    }
                    CalculatorButton(
                        text = "+",
                        modifier = Modifier.weight(1f),
                        backgroundColor = MaterialTheme.colorScheme.primary,
                        selected = "+" == operation,
                        isSoundEnabled = isSoundEnabled
                    ) {
                        handleOperation("+", currentInput, operation, firstNumber) { newOp, first ->
                            operation = newOp
                            firstNumber = first
                            expression = "$firstNumber $operation"
                            currentInput = ""
                            isNewOperation = true
                        }
                    }
                }

                // Number Pad Grid with improved spacing
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    listOf(
                        listOf("7", "8", "9"),
                        listOf("4", "5", "6"),
                        listOf("1", "2", "3"),
                        listOf("%", "0", ".", "=")
                    ).forEach { row ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            row.forEach { digit ->
                                CalculatorButton(
                                    text = digit,
                                    modifier = Modifier.weight(
                                        when {
                                            row.size == 4 -> 0.75f
                                            else -> 1f
                                        }
                                    ),
                                    backgroundColor = when (digit) {
                                        "=" -> Color(0xFFE67E73)
                                        "%" -> MaterialTheme.colorScheme.primary
                                        "." -> MaterialTheme.colorScheme.surface
                                        else -> MaterialTheme.colorScheme.surface
                                    },
                                    isSoundEnabled = isSoundEnabled
                                ) {
                                    when (digit) {
                                        "=" -> {
                                            if (operation.isNotEmpty() && currentInput.isNotEmpty()) {
                                                result = calculateResult(firstNumber, currentInput, operation)
                                                expression = "$firstNumber $operation $currentInput = $result"
                                                currentInput = ""
                                                operation = ""
                                                firstNumber = ""
                                                isNewOperation = true
                                            }
                                        }
                                        "%" -> {
                                            if (currentInput.isNotEmpty()) {
                                                val number = currentInput.toDoubleOrNull()
                                                if (number != null) {
                                                    currentInput = (number / 100).toString()
                                                    expression = "$currentInput%"
                                                }
                                            }
                                        }
                                        "." -> {
                                            if (!currentInput.contains(".")) {
                                                currentInput += if (currentInput.isEmpty()) "0." else "."
                                                if (operation.isEmpty()) {
                                                    expression = currentInput
                                                } else {
                                                    expression = "$firstNumber $operation $currentInput"
                                                }
                                            }
                                        }
                                        else -> {
                                            if (isNewOperation) {
                                                currentInput = digit
                                                isNewOperation = false
                                            } else {
                                                currentInput += digit
                                            }
                                            if (operation.isEmpty()) {
                                                expression = currentInput
                                            } else {
                                                expression = "$firstNumber $operation $currentInput"
                                            }
                                        }
                                    }
                                }
                            }
                            
                            // Operation button for non-bottom rows only
                            if (row[0] != "%" && row.size == 3) {
                                val op = when (row[0]) {
                                    "7" -> "÷"
                                    "4" -> "×"
                                    "1" -> "-"
                                    else -> "+"
                                }
                                CalculatorButton(
                                    text = op,
                                    modifier = Modifier.weight(1f),
                                    backgroundColor = MaterialTheme.colorScheme.primary,
                                    selected = op == operation,
                                    isSoundEnabled = isSoundEnabled
                                ) {
                                    handleOperation(op, currentInput, operation, firstNumber) { newOp, first ->
                                        operation = newOp
                                        firstNumber = first
                                        expression = "$firstNumber $operation"
                                        currentInput = ""
                                        isNewOperation = true
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Settings Screen
    AnimatedVisibility(
        visible = showSettings,
        enter = fadeIn() + slideInHorizontally(initialOffsetX = { it }),
        exit = fadeOut() + slideOutHorizontally(targetOffsetX = { it })
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Settings Header with Back Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showSettings = false },
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Empty box for alignment
                Box(modifier = Modifier.size(48.dp))
            }

            // Settings Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                // Sound Settings Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Button Sound",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isSoundEnabled,
                            onCheckedChange = { isSoundEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = MaterialTheme.colorScheme.primary,
                                checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                // Developer Info Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Developer Info",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InfoRow("Name", "Usman Ghani")
                            InfoRow("Designation", "Android Developer")
                            InfoRow("E-mail", "usmanghanii7729@gmail.com")
                            InfoRow("Contact", "+92 326 0157526")
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            " 2025 All Rights Reserved",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color,
    selected: Boolean = false,
    isSoundEnabled: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            if (isSoundEnabled) {
                when (text) {
                    "+", "-", "×", "÷", "%" -> MainActivity.playOperationSound()
                    "=" -> MainActivity.playEqualsSound()
                    else -> MainActivity.playButtonClickSound()
                }
            }
            onClick()
        },
        modifier = modifier
            .height(68.dp)
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) backgroundColor.copy(alpha = 0.7f) else backgroundColor
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(
            text = text,
            fontSize = when (text) {
                "+", "-", "×", "÷", "=" -> 32.sp
                else -> 24.sp
            },
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun handleOperation(
    newOp: String,
    currentInput: String,
    currentOperation: String,
    firstNumber: String,
    updateState: (String, String) -> Unit
) {
    if (currentInput.isNotEmpty()) {
        if (currentOperation.isNotEmpty() && firstNumber.isNotEmpty()) {
            val result = calculateResult(firstNumber, currentInput, currentOperation)
            updateState(newOp, result)
        } else {
            updateState(newOp, currentInput)
        }
    }
}

private fun calculateResult(first: String, second: String, operation: String): String {
    val num1 = first.toDoubleOrNull()
    val num2 = second.toDoubleOrNull()

    if (num1 == null || num2 == null) {
        return "Invalid input"
    }

    return try {
        val calculatedResult = when (operation) {
            "+" -> num1 + num2
            "-" -> num1 - num2
            "×" -> num1 * num2
            "÷" -> {
                if (num2 == 0.0) throw ArithmeticException("Division by zero")
                num1 / num2
            }
            else -> return "Select operation"
        }

        // Format the result
        if (calculatedResult % 1 == 0.0) {
            calculatedResult.toInt().toString()
        } else {
            "%.2f".format(calculatedResult)
        }
    } catch (e: ArithmeticException) {
        "Error: ${e.message}"
    } catch (e: Exception) {
        "Error"
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}