package com.fcfb.discord.health.utils

import com.fcfb.discord.health.FCFBBotHealth
import com.fcfb.discord.health.model.discord.DiscordProperties
import dev.kord.common.annotation.KordPreview

class Properties {
    @OptIn(KordPreview::class)
    fun getDiscordProperties(): DiscordProperties {
        val properties = java.util.Properties()
        val configFile = FCFBBotHealth::class.java.classLoader.getResourceAsStream("application.properties")
        properties.load(configFile)
        val token = properties.getProperty("discord.bot.token")
        val guildId = properties.getProperty("discord.guild.id")
        val healthChannelId = properties.getProperty("discord.health.channel.id")
        val arceusStatusMessageId = properties.getProperty("discord.arceus.status.message.id")
        val refbotStatusMessageId = properties.getProperty("discord.refbot.status.message.id")
        val rotomStatusMessageId = properties.getProperty("discord.rotom.status.message.id")
        val botId = properties.getProperty("discord.bot.id")
        return DiscordProperties(
            token,
            guildId,
            healthChannelId,
            arceusStatusMessageId,
            refbotStatusMessageId,
            rotomStatusMessageId,
            botId,
        )
    }

    @OptIn(KordPreview::class)
    fun getServerPort(): Int {
        val properties = java.util.Properties()
        val configFile = FCFBBotHealth::class.java.classLoader.getResourceAsStream("application.properties")
        properties.load(configFile)
        return properties.getProperty("server.port").toInt()
    }
}
