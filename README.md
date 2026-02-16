# Lazarus/Fraymus - AI System

## What Actually Works Right Now

### Core Components (VERIFIED)
- ✅ **FraymusCore** - System coordinator
- ✅ **IQTracker** - Persistent intelligence tracking  
- ✅ **SimpleServer** - HTTP server (port 8080)
- ✅ **OllamaSpine** - LLM integration
- ✅ 19 AGI components in `gemini.root.*`

### Tests (ALL PASSING)
- ✅ 4/4 tests passing in SimpleSystemTest
- ✅ Build successful

## How to Use

### Build
```bash
./gradlew build
```

### Run Tests
```bash
./gradlew test
```

### Run HTTP Server
```bash
./gradlew run --args="fraymus.web.SimpleServer"
# Then: curl http://localhost:8080/hello
```

### Run Demos
```bash
./gradlew run --args="fraymus.demo.WorkingDemo"
```

## What Doesn't Exist (Yet)

Things documented but not implemented:
- Most of the "55 layers" mentioned in deleted docs
- Quantum collision simulator
- Video generation
- MaxHeadroom interface
- Many other grand plans

## Development Philosophy

**CODE FIRST, DOCS LATER (MAYBE)**

Every commit must include:
- Working code that compiles
- Tests that pass
- Proof it works

No more promises without implementations.

## Structure

```
src/main/java/
├── fraymus/
│   ├── core/           - System core
│   ├── intelligence/   - IQ tracking
│   ├── web/           - HTTP server
│   └── demo/          - Working demos
└── gemini/root/       - AGI components (19 files)

src/test/java/
└── fraymus/           - Tests
```

## Contributing

If you add code:
1. Make it work FIRST
2. Test it
3. Commit it
4. Don't document what doesn't exist

## License

All Rights Reserved
