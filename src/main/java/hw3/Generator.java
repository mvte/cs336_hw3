package hw3;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Generator {
    Connection dbcon;

    public Generator(String db, String user, String pass) throws Exception {
        dbcon = DriverManager.getConnection(db, user, pass);
    }

    /**
     * test constructor
     */
    private Generator() {
    }

    /**
     * Generates and inserts into database 104 names.
     * @param reset true if we want to reset the values already in the table
     */
    public void generateNames(boolean reset) {
        final String endpoint = "https://randommer.io/api/Name";
        final String query = "?nameType=fullname&quantity=104";
        final String api_key = System.getenv("key");

        JSONArray raw = retrieve(endpoint, query, api_key);
        if(raw == null) {
            System.out.println("something went wrong retrieving names");
            return;
        }

        try {
            studentInsert(raw, reset);
        } catch (Exception e) {
            System.out.println("something went wrong inserting values into db");
        }

        System.out.println("student db insertion success");
    }

    /**
     * retrieves an array of json objects from an api
     * @param endpoint api endpoint
     * @param query api query
     * @param api_key api key
     * @return an array of json objects, or null if not successful
     */
    private JSONArray retrieve(String endpoint, String query, String api_key) {
        try {
            URL url = new URL(endpoint + query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if(api_key != null)
                connection.setRequestProperty("X-Api-Key", api_key);

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                Scanner read = new Scanner(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();

                while (read.hasNext()) {
                    response.append(read.nextLine());
                }
                read.close();

                JSONParser parse = new JSONParser();
                return (JSONArray) parse.parse(String.valueOf(response));
            } else {
                System.out.println("connection denied - status code: " + statusCode);
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void studentInsert(JSONArray arr, boolean reset) throws SQLException {
        if(reset) {
            Statement stmt = dbcon.createStatement();
            stmt.executeUpdate("delete from students");
        }

        String s = "insert into students values(?, ?, ?)";
        PreparedStatement p = dbcon.prepareStatement(s);
        for(Object obj : arr) {
            String full_name = obj.toString();
            String hash = String.valueOf(Math.abs(full_name.hashCode()));
            int id;
            if(hash.length() < 9) {
                while(hash.length() != 9) hash += "0";
                id = Integer.parseInt(hash);
            } else {
                id = Integer.parseInt(hash.substring(0,9));
            }
            String[] fl = full_name.split(" ");

            while(true) {
                p.clearParameters();
                p.setString(1, fl[0]);
                p.setString(2, fl[1]);
                p.setInt(3, id);

                try {
                    p.executeUpdate();
                    break;
                } catch(SQLIntegrityConstraintViolationException e) {
                    id++;
                }
            }
        }
    }

    /**
     * Generates and inserts classes into the classes database. Uses the Rutgers SOC API to create
     * class names.
     * @param reset
     */
    public void generateClasses(boolean reset) {
        final String[] subjectCodes = {"119", "160", "198", "358", "750", "640"};
        final String endpoint = "http://sis.rutgers.edu/oldsoc/courses.json";
        final String queryFormat = "?subject=%s&semester=92023&campus=NB&level=UG";

        JSONArray raw;
        for(String code : subjectCodes) {
            String query = String.format(queryFormat, code);
            if ((raw = retrieve(endpoint, query, null)) == null) {
                System.out.println("could not retrieve courses");
                break;
            }
            try {
                buildAndInsertClasses(raw, reset);
            } catch(Exception e) {
                System.out.println("something went wrong inserting values into classes");
                e.printStackTrace();
            }

            reset = false;
        }

        System.out.println("classes inserted successfully");
    }

    private void buildAndInsertClasses(JSONArray arr, boolean reset) throws SQLException {
        if(reset) {
            Statement stmt = dbcon.createStatement();
            stmt.executeUpdate("delete from classes");
        }

        String s = "insert into classes values (?, ?)";
        PreparedStatement p = dbcon.prepareStatement(s);

        for(Object obj : arr) {
            JSONObject course = (JSONObject)obj;

            String title = (String)course.get("expandedTitle");
            if(title == null || title.isBlank()) {
                continue;
            }
            Object courseCredits = course.get("credits");
            if (!(courseCredits instanceof Long) || (Long) courseCredits < 3) {
                continue;
            }
            long credits = (long)course.get("credits");

            title = title.replaceAll("\\s+", " ").trim();

            try {
                p.clearParameters();
                p.setString(1, title);
                p.setInt(2, (int)credits);
                p.executeUpdate();
            } catch(SQLIntegrityConstraintViolationException e) {
                System.out.println("primary key:" + title + " - is already in the database!");
            }
        }
    }

    public void generateMajors() {
        try {
            Statement deptStmt = dbcon.createStatement();
            ResultSet depts = deptStmt.executeQuery("select name from departments");
            ArrayList<String> deptStrings = new ArrayList<>();
            while(depts.next()) {
                deptStrings.add(depts.getString(1));
            }

            Statement studentsStmt = dbcon.createStatement();
            ResultSet students = studentsStmt.executeQuery("select id from students");

            int id;
            String s = "insert into majors values(?, ?)";
            PreparedStatement p = dbcon.prepareStatement(s);
            while(students.next()) {
                id = students.getInt("id");

                ArrayList<String> majors = pickNoReplacement(deptStrings, 1.5, 0.6);
                for(String maj : majors) {
                    p.clearParameters();
                    p.setInt(1, id);
                    p.setString(2, maj);
                    p.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> pickNoReplacement(ArrayList<String> list, double mean, double stddev) {
        ArrayList<String> result = new ArrayList<>();
        ArrayList<String> copy = (ArrayList<String>)list.clone();
        Random rand = new Random();
        int numMajors = Math.abs((int)(rand.nextGaussian(mean, stddev)));

        for(int i = 0; i < numMajors; i++) {
            int pick = rand.nextInt(copy.size()-1);
            result.add(copy.remove(pick));
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        final String db = "jdbc:mysql://localhost:3306/hw3";
        final String user = "root";
        final String pass = System.getenv("pass");

        Generator gen = new Generator(db, user, pass);
    }
}

/*
students
    use an api to generate 100 names
    for their id, truncate the hashcode of their first name last name to 9 digits
        attempt insert. if fail, increment their hashcode by 1

classes
    should have 5*8 = 40ish classes
    make 10-15 of those 4 credits, 3 credits rest
    use rutgers api to grab a random set of 50 classes
        ideally grab first 10 classes from each department

departments
    Bio - Busch, Chem - CAC, CS - Livi, Eng - CD, Math - Busch, Phys - CD

majors
    normal distribution centered around 1, with std of 0.8
    take abs value
    this should be the number of majors a student pursues, randomly selected from the list of departments

minors
    normally distributed with expected value of 0, std of 0.8
    take abs value
    this should be the number of minors a student pursues, randomly selected from the list of departments

is taking
    select 4-5 classes from classes database

has taken
    26 students per year of school
    13 will have taken one extra semester of courses in their respective years
    iterate 104/13 times
        each iteration, trim random 13 students
        add 4-5 classes (4 or 5 randomly generated)
 */
