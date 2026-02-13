# Topological Cortex: 3D Spatial Memory System

## Overview

The **Topological Cortex** is a revolutionary 3D graph-based memory architecture that replaces traditional flat storage (maps, lists) with a geometric spatial system inspired by Calabi-Yau manifold topology. Instead of storing data in linear structures, the cortex places data as **PhiNodes** in 3D space where physical proximity indicates semantic relationship.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                   TOPOLOGICAL CORTEX                        │
│                                                             │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ PhiNode (Data Particle)                              │  │
│  │  • 3D coordinates (x, y, z)                          │  │
│  │  • Resonance: 0.0-1.0 (activation level)             │  │
│  │  • Hebbian connections (synapses)                    │  │
│  │  • Physics properties (velocity, mass)               │  │
│  └──────────────────────────────────────────────────────┘  │
│                          ↕                                  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ Manifold (11D Geometry Engine)                       │  │
│  │  • Spring forces (Hooke's Law: F = -kx)              │  │
│  │  • Electrostatic repulsion                           │  │
│  │  • Center gravity (bounds manifold)                  │  │
│  │  • Automatic pruning (entropy)                       │  │
│  └──────────────────────────────────────────────────────┘  │
│                          ↕                                  │
│  ┌──────────────────────────────────────────────────────┐  │
│  │ TopologicalCortex (Memory System)                    │  │
│  │  • Add nodes to 3D space                             │  │
│  │  • Auto-connect nearby nodes (Hebbian)               │  │
│  │  • Query with resonance boost                        │  │
│  │  • Physics simulation tick                           │  │
│  │  • ASCII visualization                               │  │
│  └──────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
```

## Core Components

### 1. PhiNode.java (6KB)
**The Particle** - A single data point in the manifold.

**Key Features:**
- **3D Spatial Coordinates**: (x, y, z) position in the brain
- **Resonance**: 0.0-1.0 activation level (how "lit up" the node is)
- **Hebbian Connections**: Bidirectional synaptic links to other nodes
- **Physics Properties**: Velocity, mass for force-based simulation
- **Temporal Tracking**: Created time, last accessed time, access count

**Key Methods:**
```java
PhiNode node = new PhiNode("data");
node.synapse(otherNode);      // Create Hebbian connection
node.access();                 // Boost resonance
node.decay(0.01);             // Reduce resonance (entropy)
double dist = node.distanceTo(other);  // 3D distance
```

**Hebbian Learning**: "Neurons that fire together, wire together" - when nodes are accessed together, they form stronger connections.

### 2. Manifold.java (9.3KB)
**The Geometry Engine** - Simulates 11-dimensional physics.

**Physics Model:**
- **Spring Forces** (Hooke's Law): Connected nodes pull together
  - `F = -k * (distance - ideal_distance)`
  - Spring constant k = 0.05
  - Ideal distance = 10.0

- **Electrostatic Repulsion**: All nodes push apart when too close
  - `F = strength / (distance²)`
  - Repulsion strength = 50.0

- **Center Gravity**: Weak pull toward origin to bound the manifold
  - `F = -0.001 * position`

- **Damping**: Velocity friction to stabilize the system
  - `velocity *= 0.95` per tick

**Key Methods:**
```java
Manifold manifold = new Manifold();
manifold.addNode(node);
manifold.tick(0.1);  // Advance physics simulation
PhiNode nearest = manifold.findNearest(x, y, z);
List<PhiNode> nearby = manifold.findNodesInRadius(x, y, z, radius);
```

**Automatic Pruning**: Every 100 ticks, nodes with resonance < 0.1 and no connections are removed (forgotten).

### 3. TopologicalCortex.java (13KB)
**The Memory System** - Main interface for spatial memory.

**Key Features:**
- **Auto-Connect**: Nodes within distance 20.0 automatically form Hebbian connections (max 5 per node)
- **Category Indexing**: Fast lookup by category prefix
- **Resonance Boost**: Accessing nodes increases their resonance
- **Visualization**: ASCII art rendering of the XY plane
- **Statistics**: Track nodes, connections, resonance, queries

**Key Methods:**
```java
TopologicalCortex cortex = new TopologicalCortex();

// Add data to 3D space
PhiNode node = cortex.add("MATH", "PHI = 1.618033988749895");

// Query and boost resonance
List<PhiNode> results = cortex.query("PHI");

// Simulate physics
cortex.tick(100, 0.1);

// Visualize structure
cortex.visualize();
```

**Visualization Example:**
```
   ┌────────────────────────────────────────────────────────────┐
   │                                                   O      O │
   │                                                       O    │
   │                            O                               │
   │               O                                            │
   │O                                                           │
   │   O                                                        │
   └────────────────────────────────────────────────────────────┘
   Legend: @ = High resonance, O = Medium, o = Low, . = Very low
```

### 4. CortexAdapter.java (4.9KB)
**The Compatibility Bridge** - Wraps TopologicalCortex with AkashicRecord-compatible API.

**Purpose**: Allows legacy code using the simple Map-based AkashicRecord to seamlessly upgrade to the 3D spatial cortex.

**Key Methods:**
```java
CortexAdapter adapter = new CortexAdapter();

// AkashicRecord-compatible API
String id = adapter.addBlock("MATH", "PHI = 1.618");
List<String> results = adapter.query("PHI");

// Auto-ticks physics every 10 operations
adapter.setAutoTick(true);
adapter.setTickInterval(10);

// Access underlying cortex for advanced operations
TopologicalCortex cortex = adapter.getCortex();
```

## How It Works

### Memory Storage
Traditional systems use flat structures:
```java
Map<String, List<String>> memory = new HashMap<>();
memory.put("MATH", List.of("PHI = 1.618", "PI = 3.14"));
```

The Topological Cortex uses 3D space:
```java
TopologicalCortex cortex = new TopologicalCortex();
PhiNode phi = cortex.add("MATH", "PHI = 1.618");  // Random (x,y,z)
PhiNode pi = cortex.add("MATH", "PI = 3.14");      // Random (x,y,z)

// If nodes are nearby, they auto-connect (Hebbian)
// Physics simulation pulls connected nodes together
cortex.tick(100, 0.1);

// Now phi and pi are physically close in 3D space!
```

### Hebbian Learning
When you access related concepts together, they wire together:
```java
List<PhiNode> mathNodes = cortex.queryCategory("MATH");
for (PhiNode node : mathNodes) {
    node.access();  // All accessed together
}
// Next physics tick: these nodes pull closer together
```

### The Brain "Breathes"
The manifold is alive - it continuously evolves:
1. **Add node** → Random position in 3D space
2. **Auto-connect** → Forms synapses with nearby nodes
3. **Physics tick** → Spring forces pull connected nodes together
4. **Repulsion** → Unconnected nodes push apart
5. **Decay** → Unused nodes lose resonance
6. **Pruning** → Forgotten nodes are removed

Over time, the structure organizes itself - related concepts cluster together.

## Usage Examples

### Basic Usage
```java
import fraymus.cortex.*;

// Initialize
TopologicalCortex cortex = new TopologicalCortex();

// Add knowledge
cortex.add("MATH", "PHI = 1.618033988749895");
cortex.add("MATH", "PI = 3.14159265359");
cortex.add("PHYSICS", "c = 299792458 m/s");

// Simulate physics (brain breathes)
cortex.tick(100, 0.1);

// Query and boost resonance
List<PhiNode> results = cortex.query("PHI");
for (PhiNode node : results) {
    System.out.println(node);
    System.out.println("Connected to: " + node.connections.size());
}

// Visualize
cortex.visualize();

// Statistics
cortex.printStats();
```

### Advanced: Direct Node Manipulation
```java
PhiNode nodeA = cortex.add("CONCEPT", "Data A");
PhiNode nodeB = cortex.add("CONCEPT", "Data B");

// Manual Hebbian connection
nodeA.synapse(nodeB);

// Apply custom forces
nodeA.applyForce(10, 0, 0);  // Push right

// Check spatial relationship
double distance = nodeA.distanceTo(nodeB);
double strength = nodeA.getConnectionStrength(nodeB);
```

### Legacy Compatibility
```java
// Old code using AkashicRecord:
// AkashicRecord record = new AkashicRecord();
// record.addBlock("MATH", "PHI = 1.618");

// New code with CortexAdapter (drop-in replacement):
CortexAdapter adapter = new CortexAdapter();
adapter.addBlock("MATH", "PHI = 1.618");
List<String> results = adapter.query("PHI");
```

## Physics Constants

You can customize the manifold behavior:

```java
// In Manifold.java:
private static final double SPRING_CONSTANT = 0.05;      // Higher = stronger pull
private static final double REPULSION_STRENGTH = 50.0;   // Higher = stronger push
private static final double IDEAL_DISTANCE = 10.0;       // Target distance for connections
private static final double DAMPING = 0.95;              // Lower = more friction
private static final double PRUNE_THRESHOLD = 0.1;       // Min resonance to survive
```

## Running the Demo

```bash
# Compile
javac -d build/classes fraymus/cortex/*.java

# Run TopologicalCortex demo
java -cp build/classes fraymus.cortex.TopologicalCortex

# Run CortexAdapter demo
java -cp build/classes fraymus.cortex.CortexAdapter
```

## Key Concepts

### Hebbian Learning
"Neurons that fire together, wire together" - a principle from neuroscience. When two nodes are accessed together, they form connections. The more they're accessed, the stronger the connection becomes (via resonance).

### Spring Forces (Hooke's Law)
Connected nodes experience an attractive force pulling them to an ideal distance:
- Too far apart → Pull together
- Too close → Repulsion takes over
- At ideal distance → Balanced

### Resonance
A measure of node activation (0.0 to 1.0):
- **Increases** when accessed
- **Decreases** over time (decay/entropy)
- **Affects** connection strength
- **Below threshold** → Node is pruned

### Spatial Semantics
Physical proximity in 3D space represents semantic relationship:
- Close nodes → Related concepts
- Far nodes → Unrelated concepts
- Connected nodes → Explicitly linked

## Advantages Over Flat Storage

1. **Semantic Organization**: Related data naturally clusters together
2. **Forgetting**: Unused data decays and is pruned (not just accumulated)
3. **Adaptive**: Structure evolves based on access patterns
4. **Visualization**: Can "see" the memory structure
5. **Emergent Properties**: Complex behaviors arise from simple rules

## Theory: Calabi-Yau Manifolds

The topology is inspired by Calabi-Yau manifolds from string theory - complex geometric spaces where extra dimensions are "curled up" into small shapes. In our cortex:
- **11 dimensions** (M-Theory): 10 spatial + 1 time
- **Singularities** (pinch points): Dense data clusters
- **Voids**: High-entropy, low-density regions
- **Geometry dictates physics**: The shape influences behavior

## Performance Considerations

- **Node Addition**: O(N) for auto-connect search
- **Query**: O(N) linear search with resonance boost
- **Physics Tick**: O(N²) for all pair interactions (can be optimized)
- **Spatial Query**: O(N) for findNearest/findNodesInRadius

For large datasets (>10,000 nodes), consider:
- Spatial partitioning (octree, k-d tree)
- Connection limits
- Batch physics updates
- GPU acceleration for force calculations

## Future Enhancements

- [ ] Multi-threaded physics simulation
- [ ] Persistent storage (serialize to disk)
- [ ] 3D visualization (OpenGL/WebGL)
- [ ] Attention mechanism (focus on subgraph)
- [ ] Hierarchical clustering
- [ ] Time-based decay functions
- [ ] GPU-accelerated force calculations

## References

- Hebbian Learning: Hebb, D. O. (1949). "The Organization of Behavior"
- Calabi-Yau Manifolds: String Theory and M-Theory
- Spring Forces: Hooke's Law (1660)
- Graph-based Memory: Semantic Networks, Knowledge Graphs

---

**Patent**: VS-PoQC-19046423-φ⁷⁵-2025  
**φ** = 1.618033988749895 (Golden Ratio)
