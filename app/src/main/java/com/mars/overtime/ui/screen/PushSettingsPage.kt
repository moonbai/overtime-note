package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.AppConfig
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import com.mars.overtime.push.PushManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushSettingsPage(
    onNavigateBack: () -> Unit
) {
    val db = OvertimeApplication.database
    val configDao = db.configDao()
    val overtimeDao = db.overtimeDao()

    val scope = rememberCoroutineScope()

    val allConfigs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    var dingtalkUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_dingtalk" }?.value ?: "") }
    var feishuUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_feishu" }?.value ?: "") }
    var wxPusherUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_wxpusher" }?.value ?: "") }
    var customUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_custom" }?.value ?: "") }

    var pushResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(allConfigs) {
        dingtalkUrl = allConfigs.find { it.key == "push_dingtalk" }?.value ?: ""
        feishuUrl = allConfigs.find { it.key == "push_feishu" }?.value ?: ""
        wxPusherUrl = allConfigs.find { it.key == "push_wxpusher" }?.value ?: ""
        customUrl = allConfigs.find { it.key == "push_custom" }?.value ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("推送设置") },
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
            Text("钉钉 Webhook", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = dingtalkUrl,
                onValueChange = { dingtalkUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://oapi.dingtalk.com/robot/send?access_token=xxx") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("飞书 Webhook", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = feishuUrl,
                onValueChange = { feishuUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://open.feishu.cn/open-apis/bot/v2/hook/xxx") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("WxPusher", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = wxPusherUrl,
                onValueChange = { wxPusherUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://wxpusher.zxazx.com/api/v1/send/xxx") }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("自定义推送", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = customUrl,
                onValueChange = { customUrl = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://your-server.com/api/push") }
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        configDao.saveConfigs(
                            listOf(
                                AppConfig("push_dingtalk", dingtalkUrl),
                                AppConfig("push_feishu", feishuUrl),
                                AppConfig("push_wxpusher", wxPusherUrl),
                                AppConfig("push_custom", customUrl)
                            )
                        )
                        pushResult = "配置已保存"
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存配置")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val testRecord = OvertimeRecord(
                            id = 0,
                            date = dateFormat.format(Date()),
                            startTime = "18:00",
                            endTime = "21:00",
                            duration = 3.0,
                            type = OvertimeType.WORKDAY,
                            money = 100.0,
                            remark = "测试推送",
                            createTime = System.currentTimeMillis()
                        )

                        val results = mutableListOf<String>()

                        if (dingtalkUrl.isNotBlank()) {
                            val success = PushManager.sendDingTalk(dingtalkUrl, testRecord)
                            results.add("钉钉: ${if (success) "成功" else "失败"}")
                        }
                        if (feishuUrl.isNotBlank()) {
                            val success = PushManager.sendFeishu(feishuUrl, testRecord)
                            results.add("飞书: ${if (success) "成功" else "失败"}")
                        }
                        if (wxPusherUrl.isNotBlank()) {
                            val success = PushManager.sendWxPusher(wxPusherUrl, testRecord)
                            results.add("WxPusher: ${if (success) "成功" else "失败"}")
                        }
                        if (customUrl.isNotBlank()) {
                            val success = PushManager.sendCustom(customUrl, testRecord)
                            results.add("自定义: ${if (success) "成功" else "失败"}")
                        }

                        pushResult = if (results.isEmpty()) "未配置任何推送渠道" else results.joinToString("\n")
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("测试推送")
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("推送结果") },
            text = { Text(pushResult) },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("确定") }
            }
        )
    }
}