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
	@echo "Open web/FraymusChat.html"

clean:
	./gradlew clean
