# 🩺 ContextMonitoring – Health Recording App

A privacy-focused Android application that captures multimodal health context:
- **Heart Rate** via facial video analysis (CameraX)
- **Respiratory Patterns** via audio recording
- **Symptom Tracking** via multi-select checklist

All data is stored **locally on-device** with no cloud dependencies.

---

## 📱 Features

| Module | Description |
|-------|-------------|
| **Heart Rate Monitoring** | Records short video using CameraX for later analysis |
| **Respiratory Audio** | Captures breathing sounds with configurable duration |
| **Symptom Logging** | Select from predefined symptoms (customizable list) |
| **Draft Management** | Auto-saves progress; resume incomplete sessions |
| **History Playback** | Review past sessions with embedded video/audio players |
| **Local-Only Storage** | SQLite database + file system (no internet required) |

---

## 🗃️ Data Persistence

### Database Schema (`app/src/main/java/com/example/contextmonitoring/data/DatabaseContract.kt`)

Two tables manage all data:

#### `draft_recordings`
```sql
_id INTEGER PRIMARY KEY AUTOINCREMENT,
video_path TEXT,
audio_path TEXT,
symptoms TEXT,          -- Comma-separated symptom IDs
started_at INTEGER      -- Unix timestamp (ms)
```

#### `history`
```sql
_id INTEGER PRIMARY KEY AUTOINCREMENT,
video_path TEXT,
audio_path TEXT,
symptoms TEXT,          -- Comma-separated symptom IDs
started_at INTEGER,     -- Session start time
saved_at INTEGER        -- Completion timestamp (ms)
```

> 💡 **Note**: Symptom IDs are stored as comma-delimited strings (e.g., `"cough,fatigue"`). Trailing empty values are automatically filtered.

---

## 🧠 Dummy Calculation Engine (Planned)

When user taps **"Create New Record"**, the app will:
1. Collect current session data:
    - `heartRateVideoPath: String?`
    - `respiratoryAudioPath: String?`
    - `symptomIds: List<String>` (with empty values removed)
2. Pass to a **local analysis engine** (to be implemented)
3. Store results in database (requires schema update)

### Required Database Update
To persist calculation results, add these columns to `HistoryTable`:

```kotlin
// In DatabaseContract.kt
object HistoryTable : BaseColumns {
    // ... existing columns ...
    const val COLUMN_HEART_RATE_RESULT = "heart_rate_bpm"  // INTEGER
    const val COLUMN_RESPIRATORY_RESULT = "respiratory_rate" // INTEGER
    const val COLUMN_RISK_SCORE = "risk_score"            // REAL (0.0-1.0)
}
```

Then update `RecordingDatabaseHelper.kt`:
```kotlin
// In onCreate()
${DatabaseContract.HistoryTable.COLUMN_HEART_RATE_RESULT} INTEGER,
${DatabaseContract.HistoryTable.COLUMN_RESPIRATORY_RESULT} INTEGER,
${DatabaseContract.HistoryTable.COLUMN_RISK_SCORE} REAL
```

> ⚠️ **Migration needed**: Increment `DATABASE_VERSION` and implement `onUpgrade()`.

---

## 📁 Project Structure

```
app/src/main/java/com/example/contextmonitoring/
├── data/                  # Data layer
│   ├── DatabaseContract.kt
│   ├── RecordingDatabaseHelper.kt
│   ├── RecordingRepository.kt     # DB operations
│   ├── RecordingSession.kt        # Session data model
│   └── SymptomsData.kt            # Symptom definitions
├── ui/                    # Compose UI
│   ├── components/        # Reusable widgets
│   │   ├── AudioPlayerModal.kt    # Audio playback modal
│   │   ├── VideoPlayerModal.kt    # Video playback modal
│   │   └── ... 
│   └── screens/           # Feature screens
│       ├── HealthMonitorScreen.kt # Main recording flow
│       ├── AudioRecordingScreen.kt
│       └── VideoRecordingScreen.kt
├── utils/                 # Helpers
│   ├── AudioRecorderUtil.kt
│   ├── CameraXVideoRecorder.kt
│   ├── FileUtils.kt               # File management
│   └── Media3Helper.kt            # Media playback
└── viewmodel/             # Business logic
    └── HealthMonitorViewModel.kt  # Session state management
```

---

## 🛠️ Key Implementation Notes

### 1. **File Management**
- All media files are stored in app-specific directories (`FileUtils.kt`)
- **Critical**: `deleteAllRecords()` **does NOT delete media files**  
  → [See issue #1](#storage-leak-warning) for fix

### 2. **Media Playback**
- **Video**: Uses `ExoPlayer` in `VideoPlayerModal.kt`
- **Audio**: Uses `MediaPlayer` in `AudioPlayerModal.kt`
- Playback modals prevent overlapping (only one media type active at a time)

### 3. **Draft Workflow**
1. User starts recording → draft created in DB
2. Each completed step (video/audio/symptoms) updates draft
3. "Save Session" moves draft to `history` table and deletes draft

---

## ⚠️ Storage Leak Warning

The current **"Delete All Captured"** feature:
- ✅ Clears database tables
- ❌ **Does NOT delete media files** from device storage

**Fix**: Modify `RecordingRepository.deleteAllRecords()` to:
1. Query all `video_path`/`audio_path` values
2. Delete corresponding files using `java.io.File`
3. Then clear database tables

Example:
```kotlin
// In RecordingRepository.kt
fun deleteAllRecords(context: Context) {
    // 1. Get all file paths from DB
    // 2. Delete files: File(path).delete()
    // 3. Clear tables
}
```

---

## 🚀 Future Improvements

| Feature | Status |
|--------|--------|
| Local health analysis engine | Planned |
| File cleanup on delete | Required |
| Database migration support | Needed for new columns |
| Dark mode support | Implemented (Material 3) |
| Accessibility (TalkBack) | Partial |

---

## 📄 License

This project is for **educational and research purposes only**.  
Not intended for clinical diagnosis or medical use.