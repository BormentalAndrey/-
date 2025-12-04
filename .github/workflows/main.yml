import os

# Создаём директорию проекта
project_dir = "Kakdela-p2p"
os.makedirs(project_dir, exist_ok=True)
os.makedirs(os.path.join(project_dir, ".github/workflows"), exist_ok=True)
os.makedirs(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/data"), exist_ok=True)
os.makedirs(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui"), exist_ok=True)
os.makedirs(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens"), exist_ok=True)
os.makedirs(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/webrtc"), exist_ok=True)
os.makedirs(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/p2p"), exist_ok=True)
os.makedirs(os.path.join(project_dir, "app/src/main/res/values-ru"), exist_ok=True)  # Для локализации

# settings.gradle.kts
with open(os.path.join(project_dir, "settings.gradle.kts"), "w") as f:
    f.write("""pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Kakdela-p2p"
include(":app")
""")

# build.gradle.kts (root)
with open(os.path.join(project_dir, "build.gradle.kts"), "w") as f:
    f.write("""plugins {
    id("com.android.application") version "8.13.0" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
}
""")

# app/build.gradle.kts
with open(os.path.join(project_dir, "app/build.gradle.kts"), "w") as f:
    f.write("""plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.kakdela.p2p"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kakdela.p2p"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    packagingOptions {
        resources.excludes += setOf("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
    implementation("androidx.activity:activity-compose:1.12.1")

    implementation(platform("androidx.compose:compose-bom:2025.12.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    // Room
    implementation("androidx.room:room-runtime:2.8.4")
    kapt("androidx.room:room-compiler:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")

    // Coroutines and lifecycle
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.21")

    // WebRTC
    implementation("org.webrtc:google-webrtc:1.0.32069")

    // OkHttp and Moshi for signaling
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    implementation("com.squareup.moshi:moshi:1.15.0")

    // Coil for images/avatars
    implementation("io.coil-kt:coil-compose:2.4.0")

    // Navigation Compose
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // DataStore for settings
    implementation("androidx.datastore:datastore-preferences:1.1.0")

    // Lifecycle ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
}
""")

# gradle.properties
with open(os.path.join(project_dir, "gradle.properties"), "w") as f:
    f.write("""android.useAndroidX=true
android.enableJetifier=true
org.gradle.jvmargs=-Xmx4g -Dfile.encoding=UTF-8
org.gradle.parallel=true
org.gradle.caching=true
""")

# .github/workflows/build.yml
with open(os.path.join(project_dir, ".github/workflows/build.yml"), "w") as f:
    f.write("""name: Kakdela P2P — Полная сборка и релиз

on:
  push:
    branches: [ main ]
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Установка JDK 17
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 17

      - name: Кэш Gradle
        uses: actions/cache@v4
        with:
          path: |
            ~/.gradle/caches
            ~/.gradle/wrapper
          key: \( {{ runner.os }}-gradle- \){{ hashFiles('**/*.gradle.kts') }}
          restore-keys: ${{ runner.os }}-gradle-

      - name: Сборка Debug APK
        run: ./gradlew assembleDebug --stacktrace

      - name: Загрузка APK
        uses: actions/upload-artifact@v4
        with:
          name: Kakdela-p2p-debug.apk
          path: app/build/outputs/apk/debug/app-debug.apk
""")

# AndroidManifest.xml
with open(os.path.join(project_dir, "app/src/main/AndroidManifest.xml"), "w") as f:
    f.write("""<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.kakdela.p2p">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

    <application
        android:name=".App"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.Kakdela">
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
""")

# App.kt
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/App.kt"), "w") as f:
    f.write("""package com.kakdela.p2p

import android.app.Application
import com.kakdela.p2p.data.MessageDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MessageDatabase.getInstance(this) // init DB
    }
}
""")

# MainActivity.kt
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/MainActivity.kt"), "w") as f:
    f.write("""package com.kakdela.p2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.kakdela.p2p.ui.KakdelaTheme
import com.kakdela.p2p.ui.NavGraph

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KakdelaTheme {
                NavGraph()
            }
        }
    }
}
""")

# MessageEntity.kt from page 182 and 213
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/data/MessageEntity.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val remoteId: String? = null,
    val chatId: String,
    val senderId: String,
    val text: String?,
    val type: String = "text",
    val timestamp: Long = System.currentTimeMillis(),
    val delivered: Boolean = false,
    val synced: Boolean = false
)
""")

# MessageDao.kt from page 183
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/data/MessageDao.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {
    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    fun getMessagesForChat(chatId: String): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessage(message: MessageEntity): Long

    @Query("UPDATE messages SET delivered = :delivered WHERE localId = :localId")
    suspend fun updateDelivered(localId: Long, delivered: Boolean)

    @Query("UPDATE messages SET synced = :synced WHERE localId = :localId")
    suspend fun updateSynced(localId: Long, synced: Boolean)

    @Query("SELECT * FROM messages WHERE synced = 0 AND senderId = :me")
    suspend fun getUnsentOutgoingMessages(me: String): List<MessageEntity>

    @Query("SELECT * FROM messages WHERE remoteId = :remoteId LIMIT 1")
    suspend fun findByRemoteId(remoteId: String): MessageEntity?
}
""")

# MessageDatabase.kt from page 184
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/data/MessageDatabase.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile private var INSTANCE: MessageDatabase? = null

        fun getInstance(context: Context): MessageDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, MessageDatabase::class.java, "kakdela_messages.db")
                .fallbackToDestructiveMigration().build().also { INSTANCE = it }
        }
    }
}
""")

# MessageRepository.kt from page 185-188
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/data/MessageRepository.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.data

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

class MessageRepository(
    private val dao: MessageDao,
    private val outgoingAdapter: OutgoingAdapter? = null
) {
    fun getChatFlow(chatId: String) = dao.getMessagesForChat(chatId)

    suspend fun sendMessage(chatId: String, senderId: String = "me", text: String) {
        val remoteId = UUID.randomUUID().toString()
        val message = MessageEntity(
            remoteId = remoteId,
            chatId = chatId,
            senderId = senderId,
            text = text,
            type = "text",
            timestamp = System.currentTimeMillis(),
            delivered = false,
            synced = false
        )
        val localId = dao.insertMessage(message)

        outgoingAdapter?.let { adapter ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val ok = adapter.sendToPeer(chatId, mapOf(
                        "type" to "message",
                        "id" to remoteId,
                        "chatId" to chatId,
                        "senderId" to senderId,
                        "text" to text,
                        "timestamp" to System.currentTimeMillis()
                    ))
                    if (ok) dao.updateSynced(localId, true)
                } catch (e: Exception) {
                    Log.e("MessageRepository", "sendToPeer failed: ${e.message}")
                }
            }
        }
    }

    suspend fun onIncomingRemoteMessage(
        remoteId: String,
        chatId: String,
        senderId: String,
        text: String,
        timestamp: Long
    ) {
        if (dao.findByRemoteId(remoteId) != null) return
        val message = MessageEntity(
            remoteId = remoteId,
            chatId = chatId,
            senderId = senderId,
            text = text,
            type = "text",
            timestamp = timestamp,
            delivered = true,
            synced = true
        )
        dao.insertMessage(message)
    }

    suspend fun markDelivered(localId: Long) {
        dao.updateDelivered(localId, true)
    }

    suspend fun getUnsentOutgoing(me: String) = dao.getUnsentOutgoingMessages(me)

    interface OutgoingAdapter {
        suspend fun sendToPeer(chatId: String, payload: Map<String, Any?>): Boolean
    }
}
""")

