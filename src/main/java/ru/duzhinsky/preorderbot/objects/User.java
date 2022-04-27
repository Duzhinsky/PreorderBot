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

    public static User fromId(int id) {
        return new User(id, "", "");
    }

    public static User fromTgUsername(String tgUsername) {
        return new User(0, tgUsername, "");
    }

    public static User fromPhoneNumber(String phoneNumber) {
        return new User(0, "", phoneNumber);
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

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", tgUsername='" + tgUsername + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
