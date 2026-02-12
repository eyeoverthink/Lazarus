# go2java.py - Go to Java Transpiler

## Overview

**go2java.py** is a Single File Application (SFA) that transpiles Go source code to Java source code using regex-based transformations.

> *"Code is eternal. Language is temporary."*

## Features

- **Type Mapping**: Converts Go primitive types to Java equivalents
- **Struct Conversion**: Transforms Go structs into Java static inner classes
- **Function Translation**: Maps Go functions to Java static methods
- **Package Structure**: Preserves directory hierarchy as Java package structure
- **Main Function**: Automatically detects and converts Go main() to Java main()
- **Basic Logic Translation**: Handles variable declarations, function calls, and control flow

## Installation

No installation required! Just download `go2java.py`.

**Requirements:**
- Python 3.6 or higher
- No external dependencies

## Usage

### Basic Usage

```bash
python go2java.py <source_dir> <dest_dir> [package_name]
```

### Arguments

| Argument | Required | Description |
|----------|----------|-------------|
| `source_dir` | Yes | Directory containing Go source files (.go) |
| `dest_dir` | Yes | Output directory for generated Java files |
| `package_name` | No | Base Java package name (default: `com.fraymus.converted`) |

### Examples

**Example 1: Basic conversion**
```bash
python go2java.py ./go_src ./java_output
```
This converts all .go files in `./go_src` to Java files in `./java_output` using the default package name.

**Example 2: Custom package**
```bash
python go2java.py ./myapp/go ./myapp/java com.mycompany.app
```
Converts Go code to Java with custom package name `com.mycompany.app`.

**Example 3: Preserving directory structure**
```bash
# Input:  go_src/main.go
#         go_src/utils/helpers.go
# Output: java_output/com/fraymus/converted/Main.java
#         java_output/com/fraymus/converted/utils/Helpers.java

python go2java.py go_src java_output com.fraymus.converted
```

## Type Mappings

The transpiler automatically converts Go types to Java equivalents:

| Go Type | Java Type |
|---------|-----------|
| `string` | `String` |
| `int` | `int` |
| `int32` | `int` |
| `int64` | `long` |
| `float32` | `float` |
| `float64` | `double` |
| `bool` | `boolean` |
| `interface{}` | `Object` |
| `[]byte` | `byte[]` |
| `nil` | `null` |
| `true` | `true` |
| `false` | `false` |

## Function Mappings

| Go Function | Java Equivalent |
|-------------|-----------------|
| `fmt.Println()` | `System.out.println()` |
| `fmt.Printf()` | `System.out.printf()` |

## Conversion Examples

### Example 1: Simple Function

**Go Input:**
```go
package main

func Add(a int, b int) int {
    return a + b
}

func main() {
    result := Add(5, 3)
    fmt.Println(result)
}
```

**Java Output:**
```java
package com.fraymus.converted;

import java.util.*;
import java.io.*;

public class Main {

    public static int Add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        var result = Add(5, 3);
        System.out.println(result);
    }
}
```

### Example 2: Struct Conversion

**Go Input:**
```go
package main

type User struct {
    Name  string
    Age   int
    Email string
}

func main() {
    user := User{Name: "Alice", Age: 30}
}
```

**Java Output:**
```java
package com.fraymus.converted;

import java.util.*;
import java.io.*;

public class Main {

    public static class User {
        public String Name;
        public int Age;
        public String Email;
    }

    public static void main(String[] args) {
        var user = User{Name: "Alice", Age: 30}
    }
}
```

### Example 3: Multiple Files

**Directory Structure:**
```
go_src/
├── main.go
└── utils/
    └── helpers.go
```

After transpilation:
```
java_output/
└── com/
    └── fraymus/
        └── converted/
            ├── Main.java
            └── utils/
                └── Helpers.java
```

## Limitations

This is a **rudimentary transpiler** using regex-based transformations. It has several limitations:

### Known Limitations

1. **No AST Parsing**: Does not build an Abstract Syntax Tree
2. **Simple Patterns Only**: Complex Go syntax may not translate correctly
3. **Struct Initialization**: Go struct literals need manual conversion
4. **Pointers**: Go pointer syntax is not converted
5. **Interfaces**: Go interfaces are not fully supported
6. **Goroutines**: Concurrency patterns are not converted
7. **Error Handling**: Go's error handling is not mapped to Java exceptions
8. **Type Inference**: Limited type inference for variable declarations
9. **Methods**: Go methods (receivers) are not converted
10. **Generics**: Neither Go nor Java generics are handled

