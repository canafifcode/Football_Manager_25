# Football Manager 25

A desktop football club-management game built with **JavaFX**. Sign up, pick a club from one of six leagues, and build your dream squad by buying and selling players on a live, multiplayer transfer market — all changes sync in real time across every connected client over a custom TCP protocol.

> Built as a Level-1, Term-2 university project.

## Features

- **Account system** — sign up with a username/password and claim one club (no two users can own the same club).
- **Squad browser** — view your current squad with player stats, positions, and cards.
- **Transfer market** — search available players by name, club, league, or position and buy them for your team.
- **Live sell requests** — list your own players for sale; every other connected manager sees the listing appear instantly, and it disappears the moment someone buys it.
- **Real-time multiplayer** — a TCP socket server broadcasts every transfer to all connected clients, so squads and the transfer list stay in sync without refreshing.
- **Balance & pricing** — each club starts with a transfer budget; player prices scale with overall rating, and selling refunds 80% of market value.

## Tech Stack

- Java 21
- JavaFX 21 (controls + FXML)
- Plain Java TCP sockets (`java.net.Socket` / `ServerSocket`) with Java object serialization for the client-server protocol
- Maven (`pom.xml`) for dependency/build configuration

## Architecture

The app is a classic client-server setup:

- **`Server`** (`com.example.fm25.Server.Server`) listens on port `7564`, accepts client connections, and keeps the authoritative in-memory transfer list.
- **`ClientHandler`** processes each connected client's `BUY`/`SELL` requests, validates them (ownership, duplicate listings, etc.), updates `players.txt`, and broadcasts the updated transfer list to every connected client.
- **`BuyRequestClient`** (client side) sends requests and listens for server broadcasts, auto-reconnecting if the connection drops.
- **JavaFX controllers** (`controller` package) render the UI and react to live updates pushed from the server — no polling or shared files between users.

Player and user data (`players.txt`, `users.txt`) are simple flat files used as the data store; only the *client-to-client* transfer communication happens over the network.

## Getting Started

### Prerequisites

- JDK 21+
- Maven (or use the bundled wrapper scripts below)

### Build & Run (Maven)

```bash
# 1. Start the transfer-market server (run once, keep it running)
mvn compile exec:java -Dexec.mainClass="com.example.fm25.Server.Server"

# 2. In a separate terminal, launch a client
mvn javafx:run
```

Launch the client multiple times (or on multiple machines pointed at the server's host) to simulate several managers trading with each other live.

### Build & Run (Windows helper scripts)

If you don't have a JDK/Maven set up globally, the repo includes ready-to-use scripts (edit the `JDK` path inside them to point at your own JDK 21 install):

```bat
build.cmd         REM compiles sources into out\ and copies resources
run-server.cmd    REM starts the transfer-market server on port 7564
run-client.cmd    REM launches a game window (run once per player)
```

Always start `run-server.cmd` first, then one or more `run-client.cmd` instances.

## Project Structure

```
src/main/java/com/example/fm25/
├── Server/            # TCP server, per-client handler, connection info
├── util/NetWorkUtil.java   # Object stream read/write helper
├── Loader/            # Player/user data models and BuySell game logic
├── controller/         # JavaFX FXML controllers (sign in/up, home, market, etc.)
├── BuyRequestClient.java  # Client-side network connection
└── NetworkContext.java    # Holds the current session's socket/client/user

src/main/resources/com/example/fm25/   # FXML views, CSS, images
src/main/resources/logos/              # Club crests
src/main/resources/card/               # Player cards
players.txt / users.txt                # Flat-file data store
```

## Known Limitations

- `players.txt` and `users.txt` are plain text files, not a real database — fine for local/LAN play, not for production use.
- Server and clients are expected to run against the same `players.txt` (i.e., same machine or shared filesystem); only transfer-market communication is fully networked.
- No encryption/auth on the socket connection — intended for trusted local networks only.

## License

No license specified yet — all rights reserved by the author unless stated otherwise.
