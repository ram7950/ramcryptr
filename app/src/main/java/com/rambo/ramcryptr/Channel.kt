package com.rambo.ramcryptr

data class Channel(

    val channelName: String,

    val channelId: String,

    val joinSecret: String,

    val cryptoSeed: String =
        java.util.UUID.randomUUID()
            .toString(),

    val createdAt: Long =
        System.currentTimeMillis()
)
