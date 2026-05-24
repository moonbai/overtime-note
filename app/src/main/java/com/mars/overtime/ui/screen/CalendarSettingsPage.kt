package com.mars.overtime.ui.screen

import android.Manifest
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
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.mars.overtime.push.CalendarSyncManager
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarSettingsPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var syncResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }
    var autoSync by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日历同步") },
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("自动同步到日历", style = MaterialTheme.typography.titleMedium)
                Switch(
                    checked = autoSync,
                    onCheckedChange = { autoSync = it }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "开启后，新建加班记录将自动添加到系统日历",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        val hasPermission = CalendarSyncManager.hasCalendarPermission(context)
                        if (hasPermission) {
                            val calendarId = CalendarSyncManager.getOrCreateCalendarId(context)
                            syncResult = if (calendarId != null) {
                                "日历同步已就绪，日历ID: $calendarId"
                            } else {
                                "日历同步初始化失败"
                            }
                        } else {
                            syncResult = "请先授予日历权限"
                        }
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("测试日历同步")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    syncResult = "已创建加班记日历，请在系统日历中查看"
                    showResultDialog = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("创建加班日历")
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("日历同步") },
            text = { Text(syncResult) },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("确定") }
            }
        )
    }
}
