t1 starts ───────────────→ FINISH
t2 starts ─────────────────────→ FINISH
                             
main:
  start t1
      ↓
  start t2
      ↓
  t1.join() → WAITING 
      ↓
  t1 finishes
      ↓
  t2.join() → WAITING if t2 isn't finished yet
      ↓
  t2 finishes
      ↓
  print counter


----------------------

g++ -std=c++17 -O0 -pthread race.cpp -o race

Meaning:

-std=c++17 → C++17
-O0 → no optimization
-pthread → thread support
-o race → output executable called race

------------------------

for i in {1..20}; do ./race; done.... //multiple repetition

-------------------------
ThreadSanitizer: PROVE the Race

g++ -std=c++17 -O1 -g -fsanitize=thread -pthread race.cpp -o race-tsan

---------------------------

Multithreaded C++ Program
        ↓
Multiple threads access shared data
        ↓
T1 → counter++
T2 → counter++
        ↓
Both access `counter` concurrently
        ↓
Data Race Occurs
        ↓
ThreadSanitizer detects the race
        ↓
Reports:
  → T1: Write
  → T2: Read
  → Shared variable: `counter`
  → Location: race.cpp:8
        ↓
Incorrect result possible
        ↓
Expected → 2,000,000
Actual   → 1,000,000
        ↓
Fix
        ↓
Use `std::atomic` OR `std::mutex`

-----------------------

❌ UNSAFE
race.cpp
    ↓
shared int
    ↓
2 threads modify it
    ↓
data race


🔍 PROOF
ThreadSanitizer
    ↓
detects data race


✅ FIX
atomic.cpp
    ↓
std::atomic<int>
    ↓
safe concurrent increment
    ↓
Expected = Actual


-----------------------

Java Instruction Reordering Test

InstructionReorderingTest.java demonstrates how two threads can read stale values when they access shared variables without synchronization.

The variables `a`, `b`, `x`, and `y` are shared between both threads. The test resets them to zero, then starts two threads:

- Thread A sets `a` to 1 and then copies `b` into `x`.
- Thread B sets `b` to 1 and then copies `a` into `y`.

The main thread calls `join()` on both threads so it waits until they finish before checking the results. However, `join()` does not make the two worker threads' individual reads and writes happen in a fixed order relative to each other. Since the shared variables are not `volatile` and the workers do not use synchronization, both reads can see the original zero values (`x == 0` and `y == 0`).

If both values are zero, the test throws an `AssertionError`; otherwise, it prints `Test passed.` This is one execution of the test, so the both-zero result is possible but is not guaranteed on every run.