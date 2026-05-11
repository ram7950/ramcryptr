package com.rambo.ramcryptr

import android.content.Context

object CryptoMasterProvider {

    fun getMaster(
        context: Context
    ): String {

        return try {

            val seed =
                ChannelManager
                    .getActiveChannel(context)
                    ?.cryptoSeed

                    ?: "GLOBAL_DEFAULT"

            "ramcryptr_secret::$seed"

        } catch (_: Exception) {

            "ramcryptr_secret::GLOBAL_DEFAULT"
        }
    }
}
