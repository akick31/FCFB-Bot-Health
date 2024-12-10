package com.fcfb.discord.health.handlers.discord

import com.fcfb.discord.health.utils.Logger
import com.fcfb.discord.health.utils.Properties
import dev.kord.common.entity.Snowflake
import dev.kord.core.Kord
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.entity.Message
import dev.kord.core.entity.channel.MessageChannel
import dev.kord.core.entity.channel.thread.TextChannelThread

class DiscordMessageHandler(
    private val properties: Properties,
) {
    suspend fun sendBotDownMessage(
        client: Kord,
        botName: String,
    ) {
        val healthChannel = client.getChannel(Snowflake(properties.getDiscordProperties().healthChannelId)) as MessageChannel
        val messageContent = "ALERT: $botName is down"
        sendMessageFromChannelObject(healthChannel, messageContent)
    }

    suspend fun sendBotUpMessage(
        client: Kord,
        botName: String,
    ) {
        val healthChannel = client.getChannel(Snowflake(properties.getDiscordProperties().healthChannelId)) as MessageChannel
        val messageContent = "$botName is running and healthy"
        sendMessageFromChannelObject(healthChannel, messageContent)
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
