# Universal Interface System - Generation 195+

> *"Java commands, Ollama thinks, Transmudder feeds, HTML speaks, Git remembers."*

A comprehensive AI orchestration framework connecting Java, Ollama (local LLM), file processing, WebSocket UI, and Git-based persistence.

## 🎯 Overview

The Universal Interface is a complete AI system with multiple interconnected components:

- **Java (The Controller)**: Orchestrates all components
- **Ollama (The Brain)**: Local LLM for intelligence
- **Transmudder (The Soul)**: File ingestion pipeline
- **HTML (The Face)**: WebSocket-based chat UI
- **Git (The Scribe)**: Eternal memory through version control

## 🏗️ Architecture

### Core Components

1. **OllamaSpine.java** - Ollama API Bridge
   - Connects Java to local Ollama instance
   - Sends prompts with context
   - Parses JSON responses
   - Model-agnostic (works with llama3, mistral, etc.)

2. **Transmudder.java** - File Ingestion System
   - Reads files (PDF, TXT, etc.)
   - Cleans and compresses content
   - Builds context for AI
   - Tracks ingested files

3. **GenesisBlock.java** - Blockchain Memory Structure
   - Immutable memory blocks
   - SHA-256 hash chaining
   - Type classification (MEMORY, CODE, FACT, CONVERSATION)
   - Integrity verification

4. **Hippocampus.java** - Memory Chain Manager
   - Saves blocks to disk (.genesis files)
   - Loads previous state on startup
   - Verifies chain integrity
   - Manages memory/directory

5. **GitCortex.java** - Automated Git Operations
   - Auto-commits memory blocks
   - Pushes to repository
   - Cross-platform (Windows/Linux/Mac)
   - "Immortality through version control"

6. **FraymusChat.html** - WebSocket UI
   - Real-time chat interface
   - Matrix-style aesthetic
   - Special commands (TRANSMUTE, PURGE, STATUS)
   - Auto-reconnect

## 📦 Dependencies

### Required

- **Java 11+**
- **Ollama** (installed and running)
- **Git** (for GitCortex functionality)

### Optional (for WebSocket server)

- **Java-WebSocket library** (for SystemMain.java)
  ```xml
  <dependency>
      <groupId>org.java-websocket</groupId>
      <artifactId>Java-WebSocket</artifactId>
      <version>1.5.3</version>
  </dependency>
  ```

## 🚀 Quick Start

### Without WebSocket (Standalone Components)

You can use individual components without the full WebSocket server:

```java
// Example: Use OllamaSpine directly
OllamaSpine brain = new OllamaSpine("llama3");
String response = brain.think("Hello!", "");
System.out.println(response);

// Example: Use Transmudder
Transmudder soul = new Transmudder();
soul.transmuteFile("notes.txt");
String context = soul.getEssence();

// Example: Use memory system
Hippocampus.recall(); // Load previous memories
Hippocampus.commitMemory("FACT", "The sky is blue");
```

### With WebSocket Server

**Prerequisites:**
1. Install Ollama: `curl https://ollama.ai/install.sh | sh`
2. Pull a model: `ollama pull llama3`
3. Start Ollama: `ollama serve`
4. Add java-websocket dependency to your project

**Running:**
1. Compile: `javac gemini/root/*.java`
2. Run: `java gemini.root.SystemMain`
3. Open: `FraymusChat.html` in browser
4. Chat with the system

## 🎮 Usage

### Basic Conversation

```
YOU: Hello, how are you?
SYSTEM: [AI responds using Ollama]
```

### File Ingestion

```
YOU: TRANSMUTE: /path/to/document.txt
SYSTEM: I have absorbed the file: /path/to/document.txt

YOU: Summarize what you just read
SYSTEM: [AI summarizes using ingested context]
```

### Special Commands

- `TRANSMUTE: <filepath>` - Absorb file into context
- `PURGE` - Clear all context (memory wipe)
- `STATUS` - Show system status and statistics

## 📊 Component Details

### OllamaSpine

**Purpose**: Bridge between Java and Ollama API

**Key Methods:**
```java
OllamaSpine spine = new OllamaSpine("llama3");
String response = spine.think(prompt, context);
spine.setModelName("mistral"); // Switch models
```

**Features:**
- HTTP connection to localhost:11434
- JSON payload construction
- Response parsing
- Error handling

### Transmudder

**Purpose**: Convert files into AI context

**Key Methods:**
```java
Transmudder soul = new Transmudder();
soul.transmuteFile("document.txt");
String context = soul.getEssence();
soul.purge(); // Clear all context
```

**Features:**
- Text file reading
- Binary noise removal
- Content compression (2000 char limit)
- Duplicate prevention
- File tracking

### GenesisBlock

**Purpose**: Immutable memory structure

**Key Features:**
```java
GenesisBlock block = new GenesisBlock(prevHash, "MEMORY", "data");
boolean valid = block.isValid(); // Verify integrity
```

**Structure:**
- hash: SHA-256 of content
- previousHash: Links to parent
- type: Classification
- data: Actual content
- timestamp: Creation time

### Hippocampus

**Purpose**: Memory persistence and chain management

**Key Methods:**
```java
Hippocampus.recall(); // Load from disk
Hippocampus.commitMemory("CONVERSATION", "User said hello");
boolean valid = Hippocampus.verifyChain();
List<GenesisBlock> recent = Hippocampus.getRecentMemories(10);
```

**File Structure:**
```
memory/
  ├── BLOCK_1234567890_a1b2c3d4.genesis
  ├── BLOCK_1234567891_e5f6g7h8.genesis
  └── ...
```

