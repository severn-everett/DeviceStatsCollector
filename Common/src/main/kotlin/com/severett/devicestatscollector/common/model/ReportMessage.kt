package com.severett.devicestatscollector.common.model

import com.severett.devicestatscollector.common.serde.InstantSerializer
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ReportMessage(
    val deviceId: String,
    val messageId: String,
    @Serializable(with = InstantSerializer::class)
    val timestamp: Instant,
    val report: SystemReport
)
