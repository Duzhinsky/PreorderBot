package ru.duzhinsky.preorderbot.db;

import java.sql.SQLException;
import java.util.Optional;

public interface AuthenticationDAO {
    void setUserAuthenticationCode(Long chatId, String phoneNumber, int code) throws SQLException;
    Optional<Integer> getUserAuthenticationCode(Long chatId) throws SQLException;
}
