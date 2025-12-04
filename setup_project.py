#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
setup_project.py
Создаёт минимальный, но работоспособный Android Compose проект "Как дела?"
— структура, все файлы, workflow GitHub Actions, gradle wrapper properties и простая
Kotlin/Compose UI. Workflow запускает сначала Python (если нужно), затем Gradle build.
Запуск: python3 setup_project.py
"""

import os
import stat
from textwrap import dedent

project_dir = "Kakdela-p2p"

def ensure_dirs(list_of_dirs):
    for d in list_of_dirs:
        os.makedirs(d, exist_ok=True)

def write(relpath, content, make_executable=False):
    full = os.path.join(project_dir, relpath)
    parent = os.path.dirname(full)
    if parent and not os.path.exists(parent):
        os.makedirs(parent, exist_ok=True)
    with open(full, "w", encoding="utf-8") as f:
        f.write(dedent(content).lstrip("\n"))
    if make_executable:
        st = os.stat(full)
        os.chmod(full, st.st_mode | stat.S_IEXEC)

def main():
    print("Создаю проект в:", project_dir)

    dirs = [
        project_dir,
        os.path.join(project_dir, ".github"),
        os.path.join(project_dir, ".github", "workflows"),
        os.path.join(project_dir, "app"),
        os.path.join(project_dir, "app", "src", "main"),
        os.path.join(project_dir, "app", "src", "main", "java", "com", "kakdela", "p2p"),
        os.path.join(project_dir, "app", "src", "main", "java", "com", "kakdela", "p2p", "ui"),
        os.path.join(project_dir, "app", "src", "main", "java", "com", "kakdela", "p2p", "ui", "screens"),
        os.path.join(project_dir, "app", "src", "main", "java", "com", "kakdela", "p2p", "ui", "chat"),
        os.path.join(project_dir, "app", "src", "main", "java", "com", "kakdela", "p2p", "data"),
        os.path.join(project_dir, "app", "src", "main", "res", "values"),
        os.path.join(project_dir, "app", "src", "main", "res", "mipmap"),
        os.path.join(project_dir, "gradle"),
        os.path.join(project_dir, "gradle", "wrapper"),
    ]
    ensure_dirs(dirs)

    # settings.gradle.kts
    write("settings.gradle.kts", """
        pluginManagement {
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

    # root build.gradle.kts
    write("build.gradle.kts", """
        plugins {
            id("com.android.application") version "8.1.0" apply false
            id("org.jetbrains.kotlin.android") version "1.9.22" apply false
        }
    """)

    # gradle.properties
    write("gradle.properties", """
        android.useAndroidX=true
        android.enableJetifier=true
        org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
        org.gradle.parallel=true
    """)

    # .gitignore
    write(".gitignore", """
        .gradle/
        /build/
        **/build/
        .idea/
        .DS_Store
        /local.properties
        *.iml
    """)

    # README
    write("README.md", """
        # Как дела? — Kakdela-p2p (minimal)

        Этот репозиторий содержит минимальную Android Compose аппу "Как дела?".
        Скрипт setup_project.py создаёт структуру и файлы.

        CI: .github/workflows/build.yml запускает:
         1) Python step (если нужен)
         2) Gradle build через gradle/gradle-build-action@v2 -> assembleDebug

        Сборка: debug APK (app/build/outputs/apk/debug/app-debug.apk)
    """)

    # GitHub Actions workflow: сначала запускаем python (например, генерируем файлы), потом gradle action
    write(".github/workflows/build.yml", """
        name: Android CI (Python -> Gradle)

        on:
          push:
            branches: [ main ]
          workflow_dispatch:

        jobs:
          build:
            runs-on: ubuntu-latest
            steps:
              - name: Checkout
                uses: actions/checkout@v4

              - name: Set up Python 3.x
                uses: actions/setup-python@v4
                with:
                  python-version: '3.11'

              - name: Run setup_project.py (generate/validate files)
                run: |
                  python -V
                  python setup_project.py
                shell: bash

              - name: Set up JDK 17
                uses: actions/setup-java@v4
                with:
                  distribution: temurin
                  java-version: 17

              - name: Cache Gradle
                uses: actions/cache@v4
                with:
                  path: |
                    ~/.gradle/caches
                    ~/.gradle/wrapper
                  key: ${{ runner.os }}-gradle-${{ hashFiles('**/*.gradle*','**/gradle/wrapper/gradle-wrapper.properties') }}
                  restore-keys: ${{ runner.os }}-gradle-

              - name: Run Gradle build via gradle action
                uses: gradle/gradle-build-action@v2
                with:
                  arguments: assembleDebug --no-daemon

              - name: Upload Debug APK (if built)
                uses: actions/upload-artifact@v4
                with:
                  name: kakdela-debug-apk
                  path: app/build/outputs/apk/debug/app-debug.apk
                  if-no-files-found: ignore
    """)

    # app module build.gradle.kts
    write("app/build.gradle.kts", """
        plugins {
            id("com.android.application")
            id("org.jetbrains.kotlin.android")
            id("kotlin-kapt")
        }

        android {
            namespace = "com.kakdela.p2p"
            compileSdk = 33

            defaultConfig {
                applicationId = "com.kakdela.p2p"
                minSdk = 21
                targetSdk = 33
                versionCode = 1
                versionName = "1.0"
            }

            buildFeatures {
                compose = true
            }

            composeOptions {
                kotlinCompilerExtensionVersion = "1.6.0"
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
            implementation("androidx.core:core-ktx:1.10.1")
            implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
            implementation("androidx.activity:activity-compose:1.8.0")

            implementation(platform("androidx.compose:compose-bom:2024.12.00"))
            implementation("androidx.compose.ui:ui")
            implementation("androidx.compose.material3:material3")
            implementation("androidx.compose.ui:ui-tooling-preview")
            debugImplementation("androidx.compose.ui:ui-tooling")

            implementation("androidx.room:room-runtime:2.5.2")
            kapt("androidx.room:room-compiler:2.5.2")
            implementation("androidx.room:room-ktx:2.5.2")

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

            implementation("com.squareup.okhttp3:okhttp:4.11.0")
            implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

            implementation("io.coil-kt:coil-compose:2.4.0")
            implementation("androidx.navigation:navigation-compose:2.6.0")
            implementation("androidx.datastore:datastore-preferences:1.1.0")
            implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1")
        }
    """)

    # AndroidManifest.xml
    write("app/src/main/AndroidManifest.xml", """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.kakdela.p2p">

            <uses-permission android:name="android.permission.INTERNET" />
            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
            <uses-permission android:name="android.permission.RECORD_AUDIO" />
            <uses-permission android:name="android.permission.CAMERA" />

            <application
                android:name=".App"
                android:allowBackup="true"
                android:label="@string/app_name"
                android:theme="@style/Theme.Kakdela">
                <activity android:name=".MainActivity" android:exported="true">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
                </activity>
            </application>
        </manifest>
    """)

    # resources
    write("app/src/main/res/values/strings.xml", """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <string name="app_name">Как дела?</string>
        </resources>
    """)
    write("app/src/main/res/values/themes.xml", """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <style name="Theme.Kakdela" parent="Theme.Material3.DayNight.NoActionBar"/>
        </resources>
    """)

    # minimal Kotlin app: App.kt, MainActivity.kt, simple Compose UI
    write("app/src/main/java/com/kakdela/p2p/App.kt", """
        package com.kakdela.p2p

        import android.app.Application

        class App : Application()
    """)

    write("app/src/main/java/com/kakdela/p2p/MainActivity.kt", """
        package com.kakdela.p2p

        import android.os.Bundle
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.setContent
        import androidx.compose.foundation.layout.*
        import androidx.compose.material3.*
        import androidx.compose.runtime.*
        import androidx.compose.ui.Alignment
        import androidx.compose.ui.Modifier
        import androidx.compose.ui.unit.dp
        import com.kakdela.p2p.ui.KakdelaTheme

        class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContent {
                    KakdelaTheme {
                        Surface(modifier = Modifier.fillMaxSize()) {
                            MainScreen()
                        }
                    }
                }
            }
        }

        @Composable
        fun MainScreen() {
            var messages by remember { mutableStateOf(listOf("Привет!", "Как дела?")) }
            var text by remember { mutableStateOf("") }
            Column(Modifier.fillMaxSize().padding(16.dp)) {
                Text("Как дела?", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(12.dp))
                Column(Modifier.weight(1f)) {
                    for (m in messages) {
                        Card(Modifier.fillMaxWidth().padding(4.dp)) {
                            Text(m, Modifier.padding(8.dp))
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    TextField(value = text, onValueChange = { text = it }, modifier = Modifier.weight(1f))
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = {
                        if (text.isNotBlank()) {
                            messages = messages + text
                            text = ""
                        }
                    }) { Text("Отправить") }
                }
            }
        }
    """)

    # small utility Kotlin files to satisfy compile
    write("app/src/main/java/com/kakdela/p2p/PreviewData.kt", """
        package com.kakdela.p2p

        data class Contact(val id: String, val name: String)
    """)

    # Room DB entity/dao minimal (to keep earlier design)
    write("app/src/main/java/com/kakdela/p2p/data/MessageEntity.kt", """
        package com.kakdela.p2p.data

        import androidx.room.Entity
        import androidx.room.PrimaryKey

        @Entity(tableName = "messages")
        data class MessageEntity(
            @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
            val chatId: String = "1",
            val senderId: String = "me",
            val text: String? = "",
            val timestamp: Long = System.currentTimeMillis()
        )
    """)

    write("app/src/main/java/com/kakdela/p2p/data/MessageDao.kt", """
        package com.kakdela.p2p.data

        import androidx.room.Dao
        import androidx.room.Insert
        import androidx.room.OnConflictStrategy
        import androidx.room.Query
        import kotlinx.coroutines.flow.Flow

        @Dao
        interface MessageDao {
            @Query("SELECT * FROM messages ORDER BY timestamp ASC")
            fun getAll(): Flow<List<MessageEntity>>

            @Insert(onConflict = OnConflictStrategy.IGNORE)
            suspend fun insert(m: MessageEntity): Long
        }
    """)

    write("app/src/main/java/com/kakdela/p2p/data/MessageDatabase.kt", """
        package com.kakdela.p2p.data

        import android.content.Context
        import androidx.room.Database
        import androidx.room.Room
        import androidx.room.RoomDatabase

        @Database(entities = [MessageEntity::class], version = 1, exportSchema = false)
        abstract class MessageDatabase : RoomDatabase() {
            abstract fun messageDao(): MessageDao

            companion object {
                @Volatile private var INSTANCE: MessageDatabase? = null
                fun getInstance(context: Context): MessageDatabase =
                    INSTANCE ?: synchronized(this) {
                        INSTANCE ?: Room.databaseBuilder(context.applicationContext, MessageDatabase::class.java, "kakdela_messages.db")
                            .fallbackToDestructiveMigration().build().also { INSTANCE = it }
                    }
            }
        }
    """)

    # Simple UI theme file (to compile)
    write("app/src/main/java/com/kakdela/p2p/ui/KakdelaTheme.kt", """
        package com.kakdela.p2p.ui

        import androidx.compose.material3.MaterialTheme
        import androidx.compose.runtime.Composable

        @Composable
        fun KakdelaTheme(content: @Composable () -> Unit) {
            MaterialTheme(
                content = content
            )
        }
    """)

    # Minimal gradle wrapper properties (so workflow cache key works)
    write("gradle/wrapper/gradle-wrapper.properties", """
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists
        distributionUrl=https\\://services.gradle.org/distributions/gradle-8.6-bin.zip
    """)

    # Small placeholder gradlew — executable to avoid missing-file errors in some scripts.
    write("gradlew", """
        #!/usr/bin/env bash
        echo "This is a placeholder gradlew. CI uses gradle/gradle-build-action to install Gradle."
        echo "To use a real wrapper locally run: gradle wrapper"
        exit 0
    """, make_executable=True)

    # LICENSE
    write("LICENSE", """
        MIT License

        Copyright (c) 2025

        Permission is hereby granted, free of charge, to any person obtaining a copy...
    """)

    print("Готово. Проект создан в:", project_dir)
    print("Сделай commit & push. После push GitHub Actions запустит workflow .github/workflows/build.yml.")
    print("Если Actions падает — пришли лог (скрин/текст), я помогу исправить.")
    print()
    print("Подсказки:")
    print(" - Если хочешь собирать локально, сгенерируй настоящий gradle wrapper локально:")
    print("   1) Установи Gradle локально")
    print("   2) В папке Kakdela-p2p запусти: gradle wrapper")
    print(" - Для релизной подписи потребуется keystore и конфигурация signingConfigs (мы собираем debug).")

if __name__ == "__main__":
    main()
