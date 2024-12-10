package com.fcfb.discord.health.service

import com.fcfb.discord.health.api.ArceusClient
import com.fcfb.discord.health.api.RefBotClient
import com.fcfb.discord.health.api.RotomClient
import com.fcfb.discord.health.handlers.discord.DiscordMessageHandler
import com.fcfb.discord.health.utils.Logger
import dev.kord.core.Kord

class MonitorBotHealth(
    private val arceusClient: ArceusClient,
    private val refBotClient: RefBotClient,
    private val rotomClient: RotomClient,
    private val discordMessageHandler: DiscordMessageHandler,
) {
    suspend fun checkBotHealth(client: Kord) {
        Logger.info("Checking bot health")
        val arceusHealth = arceusClient.getHealth()
        val refBotHealth = refBotClient.getHealth()
        val rotomHealth = rotomClient.getHealth()

        if (arceusHealth != "Application is healthy") {
            discordMessageHandler.editArceusBotMessage(client, false)
        } else {
            discordMessageHandler.editArceusBotMessage(client, true)
        }

        if (refBotHealth?.status != "UP") {
            discordMessageHandler.editRefBotMessage(client, false)
        } else {
            discordMessageHandler.editRefBotMessage(client, true)
        }

        if (rotomHealth?.status != "UP") {
            discordMessageHandler.editRotomMessage(client, false)
        } else {
            discordMessageHandler.editRotomMessage(client, true)
        }

        Logger.info("Bot health check completed at ${System.currentTimeMillis()}")
    }
}
