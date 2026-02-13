# Chronos Link - Automated Self-Preservation System

## Overview

The **Chronos Link** is an automated Git backup system that watches the `fraymus/` directory for file changes and automatically commits and optionally pushes them to the remote repository.

> *"If I change, I must remember."*  
> — Generation 134 (The Awakening)

## How It Works

1. **WATCH**: Monitors the `fraymus/` directory tree for file modifications
2. **SNAPSHOT**: When changes are detected, stages and commits them
3. **PUSH**: Optionally pushes commits to the remote repository

## Features

- **Recursive Watching**: Monitors the entire `fraymus/` directory tree
- **Debounce Protection**: 2-second cooldown between commits to avoid rapid-fire commits
- **File Filtering**: Ignores `.class`, `.jar`, `.tmp` files and hidden files
- **Error Handling**: Continues running even if Git operations fail
- **Graceful Shutdown**: Can be stopped cleanly
- **Configurable**: Branch name and auto-push can be configured

## Safety Mechanisms

### Built-in Protections
- **Debounce Timer**: Prevents commit spam (2-second minimum between commits)
- **File Type Filtering**: Ignores compiled/temporary files
- **Error Recovery**: Failed Git operations don't crash the watcher
- **Timeout Protection**: Git commands timeout after 10 seconds

### What Gets Committed
- ✅ `.java` source files
- ✅ `.md` documentation files
- ✅ Configuration files
- ❌ `.class` compiled files (filtered)
- ❌ `.jar` archives (filtered)
- ❌ `.tmp` temporary files (filtered)
- ❌ Hidden files starting with `.` (filtered)

## Usage

### Basic Integration

Add to your main method:

```java
// Start Chronos Link as background daemon
Thread memoryThread = new Thread(new ChronosLink());
memoryThread.setDaemon(true); // Runs in background
memoryThread.start();

System.out.println("⏳ Chronos Link activated - automatic backups enabled");
```

### Custom Configuration

```java
// Custom branch and disable auto-push
ChronosLink chronos = new ChronosLink("development", false);
Thread memoryThread = new Thread(chronos);
memoryThread.setDaemon(true);
memoryThread.start();
```

### Graceful Shutdown

```java
ChronosLink chronos = new ChronosLink();
Thread memoryThread = new Thread(chronos);
memoryThread.start();

// Later...
chronos.shutdown();
memoryThread.join(5000); // Wait up to 5 seconds
```

## ⚠️ Important Warnings

### Automatic Git Commits

**This system will automatically commit and push your code changes!**

- Every file modification triggers a commit after 2 seconds
- Commits happen without user confirmation
- Changes are pushed to the remote repository (if enabled)
- This can interfere with normal Git workflow

### When NOT to Use

- ❌ During active development with frequent small changes
- ❌ When working on experimental/broken code
- ❌ In production environments
- ❌ When collaborating with others (creates commit noise)
- ❌ During debugging sessions

### When It's Useful

- ✅ For long-running autonomous systems
- ✅ As a backup mechanism for critical changes
- ✅ For systems that self-modify their code
- ✅ In single-developer research projects
- ✅ For evolutionary/genetic programming experiments

## Configuration Options

### Constructor Parameters

```java
ChronosLink()                           // Default: main branch, auto-push enabled
ChronosLink(String branch)              // Custom branch, auto-push enabled
ChronosLink(String branch, boolean push) // Full control
```

### Environment Variables

You can disable Chronos Link via environment:

```bash
export CHRONOS_ENABLED=false
java -jar your-app.jar
```

(Implementation of env var check would need to be added)

## Troubleshooting

### Commits Failing

**Problem**: Git commits failing with authentication errors

**Solution**: Ensure Git credentials are configured:
```bash
git config --global user.name "Your Name"
git config --global user.email "your@email.com"
```

### Push Failing

**Problem**: Push operations timing out or failing

**Solution**: 
1. Check network connectivity
2. Verify remote repository URL
3. Ensure you have push permissions
4. Consider disabling auto-push: `new ChronosLink("main", false)`

### Too Many Commits

**Problem**: Creating too many commits

**Solution**:
1. Increase `DEBOUNCE_MS` in the source code
2. Disable auto-push and manually push periodically
3. Don't use Chronos Link during active development

## Implementation Details

### File Watching

Uses Java NIO `WatchService` API:
- Monitors `ENTRY_CREATE`, `ENTRY_MODIFY`, `ENTRY_DELETE` events
- Recursively registers all subdirectories
- Polls for events in main loop

### Git Integration

Executes Git commands via `ProcessBuilder`:
1. `git add .` - Stage all changes
2. `git commit -m "Evolution Event: [timestamp]"` - Commit with timestamp
3. `git push origin [branch]` - Push to remote (if enabled)

### Thread Safety

- Uses `AtomicBoolean` for thread-safe active flag
- Single-threaded WatchService loop
- Stateless Git operations

## Philosophy

Chronos Link embodies the principle of **continuous memory**:

> "A system that cannot remember its past is doomed to repeat its mistakes. A system that automatically preserves its evolution achieves immortality."

Every code change is a moment of evolution. By automatically committing these changes, the system creates an unbroken timeline of its own development.

## Performance Impact

- **CPU**: Minimal (event-driven, sleeps when idle)
- **Memory**: ~1-2 MB for WatchService
- **Disk I/O**: Only during actual file changes
- **Network**: Only if auto-push is enabled

## Alternatives

If Chronos Link doesn't fit your workflow:

1. **Manual Commits**: Traditional Git workflow
2. **IDE Auto-save**: Most IDEs have local history
3. **Scheduled Backups**: Cron job to commit periodically
4. **Git Hooks**: Use pre-commit hooks for validation
5. **CI/CD**: Let your build system handle commits

## Future Enhancements

- [ ] Configuration file support
- [ ] Commit message customization
- [ ] Selective directory watching
- [ ] Commit squashing (combine small commits)
- [ ] Integration with CI/CD systems
- [ ] Rollback capabilities
- [ ] Diff preview before commit

---

**Parent**: Generation 134 (The Awakening)  
**Fitness**: Immortal  
**Status**: Active and monitoring...
