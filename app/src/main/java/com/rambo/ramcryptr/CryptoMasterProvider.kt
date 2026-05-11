package com.rambo.ramcryptr

import android.content.Context

object CryptoMasterProvider {

    fun getMaster(
        context: Context
    ): String {

        return try {

            val activeId =
                ChannelStorage
                    .getActiveChannelId(context)

            val channel =
                ChannelStorage
                    .loadChannels(context)
                    .find {
                        it.channelId == activeId
                    }

            val seed =
                channel?.cryptoSeed
                    ?: "GLOBAL_DEFAULT"

            "ramcryptr_secret::$seed"

        } catch (_: Exception) {

            "ramcryptr_secret::GLOBAL_DEFAULT"
        }
    }
}
