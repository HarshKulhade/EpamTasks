import java.util.concurrent.atomic.AtomicLong;

public class SimpleConcurrency {

    // 1. Synchronized counter
    static class SyncCounter {
        private long count = 0;

        synchronized void increment() {
            count++;
        }

        synchronized long get() {
            return count;
        }
    }

    // 2. CAS counter
    static class CasCounter {
        private final AtomicLong count = new AtomicLong(0);

        void increment() {
            long oldValue;
            do {
                oldValue = count.get();
            } while (!count.compareAndSet(oldValue, oldValue + 1));
        }

        long get() {
            return count.get();
        }
    }

    // 3. Simple ring buffer
    static class RingBuffer {
        private final int[] buffer = new int[5];
        private int head = 0;
        private int tail = 0;
        private int size = 0;

        synchronized void add(int value) {
            if (size == buffer.length) {
                System.out.println("Buffer is full");
                return;
            }

            buffer[tail] = value;
            tail = (tail + 1) % buffer.length;
            size++;
        }

        synchronized int remove() {
            if (size == 0) {
                System.out.println("Buffer is empty");
                return -1;
            }

            int value = buffer[head];
            head = (head + 1) % buffer.length;
            size--;
            return value;
        }
    }

    public static void main(String[] args) {
        SyncCounter sync = new SyncCounter();
        CasCounter cas = new CasCounter();

        // Test synchronized counter
        sync.increment();
        sync.increment();
        System.out.println("Synchronized: " + sync.get());

        // Test CAS counter
        cas.increment();
        cas.increment();
        System.out.println("CAS: " + cas.get());

        // Test ring buffer
        RingBuffer buffer = new RingBuffer();
        buffer.add(10);
        buffer.add(20);

        System.out.println("Removed: " + buffer.remove());
        System.out.println("Removed: " + buffer.remove());
    }
}