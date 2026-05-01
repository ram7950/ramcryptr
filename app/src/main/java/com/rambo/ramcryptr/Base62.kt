package com.rambo.ramcryptr

object Base62 {
    private val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    fun encode(num: Int): String {
        var n = num
        val sb = StringBuilder()
        while (n > 0) {
            sb.append(chars[n % 62])
            n /= 62
        }
        return sb.reverse().toString()
    }

    fun decode(str: String): Int {
        var result = 0
        for (c in str) {
            result = result * 62 + chars.indexOf(c)
        }
        return result
    }
}
