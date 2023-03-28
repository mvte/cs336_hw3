package hw3;

public class Main {
    static final String db = "";
    static final String user = "root";
    static final String pass = "micaela";
    public static void main(String[] args) {
        try {
            Generator generator = new Generator(db, user, pass);
        } catch (Exception e) {
            System.out.println("Error connecting to database");
        }
    }
}