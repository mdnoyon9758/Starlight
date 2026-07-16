# White-Label Barcode Reader: Implementation Plan

## Codebase Analysis

**Project root**: `new work/barcode-reader/`
**Package**: `com.barcodereader`
**Existing source files**: 33 Kotlin files, 0 XML string resources, 15 drawable XMLs

**Key findings**:
- Room entities/DAOs are well-designed (5 entities, 4 DAOs, converters) but completely disconnected
- Two redundant SharedPreferences classes: `HistoryStorage` and `BarcodeRepository`
- All 3 screens manage state via `remember`/`mutableStateOf` -- no ViewModels
- Services instantiated inline in Composables (no DI)
- No `strings.xml` -- every user-facing string is hardcoded
- Config classes (`WhiteLabelConfig`, `AdConfig`, `FirebaseConfig`, `AppConfig`) are complete but reference no SDK dependencies
- `proguard-rules.pro` exists but not yet inspected

---

## Phase 0: Foundation (Must Complete First -- Everything Else Depends On This)

### 0.1 Add Missing Dependencies to `app/build.gradle.kts`

**File**: `new work/barcode-reader/app/build.gradle.kts`

Add to plugins block:
```kotlin
id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
id("com.google.dagger.hilt.android") version "2.52" apply false
```

Add to project-level `build.gradle.kts`:
```kotlin
id("com.google.devtools.ksp") version "2.0.21-1.0.28" apply false
id("com.google.dagger.hilt.android") version "2.52" apply false
```

Add to `app/build.gradle.kts` plugins:
```kotlin
id("com.google.devtools.ksp")
id("com.google.dagger.hilt.android")
```

Add dependencies block entries:
```kotlin
// Room
val roomVersion = "2.6.1"
implementation("androidx.room:room-runtime:$roomVersion")
implementation("androidx.room:room-ktx:$roomVersion")
ksp("androidx.room:room-compiler:$roomVersion")

// Hilt
implementation("com.google.dagger:hilt-android:2.52")
ksp("com.google.dagger:hilt-android-compiler:2.52")
implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

// DataStore (replaces SharedPreferences)
implementation("androidx.datastore:datastore-preferences:1.1.1")

// WorkManager
implementation("androidx.work:work-runtime-ktx:2.9.1")
implementation("androidx.hilt:hilt-work:1.2.0")
ksp("com.google.dagger:hilt-compiler:1.2.0")

// Paging (for large history lists)
implementation("androidx.paging:paging-runtime-ktx:3.3.2")
implementation("androidx.paging:paging-compose:3.3.2")

// Security (encrypted storage)
implementation("androidx.security:security-crypto:1.1.0-alpha06")

// Biometric
implementation("androidx.biometric:biometric:1.1.0")

// PDF generation
implementation("com.itextpdf:itext7-core:8.0.2")

// Apache POI for XLSX
implementation("org.apache.poi:poi-ooxml:5.2.5")

// SVG generation (barcode)
implementation("com.caverock:androidsvg:1.4")

// Accompanist (permissions, system UI)
implementation("com.google.accompanist:accompanist-permissions:0.34.0")
```

### 0.2 Create `AppDatabase` @Database Class

**New file**: `app/src/main/java/com/barcodereader/data/local/database/AppDatabase.kt`

```kotlin
@Database(
    entities = [
        ScanHistoryEntity::class,
        TagEntity::class,
        CategoryEntity::class,
        FolderEntity::class,
        ScanTagCrossRef::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scanHistoryDao(): ScanHistoryDao
    abstract fun tagDao(): TagDao
    abstract fun categoryDao(): CategoryDao
    abstract fun folderDao(): FolderDao
}
```

### 0.3 Create Hilt Application Class

**New file**: `app/src/main/java/com/barcodereader/BarcodeReaderApp.kt`

```kotlin
@HiltAndroidApp
class BarcodeReaderApp : Application() {
    @Inject lateinit var database: AppDatabase
    override fun onCreate() {
        super.onCreate()
        AppConfig.init(this)
    }
}
```

**Modify**: `AndroidManifest.xml` -- add `android:name=".BarcodeReaderApp"` to `<application>`

### 0.4 Create Hilt DI Modules

**New files**:
- `app/src/main/java/com/barcodereader/di/DatabaseModule.kt` -- provides `AppDatabase`, all DAOs
- `app/src/main/java/com/barcodereader/di/RepositoryModule.kt` -- binds repository interfaces to implementations
- `app/src/main/java/com/barcodereader/di/ServiceModule.kt` -- provides `ProductLookupService`, etc.

### 0.5 Create Repository Layer

