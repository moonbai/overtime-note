package com.mars.overtime.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
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

    private fun buildUrl(config: WebDavConfig, fileName: String): String {
        var base = config.baseUrl.trimEnd('/')
        var path = config.remotePath.trimStart('/')
        if (!path.endsWith('/')) {
            path = "$path/"
        }
        return "$base/$path$fileName"
    }

    private fun buildListUrl(config: WebDavConfig): String {
        var base = config.baseUrl.trimEnd('/')
        var path = config.remotePath.trimStart('/')
        if (!path.endsWith('/')) {
            path = "$path/"
        }
        return "$base/$path"
    }

    suspend fun uploadFile(config: WebDavConfig, localFilePath: String, remoteFileName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = java.io.File(localFilePath)
            if (!file.exists()) {
                Log.e("WebDavManager", "本地文件不存在: $localFilePath")
                return@withContext false
            }

            val url = buildUrl(config, remoteFileName)
            Log.d("WebDavManager", "上传到: $url")
            
            val mediaType = "application/json".toMediaType()
            val body = file.readBytes().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .put(body)
                .header("Authorization", createCredentials(config))
                .header("Content-Type", "application/json")
                .header("Expect", "100-continue")
                .build()

            val response = client.newCall(request).execute()
            val code = response.code
            val success = response.isSuccessful || code == 201 || code == 204
            Log.d("WebDavManager", "上传结果: $code, success: $success")
            response.close()
            success
        } catch (e: Exception) {
            Log.e("WebDavManager", "上传失败", e)
            false
        }
    }

    suspend fun downloadFile(config: WebDavConfig, remoteFileName: String, localFilePath: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildUrl(config, remoteFileName)
            Log.d("WebDavManager", "下载从: $url")

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
                    Log.d("WebDavManager", "下载成功")
                    return@withContext true
                }
            }
            Log.d("WebDavManager", "下载失败: ${response.code}")
            response.close()
            false
        } catch (e: Exception) {
            Log.e("WebDavManager", "下载失败", e)
            false
        }
    }

    suspend fun listFiles(config: WebDavConfig): List<String> = withContext(Dispatchers.IO) {
        try {
            val url = buildListUrl(config)
            Log.d("WebDavManager", "列出文件从: $url")
            
            val request = Request.Builder()
                .url(url)
                .method("PROPFIND", null)
                .header("Authorization", createCredentials(config))
                .header("Depth", "1")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string() ?: ""
            Log.d("WebDavManager", "列表响应长度: ${body.length}")
            response.close()

            val pattern = Regex("<D:href>([^<]*)</D:href>")
            val files = pattern.findAll(body)
                .map { it.groupValues[1] }
                .filter { it.endsWith(".json") || it.contains("overtime") }
                .map { it.substringAfterLast('/') }
                .distinct()
                .toList()
            
            Log.d("WebDavManager", "找到文件: $files")
            files
        } catch (e: Exception) {
            Log.e("WebDavManager", "列出文件失败", e)
            emptyList()
        }
    }

    suspend fun testConnection(config: WebDavConfig): Boolean = withContext(Dispatchers.IO) {
        try {
            val url = buildListUrl(config)
            Log.d("WebDavManager", "测试连接: $url")
            
            val request = Request.Builder()
                .url(url)
                .method("PROPFIND", null)
                .header("Authorization", createCredentials(config))
                .header("Depth", "0")
                .build()

            val response = client.newCall(request).execute()
            val success = response.isSuccessful || response.code == 207
            Log.d("WebDavManager", "连接测试: ${response.code}, success: $success")
            response.close()
            success
        } catch (e: Exception) {
            Log.e("WebDavManager", "连接测试失败", e)
            false
        }
    }
}