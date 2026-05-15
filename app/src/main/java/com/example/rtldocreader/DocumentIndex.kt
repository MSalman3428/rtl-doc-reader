package com.example.rtldocreader

import androidx.room.Entity
import androidx.room.Fts5

@Fts5
@Entity(tableName = "document_index")
data class DocumentIndex(
    val file_path: String,
    val file_name: String,
    val page_number: Int,
    val normalized_content: String
)
