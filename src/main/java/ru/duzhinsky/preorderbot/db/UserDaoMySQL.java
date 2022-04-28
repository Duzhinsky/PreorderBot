package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.sql.*;
import java.util.Optional;

public class UserDaoMySQL implements UserDao {
    private final Connection connection;

    private static final String getUserByTgUsernameQuery = "SELECT users.id, users.phone, tgusers.tgusername FROM users JOIN tgusers ON users.id=tgusers.user_id WHERE tgusers.tgusername=? LIMIT 1;";
    private final PreparedStatement getUserByTgusernameStmnt;

    public UserDaoMySQL(Connection connection) throws SQLException {
        this.connection = connection;
        getUserByTgusernameStmnt = connection.prepareStatement(getUserByTgUsernameQuery);
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public Optional<User> getUserByTgUsername(String username) {
        try {
            getUserByTgusernameStmnt.setString(1,username);
            ResultSet rs = getUserByTgusernameStmnt.executeQuery();
            if(rs.next())
                return Optional.of(new User(
                        rs.getInt("users.id"),
                        rs.getString("tgusers.tgusername"),
                        rs.getString("users.phone")
                ));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
