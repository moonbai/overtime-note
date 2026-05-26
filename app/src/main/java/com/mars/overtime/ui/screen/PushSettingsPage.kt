package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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

    var pushEnabled by remember { mutableStateOf(allConfigs.find { it.key == "push_enabled" }?.value?.toBoolean() ?: false) }
    var dingtalkUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_dingtalk" }?.value ?: "") }
    var feishuUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_feishu" }?.value ?: "") }
    var wxPusherUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_wxpusher" }?.value ?: "") }
    var customUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_custom" }?.value ?: "") }
    var telegramUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_telegram" }?.value ?: "") }
    var discordUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_discord" }?.value ?: "") }
    var wecomUrl by remember { mutableStateOf(allConfigs.find { it.key == "push_wecom" }?.value ?: "") }

    var pushResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }

    LaunchedEffect(allConfigs) {
        pushEnabled = allConfigs.find { it.key == "push_enabled" }?.value?.toBoolean() ?: false
        dingtalkUrl = allConfigs.find { it.key == "push_dingtalk" }?.value ?: ""
        feishuUrl = allConfigs.find { it.key == "push_feishu" }?.value ?: ""
        wxPusherUrl = allConfigs.find { it.key == "push_wxpusher" }?.value ?: ""
        customUrl = allConfigs.find { it.key == "push_custom" }?.value ?: ""
        telegramUrl = allConfigs.find { it.key == "push_telegram" }?.value ?: ""
        discordUrl = allConfigs.find { it.key == "push_discord" }?.value ?: ""
        wecomUrl = allConfigs.find { it.key == "push_wecom" }?.value ?: ""
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("推送设置") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "返回")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
            // 总开关
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("启用推送", style = MaterialTheme.typography.titleLarge)
                        Text(
                            text = "开启后记录加班时会自动推送通知",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = pushEnabled,
                        onCheckedChange = { enabled ->
                            pushEnabled = enabled
                            scope.launch {
                                configDao.saveConfig(AppConfig("push_enabled", enabled.toString()))
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (pushEnabled) {
                Text("推送渠道", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(16.dp))

                // 钉钉
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("钉钉", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = dingtalkUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        dingtalkUrl = ""
                                    }
                                }
                            )
                        }
                        if (dingtalkUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = dingtalkUrl,
                                onValueChange = { dingtalkUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://oapi.dingtalk.com/robot/send?access_token=xxx") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 飞书
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("飞书", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = feishuUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        feishuUrl = ""
                                    }
                                }
                            )
                        }
                        if (feishuUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = feishuUrl,
                                onValueChange = { feishuUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://open.feishu.cn/open-apis/bot/v2/hook/xxx") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 企业微信
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("企业微信", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = wecomUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        wecomUrl = ""
                                    }
                                }
                            )
                        }
                        if (wecomUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = wecomUrl,
                                onValueChange = { wecomUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // WxPusher
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("WxPusher", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = wxPusherUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        wxPusherUrl = ""
                                    }
                                }
                            )
                        }
                        if (wxPusherUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = wxPusherUrl,
                                onValueChange = { wxPusherUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://wxpusher.zxazx.com/api/v1/send/xxx") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Telegram
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Telegram", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = telegramUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        telegramUrl = ""
                                    }
                                }
                            )
                        }
                        if (telegramUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = telegramUrl,
                                onValueChange = { telegramUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://api.telegram.org/botxxx/sendMessage") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Discord
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Discord", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = discordUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        discordUrl = ""
                                    }
                                }
                            )
                        }
                        if (discordUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = discordUrl,
                                onValueChange = { discordUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://discord.com/api/webhooks/xxx/xxx") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 自定义推送
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("自定义推送", style = MaterialTheme.typography.titleMedium)
                            Switch(
                                checked = customUrl.isNotBlank(),
                                onCheckedChange = { checked ->
                                    if (!checked) {
                                        customUrl = ""
                                    }
                                }
                            )
                        }
                        if (customUrl.isNotBlank()) {
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = customUrl,
                                onValueChange = { customUrl = it },
                                modifier = Modifier.fillMaxWidth(),
                                placeholder = { Text("https://your-server.com/api/push") }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            configDao.saveConfigs(
                                listOf(
                                    AppConfig("push_enabled", pushEnabled.toString()),
                                    AppConfig("push_dingtalk", dingtalkUrl),
                                    AppConfig("push_feishu", feishuUrl),
                                    AppConfig("push_wecom", wecomUrl),
                                    AppConfig("push_wxpusher", wxPusherUrl),
                                    AppConfig("push_telegram", telegramUrl),
                                    AppConfig("push_discord", discordUrl),
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
                            if (wecomUrl.isNotBlank()) {
                                val success = PushManager.sendDingTalk(wecomUrl, testRecord) // 复用钉钉逻辑
                                results.add("企业微信: ${if (success) "成功" else "失败"}")
                            }
                            if (wxPusherUrl.isNotBlank()) {
                                val success = PushManager.sendWxPusher(wxPusherUrl, testRecord)
                                results.add("WxPusher: ${if (success) "成功" else "失败"}")
                            }
                            if (telegramUrl.isNotBlank()) {
                                val success = PushManager.sendCustom(telegramUrl, testRecord)
                                results.add("Telegram: ${if (success) "成功" else "失败"}")
                            }
                            if (discordUrl.isNotBlank()) {
                                val success = PushManager.sendCustom(discordUrl, testRecord)
                                results.add("Discord: ${if (success) "成功" else "失败"}")
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
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "推送功能已关闭",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