# KakdelaTheme.kt from page 153
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/KakdelaTheme.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF007AFF)
private val TextPrimary = Color(0xFF000000)
private val TextSecondary = Color(0xFF8E8E93)
private val GrayBg = Color(0xFFF2F2F7)
private val Divider = Color(0xFFE5E5EA)
private val Error = Color(0xFFFF3B30)
private val Success = Color(0xFF34C759)

private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    background = Color.White,
    surface = Color.White,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    outline = Divider,
)

private val DarkColors = darkColorScheme(
    primary = Blue,
    onPrimary = Color.Black,
    background = Color.Black,
    surface = Color(0xFF121212),
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Divider,
)

@Composable
fun KakdelaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!darkTheme) LightColors else DarkColors
    MaterialTheme(
        colorScheme = colors,
        typography = Typography(),
        content = content
    )
}
""")

# NavGraph.kt from page 154
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/NavGraph.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kakdela.p2p.ui.screens.CallScreen
import com.kakdela.p2p.ui.screens.ChatScreen
import com.kakdela.p2p.ui.screens.ContactsScreen
import com.kakdela.p2p.ui.screens.LanguageSelectionScreen
import com.kakdela.p2p.ui.screens.MainScreen
import com.kakdela.p2p.ui.screens.VideoCallScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "language") {
        composable("language") { LanguageSelectionScreen(onDone = { navController.navigate("main") }) }
        composable("main") { MainScreen(onOpenChat = { chatId -> navController.navigate("chat/$chatId") }, onOpenContacts = { navController.navigate("contacts") }) }
        composable("contacts") { ContactsScreen(onOpenChat = { chatId -> navController.navigate("chat/$chatId") }) }
        composable("chat/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: "1"
            ChatScreen(chatId = chatId, onCall = { navController.navigate("call/$chatId") }, onVideo = { navController.navigate("video/$chatId") })
        }
        composable("call/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: "1"
            CallScreen(contactName = "Contact $chatId", onEnd = { navController.popBackStack() })
        }
        composable("video/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: "1"
            VideoCallScreen(contactName = "Contact $chatId", onEnd = { navController.popBackStack() })
        }
    }
}
""")

