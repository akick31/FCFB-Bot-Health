package com.fcfb.discord.health.service

import com.fcfb.discord.health.api.ArceusClient
import com.fcfb.discord.health.api.RefBotClient
import com.fcfb.discord.health.api.RotomClient
import com.fcfb.discord.health.handlers.discord.DiscordMessageHandler
import dev.kord.core.Kord

class MonitorBotHealth(
    private val arceusClient: ArceusClient,
    private val refBotClient: RefBotClient,
    private val rotomClient: RotomClient,
    private val discordMessageHandler: DiscordMessageHandler,
) {
    suspend fun checkBotHealth(client: Kord) {
        val arceusHealth = arceusClient.getHealth()
        val refBotHealth = refBotClient.getHealth()
        val rotomHealth = rotomClient.getHealth()

        if (arceusHealth != "Application is healthy") {
            discordMessageHandler.sendBotDownMessage(client, "Arceus Backend Service")
        } else {
            discordMessageHandler.sendBotUpMessage(client, "Arceus Backend Service")
        }

        if (refBotHealth?.status != "UP") {
            discordMessageHandler.sendBotDownMessage(client, "Discord Ref Bot")
        } else {
            discordMessageHandler.sendBotUpMessage(client, "Discord Ref Bot")
        }

        if (rotomHealth?.status != "UP") {
            discordMessageHandler.sendBotDownMessage(client, "Rotom Ping Service")
        } else {
            discordMessageHandler.sendBotUpMessage(client, "Rotom Ping Service")
        }
    }
}
