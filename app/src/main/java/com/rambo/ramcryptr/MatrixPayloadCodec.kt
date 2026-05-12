package com.rambo.ramcryptr

import android.util.Base64
import org.json.JSONObject

object MatrixPayloadCodec {

    fun encodeChannel(
        channel: Channel
    ): String {

        return try {

            val json =
                JSONObject().apply {

                    put("v", 1)

                    put(
                        "channelName",
                        channel.channelName
                    )

                    put(
                        "channelId",
                        channel.channelId
                    )

                    put(
                        "joinSecret",
                        channel.joinSecret
                    )

                    put(
                        "cryptoSeed",
                        channel.cryptoSeed
                    )

                    put(
                        "createdAt",
                        channel.createdAt
                    )
                }

            val raw =
                json.toString()

            Base64.encodeToString(
                raw.toByteArray(),
                Base64.NO_WRAP
            )

        } catch (_: Exception) {

            ""
        }
    }

    fun decodeChannel(
        payload: String
    ): Channel? {

        return try {

            val raw =
                String(
                    Base64.decode(
                        payload,
                        Base64.NO_WRAP
                    )
                )

            val json =
                JSONObject(raw)

            Channel(
                channelName =
                    json.getString(
                        "channelName"
                    ),

                channelId =
                    json.getString(
                        "channelId"
                    ),

                joinSecret =
                    json.getString(
                        "joinSecret"
                    ),

                cryptoSeed =
                    json.getString(
                        "cryptoSeed"
                    ),

                createdAt =
                    json.getLong(
                        "createdAt"
                    )
            )

        } catch (_: Exception) {

            null
        }
    }
}
