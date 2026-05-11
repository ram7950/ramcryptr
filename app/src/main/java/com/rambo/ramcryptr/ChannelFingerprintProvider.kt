package com.rambo.ramcryptr

import android.content.Context

object ChannelFingerprintProvider {

    fun getActiveFingerprint(
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

            if (channel != null) {

                ChannelFingerprint.generate(
                    channel.cryptoSeed
                )

            } else {

                "0000"
            }

        } catch (_: Exception) {

            "0000"
        }
    }
}
