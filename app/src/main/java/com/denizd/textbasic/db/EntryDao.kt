package com.denizd.textbasic.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.denizd.textbasic.model.Entry

@Dao
interface EntryDao {

    @Query("SELECT * FROM entries ORDER BY position")
    fun getAllEntries(): LiveData<List<Entry>>

    @Insert
    fun insertEntries(newEntries: List<Entry>)

    @Query("DELETE FROM entries")
    fun deleteAllEntries()
}