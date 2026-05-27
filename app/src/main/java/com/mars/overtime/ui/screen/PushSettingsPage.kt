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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PushSettingsPage(
    onNavigateBack: () -> Unit
) {
    val db = OvertimeApplication.database
    val configDao = db.configDao()
    val scope = rememberCoroutineScope()

    val configs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    var pushEnabled by remember { mutableStateOf(false) }
    var selectedChannel by remember { mutableStateOf(0) }
    var dingtalkUrl by remember { mutableStateOf("") }
    var feishuUrl by remember { mutableStateOf("") }
    var wxPusherUrl by remember { mutableStateOf("") }
    var customUrl by remember { mutableStateOf("") }

    LaunchedEffect(configs) {
        pushEnabled = configs.find { it.key == "push_enabled" }?.value?.toBoolean() ?: false
        dingtalkUrl = configs.find { it.key == "push_dingtalk" }?.value ?: ""
        feishuUrl = configs.find { it.key == "push_feishu" }?.value ?: ""
        wxPusherUrl = configs.find { it.key == "push_wxpusher" }?.value ?: ""
        customUrl = configs.find { it.key == "push_custom" }?.value ?: ""
        
        selectedChannel = when {
            dingtalkUrl.isNotEmpty() -> 1
            feishuUrl.isNotEmpty() -> 2
            wxPusherUrl.isNotEmpty() -> 3
            customUrl.isNotEmpty() -> 4
            else -> 0
        }
    }

    val channelOptions = listOf(
        "不推送",
        "钉钉机器人",
        "飞书机器人",
        "WxPusher",
        "自定义Webhook"
    )

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
                        Text("启用推送", style = MaterialTheme.typography.titleMedium)
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
                Text("推送渠道", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = channelOptions[selectedChannel],
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        channelOptions.forEachIndexed { index, option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedChannel = index
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                when (selectedChannel) {
                    1 -> {
                        Text(
                            text = "钉钉机器人Webhook地址",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = dingtalkUrl,
                            onValueChange = { dingtalkUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://oapi.dingtalk.com/robot/send?access_token=...") },
                            singleLine = true
                        )
                    }
                    2 -> {
                        Text(
                            text = "飞书机器人Webhook地址",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = feishuUrl,
                            onValueChange = { feishuUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://open.feishu.cn/open-apis/bot/v2/hook/...") },
                            singleLine = true
                        )
                    }
                    3 -> {
                        Text(
                            text = "WxPusher地址",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = wxPusherUrl,
                            onValueChange = { wxPusherUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("WxPusher推送地址") },
                            singleLine = true
                        )
                    }
                    4 -> {
                        Text(
                            text = "自定义Webhook地址",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = customUrl,
                            onValueChange = { customUrl = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://your-webhook-url.com/api/push") },
                            singleLine = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        scope.launch {
                            configDao.deleteConfig("push_dingtalk")
                            configDao.deleteConfig("push_feishu")
                            configDao.deleteConfig("push_wxpusher")
                            configDao.deleteConfig("push_custom")
                            
                            when (selectedChannel) {
                                1 -> configDao.saveConfig(AppConfig("push_dingtalk", dingtalkUrl))
                                2 -> configDao.saveConfig(AppConfig("push_feishu", feishuUrl))
                                3 -> configDao.saveConfig(AppConfig("push_wxpusher", wxPusherUrl))
                                4 -> configDao.saveConfig(AppConfig("push_custom", customUrl))
                            }
                            onNavigateBack()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("保存", style = MaterialTheme.typography.titleMedium)
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
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
