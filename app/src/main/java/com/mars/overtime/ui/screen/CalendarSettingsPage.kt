package com.mars.overtime.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import com.mars.overtime.push.CalendarSyncManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun CalendarSettingsPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val calendarPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    } else {
        arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        )
    }

    var syncEnabled by remember { mutableStateOf(false) }
    var hasPermission by remember { mutableStateOf(false) }
    var calendarId by remember { mutableStateOf<Long?>(null) }
    var testResult by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        hasPermission = allGranted
        if (allGranted) {
            scope.launch {
                val id = CalendarSyncManager.getOrCreateCalendarId(context)
                calendarId = id
                if (id != null) {
                    val db = OvertimeApplication.database
                    val configDao = db.configDao()
                    configDao.saveConfig(
                        com.mars.overtime.database.AppConfig("calendar_enabled", "true")
                    )
                    syncEnabled = true
                }
            }
        }
    }

    fun checkPermissions() {
        hasPermission = calendarPermissions.all { permission ->
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    LaunchedEffect(Unit) {
        val db = OvertimeApplication.database
        val configDao = db.configDao()
        val config = configDao.getConfig("calendar_enabled")
        syncEnabled = config?.value == "true"

        checkPermissions()

        if (hasPermission) {
            calendarId = CalendarSyncManager.getOrCreateCalendarId(context)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("日历同步") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(MiuixIcons.ArrowBack, contentDescription = "返回")
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
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "日历权限",
                                style = MiuixTheme.textStyles.titleMedium
                            )
                            Text(
                                text = if (hasPermission) "已授权" else "未授权",
                                style = MiuixTheme.textStyles.bodySmall,
                                color = if (hasPermission) MiuixTheme.colorScheme.primary else MiuixTheme.colorScheme.error
                            )
                        }
                        if (hasPermission) {
                            Icon(
                                MiuixIcons.Check,
                                contentDescription = "已授权",
                                tint = MiuixTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                MiuixIcons.Close,
                                contentDescription = "未授权",
                                tint = MiuixTheme.colorScheme.error
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!hasPermission) {
                        Button(
                            onClick = {
                                permissionLauncher.launch(calendarPermissions)
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("授予日历权限")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "自动同步",
                                style = MiuixTheme.textStyles.titleMedium
                            )
                            Text(
                                text = "新建加班记录自动添加到日历",
                                style = MiuixTheme.textStyles.bodySmall,
                                color = MiuixTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = syncEnabled && hasPermission,
                            onCheckedChange = { enabled ->
                                if (hasPermission) {
                                    scope.launch {
                                        syncEnabled = enabled
                                        val db = OvertimeApplication.database
                                        val configDao = db.configDao()
                                        configDao.saveConfig(
                                            com.mars.overtime.database.AppConfig("calendar_enabled", enabled.toString())
                                        )
                                    }
                                } else {
                                    permissionLauncher.launch(calendarPermissions)
                                }
                            },
                            enabled = hasPermission
                        )
                    }

                    if (calendarId != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "日历ID: $calendarId",
                            style = MiuixTheme.textStyles.bodySmall,
                            color = MiuixTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "测试日历同步",
                        style = MiuixTheme.textStyles.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "点击下方按钮测试日历同步功能",
                        style = MiuixTheme.textStyles.bodySmall,
                        color = MiuixTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (testResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (testResult?.startsWith("成功") == true) 
                                    MiuixTheme.colorScheme.primaryContainer 
                                else 
                                    MiuixTheme.colorScheme.errorContainer
                            )
                        ) {
                            Text(
                                text = testResult ?: "",
                                style = MiuixTheme.textStyles.bodySmall,
                                modifier = Modifier.padding(8.dp),
                                color = if (testResult?.startsWith("成功") == true) 
                                    MiuixTheme.colorScheme.onPrimaryContainer 
                                else 
                                    MiuixTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            if (hasPermission) {
                                scope.launch {
                                    try {
                                        val id = CalendarSyncManager.getOrCreateCalendarId(context)
                                        calendarId = id
                                        
                                        if (id != null) {
                                            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                            val testRecord = OvertimeRecord(
                                                id = 0,
                                                date = dateFormat.format(Date()),
                                                startTime = "17:00",
                                                endTime = "20:00",
                                                duration = 3.0,
                                                type = OvertimeType.WORKDAY,
                                                money = 100.0,
                                                remark = "测试日历同步",
                                                createTime = System.currentTimeMillis()
                                            )
                                            val success = CalendarSyncManager.addEvent(context, testRecord)
                                            testResult = if (success) "成功！已创建测试日历事件" else "失败：无法添加日历事件"
                                        } else {
                                            testResult = "失败：无法获取或创建日历"
                                        }
                                    } catch (e: Exception) {
                                        testResult = "失败：${e.message}"
                                        e.printStackTrace()
                                    }
                                }
                            } else {
                                permissionLauncher.launch(calendarPermissions)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = hasPermission
                    ) {
                        Text(if (hasPermission) "测试同步" else "请先授权日历权限")
                    }
                }
            }
        }
    }
}
