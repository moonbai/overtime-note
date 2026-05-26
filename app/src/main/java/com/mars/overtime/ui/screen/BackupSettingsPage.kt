package com.mars.overtime.ui.screen

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.AppDatabase
import com.mars.overtime.util.BackupManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingsPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val db = OvertimeApplication.database
    val scope = rememberCoroutineScope()

    var backupStatus by remember { mutableStateOf<String?>(null) }
    var restoreStatus by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var cloudSyncExpanded by remember { mutableStateOf(false) }

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isLoading = true
                try {
                    val success = BackupManager.backupToFile(context, db, uri)
                    backupStatus = if (success) "备份成功" else "备份失败"
                } catch (e: Exception) {
                    backupStatus = "备份失败: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                isLoading = true
                try {
                    val success = BackupManager.restoreFromFile(context, db, uri)
                    restoreStatus = if (success) "恢复成功" else "恢复失败"
                } catch (e: Exception) {
                    restoreStatus = "恢复失败: ${e.message}"
                } finally {
                    isLoading = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("备份与恢复") },
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
            // 本地备份卡片
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "本地备份",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "将数据备份到本地文件，或从本地文件恢复数据",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
                                    .format(java.util.Date())
                                backupLauncher.launch("overtime_backup_$timeStamp.json")
                            },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("备份数据")
                        }
                        OutlinedButton(
                            onClick = { restoreLauncher.launch(arrayOf("application/json")) },
                            enabled = !isLoading,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("恢复数据")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 云同步卡片（默认折叠）
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
                        Text(
                            text = "云同步",
                            style = MaterialTheme.typography.titleMedium
                        )
                        IconButton(onClick = { cloudSyncExpanded = !cloudSyncExpanded }) {
                            Icon(
                                imageVector = if (cloudSyncExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (cloudSyncExpanded) "收起" else "展开"
                            )
                        }
                    }
                    
                    if (cloudSyncExpanded) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "云同步功能需要自行搭建服务器端点。配置服务器地址后，数据将加密上传到您的服务器。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        var cloudServerUrl by remember { mutableStateOf("") }
                        
                        OutlinedTextField(
                            value = cloudServerUrl,
                            onValueChange = { cloudServerUrl = it },
                            label = { Text("云服务器地址") },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("https://your-server.com/api") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    // TODO: 实现云上传
                                },
                                enabled = !isLoading && cloudServerUrl.isNotEmpty(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("上传")
                            }
                            OutlinedButton(
                                onClick = {
                                    // TODO: 实现云下载
                                },
                                enabled = !isLoading && cloudServerUrl.isNotEmpty(),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("下载")
                            }
                        }
                    }
                }
            }

            // 状态显示
            if (isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            backupStatus?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (it.contains("成功")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }

            restoreStatus?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (it.contains("成功")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
