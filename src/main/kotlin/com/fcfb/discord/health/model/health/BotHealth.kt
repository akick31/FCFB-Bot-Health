package com.fcfb.discord.health.model.health

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy::class)
data class BotHealth(
    @JsonProperty("status") var status: String,
    @JsonProperty("jobs") var jobs: Map<String, Boolean>?,
    @JsonProperty("memory") var memory: Map<String, String>?,
    @JsonProperty("disk_space") var diskSpace: Map<String, String>?,
    @JsonProperty("kord") var kord: Map<String, String>?,
    @JsonProperty("message") var message: String?
)
