package com.severett.statscollector

import com.severett.statscollector.config.RabbitMQConfig
import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class StatsCollector(private val rabbitMQConfig: RabbitMQConfig) : CommandLineRunner {
    private val logger = KotlinLogging.logger { }

    override fun run(vararg args: String) {
        logger.info { "Starting application" }
        try {
            logger.info { "Connecting to ${rabbitMQConfig.url} as user ${rabbitMQConfig.userName}" }
            MqttClient(rabbitMQConfig.url, "StatsCollector", MemoryPersistence()).use { mqttClient ->
                try {
                    val connectOptions = MqttConnectOptions().apply {
                        isAutomaticReconnect = true
                        userName = rabbitMQConfig.userName
                        password = rabbitMQConfig.password.toCharArray()
                    }
                    mqttClient.connect(connectOptions)
                } finally {
                    mqttClient.disconnect()
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error while running StatsCollector" }
        }
        logger.info { "Shutting down..." }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(StatsCollector::class.java, *args)
}
