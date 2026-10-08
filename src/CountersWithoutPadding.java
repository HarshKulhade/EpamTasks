public class CountersWithoutPadding {
    static volatile int x;
    static volatile int y;

    static void main() throws Exception{
        Thread t1 = new  Thread(() -> {
            for(int i=0; i<100_000_000; i++){
                CountersWithoutPadding.x++;
            }
        });
        Thread t2 = new  Thread(() -> {
            for(int i=0; i<100_000_000; i++){
                CountersWithoutPadding.y++;
            }
        });
        long startTime = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long endTime = System.nanoTime();
        System.out.println(CountersWithoutPadding.x);
        System.out.println(CountersWithoutPadding.y);
        System.out.println("Time Taken : "+ (endTime-startTime)/1000_000+"ms");
    }
}
