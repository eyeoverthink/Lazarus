#!/bin/bash
# FRAYNIX BUILD SCRIPT
# Compiles the Fraynix microkernel from generated source files

set -e  # Exit on any error

echo "╔════════════════════════════════════════════════════════════╗"
echo "║       FRAYNIX BUILD SYSTEM v0.1                            ║"
echo "║       'From C to Silicon'                                  ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Check if fraynix_src exists
if [ ! -d "fraynix_src" ]; then
    echo "❌ ERROR: fraynix_src/ directory not found!"
    echo "   Run: java fraymus.os.FraynixBuilder first"
    exit 1
fi

cd fraynix_src

echo "🔧 Step 1/3: Assembling bootloader..."
if ! command -v nasm &> /dev/null; then
    echo "❌ ERROR: NASM not found. Install with: sudo apt-get install nasm"
    exit 1
fi
nasm -f elf32 boot.asm -o boot.o
echo "   ✅ boot.o created"

echo ""
echo "🔧 Step 2/3: Compiling kernel..."
if ! command -v gcc &> /dev/null; then
    echo "❌ ERROR: GCC not found. Install with: sudo apt-get install gcc"
    exit 1
fi
gcc -m32 -c kernel.c -o kernel.o -ffreestanding -nostdlib
echo "   ✅ kernel.o created"

echo ""
echo "🔧 Step 3/3: Linking kernel binary..."
if ! command -v ld &> /dev/null; then
    echo "❌ ERROR: LD not found. Install binutils."
    exit 1
fi
ld -m elf_i386 -T linker.ld -o fraynix_kernel boot.o kernel.o
echo "   ✅ fraynix_kernel created"

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║  ✅ BUILD COMPLETE                                         ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "Kernel binary: fraynix_src/fraynix_kernel"
echo ""
echo "To test in QEMU:"
echo "  qemu-system-i386 -kernel fraynix_src/fraynix_kernel"
echo ""
