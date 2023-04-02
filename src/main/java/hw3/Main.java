package hw3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    static final String db = "jdbc:mysql://localhost:3306/hw3";
    static final String user = "root";
    static final String pass = System.getenv("pass");

    static Generator generator;
    static Inquirer inquirer;
    static Scanner read;

    public static void main(String[] args) {
        read = new Scanner(System.in);
        try {
            generator = new Generator(db, user, pass);
            inquirer = new Inquirer(db, user, pass);
            System.out.println("Connection success");
        } catch (Exception e) {
            System.out.println("error connecting to database, please restart the program");
            return;
        }

        generator.generateMajorsMinors(true);
        generator.generateMajorsMinors(false);

        printMenu();
        int in = read.nextInt();
        while (in != 8) {
            handle(in);

            System.out.println("Which query would you like to run? (1-8)");
            in = read.nextInt();
        }

        read.close();
        System.out.println("Goodbye.");

    }

    private static void printMenu() {
        System.out.println("Welcome to the University Database. Queries available:");
        System.out.println("1. Search students by name.");
        System.out.println("2. Search students by year.");
        System.out.println("3. Search students with a GPA >= threshold.");
        System.out.println("4. Search students with a GPA <= threshold.");
        System.out.println("5. Get department statistics.");
        System.out.println("6. Get class statistics.");
        System.out.println("7. Execute an arbitrary SQL query.");
        System.out.println("8. Exit the application.");
        System.out.println("Which query would you like to run? (1-8)");
    }

    private static void handle(int in) {
        switch (in) {
            case 1:
                searchByName();
                break;
            case 2:
                System.out.println("Query 2");
                break;
            case 3:
                System.out.println("Query 3");
                break;
            case 4:
                System.out.println("Query 4");
                break;
            case 5:
                System.out.println("Query 5");
                break;
            case 6:
                System.out.println("Query 6");
                break;
            case 7:
                System.out.println("Query 7");
                break;
            case 8:
                System.out.println("Query 8");
                break;
            default:
                System.out.println("Invalid input");
                break;
        }
    }

    private static void searchByName(){
        System.out.println("Enter a name to search for:");
        String name = read.next();
        try {
            String result = inquirer.searchByName(name);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query");
        }
    }
}