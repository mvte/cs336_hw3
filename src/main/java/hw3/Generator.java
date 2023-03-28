package hw3;
import java.sql.*;

public class Generator {
    Connection con;

    public Generator(String db, String user, String pass) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        con = DriverManager.getConnection(db, user, pass);

    }

}

/*
students
    use an api to generate 100 names
    for their id, truncate the hashcode of their first name last name to 9 digits
        attempt insert, if fail, increment their hashcode by 1

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
