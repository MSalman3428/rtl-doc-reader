package com.example.rtldocreader

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Room.databaseBuilder(application, DocumentDatabase::class.java, "document_index.db")
        .fallbackToDestructiveMigration()
        .build()
    
    private val searchEngine = RTLSearchEngine()

    private val _searchResults = MutableLiveData<List<SearchMatch>>()
    val searchResults: LiveData<List<SearchMatch>> = _searchResults

    fun search(rawQuery: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (rawQuery.isBlank()) {
                _searchResults.postValue(emptyList())
                return@launch
            }
            
            // Get all documents from database
            val allDocuments = db.documentDao().getAllDocuments()
            val allMatches = mutableListOf<SearchMatch>()
            
            // Search in each document
            for (doc in allDocuments) {
                val matches = searchEngine.search(rawQuery, doc.normalized_content)
                // Enhance matches with document info
                allMatches.addAll(
                    matches.map { match ->
                        match.copy(
                            text = "${doc.file_name} (Page ${doc.page_number}): ${match.text}"
                        )
                    }
                )
            }
            
            _searchResults.postValue(allMatches)
        }
    }

    fun fuzzySearch(rawQuery: String) {
        viewModelScope.launch(Dispatchers.Default) {
            if (rawQuery.isBlank()) {
                _searchResults.postValue(emptyList())
                return@launch
            }
            
            val allDocuments = db.documentDao().getAllDocuments()
            val allMatches = mutableListOf<SearchMatch>()
            
            for (doc in allDocuments) {
                val matches = searchEngine.fuzzySearch(rawQuery, doc.normalized_content)
                allMatches.addAll(
                    matches.map { match ->
                        match.copy(
                            text = "${doc.file_name} (Page ${doc.page_number}): ${match.text}"
                        )
                    }
                )
            }
            
            _searchResults.postValue(allMatches)
        }
    }
}
