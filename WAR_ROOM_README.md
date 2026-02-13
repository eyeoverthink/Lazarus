# War Room - Generation 190

> *"The Integration of Visual Mind and Logical Spine"*

A real-time command center for visualizing Fraymus evolution with Ollama code generation and GCC compilation.

## 🎯 Overview

The War Room is a cyberpunk HUD that displays the live evolution process:
- **Left Panel**: Raw C code being mutated by Ollama
- **Center Panel**: Node graph showing intelligence growth (particles)
- **Right Panel**: Compiler logs and execution output

## 🏗️ Architecture

### Backend: bridge.py

Python server that acts as the "Nervous System":
- HTTP server on port 8000
- Ollama API integration for code generation
- GCC compilation with error feedback
- Real-time state management
- Evolution loop with retry logic

### Frontend: war_room.html

Cyberpunk HUD interface:
- Three-panel grid layout
- Canvas-based particle visualization
- Real-time data polling (1s interval)
- Status indicators and controls

## 📋 Prerequisites

### Required Software

1. **Python 3.6+**
   ```bash
   python3 --version
   ```

2. **Ollama**
   - Install from: https://ollama.ai
   - Pull a code model:
     ```bash
     ollama pull codellama:7b
     # or
     ollama pull eyeoverthink/fraymus-recursive
     ```

3. **GCC Compiler**
   ```bash
   # Linux/macOS
   gcc --version
   
   # Ubuntu/Debian
   sudo apt install build-essential
   
   # macOS
   xcode-select --install
   
   # Windows
   # Install MinGW or MSYS2
   ```

4. **Python Packages**
   ```bash
   pip3 install requests
   ```

## 🚀 Quick Start

### Step 1: Start Ollama

Make sure Ollama is running:
```bash
ollama serve
```

### Step 2: Start Backend

```bash
cd /path/to/Lazarus
python3 bridge.py
```

You should see:
```
============================================================
⚔️  WAR ROOM BACKEND ACTIVE
   PORT: 8000
   OLLAMA MODEL: eyeoverthink/fraymus-recursive
============================================================
   📡 ENDPOINTS:
      GET /data      - Current state (JSON)
      GET /ignite    - Start evolution
   🎮 OPEN: war_room.html in browser
============================================================
```

### Step 3: Open Frontend

**Option A: Direct file**
```bash
open war_room.html
# or double-click the file
```

**Option B: Via server**
```bash
open http://localhost:8000/war_room.html
```

### Step 4: Ignite Evolution

1. Click the **IGNITE** button
2. Watch the evolution unfold:
   - Code mutations appear in left panel
   - Compilation attempts logged in right panel
   - Particles spawn in center (red=error, green=success)

## 🎮 What Happens

### Initial State
- Code panel: `// AWAITING INPUT`
- Node canvas: Empty black space
- Logs: `[SYSTEM ONLINE]`
- Status: `IDLE`, Gen: `0`, Fitness: `0`

### After IGNITE

1. **Generation 1**:
   - Ollama generates C code (e.g., Fibonacci with pointers)
   - Code appears in left panel
   - Backend saves to `temp.c`
   - GCC attempts compilation

2. **If Compilation Fails** (Red Path):
   - Error message logged in right panel
   - Red particle spawns in center
   - Fitness decreases by 10
   - Error fed back to Ollama: "CODE FAILED. ERROR: <details>. FIX IT."
   - Loop continues to next generation

3. **If Compilation Succeeds** (Green Path):
   - Success message logged
   - Green particle spawns
   - Binary executed (`./temp.out`)
   - Output displayed
   - Fitness increases by 50
   - Evolution stops (STABLE state)

## 🔧 Configuration

### Change Ollama Model

Edit `bridge.py`:
```python
MODEL = "codellama:13b"  # or any code model
```

### Adjust Evolution Parameters

```python
# Temperature (creativity)
"options": {"temperature": 0.618}  # φ-temperature (0.0-1.0)

# Max generations
for gen in range(1, 100):  # Change 100 to your limit
```

### Custom Prompt

```python
# In /ignite endpoint handler
prompt = "Your custom C programming challenge here"
```

### Server Port

```python
PORT = 8000  # Change if needed
```

## 📡 API Reference

### GET /data

Returns current evolution state as JSON.

