package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.AppConfig
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import com.mars.overtime.push.PushManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.*
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.component.AlertDialog

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
    var showHelpDialog by remember { mutableStateOf(false) }
    var helpContent by remember { mutableStateOf("") }

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
                        Icon(MiuixIcons.ArrowBack, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        helpContent = """
                            各推送渠道说明：
                            
                            1. 钉钉机器人：在钉钉群中添加自定义机器人，获取Webhook地址
                            
                            2. 飞书机器人：在飞书群中添加自定义机器人，获取Webhook地址
                            
                            3. 企业微信：在企业微信群中添加群机器人，获取Webhook地址
                            
                            4. WxPusher：访问 https://wxpusher.zjiecode.com/ 获取
                            
                            5. Telegram：创建Bot并获取Token，填入格式：https://api.telegram.org/bot{token}/sendMessage
                            
                            6. Discord：在Discord服务器中创建Webhook，获取地址
                            
                            7. 自定义推送：填写您自己的API接口地址，将以POST方式发送JSON数据
                        """.trimIndent()
                        showHelpDialog = true
                    }) {
                        Icon(MiuixIcons.Help, contentDescription = "帮助")
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
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MiuixTheme.colorScheme.primaryContainer
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
                        Text("启用推送", style = MiuixTheme.textStyles.titleLarge)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "开启后记录加班时会自动推送通知",
                            style = MiuixTheme.textStyles.bodySmall,
                            color = MiuixTheme.colorScheme.onSurfaceVariant
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
                Text("推送渠道", style = MiuixTheme.textStyles.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                ChannelCard(
                    name = "钉钉",
                    description = "钉钉群机器人推送",
                    url = dingtalkUrl,
                    placeholder = "https://oapi.dingtalk.com/robot/send?access_token=xxx",
                    onUrlChange = { dingtalkUrl = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ChannelCard(
                    name = "飞书",
                    description = "飞书群机器人推送",
                    url = feishuUrl,
                    placeholder = "https://open.feishu.cn/open-apis/bot/v2/hook/xxx",
                    onUrlChange = { feishuUrl = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ChannelCard(
                    name = "企业微信",
                    description = "企业微信群机器人推送",
                    url = wecomUrl,
                    placeholder = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=xxx",
                    onUrlChange = { wecomUrl = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ChannelCard(
                    name = "WxPusher",
                    description = "微信消息推送服务",
                    url = wxPusherUrl,
                    placeholder = "https://wxpusher.zjiecode.com/api/v1/send/xxx",
                    onUrlChange = { wxPusherUrl = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ChannelCard(
                    name = "Telegram",
                    description = "Telegram机器人推送",
                    url = telegramUrl,
                    placeholder = "https://api.telegram.org/bot{token}/sendMessage",
                    onUrlChange = { telegramUrl = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ChannelCard(
                    name = "Discord",
                    description = "Discord Webhook推送",
                    url = discordUrl,
                    placeholder = "https://discord.com/api/webhooks/xxx/xxx",
                    onUrlChange = { discordUrl = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ChannelCard(
                    name = "自定义推送",
                    description = "自定义API接口推送",
                    url = customUrl,
                    placeholder = "https://your-server.com/api/push",
                    onUrlChange = { customUrl = it }
                )

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("保存配置", style = MiuixTheme.textStyles.titleMedium)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
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
                                val success = PushManager.sendWeCom(wecomUrl, testRecord)
                                results.add("企业微信: ${if (success) "成功" else "失败"}")
                            }
                            if (wxPusherUrl.isNotBlank()) {
                                val success = PushManager.sendWxPusher(wxPusherUrl, testRecord)
                                results.add("WxPusher: ${if (success) "成功" else "失败"}")
                            }
                            if (telegramUrl.isNotBlank()) {
                                val success = PushManager.sendTelegram(telegramUrl, testRecord)
                                results.add("Telegram: ${if (success) "成功" else "失败"}")
                            }
                            if (discordUrl.isNotBlank()) {
                                val success = PushManager.sendDiscord(discordUrl, testRecord)
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("测试推送", style = MiuixTheme.textStyles.titleMedium)
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "推送功能已关闭",
                        style = MiuixTheme.textStyles.bodyLarge,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
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

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text("帮助说明") },
            text = { Text(helpContent) },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) { Text("确定") }
            }
        )
    }
}

@Composable
fun ChannelCard(
    name: String,
    description: String,
    url: String,
    placeholder: String,
    onUrlChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(url.isNotBlank()) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MiuixTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(name, style = MiuixTheme.textStyles.titleMedium, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MiuixTheme.textStyles.bodySmall,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(
                    checked = url.isNotBlank(),
                    onCheckedChange = { checked ->
                        if (!checked) {
                            onUrlChange("")
                            expanded = false
                        } else {
                            expanded = true
                        }
                    }
                )
            }
            if (url.isNotBlank() || expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = url,
                    onValueChange = onUrlChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text(placeholder) },
                    singleLine = true
                )
            }
        }
    }
}
