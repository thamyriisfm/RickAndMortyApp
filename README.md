# Rick & Morty App 🛸

My first Android application built with **Jetpack Compose** that consumes the [Rick and Morty API](https://rickandmortyapi.com/) to list, search, filter and save favourite characters.

---

## Features

- 📋 **Character list** with infinite scroll pagination
- 🔍 **Search** by character name with debounce
- 🎛️ **Filters** by status and gender
- ❤️ **Favourites** — save characters locally with Room
- 🔃 **Sort favourites** by name, status or species
- 📶 **Offline mode** — automatically switches to favourites when there is no internet connection
- 🌙 **Dark / Light theme** support

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose, Material 3 |
| Navigation | Navigation Compose (type-safe routes) |
| Architecture | MVVM + Repository pattern |
| Networking | Ktor |
| Local storage | Room |
| Dependency injection | Koin |
| Image loading | Coil |
| Serialization | Kotlinx Serialization |
| Async | Kotlin Coroutines + StateFlow |

---

## Architecture

The project follows **MVVM** with a clear separation of concerns across layers:

```
app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── model/          # Domain models (CharacterRaM, CharacterResponse…)
│   ├── network/        # Ktor client, API service, config
│   └── repository/     # Repository interfaces and implementations
├── di/                 # Koin modules
├── extensions/         # Mapper extensions (CharacterRaM → CharacterDisplayModel)
├── core/               # Shared types (UiState)
├── ui/
│   ├── components/     # Shared composables (CharacterListScreen, ListItem…)
│   ├── home/           # Home screen + HomeViewModel
│   ├── details/        # Detail screen + DetailsViewModel
│   ├── favourites/     # Favourites screen + FavouritesViewModel
│   ├── navigation/     # AppDestinations, AppNavGraph
│   └── theme/          # Colours, typography, theme

rickandmorty_core_ui/   # Independent UI module
├── CharacterDisplayModel.kt
├── CharacterFilter.kt
├── SortOption.kt
├── CharacterListScreen.kt
├── ListItem.kt
├── FilterBottomSheet.kt
└── NoInternetBanner.kt
```

### Key design decisions

**UI Model separation** — `CharacterRaM` is the domain model returned by the API. `CharacterDisplayModel` lives in `:rickandmorty_core_ui` and is what the UI consumes. A mapper extension converts between the two, keeping the UI module free of domain dependencies.

**Generic UiState** — a single `sealed class UiState<T>` with `Loading`, `Success`, `Empty` and `Error` states is used across all ViewModels, avoiding duplication.

**Offline-first favourites** — when connectivity is lost, the app automatically enables the favourites filter and shows only locally stored characters. Favourites are persisted in a Room database.

---

## Module structure

```
:app                    → main application module
:rickandmorty_core_ui   → Android library with shared UI components
```

The `:rickandmorty_core_ui` module has no dependency on `:app`. It only knows about Compose, Material 3 and Coil — making it reusable and independently testable.

---

## Getting started

### Prerequisites

- Android Studio Hedgehog or later
- JDK 17
- Android SDK 24+

### Clone and run

```bash
git clone https://github.com/your-username/RickAndMortyApp.git
cd RickAndMortyApp
```

Open in Android Studio and run the `app` configuration on an emulator or physical device.

No API key is required — the [Rick and Morty API](https://rickandmortyapi.com/) is public and free.

---

## Testing

The project includes **unit tests** for all ViewModels using fakes instead of mocks for repositories.

```bash
./gradlew test
```

### Test tooling

| Tool | Purpose |
|---|---|
| JUnit 4 | Test runner |
| MockK | Mocking `ConnectivityObserver` and `android.util.Log` |
| Turbine | Testing `StateFlow` emissions |
| kotlinx-coroutines-test | Virtual time and `TestDispatcher` |

### Test coverage

| ViewModel | Tests |
|---|---|
| `HomeViewModel` | Initial load, search, filters, connectivity, pagination, error states |
| `FavouritesViewModel` | Add/remove, search, filters, sorting, combined filters |

---

## API

This app uses the public [Rick and Morty REST API](https://rickandmortyapi.com/documentation).

### Endpoints used

| Endpoint | Description |
|---|---|
| `GET /character` | Paginated list of characters |
| `GET /character?page=N` | Specific page |
| `GET /character?name=X` | Filter by name |
| `GET /character?status=X&gender=Y` | Filter by status and gender |

---

## Project conventions

- **Naming** — ViewModels suffix with `ViewModel`, repositories with `Repository`, interfaces with `RepositoryInterface`
- **Packages** — feature-based under `ui/` (home, details, favourites, navigation)
- **Constants** — string constants centralised in `utils/` to avoid hardcoded strings
- **Dispatchers** — IO operations (`withContext(Dispatchers.IO)`) are handled at the repository layer
