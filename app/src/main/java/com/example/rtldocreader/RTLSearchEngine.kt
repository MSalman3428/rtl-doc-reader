package com.example.rtldocreader

/**
 * Advanced RTL search engine that handles:
 * - Diacritic-insensitive search
 * - Ligature normalization
 * - Bidirectional text matching
 * - Fuzzy matching for variant letters
 */
class RTLSearchEngine {
    
    /**
     * Search for a query in text with RTL support.
     * Returns all match positions with context.
     */
    fun search(query: String, text: String): List<SearchMatch> {
        if (query.isBlank() || text.isBlank()) return emptyList()
        
        val normalizedQuery = TextNormalizer.normalizeRTLText(query)
        val normalizedText = TextNormalizer.normalizeRTLText(text)
        
        val matches = mutableListOf<SearchMatch>()
        var searchIndex = 0
        
        while (searchIndex <= normalizedText.length - normalizedQuery.length) {
            val potentialMatch = normalizedText.substring(
                searchIndex,
                (searchIndex + normalizedQuery.length).coerceAtMost(normalizedText.length)
            )
            
            if (potentialMatch == normalizedQuery) {
                // Find the original character position in the unnormalized text
                val originalMatch = findOriginalPosition(text, normalizedText, searchIndex, normalizedQuery.length)
                if (originalMatch != null) {
                    matches.add(originalMatch)
                    searchIndex += normalizedQuery.length
                    continue
                }
            }
            
            searchIndex++
        }
        
        return matches
    }

    /**
     * Fuzzy search allowing for variant letters (e.g., ي vs ی)
     */
    fun fuzzySearch(query: String, text: String, threshold: Float = 0.85f): List<SearchMatch> {
        if (query.isBlank() || text.isBlank()) return emptyList()
        
        val queryVariants = TextNormalizer.createSearchVariants(query)
        val allMatches = mutableListOf<SearchMatch>()
        
        // Try exact match with each variant
        for (variant in queryVariants) {
            allMatches.addAll(search(variant, text))
        }
        
        // Remove duplicates by position
        return allMatches.distinctBy { it.startPosition }
    }

    /**
     * Search using word boundaries (useful for phrase searching)
     */
    fun searchByWords(query: String, text: String): List<SearchMatch> {
        if (query.isBlank() || text.isBlank()) return emptyList()
        
        val words = query.split("\\s+".toRegex()).filter { it.isNotBlank() }
        if (words.isEmpty()) return emptyList()
        
        // Find all matches for the first word
        val firstMatches = search(words[0], text)
        if (words.size == 1) return firstMatches
        
        // For multi-word queries, find consecutive matches
        val validMatches = mutableListOf<SearchMatch>()
        val normalizedText = TextNormalizer.normalizeRTLText(text)
        
        for (firstMatch in firstMatches) {
            var allWordsFound = true
            var lastEndPos = firstMatch.endPosition
            
            // Check if remaining words follow immediately
            for (i in 1 until words.size) {
                val wordMatches = search(words[i], text.substring(lastEndPos.coerceAtMost(text.length)))
                if (wordMatches.isEmpty()) {
                    allWordsFound = false
                    break
                }
                lastEndPos = wordMatches[0].endPosition
            }
            
            if (allWordsFound) {
                validMatches.add(
                    SearchMatch(
                        text = query,
                        startPosition = firstMatch.startPosition,
                        endPosition = lastEndPos,
                        lineNumber = firstMatch.lineNumber,
                        context = firstMatch.context
                    )
                )
            }
        }
        
        return validMatches
    }

    /**
     * Case-insensitive search for LTR text mixed with RTL
     */
    fun mixedDirectionalSearch(query: String, text: String): List<SearchMatch> {
        val ltrMatches = search(query.lowercase(), text.lowercase())
        return ltrMatches.map { match ->
            // Preserve original casing in context
            match.copy(context = extractContext(text, match.startPosition, match.endPosition))
        }
    }

    /**
     * Extract surrounding context for a match
     */
    private fun extractContext(text: String, start: Int, end: Int, contextSize: Int = 50): String {
        val contextStart = (start - contextSize).coerceAtLeast(0)
        val contextEnd = (end + contextSize).coerceAtMost(text.length)
        return text.substring(contextStart, contextEnd)
    }

    /**
     * Map normalized text position back to original text position
     * (Important because normalization can change string length)
     */
    private fun findOriginalPosition(
        original: String,
        normalized: String,
        normalizedPos: Int,
        matchLength: Int
    ): SearchMatch? {
        // For exact mapping, we need to track character transformations
        var normalizedIndex = 0
        var originalIndex = 0
        var matchStart = -1
        
        while (originalIndex < original.length && normalizedIndex < normalized.length) {
            val originalChar = original[originalIndex]
            val normalizedChar = normalized[normalizedIndex]
            
            // Track where the match starts
            if (normalizedIndex == normalizedPos && matchStart == -1) {
                matchStart = originalIndex
            }
            
            // Move indices based on character mapping
            if (normalizedChar == normalizedChar) {
                normalizedIndex++
                originalIndex++
            } else {
                originalIndex++
            }
        }
        
        return if (matchStart >= 0) {
            SearchMatch(
                text = original.substring(matchStart, (matchStart + matchLength).coerceAtMost(original.length)),
                startPosition = matchStart,
                endPosition = (matchStart + matchLength).coerceAtMost(original.length),
                lineNumber = calculateLineNumber(original, matchStart),
                context = extractContext(original, matchStart, (matchStart + matchLength).coerceAtMost(original.length))
            )
        } else {
            null
        }
    }

    private fun calculateLineNumber(text: String, position: Int): Int {
        return text.substring(0, position).count { it == '\n' } + 1
    }
}

data class SearchMatch(
    val text: String,
    val startPosition: Int,
    val endPosition: Int,
    val lineNumber: Int = 1,
    val context: String = text
)