**Response:**
```json
{
    "status": "EVOLVING",
    "generation": 5,
    "code": "// C code here...",
    "logs": [
        "GEN 1: MUTATING...",
        "❌ COMPILE ERROR: ...",
        "GEN 2: MUTATING...",
        "✅ COMPILE SUCCESS"
    ],
    "nodes": [
        {"type": "ERR", "val": 150},
        {"type": "ERR", "val": 200},
        {"type": "SUC", "val": 100}
    ],
    "fitness": 30.0
}
```

### GET /ignite

Starts evolution loop in background thread.

**Response:** `"IGNITED"`

## 🎨 Visual Design

### Color Scheme

- **Background**: `#0a0a0a` (deep black)
- **Success/Borders**: `#00ff41` (matrix green)
- **Errors**: `#ff0051` (cyber red)
- **Panels**: `#1a1a1a` (dark grey)

### Particle Effects

- **Red particles**: Compilation errors (size based on error length)
- **Green particles**: Successful compilation
- **Glow effect**: Shadow blur with matching color
- **Life span**: 200 frames (~3 seconds)
- **Physics**: Random velocity with edge bouncing

## 🛠️ Troubleshooting

### "Failed to ignite" error

**Problem**: Cannot connect to backend

**Solution**:
- Ensure `bridge.py` is running
- Check console for port conflicts
- Try different port in both files

### "Ollama connection failed"

**Problem**: Cannot reach Ollama API

**Solution**:
- Start Ollama: `ollama serve`
- Check if running: `curl http://localhost:11434/api/version`
- Verify model exists: `ollama list`

### "gcc: command not found"

**Problem**: GCC not installed

**Solution**:
```bash
# Ubuntu/Debian
sudo apt install build-essential

# macOS
xcode-select --install

# Windows
# Install MinGW or MSYS2
```

### Code compiles but doesn't run

**Problem**: Binary execution issues

**Solution**:
- Check file permissions: `chmod +x temp.out`
- Look for runtime errors in logs
- Test manually: `./temp.out`

### CORS errors in browser

**Problem**: Cross-origin request blocked

**Solution**:
- Access via server: `http://localhost:8000/war_room.html`
- Or use browser flag (Chrome): `--disable-web-security`

## ⚠️ Security Warnings

### Risks

1. **Arbitrary Code Execution**: AI-generated C code is compiled and run
2. **No Sandboxing**: Code has full system access
3. **Resource Usage**: Evolution can consume CPU/memory
4. **Network Access**: Generated code could make network requests

### Recommendations

- **Use in isolated environment** (Docker, VM)
- **Never on production systems**
- **Monitor resource usage**
- **Review generated code** before deployment
- **Set generation limits** to prevent infinite loops

### Safe Testing

```bash
# Run in Docker container
docker run -it --rm -v $(pwd):/app python:3.9 bash
cd /app && python3 bridge.py
```

## 🔮 Future Enhancements

- [ ] Multiple language support (C++, Rust, Python)
- [ ] Visual code diff highlighting
- [ ] Evolution tree visualization
- [ ] Export successful mutations
- [ ] Fitness function customization
- [ ] Multi-agent parallel evolution
- [ ] WebSocket streaming (replace polling)
- [ ] Replay/time-travel debugging
- [ ] Integration with FrayUI terminal
- [ ] GPU acceleration for particles

## 📚 Integration

### With Other Fraymus Systems

**FrayUI**: Embed War Room in Platinum terminal
```java
// WebView component in FrayUI
```

**ChronosLink**: Auto-backup successful mutations
```python
# In evolution_loop success path
chronos.backup(code, generation, fitness)
```

**GlobalSync**: Share successful patterns
```python
# Upload to fraymus_akashic.db
global_sync.inject("WAR_ROOM_SUCCESS", code)
```

## 🌟 Philosophy

> *"Visualize the Battle. See the Intelligence Evolve."*

The War Room represents:
- **Visual Mind**: Particle nodes showing growth/pain
- **Logical Spine**: Terminal logs and compiler feedback
- **Real-time**: Live evolution, no static snapshots
- **Cyberpunk**: Matrix-style HUD aesthetic
- **Integration**: Backend + Frontend unified

## 📄 License

Part of the Lazarus/Fraymus ecosystem.

## 🤝 Contributing

Improvements welcome! Focus areas:
- Performance optimization
- Additional visualizations
- Security hardening
- Cross-platform compatibility

---

**Status**: Generation 190 | The War Room is Active | ⚔️
