package com.severett.devicestatscollector.common.model

import kotlinx.serialization.Serializable

@Serializable
data class SystemReport(val disks: List<DiskReport>, val cpus: List<CPUReport>, val memory: MemoryReport) {
    @Serializable
    data class DiskReport(val name: String, val diskUsed: Long, val diskCapacity: Long)
    @Serializable
    data class CPUReport(val name: String, val brand: String, val vendorId: String, val frequency: Long, val usage: Double)
    @Serializable
    data class MemoryReport(val memoryUsed: Long, val memoryCapacity: Long)
}
