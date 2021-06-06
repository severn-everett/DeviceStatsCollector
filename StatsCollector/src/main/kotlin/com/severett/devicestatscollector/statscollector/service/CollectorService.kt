package com.severett.devicestatscollector.statscollector.service

import com.severett.devicestatscollector.statscollector.config.RabbitMQConfig
import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.stereotype.Service
import java.util.concurrent.CountDownLatch
import javax.annotation.PreDestroy

@Service
class CollectorService(
    private val rabbitMQConfig: RabbitMQConfig,
    private val messageProcessingService: MessageProcessingService
) {
    private val logger = KotlinLogging.logger { }
    private val runningLatch = CountDownLatch(1)

    fun run() {
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
                    mqttClient.subscribe(rabbitMQConfig.topic, messageProcessingService)
                    runningLatch.await()
                } finally {
                    mqttClient.disconnect()
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Unexpected error while running StatsCollector" }
        }
    }

    @PreDestroy
    fun shutdown() {
        logger.info { "Initiating shutdown" }
        runningLatch.countDown()
    }
}
