public class CountersWithPadding {
    static volatile int x;
    static long p1,p2,p3,p4,p5,p6; //Paddings
    static volatile int y;

    static void main() throws Exception{
        Thread t1 = new  Thread(() -> {
            for(int i=0; i<100_000; i++){
                CountersWithPadding.x++;
            }
        });
        Thread t2 = new  Thread(() -> {
            for(int i=0; i<100_000; i++){
                CountersWithPadding.y++;
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        System.out.println(CountersWithPadding.x);
        System.out.println(CountersWithPadding.y);
    }
}

