package com.rambo.ramcryptr.encoding

object ManualKeyManager {

    private const val DEFAULT_KEY =
        "12345678901234567890123456789012"

    fun getKey(): String {
        return DEFAULT_KEY
    }

}
