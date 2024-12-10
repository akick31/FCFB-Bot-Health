package com.fcfb.discord.health.handlers.discord

import com.fcfb.discord.health.utils.Logger
import com.fcfb.discord.health.utils.Properties
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.edit
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.thread.TextChannelThread

class DiscordMessageHandler(
    private val properties: Properties,
) {
    /**
     * Edit the Arceus status message
     * @param client The Discord client
     * @param status The status of the Arceus service
     */
    suspend fun editArceusBotMessage(
        client: Kord,
        status: Boolean,
    ) {
        val channel = client.getChannel(Snowflake(properties.getDiscordProperties().healthChannelId)) as MessageChannel
        val message = channel.getMessage(Snowflake(properties.getDiscordProperties().arceusStatusMessageId))
        var messageContent = "**Arceus Status:** "
        messageContent +=
            if (status) {
                "UP"
            } else {
                "DOWN"
            }
        editMessage(message, messageContent)
    }

    /**
     * Edit the Ref Bot status message
     * @param client The Discord client
     * @param status The status of the Ref Bot service
     */
    suspend fun editRefBotMessage(
        client: Kord,
        status: Boolean,
    ) {
        val channel = client.getChannel(Snowflake(properties.getDiscordProperties().healthChannelId)) as MessageChannel
        val message = channel.getMessage(Snowflake(properties.getDiscordProperties().refbotStatusMessageId))
        var messageContent = "**Discord Ref Bot Status:** "
        messageContent +=
            if (status) {
                "UP"
            } else {
                "DOWN"
            }
        editMessage(message, messageContent)
    }

    /**
     * Edit the Rotom message
     * @param client The Discord client
     * @param status The status of the Rotom service
     */
    suspend fun editRotomMessage(
        client: Kord,
        status: Boolean,
    ) {
        val channel = client.getChannel(Snowflake(properties.getDiscordProperties().healthChannelId)) as MessageChannel
        val message = channel.getMessage(Snowflake(properties.getDiscordProperties().rotomStatusMessageId))
        var messageContent = "**Rotom Ping Service Status:** "
        messageContent +=
            if (status) {
                "UP"
            } else {
                "DOWN"
            }
        editMessage(message, messageContent)
    }

    /**
     * Edit a message
     */
    private suspend fun editMessage(
        message: Message?,
        messageContent: String,
    ) {
        message?.edit {
            content = messageContent
        }
    }

    /**
     * Send a message to a game thread via a message object
     * @param message The message object
     * @param messageContent The message content
     */
    private suspend fun sendMessageFromMessageObject(
        message: Message?,
        messageContent: String,
    ): Message? {
        val submittedMessage =
            message?.let {
                it.getChannel().createMessage {
                    content = messageContent
                }
            } ?: run {
                Logger.error("There was an error sending a message from a message object")
                null
            }
        return submittedMessage
    }

    /**
     * Send a message to a game thread via a text channel object
     * @param channel The text channel object
     * @param messageContent The message content
     */
    private suspend fun sendMessageFromChannelObject(
        channel: MessageChannel,
        messageContent: String,
    ): Message {
        val submittedMessage =
            channel.createMessage {
                content = messageContent
            }

        return submittedMessage
    }

    /**
     * Send a message to a game thread via a text channel object
     * @param textChannel The text channel object
     * @param messageContent The message content
     */
    private suspend fun sendMessageFromTextChannelObject(
        textChannel: TextChannelThread?,
        messageContent: String,
    ): Message? {
        val submittedMessage =
            textChannel?.let {
                it.createMessage {
                    content = messageContent
                }
            } ?: run {
                Logger.error("There was an error sending a message from a text channel object")
                null
            }

        return submittedMessage
    }
}
