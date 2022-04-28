package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.data.Config;

import java.sql.SQLException;

public class MySQLDAOFactory {
    private static ConnectionPool connectionPool;

    static {
        String url = Config.getProperty("dbUrl", "");
        String user = Config.getProperty("dbUser", "");
        String password = Config.getProperty("dbPass", "");
        try {
            connectionPool = new SimpleConnectionPool(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserDao getUserDao() throws SQLException {
        return new UserDaoMySQL(connectionPool.getConnection());
    }
}
