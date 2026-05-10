package com.rambo.ramcryptr

data class Channel(

    val channelName: String,

    val channelId: String,

    val joinSecret: String,

    val createdAt: Long = System.currentTimeMillis(),

    val activeVersion: String = "K1"
)
