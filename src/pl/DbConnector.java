import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConnector {
    private static final String URL = "";
    private static final String user = "";
    private static final String password = "";

    public static Connection connect() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, user, password);
//            System.out.println("Połączono z bazą danych.");
        } catch (SQLException e) {
            System.out.println("SQLException: " + e.getMessage());
        }
        return connection;
    }
}