public class InstructionReorderingTest {

    static int x, y, a, b;

    public void init() {
        x = y = a = b = 0;
    }

    public void test() throws InterruptedException {
        Thread threadA = new Thread(() -> {
            a = 1;
            x = b;
        });
        Thread threadB = new Thread(() -> {
            b = 1;
            y = a;
        });

        threadA.start();
        threadB.start();

        threadA.join();
        threadB.join();

        if (x == 0 && y == 0) {
            throw new AssertionError("Instruction reordering was not observed: x and y are both zero.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        InstructionReorderingTest test = new InstructionReorderingTest();
        test.init();
        test.test();
        System.out.println("Test passed.");
    }
}

