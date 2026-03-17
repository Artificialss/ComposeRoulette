# Compose Roulette by Artificialss

## Overview
A Compose Multiplatform roulette wheel UI library. Pure Canvas-drawn, zero third-party dependencies beyond Compose. Works on Android, iOS, Web (WasmJS/JS), and Desktop (JVM).

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
│       ├── RouletteConfig.kt    # Prize, RouletteStyle data classes
│       ├── RouletteState.kt     # Spin logic, equal-odds random, animation state
│       └── RouletteWheel.kt     # Canvas-drawn wheel composables + pointer
├── demo/                        # Test app (WasmJS) — both wheel variants + spin buttons
│   └── src/wasmJsMain/
│       ├── kotlin/.../Main.kt   # Both wheel variants, responsive layout
│       └── resources/index.html # Web host page
└── CLAUDE.md
```

## Run the Demo
```bash
./gradlew :demo:wasmJsBrowserDevelopmentRun
```
Opens at `localhost:8080` — click SPIN to test.

## Library API

### Public API
| Class | Purpose |
|---|---|
| `Prize` | Single prize: id, name, description, color, textColor, optional icon composable, tryAgain flag |
| `RouletteStyle` | Wheel visual config: borderColor, pointerColor, backgroundColor, centerColor, spinDurationMs |
| `RouletteState` | Holds spin animation, `spin()` (random) / `spin(winnerIndex)` (pre-selected), `isSpinning`, `lastWinnerIndex` |
| `RouletteWheel` | Composable with icons — icon + name + description per segment |
| `RouletteWheelSimple` | Composable text-only — name + description per segment |
| `rememberRouletteState()` | Compose factory for RouletteState |

### Design Rules
- **Pure Canvas** — all drawing via `DrawScope`, no images, no fonts
- **Zero external deps** — only `compose.runtime`, `compose.foundation`, `compose.ui`, `compose.material3`
- **Equal probability** — all prizes have equal odds, no weighting
- **Pre-selected prize** — `spin(winnerIndex)` lets the backend decide the winner before the wheel spins
- **Auto-adaptive** — text and icons scale with segment count (4/8/12+ breakpoints) and wheel size
- **Labels auto-flip** — text never renders upside down
- **Single responsibility** — library draws the wheel, consumer handles game logic

### Conventions
- All drawing in `RouletteWheel.kt` via `DrawScope`
- State management in `RouletteState.kt` via `Animatable`
- No hardcoded colors — everything comes from `Prize` and `RouletteStyle`

## Publishing
- **Group**: `com.github.Artificialss`
- **Artifact**: `ComposeRoulette`
- **JitPack**: https://jitpack.io/#Artificialss/ComposeRoulette
- **Maven Local**: `./gradlew :roulette:publishToMavenLocal`
