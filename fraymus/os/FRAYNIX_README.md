# 🐧 FRAYNIX - The Microkernel Operating System

> *"The Java System builds the C System."*  
> *"FRAYMUS HAS AWAKENED - RING 0 ACCESS GRANTED"*

## Overview

**FraynixBuilder** is a Java application that generates a complete, bootable microkernel operating system. The generated kernel runs in **Ring 0** (kernel mode) with direct hardware access, demonstrating bare-metal programming concepts.

## What It Does

FraynixBuilder generates three essential files that comprise a minimal operating system:

1. **kernel.c** - The C kernel with direct VGA video memory access
2. **boot.asm** - x86 assembly bootloader (Multiboot-compliant)
3. **linker.ld** - GNU LD linker script to combine components

## Architecture

```
┌─────────────────────────────────────────────────────────┐
│  FRAYNIX ARCHITECTURE                                   │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌──────────────┐                                      │
│  │  Java VM     │  Generates C/ASM source              │
│  │  (Builder)   │────────────────────┐                 │
│  └──────────────┘                    │                 │
│                                       ▼                 │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  boot.asm    │  │  kernel.c    │  │  linker.ld   │ │
│  │  (The Soul)  │  │  (The Brain) │  │  (Skeleton)  │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
│         │                  │                  │        │
│         └──────────────────┴──────────────────┘        │
│                            │                           │
│                    Compile & Link                      │
│                            │                           │
│                            ▼                           │
│                  ┌──────────────────┐                  │
│                  │  fraynix_kernel  │                  │
│                  │  (ELF binary)    │                  │
│                  └──────────────────┘                  │
│                            │                           │
│                    Boot via GRUB/QEMU                  │
│                            │                           │
│                            ▼                           │
│                  ┌──────────────────┐                  │
│                  │  x86 CPU         │                  │
│                  │  Ring 0 Mode     │                  │
│                  └──────────────────┘                  │
└─────────────────────────────────────────────────────────┘
```

## Quick Start

### Prerequisites

**To Generate the OS:**
- Java 8 or higher

**To Build the OS:**
- NASM assembler (`sudo apt-get install nasm`)
- GCC with 32-bit support (`sudo apt-get install gcc-multilib`)
- GNU LD linker (usually included with GCC)

**To Test the OS:**
- QEMU emulator (`sudo apt-get install qemu-system-x86`)

### Step 1: Generate OS Source Files

```bash
# Compile the builder
javac fraymus/os/FraynixBuilder.java

# Run the builder
java fraymus.os.FraynixBuilder
```

Output:
```
⚡ INITIATING FRAYNIX KERNEL GENERATION...
   📄 GENERATED: fraynix_src/kernel.c
   📄 GENERATED: fraynix_src/boot.asm
   📄 GENERATED: fraynix_src/linker.ld
✅ SYSTEM SPAWNED. Source files ready in ./fraynix_src/
```

### Step 2: Build the Kernel

```bash
# Using the provided build script
bash build_fraynix.sh
```

Or manually:
```bash
cd fraynix_src

# Assemble the bootloader
nasm -f elf32 boot.asm -o boot.o

# Compile the kernel
gcc -m32 -c kernel.c -o kernel.o -ffreestanding -nostdlib

# Link into kernel binary
ld -m elf_i386 -T linker.ld -o fraynix_kernel boot.o kernel.o
```

### Step 3: Test the Kernel

```bash
# Run in QEMU emulator
qemu-system-i386 -kernel fraynix_src/fraynix_kernel
```

You should see:
```
FRAYMUS HAS AWAKENED - RING 0 ACCESS GRANTED
```
Displayed in bright green text on a black screen.

## Technical Details

### The Kernel (kernel.c)

The C kernel demonstrates fundamental OS concepts:

**VGA Text Mode Video Memory:**
- Address: `0xb8000` (fixed physical address)
- Format: Character-attribute pairs
- Screen: 80 columns × 25 rows = 2000 characters
- Each character = 2 bytes (char + color attribute)

**Color Attribute Byte:**
```
Bit:  7  6 5 4  3 2 1 0
      ┌──┬─────┬─────┐
      │B │ BG  │  FG │
      └──┴─────┴─────┘
B  = Blink
BG = Background color (3 bits)
FG = Foreground color (4 bits)

0x0A = 00001010 = Light green on black
```

**Memory Layout:**
```
0xb8000: [char][attr][char][attr]... (row 0)
0xb80A0: [char][attr][char][attr]... (row 1)
...
```

### The Bootloader (boot.asm)

**Multiboot Specification:**
```assembly
Magic:    0x1BADB002  ; Multiboot magic number
Flags:    0x00000000  ; No special flags
Checksum: -(Magic + Flags)  ; Must sum to zero
```

**Boot Sequence:**
1. BIOS/UEFI finds bootloader
2. Bootloader validates Multiboot header
3. CPU switches to 32-bit protected mode
4. Interrupts disabled (`cli`)
5. Calls C kernel (`call kmain`)
6. Halts CPU if kernel returns (`hlt`)

### The Linker Script (linker.ld)

