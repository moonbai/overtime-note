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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowBack
import top.yukonga.miuix.kmp.icon.icons.Code
import top.yukonga.miuix.kmp.icon.icons.Edit
import top.yukonga.miuix.kmp.icon.icons.Notifications
import top.yukonga.miuix.kmp.icon.icons.CloudUpload
import top.yukonga.miuix.kmp.icon.icons.AttachMoney
import top.yukonga.miuix.kmp.icon.icons.Celebration
import top.yukonga.miuix.kmp.theme.MiuixTheme

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
                title = { Text("关于") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(MiuixIcons.ArrowBack, contentDescription = "返回")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                appIcon?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "应用图标",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(110.dp)
                    )
                } ?: Icon(
                    imageVector = MiuixIcons.Celebration,
                    contentDescription = "应用图标",
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "加班记",
                style = MiuixTheme.textStyles.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "版本 $versionName",
                style = MiuixTheme.textStyles.bodyMedium,
                color = MiuixTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "用心记录，每一份付出都值得被看见",
                style = MiuixTheme.textStyles.titleSmall,
                color = MiuixTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "关于应用",
                        style = MiuixTheme.textStyles.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "一款简洁实用的加班记录与薪资计算工具，帮您轻松记录每一次加班，精准计算应得报酬。",
                        style = MiuixTheme.textStyles.bodyMedium,
                        color = MiuixTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "主要功能",
                style = MiuixTheme.textStyles.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(12.dp))

            FeatureGrid()

            Spacer(modifier = Modifier.height(32.dp))

            AboutItem(
                icon = MiuixIcons.Code,
                title = "作者",
                subtitle = "Mars",
                onClick = {}
            )

            AboutItem(
                icon = MiuixIcons.Code,
                title = "开源仓库",
                subtitle = "github.com/moonbai/overtime-note",
                onClick = {}
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "© 2024 Mars",
                style = MiuixTheme.textStyles.bodySmall,
                color = MiuixTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "All rights reserved",
                style = MiuixTheme.textStyles.bodySmall,
                color = MiuixTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FeatureGrid() {
    Column(
        modifier = Modifier.padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = MiuixIcons.Edit,
                title = "本地记录",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = MiuixIcons.Notifications,
                title = "多渠道推送",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = MiuixIcons.Celebration,
                title = "智能识别",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = MiuixIcons.CloudUpload,
                title = "云端备份",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FeatureCard(
                icon = MiuixIcons.AttachMoney,
                title = "薪资计算",
                modifier = Modifier.weight(1f)
            )
            FeatureCard(
                icon = MiuixIcons.Edit,
                title = "更多功能",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun FeatureCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MiuixTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                style = MiuixTheme.textStyles.bodyMedium,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun AboutItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MiuixTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MiuixTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MiuixTheme.textStyles.titleSmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MiuixTheme.textStyles.bodySmall,
                    color = MiuixTheme.colorScheme.onSurfaceVariant
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
        "ic_launcher_round"
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
