#!/bin/bash
# 🦞 OpenClaw Launcher Script
# Integrates OpenClaw AI assistant with Fraynix/Lazarus ecosystem

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LAZARUS_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
OPENCLAW_DIR="$LAZARUS_ROOT/openclaw"
OPENCLAW_PORT="${OPENCLAW_PORT:-18789}"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
MAGENTA='\033[0;35m'
NC='\033[0m'

print_header() {
    echo -e "${MAGENTA}"
    echo "╔════════════════════════════════════════════════════════╗"
    echo "║    🦞 OpenClaw Integration with Fraynix/Lazarus       ║"
    echo "╚════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

print_status() { echo -e "${CYAN}[STATUS]${NC} $1"; }
print_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
print_error() { echo -e "${RED}[ERROR]${NC} $1"; }
print_warning() { echo -e "${YELLOW}[WARNING]${NC} $1"; }

check_node() {
    print_status "Checking Node.js installation..."
    if ! command -v node &> /dev/null; then
        print_error "Node.js is not installed!"
        echo "Please install Node.js ≥22 from https://nodejs.org/"
        exit 1
    fi
    
    NODE_VERSION=$(node -v | cut -d'v' -f2 | cut -d'.' -f1)
    if [ "$NODE_VERSION" -lt 22 ]; then
        print_error "Node.js version must be ≥22 (found: $(node -v))"
        exit 1
    fi
    
    print_success "Node.js $(node -v) detected"
}

install_openclaw() {
    print_header
    check_node
    
    cd "$OPENCLAW_DIR"
    
    if [ -d "node_modules" ] && [ -d "dist" ]; then
        print_success "OpenClaw is already installed"
        return 0
    fi
    
    print_status "Installing OpenClaw dependencies..."
    
    if command -v pnpm &> /dev/null; then
        pnpm install
        pnpm build
    else
        print_warning "pnpm not found, using npm"
        npm install
        npm run build
    fi
    
    print_success "OpenClaw installation complete!"
}

start_openclaw() {
    print_header
    check_node
    
    if [ ! -d "$OPENCLAW_DIR/dist" ]; then
        print_warning "OpenClaw not built. Running install first..."
        install_openclaw
    fi
    
    print_status "Starting OpenClaw Gateway on port $OPENCLAW_PORT..."
    cd "$OPENCLAW_DIR"
    
    if command -v pnpm &> /dev/null; then
        pnpm openclaw gateway --port "$OPENCLAW_PORT" --verbose
    else
        npx openclaw gateway --port "$OPENCLAW_PORT" --verbose
    fi
}

stop_openclaw() {
    print_status "Stopping OpenClaw Gateway..."
    PID=$(lsof -ti:$OPENCLAW_PORT 2>/dev/null || true)
    
    if [ -z "$PID" ]; then
        print_warning "OpenClaw Gateway not running on port $OPENCLAW_PORT"
    else
        kill $PID 2>/dev/null || true
        print_success "OpenClaw Gateway stopped"
    fi
}

status_openclaw() {
    print_header
    PID=$(lsof -ti:$OPENCLAW_PORT 2>/dev/null || true)
    
    if [ -z "$PID" ]; then
        echo -e "${RED}● OpenClaw Gateway: NOT RUNNING${NC}"
        return 1
    else
        echo -e "${GREEN}● OpenClaw Gateway: RUNNING${NC}"
        echo "  Port: $OPENCLAW_PORT"
        echo "  PID: $PID"
        return 0
    fi
}

case "${1:-help}" in
    install) install_openclaw ;;
    start) start_openclaw ;;
    stop) stop_openclaw ;;
    restart) stop_openclaw; sleep 2; start_openclaw ;;
    status) status_openclaw ;;
    *)
        print_header
        echo "Usage: $0 {install|start|stop|restart|status|help}"
        echo ""
        echo "Commands:"
        echo "  install   - Install OpenClaw dependencies"
        echo "  start     - Start OpenClaw Gateway server"
        echo "  stop      - Stop OpenClaw Gateway server" 
        echo "  restart   - Restart OpenClaw Gateway"
        echo "  status    - Check if OpenClaw is running"
        ;;
esac
