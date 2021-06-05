package com.severett.statscollector

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class StatsCollector

fun main(args: Array<String>) {
    SpringApplication.run(StatsCollector::class.java, *args)
}
