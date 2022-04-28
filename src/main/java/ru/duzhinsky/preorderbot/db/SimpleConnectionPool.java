package ru.duzhinsky.preorderbot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class SimpleConnectionPool implements ConnectionPool {
    private final String url;
    private final String user;
    private final String password;

    private static final int INITIAL_POOL_SIZE = 2;
    Stack<Connection> pool = new Stack<>();
    List<Connection> usedConnections = new ArrayList<>();

    public SimpleConnectionPool(String url, String user, String password) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;
        for(int i = 0; i < INITIAL_POOL_SIZE; ++i)
            pool.push(createConnection(url,user,password));
    }

    @Override
    public Connection getConnection() {
        if(pool.isEmpty()) {
            try {
                pool.push(createConnection(url, user, password));
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }
        Connection connection = pool.pop();
        usedConnections.add(connection);
        return connection;
    }

    @Override
    public boolean releaseConnection(Connection connection) {
        if(!usedConnections.contains(connection)) return false;
        pool.push(connection);
        return usedConnections.remove(connection);
    }

    @Override
    public void close() throws Exception {
        usedConnections.forEach(this::releaseConnection);
        for(Connection c : pool) c.close();
        pool.clear();
        usedConnections.clear();
    }

    private static Connection createConnection(String url, String user, String password) throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
}
