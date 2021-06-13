package com.severett.devicestatscollector.statscollector.service

import com.severett.devicestatscollector.common.model.ReportMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import mu.KotlinLogging
import net.jpountz.lz4.LZ4Factory
import org.eclipse.paho.client.mqttv3.IMqttMessageListener
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.springframework.stereotype.Service
import java.nio.ByteBuffer
import java.nio.ByteOrder

@Service
class MessageProcessingService : IMqttMessageListener {
    private val logger = KotlinLogging.logger { }
    private val decompressor = LZ4Factory.fastestJavaInstance().fastDecompressor()

    override fun messageArrived(topic: String, message: MqttMessage) {
        // TODO Enable logging during testing.
        logger.debug { "Received message ${message.id} of length ${message.payload.size}" }
        if (message.payload.size >= SIZE_PREPEND_END) {
            val systemReport = deserializeReport(message.payload)
            logger.info { "Decompressed Report: $systemReport" }
        } else {
            logger.warn { "Message ${message.id} is not long enough - skipping..." }
        }
    }

    private fun deserializeReport(payload: ByteArray): ReportMessage {
        val sizePrepend = payload.sliceArray(0 until SIZE_PREPEND_END)
        val compressedPayload = payload.sliceArray(SIZE_PREPEND_END until payload.size)
        val uncompressedSize = ByteBuffer.wrap(sizePrepend)
            .order(ByteOrder.LITTLE_ENDIAN)
            .int
        return if (uncompressedSize <= MAX_MESSAGE_SIZE) {
            val decompressedPayload = decompressor.decompress(compressedPayload, uncompressedSize)
            Json.decodeFromString(decompressedPayload.toString(Charsets.UTF_8))
        } else {
            throw IllegalArgumentException("Uncompressed payload size $uncompressedSize exceeds limit of $MAX_MESSAGE_SIZE")
        }
    }

    private companion object {
        private const val SIZE_PREPEND_END = 4
        private const val MAX_MESSAGE_SIZE = 5_000
    }
}
