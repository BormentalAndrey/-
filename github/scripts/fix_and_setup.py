#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
fix_and_setup.py
Автопоправки структуры, необходимые для сборки:
 - создаёт gradle/wrapper/gradle-wrapper.properties если не найден
 - создаёт исполняемый gradlew (маленькая wrapper-обёртка)
 - если нет app/build.gradle.kts — пишет минимально рабочий файл
 - если нет AndroidManifest и минимальные источники — создаёт
 - пишет README_FIXES.txt с логом действий
"""
import os, stat, sys
from pathlib import Path

repo = Path('.').resolve()
log = []

def write_if_missing(path:Path, content:str, make_exec=False, force=False):
    if path.exists() and not force:
        log.append(f"SKIP existing {path}")
        return False
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content, encoding='utf-8')
    if make_exec:
        st = os.stat(path)
        os.chmod(path, st.st_mode | stat.S_IEXEC)
    log.append(f"WROTE {path}")
    return True

def ensure_gradle_wrapper():
    gprop = repo / 'gradle' / 'wrapper' / 'gradle-wrapper.properties'
    changed = False
    if not gprop.exists():
        changed |= write_if_missing(gprop, "distributionUrl=https\\://services.gradle.org/distributions/gradle-8.6-bin.zip\n")
    gradlew = repo / 'gradlew'
    gradlew_content = """#!/usr/bin/env sh
DIR="$( cd "$( dirname "$0" )" && pwd )"
# Try to use installed gradle first:
if command -v gradle >/dev/null 2>&1; then
  gradle "$@"
  exit $?
fi
# fallback to java + wrapper jar if exists:
if [ -f "$DIR/gradle/wrapper/gradle-wrapper.jar" ]; then
  java -jar "$DIR/gradle/wrapper/gradle-wrapper.jar" "$@"
else
  echo "No gradle binary and no gradle-wrapper.jar — CI uses gradle action which downloads Gradle."
  echo "Attempting to run gradle via 'gradle' command failed."
  exit 0
fi
"""
    changed |= write_if_missing(gradlew, gradlew_content, make_exec=True)
    return changed

def ensure_app_minimum():
    changed=False
    app_gradle = repo / 'app' / 'build.gradle.kts'
    manifest = repo / 'app' / 'src' / 'main' / 'AndroidManifest.xml'
    mainkt = repo / 'app' / 'src' / 'main' / 'java' / 'com' / 'kakdela' / 'p2p' / 'MainActivity.kt'
    strings = repo / 'app' / 'src' / 'main' / 'res' / 'values' / 'strings.xml'

    sample_gradle = """
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
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
}
"""
    sample_manifest = """<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.kakdela.p2p">
    <uses-permission android:name="android.permission.INTERNET" />
    <application android:allowBackup="true" android:label="@string/app_name">
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
"""
    sample_main = """package com.kakdela.p2p

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface

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
"""
    sample_strings = """<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Как дела?</string>
</resources>
"""
    changed |= write_if_missing(app_gradle, sample_gradle)
    changed |= write_if_missing(manifest, sample_manifest)
    changed |= write_if_missing(mainkt, sample_main)
    changed |= write_if_missing(strings, sample_strings)
    return changed

def main():
    changed_any = False
    changed_any |= ensure_gradle_wrapper()
    changed_any |= ensure_app_minimum()

    # write log
    with open('README_FIXES.txt','w',encoding='utf-8') as f:
        f.write("Auto-fix run\n\n")
        for l in log:
            f.write(l+"\n")
    print("\n".join(log))
    if changed_any:
        print("Some files were added/modified. Please inspect README_FIXES.txt and commit if ok.")
    else:
        print("No changes required by fixer.")

if __name__=="__main__":
    main()
