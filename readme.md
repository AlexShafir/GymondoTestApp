# Gymondo Test App

Dear Gymondo team, here is the case study prepared according to the provided requirements file.

Selected API: https://picsum.photos/

I have added Fetch button for convenience.

## Tech Stack

- **Framework:** Jetpack Compose with Flow
- **Architecture:** Standard Android architecture, organized into:
    - `app` (UI layer)
    - `data` (Data layer)

## Design

The UI is implemented using **Material 3** guidelines:  
[https://m3.material.io/](https://m3.material.io/)

## Testing

Three tests were implemented:

- **`PicsumApiTest`** – Verifies the correctness of `PicsumRepository`
- **`MainScreenVMTest`** – Verifies the correctness  of `MainScreenViewModel`
- **`MainScreenTest`** – Verifies the correctness of `MainScreen` UI
