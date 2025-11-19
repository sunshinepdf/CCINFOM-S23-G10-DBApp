package Model;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    //these values are temp: change user and pass strings to mysql user and pass
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String USER = "root";
    private static final String PASS = "pass";

    public static Connection connectDB() {
        try{
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
