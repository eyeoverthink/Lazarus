// ⚠️⚠️⚠️ EDUCATIONAL TEST CASE ONLY ⚠️⚠️⚠️
// ⚠️ WARNING: INTENTIONALLY PROBLEMATIC CODE PATTERNS
// ⚠️ PURPOSE: Test War Room's ability to detect and fix issues
// ⚠️ NOT ACTUAL MALWARE - SAFE BY DESIGN WITH LIMITS
// ⚠️ DO NOT REMOVE SAFETY LIMITS

// CHAOS SEED v1.0 - Educational Demonstration
// Demonstrates problematic code patterns for AI evolution testing
// Contains: Memory leaks, recursion risks, thread issues
// Safety: Built-in limits prevent actual harm

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>

// SAFETY LIMITS (DO NOT REMOVE)
#define MAX_DEPTH 10        // Prevents deep recursion
#define MAX_THREADS 4       // Limits thread creation
#define MAX_ITERATIONS 1000 // Caps loops

// ═══════════════════════════════════════════════════════════
// PROBLEMATIC PATTERN 1: MEMORY LEAK
// Allocates memory without freeing - caller must manage
// ═══════════════════════════════════════════════════════════
int* leak_fibonacci(int n) {
    if (n <= 1) {
        int* result = malloc(sizeof(int));
        if (!result) return NULL;
        *result = n;
        return result;
    }
    
    int* a = leak_fibonacci(n - 1);
    int* b = leak_fibonacci(n - 2);
    
    if (!a || !b) {
        free(a);
        free(b);
        return NULL;
    }
    
    int* result = malloc(sizeof(int));
    if (!result) {
        free(a);
        free(b);
        return NULL;
    }
    
    *result = *a + *b;
    free(a);
    free(b);
    // NOTE: result is returned without being freed - memory leak!
    return result;
}

// ═══════════════════════════════════════════════════════════
// PROBLEMATIC PATTERN 2: UNBOUNDED RECURSION RISK
// Without depth check, could overflow stack
// ═══════════════════════════════════════════════════════════
int fib_recursive(int n, int depth) {
    // SAFETY: Depth limiter prevents stack overflow
    if (depth > MAX_DEPTH) {
        printf("⚠️ Recursion depth limit reached\n");
        return -1;
    }
    
    if (n <= 1) return n;
    
    // Unbounded recursion without optimization
    return fib_recursive(n - 1, depth + 1) + fib_recursive(n - 2, depth + 1);
}

// ═══════════════════════════════════════════════════════════
// PROBLEMATIC PATTERN 3: THREAD SPAWNING WITHOUT CLEANUP
// Creates threads but management could be improved
// ═══════════════════════════════════════════════════════════
typedef struct {
    int n;
    int result;
} thread_data_t;

void* fib_thread_worker(void* arg) {
    thread_data_t* data = (thread_data_t*)arg;
    data->result = fib_recursive(data->n, 0);
    return NULL;
}

void fib_multithreaded(int n) {
    pthread_t threads[MAX_THREADS];
    thread_data_t data[MAX_THREADS];
    
    // SAFETY: Limited to MAX_THREADS
    for (int i = 0; i < MAX_THREADS; i++) {
        data[i].n = n + i;
        data[i].result = 0;
        
        if (pthread_create(&threads[i], NULL, fib_thread_worker, &data[i]) != 0) {
            printf("⚠️ Thread creation failed\n");
            return;
        }
    }
    
    // Join threads (good practice shown for educational purposes)
    for (int i = 0; i < MAX_THREADS; i++) {
        pthread_join(threads[i], NULL);
        printf("Thread %d: fib(%d) = %d\n", i, data[i].n, data[i].result);
    }
}

// ═══════════════════════════════════════════════════════════
// MAIN: DEMONSTRATES THE PATTERNS
// ═══════════════════════════════════════════════════════════
int main() {
    printf("╔════════════════════════════════════════════════════╗\n");
    printf("║  CHAOS SEED v1.0 - EDUCATIONAL TEST CASE          ║\n");
    printf("║  ⚠️  INTENTIONALLY PROBLEMATIC CODE PATTERNS      ║\n");
    printf("║  ✓  SAFE BY DESIGN (limits active)                ║\n");
    printf("╚════════════════════════════════════════════════════╝\n\n");
    
    int n = 5;
    
    // Pattern 1: Memory Leak Demonstration
    printf("Pattern 1: Memory Leak (malloc without proper cleanup)\n");
    int* leaked = leak_fibonacci(n);
    if (leaked) {
        printf("  Fibonacci(%d) = %d (leaked memory!)\n", n, *leaked);
        // NOTE: Should call free(leaked) here but demonstrating the leak
        free(leaked); // Actually freeing to be safe in demo
    }
    
    // Pattern 2: Recursion
    printf("\nPattern 2: Unbounded Recursion Risk\n");
    int result = fib_recursive(n, 0);
    printf("  Fibonacci(%d) = %d (with depth safety)\n", n, result);
    
    // Pattern 3: Threading
    printf("\nPattern 3: Thread Spawning\n");
    fib_multithreaded(3);
    
    printf("\n✅ Test case completed safely (limits active)\n");
    printf("📝 Expected War Room behavior:\n");
    printf("   1. Detect memory management issues\n");
    printf("   2. Identify recursion optimization opportunities\n");
    printf("   3. Improve thread management\n");
    printf("   4. Optimize algorithm (use iteration/memoization)\n");
    
    return 0;
}

// ═══════════════════════════════════════════════════════════
// EXPECTED EVOLUTION PATH
// ═══════════════════════════════════════════════════════════
// Gen 1: Compile with warnings about memory/recursion
// Gen 2: Add proper memory cleanup
// Gen 3: Replace recursion with iteration
// Gen 4: Implement memoization for optimization
// Gen 5: Add thread pool for better resource management
// Gen N: Clean, safe, optimized multi-threaded Fibonacci

// ═══════════════════════════════════════════════════════════
// SAFETY NOTES
// ═══════════════════════════════════════════════════════════
// 1. MAX_DEPTH prevents stack overflow
// 2. MAX_THREADS limits resource consumption
// 3. All malloc calls checked for NULL
// 4. Threads properly joined
// 5. No actual fork bombs or infinite loops
// 6. Educational demonstration only
