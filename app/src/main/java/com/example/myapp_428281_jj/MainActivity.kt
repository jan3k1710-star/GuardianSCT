package com.example.myapp_428281_jj

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(applicationContext)
            MapRepository.initialize(db.mapPointDao())
            MapRepository.initializeDatabaseIfEmpty()
            MapRepository.loadPoints()
        }

        enableEdgeToEdge()
        setContent {
            MainScreen()
        }
    }
}