# 🗺️ Project Roadmap

## Milestone 1: Foundation & Connectivity
*Goal: Establish a stable connection between Client and Server with basic authentication.*

- [x] **1.1 Database Infrastructure (Server)**
    - [x] Implement a database abstraction layer (Repository Pattern).
    - [x] Configure **SQLite** as the primary database.
    - [x] Implement **PostgreSQL** as a configurable database via config or environment variable (`DATABASE_TYPE=postgresql`).
- [ ] **1.2 Identity & Authentication**
    - [ ] Define User entity (Username, UUID, Password Hashing).
    - [ ] Setup JWT (JSON Web Token) infrastructure for secure API access.
    - [ ] Create REST endpoints for User Registration and Login.
- [ ] **1.3 Connection Flow (Client)**
    - [ ] Implement "Connect to Server" screen (IP/Domain entry).
    - [ ] Server availability check & persistence of known servers (TeamSpeak-style).
    - [ ] Basic Login/Register UI in the Native Client.

## Milestone 2: Messaging & Channel Structure
*Goal: Real-time text communication in channels and private messages.*

- [ ] **2.1 WebSocket Infrastructure**
    - [ ] Setup WebSocket server within the Quarkus backend.
    - [ ] Shared-Lib: Define `@Serializable` packet types (Text, Image, System).
- [ ] **2.2 Channel Management**
    - [ ] Backend logic for creating and managing channels (Text vs. Voice).
    - [ ] Message persistence with pagination (fetching chat history).
- [ ] **2.3 Direct Messaging (DMs)**
    - [ ] Logic for 1-on-1 private conversations.
    - [ ] Real-time notifications for incoming DMs.

## Milestone 3: Advanced Roles & Permissions
*Goal: A granular permission system for server administration.*

- [ ] **3.1 Global Role System**
    - [ ] Implementation of roles (e.g., Admin, Moderator, Member).
    - [ ] Permission flags (e.g., `MANAGE_CHANNELS`, `KICK_MEMBERS`).
- [ ] **3.2 Channel Overrides**
    - [ ] Per-channel permission overwrites (similar to Discord).
    - [ ] Voice-specific constraints (e.g., `MAX_VIDEO_RESOLUTION`, `CAN_STREAM`).
- [ ] **3.3 Onboarding & Rules**
    - [ ] "Accept Rules" workflow for new members.
    - [ ] Dynamic visibility of channels based on onboarding status.

## Milestone 4: Voice, Video & WebRTC
*Goal: Low-latency voice communication and screensharing.*

- [ ] **4.1 WebRTC Signaling**
    - [ ] Implement signaling logic in the backend to broker P2P connections.
- [ ] **4.2 Voice Engine (Native Client)**
    - [ ] Integration of audio Input/Output handling.
    - [ ] Visual indicators for active speakers.
- [ ] **4.3 Video & Screensharing**
    - [ ] Peer-to-peer video streaming.
    - [ ] Role-based enforcement of stream quality and resolution.

## Milestone 5: Deployment & Public API
*Goal: Ease of use for self-hosters and third-party developers.*

- [ ] **5.1 Dockerization**
    - [ ] Create optimized Docker images (Quarkus Native executable).
    - [ ] Provide `docker-compose.yml` templates for one-click deployment.
- [ ] **5.2 Developer Ecosystem**
    - [ ] Exposure of a public API for 3rd-party clients.
    - [ ] Generate OpenAPI/Swagger documentation.
- [ ] **5.3 Web Client Parity**
    - [ ] Bring core features (Chat/Voice) to the Web-Client module.