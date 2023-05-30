package com.denizd.textbasic.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.denizd.textbasic.model.Entry

/**
 * Room database that holds all objects that need to be saved to persistent storage.
 */
@Database(
    entities = [
        Entry::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * The database access object. Database transactions go through this.
     */
    abstract fun dao(): EntryDao

    companion object {
        // The singular instance of this database
        private var instance: AppDatabase? = null

        /**
         * Retrieve the instance of the database.
         *
         * @param context   used to resolve the object
         * @return          the database
         */
        fun getInstance(context: Context): AppDatabase {
            /*
             * Check if an object has already been instantiated. Only create a new instance if none
             * exists.
             */
            if (instance == null) {
                synchronized(AppDatabase::class) {
                    instance = Room
                        .databaseBuilder(
                            context.applicationContext,
                            AppDatabase::class.java,
                            "text_basic_db",
                        )
//                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            // Instance can never be null at this point
            return instance!!
        }
    }
}