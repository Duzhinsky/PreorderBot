package ru.duzhinsky.preorderbot.objects;

public class User {
    private int id;
    private Long tgChatId;
    private String phoneNumber;

    public User(int id, Long tgChatId, String phoneNumber) {
        this.id = id;
        this.tgChatId = tgChatId;
        this.phoneNumber = phoneNumber;
    }

    public int getId() {
        return id;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getTgChatId() {
        return tgChatId;
    }

    public void setTgChatId(Long tgChatId) {
        this.tgChatId = tgChatId;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", tgChatId=" + tgChatId +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}
