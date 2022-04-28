package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.data.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String url;
    private static final String user;
    private static final String password;

    static {
        url = Config.getProperty("dbUrl", "");
        user = Config.getProperty("dbUser", "");
        password = Config.getProperty("dbPass", "");
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
}
