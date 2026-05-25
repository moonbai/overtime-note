## 中文 | [English](README_en.md)

# 加班记 (OverTime-Note)

<img src="https://img.shields.io/badge/Android-10%2B-blue" alt="Android 10+"><img src="https://img.shields.io/badge/Kotlin-1.9.24-purple" alt="Kotlin 1.9.24"><img src="https://img.shields.io/badge/Jetpack%20Compose-2024.10.01-green" alt="Compose"><img src="https://img.shields.io/badge/MiuiX-0.0.35-orange" alt="MiuiX">

极简个人加班工时记录工具，本地优先、全渠道推送、日历同步、完整备份。

## 功能特性

- ✅ **三类加班分类**：工作日延时、周末休息日、法定节假日
- ✅ **自定义薪资计算**：基础薪资基数默认2200，三类加班独立设置倍率
- ✅ **安卓日历同步**：日历标题格式为“加班类型-加班时长”，支持手动/自动同步
- ✅ **节假日与调休**：调休工作日正确判定为工作日加班，支持手动更新节假日规则
- ✅ **全渠道推送**：钉钉、飞书、WxPusher、自定义HTTP，支持测试推送
- ✅ **数据备份恢复**：本地JSON备份、WebDAV云端备份/同步
- ✅ **沉浸式状态栏**：适配安卓16小白条、全面屏
- ✅ **深浅色模式**：默认跟随系统，支持手动切换
- ✅ **无代码混淆**：minifyEnabled=false，输出可读源码

## 技术栈

| 技术 | 版本 |
|------|------|
| 语言 | Kotlin 1.9.24 |
| UI | Jetpack Compose 2024.10.01 |
| 数据库 | Room 2.6.1 |
| 网络 | OkHttp 4.12.0 |
| 最低SDK | 24 (Android 7.0) |
| 目标SDK | 35 (Android 15) |

## 项目结构

```
app/src/main/java/com/mars/overtime/
├── database/           # Room数据库
│   ├── OvertimeRecord.kt  # 加班记录实体
│   ├── AppConfig.kt       # 配置实体
│   ├── AppDatabase.kt     # 数据库类
│   ├── OvertimeDao.kt     # 加班记录DAO
│   └── ConfigDao.kt       # 配置DAO
├── push/               # 推送相关
│   ├── PushManager.kt     # 4渠道推送管理
│   └── CalendarSyncManager.kt # 日历同步
├── ui/                 # UI界面
│   ├── MainNav.kt         # 导航
│   ├── theme/Theme.kt     # 主题
│   └── screen/            # 页面
└── util/               # 工具类
    ├── SalaryCalculator.kt # 薪资计算
    ├── HolidayManager.kt  # 节假日管理
    ├── BackupManager.kt   # 备份恢复
    ├── WebDavManager.kt   # WebDAV
    └── DataMigrationUtil.kt # 数据迁移
```

## 构建与运行

### 环境要求

- Android Studio Hedgehog 或更高版本
- JDK 17
- Android SDK 35

### 编译步骤

```bash
git clone <repo-url>
cd OverTime_App
./gradlew assembleDebug
```

APK 输出位置：`app/build/outputs/apk/debug/overtime-1.0.16-debug.apk`

## 打包

本项目配置了 GitHub Actions 自动打包工作流，详见 `.github/workflows/build.yml`。

工作流将：
1. 使用固定签名构建Release APK
2. 自动上传APK到Actions Artifacts
3. 支持手动触发

## 作者与许可

- **作者**：Mars
- **许可**：MIT License
