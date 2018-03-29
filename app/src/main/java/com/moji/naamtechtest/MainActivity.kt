package com.moji.naamtechtest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
    //******************* First Challenge Code Start *******************
    fun onFirstChallengeClicked(view: View){
        val input = editInput.text.toString().toCharArray()
        editOutput.setText(recursiveIndexFinder(input))
    }

    private fun recursiveIndexFinder(input : CharArray, index : Int = 0) : String{
        if(index >= input.size) return "Position : N/A"

        return when(input[index] == 'a'){
            true -> "Position : ${index + 1}"
            false -> recursiveIndexFinder(input, index + 1)
        }
    }
    //******************* First Challenge Code End *********************



    //******************* Second Challenge Code Start *******************
    fun onSecondChallengeClicked(view: View){
        val input = editInput.text.toString().toCharArray()
        editOutput.setText(giveReport(input))
    }

    private fun giveReport(input : CharArray) : String{
        var output = "=========\n==Report==\n"

        val reportArray = ArrayList<Pair>()
        for(char in input){
            var isExisted = false
            // if char has already added to reportArray increment its repetition
            for(index in reportArray.indices){
                if(reportArray[index].char == char){
                    reportArray[index].number++
                    isExisted = true
                    break
                }
            }
            // if char hasn't yet added to reportArray, add it and set its repetition to 1
            if(!isExisted){
                reportArray.add(Pair(char, 1))
            }
        }

        for(pair in reportArray){
            output += "${pair.char} : ${pair.number}\n"
        }

        output += "Total Characters: ${input.size}"
        return output
    }

    data class Pair(val char: Char, var number: Int)
    //******************* Second Challenge Code End *********************



    //******************* Third Challenge Code Start *******************
    fun onThirdChallengeClicked(view: View){
        val input = editInput.text.toString().toCharArray()
        editOutput.setText(evaluateArithmetic(input))
    }

    private lateinit var valueStack : Stack<Double>
    private lateinit var operatorStack : Stack<Operator>
    private lateinit var value : String
    private fun evaluateArithmetic(input : CharArray) : String{
        // declare two stacks one for numbers, and one for operator
        valueStack = Stack<Double>()
        operatorStack = Stack<Operator>()
        value = ""
        try{
            for(char in input){
                when(CharType.getType(char)){

                    CharType.Digit -> value += char

                    CharType.Operator ->{
                        addOperatorToStackAndDoNecessaryCalculations(Operator.getType(char))
                    }
                    CharType.EmptySpace -> addValueToStackIfAny()

                    CharType.SubtractionOrMinus -> {
                        if(operatorStack.size == valueStack.size - 1 || value.isNotEmpty()){// must be subtraction
                            addOperatorToStackAndDoNecessaryCalculations(Operator.getType(char))
                        }else{ // must be minus
                            value += char
                        }
                    }

                    CharType.UnacceptedChar -> throw Exception()
                }
            }
            addValueToStackIfAny()

            // do the remained calculations until operatorStack is empty
            while (operatorStack.isNotEmpty()) {
                valueStack.push(Operator.applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop()))
            }

        }catch (ex : ArithmeticException){
            return "Error: Division by zero"
        }
        catch(ex : Exception) {
            return "Error: Syntax Error"
        }

        return if(valueStack.peek() > 0) {
            "${Math.floor(valueStack.pop()).toInt()}"
        }else{
            "${Math.ceil(valueStack.pop()).toInt()}"
        }
    }

    private fun addValueToStackIfAny(){
        if(value.isNotEmpty()) {
            valueStack.push(Integer.parseInt(value).toDouble())
            value = ""
        }
    }

    private fun addOperatorToStackAndDoNecessaryCalculations(operator: Operator){
        addValueToStackIfAny()
        while (operatorStack.isNotEmpty() && Operator.hasPrecedence(operator, operatorStack.peek()))
            valueStack.push(Operator.applyOperator(operatorStack.pop(), valueStack.pop(), valueStack.pop()));

        operatorStack.push(operator)
    }


    private enum class CharType {
        Digit, Operator, SubtractionOrMinus, EmptySpace,
        UnacceptedChar;
        companion object {
            fun getType(char : Char)  = when(char){
                in '0'..'9' -> Digit
                '%' , '*' , 'x' , '/' , '+' -> Operator
                '-' -> SubtractionOrMinus
                ' ' -> EmptySpace
                else -> UnacceptedChar
            }
        }
    }

    private enum class Operator {
        Modulus, Multiplication, Division,
        Addition, Subtraction, UnacceptedChar;
        companion object {
            fun getType(char : Char)  = when(char){
                '%' -> Modulus
                '*' , 'x' -> Multiplication
                '/' -> Division
                '+' -> Addition
                '-' -> Subtraction
                else -> UnacceptedChar
            }

            fun getPrecedence(operator : Operator) = when(operator){
                Modulus -> 1
                Division , Multiplication -> 2
                Addition, Subtraction -> 3
                else -> 0
            }

            fun hasPrecedence(firstOperator : Operator, secondOperator : Operator) =
                    getPrecedence(firstOperator) >= getPrecedence(secondOperator)

            fun applyOperator(operator : Operator, secondNumber: Double, firstNumber: Double): Double = when(operator){
                Modulus -> firstNumber % secondNumber
                Division ->{
                    if(secondNumber == 0.0) throw ArithmeticException()
                    firstNumber / secondNumber
                }
                Multiplication -> firstNumber * secondNumber
                Addition -> firstNumber + secondNumber
                Subtraction -> firstNumber - secondNumber
                else -> 0.0
            }
        }
    }
    //******************* Third Challenge Code End *********************
}
