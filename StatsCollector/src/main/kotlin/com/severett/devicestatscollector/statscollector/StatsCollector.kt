package com.severett.devicestatscollector.statscollector

import com.severett.devicestatscollector.statscollector.config.RabbitMQConfig
import com.severett.devicestatscollector.statscollector.service.CollectorService
import mu.KotlinLogging
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import java.util.concurrent.atomic.AtomicBoolean
import javax.annotation.PreDestroy

@SpringBootApplication
open class StatsCollector(private val collectorService: CollectorService) : CommandLineRunner {
    private val logger = KotlinLogging.logger { }

    override fun run(vararg args: String) {
        logger.info { "Starting application" }
        collectorService.run()
        logger.info { "Shutting down..." }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(StatsCollector::class.java, *args)
}
