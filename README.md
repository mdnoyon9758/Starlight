# Barcode Reader - Android APK

A modern barcode and QR code reader app for Android with iOS 26-inspired design.

## Features

### Scanning
- **Capture-to-Scan**: Take a photo or load from gallery to scan barcodes
- **Multiple Formats**: QR Code, EAN-13, UPC-A, Code 128, Code 39, Data Matrix, PDF417, ITF
- **Smart Results**: Each barcode type shows a beautiful, type-specific card:
  - URLs → Clickable browser link
  - Products → Product info with name, brand, nutriscore
  - WiFi → Network card with password
  - Phone → Call button
  - Email → Compose email button
  - Text → Decorated text card

### Generation
- **Dropdown Selector**: Choose from 8 barcode types
- **Large Preview**: 300dp barcode display
- **Share**: Share to social media, WhatsApp, etc.
- **Save to Gallery**: Save generated barcodes directly to phone

### History
- **Scan History**: All scans saved automatically
- **Export**: CSV and JSON export options
- **Favorites**: Mark important scans

## Tech Stack

- **Language**: Kotlin
- **UI**: Jetpack Compose with Material 3
- **Camera**: CameraX
- **Scanning**: Google ML Kit
- **Generation**: ZXing
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35

## Project Structure

```
new work/barcode-reader/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/barcodereader/
│       │   ├── MainActivity.kt
│       │   ├── data/           # History storage
│       │   ├── service/        # Product lookup API
│       │   ├── ui/             # Compose screens
│       │   └── util/           # Utilities
│       └── res/                # Resources
├── build.gradle.kts
└── settings.gradle.kts
```

## Building

```bash
cd new\ work/barcode-reader
gradle assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

## Latest Release

Download the latest APK from [GitHub Releases](https://github.com/mdnoyon9758/Starlight/releases/tag/v3.1.2)

## License

MIT License
