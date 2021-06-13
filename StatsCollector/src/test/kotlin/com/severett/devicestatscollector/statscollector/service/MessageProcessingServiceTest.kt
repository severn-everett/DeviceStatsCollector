package com.severett.devicestatscollector.statscollector.service

import org.eclipse.paho.client.mqttv3.MqttMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class MessageProcessingServiceTest {

    private val messageProcessingService = MessageProcessingService()

    @Test
    fun testParseValidMessage() {
        val payload = javaClass.getResourceAsStream("/messages/good/SampleMessage.lz4")!!.use { it.readAllBytes() }
        val mqttMessage = MqttMessage(payload)
        assertDoesNotThrow {
            messageProcessingService.messageArrived("TEST", mqttMessage)
        }
    }

    @ParameterizedTest
    @ValueSource(strings = ["ImproperFormatMessage.lz4"])
    fun testParseInvalidMessage(messageFile: String) {
        val payload = javaClass.getResourceAsStream("/messages/bad/$messageFile")!!.use { it.readAllBytes() }
        val mqttMessage = MqttMessage(payload)
        assertThrows<Exception> {
            messageProcessingService.messageArrived("TEST", mqttMessage)
        }
    }
}