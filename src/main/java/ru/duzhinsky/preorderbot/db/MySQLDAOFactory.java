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
        if(connectionPool == null) throw new SQLException("Connection pool was not instantiated");
        return new UserDaoMySQL(connectionPool.getConnection());
    }

    public static AuthenticationDAO getAuthenticationDAO() throws SQLException {
        if(connectionPool == null) throw new SQLException("Connection pool was not instantiated");
        return new AuthenticationDaoMySQL(connectionPool.getConnection());
    }

    public static TelegramDao getTelegramDao() throws SQLException {
        if(connectionPool == null) throw new SQLException("Connection pool was not instantiated");
        return new TelegramDaoMySQL(connectionPool.getConnection());
    }
}
