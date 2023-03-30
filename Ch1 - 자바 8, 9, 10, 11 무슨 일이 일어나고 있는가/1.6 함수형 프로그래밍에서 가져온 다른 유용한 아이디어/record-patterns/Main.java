
public class Main {
    public static void main(String[] args) {

        System.out.println("hello world!");

        Point p = new Point(1, 2);

        printSum(p);
        printSum((Object) p);
        printSum((Object) 4);

        printSumDeconstruct(p);
        printSumDeconstruct((Object) p);
        printSumDeconstruct((Object) 4);

    }

    record Point(int x, int y) {
    }

    static void printSum(Object obj) {
        if (obj instanceof Point p) {
            int x = p.x();
            int y = p.y();
            System.out.println(x + y);
        } else {
            System.out.println("Not supported.");
        }
    }

    static void printSumDeconstruct(Object obj) {
        if (obj instanceof Point(int x,int y)) {
            System.out.println(x + y);
        } else {
            System.out.println("Not supported.");
        }

    }

}