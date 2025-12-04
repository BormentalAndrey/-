#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
setup_project_full.py
Создаёт Android проект Kakdela-p2p + helper-скрипты:
- bootstrap.sh  -> скачивает gradle (gradle-8.6-bin.zip) и создает локальный gradle + gradlew
- fetch_files.py -> скачивает примеры ресурсов (avatar, signaling example)
- build_with_local_gradle.sh -> вызывает ./gradlew assembleDebug

Запуск:
  python3 setup_project_full.py
  cd Kakdela-p2p
  ./bootstrap.sh
  ./gradlew assembleDebug
"""

import os
import textwrap
import shutil

# ------------------------
# Конфигурация
# ------------------------
project_dir = os.path.join(os.getcwd(), "Kakdela-p2p")
package = "com.kakdela.p2p"
gradle_version = "8.6"
gradle_zip_name = f"gradle-{gradle_version}-bin.zip"
gradle_download_url = f"https://services.gradle.org/distributions/{gradle_zip_name}"

# ------------------------
# Утилиты
# ------------------------
def ensure_dir(path):
    os.makedirs(path, exist_ok=True)

def write_file(path, content, mode="w", permissions=None):
    ensure_dir(os.path.dirname(path))
    with open(path, mode, encoding="utf-8") as f:
        f.write(content)
    if permissions:
        os.chmod(path, permissions)
    print("Wrote:", path)

def copy_if_exists(src, dst):
    if os.path.exists(src):
        ensure_dir(os.path.dirname(dst))
        shutil.copy(src, dst)
        print("Copied:", src, "->", dst)
        return True
    return False

# ------------------------
# Создаём структуру (основные папки)
# ------------------------
dirs = [
    project_dir,
    os.path.join(project_dir, ".github", "workflows"),
    os.path.join(project_dir, "app", "src", "main", "java", *package.split(".")),
    os.path.join(project_dir, "app", "src", "main", "res", "values"),
    os.path.join(project_dir, "app", "src", "main", "res", "values-ru"),
    os.path.join(project_dir, "app", "src", "main", "res", "drawable"),
    os.path.join(project_dir, "docs"),
]
for d in dirs:
    ensure_dir(d)

# ------------------------
# Пишем основные файлы проекта (минимум)
# ------------------------
# settings.gradle.kts
write_file(os.path.join(project_dir, "settings.gradle.kts"), textwrap.dedent("""\
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
"""))

# root build.gradle.kts (пустой / placeholder)
write_file(os.path.join(project_dir, "build.gradle.kts"), textwrap.dedent("""\
    // Root build file (placeholder). Module build.gradle.kts lives in app/.
"""))

# gradle.properties
write_file(os.path.join(project_dir, "gradle.properties"), textwrap.dedent("""\
    android.useAndroidX=true
    android.enableJetifier=true
    org.gradle.jvmargs=-Xmx2g -Dfile.encoding=UTF-8