**New files**:
- `app/src/main/java/com/barcodereader/domain/repository/ScanHistoryRepository.kt`
- `app/src/main/java/com/barcodereader/domain/repository/TagRepository.kt`
- `app/src/main/java/com/barcodereader/domain/repository/CategoryRepository.kt`
- `app/src/main/java/com/barcodereader/domain/repository/FolderRepository.kt`
- `app/src/main/java/com/barcodereader/domain/repository/PreferencesRepository.kt`

### 0.6 Create UseCase Layer

**New files**:
- `app/src/main/java/com/barcodereader/domain/usecase/scan/SaveScanUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/scan/GetScansUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/scan/SearchScansUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/scan/DeleteScanUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/scan/BulkDeleteUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/scan/ToggleFavoriteUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/scan/GetStatisticsUseCase.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/tag/TagUseCases.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/category/CategoryUseCases.kt`
- `app/src/main/java/com/barcodereader/domain/usecase/folder/FolderUseCases.kt`

### 0.7 Create ViewModels

**New files**:
- `app/src/main/java/com/barcodereader/ui/scan/ScanViewModel.kt`
- `app/src/main/java/com/barcodereader/ui/history/HistoryViewModel.kt`
- `app/src/main/java/com/barcodereader/ui/generate/GenerateViewModel.kt`

### 0.8 Migrate from SharedPreferences to Room

**Migration strategy**:
1. Keep `HistoryStorage` temporarily -- add a one-time migration in `AppDatabase` companion
2. On first launch with Room, read SharedPreferences, map `ScanHistory` -> `ScanHistoryEntity`, insert into Room
3. Delete SharedPreferences file after successful migration
4. Remove `HistoryStorage.kt` and `BarcodeRepository.kt`
5. Update `AppNavigation.kt` to stop passing `HistoryStorage`

### 0.9 Refactor `MainActivity` for Hilt

```kotlin
@AndroidEntryPoint
class MainActivity : ComponentActivity() { ... }
```

---

## Phase 1: Core Architecture Fixes

### 1.1 Create `strings.xml` for All Languages

**New files**:
- `app/src/main/res/values/strings.xml` (English)
- `app/src/main/res/values-es/strings.xml` (Spanish)
- `app/src/main/res/values-de/strings.xml` (German)
- `app/src/main/res/values-fr/strings.xml` (French)
- `app/src/main/res/values-ar/strings.xml` (Arabic)
- `app/src/main/res/values-hi/strings.xml` (Hindi)

### 1.2 Update All Screens to Use `stringResource()`

### 1.3 Add RTL Support

### 1.4 Add ProGuard Rules

---

## Phase 2: Scanner Upgrade

- Live CameraX preview
- Continuous scan mode
- Batch scan
- Flash toggle
- Zoom control

## Phase 3: Generator Upgrade

- 12+ QR templates
- Customization (colors, logo, size, error correction)

## Phase 4: History Upgrade

- Folders, categories, tags UI
- Search, sort, filter
- Bulk operations
- Statistics dashboard

## Phase 5: Export, Backup, Security

- 6 export formats (PNG, SVG, PDF, CSV, JSON, XLSX)
- Local backup with WorkManager
- PIN, biometric, lock timeout

## Phase 6: AdMob & Firebase (Graceful Fallback)

- AdMob: banner, interstitial, rewarded
- Firebase: analytics, crashlytics, remote config
- All gated by config checks

## Phase 7: Settings, Legal, Documentation

- Settings screen
- About screen
- Privacy Policy, Terms, Licenses (WebView)
- White-label documentation

---

## Complete File Manifest

### New Files (80+):
See detailed list in analysis above.

### Modified Files (12):
1. `app/build.gradle.kts`
2. `build.gradle.kts` (project)
3. `AndroidManifest.xml`
4. `MainActivity.kt`
5. `ScanScreen.kt`
6. `HistoryScreen.kt`
7. `GenerateScreen.kt`
8. `AppNavigation.kt`
9. `IOSTabBar.kt`
10. `proguard-rules.pro`
11. `Theme.kt`
12. `settings.gradle.kts`

### Deleted Files (2):
1. `HistoryStorage.kt`
2. `BarcodeRepository.kt`

---

## Execution Priority

| Priority | Phase | Est. Effort |
|----------|-------|-------------|
| P0 | Foundation | 3-4 days |
| P1 | Localization | 1-2 days |
| P2 | Scanner upgrade | 2-3 days |
| P3 | Generator upgrade | 2-3 days |
| P4 | History upgrade | 3-4 days |
| P5 | Export/Backup/Security | 3-4 days |
| P6 | AdMob/Firebase | 1-2 days |
| P7 | Settings/Legal/Docs | 2-3 days |

**Total**: 17-24 developer-days
