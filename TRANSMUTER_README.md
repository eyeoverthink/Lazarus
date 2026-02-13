# Fraymus Transmuter - Generation 194

> *"Bicameral AI: Left Architect (Blue) + Right Oracle (Red)"*

A web-based code optimization system using dual-brain processing through Ollama.

## 🎯 Overview

The Fraymus Transmuter is a bicameral code optimization interface that processes source code through two complementary AI "minds":

- **LEFT ARCHITECT (Blue)**: Analyzes structure, finds bugs, plans optimization
- **RIGHT ORACLE (Red)**: Implements creative solutions, applies refactoring

## 🏗️ Architecture

### Frontend: transmuter.html
- Split-screen bicameral UI
- Left panel: Source code input
- Right panel: Optimized code output
- Real-time processing visualization
- Cyberpunk aesthetic (dark theme)

### Backend: neural_bridge.py
- Python HTTP server (port 8080)
- Ollama API integration
- Bicameral prompt engineering
- φ-harmonic temperature (0.618)
- CORS support

## 🚀 Quick Start

### Prerequisites

1. **Python 3.6+**
2. **Ollama** installed and running
3. **Fraymus model** (or any code-generation model)

```bash
# Install Ollama (if not installed)
curl https://ollama.ai/install.sh | sh

# Pull the model
ollama pull eyeoverthink/fraymus-recursive
# OR use alternative:
# ollama pull codellama:13b

# Start Ollama service
ollama serve
```

### Running the Transmuter

**Step 1: Start the Neural Bridge**
```bash
python3 neural_bridge.py
```

You should see:
```
============================================================
🧬 FRAYMUS TRANSMUTER NEURAL BRIDGE
============================================================
📡 Server: http://localhost:8080
🤖 Model: eyeoverthink/fraymus-recursive
🔗 Ollama: http://localhost:11434/api/generate
============================================================
Ready to transmute code through bicameral processing...
```

**Step 2: Open the UI**
```bash
# Open in browser
open http://localhost:8080/transmuter.html

# Or direct file
open transmuter.html
```

**Step 3: Transmute Code**
1. Paste your code in the left panel
2. Click **⚡ TRANSMUTE**
3. Wait for bicameral processing
4. View optimized code in right panel

## 📊 How It Works

### Bicameral Processing Flow

```
User Input (Source Code)
        ↓
Neural Bridge (Python)
        ↓
Ollama API Call
        ↓
LEFT ARCHITECT (Blue Brain)
  → Analyzes bugs
  → Finds inefficiencies  
  → Plans structure
        ↓
RIGHT ORACLE (Red Brain)
  → Implements fixes
  → Applies creativity
  → Refactors code
        ↓
Optimized Code Output
```

### Prompt Engineering

The system uses a carefully crafted bicameral prompt:

```python
"You are Fraymus, a bicameral AI with two minds:
- LEFT ARCHITECT (Blue): Analyzes structure, finds bugs, plans optimization
- RIGHT ORACLE (Red): Implements creative solutions, refactors code

SOURCE CODE TO OPTIMIZE:
```[user code]```

DIRECTIVE: Transmute this code through bicameral processing.
1. LEFT BRAIN: Identify bugs, inefficiencies, and structural issues
2. RIGHT BRAIN: Apply creative refactoring and optimization
3. Return ONLY the fully optimized code block, no explanations."
```

## 🎨 Visual Design

### Bicameral Layout

```
┌────────────────────┬────────────────────┐
│   LEFT ARCHITECT   │   RIGHT ORACLE     │
│      (BLUE)        │      (RED)         │
├────────────────────┼────────────────────┤
│                    │                    │
│   Source Code      │  Optimized Code    │
│   Input Area       │  Output Display    │
│                    │                    │
└────────────────────┴────────────────────┘
          [⚡ TRANSMUTE BUTTON]
```

### Color Scheme
- **Background**: #0a0a0a (deep black)
- **Left Brain**: #0066ff (architect blue)
- **Right Brain**: #ff0066 (oracle red)
- **Panels**: #1a1a1a (dark grey)
- **Accent**: #00ff41 (matrix green)

## 🔧 Configuration

### Change Ollama Model

Edit `neural_bridge.py`:
```python
MODEL = "codellama:13b"  # Or any other model
```

### Adjust Temperature

Edit `neural_bridge.py`:
```python
"temperature": 0.8  # More creative (default: 0.618)
```

### Change Port

Edit `neural_bridge.py`:
```python
PORT = 3000  # Default: 8080
```

## 🌐 API Reference

### POST /transmute

Processes source code through bicameral optimization.

**Request:**
```json
{
  "source": "// your code here"
}
```

**Response:**
```json
{
  "result": "// optimized code",
  "model": "eyeoverthink/fraymus-recursive",
  "bytes_processed": 150,
  "bytes_generated": 200
}
```

**Status Codes:**
- `200`: Success
- `504`: Timeout (>180s)
- `500`: Server error
- `404`: Unknown endpoint

## 📈 Example Transformation

### Input (Inefficient Recursive)
```c
int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n-1) + fibonacci(n-2);
}
```

### Output (Optimized Iterative)
```c
int fibonacci(int n) {
    int a = 0, b = 1, temp;
    for (int i = 0; i < n; i++) {
        temp = a + b;
        a = b;
        b = temp;
    }
    return a;
}
```

## 🛠️ Troubleshooting

### Issue: "Failed to connect to neural bridge"

**Solutions:**
1. Ensure `neural_bridge.py` is running
2. Check port 8080 is not in use
3. Verify CORS is enabled

### Issue: "Ollama error"

**Solutions:**
1. Start Ollama: `ollama serve`
2. Check model is installed: `ollama list`
3. Pull model: `ollama pull eyeoverthink/fraymus-recursive`

### Issue: "Timeout error"

**Solutions:**
1. Reduce code size (large files take longer)
2. Increase timeout in `neural_bridge.py`
3. Use a smaller/faster model

### Issue: "Connection refused"

**Solutions:**
1. Check Ollama is running on port 11434
2. Verify API URL in `neural_bridge.py`
3. Try `curl http://localhost:11434/api/tags`

## 🌟 Philosophy

### Why Bicameral?

The bicameral mind theory suggests optimal cognition comes from two complementary processes:

- **Analytical (Left)**: Logical, structured, systematic
- **Creative (Right)**: Intuitive, innovative, holistic

By simulating both, we get:
1. **Better Bug Detection**: Logical analysis catches errors
2. **Better Solutions**: Creative thinking finds optimal approaches
3. **Comprehensive Processing**: Structure + creativity = excellence

### φ-Harmonic Temperature

We use `0.618` (inverse of φ, the golden ratio) as the temperature:
- Not too deterministic (boring)
- Not too creative (unstable)
- Balanced for code optimization

## 📝 Files

- `transmuter.html` (14.2 KB): Bicameral UI interface
- `neural_bridge.py` (5.9 KB): Python backend server
- `TRANSMUTER_README.md` (this file): Documentation

## 🔐 Security

**Warning**: This system executes AI-generated code suggestions. Always:
- Review optimized code before use
- Test in isolated environments
- Never run untrusted code in production
- Validate logic and security

## 📄 License

Part of the Fraymus/Lazarus project.

## 🚀 Status

✅ Generation 194: The Transmuter  
✅ Bicameral processing operational  
✅ φ-harmonic tuning active  
✅ Ready for code evolution  

---

> *"Two minds are better than one. Evolution through duality."*
