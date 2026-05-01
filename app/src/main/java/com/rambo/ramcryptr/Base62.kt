package com.rambo.ramcryptr

object Base62 {

    private const val CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    fun encode(number: Int): String {
        if (number == 0) return "0"

        var num = number
        val result = StringBuilder()

        while (num > 0) {
            val remainder = num % 62
            result.append(CHARS[remainder])
            num /= 62
        }

        return result.reverse().toString()
    }

    fun decode(str: String): Int {
        var result = 0

        for (char in str) {
            val index = CHARS.indexOf(char)
            if (index == -1) throw IllegalArgumentException("Invalid Base62 character")

            result = result * 62 + index
        }

        return result
    }
}
