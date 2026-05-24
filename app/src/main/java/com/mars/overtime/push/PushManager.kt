package com.mars.overtime.push

import android.util.Log
import com.mars.overtime.database.OvertimeRecord
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
            OvertimeType.WORKDAY -> "工作日延时"
            OvertimeType.RESTDAY -> "休息日"
            OvertimeType.HOLIDAY -> "法定节假日"
        }
        return """日期：${record.date}
类型：$typeStr
时间：${record.startTime} - ${record.endTime}
加班时长：${record.duration}
金额：¥${"%.2f".format(record.money)}
加班事由：${record.remark}"""
    }

    suspend fun sendDingTalk(url: String, record: OvertimeRecord): Boolean {
        val text = buildText(record)
        val json = """{"msgtype":"text","text":{"content":"$text"}}"""
        return try {
            val body = json.toRequestBody(mediaType)
            val req = Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
            val res = client.newCall(req).execute()
            val response = res.body?.string()
            Log.d("PushManager", "钉钉推送响应: $response")
            res.isSuccessful && response?.contains("\"errcode\":0") == true
        } catch (e: Exception) {
            Log.e("PushManager", "钉钉推送失败", e)
            false
        }
    }

    suspend fun sendFeishu(url: String, record: OvertimeRecord): Boolean {
        val text = buildText(record)
        val json = """{"msg_type":"text","content":{"text":"$text"}}"""
        return try {
            val body = json.toRequestBody(mediaType)
            val req = Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
            val res = client.newCall(req).execute()
            val response = res.body?.string()
            Log.d("PushManager", "飞书推送响应: $response")
            res.isSuccessful && response?.contains("\"StatusCode\":0") == true
        } catch (e: Exception) {
            Log.e("PushManager", "飞书推送失败", e)
            false
        }
    }

    suspend fun sendWxPusher(url: String, record: OvertimeRecord): Boolean {
        val text = buildText(record)
        val json = """{"content":"$text","contentType":1,"summary":"加班记录推送"}"""
        return try {
            val body = json.toRequestBody(mediaType)
            val req = Request.Builder()
                .url(url)
                .post(body)
                .header("Content-Type", "application/json; charset=utf-8")
                .build()
            val res = client.newCall(req).execute()
            val response = res.body?.string()
            Log.d("PushManager", "WxPusher推送响应: $response")
            res.isSuccessful && response?.contains("\"success\":true") == true
        } catch (e: Exception) {
            Log.e("PushManager", "WxPusher推送失败", e)
            false
        }
    }

    suspend fun sendCustom(url: String, record: OvertimeRecord): Boolean {
        val text = buildText(record)
        return try {
            val body = text.toRequestBody("text/plain; charset=utf-8".toMediaType())
            val req = Request.Builder()
                .url(url)
                .post(body)
                .build()
            val res = client.newCall(req).execute()
            Log.d("PushManager", "自定义推送响应: ${res.code}")
            res.isSuccessful
        } catch (e: Exception) {
            Log.e("PushManager", "自定义推送失败", e)
            false
        }
    }
}
