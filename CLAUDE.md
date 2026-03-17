# Compose Roulette by Artificialss

## Overview
A Compose Multiplatform roulette wheel UI library. Pure Canvas-drawn, zero third-party dependencies beyond Compose Foundation. Works on Android, iOS, Web (WasmJS/JS), and Desktop (JVM).

## Package
`com.artificialss.roulette`

## Targets
Android, iOS (arm64 + simulatorArm64), JS, WasmJS, JVM

## Tech Stack
| Layer | Version |
|---|---|
| Kotlin | 2.3.0 |
| Compose Multiplatform | 1.10.0 |
| Build | Gradle with Kotlin DSL |

## Project Structure
```
ComposeRoulette/
├── roulette/                    # Library module (publishable)
│   └── src/commonMain/kotlin/com/artificialss/roulette/
│       ├── RouletteConfig.kt    # RouletteSegment, RouletteConfig data classes
│       ├── RouletteState.kt     # Spin logic, weighted random, animation state
│       └── RouletteWheel.kt     # Canvas-drawn wheel composable + pointer
├── demo/                        # Test app (WasmJS)
│   └── src/wasmJsMain/
│       ├── kotlin/.../Main.kt   # Demo with 8 segments, spin button, result text
│       └── resources/index.html # Web host page
└── CLAUDE.md
```

## Run the Demo
```bash
./gradlew :demo:wasmJsBrowserDevelopmentRun
```
Opens at `localhost:8080` — click SPIN to test.

## Library API

### Quick Start
```kotlin
implementation("com.artificialss:roulette:1.0.0")

val config = RouletteConfig(
    segments = listOf(
        RouletteSegment("Prize A", Color.Green, weight = 10.0),
        RouletteSegment("Prize B", Color.Red, weight = 5.0),
        RouletteSegment("Try Again", Color.Gray, weight = 85.0),
    )
)
val state = rememberRouletteState(config)
val scope = rememberCoroutineScope()

RouletteWheel(state = state, size = 300.dp)

Button(onClick = { scope.launch { val result = state.spin() } }) {
    Text("SPIN")
}
```

### Public API
| Class | Purpose |
|---|---|
| `RouletteSegment` | Single prize: label, color, weight, optional icon |
| `RouletteConfig` | Wheel config: segments, spin duration, colors, border |
| `RouletteState` | Holds spin animation, `spin()` suspend function, `lastResult` |
| `RouletteWheel` | The composable — pass state + size |
| `rememberRouletteState()` | Compose factory for RouletteState |

### Design Rules
- **Pure Canvas** — no Material dependencies in the library module, no images, no fonts
- **Zero external deps** — only `compose.runtime`, `compose.foundation`, `compose.ui`
- **Configurable everything** — colors, sizes, weights, durations via `RouletteConfig`
- **Weighted random** — `RouletteSegment.weight` controls probability (higher = more likely)
- **Single responsibility** — library draws the wheel, consumer handles game logic (spins per day, prize fulfillment, etc.)

### Conventions
- All drawing in `RouletteWheel.kt` via `DrawScope`
- State management in `RouletteState.kt` via `Animatable`
- No hardcoded colors — everything comes from `RouletteConfig`
- Keep the library module free of Material 3 — demo can use it