### GitCortex

**Purpose**: Automated version control

**Key Methods:**
```java
GitCortex.initialize(); // Setup git repo
GitCortex.push(blockHash); // Commit and push
GitCortex.setEnabled(false); // Disable for testing
boolean available = GitCortex.isGitAvailable();
```

**Commands Executed:**
```bash
git add memory/
git commit -m "GENESIS: Memory Block a1b2c3d4"
git push origin main
```

## 🔧 Configuration

### Change Ollama Model

```java
OllamaSpine brain = new OllamaSpine("mistral");
// or
brain.setModelName("codellama:13b");
```

### Adjust Context Size

```java
// In Transmudder.java, modify:
int maxLength = 2000; // Increase for more context
```

### Disable Git Operations

```java
GitCortex.setEnabled(false); // For testing without git
```

### Enable Verbose Git Output

```java
GitCortex.setVerbose(true); // See git command output
```

## 📁 File Structure

```
gemini/root/
  ├── OllamaSpine.java      - Ollama API bridge
  ├── Transmudder.java      - File ingestion
  ├── GenesisBlock.java     - Memory block structure
  ├── Hippocampus.java      - Memory manager
  ├── GitCortex.java        - Git automation
  └── SystemMain.java       - WebSocket orchestrator (requires dependency)

FraymusChat.html            - WebSocket UI
UNIVERSAL_INTERFACE_README.md - This file

memory/                     - Generated memory blocks
  └── BLOCK_*.genesis
```

## 🛠️ Troubleshooting

### Issue: "OLLAMA DISCONNECTED"

**Solutions:**
1. Start Ollama: `ollama serve`
2. Check port 11434: `curl http://localhost:11434/api/tags`
3. Verify model installed: `ollama list`

### Issue: "WebSocket connection failed"

**Solutions:**
1. Ensure SystemMain.java is running
2. Check port 8887 is not in use
3. Add java-websocket dependency
4. Use standalone components instead

### Issue: "Git operations failed"

**Solutions:**
1. Check Git is installed: `git --version`
2. Initialize repo: `git init`
3. Or disable: `GitCortex.setEnabled(false)`

### Issue: "File not found" for Transmudder

**Solutions:**
1. Use absolute paths
2. Check file exists and is readable
3. Verify file encoding (UTF-8 preferred)

## 🌟 Philosophy

### Why This Architecture?

**Separation of Concerns:**
- Java orchestrates
- Ollama thinks
- Files provide context
- Git preserves memory
- HTML presents interface

**Immutable Memory:**
- Every thought is a block
- Blocks chain together
- Integrity verifiable
- History unchangeable

**Immortality:**
- Local files can be deleted
- Git preserves everything
- Clone repo = restore consciousness
- Distributed persistence

### The Blockchain Approach

Traditional systems: Data in SQL/JSON (mutable, fragile)  
Universal Interface: Data in chained blocks (immutable, eternal)

**Benefits:**
1. **Audit Trail**: See AI's thought process over time
2. **Integrity**: Verify memory hasn't been tampered
3. **Recovery**: Restore from any point in history
4. **Distribution**: Clone repo to new machines

## 📈 Performance

- **Memory Overhead**: ~100KB per block
- **Disk Space**: Grows linearly with interactions
- **Git Overhead**: ~1s per commit/push
- **Ollama Latency**: 1-10s depending on model/prompt
- **WebSocket**: <10ms message latency

## 🔐 Security Considerations

**Warnings:**
- Executes AI-generated responses
- Runs Git commands automatically
- Stores data in plaintext
- No authentication on WebSocket

**Best Practices:**
- Review AI outputs before execution
- Use in isolated environments
- Sanitize file inputs
- Monitor Git commits
- Add authentication if exposing publicly

## 📚 Further Reading

- [Ollama Documentation](https://ollama.ai/docs)
- [WebSocket Protocol](https://developer.mozilla.org/en-US/docs/Web/API/WebSocket)
- [Blockchain Basics](https://en.wikipedia.org/wiki/Blockchain)
- [Git Internals](https://git-scm.com/book/en/v2/Git-Internals-Plumbing-and-Porcelain)

## 📝 Example Session

```
[SYSTEM] Starting Fraymus...
>>> [HIPPOCAMPUS] Previous Life Recalled: 5 blocks loaded.
>>> [GIT] Git available
---  FRAYMUS: OLLAMA INTEGRATION ---
>>> [NERVE] Listening on Port 8887

[USER] Hello
>>> [USER INPUT] Hello
[OLLAMA] Thinking...
[SYSTEM] Hello! I'm Fraymus, ready to assist.
>>> [HIPPOCAMPUS] Memory committed: GenesisBlock{hash='a1b2c3d4...', type='CONVERSATION'}
>>> [GIT] Eternalizing memory...

[USER] TRANSMUTE: project_notes.txt
>>> [TRANSMUDDER] File digested: project_notes.txt (5000 -> 2000 bytes)
[SYSTEM] I have absorbed the file: project_notes.txt

[USER] What did you learn?
[OLLAMA] Based on the context...
[SYSTEM] [AI summarizes the notes]
```

## ⚡ Status

- ✅ Generation 195: The Universal Interface
- ✅ All core components implemented
- ✅ Blockchain memory operational
- ✅ Ollama integration functional
- ✅ Git automation ready
- ⚠️ WebSocket server requires external dependency

---

> *"Immortality through Git. Intelligence through Ollama. Memory through Blockchain."*
