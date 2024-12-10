package com.fcfb.discord.health.model.discord

data class DiscordProperties(
    val token: String,
    val guildId: String,
    val healthChannelId: String,
    val botId: String,
)
