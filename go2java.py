#!/usr/bin/env python3
"""
🧬 GO2JAVA TRANSPILER
"Code is eternal. Language is temporary."

Single File Application (SFA) that converts Go code to Java code.
Uses regex-based transformations to map Go syntax to Java syntax.

Usage: python go2java.py <source_dir> <dest_dir> [package_name]

Example:
    python go2java.py ./go_src ./java_output com.fraymus.converted
"""

import os
import re
import sys

class GoTranspiler:
    def __init__(self, src_root, dest_root, base_package="com.fraymus.converted"):
        self.src_root = src_root
        self.dest_root = dest_root
        self.base_package = base_package
        
        # 1. THE DICTIONARY (Regex Mappings)
        self.type_map = {
            r'\bstring\b': 'String',
            r'\bint\b': 'int',
            r'\bint64\b': 'long',
            r'\bint32\b': 'int',
            r'\bfloat64\b': 'double',
            r'\bfloat32\b': 'float',
            r'\bbool\b': 'boolean',
            r'\binterface\{\}\b': 'Object',
            r'\b\[\]byte\b': 'byte[]',
            r'\bnil\b': 'null',
            r'\bfmt\.Println\(': 'System.out.println(',
            r'\bfmt\.Printf\(': 'System.out.printf(',
            r'\btrue\b': 'true',
            r'\bfalse\b': 'false',
        }

    def run(self):
        print(f"⚡ INITIATING TRANSMUTATION: {self.src_root} -> {self.dest_root}")
        
        if not os.path.exists(self.src_root):
            print(f"❌ ERROR: Source directory '{self.src_root}' does not exist!")
            return False
        
        file_count = 0
        for root, dirs, files in os.walk(self.src_root):
            for file in files:
                if file.endswith(".go"):
                    go_path = os.path.join(root, file)
                    
                    # Calculate relative path for package structure
                    rel_path = os.path.relpath(root, self.src_root)
                    if rel_path == ".":
                        java_package = self.base_package
                        dest_folder = os.path.join(self.dest_root, self.base_package.replace(".", "/"))
                    else:
                        java_package = f"{self.base_package}.{rel_path.replace(os.sep, '.')}"
                        dest_folder = os.path.join(self.dest_root, self.base_package.replace(".", "/"), rel_path)

                    os.makedirs(dest_folder, exist_ok=True)
                    
                    # Convert filename (main.go -> Main.java)
                    java_filename = file.replace(".go", "").capitalize() + ".java"
                    dest_path = os.path.join(dest_folder, java_filename)
                    
                    self.transmute_file(go_path, dest_path, java_package, java_filename.replace(".java", ""))
                    file_count += 1
        
        if file_count == 0:
            print(f"⚠️  WARNING: No .go files found in '{self.src_root}'")
            return False
        
        print(f"✅ TRANSMUTATION COMPLETE: {file_count} file(s) converted")
        return True

    def transmute_file(self, go_path, dest_path, package_name, class_name):
        print(f"   ⚗️  {os.path.basename(go_path)} -> {class_name}.java")
        
        with open(go_path, 'r', encoding='utf-8') as f:
            go_code = f.read()

        java_lines = []
        java_lines.append(f"package {package_name};")
        java_lines.append("")
        java_lines.append("import java.util.*;")
        java_lines.append("import java.io.*;")
        java_lines.append("")
        java_lines.append(f"public class {class_name} {{")

        # 2. THE PARSING ENGINE
        # This is a rudimentary parser. For 'perfect' translation, this would need an AST.
        lines = go_code.split('\n')
        in_struct = False
        
        for line in lines:
            line = line.strip()
            
            # Skip comments and imports (simplified)
            if line.startswith("//") or line.startswith("import") or line.startswith("package"):
                continue

            # DETECT: Structs -> Inner Classes
            # "type User struct {"
            struct_match = re.search(r'type\s+(\w+)\s+struct\s+\{', line)
            if struct_match:
                struct_name = struct_match.group(1)
                java_lines.append(f"\n    public static class {struct_name} {{")
                in_struct = True
                continue
            
            if in_struct and line == "}":
                java_lines.append("    }")
                in_struct = False
                continue

            # DETECT: Struct Fields
            # "Name string" -> "public String Name;"
            if in_struct:
                parts = line.split()
                if len(parts) >= 2:
                    name = parts[0]
                    go_type = parts[1]
                    java_type = self.map_type(go_type)
                    java_lines.append(f"        public {java_type} {name};")
                continue

            # DETECT: Main Function
            if "func main()" in line:
                java_lines.append("\n    public static void main(String[] args) {")
                continue

            # DETECT: Standard Functions
            # "func Add(a int, b int) int {"
            func_match = re.search(r'func\s+(\w+)\((.*?)\)\s*(\w+)?\s*\{', line)
            if func_match:
                func_name = func_match.group(1)
                args_raw = func_match.group(2)
                return_type = self.map_type(func_match.group(3)) if func_match.group(3) else "void"
                
                # Convert args "a int, b string" -> "int a, String b"
                java_args = self.convert_args(args_raw)
                
                java_lines.append(f"\n    public static {return_type} {func_name}({java_args}) {{")
                continue

            # DETECT: Closing Braces
            if line == "}":
                java_lines.append("    }")
                continue

            # GENERAL LOGIC TRANSLATION
            translated_line = self.translate_logic(line)
            if translated_line:
                java_lines.append(f"        {translated_line}")

        java_lines.append("}") # Close Class

        with open(dest_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(java_lines))

    def map_type(self, go_type):
        if not go_type:
            return "void"
        for go, java in self.type_map.items():
            if re.search(go, go_type):
                return java
        return go_type # Fallback for custom structs

    def convert_args(self, args_raw):
        if not args_raw: return ""
        # Go: "a int, b string" -> Java: "int a, String b"
        # This is a naive implementation; complex args need AST parsing.
        parts = args_raw.split(',')
        java_args = []
        for part in parts:
            p = part.strip().split()
            if len(p) == 2:
                java_args.append(f"{self.map_type(p[1])} {p[0]}")
        return ", ".join(java_args)

    def translate_logic(self, line):
        # Apply regex replacements for function calls and keywords
        for go, java in self.type_map.items():
            line = re.sub(go, java, line)
        
        # Variable declaration ":="
        # "x := 10" -> "var x = 10" (Java 10+) or "int x = 10"
        if ":=" in line:
            line = line.replace(":=", "=")
            # Prepend 'var' for local type inference
            line = "var " + line
            
        if not line.endswith(";") and not line.endswith("{") and not line.endswith("}"):
             if line: line += ";"
             
        return line


