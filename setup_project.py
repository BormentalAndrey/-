import os
import subprocess
import zipfile
from pathlib import Path
import shutil

def scan_and_analyze(directory='.'):
    """1. Сканирует все папки и файлы, 2. Анализирует"""
    print("Сканирование директории...")
    files = []
    folders = []
    for root, dirs, filenames in os.walk(directory):
        folders.extend(dirs)
        for filename in filenames:
            files.append(os.path.join(root, filename))
    print(f"Найдено папок: {len(folders)}, файлов: {len(files)}")
    return files, folders

def find_issues(files, folders):
    """3. Находит недочёты"""
    issues = []
    required_files = ['settings.gradle.kts', 'build.gradle.kts', 'app/build.gradle.kts', 'app/src/main/AndroidManifest.xml']
    for req in required_files:
        if not any(req in f for f in files):
            issues.append(f"Отсутствует файл: {req}")
    if 'app/src/main/java' not in ''.join(folders):
        issues.append("Отсутствует структура кода")
    print("Недочёты:")
    for issue in issues:
        print(issue)
    return issues

def fix_issues(issues, directory='.'):
    """Исправляет недочёты, докачивает, разархивирует"""
    project_dir = Path(directory) / "Kakdela-p2p"
    project_dir.mkdir(exist_ok=True)
    os.chdir(project_dir)
    
    # Разархивирование, если есть ZIP
    zip_files = [f for f in os.listdir('.') if f.endswith('.zip')]
    for zip_file in zip_files:
        print(f"Разархивирую {zip_file}...")
        with zipfile.ZipFile(zip_file, 'r') as zip_ref:
            zip_ref.extractall('.')

    # Создание структуры
    (project_dir / '.github/workflows').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/java/com/kakdela/p2p/data').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/java/com/kakdela/p2p/ui').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/java/com/kakdela/p2p/ui/screens').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/java/com/kakdela/p2p/ui/chat').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/java/com/kakdela/p2p/webrtc').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/java/com/kakdela/p2p/p2p').mkdir(parents=True, exist_ok=True)
    (project_dir / 'app/src/main/res/values').mkdir(parents=True, exist_ok=True)
    
    # Добавление файлов (на основе документа)
    with open("settings.gradle.kts", "w") as f:
        f.write('''pluginManagement {
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
''')

    with open("build.gradle.kts", "w") as f:
        f.write('''plugins {
    id("com.android.application") version "8.5.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.25" apply false
    id("kotlin-kapt") apply false
}
''')

    with open("app/build.gradle.kts", "w") as f:
        f.write('''plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.kakdela.p2p"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kakdela.p2p"
        minSdk = 21
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources.excludes += setOf("META-INF/*.kotlin_module")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation(platform("androidx.compose:compose-bom:2024.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // WebRTC
    implementation("org.webrtc:google-webrtc:1.0.32069")

    // OkHttp and Moshi for signaling
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // Coil for images
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.09.00"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
''')

    with open("app/src/main/AndroidManifest.xml", "w") as f:
        f.write('''<?xml version="1.0" encoding="utf-8"?>
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
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.KakdelaP2p">
        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:label="@string/app_name">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
''')

    with open("app/src/main/java/com/kakdela/p2p/App.kt", "w") as f:
        f.write('''package com.kakdela.p2p

import android.application.Application
import com.kakdela.p2p.data.MessageDatabase

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        MessageDatabase.getInstance(this)
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/MainActivity.kt", "w") as f:
        f.write('''package com.kakdela.p2p

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
''')

    with open("app/src/main/java/com/kakdela/p2p/data/MessageEntity.kt", "w") as f:
        f.write('''package com.kakdela.p2p.data

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
''')

    with open("app/src/main/java/com/kakdela/p2p/data/MessageDao.kt", "w") as f:
        f.write('''package com.kakdela.p2p.data

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
''')

    with open("app/src/main/java/com/kakdela/p2p/data/MessageDatabase.kt", "w") as f:
        f.write('''package com.kakdela.p2p.data

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
''')

    with open("app/src/main/java/com/kakdela/p2p/data/MessageRepository.kt", "w") as f:
        f.write('''package com.kakdela.p2p.data

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

    suspend fun onIncomingRemoteMessage(remoteId: String, chatId: String, senderId: String, text: String, timestamp: Long) {
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
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/KakdelaTheme.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFFBB86FC),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC5)
)

private val LightColorScheme = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF6200EE),
    secondary = androidx.compose.ui.graphics.Color(0xFF03DAC5)
)

