package com.rambo.ramcryptr

import android.content.Context

object ChannelManager {

    private var activeChannel: Channel? = null

    fun initialize(
        context: Context
    ) {

        val activeId = ChannelStorage
            .getActiveChannelId(context)

        val channels = ChannelStorage
            .loadChannels(context)

        activeChannel = channels.find {
            it.channelId == activeId
        }
    }

    fun setActiveChannel(
        context: Context,
        channel: Channel
    ) {

        activeChannel = channel

        ChannelStorage.saveActiveChannel(
            context,
            channel.channelId
        )
    }

    fun getActiveChannel(): Channel? {
        return activeChannel
    }

    fun clearActiveChannel() {
        activeChannel = null
    }

    fun getAllChannels(
        context: Context
    ): List<Channel> {

        return ChannelStorage
            .loadChannels(context)
    }

    fun deleteChannel(
        context: Context,
        channel: Channel
    ) {

        ChannelStorage.deleteChannel(
            context,
            channel.channelId
        )

        if (
            activeChannel?.channelId ==
            channel.channelId
        ) {
            clearActiveChannel()
        }
    }
}
