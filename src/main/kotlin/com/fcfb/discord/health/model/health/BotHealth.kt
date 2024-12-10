package com.fcfb.discord.health.model.health

data class BotHealth(
    val status: String,
    val jobs: Map<String, Boolean>?,
    val memory: Map<String, String>?,
    val diskSpace: Map<String, String>?,
    val kord: Map<String, String>?,
    val message: String? = null,
)
