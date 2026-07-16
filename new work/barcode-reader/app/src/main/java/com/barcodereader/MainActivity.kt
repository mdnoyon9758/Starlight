package com.barcodereader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.barcodereader.data.HistoryStorage
import com.barcodereader.ui.navigation.AppNavigation
import com.barcodereader.ui.theme.BarcodeReaderTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val historyStorage = HistoryStorage(this)
        
        setContent {
            BarcodeReaderTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(historyStorage = historyStorage)
                }
            }
        }
    }
}
