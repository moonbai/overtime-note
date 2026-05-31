package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.Add
import top.yukonga.miuix.kmp.icon.icons.Delete
import top.yukonga.miuix.kmp.icon.icons.Edit
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.component.AlertDialog
import top.yukonga.miuix.kmp.component.FloatingActionButton

@Composable
fun HomePage(
    onNavigateToAddEdit: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val db = OvertimeApplication.database
    val dao = db.overtimeDao()

    val records by dao.getAllRecords().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var recordToDelete by remember { mutableStateOf<OvertimeRecord?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("加班记") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddEdit,
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    MiuixIcons.Add,
                    contentDescription = "新建",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("暂无加班记录", style = MiuixTheme.textStyles.bodyLarge)
                    }
                }
            } else {
                items(records) { record ->
                    OvertimeRecordItem(
                        record = record,
                        onEdit = { onNavigateToAddEdit() },
                        onDelete = {
                            recordToDelete = record
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDeleteDialog && recordToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("确认删除") },
            text = { Text("确定要删除这条加班记录吗？") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            recordToDelete?.let { dao.deleteRecord(it) }
                            showDeleteDialog = false
                            recordToDelete = null
                        }
                    }
                ) { Text("删除") }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) { Text("取消") }
            }
        )
    }
}

@Composable
fun OvertimeRecordItem(
    record: OvertimeRecord,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val typeStr = when (record.type) {
        OvertimeType.WORKDAY -> "工作日延时"
        OvertimeType.RESTDAY -> "休息日"
        OvertimeType.HOLIDAY -> "法定节假日"
    }
    
    val dateStr = record.date

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = dateStr,
                    style = MiuixTheme.textStyles.titleMedium
                )
                Text(
                    text = "¥${"%.2f".format(record.money)}",
                    style = MiuixTheme.textStyles.titleMedium,
                    color = MiuixTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "$typeStr | ${record.startTime} - ${record.endTime} | ${"%.2f".format(record.duration)}小时",
                style = MiuixTheme.textStyles.bodyMedium
            )
            if (record.remark.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.remark,
                    style = MiuixTheme.textStyles.bodySmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(MiuixIcons.Edit, contentDescription = "编辑")
                }
                IconButton(onClick = onDelete) {
                    Icon(MiuixIcons.Delete, contentDescription = "删除")
                }
            }
        }
    }
}
