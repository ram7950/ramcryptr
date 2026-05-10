package com.rambo.ramcryptr

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

object ChannelStorage {

    private const val CHANNEL_DIR = "channels"
    private const val ACTIVE_FILE = "active_channel.txt"

    private fun getChannelFolder(context: Context): File {

        val dir = File(context.filesDir, CHANNEL_DIR)

        if (!dir.exists()) {
            dir.mkdirs()
        }

        return dir
    }

    fun saveChannel(
        context: Context,
        channel: Channel
    ) {

        val file = File(
            getChannelFolder(context),
            "${channel.channelId}.json"
        )

        val json = JSONObject().apply {

            put("channelName", channel.channelName)
            put("channelId", channel.channelId)
            put("joinSecret", channel.joinSecret)
            put("createdAt", channel.createdAt)
            put("activeVersion", channel.activeVersion)
        }

        file.writeText(json.toString())
    }

    fun loadChannels(
        context: Context
    ): List<Channel> {

        val dir = getChannelFolder(context)

        return dir.listFiles()?.mapNotNull { file ->

            try {

                val json = JSONObject(file.readText())

                Channel(
                    channelName = json.getString("channelName"),
                    channelId = json.getString("channelId"),
                    joinSecret = json.getString("joinSecret"),
                    createdAt = json.getLong("createdAt"),
                    activeVersion = json.getString("activeVersion")
                )

            } catch (e: Exception) {
                null
            }

        } ?: emptyList()
    }

    fun deleteChannel(
        context: Context,
        channelId: String
    ) {

        val file = File(
            getChannelFolder(context),
            "$channelId.json"
        )

        if (file.exists()) {
            file.delete()
        }
    }

    fun saveActiveChannel(
        context: Context,
        channelId: String
    ) {

        val file = File(
            context.filesDir,
            ACTIVE_FILE
        )

        file.writeText(channelId)
    }

    fun getActiveChannelId(
        context: Context
    ): String? {

        val file = File(
            context.filesDir,
            ACTIVE_FILE
        )

        return if (file.exists()) {
            file.readText()
        } else {
            null
        }
    }
}
