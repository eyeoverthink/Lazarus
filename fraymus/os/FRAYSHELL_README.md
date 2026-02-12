# FrayShell - Interactive Kernel with Hardware I/O

> *"Gives the machine ears (Keyboard) and a voice (Shell)."*

## Overview

FrayShellBuilder upgrades the Fraynix microkernel from a static display to a **fully interactive operating system** with hardware keyboard drivers and a command-line shell.

**Version**: Fraynix v0.2 (Interactive)  
**Generator**: FrayShellBuilder.java  
**Language**: C with inline x86 assembly  
**Architecture**: x86 32-bit protected mode  

## What's New in v0.2

### Hardware I/O Driver
```c
unsigned char inb(unsigned short port) {
    unsigned char result;
    __asm__("inb %1, %0" : "=a"(result) : "Nd"(port));
    return result;
}
```
- **Direct port access** via inline assembly
- **No BIOS interrupts** - pure hardware control
- **x86 `inb` instruction** to read 8-bit data from I/O ports

### Keyboard Driver
```c
char get_char() {
    unsigned char scancode;
    while(1) {
        if((inb(0x64) & 1) == 0) continue;  // Wait for ready
        scancode = inb(0x60);                 // Read scancode
        if(scancode & 0x80) continue;         // Ignore release
        // Map scancode to ASCII...
    }
}
```
- **Port 0x60**: Keyboard data port (scancode)
- **Port 0x64**: Keyboard status port (ready flag)
- **Scancode mapping**: Hardware signals → ASCII characters
- **Polling-based**: Blocks until key press (simple but functional)

### Video Driver Enhancement
```c
void kprint(const char* str) {
    // Character-by-character VGA writing
    // Supports newline (\n) formatting
    // Automatic line wrapping
}
```
- **Newline support**: Proper `\n` handling
- **Automatic wrapping**: Calculates line positions (160 bytes = 80 chars × 2)
- **VGA text mode**: Direct 0xb8000 memory writes

### Interactive Shell
```c
void kmain(void) {
    while(1) {
        kprint("fray> ");
        // Read input line
        // Parse command
        // Execute and display output
    }
}
```
- **Command prompt**: "fray> "
- **128-byte buffer**: Stores typed commands
- **Echo typing**: Characters appear as you type
- **Command parsing**: Simple prefix matching
- **Infinite loop**: Shell never exits

## Hardware Keyboard Protocol

### Keyboard Controller Ports

| Port   | Direction | Purpose                                    |
|--------|-----------|-------------------------------------------|
| 0x60   | Read      | Scancode data (key pressed/released)      |
| 0x64   | Read      | Status register (bit 0 = data available)  |

### Reading Keyboard Input

1. **Poll status port** (0x64) until bit 0 is set
2. **Read data port** (0x60) to get scancode
3. **Check bit 7** of scancode:
   - 0 = Key press
   - 1 = Key release (ignore)
4. **Map scancode** to ASCII character

### Scancode Reference

| Scancode | Key   | ASCII | Scancode | Key   | ASCII |
|----------|-------|-------|----------|-------|-------|
| 0x1E     | A     | 'a'   | 0x30     | B     | 'b'   |
| 0x2E     | C     | 'c'   | 0x20     | D     | 'd'   |
| 0x12     | E     | 'e'   | 0x21     | F     | 'f'   |
| 0x22     | G     | 'g'   | 0x23     | H     | 'h'   |
| 0x17     | I     | 'i'   | 0x24     | J     | 'j'   |
| 0x25     | K     | 'k'   | 0x26     | L     | 'l'   |
| 0x32     | M     | 'm'   | 0x31     | N     | 'n'   |
| 0x18     | O     | 'o'   | 0x19     | P     | 'p'   |
| 0x10     | Q     | 'q'   | 0x13     | R     | 'r'   |
| 0x1F     | S     | 's'   | 0x14     | T     | 't'   |
| 0x16     | U     | 'u'   | 0x2F     | V     | 'v'   |
| 0x11     | W     | 'w'   | 0x2D     | X     | 'x'   |
| 0x15     | Y     | 'y'   | 0x2C     | Z     | 'z'   |
| 0x39     | Space | ' '   | 0x1C     | Enter | '\n'  |

