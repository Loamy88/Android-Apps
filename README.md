# Countdown to Worlds – Android Widget

A 4×1 home screen widget that counts down to **April 28** with days, hours, minutes, and seconds.

## Build Instructions

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 26+
- Kotlin 1.9+

### Steps
1. Unzip the project folder
2. Open **Android Studio** → *Open* → select the `CountdownToWorlds` folder
3. Let Gradle sync finish (may take a minute on first run)
4. Connect an Android device or start an emulator (API 26+)
5. Click **Run ▶** or press `Shift+F10`

### Adding the Widget to Your Home Screen
1. Long-press on an empty area of your Android home screen
2. Tap **Widgets**
3. Find **Countdown to Worlds** and drag it onto the home screen
4. The widget takes a **4×1** slot

## Features
- 🏆 Counts down to April 28 (auto-advances to next year if date has passed)
- Updates every 15 minutes in the background
- Deep navy blue gradient design
- Shows Days / Hrs / Min / Sec
- Displays a trophy celebration when the day arrives

## Project Structure
```
app/src/main/
├── java/com/countdown/worlds/
│   ├── CountdownWidget.kt   # AppWidgetProvider logic
│   └── MainActivity.kt      # Launcher activity
├── res/
│   ├── layout/
│   │   ├── widget_countdown.xml   # 4×1 widget layout
│   │   └── activity_main.xml      # App open screen
│   ├── xml/countdown_widget_info.xml  # Widget metadata (size, update rate)
│   ├── drawable/widget_background.xml # Navy gradient background
│   └── mipmap-*/ic_launcher*.png      # App icons
└── AndroidManifest.xml
```
