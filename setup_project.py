#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
setup_project.py
Создаёт минимальную, но реально собираемую Android-структуру проекта
с приложением "Как дела?" (Concept messenger). Скрипт создаёт файлы в корне репозитория.
Запуск на GitHub Actions: python3 setup_project.py
"""
import os
import stat
from textwrap import dedent

# Если хочешь — поменяй на "Kakdela-p2p" (но workflow настроен на корень).
project_dir = "."  # создаём прямо в репозитории

def write(relpath: str, content: str, make_executable: bool = False):
    path = os.path.join(project_dir, relpath)
    folder = os.path.dirname(path)
    if folder and not os.path.exists(folder):
        os.makedirs(folder, exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(dedent(content).lstrip("\n"))
    if make_executable:
        st = os.stat(path)
        os.chmod(path, st.st_mode | stat.S_IEXEC)

def main():
    print("Создаём структуру проекта в репозитории (текущая директория).")

    # settings, gradle props, wrapper props
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

    write("build.gradle.kts", """
        plugins {
            // root build only declares plugin versions - modules apply plugins themselves
        }
    """)

    write("gradle/wrapper/gradle-wrapper.properties", r"""
        distributionBase=GRADLE_USER_HOME
        distributionPath=wrapper/dists
        zipStoreBase=GRADLE_USER_HOME
        zipStorePath=wrapper/dists
        distributionUrl=https\://services.gradle.org/distributions/gradle-8.6-bin.zip
    """)

    # Minimal app/build.gradle.kts
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

            buildTypes {
                getByName("release") {
                    isMinifyEnabled = false
                }
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
        }

        dependencies {
            implementation("androidx.core:core-ktx:1.10.1")
            implementation("androidx.activity:activity-compose:1.8.0")
            implementation(platform("androidx.compose:compose-bom:2024.12.00"))
            implementation("androidx.compose.ui:ui")
            implementation("androidx.compose.material3:material3")
            implementation("androidx.compose.ui:ui-tooling-preview")
            debugImplementation("androidx.compose.ui:ui-tooling")
        }
    """)

    # gradle.properties
    write("gradle.properties", """
        org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
        android.useAndroidX=true
        android.enableJetifier=true
    """)

    # AndroidManifest
    write("app/src/main/AndroidManifest.xml", """
        <?xml version="1.0" encoding="utf-8"?>
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.kakdela.p2p">

            <uses-permission android:name="android.permission.INTERNET" />

            <application
                android:allowBackup="true"
                android:label="@string/app_name">
                <activity android:name=".MainActivity" android:exported="true">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN" />
                        <category android:name="android.intent.category.LAUNCHER" />
                    </intent-filter>
                </activity>
            </application>
        </manifest>
    """)

    # Strings & theme (very small)
    write("app/src/main/res/values/strings.xml", """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <string name="app_name">Как дела?</string>
        </resources>
    """)
    write("app/src/main/res/values/themes.xml", """
        <?xml version="1.0" encoding="utf-8"?>
        <resources>
            <style name="Theme.Kakdela" parent="Theme.Material3.DayNight.NoActionBar">
            </style>
        </resources>
    """)

    # Kotlin minimal files
    write("app/src/main/java/com/kakdela/p2p/App.kt", """
        package com.kakdela.p2p

        import android.app.Application

        class App : Application() {
            override fun onCreate() {
                super.onCreate()
                // инициализация, если нужна
            }
        }
    """)

    write("app/src/main/java/com/kakdela/p2p/MainActivity.kt", """
        package com.kakdela.p2p

        import android.os.Bundle
        import androidx.activity.ComponentActivity
        import androidx.activity.compose.setContent
        import androidx.compose.material3.Text
        import androidx.compose.material3.Surface
        import androidx.compose.material3.MaterialTheme

        class MainActivity : ComponentActivity() {
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                setContent {
                    MaterialTheme {
                        Surface {
                            Text("Как дела? — демо")
                        }
                    }
                }
            }
        }
    """)

    # Small helper Kotlin to avoid empty module issues
    write("app/src/main/java/com/kakdela/p2p/Hello.kt", """
        package com.kakdela.p2p
        fun hello(): String = "Hello"
    """)

    # README
    write("README.md", """
        # Kakdela-p2p — простая демо-версия
        Скрипт setup_project.py сгенерировал минимальную Android Studio структуру.
        Workflow запускает python-скрипт, а затем Gradle action собирает assembleDebug.
    """)

    # placeholder gradlew (не обязателен, но иногда удобно)
    write("gradlew", """
        #!/usr/bin/env bash
        echo "Gradle wrapper placeholder. CI uses gradle action which installs Gradle."
        exit 0
    """, make_executable=True)

    print("Генерация завершена. Проверь файлы, закоммить и запушь.")
    print("После пуша Actions запустится автоматически (workflow .github/workflows/build.yml).")

if __name__ == "__main__":
    main()
