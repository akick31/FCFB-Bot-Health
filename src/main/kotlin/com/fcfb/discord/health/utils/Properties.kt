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
        val botId = properties.getProperty("discord.bot.id")
        return DiscordProperties(
            token,
            guildId,
            healthChannelId,
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
