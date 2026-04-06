# Palodon

Palodon is a secure, self-hosted chat platform designed for small groups of friends. It provides a Discord-like experience, including text chat, voice calls, video calls, and screensharing, with a primary focus on usability, security, and robust encryption.

## Future Features

- **Self-Hosted:** Full control over your data and server environment.
- **Server Channels:** Create persistent voice and text channels accessible to everyone on the server.
- **Private Messaging:** Secure 1-to-1 messaging that remains private from other server members.
- **Rich Media:** Support for high-quality audio/video calls and screensharing.
- **Security First:** Built with modern encryption standards to ensure user privacy.

## Project Structure

This is a Kotlin Multiplatform (KMP) project consisting of several modules:

- **`server`**: The backend powered by Quarkus.
- **`web-client`**: The web-based frontend built with Kobweb and Compose HTML.
- **`native-client`**: Native applications for Android, iOS, and Desktop.
- **`shared-lib`**: Shared logic, models, and encryption utilities used by all modules.

## Developer Setup

The project is optimized for development using **IntelliJ IDEA**.

### Prerequisites

- **JDK 17** (or higher)
- **IntelliJ IDEA** (Ultimate or Community) with the Kotlin and Android plugins installed.

### Running the Modules

Pre-configured Run Configurations are provided in the `.idea` folder. You can find them in the "Run/Debug Configurations" dropdown at the top of IntelliJ.

#### 1. Web Client
To run the web client:
- Select the **`Web Client`** configuration and click **Run**.
- The web client will be available at `http://localhost:4200` by default.

#### 2. Quarkus Server
To run the api, which is needed by all the clients:
- Select the **`Server`** configuration and click **Run**.
- The server will be available at `http://localhost:8080` by default.
- This runs the Quarkus dev mode, supporting hot-reload for your API changes.

#### 3. Web (Fullstack)
To run both the web client and the server simultaneously:
- Select the **`Web Client [Fullstack]`** configuration and click **Run**.
- The web client will be available at `http://localhost:4200` by default.
- The server will be available at `http://localhost:8080` by default.

#### 4. Native Clients
For native development, use the following configurations depending on your target:
- **`Native Desktop`**: Runs the Compose-based desktop application.
- **`Native Android`**: Deploys the application to a connected Android device or emulator.
- **`Native iOS`**: (macOS only) Deploys to a connected iOS device or simulator.

### Testing
To run tests across the project, you can use the Gradle tool window or run:
```bash
./gradlew test
```
To run specific module tests (e.g., the Quarkus server):
```bash
./gradlew :server:test
```
