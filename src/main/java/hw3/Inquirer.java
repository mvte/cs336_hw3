package hw3;

import java.sql.*;

public class Inquirer {
    private final Connection dbcon;

    public Inquirer(String db, String user, String pass) throws SQLException {
        dbcon = DriverManager.getConnection(db, user, pass);
    }

    private String getStudentInfo(int id) throws SQLException{
        String q1 = "select * from students where id = ?";
        String q2 = "select * from majors where sid = ?";
        String q3 = "select * from minors where sid = ?";
        String result = "";

        PreparedStatement st1 = dbcon.prepareStatement(q1);
        st1.setInt(1, id);
        ResultSet basic = st1.executeQuery();

        PreparedStatement st2 = dbcon.prepareStatement(q2);
        st2.setInt(1, id);
        ResultSet majors = st2.executeQuery();

        PreparedStatement st3 = dbcon.prepareStatement(q3);
        st3.setInt(1, id);
        ResultSet minors = st3.executeQuery();

        if (!basic.next()) {
            return "Student not found";
        }
        result += basic.getString("last_name") + ", " + basic.getString("first_name") + "\n" +
                "ID: " + basic.getInt("id") + "\n";

        String majString = "";
        while(majors.next()) {
            majString += majors.getString("dname") + ", ";
        }
        result += majString.isEmpty() ? "Major: No Major\n" : "Major: " + majString.substring(0, majString.length() - 2) + "\n";

        String minString = "";
        while(minors.next()) {
            minString += minors.getString("dname") + ", ";
        }
        result += minString.isEmpty() ? "Minor: No Minor\n" : "Minor: " + minString.substring(0, minString.length() - 2) + "\n";

        return result;
    }

    public String searchByName(String name) throws SQLException {
        String query = "select * from students where first_name like ? or last_name like ?";
        PreparedStatement stmt = dbcon.prepareStatement(query);
        stmt.setString(1, "%" + name + "%");
        stmt.setString(2, "%" + name + "%");
        ResultSet rs = stmt.executeQuery();

        String res = "";
        while(rs.next()) {
            res += getStudentInfo(rs.getInt("id")) + "\n";
        }

        return res;
    }

    public String searchByYear(int year) throws SQLException {
        String query = "select * from students where year = ?";
        PreparedStatement stmt = dbcon.prepareStatement(query);
        stmt.setInt(1, year);
        ResultSet rs = stmt.executeQuery();

        String res = "";
        while (rs.next()) {
            res += rs.getString("first_name") + " " + rs.getString("last_name") + " " + rs.getInt("id") + "\n";
        }
        return res;
    }



}