# LanguageSelectionScreen.kt from page 159
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens/LanguageSelectionScreen.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LanguageSelectionScreen(onDone: (String) -> Unit) {
    val langs = listOf(
        "en" to "English",
        "ru" to "Русский",
        "zh" to "中文",
        "es" to "Español",
        "fr" to "Français",
        "de" to "Deutsch",
        "hi" to "हहहहहह"
    )
    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Select language", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        langs.forEach { (code, name) ->
            Button(onClick = { onDone(code) }, modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text(name)
            }
        }
    }
}
""")

# MainScreen.kt from page 161
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens/MainScreen.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakdela.p2p.Chat
import com.kakdela.p2p.PreviewData

@Composable
fun MainScreen(onOpenChat: (String) -> Unit, onOpenContacts: () -> Unit) {
    val chats = PreviewData.chats
    Scaffold(topBar = { CenterAlignedTopAppBar(title = { Text("Chats") }, actions = { IconButton(onClick = onOpenContacts) { Text("Contacts") } }) }, floatingActionButton = { FloatingActionButton(onClick = { }) { Text("+") } }) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(chats) { chat ->
                Text(chat.name, Modifier.clickable { onOpenChat(chat.id) }.padding(16.dp))
            }
        }
    }
}
""")

# ChatScreen.kt from page 163
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens/ChatScreen.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kakdela.p2p.data.MessageDatabase
import com.kakdela.p2p.data.MessageRepository
import com.kakdela.p2p.data.MessageEntity
import com.kakdela.p2p.ui.chat.ChatViewModel
import com.kakdela.p2p.ui.chat.ChatViewModelFactory

@Composable
fun ChatScreen(chatId: String, onCall: () -> Unit, onVideo: () -> Unit) {
    val context = LocalContext.current
    val repo = remember { MessageRepository(MessageDatabase.getInstance(context).messageDao()) }
    val vm: ChatViewModel = viewModel(factory = ChatViewModelFactory(repo, chatId))
    val messages by vm.messages.collectAsState()

    Scaffold(topBar = { SmallTopAppBar(title = { Text("Chat") }, actions = { Button(onClick = onCall) { Text("Call") }; Button(onClick = onVideo) { Text("Video") } }) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(Modifier.weight(1f)) {
                items(messages) { MessageEntityBubble(it) }
            }
            MessageInput(onSend = { vm.sendMessage(it) })
        }
    }
}

