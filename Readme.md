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

## Data Race

A **data race** happens when **two or more threads access the same shared data at the same time, and at least one of them is modifying it**, without proper synchronization.

For example:

```java
static int counter = 0;
```

If two threads execute:

```java
counter++;
```

at the same time, it may look like both threads will simply increase the value.

But `counter++` is actually made up of multiple steps:

1. Read the current value
2. Add `1`
3. Write the new value back

So both threads can read the same old value before either one writes the updated value.

For example:

```text
Initial value = 0

Thread 1 → reads 0
Thread 2 → reads 0

Thread 1 → writes 1
Thread 2 → writes 1
```

The expected result was `2`, but the actual result can be `1`.

That's a **data race**.

### Why does it occur?

A data race mainly occurs because:

- Multiple threads are accessing shared data.
- At least one thread is modifying that data.
- There is no proper synchronization between those accesses.
- The operations can happen in an unpredictable order.

So even if the code looks correct when executed once, the result can change depending on how the threads are scheduled by the CPU.

---

## Visibility and Instruction Ordering

In a multithreaded program, one thread may update a variable, but another thread does not necessarily see that update immediately.

This is called a **visibility problem**.

For example:

```java
boolean running = true;
```

One thread may continuously check:

```java
while (running) {
    // do something
}
```

while another thread changes:

```java
running = false;
```

Without proper synchronization, the first thread may continue seeing the old value of `running`.

### Instruction Ordering

Modern CPUs and compilers can **reorder instructions** to improve performance, as long as the result remains correct from a single-threaded point of view.

But in multithreaded code, this can sometimes produce unexpected results because another thread may observe the operations in a different order than we expected.

For example, one thread might conceptually do:

```text
write data
write flag
```

But another thread could observe the flag before it safely observes the updated data if proper memory-ordering rules are not used.

This is why concurrency is not only about making operations atomic. We also need to make sure that **changes become visible to other threads and that important operations happen in the required order**.

---

## Lock-Free CAS Loop

A common technique used in lock-free programming is the **CAS loop**.

CAS stands for **Compare-And-Swap**.

The basic idea is:

1. Read the current value.
2. Calculate the new value.
3. Try to replace the old value with the new value using CAS.
4. If another thread changed the value before us, CAS fails.
5. Read the value again and retry.

For example:

```java
do {
    oldValue = value.get();
    newValue = oldValue + 1;
} while (!value.compareAndSet(oldValue, newValue));
```

The important part here is that we don't simply assume that our value is still valid.

If another thread changes the value between our read and CAS operation, `compareAndSet()` returns `false`, and the loop tries again.

So the code keeps retrying until one thread successfully updates the value.

---

## Why is it called Lock-Free?

It is called **lock-free** because the algorithm does not use a traditional lock such as:

```java
synchronized
```

or:

```java
Lock
```

A thread does not have to wait for another thread to release a lock.

Instead, threads use atomic operations such as **CAS** to update shared data.

The important idea is:

> Even if one thread gets delayed or stopped, some other thread can still make progress.

That's why it is called **lock-free**.

However, lock-free does **not** mean that every thread will always finish immediately. A particular thread may keep losing the CAS race and retry multiple times.

The guarantee is about **system-wide progress**, not necessarily individual-thread progress.

---

### In Simple Words

```text
Data Race
   ↓
Multiple threads access shared data incorrectly
   ↓
Unexpected or inconsistent result


Visibility
   ↓
One thread changes data
   ↓
Another thread may not immediately see the change


Instruction Ordering
   ↓
Compiler/CPU may reorder operations
   ↓
Other threads may observe operations differently


CAS Loop
   ↓
Read → Calculate → Compare & Swap
   ↓
If CAS fails → Retry


Lock-Free
   ↓
No traditional lock is used
   ↓
Threads use atomic operations
   ↓
At least one thread can keep making progress
```

