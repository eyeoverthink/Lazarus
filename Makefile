MODEL ?= llama3
EMBED_MODEL ?= embeddinggemma
PORT ?= 8887
OPENCLAW_PORT ?= 18789

.PHONY: pull run ui clean doctor openclaw-install openclaw-start openclaw-stop openclaw-restart openclaw-status openclaw-update help

doctor:
	@command -v ollama >/dev/null 2>&1 || (echo "Ollama not found in PATH" && exit 1)
	@java -version

pull:
	ollama pull $(MODEL)
	ollama pull $(EMBED_MODEL)

run:
	FRAYMUS_MODEL=$(MODEL) FRAYMUS_EMBED_MODEL=$(EMBED_MODEL) ./gradlew run

ui:
	@echo "🎨 Opening Lazarus UI..."
	@echo "Main Interface: web/index.html"
	@echo "Launcher: web/launcher.html"
	@echo "Experiments: web/experiments.html"
	@echo "Chat: web/FraymusChat.html"
	@echo ""
	@echo "Opening in default browser..."
	@if command -v xdg-open > /dev/null; then \
		xdg-open web/index.html; \
	elif command -v open > /dev/null; then \
		open web/index.html; \
	elif command -v start > /dev/null; then \
		start web/index.html; \
	else \
		echo "Please manually open: web/index.html"; \
	fi

clean:
	./gradlew clean

# OpenClaw Integration Commands
openclaw-install:
	@echo "🦞 Installing OpenClaw..."
	@./scripts/openclaw-launcher.sh install

openclaw-start:
	@echo "🦞 Starting OpenClaw Gateway..."
	@./scripts/openclaw-launcher.sh start

openclaw-stop:
	@echo "🦞 Stopping OpenClaw Gateway..."
	@./scripts/openclaw-launcher.sh stop

openclaw-restart:
	@echo "🦞 Restarting OpenClaw Gateway..."
	@./scripts/openclaw-launcher.sh restart

openclaw-status:
	@./scripts/openclaw-launcher.sh status

openclaw-update:
	@echo "🦞 Updating OpenClaw to latest version..."
	@./scripts/openclaw-launcher.sh update

help:
	@echo "╔════════════════════════════════════════════════════════╗"
	@echo "║           Lazarus/Fraynix Makefile Commands           ║"
	@echo "╚════════════════════════════════════════════════════════╝"
	@echo ""
	@echo "Main Commands:"
	@echo "  make ui                - Open beautiful web interface"
	@echo "  make run               - Run Lazarus system"
	@echo "  make test              - Run test suite"
	@echo "  make clean             - Remove build artifacts"
	@echo "  make doctor            - Check system dependencies"
	@echo ""
	@echo "OpenClaw Integration:"
	@echo "  make openclaw-install  - Install OpenClaw dependencies"
	@echo "  make openclaw-start    - Start OpenClaw Gateway (port $(OPENCLAW_PORT))"
	@echo "  make openclaw-stop     - Stop OpenClaw Gateway"
	@echo "  make openclaw-restart  - Restart OpenClaw Gateway"
	@echo "  make openclaw-status   - Check if OpenClaw is running"
	@echo "  make openclaw-update   - Update OpenClaw to latest version"
	@echo ""
	@echo "Quick Start:"
	@echo "  1. make openclaw-install    # Install OpenClaw (one time)"
	@echo "  2. make openclaw-start      # Start OpenClaw Gateway"
	@echo "  3. make ui                  # Open Fraynix interface"
	@echo ""
	@echo "For more information, see README.md or GETTING_STARTED.md"
