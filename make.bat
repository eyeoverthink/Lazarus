@echo off
REM Fraymus Universal Interface - Windows Batch Script

set MODEL=llama3
set EMBED_MODEL=nomic-embed-text

echo ╔════════════════════════════════════════════════════════════╗
echo ║     FRAYMUS UNIVERSAL INTERFACE - WINDOWS LAUNCHER         ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

echo == Checking Ollama ==
ollama list
if errorlevel 1 (
    echo ❌ Ollama not found. Install from: https://ollama.ai
    pause
    exit /b 1
)

echo.
echo == Pulling models ==
ollama pull %MODEL%
ollama pull %EMBED_MODEL%

echo.
echo == Building project ==
call gradlew.bat build

echo.
echo == Running Genesis Zero ==
call gradlew.bat run

pause
