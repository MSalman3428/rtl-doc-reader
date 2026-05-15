package com.example.rtldocreader

import java.text.Bidi
import java.text.Normalizer
import com.ibm.icu.text.UnicodeSet
import com.ibm.icu.text.Normalizer2

object TextNormalizer {
    // Comprehensive diacritic patterns for Arabic, Urdu, Pashto
    private val arabicDiacritics = "[\u064B-\u065F\u0670\u06D6-\u06ED\u08D3-\u08FF]".toRegex()
    
    // Arabic variant mappings (normalize presentation forms to base letters)
    private val arabicVariants = mapOf(
        '\u064A' to 'ي',  // Arabic Letter Yeh
        '\u06CC' to 'ي',  // Farsi Yeh
        '\u06D2' to 'ي',  // Yeh Barree
        '\u06C1' to 'ه',  // Heh Goal
        '\u06C2' to 'ه',  // Heh Goal with Hamza
        '\u0647' to 'ه',  // Arabic Letter Heh
        '\u0643' to 'ك',  // Arabic Letter Kaf
        '\u06A9' to 'ك',  // Keheh
        '\u0627' to 'ا',  // Alef
        '\u0622' to 'ا',  // Alef with Madda
        '\u0623' to 'ا',  // Alef with Hamza Above
        '\u0625' to 'ا'   // Alef with Hamza Below
    )
    
    // BiDi isolation markers to remove
    private val bidiMarkers = setOf(
        '\u200F',  // Right-to-Left Mark
        '\u200E',  // Left-to-Right Mark
        '\u202B',  // Right-to-Left Embedding
        '\u202A',  // Left-to-Right Embedding
        '\u202C',  // Pop Directional Formatting
        '\u061C'   // Arabic Letter Mark
    )
    
    private val nfd = Normalizer2.getInstance(null, "nfd", Normalizer2.Mode.DECOMPOSED)
    private val nfkc = Normalizer2.getInstance(null, "nfkc", Normalizer2.Mode.COMPOSE)

    /**
     * Normalize RTL text for search by:
     * 1. Applying NFKC Unicode normalization
     * 2. Stripping diacritics
     * 3. Normalizing variant letters
     * 4. Fixing bidirectional text order
     */
    fun normalizeRTLText(input: String): String {
        if (input.isBlank()) return ""
        
        // Step 1: Apply NFKC normalization
        var normalized = nfkc.normalize(input)
        
        // Step 2: Remove all diacritical marks
        normalized = normalized.replace(arabicDiacritics, "")
        
        // Step 3: Remove BiDi control markers
        normalized = normalized.filter { it !in bidiMarkers }
        
        // Step 4: Normalize variant letters to their base forms
        normalized = normalized.map { char ->
            arabicVariants[char] ?: char
        }.joinToString("")
        
        // Step 5: Fix logical string order for mixed RTL/LTR text
        normalized = reorderBidiText(normalized)
        
        return normalized.trim()
    }

    /**
     * Reorder text using bidirectional algorithm to ensure
     * logical order (typing order) is preserved for search.
     */
    private fun reorderBidiText(input: String): String {
        if (input.isBlank()) return input
        
        val bidi = Bidi(input, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT)
        
        // If text is purely LTR, no reordering needed
        if (!bidi.isMixed && bidi.baseLevel == 0) {
            return input
        }
        
        val reordered = StringBuilder()
        
        // Process each run (contiguous LTR or RTL segment)
        for (i in 0 until bidi.runCount) {
            val start = bidi.getRunStart(i)
            val limit = bidi.getRunLimit(i)
            val level = bidi.getRunLevel(i)
            val segment = input.substring(start, limit)
            
            // RTL runs (odd level) need segment reversal for logical ordering
            if (level % 2 == 1) {
                reordered.append(segment.reversed())
            } else {
                reordered.append(segment)
            }
        }
        
        return reordered.toString()
    }

    /**
     * Create a searchable index entry that matches both diacritized
     * and non-diacritized versions of a word.
     */
    fun createSearchVariants(text: String): List<String> {
        val variants = mutableListOf<String>()
        
        // Add the normalized version
        variants.add(normalizeRTLText(text))
        
        // Add the NFD (decomposed) version for accent-insensitive search
        val decomposed = nfd.normalize(text)
        if (decomposed != text) {
            variants.add(normalizeRTLText(decomposed))
        }
        
        // Add variant letter replacements
        for ((old, new) in arabicVariants) {
            if (text.contains(old)) {
                val variant = text.replace(old, new)
                variants.add(normalizeRTLText(variant))
            }
        }
        
        return variants.distinct()
    }

    /**
     * Detect if text contains RTL scripts
     */
    fun isRTLText(text: String): Boolean {
        val rtlRanges = setOf(
            0x0590 to 0x08FF,   // Hebrew through Arabic
            0xFB1D to 0xFB4F,   // Presentation Forms A
            0xFB50 to 0xFDFF,   // Arabic Presentation Forms
            0xFE70 to 0xFEFF    // Arabic Presentation Forms B
        )
        
        return text.any { char ->
            val code = char.code
            rtlRanges.any { (start, end) -> code in start..end }
        }
    }

    /**
     * Segment text into RTL and LTR portions for proper rendering
     */
    fun segmentBidiText(text: String): List<TextSegment> {
        val segments = mutableListOf<TextSegment>()
        val bidi = Bidi(text, Bidi.DIRECTION_DEFAULT_LEFT_TO_RIGHT)
        
        for (i in 0 until bidi.runCount) {
            val start = bidi.getRunStart(i)
            val limit = bidi.getRunLimit(i)
            val level = bidi.getRunLevel(i)
            val segment = text.substring(start, limit)
            
            segments.add(
                TextSegment(
                    text = segment,
                    isRTL = level % 2 == 1,
                    startIndex = start,
                    endIndex = limit
                )
            )
        }
        
        return segments
    }
    
    data class TextSegment(
        val text: String,
        val isRTL: Boolean,
        val startIndex: Int,
        val endIndex: Int
    )
}
