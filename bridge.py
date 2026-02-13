#!/usr/bin/env python3
"""
🧬 FRAYMUS BRIDGE // GEN 190
The War Room Backend - Nervous System

Connects the Local God Core to the War Room HUD.
Runs evolution loop with Ollama + GCC compilation.
"""

import http.server
import socketserver
import json
import subprocess
import requests
import time
import threading
import os

PORT = 8000
OLLAMA_URL = "http://localhost:11434/api/generate"
MODEL = "eyeoverthink/fraymus-recursive"

# STATE
current_state = {
    "status": "IDLE",
    "generation": 0,
    "code": "// AWAITING INPUT",
    "logs": ["[SYSTEM ONLINE]"],
    "nodes": [],  # Visual nodes
    "fitness": 0.0
}

def ask_fraymus(prompt):
    """The Neural Link to Ollama."""
    try:
        res = requests.post(OLLAMA_URL, json={
            "model": MODEL, 
            "prompt": prompt, 
            "stream": False,
            "options": {"temperature": 0.618}  # φ-temperature
        })
        return res.json().get("response", "")
    except Exception as e:
        print(f"❌ Ollama connection failed: {e}")
        return None

def clean_code(raw):
    """Extracts C code from markdown blocks."""
    if not raw:
        return ""
    clean = raw.replace("```c", "").replace("```cpp", "").replace("```", "")
    return clean.strip()

def evolution_loop(target_prompt):
    """The Infinite Game - Evolution with feedback."""
    global current_state
    current_state["status"] = "EVOLVING"
    current_state["nodes"] = []  # Reset visuals
    
    prompt = target_prompt
    
    for gen in range(1, 100):
        current_state["generation"] = gen
        current_state["logs"].append(f"GEN {gen}: MUTATING...")
        
        # 1. GENERATE CODE
        raw = ask_fraymus(prompt)
        if not raw:
            current_state["logs"].append("❌ OLLAMA CONNECTION FAILED")
            break
        
        code = clean_code(raw)
        current_state["code"] = code
        
        # 2. COMPILE (The Pain Test)
        try:
            with open("temp.c", "w") as f:
                f.write(code)
        except Exception as e:
            current_state["logs"].append(f"❌ FILE WRITE ERROR: {e}")
            break
        
        # Try compiling with GCC
        proc = subprocess.run(
            ["gcc", "temp.c", "-o", "temp.out"], 
            capture_output=True, 
            text=True
        )
        
        if proc.returncode != 0:
            # FAILURE - Red Node (Pain)
            err = proc.stderr
            current_state["logs"].append(f"❌ COMPILE ERROR: {err[:100]}...")
            current_state["fitness"] = max(0, current_state["fitness"] - 10)
            
            # Add Red Node
            current_state["nodes"].append({"type": "ERR", "val": len(err)})
            
            # Feed back error to Ollama
            prompt = f"CODE FAILED TO COMPILE.\nERROR: {err}\nFIX IT. Keep pointer arithmetic logic."
        else:
            # SUCCESS - Green Node (Evolution)
            current_state["logs"].append("✅ COMPILE SUCCESS. BINARY LINKED.")
            current_state["fitness"] += 50
            current_state["status"] = "STABLE"
            
            # Add Green Node
            current_state["nodes"].append({"type": "SUC", "val": 100})
            
            # Run the binary
            try:
                run = subprocess.run(
                    ["./temp.out"], 
                    capture_output=True, 
                    text=True,
                    timeout=2
                )
                current_state["logs"].append(f"📤 OUTPUT: {run.stdout[:100]}")
            except subprocess.TimeoutExpired:
                current_state["logs"].append("⏱️ EXECUTION TIMEOUT")
            except Exception as e:
                current_state["logs"].append(f"⚠️ EXECUTION ERROR: {e}")
            
            break  # Success - stop evolution
            
        time.sleep(1)  # Pace the war
    
    current_state["status"] = "IDLE"
    current_state["logs"].append("🏁 EVOLUTION COMPLETE")

# HTTP SERVER (Serves the UI and Data)
class Handler(http.server.SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/data":
            # Serve current state as JSON
            self.send_response(200)
            self.send_header('Content-type', 'application/json')
            self.send_header('Access-Control-Allow-Origin', '*')
            self.end_headers()
            self.wfile.write(json.dumps(current_state).encode())
        elif self.path.startswith("/ignite"):
            # Start Evolution Thread
            prompt = "Write a C program that calculates Fibonacci numbers using pointer arithmetic. Optimize for speed and memory efficiency."
            t = threading.Thread(target=evolution_loop, args=(prompt,), daemon=True)
            t.start()
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b"IGNITED")
        else:
            # Serve static files
            super().do_GET()
    
    def log_message(self, format, *args):
        """Suppress default server logs"""
        pass

if __name__ == "__main__":
    print("=" * 60)
    print("⚔️  WAR ROOM BACKEND ACTIVE")
    print(f"   PORT: {PORT}")
    print("   OLLAMA MODEL: " + MODEL)
    print("=" * 60)
    print("   📡 ENDPOINTS:")
    print("      GET /data      - Current state (JSON)")
    print("      GET /ignite    - Start evolution")
    print("   🎮 OPEN: war_room.html in browser")
    print("=" * 60)
    
    socketserver.TCPServer.allow_reuse_address = True
    with socketserver.TCPServer(("", PORT), Handler) as httpd:
        try:
            httpd.serve_forever()
        except KeyboardInterrupt:
            print("\n🛑 War Room shutting down...")
