package cloudNettyServer;

import java.sql.*;

/*
* Authorization method. Send where login and password.
* If login doesn't exist method sends "Login didn't found"
* If login and password don't match each other method sends "Wrong password"
* If everything is Ok method sends "Access is allowed"
*/

public class AuthService {
    public static void main(String[] args) {
        int password = "pass1".hashCode();
        System.out.println(getAccess("Ivan84", password));
    }

    public static String getAccess(String login, int password) {
        String log = null;
        try {
            String sql = String.format("SELECT login FROM users WHERE login = '%s' AND password = '%s'", login, password);
            Connection connection = DriverManager.getConnection("jdbc:sqlite:server\\usersDB.db");

            //return "login doesn't exist"
            Statement statement = connection.createStatement();
            ResultSet exist = statement.executeQuery(sql);
            log = exist.getString(1);

            connection.close();
        } catch (SQLException e) {
            return "Wrong login or password";
        }
        return log;
    }
}
