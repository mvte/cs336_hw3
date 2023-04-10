package hw3;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    static String db = "jdbc:mysql://localhost:3306/hw3";
    static String user = "root";
    static String pass = System.getenv("pass");

    static Generator generator;
    static Inquirer inquirer;
    static Scanner read;

    public static void main(String[] args) {
        if(args.length == 3) {
            db = "jdbc:mysql://" + args[0];
            user = args[1];
            pass = args[2];
        }

        read = new Scanner(System.in);
        try {
            generator = new Generator(db, user, pass);
            inquirer = new Inquirer(db, user, pass);
            System.out.println("Connection success");
        } catch (Exception e) {
            System.out.println("Error connecting to database, please check your arguments and restart the program");
            return;
        }

        try {
            generator.initializeViews();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not create views, please restart the program");
            return;
        }

        printMenu();
        String in = read.nextLine();
        while (!in.equals("8")) {
            handle(in);

            System.out.println("Which query would you like to run? (1-8)");
            in = read.next();

            read.nextLine();
        }

        read.close();
        System.out.println("Goodbye.");

        generator.destroyViews();
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

    private static void handle(String in) {
        switch (in) {
            case "1" -> searchByName();
            case "2" -> searchByYear();
            case "3" -> searchByGPA(true);
            case "4" -> searchByGPA(false);
            case "5" -> getDeptStats();
            case "6" -> getClassStats();
            case "7" -> customQuery();
            default -> System.out.println("Invalid input");
        }
    }

    private static void searchByName(){
        System.out.println("Enter a name to search for:");
        String name = read.nextLine();
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
        String year = read.nextLine().toLowerCase();
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
        String dept = read.nextLine();
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
        String name = read.nextLine();
        try {
            String result = inquirer.getClassStats(name);
            System.out.println(result);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query (class not found)");
        }
    }

    private static void customQuery() {
        System.out.println("Please enter the query.");
        String query = read.nextLine();
        try {
            inquirer.customQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error executing query");
        }
    }
}