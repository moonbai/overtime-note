package com.mars.overtime.ui.screen

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutPage(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val appIcon = remember { loadMipmapIcon(context) }
    val versionName = remember { getVersionName(context) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于加班记") },
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                appIcon?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "应用图标",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(100.dp)
                    )
                } ?: Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "应用图标",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "加班记",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "版本 $versionName",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "用心记录，每一份付出都值得被看见",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "一款简洁实用的加班记录与薪资计算工具，帮你轻松记录每一次加班，精准计算应得报酬。",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "主要功能",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            FeatureItem(
                icon = Icons.Default.Edit,
                title = "本地记录",
                description = "所有数据存储在本地，保障隐私安全"
            )
            FeatureItem(
                icon = Icons.Default.Notifications,
                title = "多渠道推送",
                description = "支持钉钉、飞书、企业微信等推送"
            )
            FeatureItem(
                icon = Icons.Default.CalendarToday,
                title = "日历同步",
                description = "自动添加加班事件到系统日历"
            )
            FeatureItem(
                icon = Icons.Default.CloudUpload,
                title = "云端备份",
                description = "WebDAV备份，多设备同步数据"
            )
            FeatureItem(
                icon = Icons.Default.AttachMoney,
                title = "薪资计算",
                description = "灵活设置，自动计算加班报酬"
            )
            FeatureItem(
                icon = Icons.Default.Celebration,
                title = "智能识别",
                description = "自动判断节假日与休息日"
            )

            Spacer(modifier = Modifier.height(40.dp))

            AboutItem(
                icon = {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "作者",
                subtitle = "Mars",
                onClick = {}
            )

            AboutItem(
                icon = {
                    Icon(
                        Icons.Default.GitHub,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                },
                title = "开源仓库",
                subtitle = "github.com/moonbai/overtime-note",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "© 2024 Mars",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = "All rights reserved",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AboutItem(
    icon: @Composable () -> Unit,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                icon()
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun loadMipmapIcon(context: Context): Bitmap? {
    val resources = context.resources
    val packageName = context.packageName
    
    val mipmapNames = listOf(
        "ic_launcher",
        "ic_launcher_round",
        "ic_launcher_foreground"
    )
    
    val densitySuffixes = listOf(
        "xxxhdpi",
        "xxhdpi",
        "xhdpi",
        "hdpi",
        "mdpi",
        ""
    )
    
    for (name in mipmapNames) {
        for (suffix in densitySuffixes) {
            val resName = if (suffix.isEmpty()) name else "${name}_${suffix}"
            val resId = resources.getIdentifier(resName, "mipmap", packageName)
            if (resId != 0) {
                try {
                    val options = BitmapFactory.Options().apply {
                        inPreferredConfig = Bitmap.Config.ARGB_8888
                    }
                    val bitmap = BitmapFactory.decodeResource(resources, resId, options)
                    if (bitmap != null) {
                        return bitmap
                    }
                } catch (e: Exception) {
                    continue
                }
            }
        }
    }
    
    return try {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        val drawable = packageInfo.applicationInfo.loadIcon(packageManager)
        drawableToBitmap(drawable)
    } catch (e: Exception) {
        null
    }
}

private fun getVersionName(context: Context): String {
    return try {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName ?: "1.0"
    } catch (e: Exception) {
        "1.0"
    }
}

private fun drawableToBitmap(drawable: Drawable): Bitmap? {
    return when (drawable) {
        is BitmapDrawable -> drawable.bitmap
        else -> {
            val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 192
            val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 192
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
}
