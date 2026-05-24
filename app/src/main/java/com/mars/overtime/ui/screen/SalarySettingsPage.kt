package com.mars.overtime.ui.screen

import android.app.Application
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalarySettingsPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val application = context.applicationContext as Application
    val db = (application as OvertimeApplication).database
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("薪资设置") },
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
            Text("基础薪资基数 (元/月)", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = baseSalary,
                onValueChange = { baseSalary = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("工作日延时倍率", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = workdayRate,
                onValueChange = { workdayRate = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("休息日倍率", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = restdayRate,
                onValueChange = { restdayRate = it },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("法定节假日倍率", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = holidayRate,
                onValueChange = { holidayRate = it },
                modifier = Modifier.fillMaxWidth()
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
