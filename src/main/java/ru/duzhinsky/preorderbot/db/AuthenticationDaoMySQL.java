package ru.duzhinsky.preorderbot.db;

import java.sql.*;
import java.util.Optional;

public class AuthenticationDaoMySQL implements AuthenticationDAO {
    private final Connection connection;

    public static final String setUserAuthenticationCodeQuery = "INSERT INTO authenticationcodes (chat_id, code, created, phone) VALUES (?,?,now(),?);";
    public final PreparedStatement setUserAuthenticationCodeStatement;

    public static final String getUserAuthenticationCodeQuery = "SELECT code FROM authenticationcodes WHERE chat_id=? ORDER BY created DESC;";
    public final PreparedStatement getUserAuthenticationCodeStatement;

    public AuthenticationDaoMySQL(Connection connection) throws SQLException {
        this.connection = connection;
        setUserAuthenticationCodeStatement = connection.prepareStatement(setUserAuthenticationCodeQuery);
        getUserAuthenticationCodeStatement = connection.prepareStatement(getUserAuthenticationCodeQuery);
    }

    @Override
    public void setUserAuthenticationCode(Long chatId, String phoneNumber, int code) throws SQLException {
        setUserAuthenticationCodeStatement.setLong(1, chatId);
        setUserAuthenticationCodeStatement.setInt(2, code);
        setUserAuthenticationCodeStatement.setString(3, phoneNumber);
        setUserAuthenticationCodeStatement.executeUpdate();
    }

    @Override
    public Optional<Integer> getUserAuthenticationCode(Long chatId) throws SQLException {
        getUserAuthenticationCodeStatement.setLong(1, chatId);
        ResultSet rs = getUserAuthenticationCodeStatement.executeQuery();
        if(rs.next())
            return Optional.of(rs.getInt("code"));
        return Optional.empty();
    }
}
