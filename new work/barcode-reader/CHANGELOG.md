# Changelog

All notable changes to this project will be documented in this file.

## [3.0.0] - 2024-01-15

### Added
- Complete white-label configuration system
- Room database for scan history
- Hilt dependency injection
- ViewModels for all screens
- Continuous scanning mode
- Batch scanning mode
- Flash toggle
- Zoom control
- QR code templates (WiFi, Email, Phone, SMS, Location, Contact)
- Custom QR colors
- PDF export
- XLSX export
- ZIP export/import
- Statistics dashboard
- Settings screen with theme customization
- Security features (PIN, Password, Biometric)
- Localization support (English, Spanish, German, French, Arabic, Hindi)
- Material3 design
- Dark/Light theme support
- AMOLED theme option

### Changed
- Migrated from SharedPreferences to Room database
- Migrated from direct instantiation to Hilt dependency injection
- Updated to Kotlin 2.0
- Updated to Compose BOM 2024.12.01
- Updated to Material3
- Improved UI with modern design patterns
- Extracted all strings to resources for localization

### Fixed
- Fixed duplicate detection in scan history
- Fixed export formatting issues
- Improved error handling throughout the app

### Removed
- Removed legacy SharedPreferences storage
- Removed unused dependencies

## [2.0.0] - 2023-06-01

### Added
- Barcode generation
- QR code generation
- Gallery import
- History management
- CSV/JSON export
- Favorites system

### Changed
- Improved scanning accuracy
- Updated UI design

## [1.0.0] - 2023-01-01

### Added
- Initial release
- Basic barcode scanning
- QR code scanning
- Camera support