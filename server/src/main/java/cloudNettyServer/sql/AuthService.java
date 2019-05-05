package cloudNettyServer.sql;

import java.sql.*;

/*
* If login and password don't match each other method return '-1'
* If everything is Ok method return ID which is the number of the client folder
*/

public class AuthService {
    public int getAccess(String login, int password) {
        Integer clientFolder = null;
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:server\\usersDB.db")) {
            String sqlStr = "SELECT id FROM users WHERE login = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStr);
            preparedStatement.setString(1, login);
            preparedStatement.setInt(2, password);
            ResultSet exist0 = preparedStatement.executeQuery();
            clientFolder = Integer.parseInt(exist0.getString(1));

        } catch (SQLException e) {
            return -1;
        }
        return clientFolder;
    }
}
