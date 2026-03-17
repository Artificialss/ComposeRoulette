# 🎰 Compose Roulette

**A spin-to-win roulette wheel for Compose Multiplatform.**
Pure Canvas. No images. No third-party libs. Just vibes and equal-odds randomness.

> *"You miss 100% of the spins you don't take."* — Wayne Gretzky, probably

![Kotlin](https://img.shields.io/badge/Kotlin-2.3.0-purple?logo=kotlin)
![Compose](https://img.shields.io/badge/Compose-1.10.0-blue?logo=jetpackcompose)
![Platforms](https://img.shields.io/badge/Platforms-Android%20%7C%20iOS%20%7C%20Web%20%7C%20Desktop-green)

---

## Two Flavors

### `RouletteWheel` — with icons
Each segment shows: **icon** (left) + **name** (bold, 1 line) + **description** (2 lines max).
If you don't pass an icon, you get a default star. You're welcome.

### `RouletteWheelSimple` — text only
Same `Prize` object, icon field ignored. Just centered bold name + description.
For when you want the wheel to feel *classy*.

---

## Quick Start

```kotlin
// 1. Define your prizes
val prizes = listOf(
    Prize("Premium Forever", "Lifetime access", Color.Gold, icon = { CrownIcon() }),
    Prize("7-Day Trial", "Try premium free", Color.Green),
    Prize("50 Coins", "Bonus credits", Color.Purple),
    Prize("Try Again", "Better luck next time", Color.DarkGray),
)

// 2. Create state
val state = rememberRouletteState(prizes.size)
val scope = rememberCoroutineScope()

// 3. Render — wheel adapts to available space (15dp padding auto)
RouletteWheel(
    prizes = prizes,
    state = state,
    onResult = { prize -> println("Won: ${prize.name}") },
    modifier = Modifier.fillMaxWidth().height(320.dp)
)

// 4. Spin
Button(onClick = {
    scope.launch {
        val winnerIndex = state.spin()
        // Do something with prizes[winnerIndex]
    }
}) { Text("SPIN") }
```

---

## The `Prize` Object

Every prize on the wheel uses the same data class — both wheel variants accept it.

```kotlin
data class Prize(
    val name: String,           // Bold title, 1 line max, ellipsis if too long
    val description: String,    // Subtitle, 2 lines max, ellipsis if too long
    val color: Color,           // Segment background color
    val textColor: Color,       // Text color (default: White)
    val icon: (@Composable () -> Unit)?  // Optional icon composable (null = default star)
)
```

| Field | Required | Notes |
|-------|----------|-------|
| `name` | Yes | 1 line, bold, ellipsis overflow |
| `description` | No | 2 lines max, ellipsis overflow, 70% text alpha |
| `color` | Yes | Background of the segment slice |
| `textColor` | No | Default `Color.White` |
| `icon` | No | Any composable. Null = default star icon. Ignored by `RouletteWheelSimple` |

**All prizes have equal probability. Always. No weighting.**
The wheel is fair. Your odds are `1/N` where N = number of prizes.

---

## The Two Composables

### `RouletteWheel` (with icons)

```kotlin
@Composable
fun RouletteWheel(
    prizes: List<Prize>,        // Min 1. Equal odds.
    state: RouletteState,       // From rememberRouletteState()
    onResult: (Prize) -> Unit,  // Called with the winner after spin
    style: RouletteStyle,       // Colors + spin duration
    modifier: Modifier          // Wheel fills available space with 15dp padding
)
```

### `RouletteWheelSimple` (text only)

```kotlin
@Composable
fun RouletteWheelSimple(
    prizes: List<Prize>,        // Same Prize object — icon field ignored
    state: RouletteState,
    onResult: (Prize) -> Unit,
    style: RouletteStyle,
    modifier: Modifier
)
```

**Both auto-adapt:**
- Wheel fills the smaller dimension of the available space
- 15dp padding around the wheel
- Text and icons shrink based on number of segments (4/8/12+ breakpoints)
- Name: 1 line with ellipsis. Description: 2 lines with ellipsis. Never cut.

---

## `RouletteState` — Spin Logic

```kotlin
// Basic: random selection (equal odds)
val state = rememberRouletteState(prizes.size)

// With custom spin duration
val state = rememberRouletteState(prizes.size, spinDurationMs = 6000)

// With server-side selection — wheel waits for your function to return
val state = rememberRouletteState(
    prizeCount = prizes.size,
    selector = { count ->
        // This suspend function runs BEFORE the wheel knows where to land.
        // Perfect for: server calls, custom logic, rigged outcomes (we don't judge).
        val response = api.pickWinner(userId)
        response.prizeIndex  // return 0..count-1
    }
)
```

| Property | Type | Description |
|----------|------|-------------|
| `state.isSpinning` | `Boolean` | True while wheel is animating |
| `state.lastWinnerIndex` | `Int` | Index of last winner (-1 if never spun) |
| `state.spin()` | `suspend fun` | Spins, returns winner index |

### The `selector` Parameter

If you pass `selector = null` (default), the wheel picks randomly with equal odds.

If you pass a `suspend` function, the wheel calls it to get the winner index. The function can do anything — call a server, check a database, ask a magic 8-ball. The wheel starts spinning immediately and lands on whatever index you return.

```kotlin
// Example: server decides the prize
val state = rememberRouletteState(prizes.size) { count ->
    val result = myApi.getSpinResult(userId)
    result.winnerIndex
}
```

---

## `RouletteStyle` — Customization

```kotlin
data class RouletteStyle(
    val borderColor: Color,      // Outer ring + center hub border
    val pointerColor: Color,     // Triangle pointer at top
    val backgroundColor: Color,  // Behind the wheel
    val centerColor: Color,      // Center hub fill
    val spinDurationMs: Int      // Spin animation length (default 4000ms)
)
```

### Examples

```kotlin
// Gold casino
RouletteStyle(borderColor = Color(0xFFFFC84C), pointerColor = Color(0xFFFFC84C))

// Minimal dark
RouletteStyle(borderColor = Color(0xFF333333), pointerColor = Color.White, borderWidth = 2f)

// Party mode
RouletteStyle(borderColor = Color.Magenta, pointerColor = Color.Cyan, spinDurationMs = 1500)
```

---

## Edge Cases

| Scenario | Behavior |
|----------|----------|
| 1 prize | Whole wheel is one color. Sarcastic mode. Still spins. Still "wins". |
| 0 prizes | Nothing renders. Empty composable. |
| 20+ prizes | Segments get thin, text shrinks to fit, everything still readable with ellipsis. |

---

## Run the Demo

```bash
git clone https://github.com/ArtificialSS/ComposeRoulette.git
cd ComposeRoulette
./gradlew :demo:wasmJsBrowserDevelopmentRun
```

Demo shows both `RouletteWheel` and `RouletteWheelSimple` side-by-side (landscape) or stacked (portrait).

---

## Platform Support

| Platform | Status |
|----------|--------|
| Android | ✅ |
| iOS | ✅ |
| Web (WasmJS) | ✅ |
| Web (JS) | ✅ |
| Desktop (JVM) | ✅ |

---

## License

MIT — use it, ship it, spin it.

---

**Made with ☕ by [Artificialss](https://artificialss.ai)**
