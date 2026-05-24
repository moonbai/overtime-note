
## English | [中文](README.md)

### Overtime Record App

<img src="https://img.shields.io/badge/Android-10%2B-blue" alt="Android 10+"><img src="https://img.shields.io/badge/Kotlin-1.9.24-purple" alt="Kotlin 1.9.24"><img src="https://img.shields.io/badge/Jetpack%20Compose-2024.10.01-green" alt="Compose"><img src="https://img.shields.io/badge/MiuiX-0.0.35-orange" alt="MiuiX">

Minimalist personal overtime tracking tool for Android, with local-first design, multi-channel notifications, calendar sync, and complete backup.

### Features

- ✅ **Three Overtime Types**: Workday Overtime, Weekend Overtime, Statutory Holiday Overtime
- ✅ **Custom Salary Calculation**: Default base salary 2200, independent multiplier settings
- ✅ **Android Calendar Sync**: Calendar events with format "Type-Duration", manual/auto sync
- ✅ **Holidays & Workdays**: Correctly identifies compensatory workdays as workday overtime, supports manual holiday rule updates
- ✅ **Multi-channel Notifications**: DingTalk, Feishu, WxPusher, Custom HTTP, with test notification support
- ✅ **Backup & Restore**: Local JSON backup, WebDAV cloud backup/sync
- ✅ **MiuiX UI**: Global MiuiX components, MIUI design style
- ✅ **Immersive Status Bar**: Android 16+ pill-shaped navigation support
- ✅ **Dark/Light Mode**: Follows system by default, manual toggle support
- ✅ **No Code Obfuscation**: minifyEnabled=false

### Tech Stack

| Tech | Version |
|------|---------|
| Language | Kotlin 1.9.24 |
| UI | Jetpack Compose 2024.10.01 + MiuiX 0.0.35 |
| Database | Room 2.6.1 |
| Network | OkHttp 4.12.0 |
| Min SDK | 24 (Android 7.0) |
| Target SDK | 35 (Android 15) |

### Project Structure



```
app/src/main/java/com/mars/overtime/
├── database/           # Room database
│   ├── OvertimeRecord.kt  # Overtime record entity
│   ├── AppConfig.kt       # Config entity
│   ├── AppDatabase.kt     # Database class
│   ├── OvertimeDao.kt     # Overtime record DAO
│   └── ConfigDao.kt       # Config DAO
├── push/               # Push related
│   ├── PushManager.kt     # 4‑channel push manager
│   └── CalendarSyncManager.kt # Calendar sync
├── ui/                 # UI screens
│   ├── MainNav.kt         # Navigation
│   ├── theme/Theme.kt     # Theme
│   └── screen/            # Pages
└── util/               # Utilities
    ├── SalaryCalculator.kt # Salary calculation
    ├── HolidayManager.kt  # Holiday management
    ├── BackupManager.kt   # Backup & restore
    ├── WebDavManager.kt   # WebDAV
    └── DataMigrationUtil.kt # Data migration
```

### Build & Run

```bash
git clone <repo-url>
cd OverTime_App
./gradlew assembleDebug
```

Output APK: `app/build/outputs/apk/debug/overtime-1.0.16-debug.apk`

### CI/CD

See `.github/workflows/build.yml` for automated build workflow with fixed signing.

### Author & License

- **Author**: Mars
- **License**: MIT License
