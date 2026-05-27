package com.mars.overtime.push

import android.util.Log
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

object PushManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(15, java.util.concurrent.TimeUnit.SECONDS)
        .build()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    fun buildText(record: OvertimeRecord): String {
        val typeStr = when (record.type) {
            OvertimeType.WORKDAY -> "工作日"
            OvertimeType.RESTDAY -> "休息日"
            OvertimeType.HOLIDAY -> "节假日"
            OvertimeType.LEAVE_HALF -> "请假(半天)"
            OvertimeType.LEAVE_FULL -> "请假(全天)"
        }
        val reason = record.remark.takeIf { it.isNotBlank() } ?: "无"
        return """日期: ${record.date}
类型: $typeStr
时间: ${record.startTime}-${record.endTime}
加班时长: ${"%.2f".format(record.duration)}
金额: ¥${"%.2f".format(record.money)}
加班事由: $reason""".trimIndent()
    }

    suspend fun sendDingTalk(url: String, record: OvertimeRecord): Boolean = withContext(Dispatchers.IO) {
        val text = buildText(record)
        val json = """{"msgtype":"text","text":{"content":"${escapeJson(text)}"}}"""
        try {
            val body = json.toRequestBody(mediaType)
            val req = Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
            val res = client.newCall(req).execute()
            val response = res.body?.string()
            Log.d("PushManager", "钉钉推送响应: $response")
            res.close()
            res.isSuccessful && response?.contains("\"errcode\":0") == true
        } catch (e: Exception) {
            Log.e("PushManager", "钉钉推送失败", e)
            false
        }
    }

    suspend fun sendFeishu(url: String, record: OvertimeRecord): Boolean = withContext(Dispatchers.IO) {
        val text = buildText(record)
        
        if (url.contains("open.feishu.cn/open-apis/bot/v2/hook")) {
            val json = """{"msg_type":"text","content":{"text":"${escapeJson(text)}"}}"""
            try {
                val body = json.toRequestBody(mediaType)
                val req = Request.Builder()
                    .url(url)
                    .post(body)
                    .header("Content-Type", "application/json; charset=utf-8")
                    .build()
                val res = client.newCall(req).execute()
                val response = res.body?.string()
                Log.d("PushManager", "飞书推送响应: $response")
                res.close()
                
                if (response != null) {
                    val jsonResponse = try {
                        org.json.JSONObject(response)
                    } catch (e: Exception) {
                        null
                    }
                    jsonResponse?.let {
                        val code = it.optInt("code", -1)
                        val statusCode = it.optInt("StatusCode", -1)
                        return@withContext code == 0 || statusCode == 0 || res.isSuccessful
                    }
                }
                res.isSuccessful
            } catch (e: Exception) {
                Log.e("PushManager", "飞书推送失败", e)
                false
            }
        } else {
            Log.d("PushManager", "飞书URL格式不正确: $url")
            false
        }
    }

    suspend fun sendWxPusher(url: String, record: OvertimeRecord): Boolean = withContext(Dispatchers.IO) {
        val text = buildText(record)
        val json = """{"content":"${escapeJson(text)}","contentType":1,"summary":"加班记录推送"}"""
        try {
            val body = json.toRequestBody(mediaType)
            val req = Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
            val res = client.newCall(req).execute()
            val response = res.body?.string()
            Log.d("PushManager", "WxPusher推送响应: $response")
            res.close()
            res.isSuccessful && response?.contains("\"success\":true") == true
        } catch (e: Exception) {
            Log.e("PushManager", "WxPusher推送失败", e)
            false
        }
    }

    suspend fun sendCustom(url: String, record: OvertimeRecord): Boolean = withContext(Dispatchers.IO) {
        val text = buildText(record)
        try {
            val body = text.toRequestBody("text/plain; charset=utf-8".toMediaType())
            val req = Request.Builder()
                .url(url)
                .post(body)
                .build()
            val res = client.newCall(req).execute()
            Log.d("PushManager", "自定义推送响应: ${res.code}")
            res.close()
            res.isSuccessful
        } catch (e: Exception) {
            Log.e("PushManager", "自定义推送失败", e)
            false
        }
    }

    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }
}
