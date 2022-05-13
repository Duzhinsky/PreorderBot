package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TelegramDaoMySQL implements TelegramDao {
    Connection connection;

    private static final String associateUserWithTelegramQuery =
            "INSERT INTO tgusers (user_id, chat_id) SELECT id, ? FROM users WHERE phone=? LIMIT 1;";
    private final PreparedStatement associateUserWithTelegramStatement;

    private static final String getUserByTgChatIdQuery =
            "SELECT u.id, u.phone, t.chat_id FROM users AS u JOIN tgusers t on u.id = t.user_id WHERE t.chat_id = ?;";
    private final PreparedStatement getUserByTgChatIdStatement;

    private static final String getLastVerifiedPhoneQuery =
            "SELECT phone FROM authenticationcodes WHERE authenticationCodes.chat_id=? ORDER BY created DESC LIMIT 1;";
    private final PreparedStatement getLastVerifiedPhoneStatement;

    private static final String logMessageQuery =
            "INSERT INTO messagepool (chat_id, message_id) VALUES (?,?);";
    private final PreparedStatement logMessageStatement;

    private final PreparedStatement clearMessagePoolStatement;

    public TelegramDaoMySQL(Connection connection) throws SQLException {
        this.connection = connection;
        associateUserWithTelegramStatement = connection.prepareStatement(associateUserWithTelegramQuery);
        getUserByTgChatIdStatement = connection.prepareStatement(getUserByTgChatIdQuery);
        getLastVerifiedPhoneStatement = connection.prepareStatement(getLastVerifiedPhoneQuery);
        logMessageStatement = connection.prepareStatement(logMessageQuery);
        clearMessagePoolStatement = connection.prepareStatement("DELETE FROM messagepool WHERE chat_id=?;");
    }

    @Override
    public void associateUserWithTelegram(String phoneNumber, Long chatId) throws Exception {
        associateUserWithTelegramStatement.setLong(1, chatId);
        associateUserWithTelegramStatement.setString(2, phoneNumber);
        associateUserWithTelegramStatement.executeUpdate();
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
    public Optional<String> getLastVerifiedPhone(Long chatId) throws Exception {
        getLastVerifiedPhoneStatement.setLong(1, chatId);
        ResultSet rs = getLastVerifiedPhoneStatement.executeQuery();
        if(rs.next()) {
            return Optional.of(
                    rs.getString("phone")
            );
        }
        return Optional.empty();
    }

    @Override
    public void logMessage(Long chatId, Integer messageId) throws SQLException {
        logMessageStatement.setLong(1, chatId);
        logMessageStatement.setInt(2, messageId);
        logMessageStatement.executeUpdate();
    }

    @Override
    public void clearMessagePool(Long charId) throws Exception {
        clearMessagePoolStatement.setLong(1, charId);
        clearMessagePoolStatement.executeUpdate();
    }
}
