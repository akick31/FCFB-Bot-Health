package com.fcfb.discord.health

import com.fcfb.discord.health.koin.appModule
import com.fcfb.discord.health.service.MonitorBotHealth
import com.fcfb.discord.health.utils.Logger
import com.fcfb.discord.health.utils.Properties
import dev.kord.common.annotation.KordPreview
import dev.kord.core.Kord
import dev.kord.gateway.Heartbeat
import dev.kord.gateway.Intent
import dev.kord.gateway.PrivilegedIntent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.mp.KoinPlatform.getKoin
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

@KordPreview
class FCFBBotHealth(
    private val properties: Properties,
    private val monitorBotHealth: MonitorBotHealth,
) {
    private lateinit var client: Kord
    private var heartbeatJob: Job? = null
    private var monitorHealthJob: Job? = null
    private var restartJob: Job? = null

    /**
     * Start the Discord bot and it's services
     */
    fun start() =
        runBlocking {
            try {
                startHeartbeat()
                startRestartJob()
                initializeBot()
                startMonitoringHealth()
                startServices()
            } catch (e: Exception) {
                Logger.error("Failed to start bot: ${e.message}", e)
            }
        }

    /**
     * Start a coroutine to send regular heartbeats to Discord
     */
    private fun startHeartbeat() {
        heartbeatJob?.cancel() // Cancel any existing heartbeat job
        heartbeatJob =
            CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    delay(15.seconds)
                    try {
                        // Attempt to fetch the bot's own user info as a "heartbeat" check
                        Heartbeat(15)
                    } catch (e: Exception) {
                        Logger.warn("Heartbeat failed: Bot appears disconnected. Attempting to reconnect...")
                        restartMonitoringHealth()
                    }
                }
            }
    }

    /**
     * Schedule a restart for 4 AM EST every day
     */
    private fun startRestartJob() {
        restartJob?.cancel() // Cancel any existing restart job
        restartJob =
            CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    val now = ZonedDateTime.now(ZoneId.of("America/New_York"))
                    val nextRestart = now.withHour(4).withMinute(0).withSecond(0).withNano(0)
                    val delay =
                        if (now.isAfter(nextRestart)) {
                            ChronoUnit.MILLIS.between(now, nextRestart.plusDays(1))
                        } else {
                            ChronoUnit.MILLIS.between(now, nextRestart)
                        }
                    Logger.info("Next restart scheduled in ${delay / 1000 / 60} minutes.")
                    delay(delay)
                    Logger.info("Restarting bot for daily maintenance...")
                    restartMonitoringHealth()
                }
            }
    }

    /**
     * Start monitoring bot health
     */
    private fun startMonitoringHealth() {
        monitorHealthJob?.cancel() // Cancel any existing health monitor job
        monitorHealthJob =
            CoroutineScope(Dispatchers.IO).launch {
                while (isActive) {
                    delay(5.seconds)
                    monitorBotHealth.checkBotHealth(client)
                }
            }
    }

    /**
     * Restart the Discord bot
     */
    private suspend fun restartMonitoringHealth() {
        try {
            logoutOfDiscord()
            startMonitoringHealth()
            Logger.info("Bot restarted successfully.")
        } catch (e: Exception) {
            Logger.error("Failed to restart bot: ${e.message}", e)
        }
    }

    /**
     * Clean up any resources, including heartbeat job
     */
    fun stopJobs() {
        heartbeatJob?.cancel()
        monitorHealthJob?.cancel()
        restartJob?.cancel()
        Logger.info("FCFB Discord Ref Bot stopped.")
    }

    /**
     * Initialize the Discord bot with Kord
     */
    private suspend fun initializeBot() {
        client = Kord(properties.getDiscordProperties().token)
        Logger.info("Bot Health Monitor initialized successfully!")
    }

    /**
     * Start the Ktor server and Discord bot
     */
    private fun startServices(
    ) = runBlocking {
        launch {
            loginToDiscord()
        }
    }

    /**
     * Login to Discord
     */
    private suspend fun loginToDiscord() {
        Logger.info("Logging into the Bot Health Monitor...")
        client.login {
            @OptIn(PrivilegedIntent::class)
            intents += Intent.MessageContent
        }
        Logger.info("Bot Health Monitor logged in successfully!")
    }

    /**
     * Stop the Discord bot
     */
    private suspend fun logoutOfDiscord() {
        Logger.info("Shutting down the Bot Health Monitor...")
        try {
            client.logout()
            client.shutdown()
        } catch (e: Exception) {
            Logger.warn("Failed to logout of Discord: ${e.message}")
        }
        Logger.info("Bot Health Monitor shut down successfully!")
    }
}

@OptIn(KordPreview::class)
fun main() {
    Logger.info("Starting Bot Health Monitor...")

    // Dependency injection
    startKoin {
        modules(appModule)
    }

    val bot: FCFBBotHealth = getKoin().get()
    bot.start()
    Runtime.getRuntime().addShutdownHook(Thread { bot.stopJobs() })
}