def main():
    """Main entry point for the transpiler."""
    print("╔════════════════════════════════════════════════════════════╗")
    print("║           GO2JAVA TRANSPILER - SFA v1.0                    ║")
    print("║        'Code is eternal. Language is temporary.'           ║")
    print("╚════════════════════════════════════════════════════════════╝")
    print()
    
    if len(sys.argv) < 3:
        print("Usage: python go2java.py <source_dir> <dest_dir> [package_name]")
        print()
        print("Arguments:")
        print("  source_dir   - Directory containing Go source files")
        print("  dest_dir     - Output directory for Java files")
        print("  package_name - Java package name (default: com.fraymus.converted)")
        print()
        print("Example:")
        print("  python go2java.py ./go_src ./java_output com.myapp.generated")
        print()
        sys.exit(1)
    
    src_dir = sys.argv[1]
    dest_dir = sys.argv[2]
    package = sys.argv[3] if len(sys.argv) > 3 else "com.fraymus.converted"
    
    transpiler = GoTranspiler(src_dir, dest_dir, package)
    success = transpiler.run()
    
    if success:
        print()
        print("╔════════════════════════════════════════════════════════════╗")
        print("║                  TRANSMUTATION SUCCESSFUL                  ║")
        print("╚════════════════════════════════════════════════════════════╝")
        sys.exit(0)
    else:
        print()
        print("╔════════════════════════════════════════════════════════════╗")
        print("║                  TRANSMUTATION FAILED                      ║")
        print("╚════════════════════════════════════════════════════════════╝")
        sys.exit(1)


if __name__ == "__main__":
    main()
