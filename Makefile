MODEL ?= llama3
EMBED_MODEL ?= embeddinggemma
PORT ?= 8887

.PHONY: pull run ui clean doctor

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
