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

Think of it like this:

> Two people are working on different documents, but both documents are kept in the same folder. Every time one person changes their document, the other person's copy of the folder becomes outdated.

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