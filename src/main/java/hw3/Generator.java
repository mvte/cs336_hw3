package hw3;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Generator {
    Connection dbcon;

    public Generator(String db, String user, String pass) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        dbcon = DriverManager.getConnection(db, user, pass);
    }

    /**
     * test constructor
     */
    private Generator() {
    }

    /**
     * Generates and inserts into database 100 names.
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
            insert(raw, reset);
        } catch (Exception e) {
            System.out.println("something went wrong inserting values into db");
        }


    }

    private JSONArray retrieve(String endpoint, String query, String api_key) {
        try {
            URL url = new URL(endpoint + query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Api-Key", api_key);

            int statusCode = connection.getResponseCode();
            if (statusCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                JSONParser parse = new JSONParser();
                return (JSONArray) parse.parse(String.valueOf(response));
            } else {
                System.out.println("status code: " + statusCode);
                return null;
            }

        } catch (Exception e) {
            return null;
        }
    }

    private void insert(JSONArray arr, boolean reset) throws SQLException {
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


        }
    }

    public static void main(String[] args) {
        Generator gen = new Generator();
        gen.generateNames(false);
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
    normal distribution centered around 0, with std of 1.5
    take abs value. if 0, turn to 1

minors
    normally distributed with expected value of 0.0, std of 0.8
    take abs value

is taking
    select 4-5 classes from classes database

has taken
    26 students per year of school
    13 will have taken one extra semester of courses in their respective years
    iterate 104/13 times
        each iteration, trim random 13 students
        add 4-5 classes (4 or 5 randomly generated)
 */
