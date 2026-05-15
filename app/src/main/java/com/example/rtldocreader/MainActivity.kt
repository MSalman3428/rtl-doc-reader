package com.example.rtldocreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rtldocreader.ui.theme.RTLDOCReaderTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RTLDOCReaderTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    MainScreen(viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val queryState = remember { mutableStateOf("") }
    val results by viewModel.searchResults.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("RTL Doc Reader") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(16.dp)
            .fillMaxSize()) {
            OutlinedTextField(
                value = queryState.value,
                onValueChange = {
                    queryState.value = it
                    viewModel.search(it)
                },
                label = { Text("Search documents (Arabic/Urdu/Pashto/English)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            if (results.isEmpty() && queryState.value.isNotBlank()) {
                Text(
                    text = "No matches found",
                    modifier = Modifier.padding(16.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
                )
            } else if (results.isNotEmpty()) {
                Text(
                    text = "Found ${results.size} match(es)",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = androidx.compose.material3.MaterialTheme.typography.labelMedium
                )
            }

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(results) { match ->
                    SearchResultCard(match)
                }
            }
        }
    }
}

@Composable
fun SearchResultCard(match: SearchMatch) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = match.text,
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Text(
                text = match.context.take(200),
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 3
            )
            Text(
                text = "Line ${match.lineNumber} (Pos ${match.startPosition})",
                style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 8.dp),
                color = androidx.compose.material3.MaterialTheme.colorScheme.primary
            )
        }
    }
}
