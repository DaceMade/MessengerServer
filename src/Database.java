import java.sql.*;

public class Database {

    private Connection connection;
    private Statement statement;

    public void connect() {
        final String URL = "jdbc:postgresql://localhost:5432/Users";
        final String LOGIN = "postgres";
        final String PASSWORD = "postgres";

        try {

            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, LOGIN, PASSWORD);
            statement = connection.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }



    public ResultSet newQuery(String query) throws SQLException {
        return statement.executeQuery(query);
    }

    public void downDataBase() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