"""))

# app/build.gradle.kts
write_file(os.path.join(project_dir, "app", "build.gradle.kts"), textwrap.dedent(f"""\
    plugins {{
        id("com.android.application")
        id("org.jetbrains.kotlin.android")
        id("kotlin-kapt")
    }}

    android {{
        namespace = "{package}"
        compileSdk = 34

        defaultConfig {{
            applicationId = "{package}"
            minSdk = 21
            targetSdk = 34
            versionCode = 1
            versionName = "1.0"
        }}

        buildFeatures {{
            compose = true
        }}
        composeOptions {{
            kotlinCompilerExtensionVersion = "1.6.0"
        }}

        compileOptions {{
            sourceCompatibility = JavaVersion.VERSION_17
            targetCompatibility = JavaVersion.VERSION_17
        }}
        kotlinOptions {{
            jvmTarget = "17"
        }}
        packagingOptions {{
            resources.excludes += setOf("META-INF/*.kotlin_module")
        }}
    }}

    dependencies {{
        implementation("androidx.core:core-ktx:1.10.1")
        implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
        implementation("androidx.activity:activity-compose:1.8.0")
        implementation(platform("androidx.compose:compose-bom:2024.12.00"))
        implementation("androidx.compose.ui:ui")
        implementation("androidx.compose.material3:material3")
        implementation("androidx.compose.ui:ui-tooling-preview")
        debugImplementation("androidx.compose.ui:ui-tooling")
        implementation("androidx.navigation:navigation-compose:2.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    }}
"""))

# AndroidManifest.xml
write_file(os.path.join(project_dir, "app", "src", "main", "AndroidManifest.xml"), textwrap.dedent(f"""\
    <?xml version="1.0" encoding="utf-8"?>
    <manifest xmlns:android="http://schemas.android.com/apk/res/android"
        package="{package}">

        <uses-permission android:name="android.permission.INTERNET" />
        <application
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
"""))

# strings.xml (default + ru)
write_file(os.path.join(project_dir, "app", "src", "main", "res", "values", "strings.xml"), textwrap.dedent("""\
    <resources>
        <string name="app_name">Как дела?</string>
        <string name="hello">Hello from Kakdela</string>
    </resources>
"""))
write_file(os.path.join(project_dir, "app", "src", "main", "res", "values-ru", "strings.xml"), textwrap.dedent("""\
    <resources>
        <string name="app_name">Как дела?</string>
        <string name="hello">Привет от Как дела?</string>
    </resources>
"""))

# minimal colors/themes
write_file(os.path.join(project_dir, "app", "src", "main", "res", "values", "colors.xml"), textwrap.dedent("""\
    <resources>
        <color name="md_theme_light_primary">#0066FF</color>
    </resources>
"""))
write_file(os.path.join(project_dir, "app", "src", "main", "res", "values", "themes.xml"), textwrap.dedent("""\
    <resources xmlns:tools="http://schemas.android.com/tools">
        <style name="Theme.Kakdela" parent="Theme.Material3.DayNight.NoActionBar">
            <item name="android:windowBackground">?android:colorBackground</item>
        </style>
    </resources>
"""))

# MainActivity.kt (минимал)
main_activity_path = os.path.join(project_dir, "app", "src", "main", "java", *package.split("."), "MainActivity.kt")
write_file(main_activity_path, textwrap.dedent(f"""\
    package {package}

    import android.os.Bundle
    import androidx.activity.ComponentActivity
    import androidx.activity.compose.setContent
    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Alignment
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp

    class MainActivity : ComponentActivity() {{
        override fun onCreate(savedInstanceState: Bundle?) {{
            super.onCreate(savedInstanceState)
            setContent {{
                KakdelaApp()
            }}
        }}
    }}

    @Composable
    fun KakdelaApp() {{
        MaterialTheme {{
            Surface(modifier = Modifier.fillMaxSize()) {{
                Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {{
                    Text(text = "Как дела? (Demo)", style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Пример минимального приложения", style = MaterialTheme.typography.bodyMedium)
                }}
            }}
        }}
    }}
"""))

# Preview README
write_file(os.path.join(project_dir, "README.md"), textwrap.dedent("""\
    # Kakdela-p2p (demo)
    Этот репозиторий создан автоматически скриптом setup_project_full.py
    Сборка:
      1. ./bootstrap.sh   # скачивает gradle и делает ./gradlew
      2. ./gradlew assembleDebug
"""))

# copy uploaded PDF if exists (local path used by environment, optional)
uploaded_local = "/mnt/data/Конечно, я дел-WPS Office.pdf"
if copy_if_exists(uploaded_local, os.path.join(project_dir, "docs", "Конечно_я_дел.pdf")):
    print("Found and copied uploaded PDF into docs/")

# ------------------------
# Создаём helper-скрипты
# ------------------------

# bootstrap.sh: скачивает gradle-<version>-bin.zip и распаковывает в ./gradle-local,
# создаёт простые обёртки gradlew/gradlew.bat которые вызывают локальную распакованную gradle/bin/gradle
bootstrap_sh = textwrap.dedent(f"""\
    #!/usr/bin/env bash
    set -e
    echo "Bootstrap: скачиваем Gradle {gradle_version} и готовим локальный gradle"

    PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
    TARGET_DIR="$PROJECT_DIR/gradle-local"
    ZIP_NAME="{gradle_zip_name}"
    DOWNLOAD_URL="{gradle_download_url}"
    TMP_ZIP="/tmp/$ZIP_NAME"

    mkdir -p "$TARGET_DIR"

    if [ ! -d "$TARGET_DIR/gradle-{gradle_version}" ]; then
      echo "Скачиваем {gradle_zip_name}..."
      if command -v curl >/dev/null 2>&1; then
        curl -L -o "$TMP_ZIP" "$DOWNLOAD_URL"
      elif command -v wget >/dev/null 2>&1; then
        wget -O "$TMP_ZIP" "$DOWNLOAD_URL"
      else
        echo "Ошибка: ни curl, ни wget не установлены. Установите один из них."
        exit 1
      fi
      echo "Распаковываем..."
      mkdir -p "$TARGET_DIR"
      unzip -q "$TMP_ZIP" -d "$TARGET_DIR"
      rm -f "$TMP_ZIP"
    else
      echo "Gradle уже распакован в $TARGET_DIR/gradle-{gradle_version}"
    fi

    # создаём small gradlew wrapper (исполняемый)
    GRADLE_BIN="$TARGET_DIR/gradle-{gradle_version}/bin/gradle"
    GRADLEW="$PROJECT_DIR/gradlew"
    cat > "$GRADLEW" <<'EOS'
    #!/usr/bin/env bash
    PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"
    exec "$PROJECT_DIR/gradle-local/gradle-{gradle_version}/bin/gradle" "$@"
    EOS
    chmod +x "$GRADLEW"

    # gradlew.bat for windows
    GRADLEW_BAT="$PROJECT_DIR/gradlew.bat"
    cat > "$GRADLEW_BAT" <<'EOS'
    @echo off
    set SCRIPT_DIR=%~dp0
    "%SCRIPT_DIR%\\gradle-local\\gradle-{gradle_version}\\bin\\gradle.bat" %*
    EOS

    echo "Bootstrap завершён. Используй ./gradlew assembleDebug внутри папки проекта (или запусти workflow на GitHub)."
""")
write_file(os.path.join(project_dir, "bootstrap.sh"), bootstrap_sh, permissions=0o755)

# fetch_files.py: пример скачивания ресурсов (аватар, signaling example)
fetch_files_py = textwrap.dedent("""\
    #!/usr/bin/env python3
    # простой скрипт который скачивает несколько вспомогательных файлов в проект
    import os
    import urllib.request

    project_dir = os.path.join(os.getcwd(), "Kakdela-p2p")
    assets = {
        "https://raw.githubusercontent.com/google/material-design-icons/master/notification/2x_web/ic_notifications_48px.png": "app/src/main/res/drawable/ic_notifications.png",
        "https://raw.githubusercontent.com/mdn/learning-area/main/webrtc/scripts/signaling-server.js": "server/signaling-server.js",
        "https://raw.githubusercontent.com/github/explore/main/topics/android/android.png": "docs/android-topic.png"
    }

    os.makedirs(os.path.join(project_dir), exist_ok=True)
    for url, rel in assets.items():
        dst = os.path.join(project_dir, rel)
        os.makedirs(os.path.dirname(dst), exist_ok=True)
        try:
            print('Downloading', url, '->', dst)
            urllib.request.urlretrieve(url, dst)
        except Exception as e:
            print('Failed to download', url, e)

    print("Done. Проверить папку:", project_dir)
""")
write_file(os.path.join(project_dir, "fetch_files.py"), fetch_files_py, permissions=0o755)

# build_with_local_gradle.sh
build_sh = textwrap.dedent(f"""\
    #!/usr/bin/env bash
    set -e
    cd "$(dirname "$0")"
    if [ ! -x "./gradlew" ]; then
      echo "gradlew не найден/не исполняемый. Запусти ./bootstrap.sh сначала."
      exit 1
    fi
    echo "Запускаем сборку Debug APK..."
    ./gradlew assembleDebug --no-daemon --stacktrace
""")
write_file(os.path.join(project_dir, "build_with_local_gradle.sh"), build_sh, permissions=0o755)

# .github workflow (как раньше)
workflow = textwrap.dedent(f"""\
    name: Build Android APK (Kakdela-p2p)

    on:
      push:
        branches: [ main ]
      workflow_dispatch:

    jobs:
      build:
        runs-on: ubuntu-latest
        steps:
          - name: Checkout repo
            uses: actions/checkout@v4

          - name: Setup JDK 17
            uses: actions/setup-java@v4
            with:
              distribution: temurin
              java-version: 17

          - name: Setup Gradle (action)
            uses: gradle/gradle-build-action@v2
            with:
              gradle-version: {gradle_version}

          - name: Build Debug APK
            run: |
              cd Kakdela-p2p
              gradle assembleDebug --no-daemon --stacktrace
            env:
              CI: true

          - name: Upload APK artifact
            uses: actions/upload-artifact@v4
            with:
              name: kakdela-apk
              path: Kakdela-p2p/app/build/outputs/apk
""")
write_file(os.path.join(project_dir, ".github", "workflows", "build.yml"), workflow)

# .gitignore
write_file(os.path.join(project_dir, ".gitignore"), textwrap.dedent("""\
    .gradle/
    build/
    **/build/
    .idea/
    .DS_Store
    local.properties
    /kotlin/
    gradle-local/
"""))

print("\nГотово. Инструкции:")
print("1) Перейди в папку проекта:", project_dir)
print("2) Запусти ./bootstrap.sh  -> он скачает gradle и создаст ./gradlew")
print("3) Запусти ./fetch_files.py -> скачает дополнительные демонстрационные ресурсы")
print("4) Запусти ./gradlew assembleDebug (или ./build_with_local_gradle.sh) чтобы собрать debug APK")
print("\nЕсли хочешь — могу дополнительно добавить полную реализацию Room, WebRTC и P2P (DAO/ViewModel/сигналинг).")
