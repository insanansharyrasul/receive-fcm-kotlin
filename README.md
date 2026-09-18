# receive_fcm_kotlin (Receive FCM Android Client)

An Android application built with **Kotlin** and **Jetpack Compose** demonstrating how to receive and handle **Firebase Cloud Messaging (FCM)** push notifications from a backend service (such as `send_fcm_nest` or any service utilizing the Firebase Admin SDK / HTTP v1 API).

---

## Tech Stack

- **Language**: Kotlin
- **UI Toolkit**: Jetpack Compose + Material 3
- **Push Notifications**: Firebase Cloud Messaging (`firebase-messaging`, Firebase BOM)
- **Analytics**: Firebase Analytics (`firebase-analytics`)
- **Build System**: Gradle (Kotlin DSL `.gradle.kts`) with Version Catalog (`libs.versions.toml`)
- **Target SDK**: Android 37 (Compile SDK: 37, Min SDK: 36)

---

## Prerequisites

- **Android Studio** (Ladybug / Meerkat or later recommended)
- **JDK 17+** (Java 11/17 source compatibility)
- An Android device or emulator running **Google APIs / Google Play Store**
- A **Firebase Project** configured in the [Firebase Console](https://console.firebase.google.com/)

---

## Getting Started

### 1. Clone the Repository
```bash
git clone <repository-url>
cd receive_fcm_kotlin
```

### 2. Add Firebase Configuration
1. Go to the [Firebase Console](https://console.firebase.google.com/) and open your project.
2. Register an Android app with the package name:
   ```text
   com.example.learn_fcm
   ```
3. Download `google-services.json`.
4. Place `google-services.json` inside the `app/` directory:
   ```text
   receive_fcm_kotlin/app/google-services.json
   ```

### 3. Build & Run
Open the project in Android Studio or build via Gradle:

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device/emulator
./gradlew installDebug
```

---

## Testing FCM Push Notifications

### 1. Retrieve the FCM Registration Token
When the app launches, grant notification permissions. The device token will be printed in **Logcat**:

```bash
adb logcat -s FCM
```

Look for a log line similar to:
```text
D/FCM: FCM token: eX4mPL3-t0kEn_AbCdEf123456...
```

### 2. Send a Test Message

#### Option A: Firebase Console (Notification Composer)
1. In the Firebase Console, navigate to **Engage** > **Messaging**.
2. Click **New campaign** > **Firebase Notification messages**.
3. Fill in the Notification Title and Text.
4. Click **Send test message**, paste the device registration token obtained from Logcat, and press **Test**.

#### Option B: Backend (e.g. `send_fcm_nest` / HTTP v1 API)
Send an HTTP POST request to the Firebase v1 endpoint or trigger your NestJS backend service with a JSON payload:

```json
{
  "message": {
    "token": "<YOUR_DEVICE_FCM_TOKEN>",
    "notification": {
      "title": "Hello from Backend!",
      "body": "This is a test push notification from send_fcm_nest."
    },
    "data": {
      "extra_info": "custom-value"
    }
  }
}
```

Or a data-only payload:

```json
{
  "message": {
    "token": "<YOUR_DEVICE_FCM_TOKEN>",
    "data": {
      "title": "Data Alert",
      "body": "Payload delivered entirely via data attributes."
    }
  }
}
```

---

## Notification Handling Details

- **App in Background / Killed**: Standard `notification` payloads are displayed automatically by Google Play Services system tray; clicking launches the app.
- **App in Foreground**: Intercepted by [`MyFirebaseMessagingService`](app/src/main/java/com/example/receive_fcm_kotlin/java/MyFirebaseMessagingService.kt) to construct and present a heads-up alert on the `fcm_default_channel` notification channel.
- **Data Messages**: Always delegated to `onMessageReceived` in `MyFirebaseMessagingService`, where titles and bodies are dynamically mapped and posted.

---

## License

This project is licensed under the MIT License - see the LICENSE file for details.