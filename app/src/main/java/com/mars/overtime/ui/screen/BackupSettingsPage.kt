package com.mars.overtime.ui.screen

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.mars.overtime.database.AppConfig
import com.mars.overtime.util.*
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackupSettingsPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val db = OvertimeApplication.database
    val overtimeDao = db.overtimeDao()
    val configDao = db.configDao()

    val scope = rememberCoroutineScope()

    val records by overtimeDao.getAllRecords().collectAsState(initial = emptyList())
    val allConfigs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    var webdavUrl by remember { mutableStateOf("") }
    var webdavUsername by remember { mutableStateOf("") }
    var webdavPassword by remember { mutableStateOf("") }
    var webdavPath by remember { mutableStateOf("/overtime_backup/") }

    var operationResult by remember { mutableStateOf("") }
    var showResultDialog by remember { mutableStateOf(false) }

    val backupFiles = DataMigrationUtil.listBackupFiles(context)

    LaunchedEffect(allConfigs) {
        webdavUrl = allConfigs.find { it.key == "webdav_url" }?.value ?: ""
        webdavUsername = allConfigs.find { it.key == "webdav_username" }?.value ?: ""
        webdavPassword = allConfigs.find { it.key == "webdav_password" }?.value ?: ""
        webdavPath = allConfigs.find { it.key == "webdav_path" }?.value ?: "/overtime_backup/"
    }

    val pickFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            scope.launch {
                val inputStream = context.contentResolver.openInputStream(it)
                inputStream?.use { stream ->
                    val tempFile = File(context.cacheDir, "import_temp.json")
                    tempFile.outputStream().use { output -> stream.copyTo(output) }
                    val data = BackupManager.importData(tempFile.absolutePath)
                    if (data != null) {
                        overtimeDao.insertAllRecords(data.records)
                        configDao.saveConfigs(data.configs)
                        operationResult = "导入成功！共导入 ${data.records.size} 条记录"
                    } else {
                        operationResult = "导入失败，文件格式错误"
                    }
                    tempFile.delete()
                }
                showResultDialog = true
            }
        }
    }

    val createFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        uri?.let {
            scope.launch {
                val outputStream = context.contentResolver.openOutputStream(it)
                outputStream?.use { stream ->
                    val tempFile = File(context.cacheDir, "export_temp.json")
                    val success = BackupManager.exportData(records, allConfigs, tempFile.absolutePath)
                    if (success) {
                        tempFile.inputStream().use { input -> stream.write(input.readBytes()) }
                        operationResult = "导出成功！"
                    } else {
                        operationResult = "导出失败"
                    }
                    tempFile.delete()
                }
                showResultDialog = true
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("备份恢复") },
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
            Text("本地备份", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val filePath = DataMigrationUtil.getBackupFilePath(context)
                        val success = BackupManager.exportData(records, allConfigs, filePath)
                        operationResult = if (success) "备份成功！\n路径: $filePath" else "备份失败"
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("备份到本地")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { createFileLauncher.launch("overtime_backup.json") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("导出备份文件")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { pickFileLauncher.launch("application/json") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("导入备份文件")
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("WebDAV 云端备份", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = webdavUrl,
                onValueChange = { webdavUrl = it },
                label = { Text("WebDAV 服务器地址") },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("https://dav.example.com") }
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = webdavUsername,
                onValueChange = { webdavUsername = it },
                label = { Text("用户名") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = webdavPassword,
                onValueChange = { webdavPassword = it },
                label = { Text("密码") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = webdavPath,
                onValueChange = { webdavPath = it },
                label = { Text("远程路径") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    scope.launch {
                        configDao.saveConfigs(
                            listOf(
                                AppConfig("webdav_url", webdavUrl),
                                AppConfig("webdav_username", webdavUsername),
                                AppConfig("webdav_password", webdavPassword),
                                AppConfig("webdav_path", webdavPath)
                            )
                        )
                        operationResult = "配置保存成功！"
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
                        val config = WebDavManager.WebDavConfig(
                            baseUrl = webdavUrl,
                            username = webdavUsername,
                            password = webdavPassword,
                            remotePath = webdavPath
                        )

                        val connected = WebDavManager.testConnection(config)
                        if (connected) {
                            operationResult = "WebDAV 连接成功！"
                        } else {
                            operationResult = "WebDAV 连接失败，请检查配置"
                        }
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("测试连接")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val filePath = DataMigrationUtil.getBackupFilePath(context)
                        val exportSuccess = BackupManager.exportData(records, allConfigs, filePath)
                        if (exportSuccess) {
                            val config = WebDavManager.WebDavConfig(
                                baseUrl = webdavUrl,
                                username = webdavUsername,
                                password = webdavPassword,
                                remotePath = webdavPath
                            )
                            val fileName = DataMigrationUtil.generateBackupFileName()
                            val uploadSuccess = WebDavManager.uploadFile(config, filePath, fileName)
                            operationResult = if (uploadSuccess) "WebDAV 上传成功！" else "WebDAV 上传失败"
                        } else {
                            operationResult = "本地备份失败"
                        }
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("上传到 WebDAV")
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    scope.launch {
                        val config = WebDavManager.WebDavConfig(
                            baseUrl = webdavUrl,
                            username = webdavUsername,
                            password = webdavPassword,
                            remotePath = webdavPath
                        )
                        val remoteFiles = WebDavManager.listFiles(config)
                        operationResult = if (remoteFiles.isNotEmpty()) {
                            "云端文件列表:\n${remoteFiles.joinToString("\n")}"
                        } else {
                            "云端无备份文件"
                        }
                        showResultDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("查看云端备份")
            }
        }
    }

    if (showResultDialog) {
        AlertDialog(
            onDismissRequest = { showResultDialog = false },
            title = { Text("操作结果") },
            text = { Text(operationResult) },
            confirmButton = {
                TextButton(onClick = { showResultDialog = false }) { Text("确定") }
            }
        )
    }
}