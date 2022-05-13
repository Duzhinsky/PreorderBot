package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.sql.*;
import java.util.Optional;

public class UserDaoMySQL implements UserDao {
    private final Connection connection;

    private static final String addUserQuery = "INSERT INTO users (phone) VALUES (?);";
    private final PreparedStatement addUserStatement;

    private static final String getUserByPhoneQuery =
            "SELECT users.id, users.phone " +
                    "FROM users " +
                    "WHERE users.phone=? LIMIT 1;";
    private final PreparedStatement getUserByPhoneStatement;

    public UserDaoMySQL(Connection connection) throws SQLException {
        this.connection = connection;
        getUserByPhoneStatement = connection.prepareStatement(getUserByPhoneQuery);
        addUserStatement = connection.prepareStatement(addUserQuery);
    }

    @Override
    public void addUser(String phoneNumber) throws Exception {
        addUserStatement.setString(1, phoneNumber);
        addUserStatement.executeUpdate();
    }

    @Override
    public Optional<User> getUserByPhone(String phoneNumber) throws Exception {
        getUserByPhoneStatement.setString(1,phoneNumber);
        ResultSet rs = getUserByPhoneStatement.executeQuery();
        if(rs.next())
            return Optional.of(new User(
                    rs.getInt("users.id"),
                    0L,
                    rs.getString("users.phone")
            ));
        return Optional.empty();
    }

}
