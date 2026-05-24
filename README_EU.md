
## English | [中文](README.md)


### Overtime Record App

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
- **GitHub**: https://github.com/compose-miuix-ui/miuix
- **License**: MIT License
