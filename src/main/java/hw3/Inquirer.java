package hw3;

import java.sql.*;

public class Inquirer {
    private final Connection dbcon;

    public Inquirer(String db, String user, String pass) throws SQLException {
        dbcon = DriverManager.getConnection(db, user, pass);
    }

    /**
     * Returns a string containing the student's name, id, major(s), minor(s), gpa, and credit. Information about
     * credits and gpa is taken from the student_credits and student_grades views, respectively. These views are
     * generated by the Generator class upon program startup.
     * @param id the id of the student we're finding info about
     * @return a string containing the student's name, id, major(s), minor(s), gpa, and credit
     * @throws SQLException if there is an error with the database
     */
    private String getStudentInfo(int id) throws SQLException {
        String q1 = "select * from students where id = ?";
        String q2 = "select * from majors where sid = ?";
        String q3 = "select * from minors where sid = ?";
        String q4 = "select * from student_credits where sid = ?";
        String q5 = "select * from student_grades where id = ?";
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

        PreparedStatement st4 = dbcon.prepareStatement(q4);
        st4.setInt(1, id);
        ResultSet credits = st4.executeQuery();

        PreparedStatement st5 = dbcon.prepareStatement(q5);
        st5.setInt(1, id);
        ResultSet grades = st5.executeQuery();

        if (!basic.next()) {
            return "Student not found";
        }
        result += basic.getString("last_name") + ", " + basic.getString("first_name") + "\n" +
                "ID: " + basic.getInt("id") + "\n";

        String majString = "";
        while (majors.next()) {
            majString += majors.getString("dname") + ", ";
        }
        result += majString.isEmpty() ? "Major: None\n" : "Major: " + majString.substring(0, majString.length() - 2) + "\n";

        String minString = "";
        while (minors.next()) {
            minString += minors.getString("dname") + ", ";
        }
        result += minString.isEmpty() ? "Minor: None\n" : "Minor: " + minString.substring(0, minString.length() - 2) + "\n";

        if (grades.next())
            result += "GPA: " + String.format("%.2f", grades.getBigDecimal("GPA")) + "\n";
        else
            result += "GPA: Has not completed any classes\n";

        if (credits.next())
            result += "Credits: " + credits.getInt("creditsTaken") + "\n";
        else
            result += "Credits: 0\n";

        return result;
    }


    /**
     * Returns a string containing the student's name, id, major(s), minor(s), gpa, and credit if their either their
     * first or last name contains the given string.
     * @param name the substring to search for
     * @return a string containing information about students that match the given substring
     * @throws SQLException if the query fails or there is an error with the database
     */
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

        return res.length() <= 1 ? "No students found" : res.substring(0, res.length()-1);
    }

    /**
     * Returns a string containing the student's name, id, major(s), minor(s), gpa, and credit if they are
     * in the given year. The year a student is in is determined by how many credits they have.
     * @param year the year to search for
     * @return a string containing information about students in the given year
     * @throws SQLException if the query fails
     */
    public String searchByYear(String year) throws SQLException {
        String query = "select sid"
                + " from student_credits"
                + " where creditsTaken > ? and creditsTaken < ?";

        PreparedStatement stmt = dbcon.prepareStatement(query);
        switch (year.toLowerCase()) {
            case "fr" -> {
                stmt.setInt(1, 0);
                stmt.setInt(2, 29);
            }
            case "so" -> {
                stmt.setInt(1, 30);
                stmt.setInt(2, 59);
            }
            case "jr" -> {
                stmt.setInt(1, 60);
                stmt.setInt(2, 89);
            }
            case "sr" -> {
                stmt.setInt(1, 90);
                stmt.setInt(2, 200);
            }
            default -> {
                return "Invalid year";
            }
        }

        ResultSet rs = stmt.executeQuery();

        String res = "";
        while (rs.next()) {
            res += getStudentInfo(rs.getInt("sid")) + "\n";
        }
        return res.substring(0, res.length()-1);
    }

    /**
     * Returns a string containing the student info for all students with a GPA between min and max. Since the
     * requirements only ask for a minimum or maximum threshold, a default min or max is used if the other is not.
     * @param min minimum gpa
     * @param max maximum gpa
     * @return string containing student info for all students with a GPA between min and max
     * @throws SQLException if the query fails
     */
    public String searchByGPA(double min, double max) throws SQLException {
        String query = "select id from student_grades where GPA >= ? and GPA <= ?";
        PreparedStatement stmt = dbcon.prepareStatement(query);
        stmt.setDouble(1, min);
        stmt.setDouble(2, max);
        ResultSet rs = stmt.executeQuery();

        String res = "";
        while (rs.next()) {
            res += getStudentInfo(rs.getInt("id")) + "\n";
        }
        return !res.isEmpty() ? res.substring(0, res.length()-1) : "No students found";
    }

    /**
     * Returns a string containing the number of students and average GPA for the given department.
     * @param dept The department to get stats for.
     * @return String containing the number of students and average GPA for the given department.
     * @throws SQLException if the query fails
     */
    public String getDeptStats(String dept) throws SQLException {
        String query = "select numStudents, avgGpa from deptStats where dname = ?";
        PreparedStatement stmt = dbcon.prepareStatement(query);
        stmt.setString(1, dept);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return "Number of students: " + rs.getInt("numStudents") + "\n" +
                    "Average GPA: " + String.format("%.2f", rs.getBigDecimal("avgGpa")) + "\n";
        }

        return "No such department exists";
    }

    /**
     * Returns a string containing the number of students taking a class, and the grades of students who have
     * previously taken the class.
     * @param course The course to get stats for.
     * @return String containing the number of students taking a class, and the grades of students who have already taken it
     * @throws SQLException if the query fails
     */
    public String getClassStats(String course) throws SQLException{
        course = course.toUpperCase();
        String result = course + "\n";
        String q1 = "select count(t.sid) as numStudents " +
            "from classes c left outer join isTaking t on t.name = c.name " +
            "where c.name = ? " +
            "group by c.name";
        String q2 = "select t.grade, count(t.grade) as numStudents " +
                "from hasTaken t left outer join classes c on c.name = t.name " +
                "where c.name = ? " +
                "group by t.grade ";

        PreparedStatement p1 = dbcon.prepareStatement(q1);
        p1.setString(1, course);
        ResultSet num = p1.executeQuery();
        if(!num.next())
            return "No such course exists";
        result += num.getInt("numStudents") + " students currently enrolled. \n" +
                "Grades of previous enrollees: \n";

        PreparedStatement p2 = dbcon.prepareStatement(q2);
        p2.setString(1, course);
        ResultSet grades = p2.executeQuery();
        while(grades.next()){
            result += grades.getString("grade") + " " + grades.getInt("numStudents") + "\n";
        }

        return result;
    }

    /**
     * Prints out the result of a custom query.
     * Uses the DBTablePrinter class, shout out to htorun for making this class public.
     * @param query The query to execute
     * @throws SQLException if the query fails
     */
    public void customQuery(String query) throws SQLException {
        Statement stmt = dbcon.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        DBTablePrinter.printResultSet(rs);
    }
}
