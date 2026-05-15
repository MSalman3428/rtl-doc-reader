package com.example.rtldocreader

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DocumentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<DocumentIndex>)

    @Query("SELECT file_path, file_name, page_number, normalized_content FROM document_index WHERE document_index MATCH :query")
    suspend fun search(query: String): List<DocumentIndex>
    
    @Query("SELECT file_path, file_name, page_number, normalized_content FROM document_index ORDER BY file_name ASC")
    suspend fun getAllDocuments(): List<DocumentIndex>
    
    @Query("SELECT DISTINCT file_name FROM document_index ORDER BY file_name ASC")
    suspend fun getAllFileNames(): List<String>
    
    @Query("DELETE FROM document_index WHERE file_path = :filePath")
    suspend fun deleteByFilePath(filePath: String)
    
    @Query("SELECT COUNT(*) FROM document_index")
    suspend fun getDocumentCount(): Int
}
