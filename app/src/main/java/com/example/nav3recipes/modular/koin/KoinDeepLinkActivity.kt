package com.example.nav3recipes.modular.koin

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.lifecycle.compose.dropUnlessResumed
import com.example.nav3recipes.ui.setEdgeToEdgeConfig

class KoinDeepLinkActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setEdgeToEdgeConfig()
        setContent {
            Scaffold { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(
                        onClick = dropUnlessResumed {
                            val intent = Intent(
                                this@KoinDeepLinkActivity,
                                KoinModularActivity::class.java,
                            )
                            startActivity(intent)
                        }
                    ) {
                        Text("Conversation List")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    var conversationId by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = conversationId,
                        onValueChange = { conversationId = it },
                        label = { Text("Conversation Number") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        enabled = conversationId.toIntOrNull() != null,
                        onClick = dropUnlessResumed {
                            val intent = Intent(
                                this@KoinDeepLinkActivity,
                                KoinModularActivity::class.java,
                            ).apply {
                                data = "$CONVERSATION_URI/$conversationId".toUri()
                            }
                            startActivity(intent)
                        }
                    ) {
                        Text("Deep Link to Conversation")
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = dropUnlessResumed {
                            val intent = Intent(
                                this@KoinDeepLinkActivity,
                                KoinModularActivity::class.java,
                            ).apply {
                                data = PROFILE_URI.toUri()
                            }
                            startActivity(intent)
                        }
                    ) {
                        Text("Deep Link to Profile")
                    }
                }
            }
        }
    }
}
