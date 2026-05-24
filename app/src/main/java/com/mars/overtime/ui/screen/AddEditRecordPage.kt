package com.mars.overtime.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import com.mars.overtime.push.PushManager
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
    val application = context.applicationContext as Application
    val db = (application as OvertimeApplication).database
    val overtimeDao = db.overtimeDao()
    val configDao = db.configDao()

    val scope = rememberCoroutineScope()

    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

    var date by remember { mutableStateOf(today) }
    var startTime by remember { mutableStateOf("18:00") }
    var endTime by remember { mutableStateOf("21:00") }
    var duration by remember { mutableStateOf(3.0) }
    var selectedType by remember { mutableStateOf(OvertimeType.WORKDAY) }
    var remark by remember { mutableStateOf("") }
    var money by remember { mutableStateOf(0.0) }

    val allConfigs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    LaunchedEffect(startTime, endTime, selectedType, allConfigs) {
        duration = SalaryCalculator.calculateDuration(startTime, endTime)
        money = SalaryCalculator.calculateMoneyWithConfig(allConfigs, selectedType, duration)
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
            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("日期 (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("开始时间") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("结束时间") },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text("加班时长: ${"%.2f".format(duration)} 小时", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Text("加班类型:", style = MaterialTheme.typography.bodyLarge)
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
            Spacer(modifier = Modifier.height(8.dp))

            Text("预计金额: ¥${"%.2f".format(money)}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = remark,
                onValueChange = { remark = it },
                label = { Text("加班事由") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        val record = OvertimeRecord(
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            duration = duration,
                            type = selectedType,
                            money = money,
                            remark = remark
                        )
                        overtimeDao.insertRecord(record)

                        val pushConfig = configDao.getAllConfigs()
                        pushConfig.collect { configs ->
                            val dingUrl = configs.find { it.key == "push_dingtalk" }?.value
                            val feishuUrl = configs.find { it.key == "push_feishu" }?.value
                            val wxUrl = configs.find { it.key == "push_wxpusher" }?.value
                            val customUrl = configs.find { it.key == "push_custom" }?.value

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
}
