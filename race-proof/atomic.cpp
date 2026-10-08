#include <iostream>
#include <thread>
#include <atomic>
using namespace std;

atomic<int> counter{0}; // increment will perform atomically

void increment() {
    for (int i = 0; i < 1'000'000; ++i) {
        ++counter;
    }
}

int main() {
    thread t1(increment);
    thread t2(increment);

    t1.join();
    t2.join();

    cout << "Expected: 2000000\n";
    cout << "Actual:   " << counter << '\n';

    return 0;
}