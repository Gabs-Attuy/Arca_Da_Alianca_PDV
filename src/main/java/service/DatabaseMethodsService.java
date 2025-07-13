package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import config.DBConfig;

/**
 *
 * @author jeffe
 */
public class DatabaseMethodsService {
    
    private static final String URL = DBConfig.getUrl();
    private static final String USER = DBConfig.getUser();
    private static final String PASSWORD = DBConfig.getPassword();
    
    public static Connection getConnection() throws Exception {
        Class.forName("org.postgresql.Driver");
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
