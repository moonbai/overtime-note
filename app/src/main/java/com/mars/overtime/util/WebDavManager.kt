package com.mars.overtime.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

object WebDavManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    data class WebDavConfig(
        val baseUrl: String,
        val username: String,
        val password: String,
        val remotePath: String = "/overtime_backup/"
    )

    private fun createCredentials(config: WebDavConfig): String {
        val credentials = "${config.username}:${config.password}"
        return "Basic " + android.util.Base64.encodeToString(credentials.toByteArray(), android.util.Base64.NO_WRAP)
    }

    suspend fun uploadFile(config: WebDavConfig, localFilePath: String, remoteFileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = java.io.File(localFilePath)
            if (!file.exists()) return@withContext false

            val url = "${config.baseUrl}${config.remotePath}$remoteFileName".removeSuffix("/") + "/" + remoteFileName
            val mediaType = "application/json".toMediaType()
            val body = file.readBytes().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", createCredentials(config))
                .header("Content-Type", "application/json")
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful || response.code == 201 || response.code == 204
            Log.d("WebDavManager", "上传结果: ${response.code}")
            response.close()
            success
        } catch (e: Exception) {
            Log.e("WebDavManager", "上传失败", e)
            false
        }
    }

    suspend fun downloadFile(config: WebDavConfig, remoteFileName: String, localFilePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "${config.baseUrl}${config.remotePath}$remoteFileName".removeSuffix("/") + "/" + remoteFileName

            val request = Request.Builder()
                .url(url)
                .get()
                .header("Authorization", createCredentials(config))
                .build()

            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                response.body?.bytes()?.let { bytes ->
                    java.io.File(localFilePath).writeBytes(bytes)
                    response.close()
                    return@withContext true
                }
            }
            response.close()
            false
        } catch (e: Exception) {
            Log.e("WebDavManager", "下载失败", e)
            false
        }
    }

    suspend fun listFiles(config: WebDavConfig): List<String> = withContext(Dispatchers.IO) {
        try {
            val url = "${config.baseUrl}${config.remotePath}".removeSuffix("/") + "/"
            val request = Request.Builder()
                .url(url)
                .method("PROPFIND", null)
                .header("Authorization", createCredentials(config))
                .header("Depth", "1")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            response.close()

            val pattern = Regex("<D:href>[^<]*/([^<]+\\.json)</D:href>")
            pattern.findAll(body).map { it.groupValues[1] }.distinct().toList()
        } catch (e: Exception) {
            Log.e("WebDavManager", "列出文件失败", e)
            emptyList()
        }
    }

    suspend fun testConnection(config: WebDavConfig): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = "${config.baseUrl}${config.remotePath}".removeSuffix("/") + "/"
            val request = Request.Builder()
                .url(url)
                .method("PROPFIND", null)
                .header("Authorization", createCredentials(config))
                .header("Depth", "0")
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful
            response.close()
            success
        } catch (e: Exception) {
            Log.e("WebDavManager", "连接测试失败", e)
            false
        }
    }
}
