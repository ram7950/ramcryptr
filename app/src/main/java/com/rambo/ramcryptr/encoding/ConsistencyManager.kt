package com.rambo.ramcryptr.encoding

object ConsistencyManager {

    private const val MARKER =
        "🔄CON🔄"

    fun encode(
        message:String
    ):String{

        return MARKER +
            AES256Encoder.encryptText(
                message,
                "EMERGENCY_BACKUP_KEY"
            )
    }

    fun decode(
        data:String
    ):String{

        val clean=
            data.removePrefix(
                MARKER
            )

        return AES256Encoder.decryptText(
            clean,
            "EMERGENCY_BACKUP_KEY"
        )
    }

}
