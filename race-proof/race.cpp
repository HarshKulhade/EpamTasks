#include <iostream>
#include <thread>
using namespace std;

int counter = 0; // both threads access the same counter variable

void increment(){ //Each thread tries to increment counter 1,000,000 times
    for(int i = 0; i < 1'000'000; ++i){
        ++counter;
    }
}

int main(){
    thread t1(increment); // t1 working
    thread t2(increment); // t2 working

    t1.join(); // waits for  t1
    t2.join(); // will reach only after t1 finishes

    cout << "Expected: 2000000\n";
    cout << "Actual:  " << counter << '\n';

    return 0;

}