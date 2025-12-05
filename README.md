Veterinary Clinic App

A clean, modern Android app using MVVM · Clean Architecture · Jetpack Compose · Kotlin Flows · Hilt

<p align="center"> <img src="https://img.shields.io/badge/Android-Compose-brightgreen?logo=android&style=for-the-badge" /> <img src="https://img.shields.io/badge/Kotlin-Coroutines-blueviolet?logo=kotlin&style=for-the-badge" /> <img src="https://img.shields.io/badge/MVVM-Clean%20Architecture-orange?style=for-the-badge" /> <img src="https://img.shields.io/badge/Hilt-DI-blue?logo=dagger&style=for-the-badge" /> <img src="https://img.shields.io/badge/MockK-Testing-green?style=for-the-badge" /> </p>
📸 App Screenshots

Replace these with your actual screenshots once taken

<p align="center"> <img src="screenshots/home_light.png" width="32%" /> <img src="screenshots/home_dark.png" width="32%" /> <img src="screenshots/pet_detail.png" width="32%" /> </p>
📌 Overview

This mobile app is built for a small veterinary clinic to help users:

Contact the clinic via Chat or Call

View clinic Working Hours

Browse Pet information

Open pet details via WebView

Automatically detect if contacting the clinic is allowed based on current time

Load data from remote JSON, with fallback to local resources if unavailable

The assignment strictly follows Clean Architecture + MVVM + Jetpack Compose, with no external UI libraries.

🚀 Features
🟢 Contact Options (Chat & Call)

Dynamically enabled/disabled based on config.json

Shows correct alert depending on work hours
✔ Inside hours → “Thank you for getting in touch…”
✔ Outside hours → “Work hours has ended…”

📅 Working Hours Logic

Fully supports formats like:
"M-F 9:00 - 18:00"

Automatically reads user’s current day/time

Works across orientations & screen sizes

🐶 Pet Browser

Fetches list of pets from remote pets.json

Falls back to local JSON if network fails

Click on a pet → Opens web page in WebView

📡 Network Handling

Manual HTTP using HttpURLConnection

Handles:

2xx success

4xx/5xx failures

Timeouts

No internet

No 3rd-party networking libraries used.

🧰 Local Fallback

If remote JSON fails → loads from:

res/raw/config.json
res/raw/pets.json

🧼 Clean UI

Jetpack Compose

No extraneous code, print logs, or comments

Constraint-free flexible layout

Eliminates spacing when buttons are disabled

🏛️ Architecture
📐 Overall Architecture (Clean Architecture + MVVM)
presentation/
├── MainScreen.kt
├── MainViewModel.kt
└── components/
domain/
├── models/
└── repository/
data/
├── remote/HttpApiService.kt
├── local/LocalJsonLoader.kt
└── repository/VetRepositoryImpl.kt
di/
└── NetworkModule.kt   (Hilt)
util/
└── WorkHoursUtil.kt

🧩 Architecture Diagram
<p align="center"> <img src="https://raw.githubusercontent.com/github/explore/main/topics/architecture/architecture.png" width="480" /> </p>

Arrows: data flows downward, UI reacts upward.

🧪 Unit Testing

This project includes full test coverage using:

Component	Framework
ViewModel	JUnit4 + MockK + coroutines-test
WorkHoursUtil	JUnit4 + MockK (mock LocalDateTime.now)
Repository	MockK
HttpApiService	Fake HTTP + local loader tests
🧪 Example test types

Time-based testing
Mocking LocalDateTime.now()

Dispatcher testing
Using custom MainDispatcherRule

StateFlow testing
Verifying UiState transitions

Network fallback testing
Remote 404 → Local JSON fallback

🛠️ Tech Stack
Category	Technology
UI	Jetpack Compose
Language	Kotlin
State	StateFlow + MutableStateFlow
Async	Kotlin Coroutines
Dependency Injection	Hilt
Network	HttpURLConnection (no 3rd-party libs)
JSON	JSONObject / Manual parsing
Testing	JUnit4, MockK, Coroutines Test
Architecture	MVVM + Clean Architecture
📦 JSON File Formats
config.json
{
"settings": {
"isChatEnabled": true,
"isCallEnabled": true,
"workHours": "M-F 9:00 - 18:00"
}
}

pets.json
{
"pets": [
{
"title": "Dog",
"image_url": "...",
"content_url": "...",
"date_added": "2024-01-01"
}
]
}

⚙️ Setup & Installation
1️⃣ Clone
git clone <your-repo-url>

2️⃣ Add remote JSON URLs (temporary server)

Inside NetworkModule.kt:

@Provides fun provideConfigUrl() = "https://your-temp-hosting.com/config.json"
@Provides fun providePetsUrl()  = "https://your-temp-hosting.com/pets.json"

3️⃣ Build & Run

Open in Android Studio Hedgehog or later
Run on a device/emulator.

🎨 UI Examples

Add real screenshots later

<p align="center"> <img src="screenshots/contact_buttons.png" width="42%" /> <img src="screenshots/pet_list.png" width="42%" /> </p>
⚠️ Assignment Guidelines Complied With

✔ No 3rd-party UI / networking libraries
✔ SOLID principles
✔ Clean code, DRY, no duplication
✔ No commented/unwanted code
✔ No dead code
✔ UI matches provided wireframe
✔ Code fully testable
✔ No deep nesting
✔ Proper naming conventions
✔ Uses Kotlin flows instead of LiveData
✔ Manual image loading (no Coil/Glide)