package com.measuremate.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.measuremate.data.models.MeasurementEntity
import com.measuremate.data.models.NoteEntity
import com.measuremate.data.models.PhotoEntity
import com.measuremate.data.models.ProjectEntity
import com.measuremate.data.models.SettingEntity

@Database(
    entities = [
        ProjectEntity::class,
        MeasurementEntity::class,
        NoteEntity::class,
        PhotoEntity::class,
        SettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MeasureMateDatabase : RoomDatabase() {
    abstract fun dao(): MeasureMateDao

    companion object {
        @Volatile private var instance: MeasureMateDatabase? = null

        fun get(context: Context): MeasureMateDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    MeasureMateDatabase::class.java,
                    "measuremate.db"
                ).build().also { instance = it }
            }
    }
}
