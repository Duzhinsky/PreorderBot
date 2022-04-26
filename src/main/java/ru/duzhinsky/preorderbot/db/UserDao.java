package ru.duzhinsky.preorderbot.db;

import ru.duzhinsky.preorderbot.objects.User;

public interface UserDao {
    boolean isUserRegistered(User user);
    void addUser(User user);
    User getUser(User user);
}
