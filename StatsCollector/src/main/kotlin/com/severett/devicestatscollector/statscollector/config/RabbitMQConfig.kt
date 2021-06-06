package com.severett.devicestatscollector.statscollector.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("statscollector.rabbitmq")
open class RabbitMQConfig(
    var url: String = "",
    var userName: String = "",
    var password: String = "",
    var topic: String = ""
)
