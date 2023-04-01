package hw3;

public class Main {
    static final String db = "jdbc:mysql://localhost:3306/hw3";
    static final String user = "root";
    static final String pass = System.getenv("pass");

    public static void main(String[] args) {
        Generator generator = null;
        try {
            generator = new Generator(db, user, pass);
            System.out.println("connection success");
        } catch (Exception e) {
            System.out.println("error connecting to database");
        }

        //generator.generateNames(true);
        //generator.generateClasses(true);
        //generator.generateMajorsMinors(true);
        //generator.generateMajorsMinors(false);
        //generator.generateTaking();
        generator.generateTaken();
    }
}