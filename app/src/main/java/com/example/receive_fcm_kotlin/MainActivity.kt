package com.example.receive_fcm_kotlin

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.receive_fcm_kotlin.ui.theme.Learn_fcmTheme
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private var showRationale by mutableStateOf(false)

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { _ -> }

    private fun askNotificationPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
            showRationale = true
        } else {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val topicName = "general"
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("FCM", "subscribed to $topicName")
                } else {
                    Log.w("FCM", "subscription failed", task.exception)
                }
            }
        askNotificationPermission()
        enableEdgeToEdge()
        setContent {
            Learn_fcmTheme {
                if (showRationale) {
                    AlertDialog(
                        onDismissRequest = { showRationale = false },
                        title = { Text("Allow notifications?") },
                        text = { Text("We use notifications to show incoming messages. Allow to stay updated.") },
                        confirmButton = {
                            TextButton(onClick = {
                                showRationale = false
                                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }) { Text("Allow") }
                        },
                        dismissButton = {
                            TextButton(onClick = { showRationale = false }) { Text("Not now") }
                        })
                }
                Scaffold() { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Greeting(
                            name = "Android",
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Learn_fcmTheme {
        Greeting("Android")
    }
}