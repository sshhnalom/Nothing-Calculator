package com.example.calculator.domain

import kotlin.math.*

class ExpressionEvaluator(private val isDegree: Boolean = false) {

    private var pos = 0
    private var tokens = listOf<String>()

    @Synchronized
    fun evaluate(expression: String): Double {
        val cleanedExpr = preprocess(expression)
        this.tokens = tokenize(cleanedExpr)
        this.pos = 0

        val result = parseExpression()
        if (pos < tokens.size) {
            throw IllegalArgumentException("Unparsed detail: ${tokens[pos]}")
        }
        
        // Handle floating point precision errors
        return roundToSignificant(result)
    }

    private fun peek(): String? {
        return if (pos < tokens.size) tokens[pos] else null
    }

    private fun consume(expected: String): Boolean {
        if (peek() == expected) {
            pos++
            return true
        }
        return false
    }

    private fun parseExpression(): Double {
        var value = parseTerm()
        while (true) {
            if (consume("+")) {
                val term = parseTerm()
                value += term
            } else if (consume("-")) {
                val term = parseTerm()
                value -= term
            } else {
                break
            }
        }
        return value
    }

    private fun parseTerm(): Double {
        var value = parsePower()
        while (true) {
            val next = peek()
            if (next != null && (
                next == "(" || 
                next == "sin" || 
                next == "cos" || 
                next == "tan" || 
                next == "asin" || 
                next == "acos" || 
                next == "atan" || 
                next == "ln" || 
                next == "log" || 
                next == "sqrt" || 
                next == "pi" || 
                next == "e" || 
                next.matches("""^[0-9]+(\.[0-9]+)?""".toRegex())
            )) {
                // Implicit multiplication helper
                val multiplier = parsePower()
                value *= multiplier
            } else if (consume("*")) {
                val multiplier = parsePower()
                value *= multiplier
            } else if (consume("/")) {
                val divisor = parsePower()
                if (divisor == 0.0) throw ArithmeticException("Division by zero")
                value /= divisor
            } else if (consume("%")) {
                val divisor = parsePower()
                value %= divisor
            } else {
                break
            }
        }
        return value
    }

    private fun parsePower(): Double {
        var value = parseFactor()
        while (true) {
            if (consume("^")) {
                val exponent = parseFactor()
                value = value.pow(exponent)
            } else {
                break
            }
        }
        return value
    }

    private fun parseFactor(): Double {
        val token = peek() ?: throw IllegalArgumentException("Unexpected end of expression")

        // Unary operators
        if (consume("-")) {
            return -parseFactor()
        }
        if (consume("+")) {
            return parseFactor()
        }

        // Scientific functions
        if (consume("sin")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            val res = if (isDegree) sin(Math.toRadians(valArg)) else sin(valArg)
            return checkFactorial(res)
        }
        if (consume("cos")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            val res = if (isDegree) cos(Math.toRadians(valArg)) else cos(valArg)
            return checkFactorial(res)
        }
        if (consume("tan")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            val res = if (isDegree) tan(Math.toRadians(valArg)) else tan(valArg)
            return checkFactorial(res)
        }
        if (consume("asin")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            val rad = asin(valArg)
            val res = if (isDegree) Math.toDegrees(rad) else rad
            return checkFactorial(res)
        }
        if (consume("acos")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            val rad = acos(valArg)
            val res = if (isDegree) Math.toDegrees(rad) else rad
            return checkFactorial(res)
        }
        if (consume("atan")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            val rad = atan(valArg)
            val res = if (isDegree) Math.toDegrees(rad) else rad
            return checkFactorial(res)
        }
        if (consume("ln")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            return checkFactorial(ln(valArg))
        }
        if (consume("log")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            return checkFactorial(log10(valArg))
        }
        if (consume("sqrt")) {
            consume("(")
            val valArg = parseExpression()
            consume(")")
            if (valArg < 0) throw ArithmeticException("Square root of negative number")
            return checkFactorial(sqrt(valArg))
        }

        // Parentheses
        if (consume("(")) {
            val value = parseExpression()
            consume(")")
            return checkFactorial(value)
        }

        // Constants
        if (consume("pi")) {
            return checkFactorial(Math.PI)
        }
        if (consume("e")) {
            return checkFactorial(Math.E)
        }

        // Number tokens
        val numberRegex = """^[0-9]+(\.[0-9]+)?""".toRegex()
        if (token.matches(numberRegex) || token.toDoubleOrNull() != null) {
            pos++
            return checkFactorial(token.toDouble())
        }

        throw IllegalArgumentException("Invalid token: $token")
    }

