package com.example.rtldocreader

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.room.Room
import java.io.File

class DocumentIndexWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val paths = inputData.getStringArray("document_paths") ?: return Result.failure()
        val db = Room.databaseBuilder(applicationContext, DocumentDatabase::class.java, "document_index.db")
            .fallbackToDestructiveMigration()
            .build()

        val entries = mutableListOf<DocumentIndex>()
        val extractor = OfficeTextExtractor()
        val pdfExtractor = PdfTextExtractor()

        for (path in paths) {
            val file = File(path)
            val rawText = when (file.extension.lowercase()) {
                "pdf" -> pdfExtractor.extractText(file, applicationContext)
                "docx", "pptx" -> extractor.extractText(file, applicationContext)
                else -> continue
            }
            entries.add(
                DocumentIndex(
                    file_path = file.absolutePath,
                    file_name = file.name,
                    page_number = 1,
                    normalized_content = rawText
                )
            )
        }

        db.documentDao().insertAll(entries)
        return Result.success()
    }
}
