package hw3;

public class Main {
    static final String db = "";
    static final String user = "root";
    static final String pass = System.getenv("pass");

    public static void main(String[] args) {
        Generator generator = null;
        try {
            generator = new Generator(db, user, pass);
        } catch (Exception e) {
            System.out.println("Error connecting to database");
        }

        generator.generateNames(false);
    }
}