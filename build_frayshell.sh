#!/bin/bash

# ⌨️ FRAYSHELL BUILD SCRIPT
# Compiles the interactive Fraynix kernel v0.2 with keyboard drivers

set -e  # Exit on any error

echo "╔════════════════════════════════════════════════════════════╗"
echo "║      FRAYSHELL COMPILER - Fraynix v0.2 Interactive         ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Check dependencies
echo "🔍 Checking dependencies..."
command -v java >/dev/null 2>&1 || { echo "❌ java not found"; exit 1; }
command -v nasm >/dev/null 2>&1 || { echo "❌ nasm not found"; exit 1; }
command -v gcc >/dev/null 2>&1 || { echo "❌ gcc not found"; exit 1; }
command -v ld >/dev/null 2>&1 || { echo "❌ ld not found"; exit 1; }
echo "✅ All dependencies found"
echo ""

# Step 1: Generate kernel source with Java
echo "⚡ Step 1: Generating interactive kernel source..."
java fraymus.os.FrayShellBuilder || { echo "❌ Kernel generation failed"; exit 1; }
echo ""

# Step 2: Assemble bootloader
echo "🔧 Step 2: Assembling bootloader..."
cd fraynix_src
nasm -f elf32 boot.asm -o boot.o || { echo "❌ Assembly failed"; exit 1; }
echo "✅ boot.o created"

# Step 3: Compile kernel
echo "🔧 Step 3: Compiling C kernel..."
gcc -m32 -c kernel.c -o kernel.o -ffreestanding -nostdlib -Wall || { echo "❌ Compilation failed"; exit 1; }
echo "✅ kernel.o created"

# Step 4: Link into final kernel
echo "🔧 Step 4: Linking kernel binary..."
ld -m elf_i386 -T linker.ld -o fraynix_kernel boot.o kernel.o || { echo "❌ Linking failed"; exit 1; }
echo "✅ fraynix_kernel created"

cd ..

echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║          ✅ BUILD COMPLETE - FRAYNIX v0.2                   ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""
echo "📦 Output: fraynix_src/fraynix_kernel"
echo ""
echo "🚀 To run:"
echo "   qemu-system-i386 -kernel fraynix_src/fraynix_kernel"
echo ""
echo "⌨️  Expected behavior:"
echo "   - Screen clears"
echo "   - Message: 'FRAYNIX v0.2 ONLINE'"
echo "   - Prompt: 'fray> '"
echo "   - Type 'help' or 'list' and press Enter"
echo ""