### What Works Well

✅ Simple functions with primitive types  
✅ Basic structs with primitive fields  
✅ Main function detection  
✅ Package/directory structure  
✅ Simple variable declarations  
✅ fmt.Println/Printf conversion  

### What Needs Manual Fixing

⚠️ Struct initialization syntax  
⚠️ Control flow blocks (if/for/switch)  
⚠️ Complex function signatures  
⚠️ Custom types beyond structs  
⚠️ Method receivers  

## Advanced Usage

### Custom Type Mappings

You can modify the `type_map` dictionary in `GoTranspiler.__init__()` to add custom type mappings:

```python
self.type_map = {
    r'\bstring\b': 'String',
    r'\bint\b': 'int',
    # Add your custom mappings here
    r'\bMyCustomType\b': 'MyJavaType',
}
```

### Processing Multiple Projects

Use a shell script to process multiple Go projects:

```bash
#!/bin/bash
for dir in project1 project2 project3; do
    python go2java.py "./go_projects/$dir" "./java_output/$dir" "com.mycompany.$dir"
done
```

## Testing

Test the transpiler with the provided example files:

```bash
# Create test directory
mkdir -p test_go_src

# Create a simple Go file
cat > test_go_src/main.go << 'EOF'
package main

import "fmt"

func main() {
    message := "Hello, World!"
    fmt.Println(message)
}
EOF

# Run transpiler
python go2java.py test_go_src test_java_output

# Check output
cat test_java_output/com/fraymus/converted/Main.java
```

## Troubleshooting

### No files converted

**Problem:** "No .go files found"

**Solution:** Verify the source directory contains .go files:
```bash
ls -la <source_dir>/*.go
```

### Import errors in generated Java

**Problem:** Generated Java files won't compile

**Solution:** The transpiler is basic - manually review and fix:
- Struct initialization syntax
- Control flow blocks
- Type conversions

### Package structure issues

**Problem:** Generated package structure is wrong

**Solution:** Use absolute paths and verify the base package name:
```bash
python go2java.py "$(pwd)/go_src" "$(pwd)/java_output" com.correct.package
```

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│                   GoTranspiler                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  1. File Discovery                                      │
│     └─> os.walk() through source directory             │
│                                                         │
│  2. Package Structure Mapping                           │
│     └─> Convert dir/subdir to package.subpackage       │
│                                                         │
│  3. Per-File Transpilation                              │
│     ├─> Read Go source                                  │
│     ├─> Line-by-line parsing                            │
│     │   ├─> Struct detection                            │
│     │   ├─> Function detection                          │
│     │   ├─> Type mapping                                │
│     │   └─> Logic translation                           │
│     └─> Write Java output                               │
│                                                         │
│  4. Regex-Based Transformations                         │
│     └─> Apply type_map substitutions                    │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

## Philosophy

**Why Transpile?**

1. **Code Migration**: Move existing Go codebases to Java ecosystem
2. **Learning Tool**: Understand differences between Go and Java
3. **Prototyping**: Quick conversion for experimentation
4. **Legacy Support**: Bridge between language ecosystems

**Design Principles**

- **Simple**: Single file, no dependencies
- **Fast**: Regex-based for speed (not accuracy)
- **Extensible**: Easy to modify type mappings
- **Transparent**: Clear limitations and expectations

## Future Enhancements

Potential improvements (not yet implemented):

- [ ] AST-based parsing for accurate translation
- [ ] Support for Go methods (receivers)
- [ ] Interface conversion
- [ ] Better error handling mapping
- [ ] Goroutine to Thread/CompletableFuture conversion
- [ ] Custom type registry
- [ ] Configuration file support
- [ ] Incremental compilation
- [ ] Source maps for debugging
- [ ] Type inference improvements

## Related Tools

- **gopherjs**: Go to JavaScript transpiler
- **GopherLua**: Go to Lua
- **go2cs**: Go to C#
- **Grumpy**: Go to Python (CPython runtime)

## License

Part of the Fraymus/Lazarus project.

## Credits

Created as part of the autonomous code generation experiments.

---

**Status**: Functional for simple Go programs  
**Use Case**: Quick prototyping and learning  
**Production Ready**: No - manual review required  

*"Code is eternal. Language is temporary."*
