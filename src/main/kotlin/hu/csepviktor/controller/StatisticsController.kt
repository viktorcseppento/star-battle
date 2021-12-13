package hu.csepviktor.controller

import hu.csepviktor.Statistics
import hu.csepviktor.StatisticsModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tornadofx.*
import java.io.File
import kotlin.experimental.xor

class StatisticsController : Controller() {

    private val statisticsModel: StatisticsModel by inject()
    private val statisticsPath = "data/statistics"

    private val secretString = "This string is used to encrypt/decrypt the game statistics.".encodeToByteArray()

    private fun ByteArray.encryptDecrypt(): ByteArray {
        this.forEachIndexed { i, byte ->
            this[i] = byte xor secretString[i % secretString.size]
        }
        return this
    }

    private fun createFile() {
        File(statisticsPath.substringBeforeLast('/')).mkdirs()
        File(statisticsPath).createNewFile()
    }

    fun readStatistics(): StatisticsModel {
        val file = File(statisticsPath)
        val statistics = try {
            if (file.exists()) Json.decodeFromString(file.readBytes().encryptDecrypt().decodeToString())
            else Statistics()
        } catch (_: SerializationException) {
            Statistics()
        }

        return StatisticsModel(statistics)
    }

    fun writeStatistics() {
        val file = File(statisticsPath)
        if (!file.exists()) createFile()
        file.writeBytes(Json.encodeToString(statisticsModel.item).encodeToByteArray().encryptDecrypt())
    }
}