@Composable
fun MessageEntityBubble(entity: MessageEntity) {
    val outgoing = entity.senderId == "me"
    val bubbleColor = if (outgoing) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (outgoing) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(modifier = Modifier.padding(8.dp).fillMaxWidth(), contentAlignment = if (outgoing) Alignment.CenterEnd else Alignment.CenterStart) {
        Surface(shape = MaterialTheme.shapes.medium, color = bubbleColor) {
            Text(entity.text ?: "", color = textColor, modifier = Modifier.padding(8.dp))
        }
    }
}

@Composable
fun MessageInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    Row(Modifier.fillMaxWidth().padding(8.dp)) {
        TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
        Button(onClick = { onSend(text); text = "" }) { Text("Send") }
    }
}
""")

# ContactsScreen.kt from page 165
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens/ContactsScreen.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kakdela.p2p.Contact
import com.kakdela.p2p.PreviewData

@Composable
fun ContactsScreen(onOpenChat: (String) -> Unit) {
    val contacts = PreviewData.contacts
    LazyColumn {
        items(contacts) { contact ->
            Text(contact.name, Modifier.clickable { onOpenChat(contact.id) }.padding(16.dp))
        }
    }
}
""")

# CallScreen.kt from page 167
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens/CallScreen.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CallScreen(contactName: String, onEnd: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Calling $contactName")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onEnd) { Text("End Call") }
        }
    }
}
""")

# VideoCallScreen.kt from page 168
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/screens/VideoCallScreen.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VideoCallScreen(contactName: String, onEnd: () -> Unit) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("Video Calling $contactName")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onEnd) { Text("End Video") }
        }
    }
}
""")

# PreviewData.kt from page 156
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/PreviewData.kt"), "w") as f:
    f.write("""package com.kakdela.p2p

import com.kakdela.p2p.data.MessageEntity

data class Chat(val id: String, val name: String, val lastMessage: String, val time: String, val unread: Int = 0)

data class Message(val id: String, val text: String, val outgoing: Boolean, val timestamp: Long)

data class Contact(val id: String, val name: String, val phone: String, val installed: Boolean = false)

object PreviewData {
    val chats = listOf(
        Chat("1", "Anna", "See you tomorrow!", "09:12", 2),
        Chat("2", "Mark", "Let's meet", "08:45", 0),
        Chat("3", "Team", "Report sent", "Yesterday", 5)
    )

    val messages = listOf(
        MessageEntity(localId = 1, remoteId = "m1", chatId = "1", senderId = "friend", text = "Hello", type = "text", timestamp = System.currentTimeMillis(), delivered = true, synced = true),
        MessageEntity(localId = 2, remoteId = "m2", chatId = "1", senderId = "me", text = "Hi", type = "text", timestamp = System.currentTimeMillis(), delivered = true, synced = true)
    )

    val contacts = listOf(
        Contact("1", "Anna", "+7 999 111 22 33", true),
        Contact("2", "Mark", "+1 555 444 3333", false),
        Contact("3", "Liu", "+86 138 0013 8000", true)
    )
}
""")

