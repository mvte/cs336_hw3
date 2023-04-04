package hw3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                searchByYear();
                break;
            case 3:
                searchByGPA(true);
                break;
            case 4:
                searchByGPA(false);
                break;
            case 5:
                getDeptStats();
                break;
            case 6:
                System.out.println("Query 6");
                break;
            case 7:
                System.out.println("Query 7");
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

    private static void searchByYear() {
        List<String> ref = List.of("fr", "so", "jr", "sr");
        System.out.println("Please enter a year:");
        String year = read.next().toLowerCase();
        if(!ref.contains(year)){
            System.out.println("Invalid year");
            return;
        }

        try {
            String result = inquirer.searchByYear(year);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query");
        }
    }

    private static void searchByGPA(boolean min) {
        System.out.println("Please enter the threshold:");
        double gpa = read.nextDouble();
        while(gpa < 0 || gpa > 4.0){
            System.out.println("Invalid GPA, please enter a value between 0 and 4.0");
            gpa = read.nextDouble();
        }
        try {
            String result = min ? inquirer.searchByGPA(gpa, 4.1) : inquirer.searchByGPA(-0.1, gpa);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query");
        }
    }

    private static void getDeptStats() {
        System.out.println("Please enter a department name:");
        String dept = read.next();
        try {
            String result = inquirer.getDeptStats(dept);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query");
        }
    }

    private static void getClassStats() {
        System.out.println("Please enter a class name:");
        String name = read.next();
        try {
            String result = inquirer.getClassStats(name);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query");
        }
    }
}