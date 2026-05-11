package com.rambo.ramcryptr

object ChannelCryptoBridge {

    fun getActiveSeed(
        context: android.content.Context
    ): String {

        return try {

            ChannelManager
                .getActiveChannel(context)
                ?.cryptoSeed

                ?: "GLOBAL_DEFAULT"

        } catch (_: Exception) {

            "GLOBAL_DEFAULT"
        }
    }
}
