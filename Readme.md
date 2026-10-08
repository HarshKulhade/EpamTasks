## False Sharing

**False sharing** is a performance problem that can happen when **two threads are working on different variables, but those variables happen to be stored in the same CPU cache line**.

For example:

```
static volatile int x;
static volatile int y;
```

- Thread 1 updates `x`
- Thread 2 updates `y`
- `x` and `y` are different variables, so it looks perfectly safe.
- But if they are on the **same cache line**, the CPU keeps invalidating and reloading that cache line between cores.

This creates unnecessary CPU work and can slow down the application.

That's essentially **false sharing**.

---

## Padding

**Padding** is a technique used to prevent false sharing.

We add unused fields between frequently updated variables:

```
static volatile int x;

static long p1, p2, p3, p4, p5, p6; // Padding

static volatile int y;
```

The idea is to create enough space so that `x` and `y` end up on **different cache lines**.

```
Without padding:

Cache Line
┌─────────────────────────┐
│    x    │    y          │
└─────────────────────────┘
       ↑
   False sharing

With padding:

Cache Line 1                  Cache Line 2
┌──────────────────┐          ┌──────────────┐
│ x + padding      │          │ y            │
└──────────────────┘          └──────────────┘
       ↑                            ↑
    Thread 1                     Thread 2
```

So each thread can update its own cache line without constantly interfering with the other thread.

---

## Instruction Reordering

`InstructionReorderingTest` demonstrates a two-thread read/write pattern using the shared, non-`volatile` variables `a`, `b`, `x`, and `y`.

- Thread A writes `1` to `a`, then reads `b` into `x`.
- Thread B writes `1` to `b`, then reads `a` into `y`.
- Both threads are started and joined before the results are checked.

Because the threads do not synchronize with each other while accessing these variables, the Java Memory Model does not guarantee that each thread will observe the other thread's write. In particular, the outcome `x == 0` and `y == 0` is possible.

The current test throws an `AssertionError` if both values are zero; otherwise, it prints `Test passed.` This is a single run, so it does not guarantee that any particular outcome will occur each time.
