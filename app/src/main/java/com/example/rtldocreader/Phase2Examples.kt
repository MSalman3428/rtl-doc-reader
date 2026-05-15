package com.example.rtldocreader

/**
 * Phase 2 Implementation Examples & Test Cases
 * 
 * This file demonstrates the Arabic Script Fix with Unicode normalization
 * and ICU library logic for searching RTL text.
 */
object Phase2Examples {
    
    // ============================================
    // Example 1: Urdu Text with Diacritics
    // ============================================
    fun example1_UrduWithDiacritics() {
        val query = "کتاب"  // "Book" in Urdu
        val text = "یہ ایک بہترین کتاب ہے"  // "This is a great book"
        
        val engine = RTLSearchEngine()
        val results = engine.search(query, text)
        
        println("=== Example 1: Urdu Text ===")
        println("Query: $query")
        println("Text: $text")
        println("Matches found: ${results.size}")
        results.forEach { match ->
            println("  - Position ${match.startPosition}-${match.endPosition}: '${match.text}'")
            println("    Context: ${match.context}")
        }
    }
    
    // ============================================
    // Example 2: Pashto Text with Variant Letters
    // ============================================
    fun example2_PashtoVariants() {
        val queries = listOf("پښتو", "پشتو")  // Different ways to write "Pashto"
        val text = "پښتو زبان بہت ڈھکی ہوئی ہے"
        
        val engine = RTLSearchEngine()
        
        println("\n=== Example 2: Pashto Variants ===")
        println("Text: $text")
        
        for (query in queries) {
            val results = engine.search(query, text)
            println("Query: $query -> Found ${results.size} match(es)")
        }
    }
    
    // ============================================
    // Example 3: Arabic Text with Tashkeel Marks
    // ============================================
    fun example3_ArabicWithTashkeel() {
        // Arabic text WITHOUT diacritics
        val queryPlain = "كراتشى"  // Karachi (plain)
        
        // Arabic text WITH diacritics (Tashkeel)
        val textWithDiacritics = "كَرَاتْشَىّ مَدِينَة عَظِيمَة"  // Karachi (with diacritics)
        
        val engine = RTLSearchEngine()
        val results = engine.search(queryPlain, textWithDiacritics)
        
        println("\n=== Example 3: Arabic with Tashkeel ===")
        println("Query (plain): $queryPlain")
        println("Text (with diacritics): $textWithDiacritics")
        println("Matches found: ${results.size}")
        results.forEach { match ->
            println("  - Line ${match.lineNumber}, Position ${match.startPosition}: '${match.text}'")
        }
    }
    
    // ============================================
    // Example 4: Mixed LTR/RTL Document
    // ============================================
    fun example4_MixedDirectionalText() {
        val query = "عربی"  // Arabic in Urdu context
        val text = "The word عربی means Arabic. It is a complex language."
        
        val engine = RTLSearchEngine()
        val results = engine.search(query, text)
        
        println("\n=== Example 4: Mixed Directional Text ===")
        println("Query: $query")
        println("Text: $text")
        println("Matches: ${results.size}")
        results.forEach { match ->
            println("  - Position ${match.startPosition}: '${match.text}'")
            println("    Context: ${match.context}")
        }
    }
    
    // ============================================
    // Example 5: Unicode Normalization Demo
    // ============================================
    fun example5_UnicodeNormalization() {
        // Different Unicode representations of the same letter
        val variants = listOf(
            "ي",    // U+064A (Arabic Yeh)
            "ی",    // U+06CC (Farsi Yeh)
            "ے",    // U+06D2 (Yeh Barree)
            "ۍ"     // U+06CD (Yeh Barree with Small V)
        )
        
        println("\n=== Example 5: Unicode Variant Normalization ===")
        println("All these characters represent 'Yeh':")
        
        for (variant in variants) {
            val normalized = TextNormalizer.normalizeRTLText(variant)
            println("  - Original: '$variant' (U+${variant[0].code.toString(16).padStart(4, '0').uppercase()})")
            println("    Normalized: '$normalized'")
        }
    }
    
