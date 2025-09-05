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

    private var currentOperation: Operation? = null

    private fun addCallBack() {
        binding.clear.setOnClickListener {
            onClearInput()
        }

        binding.back.setOnClickListener {
            val oldDigit = binding.inputDigit.text.toString()
            binding.inputDigit.text = backspaceOperation(oldDigit)
        }

        binding.plus.setOnClickListener {
            displayOperation(Operation.Plus)
        }

        binding.minus.setOnClickListener {
            displayOperation(Operation.Minus)
        }

        binding.devide.setOnClickListener {
            displayOperation(Operation.Division)
        }

        binding.reminder.setOnClickListener {
            displayOperation(Operation.Reminder)
        }

        binding.times.setOnClickListener {
            displayOperation(Operation.Times)
        }

        binding.equal.setOnClickListener {
            try {
                val result = doCurrentOperation()
                binding.inputDigit.text = result.toString()
            }catch (e: ArithmeticException){
                binding.inputDigit.text = "${e.message}"
            }catch (e: Throwable){
                binding.inputDigit.text = "${e.message}"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayOperation(operation: Operation) {
        val input = binding.inputDigit.text.toString().trim()

        currentOperation = operation

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

    private fun doCurrentOperation(): Double {
        val input = binding.inputDigit.text.toString().trim()
        val numbers =
            input.replace(" ", "").split(Regex("[-+×÷%]")).map { it.toDouble() }.toMutableList()
        val operators = Regex("[-+×÷%]").findAll(input).map { it.value }.toMutableList()

        var i = 0
        while (i < operators.size) {
            val currentDigit = numbers[i]
            val nextDigit = numbers[i + 1]
            val reduceIndex = i - 1

            when (operators[i]) {
                Operation.Times.symbol.trim() -> {
                    numbers[i] = currentDigit * nextDigit
                    numbers.removeAt(i + 1)
                    operators.removeAt(i)
                    reduceIndex
                }

                Operation.Division.symbol.trim() -> {
                    if (nextDigit == 0.0) throw ArithmeticException("Error Division by zero")
                    numbers[i] = currentDigit / nextDigit
                    numbers.removeAt(i + 1)
                    operators.removeAt(i)
                    reduceIndex
                }

                Operation.Reminder.symbol.trim() -> {
                    numbers[i] = currentDigit * (nextDigit / 100)
                    numbers.removeAt(i + 1)
                    operators.removeAt(i)
                    reduceIndex
                }
            }
            i++
        }

        var result = numbers[0]
        for (j in operators.indices) {
            val next = numbers[j + 1]

            result = when (operators[j]) {
                Operation.Plus.symbol.trim() -> result + next
                Operation.Minus.symbol.trim() -> result - next
                else -> result
            }
        }

        return result
    }

    private fun backspaceOperation(oldDigit: String): String {
        return if (oldDigit.isNotEmpty()) {
            val newDigit = oldDigit.dropLast(n = 1)
            newDigit.ifEmpty { "0" }
        } else {
            "0"
        }
    }

    fun onClickNumber(v: View) {
        val newDigit = (v as Button).text.toString()
        val oldDigit = binding.inputDigit.text.toString()

        val newOperation = when {
            oldDigit == "0" && newDigit == "0" -> oldDigit
            oldDigit == "0" && newDigit == "." -> "0."
            oldDigit == "0" -> newDigit
            oldDigit.isNotEmpty() && oldDigit.last() == '.' && newDigit == "." -> oldDigit
            else -> oldDigit + newDigit
        }
        binding.inputDigit.text = newOperation
    }

    fun onClearInput() {
        binding.inputDigit.text = "0"
        currentOperation = null
    }

    private fun Char.isOperator() = this in "+-×÷%"
}