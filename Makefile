# Fraymus Universal Interface - Makefile
# Cross-platform automation for Unix/Mac/Git Bash

MODEL ?= llama3
EMBED_MODEL ?= embeddinggemma
PORT ?= 8887

.PHONY: doctor pull build run clean test

# Check system prerequisites
doctor:
	@echo "==  Checking Ollama =="
	@command -v ollama >/dev/null 2>&1 || (echo "❌ Ollama not found in PATH" && exit 1)
	@ollama list || echo "⚠️  Ollama not running. Start with: ollama serve"
	@echo "== Checking Java =="
	@java -version
	@echo "== Checking Gradle =="
	@./gradlew --version || echo "Using gradle wrapper"
	@echo "✅ System check complete"

# Pull required Ollama models
pull:
	@echo "📥 Pulling models..."
	ollama pull $(MODEL)
	ollama pull $(EMBED_MODEL)
	@echo "✅ Models ready"

# Build the project
build:
	@echo "🔨 Building project..."
	./gradlew build
	@echo "✅ Build complete"

# Run Genesis Zero Protocol
run:
	@echo "🚀 Running Genesis Zero..."
	./gradlew run

# Run tests
test:
	@echo "🧪 Running tests..."
	./gradlew test

# Clean build artifacts
clean:
	@echo "🧹 Cleaning..."
	./gradlew clean
	rm -rf build/ .gradle/
	@echo "✅ Clean complete"

# Display help
help:
	@echo "Fraymus Universal Interface - Build Commands"
	@echo ""
	@echo "make doctor    - Check system requirements"
	@echo "make pull      - Download Ollama models"
	@echo "make build     - Compile the project"
	@echo "make run       - Execute Genesis Zero"
	@echo "make test      - Run tests"
	@echo "make clean     - Remove build artifacts"