**Note**: Scancodes are from Set 1 (IBM XT). Modern keyboards may use different sets.

## Built-in Commands

### `help`
Displays system information.

**Usage**: Type `help` and press Enter

**Output**:
```
  System: Fraynix OS
  Kernel: Micro (C)
  FS: FrayFS
```

### `list`
Shows mock filesystem listing.

**Usage**: Type `list` and press Enter

**Output**:
```
  [FILE] welcome.txt
  [FILE] secret.dat
```

### Unknown Commands
Any other input displays: `Unknown command.`

## Building and Running

### Prerequisites
- **Java 8+** (to run FrayShellBuilder)
- **NASM** (assembly compiler)
- **GCC** (with 32-bit support)
- **GNU LD** (linker)
- **QEMU** (optional, for testing)

### Step 1: Generate Interactive Kernel

```bash
# Compile the builder
javac fraymus/os/FrayShellBuilder.java

# Generate kernel.c
java fraymus.os.FrayShellBuilder
```

**Output**:
```
⚡ UPGRADING KERNEL: Injecting I/O Drivers...
   📄 GENERATED: fraynix_src/kernel.c
✅ KERNEL UPGRADED. Fraynix is now listening.
   Run: './build_frayshell.sh' to rebuild the kernel.
```

### Step 2: Build the Kernel

```bash
# Use the provided build script
bash build_frayshell.sh
```

**Manual build** (if script unavailable):
```bash
cd fraynix_src

# Assemble bootloader
nasm -f elf32 boot.asm -o boot.o

# Compile kernel
gcc -m32 -c kernel.c -o kernel.o -ffreestanding -nostdlib

# Link
ld -m elf_i386 -T linker.ld -o fraynix_kernel boot.o kernel.o
```

### Step 3: Run in Emulator

```bash
qemu-system-i386 -kernel fraynix_src/fraynix_kernel
```

### Expected Output

```
FRAYNIX v0.2 ONLINE
COMMANDS: [help, list, reboot]

fray> _
```

The cursor blinks (hardware default) and waits for input.

## Interactive Session Example

```
fray> help
  System: Fraynix OS
  Kernel: Micro (C)
  FS: FrayFS

fray> list
  [FILE] welcome.txt
  [FILE] secret.dat

fray> invalid
  Unknown command.

fray> _
```

## Architecture Diagram

```
┌─────────────────────────────────────────┐
│         User Types on Keyboard          │
└────────────────┬────────────────────────┘
                 │ (Physical electrical signals)
                 ▼
┌─────────────────────────────────────────┐
│   Keyboard Controller (Port 0x60/0x64)  │
│   • Converts keypresses to scancodes    │
│   • Stores in buffer                    │
│   • Sets ready flag in status register  │
└────────────────┬────────────────────────┘
                 │ (I/O port communication)
                 ▼
┌─────────────────────────────────────────┐
│      Keyboard Driver (get_char)         │
│   • Polls port 0x64 for ready flag      │
│   • Reads scancode from port 0x60       │
│   • Filters key releases (bit 7)        │
│   • Maps scancode to ASCII              │
└────────────────┬────────────────────────┘
                 │ (ASCII character)
                 ▼
┌─────────────────────────────────────────┐
│           Shell (kmain loop)            │
│   • Echoes character to screen          │
│   • Stores in 128-byte buffer           │
│   • On Enter: parse and execute command │
│   • Repeat forever                      │
└────────────────┬────────────────────────┘
                 │ (Command output string)
                 ▼
┌─────────────────────────────────────────┐
│       Video Driver (kprint)             │
│   • Writes to VGA buffer (0xb8000)      │
│   • Handles newlines and wrapping       │
│   • Sets color attributes (0x07)        │
└─────────────────────────────────────────┘
                 │
                 ▼
           User sees output
```

