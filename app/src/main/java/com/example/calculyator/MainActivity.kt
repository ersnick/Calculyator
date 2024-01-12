package com.example.calculyator

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    lateinit var editText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editText = findViewById(R.id.resultTextView)

        val buttonClickHandler = View.OnClickListener { v ->
            val button = v as Button
            val currentText = editText.text.toString()
            val buttonText = button.text.toString()

            when (buttonText) {
                "=" -> calculateResult(currentText)
                "C" -> clearInput()
                else -> appendToInput(buttonText)
            }
        }

        val buttonIds = arrayOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6,
            R.id.btn7, R.id.btn8, R.id.btn9, R.id.btnPlus, R.id.btnMinus,
            R.id.btnMultiply, R.id.btnDivide, R.id.btnClear, R.id.btnEquals
        )

        for (buttonId in buttonIds) {
            findViewById<Button>(buttonId).setOnClickListener(buttonClickHandler)
        }
    }

    private fun calculateResult(input: String) {
        try {
            val result = eval(input)
            editText.setText(result.toString())
        } catch (e: Exception) {
            editText.setText("Error")
        }
    }

    private fun clearInput() {
        editText.setText("")
    }

    private fun appendToInput(value: String) {
        val currentText = editText.text.toString()
        editText.setText(currentText + value)
    }

    private fun eval(input: String): Double {
        return object : Any() {
            var pos = -1
            var ch = ' '

            fun nextChar() {
                ch = if (++pos < input.length) input[pos] else '\u0000'
            }

            fun eat(charToEat: Char): Boolean {
                while (ch == ' ') nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < input.length) throw RuntimeException("Unexpected: " + ch)
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    when {
                        eat('+') -> x += parseTerm()
                        eat('-') -> x -= parseTerm()
                        else -> return x
                    }
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    when {
                        eat('*') -> x *= parseFactor()
                        eat('/') -> x /= parseFactor()
                        else -> return x
                    }
                }
            }

            fun parseFactor(): Double {
                if (eat('+')) return parseFactor()
                if (eat('-')) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('(')) {
                    x = parseExpression()
                    eat(')')
                } else if ((ch in '0'..'9') || ch == '.') {
                    while ((ch in '0'..'9') || ch == '.') nextChar()
                    x = input.substring(startPos, pos).toDouble()
                } else {
                    throw RuntimeException("Unexpected: " + ch)
                }

                return x
            }
        }.parse()
    }
}