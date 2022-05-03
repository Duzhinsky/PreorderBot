package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.sql.*;
import java.util.Optional;

public class UserDaoMySQL implements UserDao {
    private final Connection connection;


    private static final String addUserQuery = "INSERT INTO users (phone) VALUES (?);";
    private final PreparedStatement addUserStatement;

    private static final String associateUserWithTelegramQuery =
            "INSERT INTO tgusers (user_id, chat_id) SELECT id, ? FROM users WHERE phone=? LIMIT 1;";
    private final PreparedStatement associateUserWithTelegram;

    private static final String getUserByTgChatIdQuery = "\n" +
            "SELECT u.id, u.phone, t.chat_id FROM users AS u JOIN tgusers t on u.id = t.user_id WHERE t.chat_id = ?;";
    private final PreparedStatement getUserByTgChatIdStatement;

    private static final String getUserByPhoneQuery =
            "SELECT users.id, users.phone " +
                    "FROM users " +
                    "WHERE users.phone=? LIMIT 1;";
    private final PreparedStatement getUserByPhoneStatement;

    public UserDaoMySQL(Connection connection) throws SQLException {
        this.connection = connection;
        getUserByTgChatIdStatement = connection.prepareStatement(getUserByTgChatIdQuery);
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
    public void associateUserWithTelegram(String phone, Long chatId) throws Exception {
        associateUserWithTelegram.setLong(1, chatId);
        associateUserWithTelegram.setString(2, phone);
        associateUserWithTelegram.executeUpdate();
    }

    @Override
    public Optional<User> getUserByTgChatId(Long chatId) throws Exception {
        getUserByTgChatIdStatement.setLong(1, chatId);
        ResultSet rs = getUserByTgChatIdStatement.executeQuery();
        if(rs.next()) {
            return Optional.of(new User(
                    rs.getInt(1),
                    rs.getLong(3),
                    rs.getString(2)
            ));
        }
        return Optional.empty();
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

    @Override
    public boolean isUserPresentByTgChatId(Long chatId) throws Exception {
        return getUserByTgChatId(chatId).isPresent();
    }

}
