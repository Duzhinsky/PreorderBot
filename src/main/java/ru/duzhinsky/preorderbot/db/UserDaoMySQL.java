package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.sql.*;
import java.util.Optional;

public class UserDaoMySQL implements UserDao {
    private final Connection connection;

    private static final String getUserByTgUsernameQuery =
            "SELECT users.id, users.phone, tgusers.tgusername " +
                    "FROM users " +
                    "JOIN tgusers ON users.id=tgusers.user_id " +
                    "WHERE tgusers.tgusername=? LIMIT 1;";
    private final PreparedStatement getUserByTgUsernameStatement;

    private static final String getUserByPhoneQuery =
            "SELECT users.id, users.phone " +
                    "FROM users " +
                    "WHERE users.phone=? LIMIT 1;";
    private final PreparedStatement getUserByPhoneStatement;

    private static final String addUserQuery = "INSERT INTO users (phone) VALUES (?);";
    private final PreparedStatement addUserStatement;

    private static final String associateUserWithTelegramQuery =
            "INSERT INTO tgusers (user_id, tgusername) SELECT id, ? FROM users WHERE phone=? LIMIT 1;";
    private final PreparedStatement associateUserWithTelegram;

    public UserDaoMySQL(Connection connection) throws SQLException {
        this.connection = connection;
        getUserByTgUsernameStatement = connection.prepareStatement(getUserByTgUsernameQuery);
        getUserByPhoneStatement = connection.prepareStatement(getUserByPhoneQuery);
        addUserStatement = connection.prepareStatement(addUserQuery);
        associateUserWithTelegram = connection.prepareStatement(associateUserWithTelegramQuery);
    }

    @Override
    public void addUser(String phoneNumber) throws Exception {
        addUserStatement.setString(1, phoneNumber);
        addUserStatement.executeUpdate();
    }

    @Override
    public void associateUserWithTelegram(String phone, String username) throws Exception {
        associateUserWithTelegram.setString(1, username);
        associateUserWithTelegram.setString(2,phone);
        associateUserWithTelegram.executeUpdate();
    }

    @Override
    public Optional<User> getUserByTgUsername(String username) throws Exception {
        getUserByTgUsernameStatement.setString(1,username);
        ResultSet rs = getUserByTgUsernameStatement.executeQuery();
        if(rs.next())
            return Optional.of(new User(
                    rs.getInt("users.id"),
                    rs.getString("tgusers.tgusername"),
                    rs.getString("users.phone")
            ));
        return Optional.empty();
    }

    @Override
    public Optional<User> getUserByPhone(String phoneNumber) throws Exception {
        getUserByPhoneStatement.setString(1,phoneNumber);
        ResultSet rs = getUserByPhoneStatement.executeQuery();
        if(rs.next())
            return Optional.of(new User(
                    rs.getInt("users.id"),
                    "",
                    rs.getString("users.phone")
            ));
        return Optional.empty();
    }

    @Override
    public boolean isUserPresentByTgUsername(String username) throws Exception {
        return getUserByTgUsername(username).isPresent();
    }
}
