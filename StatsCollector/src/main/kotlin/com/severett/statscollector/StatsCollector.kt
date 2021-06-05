package com.severett.statscollector

import mu.KotlinLogging
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class StatsCollector : CommandLineRunner {
    private val logger = KotlinLogging.logger { }

    override fun run(vararg args: String) {
        logger.info { "Starting application" }
        logger.info { "Shutting down..." }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(StatsCollector::class.java, *args)
}
