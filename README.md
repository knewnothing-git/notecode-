# NoteCode++ for Android

<p align="center">
  <strong>A desktop-class text and source code editor engineered specifically for Android devices.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform Android" />
  <img src="https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
  <img src="https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" alt="Jetpack Compose" />
  <img src="https://img.shields.io/badge/Design-Material%203-FF7043?style=for-the-badge" alt="Material 3" />
  <img src="https://img.shields.io/badge/Privacy-100%25%20Offline-00C853?style=for-the-badge" alt="Offline Privacy" />
</p>

---

## Overview

**NoteCode++** brings the productivity and classic developer workflow of desktop code editors like Notepad++ to Android. Built 100% natively using **Jetpack Compose** and **Material Design 3**, NoteCode++ is designed for programmers, sysadmins, writers, and power users who need a robust, privacy-first code editor on phones and tablets.

---

## ✨ Features

### 📑 Multi-Tab Workspace
- Open, edit, and switch between multiple documents seamlessly.
- Visual dirty/modified indicators (`*`) and tab close affordances.
- New tab creation, document renaming, and quick document switching.

### 🎨 Syntax Highlighting & Themes
- Built-in lexical syntax highlighters for **12+ programming and markup languages**:
  - **Kotlin, Java, Python, JavaScript, TypeScript, C, C++, HTML, CSS, JSON, Markdown, XML, SQL, Shell, Plain Text**.
- **8 Themes** including:
  - *Notepad++ Classic*
  - *Modern Dark*
  - *Monokai*
  - *Dracula*
  - *Solarized Dark*
  - *Solarized Light*
  - *Nord*
  - *GitHub Light*

### 📁 Real Android Storage Integration
- Uses Android's **Storage Access Framework (SAF)** to open and save files from:
  - Internal device storage
  - MicroSD cards
  - USB OTG drives
  - Cloud storage providers (Google Drive, Nextcloud, OneDrive)

### 🔍 Find & Replace
- Fast in-editor search with real-time match highlighting.
- Next / Previous match navigation.
- **Match Case** (case-sensitive), **Whole Word**, and **Regex (Regular Expression)** search support.
- One-by-one or replace-all operations.

### ⚡ Developer & Line Utilities
- **Sort Lines**: Sort selected or document lines ascending (A-Z) or descending (Z-A).
- **Remove Duplicate Lines**: Instant deduplication for lists and configs.
- **Trim Whitespace**: Clean leading and trailing spaces across all lines.
- **Join Lines / Reverse Lines**: Invert or collapse text structures.
- **Line Operations**: Duplicate line, delete line, toggle comment syntax, and insert timestamps.
- **Bookmarks**: Set line bookmarks, jump between next/previous bookmarks, and clear all.
- **Go to Line**: Instant navigation to any line number.
- **Case Conversion**: UPPERCASE, lowercase, Title Case, camelCase, snake_case, and kebab-case.

### 📊 Telemetry & Document Statistics
- Real-time status bar showing cursor line, column number, character selection length, total characters, and line count.
- Detailed **Document Statistics Dialog**: word count, character count (with and without whitespace), and formatted file size in bytes/KB/MB.
- Line ending detection and switching: **Unix (LF)** vs. **Windows (CRLF)**.
- Character encoding handling: **UTF-8** and **ANSI (Windows-1252)**.

### 🌐 Live HTML & Markdown Preview
- Built-in previewer to render Markdown and live HTML/CSS output directly inside the app.

### 🔒 100% Offline & Private
- Zero telemetry, zero analytics tracking, and zero advertising SDKs.
- Operates entirely locally on your device with no required account or subscription paywall.

---

## 📱 APK Download & Installation

The compiled debug APK is included directly in this repository:

- **Location**: [`apk/notecode-plus-plus-debug.apk`](apk/notecode-plus-plus-debug.apk)

### How to Install:
1. Download `notecode-plus-plus-debug.apk` directly to your Android device or clone this repo.
2. In Android Settings, ensure **Install unknown apps** is permitted for your browser or file manager.
3. Tap the downloaded `.apk` file and select **Install**.

*Alternatively, install via ADB:*
```bash
adb install apk/notecode-plus-plus-debug.apk
```

---

## 🛠️ Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/) (100%)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material Design 3
- **Architecture**: MVVM (Model-View-ViewModel) + Unidirectional Data Flow (UDF)
- **Asynchronous Flow**: Kotlin Coroutines & `StateFlow`
- **File Access**: Android Storage Access Framework (SAF) `OpenDocument` & `CreateDocument`
- **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`) and Version Catalogs (`libs.versions.toml`)

---

## 🚀 Building from Source

### Prerequisites
- **Android Studio**: Ladybug / Meerkat or newer
- **JDK**: Version 17+
- **Android SDK**: Compile SDK 34, Min SDK 26

### Clone & Build
```bash
# Clone the repository
git clone https://github.com/your-username/notecode-android.git
cd notecode-android

# Build Debug APK
./gradlew assembleDebug

# The APK will be generated at:
# app/build/outputs/apk/debug/app-debug.apk
```

---

## 📂 Project Structure

```
├── apk/
│   └── notecode-plus-plus-debug.apk   # Pre-compiled ready-to-install APK
├── app/
│   ├── src/main/
│   │   ├── java/com/example/
│   │   │   ├── data/model/            # Document entities & state models
│   │   │   ├── ui/
│   │   │   │   ├── components/        # Canvas, Toolbars, Dialogs, Status & Tab Bars
│   │   │   │   ├── screen/            # Main NotepadScreen Compose view
│   │   │   │   ├── syntax/            # Lexer & Syntax Highlighter engine
│   │   │   │   ├── theme/             # Editor theme palettes & M3 theming
│   │   │   │   └── viewmodel/         # EditorViewModel & state managers
│   │   │   └── MainActivity.kt        # Main single-activity entry point
│   │   └── res/                       # Vector assets, launcher icons, strings
│   └── build.gradle.kts
├── gradle/
│   └── libs.versions.toml             # Gradle Version Catalog
└── README.md
```

---

## 📄 License

This project is open-source under the [Apache 2.0 License](LICENSE).
Feel free to fork, contribute, and adapt it for your own developer tool needs!
