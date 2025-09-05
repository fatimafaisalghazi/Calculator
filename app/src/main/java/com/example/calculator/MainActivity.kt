package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        addCallBack()
    }

    private fun addCallBack() {

        binding.clear.setOnClickListener { onClearInput() }

        binding.back.setOnClickListener { backspaceOperation() }

        binding.plus.setOnClickListener { displayOperation(Operation.Plus) }

        binding.minus.setOnClickListener { displayOperation(Operation.Minus) }

        binding.devide.setOnClickListener { displayOperation(Operation.Division) }

        binding.reminder.setOnClickListener { displayOperation(Operation.Reminder) }

        binding.times.setOnClickListener { displayOperation(Operation.Times) }

        binding.minusPlus.setOnClickListener { reverseSign() }

        binding.equal.setOnClickListener { onEqualClick() }
    }

    @SuppressLint("SetTextI18n")
    private fun displayOperation(operation: Operation) {
        val input = binding.inputDigit.text.toString().trim()

        val symbol = when (operation) {
            Operation.Plus -> Operation.Plus.symbol
            Operation.Minus -> Operation.Minus.symbol
            Operation.Times -> Operation.Times.symbol
            Operation.Division -> Operation.Division.symbol
            Operation.Reminder -> Operation.Reminder.symbol
        }

        if (!input.last().isOperator()) {
            binding.inputDigit.text = input + symbol
        }
    }

    private fun reverseSign() {
        val current = binding.inputDigit.text.toString().toDouble()
        binding.inputDigit.text = (-current).toString()
    }

    private fun doCurrentOperation(): Double {
        val input = binding.inputDigit.text.toString().trim()
        val numbers = input.replace(oldValue = " ", newValue = "")
            .split(Regex(pattern = MAIN_PATTERN_OPERATOR)).map { it.toDouble() }.toMutableList()
        val operators =
            Regex(pattern = MAIN_PATTERN_OPERATOR).findAll(input).map { it.value }.toMutableList()
        var currentIndex = ZERO.toInt()

        while (currentIndex < operators.size) {
            if (currentIndex >= numbers.size - 1) break

            val currentDigit = numbers[currentIndex]
            val nextDigit = numbers[currentIndex + 1]

            when (operators[currentIndex].trim()) {
                Operation.Times.symbol.trim() -> {
                    numbers[currentIndex] = currentDigit * nextDigit
                    numbers.removeAt(currentIndex + 1)
                    operators.removeAt(currentIndex)
                    continue
                }

                Operation.Division.symbol.trim() -> {
                    if (nextDigit == ZERO.toDouble()) throw ArithmeticException(DIVISION_BY_ZERO)
                    numbers[currentIndex] = currentDigit / nextDigit
                    numbers.removeAt(currentIndex + 1)
                    operators.removeAt(currentIndex)
                    continue
                }

                Operation.Reminder.symbol.trim() -> {
                    numbers[currentIndex] = currentDigit * (nextDigit / 100)
                    numbers.removeAt(currentIndex + 1)
                    operators.removeAt(currentIndex)
                    continue
                }
            }
            currentIndex++
        }

        var result = numbers[ZERO.toInt()]
        for (j in operators.indices) {
            if (j + 1 >= numbers.size) break
            val next = numbers[j + 1]

            result = when (operators[j].trim()) {
                Operation.Plus.symbol.trim() -> result + next
                Operation.Minus.symbol.trim() -> result - next
                else -> result
            }
        }
        return result
    }

    private fun backspaceOperation() {
        val oldDigit = binding.inputDigit.text.toString()
        val newDigit = oldDigit.dropLast(n = 1)

        binding.inputDigit.text = if (oldDigit.isNotEmpty()) {
            newDigit.ifEmpty { ZERO }
        } else { ZERO }
    }

    fun onClickNumber(v: View) {
        val newDigit = (v as Button).text.toString()
        val oldDigit = binding.inputDigit.text.toString()

        val newOperation = when {
            oldDigit == ZERO && newDigit == ZERO -> oldDigit
            oldDigit == ZERO && newDigit == POINT -> ZERO_POINT
            oldDigit == ZERO -> newDigit
            oldDigit.isNotEmpty() && oldDigit.last().toString() == POINT && newDigit == POINT -> oldDigit
            else -> oldDigit + newDigit
        }
        binding.inputDigit.text = newOperation
    }

    private fun onClearInput() {
        binding.inputDigit.text = ZERO
        binding.previous.text = ""
    }

    private fun onEqualClick() {
        try {
            val previousOperation = binding.inputDigit.text.toString()
            binding.previous.text = previousOperation
            val result = doCurrentOperation()
            binding.inputDigit.text = result.toString()
        } catch (e: ArithmeticException) {
            binding.inputDigit.text = "${e.message}"
        } catch (e: Throwable) {
            binding.inputDigit.text = ERROR_MESSAGE
        }
    }

    private fun Char.isOperator() = this in MAIN_OPERATOR

    private companion object {
        const val ZERO = "0"
        const val POINT = "."
        const val MAIN_PATTERN_OPERATOR = "[-+×÷%]"
        const val MAIN_OPERATOR = "+-×÷%"
        const val ERROR_MESSAGE = "don't do that again"
        const val ZERO_POINT = "0."
        const val DIVISION_BY_ZERO = "Error Division by zero"
    }
}