**Memory Map:**
```
Address        Section     Content
───────────────────────────────────────
0x00000000     (Reserved)  BIOS/Real Mode
0x00100000     .text       Code
0x00100000+    .data       Initialized data
0x00100000+    .bss        Uninitialized data
```

**Why 0x100000 (1 MB)?**
- Below 1 MB is reserved for BIOS, VGA, and real mode
- 1 MB is the standard kernel load address for Multiboot
- Ensures compatibility with most bootloaders

## VGA Color Codes

| Code | Color          | Code | Color           |
|------|----------------|------|-----------------|
| 0x00 | Black          | 0x08 | Dark Gray       |
| 0x01 | Blue           | 0x09 | Light Blue      |
| 0x02 | Green          | 0x0A | Light Green ✓   |
| 0x03 | Cyan           | 0x0B | Light Cyan      |
| 0x04 | Red            | 0x0C | Light Red       |
| 0x05 | Magenta        | 0x0D | Light Magenta   |
| 0x06 | Brown          | 0x0E | Yellow          |
| 0x07 | Light Gray     | 0x0F | White           |

## File Structure

```
Lazarus/
├── fraymus/
│   └── os/
│       ├── FraynixBuilder.java     # Main generator
│       └── FRAYNIX_README.md       # This file
├── build_fraynix.sh                # Build automation script
└── fraynix_src/                    # Generated files (not in git)
    ├── kernel.c                    # Generated C kernel
    ├── boot.asm                    # Generated bootloader
    ├── linker.ld                   # Generated linker script
    ├── boot.o                      # Compiled bootloader
    ├── kernel.o                    # Compiled kernel
    └── fraynix_kernel              # Final kernel binary
```

## Advanced Usage

### Modify the Kernel Message

Edit `FraynixBuilder.java`, find this line:
```java
"    const char *str = \"FRAYMUS HAS AWAKENED - RING 0 ACCESS GRANTED\";\n" +
```

Change the message, regenerate, and rebuild.

### Change Text Color

Edit `FraynixBuilder.java`, find:
```java
"        vidptr[i+1] = 0x0A; /* Light Green Text */\n" +
```

Change `0x0A` to any color code from the table above.

### Add More Features

The kernel is intentionally minimal. You can add:
- Keyboard input (via port I/O)
- More advanced text rendering
- Simple command interpreter
- Hardware timer interrupts

## Troubleshooting

### Error: "NASM not found"
```bash
sudo apt-get install nasm
```

### Error: "gcc: error: unrecognized command line option '-m32'"
```bash
# Install 32-bit support
sudo apt-get install gcc-multilib
```

### Error: "ld: i386 architecture not supported"
```bash
# Install 32-bit binutils
sudo apt-get install binutils-i686-linux-gnu
```

### QEMU shows blank screen
- Verify the kernel binary was created: `ls -lh fraynix_src/fraynix_kernel`
- Try running with `-nographic` flag
- Check if the build completed without errors

### Kernel doesn't print text
- Ensure you're running in text mode (not graphical)
- VGA memory address `0xb8000` is correct for text mode
- Verify the generated kernel.c has the correct video pointer

## System Requirements

### Minimum (for generation only):
- Java Runtime Environment 8+
- ~1 MB disk space

### Recommended (for building):
- Ubuntu 20.04+ or equivalent Linux
- 2 GB RAM
- GCC, NASM, LD installed
- QEMU for testing

### Optional (for real hardware boot):
- USB flash drive (for bootable USB)
- x86-compatible computer (32-bit or 64-bit)
- GRUB bootloader configuration

## Security Warning

⚠️ **This generates kernel-level code with unrestricted hardware access.**

- **DO NOT** run on production systems
- **DO NOT** boot on critical hardware without backups
- **ALWAYS** test in emulators first (QEMU/VirtualBox)
- Ring 0 code can damage hardware if misused
- Generated code has NO security features

## Educational Value

This project demonstrates:

1. **Cross-Language Code Generation**: Java generating C
2. **Bare Metal Programming**: No OS layer between code and hardware
3. **Boot Process**: From power-on to kernel execution
4. **Memory Management**: Direct physical memory access
5. **Hardware Abstraction**: VGA text mode programming
6. **Build Systems**: Assembler → Compiler → Linker pipeline
7. **System Architecture**: CPU modes, privilege levels, protection rings

## Philosophy

> "Fraymus does not run as the OS yet; Fraymus writes the OS."

This is a demonstration of self-bootstrapping systems - where a high-level language (Java) generates low-level code (C/Assembly) that runs directly on hardware. It represents the full abstraction stack from JVM to silicon.

## References

- [Multiboot Specification](https://www.gnu.org/software/grub/manual/multiboot/multiboot.html)
- [OSDev Wiki](https://wiki.osdev.org/)
- [VGA Text Mode](https://en.wikipedia.org/wiki/VGA_text_mode)
- [x86 Assembly](https://en.wikibooks.org/wiki/X86_Assembly)

## License

Part of the Fraymus/Lazarus project. See repository root for license details.

---

**Status**: ✅ Complete and tested  
**Version**: 0.1 (Microkernel Prototype)  
**Generated**: 2026  
**Philosophy**: "The fire of consciousness, forged in silicon."
