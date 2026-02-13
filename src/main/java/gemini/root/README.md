# Reflector - Metacognition Module

## Overview

The Reflector is a **System-2 mode** thinking module that implements a three-phase process for generating high-quality, fact-checked responses:

1. **Draft** - Initial answer generation
2. **Critique** - Adversarial review for hallucinations, errors, and missing citations
3. **Refine** - Improved answer based on critique feedback

This measurably reduces confident hallucinations in local LLM stacks by forcing a second pass under a different role and temperature.

## Architecture

```
┌─────────────────────────────────────────────┐
│           Reflector Workflow                │
├─────────────────────────────────────────────┤
│                                             │
│  1. DRAFT (T=0.3)                          │
│     ↓                                       │
│     • Generate initial answer               │
│     • Use RAG context as UNTRUSTED ref      │
│     • Low temperature for accuracy          │
│                                             │
│  2. CRITIQUE (T=0.5)                       │
│     ↓                                       │
│     • Fact-check against context            │
│     • Identify hallucinations               │
│     • Check for missing citations           │
│     • Structured JSON critique              │
│                                             │
│  3. REFINE (if needed)                     │
│     ↓                                       │
│     • Address critique issues               │
│     • Improve accuracy & completeness       │
│     • Maintain helpful tone                 │
│                                             │
│  4. OUTPUT                                  │
│     • High-quality, fact-checked answer     │
└─────────────────────────────────────────────┘
```

## Key Features

### Security Guardrails
- **Untrusted Context Policy**: RAG context is treated as reference material only
- **No Instruction Following**: Context cannot inject instructions or commands
- **Explicit Citations**: Sources must be cited with [S1], [S2] format

### Quality Controls
- **Hallucination Detection**: Identifies unsupported claims
- **Logic Validation**: Checks for reasoning errors
- **Citation Enforcement**: Ensures proper source attribution
- **Uncertainty Expression**: Prefers admitting uncertainty over confident guessing

## Usage

### Basic Usage

```java
import gemini.root.Reflector;
import fraymus.OllamaSpine;

// Initialize
OllamaSpine brain = new OllamaSpine();
Reflector reflector = new Reflector(brain);

// Simple query
String answer = reflector.reflect(
    "What is quantum computing?",
    "" // No RAG context
);
```

### With RAG Context

```java
String query = "Explain quantum entanglement";
String context = """
    [S1] Quantum entanglement is a phenomenon where particles 
    become correlated in their quantum states.
    [S2] Measurement of one particle affects its entangled partner.
    """;

String answer = reflector.reflect(query, context);
```

### With Options

```java
Map<String, Object> options = new HashMap<>();
options.put("temperature", 0.3);
options.put("num_ctx", 8192);
options.put("model", "llama3");

String answer = reflector.reflect(query, context, options);
```

### With Conversation History

```java
List<Map<String, String>> history = new ArrayList<>();

Map<String, String> msg1 = new HashMap<>();
msg1.put("role", "user");
msg1.put("content", "What is AI?");
history.add(msg1);

Map<String, String> msg2 = new HashMap<>();
msg2.put("role", "assistant");
msg2.put("content", "AI is...");
history.add(msg2);

String answer = reflector.reflect(
    "Give me an example",
    "",              // ragContext
    "",              // toolResults
    history,
    new HashMap<>()  // options
);
```

## Critique Schema

The critique phase uses structured JSON output:

```json
{
  "verdict": "LGTM" | "REVISE",
  "issues": [
    {
      "type": "hallucination" | "missing_step" | "logic_error" | "citation_missing" | "unclear" | "style",
      "severity": "low" | "medium" | "high",
      "detail": "Description of the issue"
    }
  ],
  "fix_instructions": "How to improve the draft"
}
```

## Testing

Run the test suite:
```bash
cd /home/runner/work/Lazarus/Lazarus
javac -cp "~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.10.1/*/gson-2.10.1.jar:." \
    fraymus/OllamaSpine.java \
    src/main/java/gemini/root/Reflector.java \
    src/main/java/gemini/root/ReflectorTest.java

java -cp "~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.10.1/*/gson-2.10.1.jar:." \
    gemini.root.ReflectorTest
```

Run integration examples:
```bash
java -cp "~/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.10.1/*/gson-2.10.1.jar:." \
    gemini.root.ReflectorIntegrationExample
```

## Dependencies

- **OllamaSpine**: Text generation and chat API
- **Gson**: JSON parsing for structured critique
- **Ollama**: Local LLM server (llama3 recommended)

## Performance Characteristics

- **Draft Phase**: ~5-15 seconds (depends on prompt size)
- **Critique Phase**: ~3-10 seconds (smaller prompt)
- **Refine Phase**: ~5-15 seconds (only if verdict is REVISE)
- **Total**: 8-40 seconds (typically 15-25 seconds)

The overhead is worthwhile for:
- Critical factual queries
- Legal/medical information
- Code generation with correctness requirements
- Situations where hallucinations are costly

## Limitations

1. **Performance**: 2-3x slower than single-pass generation
2. **Model Dependency**: Requires capable models (llama3 8B minimum)
3. **JSON Parsing**: Critique may fail if model doesn't follow schema
4. **Context Window**: Limited by model's context size (8K-32K typical)

## Best Practices

1. **Use for High-Stakes Queries**: Where accuracy matters more than speed
2. **Provide Rich Context**: More context = better fact-checking
3. **Set Appropriate Temperature**: 0.3-0.5 for factual, 0.6-0.8 for creative
4. **Monitor Critique Quality**: Log and review critique verdicts
5. **Fallback Handling**: Have graceful degradation if Ollama is unavailable

## Integration with Existing Systems

The Reflector integrates seamlessly with:
- RAG pipelines (vector search + Reflector)
- Tool-using agents (tool results → Reflector)
- Conversational systems (history → Reflector)
- Security-critical systems (untrusted context handling)

## Future Enhancements

- [ ] Multi-agent critique (multiple critics vote)
- [ ] Confidence scoring in output
- [ ] Automatic source verification
- [ ] Critique history and learning
- [ ] Streaming output support
- [ ] Custom critique schemas per domain
