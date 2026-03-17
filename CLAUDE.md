# Compose Roulette by Artificialss

## Overview
A Compose Multiplatform roulette wheel UI library. Pure Canvas-drawn, zero third-party dependencies beyond Compose. Works on Android, iOS, Web (WasmJS/JS), and Desktop (JVM).

## Package
`com.artificialss.roulette`

## Version
`1.0.2`

## Targets
Android, iOS (arm64 + simulatorArm64), JS, WasmJS, JVM

## Tech Stack
| Layer | Version |
|---|---|
| Kotlin | 2.3.0 |
| Compose Multiplatform | 1.10.0 |
| Android compileSdk | 36 (minSdk 24) |
| Build | Gradle with Kotlin DSL |

## Project Structure
```
ComposeRoulette/
├── roulette/                       # Library module (publishable via JitPack)
│   ├── build.gradle.kts            # KMP targets + maven-publish + Android library
│   └── src/commonMain/kotlin/com/artificialss/roulette/
│       ├── RouletteConfig.kt       # Prize data class + RouletteStyle
│       ├── RouletteState.kt        # Spin logic: random or pre-selected winner
│       └── RouletteWheel.kt        # Canvas wheel + pointer + text overlay
├── demo/                           # Test app (WasmJS)
│   └── src/wasmJsMain/
│       ├── kotlin/.../Main.kt      # Both wheel variants, responsive, winner display
│       └── resources/index.html    # Web host
├── assets/
│   └── demo-screenshot.png         # README screenshot
├── CLAUDE.md                       # This file
└── README.md                       # Public docs with installation + API reference
```

## Installation

### JitPack (recommended)
```kotlin
// settings.gradle.kts
repositories { maven("https://jitpack.io") }

// build.gradle.kts — use the roulette submodule, not the root
commonMain.dependencies {
    implementation("com.github.Artificialss.ComposeRoulette:roulette:1.0.2")
}
```

### Composite Build (local dev)
```kotlin
// settings.gradle.kts
includeBuild("../ComposeRoulette") {
    dependencySubstitution {
        substitute(module("com.github.Artificialss.ComposeRoulette:roulette")).using(project(":roulette"))
    }
}
```

## Run the Demo
```bash
./gradlew :demo:wasmJsBrowserDevelopmentRun
```
Opens at `localhost:8080`.

## Library API

### Data Classes

```
Prize
├── id: String              # Unique ID for result handling
├── name: String            # Bold title (1 line, ellipsis)
├── description: String     # Subtitle (1 line, ellipsis, 60% alpha)
├── color: Color            # Segment background
├── textColor: Color        # Text color (default: White)
├── icon: (@Composable)?    # Optional icon composable (null = default star)
└── tryAgain: Boolean       # Signal consumer to allow re-spin (default: false)

RouletteStyle
├── borderColor: Color      # Outer ring + center hub border
├── pointerColor: Color     # Left-side pointer triangle
├── backgroundColor: Color  # Behind the wheel
├── centerColor: Color      # Center hub fill
└── spinDurationMs: Int     # Animation duration (default: 4000)
```

### Composables

| Function | Description |
|----------|-------------|
| `RouletteWheel(prizes, state, onResult, style, modifier)` | Wheel with icons — icon on top + name + description per segment |
| `RouletteWheelSimple(prizes, state, onResult, style, modifier)` | Text-only — name + description centered, icon field ignored |
| `rememberRouletteState(prizeCount, spinDurationMs)` | Creates and remembers spin state |

### State API

| Member | Description |
|--------|-------------|
| `state.spin(): Int` | Random equal-odds spin. Suspends until animation completes. Returns winner index. |
| `state.spin(winnerIndex: Int?): Int` | Pre-selected winner. Wheel animates to land on that exact segment. |
| `state.isSpinning: Boolean` | `true` during animation |
| `state.lastWinnerIndex: Int` | Last winner (-1 if never spun) |

### Spin Flow
```
Consumer calls state.spin()
  ├─ Random: picks winner with equal odds
  └─ Pre-selected: spin(winnerIndex = N)
       ↓
Wheel starts spinning (4-6 full rotations)
       ↓
EaseOutCubic deceleration → lands on winner segment
       ↓
Returns winner index → consumer checks prize.tryAgain
  ├─ tryAgain = true  → consumer re-enables spin button
  └─ tryAgain = false → consumer grants the prize
```

## Design Rules
- **Pure Canvas** — all drawing via `DrawScope`, no images, no custom fonts
- **Zero external deps** — only `compose.runtime`, `compose.foundation`, `compose.ui`, `compose.material3`
- **Equal probability** — all prizes have equal odds, no weighting system
- **Pre-selected prize** — `spin(winnerIndex)` for server-decided outcomes
- **Auto-adaptive** — fills available space with 15dp padding, text/icons scale with segment count
- **Labels auto-flip** — text never renders upside down (180° flip when angle > 90° and < 270°)
- **Pointer on left** — triangle points right from the left edge
- **Icon on top** — vertical layout: icon → name → description per segment
- **Single responsibility** — library draws + animates, consumer owns game logic

## Conventions
- All drawing in `RouletteWheel.kt` via `DrawScope`
- State management in `RouletteState.kt` via `Animatable`
- No hardcoded colors — everything from `Prize` and `RouletteStyle`
- Android target requires `android.useAndroidX=true` in `gradle.properties`

## Publishing
- **Group**: `com.github.Artificialss`
- **Artifact**: `ComposeRoulette:roulette`
- **JitPack**: https://jitpack.io/#Artificialss/ComposeRoulette
- **Maven Local**: `./gradlew :roulette:publishToMavenLocal`
- **Tag format**: `1.0.X` — JitPack builds from tags automatically
