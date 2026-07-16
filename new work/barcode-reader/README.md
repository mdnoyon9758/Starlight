# Barcode Scanner - Premium White-Label Android App

A complete, premium-grade barcode scanner and generator Android application with full white-label capabilities. Perfect for developers who want to publish their own branded barcode scanner app.

## Features

### Scanner
- Camera-based barcode scanning using Google ML Kit
- Gallery image import for offline scanning
- Support for 13+ barcode formats (QR, Code 128, EAN-13, UPC, etc.)
- Continuous scanning mode
- Batch scanning mode
- Flash toggle
- Zoom control

### Generator
- QR Code generation
- Barcode generation (Code 128, Code 39, EAN-13, UPC, PDF417, Data Matrix, ITF)
- WiFi QR codes
- Email QR codes
- Phone QR codes
- SMS QR codes
- Location QR codes
- Contact/VCard QR codes
- Custom colors and styles

### History
- Automatic scan history
- Favorites system
- Pin important scans
- Search functionality
- Sort and filter
- Bulk operations
- CSV/JSON export
- PNG/SVG export
- PDF export
- XLSX export
- ZIP export/import

### Statistics
- Total scans
- Today's scans
- Weekly/monthly statistics
- Most scanned type
- Favorites count

### Settings
- Theme customization (Light/Dark/System/AMOLED)
- Scanner settings (Sound, Haptic feedback)
- Security settings (PIN, Password, Biometric)
- Language selection
- Backup & Restore

### Security
- PIN lock
- Password lock
- Biometric authentication (Fingerprint/Face)
- All optional - app works without them

### White-Label Ready
- Change app name
- Change package name
- Change colors
- Change icons
- Change company info
- All from one configuration file

## Requirements

- Android Studio Hedgehog or newer
- Android SDK 35 (Android 15)
- Minimum SDK 26 (Android 8.0)
- Kotlin 2.0+
- Gradle 8.7+

## Installation

1. Clone or download the project
2. Open in Android Studio
3. Sync Gradle files
4. Build and run

## Configuration

### App Branding

Edit `app/src/main/java/com/barcodereader/config/BrandingConfig.kt`:

```kotlin
object BrandingConfig {
    const val APP_NAME = "Your App Name"
    const val PACKAGE_NAME = "com.yourcompany.yourapp"
    const val APPLICATION_ID = "com.yourcompany.yourapp"
    const val COMPANY_NAME = "Your Company"
    // ... more options
}
```

### AdMob (Optional)

Edit `app/src/main/java/com/barcodereader/config/AdConfig.kt`:

```kotlin
object AdConfig {
    const val ADMOB_APP_ID = "ca-app-pub-XXXXXXXXXXXXXXXX~XXXXXXXXXX"
    const val BANNER_AD_UNIT_ID = "ca-app-pub-XXXXXXXXXXXXXXXX/XXXXXXXXXX"
    // ... more ad units
}
```

### Firebase (Optional)

1. Add `google-services.json` to `app/` directory
2. Firebase will be automatically enabled

## Customization

### Change App Name
1. Edit `BrandingConfig.kt`
2. Update `res/values/strings.xml`

### Change Colors
1. Edit `BrandingConfig.kt` for primary colors
2. Edit `Theme.kt` for full theme customization

### Change Icons
1. Replace icons in `res/mipmap-*` directories
2. Update `BrandingConfig.kt` with new resource IDs

## Architecture

- **UI Layer**: Jetpack Compose with Material3
- **ViewModel Layer**: Hilt-injected ViewModels
- **Domain Layer**: UseCases
- **Data Layer**: Room Database + Repository Pattern
- **DI**: Hilt Dependency Injection

## Tech Stack

- Kotlin
- Jetpack Compose
- Material3
- Hilt
- Room
- CameraX
- ML Kit
- ZXing
- Coroutines
- Flow

## License

Commercial License - See LICENSE.md for details.

## Support

- Email: support@example.com
- Website: https://yourwebsite.com

## Changelog

See CHANGELOG.md for version history.