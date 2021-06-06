package com.severett.devicestatscollector.statscollector.service

import com.severett.devicestatscollector.common.model.SystemReport
import com.severett.devicestatscollector.statscollector.config.RabbitMQConfig
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.jpountz.lz4.LZ4Factory
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CountDownLatch
import javax.annotation.PreDestroy

@Service
class CollectorService(private val rabbitMQConfig: RabbitMQConfig) {
    private val logger = KotlinLogging.logger { }
    private val runningLatch = CountDownLatch(1)
    private val decompressor = LZ4Factory.fastestJavaInstance().fastDecompressor()

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
                    mqttClient.subscribe(rabbitMQConfig.topic) { _, message ->
                        logger.info { "Received message of length ${message.payload.size}" }
                        val sizePrepend = message.payload.slice(0 until 4)
                        val compressedPayload = message.payload.slice(4 until message.payload.size)
                        val uncompressedSize = ByteBuffer.wrap(sizePrepend.toByteArray())
                            .order(ByteOrder.LITTLE_ENDIAN)
                            .int
                        val decompressedPayload =
                            decompressor.decompress(compressedPayload.toByteArray(), uncompressedSize)
                        val decompressedString = decompressedPayload.toString(Charsets.UTF_8)
                        val systemReport: SystemReport = Json.decodeFromString(decompressedString)
                        logger.info { "Decompressed Report: $systemReport" }
                    }
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