## Adding New Commands

To add a new command, edit the generated `kernel.c` after generation or modify `FrayShellBuilder.java`:

### Example: Adding "time" Command

In `FrayShellBuilder.java`, add to the execute section:

```java
"        } else if (buffer[0] == 't' && buffer[1] == 'i') {\n" +
"            kprint(\"  Time: 12:34:56 UTC\\n\");\n" +
```

Or in the generated `kernel.c`:

```c
} else if (buffer[0] == 't' && buffer[1] == 'i') {
    kprint("  Time: 12:34:56 UTC\n");
```

**Note**: Prefix matching means "ti", "time", "tilde" all match. Use more characters for specificity:

```c
if(buffer[0]=='t' && buffer[1]=='i' && buffer[2]=='m' && buffer[3]=='e') {
    // Only matches "time"
}
```

## Memory Layout

| Address   | Section | Content                          |
|-----------|---------|----------------------------------|
| 0x100000  | .text   | Code (kmain, kprint, get_char)   |
| 0x10xxxx  | .data   | Initialized data (vidptr, etc.)  |
| 0x10xxxx  | .bss    | Uninitialized data               |
| 0xb8000   | VGA     | Video memory (80×25×2 = 4000B)   |
| Stack     | Stack   | Local variables (buffer[128])    |

## Security Considerations

**WARNING**: This kernel has NO security features:

- **No input validation**: Buffer overflow possible if >128 characters
- **No memory protection**: Any code can access all memory
- **No privilege separation**: Everything runs in Ring 0 (kernel mode)
- **No authentication**: No user system
- **No encryption**: All data in plaintext

**Only run in isolated environments** (VMs, emulators). Never on production hardware.

## Troubleshooting

### Keyboard Not Responding
- **Cause**: QEMU may need keyboard focus
- **Fix**: Click inside QEMU window

### Characters Not Echoing
- **Cause**: Video buffer index overflow
- **Fix**: Clear screen or restart (reboot via QEMU)

### Wrong Characters Appearing
- **Cause**: Scancode set mismatch
- **Fix**: Some keyboards use Set 2/Set 3. Consult keyboard controller docs.

### Compilation Errors
- **Cause**: Missing dependencies or wrong GCC version
- **Fix**: Ensure 32-bit GCC support: `gcc -m32 -print-file-name=libc.a`

## Next Steps: Self-Hosting Compiler

The problem statement mentions the final evolution:

> **Shall we build the Fraymus Compiler (in C)? So that Fraynix can compile itself inside itself (Self-Hosting)?**

This would involve:
1. **Lexer**: Tokenize C source code
2. **Parser**: Build AST from tokens
3. **Code Generator**: Emit x86 assembly
4. **Assembler**: Convert assembly to machine code
5. **Linker**: Combine object files

With a self-hosting compiler, Fraynix could:
- Compile its own kernel source
- Execute compiled programs
- Bootstrap itself from source
- Become a true development environment

**Status**: Ready for compiler implementation

## Philosophy

> "The machine is listening. It is waiting for your input."

This demonstrates:
- **Hardware abstraction layers** built from scratch
- **Polling I/O** as the simplest driver model
- **Scancode protocol** for electrical signal interpretation
- **Terminal emulation** as fundamental OS interface
- **Command parsing** for user interaction

From raw electricity to typed commands in ~150 lines of C.

## References

- **Intel 8042**: Keyboard controller chip
- **VGA Text Mode**: Video buffer at 0xb8000
- **x86 I/O Ports**: `in` and `out` instructions
- **Multiboot Specification**: Boot protocol
- **Scancode Set 1**: IBM XT keyboard protocol

---

**Generated by**: FrayShellBuilder.java (Fraymus v3.0)  
**Philosophy**: "Bare metal. No libraries. Just signals."  
**Status**: Fully functional interactive OS
