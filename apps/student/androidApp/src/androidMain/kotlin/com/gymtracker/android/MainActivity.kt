package com.gymtracker.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.gymtracker.AppDependencies
import com.gymtracker.data.db.DatabaseDriverFactory
import com.gymtracker.ui.App
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            AppDependencies.init(DatabaseDriverFactory(applicationContext))
            setContent { App() }
        }
    }
}
