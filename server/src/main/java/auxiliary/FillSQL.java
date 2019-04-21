package auxiliary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class FillSQL {

    public static void main(String[] args) {
            createTable();
            fillTable();
    }

    private static void createTable() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:server\\usersDB.db");
            Statement statement = connection.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS users (ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
                    " login TEXT NOT NULL UNIQUE, password INTEGER NOT NULL, folder TEXT, name TEXT, surname, birthday DATE)");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

    private static void fillTable() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:server\\usersDB.db");
            Statement statement = connection.createStatement();
            String login1 = "Ivan84";
            String login2 = "BeautyRose";
            String login3 = "Filip1";
            int password1 = "pass1".hashCode();
            int password2 = "pass2".hashCode();
            int password3 = "pass3".hashCode();
            statement.execute("INSERT INTO users (login, password) VALUES ('" + login1 + "', " + password1 + " )");
            statement.execute("INSERT INTO users (login, password) VALUES ('" + login2 + "', " + password2 + " )");
            statement.execute("INSERT INTO users (login, password) VALUES ('" + login3 + "', " + password3 + " )");
            connection.close();
        } catch (SQLException e) {
            System.out.println("Something went wrong: " + e.getMessage());
        }
    }

}
