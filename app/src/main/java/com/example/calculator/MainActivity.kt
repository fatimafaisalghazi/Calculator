package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
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
            val result = doCurrentOperation()
            binding.inputDigit.text = result.toString()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun displayOperation(operation: Operation) {
        val input = binding.inputDigit.text.toString().trim()

        currentOperation = operation

        val symbol = when (operation) {
            Operation.Plus -> " + "
            Operation.Minus -> " - "
            Operation.Times -> " × "
            Operation.Division -> " ÷ "
            Operation.Reminder -> " % "
        }

        if (!input.last().isOperator()) {
            binding.inputDigit.text = input + symbol
        }
    }

    private fun doCurrentOperation(): Double {
        val input = binding.inputDigit.text.toString().trim()

        return when (currentOperation) {
            Operation.Minus -> {
                val parts = input.split("-").map { it.toDouble() }
                var result = parts[0]

                for (i in 1 until parts.size) {
                    result -= parts[i]
                }
                result
            }

            Operation.Plus -> {
                val parts = input.split("-").map { it.toDouble() }
                var result = parts[0]

                for (i in 1 until parts.size) {
                    result += parts[i]
                }
                result
            }

            Operation.Times -> {
                val parts = input.split("-").map { it.toDouble() }
                var result = parts[0]
                for (i in 1 until parts.size) {
                    result *= parts[i]
                }
                result
            }

            Operation.Reminder -> {
                val part = input.split("%")
                val first = part[0].toDouble()
                val second = part[1].toDouble()
                first % second
            }

            Operation.Division -> {
                val parts = input.split("-").map { it.toDouble() }
                var result = parts[0]
                for (i in 1 until parts.size) {
                    result /= parts[i]
                }
                result
            }
            null -> 0.0
        }
    }

    fun backspaceOperation(oldDigit: String): String {
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
            else -> oldDigit + newDigit
        }
        binding.inputDigit.text = newOperation
    }

    fun onClearInput() {
        binding.inputDigit.text = "0"
        binding.solution.text = ""
        currentOperation = null
    }

    private fun Char.isOperator() = this in "+-×÷%"
}