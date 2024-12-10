package com.fcfb.discord.health.koin

import com.fcfb.discord.health.FCFBBotHealth
import com.fcfb.discord.health.api.ArceusClient
import com.fcfb.discord.health.api.RefBotClient
import com.fcfb.discord.health.api.RotomClient
import com.fcfb.discord.health.handlers.discord.DiscordMessageHandler
import com.fcfb.discord.health.service.MonitorBotHealth
import com.fcfb.discord.health.utils.Properties
import dev.kord.common.annotation.KordPreview
import org.koin.dsl.module

@OptIn(KordPreview::class)
val appModule =
    module {
        single { Properties() }
        single { ArceusClient() }
        single { RefBotClient() }
        single { RotomClient() }

        // Classes with dependencies
        single { DiscordMessageHandler(get()) }
        single { MonitorBotHealth(get(), get(), get(), get()) }
        single { FCFBBotHealth(get(), get()) }
    }
