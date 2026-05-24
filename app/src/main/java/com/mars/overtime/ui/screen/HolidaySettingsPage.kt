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
import com.mars.overtime.util.HolidayManager
import kotlinx.coroutines.launch
import java.time.Year

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HolidaySettingsPage(
    onNavigateBack: () -> Unit
) {
    val scope = rememberCoroutineScope()

    var updateResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("节假日管理") },
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
            Text("节假日规则说明", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "应用会自动识别法定节假日和调休工作日。调休工作日将正确判定为工作日加班。\n\n" +
                "数据来源：timor.tech 节假日 API\n\n" +
                "• 周末休息日 → 休息日加班\n" +
                "• 法定节假日 → 法定节假日加班\n" +
                "• 调休工作日 → 工作日延时加班",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            val currentYear = Year.now().value.toString()
            Text("更新年份: $currentYear", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    isLoading = true
                    scope.launch {
                        val success = HolidayManager.fetchHolidays(currentYear)
                        updateResult = if (success) {
                            "$currentYear 年节假日规则已更新"
                        } else {
                            "更新失败，请检查网络连接"
                        }
                        isLoading = false
                        showResultDialog = true
                    }
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("手动更新节假日规则")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    HolidayManager.clearCache()
                    updateResult = "节假日缓存已清除"
                    showResultDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("清除缓存")
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("更新结果") },
            text = { Text(updateResult) },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("确定") }
            }
        )
    }
}