@Composable
fun KakdelaTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/NavGraph.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kakdela.p2p.ui.screens.ChatListScreen
import com.kakdela.p2p.ui.screens.ChatScreen
import com.kakdela.p2p.ui.screens.CallScreen
import com.kakdela.p2p.ui.screens.VideoCallScreen
import androidx.navigation.navArgument
import androidx.navigation.NavType

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "chat_list") {
        composable("chat_list") { ChatListScreen(navController) }
        composable("chat/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { backStackEntry ->
            ChatScreen(chatId = backStackEntry.arguments?.getString("chatId") ?: "", navController = navController)
        }
        composable("call/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { backStackEntry ->
            CallScreen(chatId = backStackEntry.arguments?.getString("chatId") ?: "", navController = navController)
        }
        composable("video/{chatId}", arguments = listOf(navArgument("chatId") { type = NavType.StringType })) { backStackEntry ->
            VideoCallScreen(chatId = backStackEntry.arguments?.getString("chatId") ?: "", navController = navController)
        }
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/screens/ChatListScreen.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun ChatListScreen(navController: NavController) {
    val chats = listOf("Chat1", "Chat2", "Chat3") // Заменить на реальные данные

    LazyColumn {
        items(chats) { chat ->
            Text(text = chat, modifier = Modifier.clickable { navController.navigate("chat/$chat") })
        }
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/screens/ChatScreen.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kakdela.p2p.data.MessageEntity
import com.kakdela.p2p.ui.chat.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ChatScreen(chatId: String, navController: NavController) {
    val viewModel: ChatViewModel = viewModel()
    val messages by viewModel.getMessages(chatId).collectAsState(initial = emptyList())

    Column {
        Row {
            Button(onClick = { navController.navigate("call/$chatId") }) {
                Text("Звонок")
            }
            Button(onClick = { navController.navigate("video/$chatId") }) {
                Text("Видео")
            }
        }
        LazyColumn(Modifier.weight(1f)) {
            items(messages) { message ->
                Text(message.text ?: "")
            }
        }
        var text by remember { mutableStateOf("") }
        Row(Modifier.fillMaxWidth()) {
            TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
            Button(onClick = { viewModel.sendMessage(chatId, "me", text); text = "" }) {
                Text("Отправить")
            }
        }
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/screens/CallScreen.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun CallScreen(chatId: String, navController: NavController) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Аудиозвонок с $chatId")
        Button(onClick = { navController.popBackStack() }) {
            Text("Завершить звонок")
        }
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/screens/VideoCallScreen.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun VideoCallScreen(chatId: String, navController: NavController) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Видеозвонок с $chatId")
        Button(onClick = { navController.popBackStack() }) {
            Text("Завершить видео")
        }
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/ui/chat/ChatViewModel.kt", "w") as f:
        f.write('''package com.kakdela.p2p.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kakdela.p2p.data.MessageRepository
import com.kakdela.p2p.data.MessageDatabase
import com.kakdela.p2p.data.MessageEntity
import kotlinx.coroutines.launch
import android.app.Application

class ChatViewModel(application: Application) : ViewModel() {
    private val repo = MessageRepository(MessageDatabase.getInstance(application).messageDao())

    fun getMessages(chatId: String) = repo.getChatFlow(chatId)

    fun sendMessage(chatId: String, senderId: String, text: String) {
        viewModelScope.launch {
            repo.sendMessage(chatId, senderId, text)
        }
    }
}
''')

    with open("app/src/main/java/com/kakdela/p2p/webrtc/WebRTCClient.kt", "w") as f:
        f.write('''package com.kakdela.p2p.webrtc

import android.content.Context
import org.webrtc.PeerConnectionFactory
import org.webrtc.PeerConnection
import org.webrtc.DataChannel

class WebRTCClient(context: Context) {
    private val factory: PeerConnectionFactory

    init {
        PeerConnectionFactory.initialize(PeerConnectionFactory.InitializationOptions.builder(context).createInitializationOptions())
        factory = PeerConnectionFactory.builder().createPeerConnectionFactory()
    }

    fun createPeerConnection() : PeerConnection? {
        return factory.createPeerConnection(PeerConnection.RTCConfiguration(listOf()), null as PeerConnection.Observer?)
    }
}
''')

    with open(".github/workflows/build.yml", "w") as f:
        f.write('''name: Build APK

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: 'temurin'
          java-version: '17'
      - name: Setup Gradle
        uses: gradle/actions/setup-gradle@v3
      - name: Build debug APK
        run: ./gradlew assembleDebug --stacktrace
      - name: Upload APK
        uses: actions/upload-artifact@v4
        with:
          name: apk
          path: app/build/outputs/apk/debug/app-debug.apk
''')

    # Докачка Gradle wrapper если нужно
    if not os.path.exists("gradlew"):
        print("Докачиваю Gradle wrapper...")
        subprocess.run(["gradle", "wrapper", "--gradle-version", "8.9"], check=True)

def run_functions():
    """4. Запускает необходимые функции"""
    subprocess.run(["./gradlew", "tasks"], check=True)  # Пример

def build_apk():
    """5. Собирает APK"""
    print("Сборка APK...")
    subprocess.run(["./gradlew", "assembleDebug"], check=True)
    apk_path = "app/build/outputs/apk/debug/app-debug.apk"
    if os.path.exists(apk_path):
        print(f"APK собран: {apk_path}")
    else:
        print("Ошибка сборки")

def main():
    files, folders = scan_and_analyze()
    issues = find_issues(files, folders)
    fix_issues(issues)
    run_functions()
    build_apk()

if __name__ == "__main__":
    main()