    // ============================================
    // Example 6: BiDi Text Segmentation
    // ============================================
    fun example6_BidiSegmentation() {
        val text = "Hello مرحبا World شاہ Test"
        
        val segments = TextNormalizer.segmentBidiText(text)
        
        println("\n=== Example 6: BiDi Text Segmentation ===")
        println("Text: $text")
        println("Segments:")
        
        for (segment in segments) {
            val direction = if (segment.isRTL) "RTL" else "LTR"
            println("  - [$direction] '${segment.text}' (pos ${segment.startIndex}-${segment.endIndex})")
        }
    }
    
    // ============================================
    // Example 7: Fuzzy Search with Variants
    // ============================================
    fun example7_FuzzySearch() {
        val query = "کراچی"  // Query with variant letters
        val text = "کراچی ہے پاکستان کا سب سے بڑا شہر ہے"
        
        val engine = RTLSearchEngine()
        val results = engine.fuzzySearch(query, text)
        
        println("\n=== Example 7: Fuzzy Search ===")
        println("Query: $query")
        println("Text: $text")
        println("Fuzzy matches found: ${results.size}")
    }
    
    // ============================================
    // Example 8: Phrase Search
    // ============================================
    fun example8_PhraseSearch() {
        val phraseQuery = "اللہ آپ کو"  // "God/Allah bless you" in Urdu
        val text = "اللہ آپ کو برکت دے۔ یہ ایک دعا ہے۔"
        
        val engine = RTLSearchEngine()
        val results = engine.searchByWords(phraseQuery, text)
        
        println("\n=== Example 8: Phrase Search ===")
        println("Query: $phraseQuery")
        println("Text: $text")
        println("Phrase matches: ${results.size}")
        results.forEach { match ->
            println("  - Line ${match.lineNumber}: '${match.text}'")
        }
    }
    
    // ============================================
    // Example 9: RTL Detection
    // ============================================
    fun example9_RTLDetection() {
        val testStrings = listOf(
            "Hello World",
            "مرحبا بالعالم",
            "اردو ہے",
            "پشتو",
            "Mix اردو and English"
        )
        
        println("\n=== Example 9: RTL Detection ===")
        for (str in testStrings) {
            val isRTL = TextNormalizer.isRTLText(str)
            val direction = if (isRTL) "RTL" else "LTR"
            println("'$str' -> $direction")
        }
    }
    
    // ============================================
    // Example 10: Normalization Transformation Demo
    // ============================================
    fun example10_NormalizationTransform() {
        val examples = listOf(
            "کتاب",      // Simple Urdu word
            "کِتَاب",    // With Tashkeel (diacritics)
            "القرآن",   // Arabic with Al prefix
            "الْقُرْآن"  // Arabic with full diacritics
        )
        
        println("\n=== Example 10: Normalization Transformation ===")
        for (example in examples) {
            val normalized = TextNormalizer.normalizeRTLText(example)
            println("Original:   '$example'")
            println("Normalized: '$normalized'")
            println()
        }
    }
    
    // ============================================
    // Main execution
    // ============================================
    @JvmStatic
    fun main(args: Array<String>) {
        println("╔════════════════════════════════════════════════════════════════╗")
        println("║ Phase 2: Arabic Script Fix - RTL Text Normalization & Search  ║")
        println("║ Implementation Examples for Kotlin/Android                    ║")
        println("╚════════════════════════════════════════════════════════════════╝")
        
        example1_UrduWithDiacritics()
        example2_PashtoVariants()
        example3_ArabicWithTashkeel()
        example4_MixedDirectionalText()
        example5_UnicodeNormalization()
        example6_BidiSegmentation()
        example7_FuzzySearch()
        example8_PhraseSearch()
        example9_RTLDetection()
        example10_NormalizationTransform()
        
        println("\n╔════════════════════════════════════════════════════════════════╗")
        println("║ Phase 2 Examples Completed                                    ║")
        println("╚════════════════════════════════════════════════════════════════╝")
    }
}
