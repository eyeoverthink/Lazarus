# Run Foundation - Reproducible Scientific Computing

This package provides a shared foundation for reproducible, logged, and defensible scientific computing experiments.

## Overview

The `fraymus.run` package implements a clean, reproducible architecture for computational experiments in:
- Cancer Research (cell evolution modeling)
- Drug Discovery (molecular optimization)
- Protein Folding (3D structure prediction)
- Any physics-based simulation

## Core Components

### 1. RunConfig
Immutable configuration for reproducible runs:
- `seed` - Random seed (reproducibility)
- `steps` - Simulation steps
- `populationSize` - Number of entities
- `gravityConstant` - Physics constant (default: φ = 1.618...)
- `fusionDistance` - Interaction threshold
- `energyThreshold` - Fusion energy requirement
- `outDir` - Output directory
- `prettyConsole` - Human-readable console output
- `jsonl` - Machine-readable JSONL logging

### 2. RunContext
Shared execution context:
- `cfg` - RunConfig instance
- `rng` - SplittableRandom (seeded, deterministic)
- `log` - EventLogger for JSONL output
- `clock` - RunClock for timing

### 3. EventLogger
JSONL event logging:
- `header()` - Write run metadata
- `step()` - Log step metrics
- `event()` - Log custom events
- `footer()` - Write summary

### 4. RunClock
Time tracking:
- Start timestamp
- Elapsed time (ms/sec)
- Human-readable formatting

## RNG Rules (Non-Negotiable)

**CRITICAL**: All randomness must come from `ctx.rng`

✅ **DO:**
```java
double x = ctx.nextDouble();
int i = ctx.nextInt(100);
boolean b = ctx.nextBoolean();
```

❌ **DON'T:**
```java
new Random()      // NEVER
Math.random()     // NEVER
```

**Why?**
- Same seed → Same output (reproducibility)
- All randomness tracked
- Results are defensible

## Usage

### Basic Example

```java
// Configure run
RunConfig cfg = RunConfig.builder()
    .seed(12345)
    .steps(1000)
    .populationSize(100)
    .build();

// Create context
RunContext ctx = RunContext.create(cfg, "my-experiment");

// Log header
ctx.log.header(Map.of(
    "experiment", "MyExperiment",
    "description", "Testing reproducibility"
));

// Run simulation
for (int step = 0; step < ctx.cfg.steps; step++) {
    // Use ctx.rng for ALL random numbers
    double value = ctx.nextDouble();
    
    // Log metrics
    ctx.log.step(step, Map.of("value", value));
}

// Write summary
ctx.log.footer(Map.of("completed", true));

// Cleanup
ctx.close();
```

### Cancer Research Example

```bash
java fraymus.run.CancerResearchEngine
```

Simulates cancer cell evolution:
- Cells organize via Hebbian gravity
- Fusion creates drug-resistant mutations
- Logs resistant cell counts over time

**Output:**
- Console: Beautiful progress display
- JSONL: `runs/cancer/cancer-research-001.jsonl`

### Drug Discovery Example

```bash
java fraymus.run.DrugDiscoveryEngine
```

Optimizes drug molecules:
- Maximizes binding affinity
- Minimizes toxicity
- Synthesizes novel compounds via fusion

**Output:**
- Console: Best molecule scores
- JSONL: `runs/drugs/drug-discovery-001.jsonl`

### Protein Folding Example

```bash
java fraymus.run.ProteinFoldingEngine
```

Predicts 3D protein structure:
- Amino acids interact via charges
- Energy minimization
- Compactness measurement

**Output:**
- Console: Energy landscape
- JSONL: `runs/protein/protein-folding-001.jsonl`

## JSONL Format

Each line is a JSON object:

**Header:**
```json
{
  "event": "header",
  "timestamp": "2026-02-13T13:00:00Z",
  "runName": "experiment-001",
  "seed": 12345,
  "steps": 1000,
  "populationSize": 100
}
```

**Step:**
```json
{
  "event": "step",
  "timestamp": "2026-02-13T13:00:01Z",
  "elapsedMs": 123,
  "step": 0,
  "metric1": 42.0,
  "metric2": 99
}
```

**Footer:**
```json
{
  "event": "footer",
  "timestamp": "2026-02-13T13:00:10Z",
  "elapsedMs": 10000,
  "elapsedSec": 10.0,
  "summary": "Final results"
}
```

## Reproducibility

Same seed → Same output:

```bash
# Run 1
java -cp ... fraymus.run.CancerResearchEngine

# Run 2 (identical results)
java -cp ... fraymus.run.CancerResearchEngine
```

Both produce identical JSONL files that can be:
- Plotted in Python/R
- Analyzed statistically
- Compared across runs
- Published as supplementary data

## Advantages

1. **Reproducible**: Same seed → Same results
2. **Logged**: All events in JSONL
3. **Defensible**: Complete audit trail
4. **Plotable**: Easy data analysis
5. **Comparable**: Standardized format
6. **Clean**: No randomness leaks

## Integration

To use in your own engine:

```java
public class MyEngine {
    private final RunContext ctx;
    
    public MyEngine(RunContext ctx) {
        this.ctx = ctx;
    }
    
    public void run() {
        ctx.log.header(Map.of("engine", "MyEngine"));
        
        for (int step = 0; step < ctx.cfg.steps; step++) {
            // Use ctx.rng for randomness
            double x = ctx.nextDouble();
            
            // Your simulation logic here
            
            // Log metrics
            ctx.log.step(step, Map.of("x", x));
        }
        
        ctx.log.footer(Map.of("done", true));
    }
}
```

## Files

- `RunConfig.java` - Configuration builder
- `RunContext.java` - Execution context
- `RunClock.java` - Time tracking
- `EventLogger.java` - JSONL logging
- `CancerResearchEngine.java` - Example: cancer cells
- `DrugDiscoveryEngine.java` - Example: drug optimization
- `ProteinFoldingEngine.java` - Example: protein structure

## Dependencies

- Java 17+
- Gson 2.10.1 (for JSONL)

## License

Part of the Lazarus/Fraymus project.
