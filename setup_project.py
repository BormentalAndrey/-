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

    # Создание структуры (явно создаём 'app' сначала)
    app_dir = project_dir / 'app'
    app_dir.mkdir(exist_ok=True)  # Явное создание 'app'
    
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

    with open("app/build.gradle.kts", "w") as f:  # Теперь 'app' существует
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

    # ... (остальной код файлов из оригинального скрипта без изменений)

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
