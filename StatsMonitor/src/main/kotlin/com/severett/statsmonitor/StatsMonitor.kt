package com.severett.statsmonitor

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class StatsMonitor

fun main(args: Array<String>) {
    SpringApplication.run(StatsMonitor::class.java, *args)
}
