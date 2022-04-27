package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

import java.sql.*;
import java.util.Optional;

public class UserDB implements UserDao {
    private final Connection connection;

    public UserDB() throws SQLException {
        this.connection = Database.getConnection();
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }

    @Override
    public void addUser(User user) {

    }

    @Override
    public Optional<User> getUser(User user) {
        String query = "SELECT users.id, users.phone, tgusers.tgusername FROM users JOIN tgusers ON users.id=tgusers.user_id WHERE ";
        if(user.getId() != 0)
            query += String.format("users.id = %d", user.getId());
        else if(!user.getPhoneNumber().isEmpty())
            query += String.format("users.phone = '%s'", user.getPhoneNumber());
        else if(!user.getTgUsername().isEmpty())
            query += String.format("tgusers.tgusername = '%s'", user.getTgUsername());
        else return Optional.empty();
        query += " LIMIT 1;";

        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
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
