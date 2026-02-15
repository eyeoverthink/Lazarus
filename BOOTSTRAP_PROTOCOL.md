# Bootstrap Protocol - Planetary Cortex Network Formation

## Overview

The Bootstrap Protocol enables Fraymus nodes to form a decentralized, self-organizing network for semantic routing and intelligent communication.

## Components

### 1. NeuroQuant (`fraymus/bio/NeuroQuant.java`)

Vector identity for nodes in 10,000-dimensional semantic space.

**Features:**
- Unique node ID (UUID-based)
- 10,000-dimensional normalized vector
- Cosine similarity calculation for semantic matching
- Serializable for network transmission

**Usage:**
```java
NeuroQuant identity = new NeuroQuant();
double similarity = identity.similarity(otherNode);
```

### 2. PlanetaryNode (`fraymus/net/PlanetaryNode.java`)

P2P network node - each node is both server and client.

**Features:**
- TCP/IP server for accepting connections
- Peer registration and management
- Vector identity management
- Concurrent peer table
- Genesis mode support

**Usage:**
```java
PlanetaryNode node = new PlanetaryNode(50000);
node.startServer();
node.registerPeer(identity, ipAddress);
```

### 3. PlanetaryBootstrap (`fraymus/net/PlanetaryBootstrap.java`)

Bootstrap protocol implementation with two modes.

**Genesis Mode** - Start as first node:
```java
PlanetaryBootstrap bootstrap = new PlanetaryBootstrap(node);
bootstrap.startAsGenesis();
```

**Join Mode** - Connect to existing network:
```java
PlanetaryBootstrap bootstrap = new PlanetaryBootstrap(node);
bootstrap.connectToSeed("192.168.1.100", 50000);
```

## Protocol Flow

### Genesis Mode

1. Node marks itself as genesis
2. Starts TCP server
3. Waits for connections
4. No peers initially - becomes network anchor

### Join Mode

1. **Handshake** - Connect to seed node via TCP
2. **Identity Exchange** - Send/receive NeuroQuant vectors
3. **Peer Sync** - Request complete peer table
4. **Registration** - Add all peers to local table
5. **Integration** - Node is now part of global brain

## Running the Demo

### Full Demo (Genesis + Multiple Joins)
```bash
java fraymus.net.BootstrapDemo
```

### Genesis Mode Only
```bash
java fraymus.net.BootstrapDemo genesis
```

### Join Mode Only
```bash
java fraymus.net.BootstrapDemo join <seed-ip> <seed-port>
```

Example:
```bash
# Terminal 1: Start genesis
java fraymus.net.BootstrapDemo genesis

# Terminal 2: Join network
java fraymus.net.BootstrapDemo join localhost 50000
```

## Network Formation

```
Genesis Node (Port 50000)
    ↓
Node 1 joins (Port 50001)
    ├─ Downloads: Genesis peer info
    └─ Registers: Genesis as peer
    ↓
Node 2 joins (Port 50002)
    ├─ Downloads: Genesis + Node 1
    └─ Registers: Both as peers
    ↓
Node N joins (Port 500XX)
    ├─ Downloads: All previous nodes
    └─ Registers: Complete peer table

Result: Fully connected network
```

## Features

- **Zero Configuration** - No central server required
- **Self-Organizing** - Automatic peer discovery
- **Semantic Routing** - Vector-based identity
- **Decentralized** - No single point of failure
- **Scalable** - O(1) join time
- **Resilient** - Gossip-based peer sync

## Use Cases

1. **Distributed Research Network** - Experts finding experts
2. **Decentralized AI Training** - Compute resource discovery
3. **Knowledge Sharing** - Semantic content routing
4. **IoT Networks** - Device mesh formation
5. **Blockchain Consensus** - Node discovery

## Security Considerations

Current implementation is for demonstration. Production use should add:
- TLS/SSL encryption for connections
- Peer authentication
- Rate limiting
- DoS protection
- Peer verification

## Future Enhancements

- [ ] Encryption layer (AES-256)
- [ ] Anonymous routing
- [ ] NAT traversal
- [ ] IPv6 support
- [ ] Peer reputation system
- [ ] Dynamic port allocation
- [ ] Multi-network support

## API Reference

### NeuroQuant
- `NeuroQuant()` - Create with random vector
- `double similarity(NeuroQuant other)` - Calculate cosine similarity
- `double[] getVector()` - Get vector copy
- `String id` - Unique node identifier

### PlanetaryNode
- `PlanetaryNode(int port)` - Create node on port
- `void startServer()` - Start TCP server
- `void stopServer()` - Stop server
- `void registerPeer(NeuroQuant, String)` - Register peer
- `int getPeerCount()` - Get number of peers
- `Map<String, PeerInfo> collectPeerTable()` - Get peer table

### PlanetaryBootstrap
- `void startAsGenesis()` - Genesis mode
- `void connectToSeed(String ip, int port)` - Join mode

## Architecture

```
┌─────────────────────────────────────┐
│      Planetary Cortex Network       │
├─────────────────────────────────────┤
│                                     │
│  ┌─────────┐      ┌─────────┐      │
│  │  Node   │◄────►│  Node   │      │
│  │ (Seed)  │      │  (Peer) │      │
│  └─────────┘      └─────────┘      │
│       ▲                 ▲           │
│       │                 │           │
│       ▼                 ▼           │
│  ┌─────────┐      ┌─────────┐      │
│  │  Node   │◄────►│  Node   │      │
│  │ (Peer)  │      │  (Peer) │      │
│  └─────────┘      └─────────┘      │
│                                     │
└─────────────────────────────────────┘
```

Each node:
- Has unique vector identity (NeuroQuant)
- Maintains peer table
- Routes by semantic similarity
- Self-organizes topology

## License

Part of the Lazarus/Fraynix project.
