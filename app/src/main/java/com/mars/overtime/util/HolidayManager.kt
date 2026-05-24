package com.mars.overtime.util

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object HolidayManager {
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private const val HOLIDAY_API_URL = "https://timor.tech/api/holiday/year/"

    private val holidayCache = mutableMapOf<String, HolidayInfo>()

    data class HolidayInfo(
        val date: String,
        val isHoliday: Boolean,
        val name: String = "",
        val isWorkday: Boolean = false
    )

    private fun isWeekend(dateStr: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr) ?: return false
        val cal = Calendar.getInstance()
        cal.time = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    suspend fun isHoliday(dateStr: String): Boolean = withContext(Dispatchers.IO) {
        holidayCache[dateStr]?.let { return@withContext it.isHoliday }

        val year = dateStr.substring(0, 4)
        try {
            val req = Request.Builder().url("$HOLIDAY_API_URL$year").build()
            val res = client.newCall(req).execute()
            val body = res.body?.string()

            if (body != null) {
                val json = org.json.JSONObject(body)
                val holidayJson = json.optJSONObject("holiday")
                if (holidayJson != null) {
                    val targetJson = holidayJson.optJSONObject(dateStr)
                    val info = if (targetJson != null) {
                        HolidayInfo(
                            date = dateStr,
                            isHoliday = true,
                            name = targetJson.optString("name", "")
                        )
                    } else {
                        val workdayJson = json.optJSONObject("workday")
                        val isWorkday = workdayJson?.has(dateStr) == true
                        HolidayInfo(
                            date = dateStr,
                            isHoliday = isWorkday,
                            isWorkday = isWorkday
                        )
                    }
                    holidayCache[year] = info
                    return@withContext info.isHoliday
                }
            }

            val result = isWeekend(dateStr)
            holidayCache[dateStr] = HolidayInfo(dateStr, result)
            result
        } catch (e: Exception) {
            Log.e("HolidayManager", "获取节假日失败", e)
            isWeekend(dateStr)
        }
    }

    suspend fun isWorkday(dateStr: String): Boolean = withContext(Dispatchers.IO) {
        val info = holidayCache[dateStr]
        info?.isWorkday == true
    }

    suspend fun fetchHolidays(year: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val req = Request.Builder().url("$HOLIDAY_API_URL$year").build()
            val res = client.newCall(req).execute()
            val body = res.body?.string()
            body != null && body.contains("holiday")
        } catch (e: Exception) {
            Log.e("HolidayManager", "拉取节假日失败", e)
            false
        }
    }

    fun clearCache() {
        holidayCache.clear()
    }
}
