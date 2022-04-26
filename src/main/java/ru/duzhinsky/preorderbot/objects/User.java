package ru.duzhinsky.preorderbot.objects;

public class User {
    private int id;
    private String tgUsername;
    private String phoneNumber;

    public User(int id, String tgUsername, String phoneNumber) {
        this.id = id;
        this.tgUsername = tgUsername;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public String getTgUsername() {
        return tgUsername;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
