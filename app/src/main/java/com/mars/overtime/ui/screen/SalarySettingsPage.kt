package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mars.overtime.OvertimeApplication
import com.mars.overtime.database.AppConfig
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.*
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.*
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun SalarySettingsPage(
    onNavigateBack: () -> Unit
) {
    val db = OvertimeApplication.database
    val configDao = db.configDao()

    val scope = rememberCoroutineScope()

    val allConfigs by configDao.getAllConfigs().collectAsState(initial = emptyList())

    var baseSalary by remember { mutableStateOf(allConfigs.find { it.key == "base_salary" }?.value ?: "2200") }
    var workdayRate by remember { mutableStateOf(allConfigs.find { it.key == "workday_rate" }?.value ?: "1.5") }
    var restdayRate by remember { mutableStateOf(allConfigs.find { it.key == "restday_rate" }?.value ?: "2.0") }
    var holidayRate by remember { mutableStateOf(allConfigs.find { it.key == "holiday_rate" }?.value ?: "3.0") }

    LaunchedEffect(allConfigs) {
        baseSalary = allConfigs.find { it.key == "base_salary" }?.value ?: "2200"
        workdayRate = allConfigs.find { it.key == "workday_rate" }?.value ?: "1.5"
        restdayRate = allConfigs.find { it.key == "restday_rate" }?.value ?: "2.0"
        holidayRate = allConfigs.find { it.key == "holiday_rate" }?.value ?: "3.0"
    }

    val baseSalaryNum = baseSalary.toDoubleOrNull() ?: 0.0
    val workdayRateNum = workdayRate.toDoubleOrNull() ?: 1.5
    val restdayRateNum = restdayRate.toDoubleOrNull() ?: 2.0
    val holidayRateNum = holidayRate.toDoubleOrNull() ?: 3.0
    
    val hourlyRate = if (baseSalaryNum > 0) baseSalaryNum / 21.75 / 8 else 0.0

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("薪资设置") },
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
            Text("基础薪资基数 (元/月)", style = MiuixTheme.textStyles.titleMedium)
            OutlinedTextField(
                value = baseSalary,
                onValueChange = { baseSalary = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MiuixTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("加班工资计算", style = MiuixTheme.textStyles.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "小时基础工资: ¥${"%.2f".format(hourlyRate)}/小时",
                        style = MiuixTheme.textStyles.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "工作日加班: ¥${"%.2f".format(hourlyRate * workdayRateNum)}/小时",
                        style = MiuixTheme.textStyles.bodyMedium
                    )
                    Text(
                        text = "休息日加班: ¥${"%.2f".format(hourlyRate * restdayRateNum)}/小时",
                        style = MiuixTheme.textStyles.bodyMedium
                    )
                    Text(
                        text = "法定节假日: ¥${"%.2f".format(hourlyRate * holidayRateNum)}/小时",
                        style = MiuixTheme.textStyles.bodyMedium
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text("工作日延时倍率", style = MiuixTheme.textStyles.titleMedium)
            OutlinedTextField(
                value = workdayRate,
                onValueChange = { workdayRate = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("休息日倍率", style = MiuixTheme.textStyles.titleMedium)
            OutlinedTextField(
                value = restdayRate,
                onValueChange = { restdayRate = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("法定节假日倍率", style = MiuixTheme.textStyles.titleMedium)
            OutlinedTextField(
                value = holidayRate,
                onValueChange = { holidayRate = it },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    scope.launch {
                        configDao.saveConfigs(
                            listOf(
                                AppConfig("base_salary", baseSalary),
                                AppConfig("workday_rate", workdayRate),
                                AppConfig("restday_rate", restdayRate),
                                AppConfig("holiday_rate", holidayRate)
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("保存")
            }
        }
    }
}
