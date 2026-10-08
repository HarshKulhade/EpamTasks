#include <iostream>
#include <thread>
#include <mutex>

using namespace std;

int counter = 0;
mutex m;

void increment() {
    for (int i = 0; i < 1'000'000; ++i) {
        lock_guard<mutex> lock(m);
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