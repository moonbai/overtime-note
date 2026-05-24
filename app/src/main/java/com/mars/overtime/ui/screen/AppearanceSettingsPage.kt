package com.mars.overtime.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mars.overtime.util.AppSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppearanceSettingsPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val useMiuix by AppSettings.getUseMiux(context).collectAsState(initial = false)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("外观设置") },
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
        ) {
            ListItem(
                headlineContent = { Text("使用 MiuiX 主题") },
                supportingContent = { Text("启用 MiuiX 风格的界面组件") },
                trailingContent = {
                    Switch(
                        checked = useMiuix,
                        onCheckedChange = { checked ->
                            scope.launch {
                                AppSettings.saveUseMiux(context, checked)
                            }
                        }
                    )
                }
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "说明：切换主题后应用将自动重新应用主题。",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