    private fun checkFactorial(baseValue: Double): Double {
        var value = baseValue
        while (consume("!")) {
            value = factorial(value)
        }
        return value
    }

    private fun factorial(n: Double): Double {
        if (n < 0.0) throw IllegalArgumentException("Factorial of negative number undefined")
        val intPart = n.toInt()
        if (n == intPart.toDouble()) {
            var r = 1.0
            for (i in 2..intPart) {
                r *= i
            }
            return r
        }
        return gammaLanczos(n + 1.0)
    }

    private fun gammaLanczos(x: Double): Double {
        val g = 7
        val p = doubleArrayOf(
            0.99999999999980993, 676.5203681218851, -1259.1392167224028,
            771.32342877765313, -176.61502916214059, 12.507343278686905,
            -0.13857109526572012, 9.9843695780195716e-6, 1.5056327351493116e-7
        )
        var y = x
        if (y < 0.5) {
            return Math.PI / (sin(Math.PI * y) * gammaLanczos(1.0 - y))
        }
        y -= 1.0
        var a = p[0]
        val t = y + g + 0.5
        for (i in 1 until p.size) {
            a += p[i] / (y + i)
        }
        return sqrt(2.0 * Math.PI) * t.pow(y + 0.5) * exp(-t) * a
    }

    private fun preprocess(expr: String): String {
        var str = expr.replace(" ", "")
        // Map operators to standard ASCII equivalents
        str = str.replace("×", "*")
        str = str.replace("÷", "/")
        str = str.replace("π", "pi")
        str = str.replace("√", "sqrt")

        // Auto-close open parentheses
        val openCount = str.count { it == '(' }
        val closeCount = str.count { it == ')' }
        if (openCount > closeCount) {
            val needed = openCount - closeCount
            str += ")".repeat(needed)
        }
        return str
    }

    private fun tokenize(expr: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < expr.length) {
            val c = expr[i]
            when {
                c in "+-*/^()!%" -> {
                    tokens.add(c.toString())
                    i++
                }
                c.isDigit() || c == '.' -> {
                    val sb = StringBuilder()
                    while (i < expr.length && (expr[i].isDigit() || expr[i] == '.')) {
                        sb.append(expr[i])
                        i++
                    }
                    tokens.add(sb.toString())
                }
                c.isLetter() -> {
                    val sb = StringBuilder()
                    while (i < expr.length && expr[i].isLetter()) {
                        sb.append(expr[i])
                        i++
                    }
                    val word = sb.toString()
                    when (word) {
                        "sin", "cos", "tan", "asin", "acos", "atan", "ln", "log", "sqrt", "pi", "e" -> {
                            tokens.add(word)
                        }
                        else -> {
                            // Split individual character letters as separate variables (like implicit mult of separate variables)
                            for (char in word) {
                                tokens.add(char.toString())
                            }
                        }
                    }
                }
                else -> {
                    // Skip or log unknown char
                    tokens.add(c.toString())
                    i++
                }
            }
        }
        return tokens
    }

    private fun roundToSignificant(value: Double): Double {
        if (value.isNaN() || value.isInfinite()) return value
        // Limit floating point precision errors like 0.1 + 0.2 = 0.30000000000000004
        val precision = 12
        val scale = 10.0.pow(precision)
        return round(value * scale) / scale
    }
}
