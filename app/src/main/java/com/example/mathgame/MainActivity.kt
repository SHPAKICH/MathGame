package com.example.mathgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mathgame.ui.theme.MathGameTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MathGameTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MathGameScreen()
                }
            }
        }
    }
}

@Composable
fun MathGameScreen() {
    var problem by remember { mutableStateOf<MathProblem?>(null) }
    var userAnswer by remember { mutableStateOf("") }
    var isStartEnabled by remember { mutableStateOf(true) }
    var backgroundColor by remember { mutableStateOf(Color.White) }
    var correctAnswers by remember { mutableStateOf(0) }
    var incorrectAnswers by remember { mutableStateOf(0) }

    val percentage = if (correctAnswers + incorrectAnswers > 0) {
        ((correctAnswers.toFloat() / (correctAnswers + incorrectAnswers)) * 100).let { percentage ->
            (percentage * 100).roundToInt().toFloat() / 100
        }
    } else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Problem Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(16.dp)
        ) {
            Text(
                text = problem?.toString() ?: "Press START to begin",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Answer Input
        OutlinedTextField(
            value = userAnswer,
            onValueChange = { userAnswer = it },
            label = { Text("Enter answer") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            enabled = !isStartEnabled,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = {
                    problem = MathProblem.generate()
                    userAnswer = ""
                    isStartEnabled = false
                    backgroundColor = Color.White
                },
                enabled = isStartEnabled
            ) {
                Text("START")
            }
            Button(
                onClick = {
                    problem?.let {
                        val isCorrect = it.checkAnswer(userAnswer.toIntOrNull() ?: 0)
                        backgroundColor = if (isCorrect) Color.Green else Color.Red
                        if (isCorrect) correctAnswers++ else incorrectAnswers++
                        isStartEnabled = true
                    }
                },
                enabled = !isStartEnabled && userAnswer.isNotEmpty()
            ) {
                Text("CHECK")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Statistics
        Text("Correct Answers: $correctAnswers")
        Text("Incorrect Answers: $incorrectAnswers")
        Text("Percentage Correct: $percentage%")
    }
}

data class MathProblem(
    val operand1: Int,
    val operand2: Int,
    val operator: Char,
    val result: Int
) {
    companion object {
        fun generate(): MathProblem {
            val operators = listOf('+', '-', '*', '/')
            val operator = operators.random()
            val num1 = (10..99).random()
            val num2 = when (operator) {
                '/' -> {
                    // Ensure integer division
                    val possibleDivisors = (1..num1).filter { num1 % it == 0 && it in 10..99 }
                    possibleDivisors.random()
                }
                else -> (10..99).random()
            }

            val result = when (operator) {
                '+' -> num1 + num2
                '-' -> num1 - num2
                '*' -> num1 * num2
                '/' -> num1 / num2
                else -> 0
            }

            return MathProblem(num1, num2, operator, result)
        }
    }

    fun checkAnswer(answer: Int): Boolean = answer == result

    override fun toString(): String = "$operand1 $operator $operand2 = ?"
}