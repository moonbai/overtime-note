package com.mars.overtime.util

import android.util.Log
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.OvertimeType
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

    private const val DEFAULT_HOLIDAY_API_URL_TEMPLATE = "https://api.example.com/holiday/year/{year}"

    private val holidayCache = mutableMapOf<String, HolidayInfo>()

    data class HolidayInfo(
        val date: String,
        val isHoliday: Boolean,
        val name: String = "",
        val isWorkday: Boolean = false,
        val detailsType: Int = 0
    )

    private fun isWeekend(dateStr: String): Boolean {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateStr) ?: return false
        val cal = Calendar.getInstance()
        cal.time = date
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
    }

    private suspend fun getHolidayApiUrl(): String? {
        return try {
            val db = OvertimeApplication.database
            val configDao = db.configDao()
            val config = configDao.getConfig("holiday_api_url")
            config?.value?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }

    private fun buildApiUrl(year: String, customUrl: String?): String {
        return if (customUrl != null) {
            customUrl
                .replace("\${year}", year)
                .replace("\${years}", year)
                .replace("{year}", year)
                .replace("{years}", year)
        } else {
            DEFAULT_HOLIDAY_API_URL_TEMPLATE.replace("{year}", year)
        }
    }

    suspend fun getOvertimeType(dateStr: String): OvertimeType = withContext(Dispatchers.IO) {
        holidayCache[dateStr]?.let {
            return@withContext when (it.detailsType) {
                1 -> OvertimeType.RESTDAY
                3 -> OvertimeType.HOLIDAY
                else -> OvertimeType.WORKDAY
            }
        }

        val year = dateStr.substring(0, 4)
        try {
            val customUrl = getHolidayApiUrl()
            val url = buildApiUrl(year, customUrl)
            
            Log.d("HolidayManager", "请求节假日API: $url")
            
            val req = Request.Builder().url(url).build()
            val res = client.newCall(req).execute()
            val body = res.body?.string()
            res.close()

            if (body != null) {
                Log.d("HolidayManager", "API响应: ${body.take(500)}")
                val json = org.json.JSONObject(body)
                val code = json.optInt("code", -1)
                
                if (code == 0) {
                    val dataArray = json.optJSONArray("data")
                    if (dataArray != null) {
                        for (i in 0 until dataArray.length()) {
                            val item = dataArray.optJSONObject(i)
                            if (item != null) {
                                val date = item.optString("date", "")
                                val detailsType = item.optInt("detailsType", 0)
                                val name = item.optString("name", "")
                                val isHoliday = detailsType != 0
                                
                                val info = HolidayInfo(
                                    date = date,
                                    isHoliday = isHoliday,
                                    name = name,
                                    detailsType = detailsType
                                )
                                holidayCache[date] = info
                            }
                        }
                    }
                }
            }

            val info = holidayCache[dateStr]
            if (info != null) {
                return@withContext when (info.detailsType) {
                    1 -> OvertimeType.RESTDAY
                    3 -> OvertimeType.HOLIDAY
                    else -> OvertimeType.WORKDAY
                }
            }

            if (isWeekend(dateStr)) {
                holidayCache[dateStr] = HolidayInfo(dateStr, true, detailsType = 1)
                OvertimeType.RESTDAY
            } else {
                holidayCache[dateStr] = HolidayInfo(dateStr, false, detailsType = 0)
                OvertimeType.WORKDAY
            }
        } catch (e: Exception) {
            Log.e("HolidayManager", "获取节假日失败", e)
            if (isWeekend(dateStr)) {
                OvertimeType.RESTDAY
            } else {
                OvertimeType.WORKDAY
            }
        }
    }

    suspend fun isHoliday(dateStr: String): Boolean = withContext(Dispatchers.IO) {
        getOvertimeType(dateStr) != OvertimeType.WORKDAY
    }

    suspend fun isWorkday(dateStr: String): Boolean = withContext(Dispatchers.IO) {
        getOvertimeType(dateStr) == OvertimeType.WORKDAY
    }

    suspend fun fetchHolidays(year: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val customUrl = getHolidayApiUrl()
            val url = buildApiUrl(year, customUrl)
            
            val req = Request.Builder().url(url).build()
            val res = client.newCall(req).execute()
            val body = res.body?.string()
            res.close()
            body != null
        } catch (e: Exception) {
            Log.e("HolidayManager", "拉取节假日失败", e)
            false
        }
    }

    fun clearCache() {
        holidayCache.clear()
    }
}