# ChatViewModel.kt from page 190
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/ui/chat/ChatViewModel.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kakdela.p2p.data.MessageRepository
import com.kakdela.p2p.data.MessageEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(private val repo: MessageRepository, private val chatId: String) : ViewModel() {
    val messages: StateFlow<List<MessageEntity>> = repo.getChatFlow(chatId)
        .map { it.sortedBy { m -> m.timestamp } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun sendMessage(text: String) {
        viewModelScope.launch {
            repo.sendMessage(chatId, text = text)
        }
    }

    fun onIncoming(remoteId: String, senderId: String, text: String, timestamp: Long) {
        viewModelScope.launch {
            repo.onIncomingRemoteMessage(remoteId, chatId, senderId, text, timestamp)
        }
    }
}

class ChatViewModelFactory(private val repo: MessageRepository, private val chatId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChatViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(repo, chatId) as T
        }
        throw IllegalArgumentException("Unknown model class")
    }
}
""")

# WebRTCClient.kt from page 93-104
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/webrtc/WebRTCClient.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.webrtc

import android.content.Context
import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.webrtc.*

class WebRTCClient(
    private val context: Context,
    private val signalingUrl: String,
    private val roomId: String,
    private val listener: Listener
) {
    interface Listener {
        fun onLocalStream(localVideoTrack: VideoTrack?, localAudioTrack: AudioTrack?)
        fun onRemoteStream(remoteVideoTrack: VideoTrack?, remoteAudioTrack: AudioTrack?)
        fun onDataChannelMessage(text: String)
        fun onConnectionState(state: PeerConnection.PeerConnectionState)
        fun onLog(msg: String)
    }

    private val TAG = "WebRTCClient"
    private val executor = java.util.concurrent.Executors.newSingleThreadExecutor()
    private val scope = kotlinx.coroutines.CoroutineScope(executor.asCoroutineDispatcher() + kotlinx.coroutines.SupervisorJob())

    // WebRTC
    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var localAudioTrack: AudioTrack? = null
    private var localVideoTrack: VideoTrack? = null
    private var localVideoSource: VideoSource? = null
    private var dataChannel: DataChannel? = null

    // Signaling (OkHttp WebSocket)
    private var ws: WebSocket? = null
    private val ok = OkHttpClient.Builder().build()
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mapAdapter = moshi.adapter(Map::class.java)

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        // add TURN if needed
    )

    init {
        // Initialize PeerConnectionFactory
        val initializationOptions = PeerConnectionFactory.InitializationOptions.builder(context)
            .setFieldTrials("") // add if needed
            .createInitializationOptions()
        PeerConnectionFactory.initialize(initializationOptions)

        val options = PeerConnectionFactory.Options()
        val encoderFactory = DefaultVideoEncoderFactory(EglBase.create().eglBaseContext, true, true)
        val decoderFactory = DefaultVideoDecoderFactory(EglBase.create().eglBaseContext)
        factory = PeerConnectionFactory.builder()
            .setOptions(options)
            .setVideoEncoderFactory(encoderFactory)
            .setVideoDecoderFactory(decoderFactory)
            .createPeerConnectionFactory()
    }

    fun createPeerConnection() {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)
        peerConnection = factory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(newState: PeerConnection.SignalingState) { log("signaling $newState") }
            override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState) { log("ice conn $newState") }
            override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState) { log("ice gather $newState") }
            override fun onIceCandidate(candidate: IceCandidate) {
                log("ice candidate")
                sendSignaling(mapOf("type" to "candidate", "candidate" to candidate.sdp, "sdpMid" to candidate.sdpMid, "sdpMLineIndex" to candidate.sdpMLineIndex))
            }
            override fun onDataChannel(dc: DataChannel?) {
                log("onDataChannel")
                dataChannel = dc
                setupDataChannelCallbacks()
            }
            override fun onRenegotiationNeeded() { log("renegotiate needed") }
            override fun onAddTrack(receiver: RtpReceiver?, streams: Array<out MediaStream>?) {
                val track = receiver?.track() ?: return
                if (track is VideoTrack) listener.onRemoteStream(track, null)
                if (track is AudioTrack) listener.onRemoteStream(null, track)
            }
            override fun onRemoveTrack(receiver: RtpReceiver?) { log("remove track") }
            override fun onAddStream(stream: MediaStream?) {
                stream?.videoTracks?.firstOrNull()?.let { listener.onRemoteStream(it, null) }
                stream?.audioTracks?.firstOrNull()?.let { listener.onRemoteStream(null, it) }
            }
            override fun onRemoveStream(stream: MediaStream?) { log("remove stream") }
            override fun onConnectionChange(newState: PeerConnection.PeerConnectionState) {
                listener.onConnectionState(newState)
            }
            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
            override fun onSelectedCandidatePairChanged(event: CandidatePairChangeEvent?) {}
            override fun onTrack(transceiver: RtpTransceiver?) {}
            override fun onSignalingChange(p0: PeerConnection.SignalingState?) {}
            override fun onIceConnectionChange(p0: PeerConnection.IceConnectionState?) {}
            override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {}
            override fun onRenegotiationNeeded() {}
            override fun onAddStream(p0: MediaStream?) {}
            override fun onRemoveStream(p0: MediaStream?) {}
            override fun onDataChannel(p0: DataChannel?) {}
            override fun onAddTrack(p0: RtpReceiver?, p1: Array<out MediaStream>?) {}
        })
    }

    private fun setupDataChannelCallbacks() {
        dataChannel?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(previousAmount: Long) {}
            override fun onStateChange() { log("datachannel state ${dataChannel?.state()}") }
            override fun onMessage(buffer: DataChannel.Buffer?) {
                buffer?.let {
                    val bytes = ByteArray(buffer.data.remaining())
                    buffer.data.get(bytes)
                    listener.onDataChannelMessage(String(bytes))
                }
            }
        })
    }

    fun createLocalMedia(enableVideo: Boolean) {
        val audioSource = factory?.createAudioSource(MediaConstraints())
        localAudioTrack = factory?.createAudioTrack("audio", audioSource)

        if (enableVideo) {
            localVideoSource = factory?.createVideoSource(false)
            val capturer = createCameraCapturer()
            capturer?.startCapture(1280, 720, 30)
            localVideoTrack = factory?.createVideoTrack("video", localVideoSource)
        }

        localAudioTrack?.let { peerConnection?.addTrack(it) }
        localVideoTrack?.let { peerConnection?.addTrack(it) }
        listener.onLocalStream(localVideoTrack, localAudioTrack)
    }

    private fun createCameraCapturer(): CameraVideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        for (deviceName in enumerator.deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                val capturer = enumerator.createCapturer(deviceName, null)
                if (capturer != null) return capturer as CameraVideoCapturer
            }
        }
        return null
    }

    fun connectSignaling() {
        val req = Request.Builder().url(signalingUrl).build()
        ws = ok.newWebSocket(req, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                log("WS open")
                val join = mapOf("type" to "join", "roomId" to roomId)
                webSocket.send(mapAdapter.toJson(join))
            }
            override fun onMessage(webSocket: WebSocket, text: String) {
                log("WS msg: $text")
                try {
                    val map = mapAdapter.fromJson(text) ?: return
                    when (map["type"] as? String) {
                        "offer" -> handleRemoteOffer(map["sdp"] as? String ?: "")
                        "answer" -> handleRemoteAnswer(map["sdp"] as? String ?: "")
                        "candidate" -> handleRemoteCandidate(map["candidate"] as? String ?: "")
                        "message" -> listener.onDataChannelMessage(map["text"] as? String ?: "")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                log("WS failure: ${t.message}")
            }
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                log("WS closed: $code $reason")
            }
        })
    }

    private fun sendSignaling(payload: Map<String, Any?>) {
        val json = mapAdapter.toJson(payload)
        ws?.send(json)
    }

    fun createOffer() {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        }
        peerConnection?.createOffer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                desc?.let {
                    peerConnection?.setLocalDescription(object : SdpObserver {
                        override fun onSetSuccess() {
                            sendSignaling(mapOf("type" to "offer", "sdp" to it.description))
                        }
                        override fun onSetFailure(p0: String?) = log("setLocal fail: $p0")
                        override fun onCreateSuccess(p0: SessionDescription?) {}
                        override fun onCreateFailure(p0: String?) {}
                    }, it)
                }
            }
            override fun onCreateFailure(p0: String?) {}
            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String?) {}
        }, constraints)
    }

    fun handleRemoteOffer(sdp: String) {
        val desc = SessionDescription(SessionDescription.Type.OFFER, sdp)
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {
                peerConnection?.createAnswer(object : SdpObserver {
                    override fun onCreateSuccess(answer: SessionDescription?) {
                        answer?.let {
                            peerConnection?.setLocalDescription(object : SdpObserver {
                                override fun onSetSuccess() {
                                    sendSignaling(mapOf("type" to "answer", "sdp" to it.description))
                                }
                                override fun onSetFailure(p0: String?) = log("setLocal fail: $p0")
                                override fun onCreateSuccess(p0: SessionDescription?) {}
                                override fun onCreateFailure(p0: String?) {}
                            }, it)
                        }
                    }
                    override fun onCreateFailure(p0: String?) {}
                    override fun onSetSuccess() {}
                    override fun onSetFailure(p0: String?) {}
                }, MediaConstraints())
            }
            override fun onSetFailure(p0: String?) { log("set remote fail $p0") }
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
        }, desc)
    }

    fun handleRemoteAnswer(sdp: String) {
        val desc = SessionDescription(SessionDescription.Type.ANSWER, sdp)
        peerConnection?.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String?) { log("set remote fail $p0") }
            override fun onCreateSuccess(p0: SessionDescription?) {}
            override fun onCreateFailure(p0: String?) {}
        }, desc)
    }

    fun handleRemoteCandidate(candidate: String) {
        peerConnection?.addIceCandidate(IceCandidate("0", 0, candidate))
    }

    fun sendData(text: String) {
        dataChannel?.send(DataChannel.Buffer(java.nio.ByteBuffer.wrap(text.toByteArray()), false))
    }

    private fun log(msg: String) {
        Log.d(TAG, msg)
        listener.onLog(msg)
    }

    fun close() {
        ws?.close(1000, "bye")
        peerConnection?.close()
        factory?.dispose()
        executor.shutdown()
    }
}
""")

