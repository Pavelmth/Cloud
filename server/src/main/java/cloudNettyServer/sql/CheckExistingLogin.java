package cloudNettyServer.sql;

import java.sql.*;

/*
 * If login doesn't exist method sends '-1'
 * If everything is Ok method return ID
 */

public class CheckExistingLogin {
    public int getLogin(String login) {
        Integer clientFolder = null;
        try(Connection connection = DriverManager.getConnection("jdbc:sqlite:server\\usersDB.db")) {
            String sqlStr = "SELECT id FROM users WHERE login = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStr);
            preparedStatement.setString(1, login);
            ResultSet exist0 = preparedStatement.executeQuery();
            clientFolder = Integer.parseInt(exist0.getString(1));
        } catch (SQLException e) {
            return -1;
        }
        return clientFolder;
    }
}
