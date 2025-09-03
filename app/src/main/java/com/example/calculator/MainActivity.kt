package com.example.calculator

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
//        enableEdgeToEdge()
        setContentView(binding.root)
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
        addCallBack()
    }

    var currentOperation: Operation? = null

    private fun addCallBack() {
        binding.clear.setOnClickListener {
            onClearInput()
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

    private fun displayOperation(operation: Operation) {
        currentOperation = operation

        val symbol = when (operation) {
            Operation.Plus -> "+"
            Operation.Minus -> "-"
            Operation.Times -> "×"
            Operation.Division -> "÷"
            Operation.Reminder -> "%"
        }
        binding.inputDigit.text = binding.inputDigit.text.toString() + symbol
    }

    private fun doCurrentOperation(): Double {
        val input = binding.inputDigit.text.toString()

        return when (currentOperation) {
            Operation.Minus -> {
                val  part = input.split("-")
                val first = part[0].toDouble()  ; val second = part[1].toDouble()
                first - second
            }

            Operation.Plus -> {
                val part = input.split("+")
                val first = part[0].toDouble() ; val second = part[1].toDouble()
                first + second
            }

            Operation.Times -> {
                val part = input.split("")
                val first = part[0].toDouble() ; val second = part[1].toDouble()
                first * second
            }

            Operation.Reminder -> {
                val part = input.split("%")
                val first = part[0].toDouble() ; val second = part[1].toDouble()
                first % second
            }

            Operation.Division -> {
                val  part = input.split("/")
                val first = part[0].toDouble() ; val second = part[1].toDouble()
                first / second
            }

            null -> 0.0
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
        binding.inputDigit.text = ""
    }
}