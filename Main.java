public class Main {
    public static void main(String[] args) {

        Triangle t = new Triangle();
        t.makeVisible();

        t.walk(5);
        t.walk2(3);

        System.out.println("Area: " + t.area());
    }
}
