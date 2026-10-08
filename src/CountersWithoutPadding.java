public class CountersWithoutPadding {
    static volatile int x;
    static volatile int y;

    static void main() throws Exception{
        Thread t1 = new  Thread(() -> {
            for(int i=0; i<100_000; i++){
                CountersWithoutPadding.x++;
            }
        });
        Thread t2 = new  Thread(() -> {
            for(int i=0; i<100_000; i++){
                CountersWithoutPadding.y++;
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(CountersWithoutPadding.x);
        System.out.println(CountersWithoutPadding.y);
    }
}
