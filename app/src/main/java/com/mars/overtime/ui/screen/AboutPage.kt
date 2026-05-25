package com.mars.overtime.ui.screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val appIcon = remember { loadMipmapIcon(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于") },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    appIcon?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "应用图标",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(96.dp)
                        )
                    } ?: Icon(
                        imageVector = Icons.Default.Work,
                        contentDescription = "应用图标",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "加班记",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "版本 1.0.16",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            AboutInfoItem("作者", "Mars")
            AboutInfoItem("GitHub", "https://github.com/moonbai/overtime-note")
            AboutInfoItem("邮箱", "mars@example.com")
            AboutInfoItem("开源协议", "MIT")

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "极简个人加班工时记录工具\n本地优先、全渠道推送、日历同步、完整备份",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "© 2024 Mars. All rights reserved.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun loadMipmapIcon(context: Context): Bitmap? {
    return try {
        val resources = context.resources
        val packageName = context.packageName
        
        val iconNames = listOf("ic_launcher", "ic_launcher_round")
        val densities = listOf("xxxhdpi", "xxhdpi", "xhdpi", "hdpi", "mdpi")
        
        for (iconName in iconNames) {
            for (density in densities) {
                val resName = "${iconName}_foreground"
                val resId = resources.getIdentifier(resName, "mipmap", packageName)
                if (resId != 0) {
                    val drawable: Drawable? = try {
                        resources.getDrawableForDensity(resId, resources.displayMetrics.densityDpi, null)
                    } catch (e: Exception) {
                        null
                    }
                    return drawableToBitmap(drawable)
                }
            }
        }
        
        for (iconName in iconNames) {
            val resId = resources.getIdentifier(iconName, "mipmap", packageName)
            if (resId != 0) {
                val drawable: Drawable? = try {
                    resources.getDrawable(resId, null)
                } catch (e: Exception) {
                    null
                }
                return drawableToBitmap(drawable)
            }
        }
        
        null
    } catch (e: Exception) {
        null
    }
}

private fun drawableToBitmap(drawable: Drawable?): Bitmap? {
    if (drawable == null) return null
    
    return when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        is VectorDrawable -> {
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(1),
                drawable.intrinsicHeight.coerceAtLeast(1),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
        else -> {
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 48
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 48
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}

@Composable
fun AboutInfoItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}