public class CountersWithPadding {
    static volatile int x;
    static long p1,p2,p3,p4,p5,p6; //Paddings
    static volatile int y;

    static void main() throws Exception{
        Thread t1 = new  Thread(() -> {
            for(int i=0; i<100_000_00; i++){
                CountersWithPadding.x++;
            }
        });
        Thread t2 = new  Thread(() -> {
            for(int i=0; i<100_000_00; i++){
                CountersWithPadding.y++;
            }
        });
        long startTime = System.nanoTime();
        t1.start();
        t2.start();
        t1.join();
        t2.join();
        long endTime = System.nanoTime();
        System.out.println(CountersWithPadding.x);
        System.out.println(CountersWithPadding.y);
        System.out.println("Time Taken : "+ (endTime-startTime)/100_000+"ms");
    }
}

