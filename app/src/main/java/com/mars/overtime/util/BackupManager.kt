package com.mars.overtime.util

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mars.overtime.database.AppConfig
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class BackupData(
    val records: List<OvertimeRecord> = emptyList(),
    val configs: List<AppConfig> = emptyList()
)

object BackupManager {
    private val gson: Gson = GsonBuilder()
        .setPrettyPrinting()
        .setDateFormat("yyyy-MM-dd HH:mm:ss")
        .create()

    fun exportData(records: List<OvertimeRecord>, configs: List<AppConfig>, filePath: String): Boolean {
        return try {
            val data = BackupData(records, configs)
            val json = gson.toJson(data)
            val file = File(filePath)
            FileWriter(file).use { writer ->
                writer.write(json)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun importData(filePath: String): BackupData? {
        return try {
            val file = File(filePath)
            if (!file.exists()) return null
            val reader = FileReader(file)
            gson.fromJson(reader, BackupData::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
