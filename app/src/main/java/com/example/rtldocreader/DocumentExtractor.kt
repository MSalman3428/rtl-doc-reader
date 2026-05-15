package com.example.rtldocreader

import android.net.Uri
import android.content.Context
import java.io.File
import java.io.FileInputStream

sealed class DocumentExtractor {
    abstract suspend fun extractText(file: File, context: Context): String
}

class PdfTextExtractor : DocumentExtractor() {
    override suspend fun extractText(file: File, context: Context): String {
        val document = com.tom_roush.pdfbox.pdmodel.PDDocument.load(file)
        val stripper = com.tom_roush.pdfbox.text.PDFTextStripper()
        return try {
            val raw = stripper.getText(document)
            TextNormalizer.normalizeRTLText(raw)
        } finally {
            document.close()
        }
    }
}

class OfficeTextExtractor : DocumentExtractor() {
    override suspend fun extractText(file: File, context: Context): String {
        return when (file.extension.lowercase()) {
            "docx" -> extractDocx(file)
            "pptx" -> extractPptx(file)
            else -> ""
        }
    }

    private fun extractDocx(file: File): String {
        FileInputStream(file).use { stream ->
            val doc = org.apache.poi.xwpf.usermodel.XWPFDocument(stream)
            val builder = StringBuilder()
            for (para in doc.paragraphs) {
                builder.append(para.text).append('\n')
            }
            return TextNormalizer.normalizeRTLText(builder.toString())
        }
    }

    private fun extractPptx(file: File): String {
        FileInputStream(file).use { stream ->
            val slideshow = org.apache.poi.xslf.usermodel.XMLSlideShow(stream)
            val builder = StringBuilder()
            for (slide in slideshow.slides) {
                for (shape in slide.shapes) {
                    if (shape is org.apache.poi.xslf.usermodel.XSLFTextShape) {
                        builder.append(shape.text).append('\n')
                    }
                }
            }
            return TextNormalizer.normalizeRTLText(builder.toString())
        }
    }
}
