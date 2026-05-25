package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import com.mars.overtime.push.CalendarSyncManager
import com.mars.overtime.push.PushManager
import com.mars.overtime.util.HolidayManager
import com.mars.overtime.util.SalaryCalculator
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val db = OvertimeApplication.database
    val overtimeDao = db.overtimeDao()
    val configDao = db.configDao()

    val scope = rememberCoroutineScope()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    dateFormat.timeZone = TimeZone.getDefault()
    
    val calendar = Calendar.getInstance()
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinute = calendar.get(Calendar.MINUTE)
    
    val nearestHour = if (currentMinute < 30) currentHour else currentHour + 1
    val defaultEndTime = String.format("%02d:%02d", nearestHour, if (currentMinute < 30) 30 else 0)

    var selectedDate by remember { mutableStateOf(dateFormat.format(Date())) }
    var selectedStartTime by remember { mutableStateOf("17:00") }
    var selectedEndTime by remember { mutableStateOf(defaultEndTime) }
    var duration by remember { mutableStateOf(3.0) }
    var selectedType by remember { mutableStateOf(OvertimeType.WORKDAY) }
    var remark by remember { mutableStateOf("") }
    var money by remember { mutableStateOf(0.0) }
    var holidayInfo by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    val allConfigs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    fun updateOvertimeType(dateStr: String) {
        isLoading = true
        scope.launch {
            try {
                val holidayInfoResult = HolidayManager.getOvertimeType(dateStr)
                selectedType = holidayInfoResult
                holidayInfo = when (holidayInfoResult) {
                    OvertimeType.WORKDAY -> "工作日"
                    OvertimeType.RESTDAY -> "休息日"
                    OvertimeType.HOLIDAY -> "法定节假日"
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        updateOvertimeType(selectedDate)
    }

    LaunchedEffect(selectedStartTime, selectedEndTime, selectedType, allConfigs) {
        duration = SalaryCalculator.calculateDuration(selectedStartTime, selectedEndTime)
        money = SalaryCalculator.calculateMoneyWithConfig(allConfigs, selectedType, duration)
    }

    fun parseDateToLocalCalendar(dateStr: String): Calendar {
        val cal = Calendar.getInstance(TimeZone.getDefault())
        try {
            val date = dateFormat.parse(dateStr)
            if (date != null) {
                cal.time = date
            }
        } catch (e: Exception) {
        }
        return cal
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("新建加班记录") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedButton(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("日期: $selectedDate")
            }
            Spacer(modifier = Modifier.height(8.dp))

            if (holidayInfo != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "当前日期类型: $holidayInfo",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            } else if (isLoading) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = "正在加载日期信息...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { showStartTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("开始: $selectedStartTime")
                }
                OutlinedButton(
                    onClick = { showEndTimePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("结束: $selectedEndTime")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("加班时长: ${"%.2f".format(duration)} 小时", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Text("加班类型:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OvertimeType.values().forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = {
                            Text(
                                when (type) {
                                    OvertimeType.WORKDAY -> "工作日"
                                    OvertimeType.RESTDAY -> "休息日"
                                    OvertimeType.HOLIDAY -> "法定节假日"
                                }
                            )
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("预计金额: ¥${"%.2f".format(money)}", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = remark,
                onValueChange = { remark = it },
                label = { Text("加班事由") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val record = OvertimeRecord(
                            id = 0,
                            date = selectedDate,
                            startTime = selectedStartTime,
                            endTime = selectedEndTime,
                            duration = duration,
                            type = selectedType,
                            money = money,
                            remark = remark,
                            createTime = System.currentTimeMillis()
                        )
                        overtimeDao.insertRecord(record)

                        val configs = configDao.getAllConfigsOnce()
                        val dingUrl = configs.find { it.key == "push_dingtalk" }?.value
                        val feishuUrl = configs.find { it.key == "push_feishu" }?.value
                        val wxUrl = configs.find { it.key == "push_wxpusher" }?.value
                        val customUrl = configs.find { it.key == "push_custom" }?.value
                        val calendarEnabled = configs.find { it.key == "calendar_enabled" }?.value == "true"

                        if (!dingUrl.isNullOrBlank()) {
                            PushManager.sendDingTalk(dingUrl, record)
                        }
                        if (!feishuUrl.isNullOrBlank()) {
                            PushManager.sendFeishu(feishuUrl, record)
                        }
                        if (!wxUrl.isNullOrBlank()) {
                            PushManager.sendWxPusher(wxUrl, record)
                        }
                        if (!customUrl.isNullOrBlank()) {
                            PushManager.sendCustom(customUrl, record)
                        }
                        
                        if (calendarEnabled) {
                            CalendarSyncManager.addEvent(context, record)
                        }

                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }

    if (showDatePicker) {
        val initialCalendar = parseDateToLocalCalendar(selectedDate)
        val initialMillis = initialCalendar.timeInMillis
        
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val cal = Calendar.getInstance(TimeZone.getDefault())
                            cal.timeInMillis = selectedMillis
                            cal.set(Calendar.HOUR_OF_DAY, 0)
                            cal.set(Calendar.MINUTE, 0)
                            cal.set(Calendar.SECOND, 0)
                            cal.set(Calendar.MILLISECOND, 0)
                            selectedDate = dateFormat.format(cal.time)
                            updateOvertimeType(selectedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("取消")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showStartTimePicker) {
        TimePickerDialogWrapper(
            initialHour = selectedStartTime.split(":")[0].toInt(),
            initialMinute = selectedStartTime.split(":")[1].toInt(),
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute ->
                selectedStartTime = String.format("%02d:%02d", hour, minute)
                showStartTimePicker = false
            }
        )
    }

    if (showEndTimePicker) {
        TimePickerDialogWrapper(
            initialHour = selectedEndTime.split(":")[0].toInt(),
            initialMinute = selectedEndTime.split(":")[1].toInt(),
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour, minute ->
                selectedEndTime = String.format("%02d:%02d", hour, minute)
                showEndTimePicker = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialogWrapper(
    initialHour: Int,
    initialMinute: Int,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.wrapContentSize(),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "选择时间",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(20.dp))
                TimePicker(state = timePickerState)
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("取消")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) {
                        Text("确定")
                    }
                }
            }
        }
    }
}
