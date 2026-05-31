package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.OvertimeRecord
import com.mars.overtime.database.OvertimeType
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun StatisticsPage() {
    val db = OvertimeApplication.database
    val dao = db.overtimeDao()
    val configDao = db.configDao()
    val scope = rememberCoroutineScope()

    val currentCalendar = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+8"))
    var selectedYear by remember { mutableStateOf(currentCalendar.get(java.util.Calendar.YEAR)) }
    var selectedMonth by remember { mutableStateOf(currentCalendar.get(java.util.Calendar.MONTH) + 1) }

    val records = remember { mutableStateListOf<OvertimeRecord>() }

    LaunchedEffect(selectedYear, selectedMonth) {
        scope.launch {
            val monthStart = String.format("%04d-%02d-01", selectedYear, selectedMonth)

            val lastDay = java.util.Calendar.getInstance(java.util.TimeZone.getTimeZone("GMT+8")).apply {
                set(selectedYear, selectedMonth - 1, 1)
                add(java.util.Calendar.MONTH, 1)
                add(java.util.Calendar.DAY_OF_MONTH, -1)
            }.get(java.util.Calendar.DAY_OF_MONTH)
            val monthEnd = String.format("%04d-%02d-%02d", selectedYear, selectedMonth, lastDay)

            val monthRecords = dao.getRecordsByDateRange(monthStart, monthEnd)
            records.clear()
            records.addAll(monthRecords.first())
        }
    }

    val allConfigs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    val baseSalary = allConfigs.find { it.key == "base_salary" }?.value?.toDoubleOrNull() ?: 0.0
    val workdayRate = allConfigs.find { it.key == "workday_rate" }?.value?.toDoubleOrNull() ?: 1.5
    val restdayRate = allConfigs.find { it.key == "restday_rate" }?.value?.toDoubleOrNull() ?: 2.0
    val holidayRate = allConfigs.find { it.key == "holiday_rate" }?.value?.toDoubleOrNull() ?: 3.0

    val hourlyRate = if (baseSalary > 0) baseSalary / 21.75 / 8 else 0.0

    val totalHours = records.sumOf { it.duration }
    val workdayRecords = records.filter { it.type == OvertimeType.WORKDAY }
    val restdayRecords = records.filter { it.type == OvertimeType.RESTDAY }
    val holidayRecords = records.filter { it.type == OvertimeType.HOLIDAY }

    val workdayHours = workdayRecords.sumOf { it.duration }
    val restdayHours = restdayRecords.sumOf { it.duration }
    val holidayHours = holidayRecords.sumOf { it.duration }

    val workdaySalary = workdayHours * hourlyRate * workdayRate
    val restdaySalary = restdayHours * hourlyRate * restdayRate
    val holidaySalary = holidayHours * hourlyRate * holidayRate
    val totalSalary = workdaySalary + restdaySalary + holidaySalary

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("统计", style = MiuixTheme.textStyles.titleLarge)
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MiuixTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = {
                            if (selectedMonth == 1) {
                                selectedYear--
                                selectedMonth = 12
                            } else {
                                selectedMonth--
                            }
                        }) {
                            Icon(MiuixIcons.ArrowLeft, contentDescription = "上一月")
                        }
                        Text(
                            text = "${selectedYear}年${selectedMonth}月",
                            style = MiuixTheme.textStyles.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = {
                            if (selectedMonth == 12) {
                                selectedYear++
                                selectedMonth = 1
                            } else {
                                selectedMonth++
                            }
                        }) {
                            Icon(MiuixIcons.ArrowRight, contentDescription = "下一月")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MiuixTheme.colorScheme.primaryContainer
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "月度加班汇总",
                            style = MiuixTheme.textStyles.titleMedium,
                            color = MiuixTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = String.format("%.1f", totalHours),
                            style = MiuixTheme.textStyles.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "小时",
                            style = MiuixTheme.textStyles.bodyLarge,
                            color = MiuixTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = String.format("¥%.2f", totalSalary),
                            style = MiuixTheme.textStyles.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = MiuixTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MiuixTheme.colorScheme.surface
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "分类统计",
                            style = MiuixTheme.textStyles.titleMedium,
                            modifier = Modifier.padding(bottom = 16.dp),
                            color = MiuixTheme.colorScheme.onSurface
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            StatItem(
                                title = "工作日",
                                hours = workdayHours,
                                salary = workdaySalary,
                                color = MiuixTheme.colorScheme.primary
                            )
                            StatItem(
                                title = "休息日",
                                hours = restdayHours,
                                salary = restdaySalary,
                                color = MiuixTheme.colorScheme.secondary
                            )
                            StatItem(
                                title = "节假日",
                                hours = holidayHours,
                                salary = holidaySalary,
                                color = Color(0xFFFF5722)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Text(
                    text = "详细记录",
                    style = MiuixTheme.textStyles.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp),
                    color = MiuixTheme.colorScheme.onBackground
                )
            }

            if (records.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 64.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "本月暂无加班记录",
                            style = MiuixTheme.textStyles.bodyLarge,
                            color = MiuixTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(records.sortedByDescending { it.date }) { record ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
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
                                Text(
                                    text = record.date,
                                    style = MiuixTheme.textStyles.titleSmall
                                )
                                Text(
                                    text = when (record.type) {
                                        OvertimeType.WORKDAY -> "工作日"
                                        OvertimeType.RESTDAY -> "休息日"
                                        OvertimeType.HOLIDAY -> "节假日"
                                    },
                                    style = MiuixTheme.textStyles.bodySmall,
                                    color = when (record.type) {
                                        OvertimeType.WORKDAY -> MiuixTheme.colorScheme.primary
                                        OvertimeType.RESTDAY -> MiuixTheme.colorScheme.secondary
                                        OvertimeType.HOLIDAY -> Color(0xFFFF5722)
                                    }
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${record.duration} 小时",
                                    style = MiuixTheme.textStyles.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = String.format("¥%.2f", calculateSalary(record, hourlyRate, workdayRate, restdayRate, holidayRate)),
                                    style = MiuixTheme.textStyles.bodyLarge,
                                    color = MiuixTheme.colorScheme.secondary
                                )
                            }
                            if (record.remark.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = record.remark,
                                    style = MiuixTheme.textStyles.bodySmall,
                                    color = MiuixTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(
    title: String,
    hours: Double,
    salary: Double,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 8.dp)
    ) {
        Text(
            text = String.format("%.1f", hours),
            style = MiuixTheme.textStyles.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = "小时",
            style = MiuixTheme.textStyles.bodySmall,
            color = MiuixTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = String.format("¥%.2f", salary),
            style = MiuixTheme.textStyles.bodySmall,
            color = MiuixTheme.colorScheme.secondary
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = title,
            style = MiuixTheme.textStyles.bodySmall,
            color = MiuixTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun calculateSalary(
    record: OvertimeRecord,
    hourlyRate: Double,
    workdayRate: Double,
    restdayRate: Double,
    holidayRate: Double
): Double {
    return when (record.type) {
        OvertimeType.WORKDAY -> record.duration * hourlyRate * workdayRate
        OvertimeType.RESTDAY -> record.duration * hourlyRate * restdayRate
        OvertimeType.HOLIDAY -> record.duration * hourlyRate * holidayRate
    }
}
