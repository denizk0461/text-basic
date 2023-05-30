package com.denizd.textbasic.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores a single entry. For use with a Room database.
 */
@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey val id: Int,
    val text: String,
    val position: Int,
)