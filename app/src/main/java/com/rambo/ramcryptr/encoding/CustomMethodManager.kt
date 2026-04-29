package com.rambo.ramcryptr.encoding

import android.util.Base64

object CustomMethodManager {

    fun xorEncode(
        text:String,
        key:Int
    ):String{

        val bytes=
            text.toByteArray().map{
                (it.toInt() xor key)
                    .toByte()
            }.toByteArray()

        return Base64.encodeToString(
            bytes,
            Base64.NO_WRAP
        )
    }

}