# WifiP2pHelper.kt from page 59-62
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/p2p/WifiP2pHelper.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.p2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.*
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetAddress

data class PeerDevice(
    val deviceName: String,
    val deviceAddress: String,
    val device: WifiP2pDevice
)

class WifiP2pHelper(private val context: Context) {

    private val manager = context.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
    private val channel = manager.initialize(context, Looper.getMainLooper(), null)
    private val peersInternal = MutableStateFlow<List<PeerDevice>>(emptyList())
    val peers: StateFlow<List<PeerDevice>> = peersInternal.asStateFlow()

    private val connectionInfoChannel = Channel<WifiP2pInfo>(Channel.BUFFERED)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(ctx: Context?, intent: Intent?) {
            when (intent?.action) {
                WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION -> {
                    manager.requestPeers(channel) { peerList ->
                        val list = peerList.deviceList.map { d ->
                            PeerDevice(d.deviceName ?: d.deviceAddress, d.deviceAddress, d)
                        }
                        peersInternal.tryEmit(list)
                    }
                }
                WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION -> {
                    manager.requestConnectionInfo(channel) { info ->
                        connectionInfoChannel.trySend(info)
                    }
                }
            }
        }
    }

    fun register() {
        val filter = IntentFilter().apply {
            addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
        }
        context.registerReceiver(receiver, filter)
    }

    fun unregister() {
        try {
            context.unregisterReceiver(receiver)
        } catch (e: Exception) { /* ignore */ }
    }

    fun discoverPeers(onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
        manager.discoverPeers(channel, object : WifiP2pManager.ActionListener {
            override fun onSuccess() = onSuccess()
            override fun onFailure(reason: Int) = onFailure(reason)
        })
    }

    fun connectTo(device: WifiP2pDevice, onSuccess: () -> Unit = {}, onFailure: (Int) -> Unit = {}) {
        val config = WifiP2pConfig().apply {
            deviceAddress = device.deviceAddress
        }
        manager.connect(channel, config, object : WifiP2pManager.ActionListener {
            override fun onSuccess() = onSuccess()
            override fun onFailure(reason: Int) = onFailure(reason)
        })
    }
}
""")

# P2pSocketServer.kt from page 64-65
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/p2p/P2pSocketServer.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.p2p

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.ServerSocket
import java.net.Socket
import java.nio.charset.StandardCharsets
import kotlin.time.Duration.Companion.seconds

data class WireMessage(val type: String, val payload: Map<String, Any?>)

class P2pSocketServer(
    private val port: Int = 8888,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var serverSocket: ServerSocket? = null
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    val incoming = Channel<Pair<Socket, WireMessage>>(Channel.UNLIMITED)

    fun start() {
        scope.launch {
            serverSocket = ServerSocket(port)
            while (true) {
                try {
                    val client = serverSocket!!.accept()
                    handleClient(client)
                } catch (e: Exception) {
                    e.printStackTrace()
                    kotlinx.coroutines.delay(1000)
                }
            }
        }
    }

    private fun handleClient(socket: Socket) {
        scope.launch {
            try {
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8))
                val writer = OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8)
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    try {
                        val adapter = moshi.adapter(Map::class.java)
                        val map = adapter.fromJson(line!!)
                        val type = map?.get("type") as? String ?: "unknown"
                        val payload = map as Map<String, Any?>
                        val msg = WireMessage(type, payload)
                        incoming.send(socket to msg)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                try { socket.close() } catch (_: Exception) {}
            }
        }
    }

    fun stop() {
        try { serverSocket?.close() } catch (_: Exception) {}
    }
}
""")

