package com.example.rtldocreader

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DocumentIndex::class], version = 1, exportSchema = false)
abstract class DocumentDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}