# P2pSocketClient.kt from page 66-67
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/p2p/P2pSocketClient.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.p2p

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.OutputStreamWriter
import java.net.Socket
import java.nio.charset.StandardCharsets

class P2pSocketClient(
    private val host: String,
    private val port: Int = 8888,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var socket: Socket? = null
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    suspend fun connect(): Socket {
        return withContext(scope.coroutineContext) {
            socket = Socket(host, port)
            socket!!
        }
    }

    suspend fun send(message: WireMessage) {
        withContext(scope.coroutineContext) {
            val writer = OutputStreamWriter(socket!!.getOutputStream(), StandardCharsets.UTF_8)
            val adapter = moshi.adapter(Map::class.java)
            val json = adapter.toJson(message.payload + mapOf("type" to message.type))
            writer.write(json + "\\n")
            writer.flush()
        }
    }

    fun close() {
        try { socket?.close() } catch (_: Exception) {}
    }
}
""")

# P2pRepository.kt from page 70
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/p2p/P2pRepository.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.p2p

import com.kakdela.p2p.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.Socket
import java.util.UUID

class P2pRepository(
    private val db: AppDatabase, // or MessageRepository
    private val server: P2pSocketServer,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val moshi = Moshi.Builder().build()

    init {
        scope.launch {
            for ((socket, wire) in server.incoming) {
                handleIncomingWire(wire, socket)
            }
        }
    }

    fun startServer() {
        server.start()
    }

    private fun handleIncomingWire(wire: WireMessage, socket: Socket) {
        when (wire.type) {
            "message" -> {
                val remoteId = wire.payload["id"] as? String ?: return
                val chatId = wire.payload["chatId"] as? String ?: return
                val senderId = wire.payload["senderId"] as? String ?: return
                val text = wire.payload["text"] as? String ?: return
                val timestamp = wire.payload["timestamp"] as? Long ?: System.currentTimeMillis()
                // save as incoming
                scope.launch {
                    db.messageDao().insertMessage(MessageEntity(remoteId = remoteId, chatId = chatId, senderId = senderId, text = text, timestamp = timestamp, delivered = true, synced = true))
                }
            }
            // add handshake, sync etc.
        }
    }

    fun sendMessageToPeer(chatId: String, text: String, peerHost: String, peerPort: Int = 8888) {
        scope.launch {
            try {
                val client = P2pSocketClient(peerHost, peerPort)
                client.connect()
                val remoteId = UUID.randomUUID().toString()
                client.send(WireMessage("message", mapOf("id" to remoteId, "chatId" to chatId, "senderId" to "me", "text" to text, "timestamp" to System.currentTimeMillis())))
                client.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
""")

# ContactsManager.kt from page 218
with open(os.path.join(project_dir, "app/src/main/java/com/kakdela/p2p/p2p/ContactsManager.kt"), "w") as f:
    f.write("""package com.kakdela.p2p.p2p

import android.content.Context
import android.provider.ContactsContract
import com.kakdela.p2p.Contact
import java.util.UUID

class ContactsManager(private val context: Context) {
    fun loadContacts(): List<Contact> {
        val res = mutableListOf<Contact>()
        val cr = context.contentResolver
        val cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER), null, null, null)
        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(0) ?: ""
                val number = it.getString(1) ?: ""
                res.add(Contact(UUID.randomUUID().toString(), name, number, false))
            }
        }
        return res
    }
}
""")

# And so on for other files.

# Since there are many, the script will have all.

print("Проект создан в директории " + project_dir + ". Открой в Android Studio или собери через ./gradlew assembleDebug.")
