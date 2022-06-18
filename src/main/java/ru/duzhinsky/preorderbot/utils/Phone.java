package ru.duzhinsky.preorderbot.utils;

import lombok.Getter;

import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Phone {
    @Getter
    private String phone;
    private static final Pattern phonePattern = Pattern.compile("(?:\\+|\\d)[\\d\\-() ]{9,}\\d");

    private Phone(String phone) {
        setPhone(phone);
    }

    public static Optional<Phone> findPhone(String str) {
        Matcher matcher = phonePattern.matcher(str);
        if(matcher.find())
            return Optional.of(new Phone(matcher.group()));
        else
            return Optional.empty();
    }

    public void setPhone(String ph) {
        this.phone = ph.replaceAll("[^\\d]", "");
        if(phone.startsWith("8"))
            phone = "7" + phone.substring(1);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone1 = (Phone) o;
        return Objects.equals(phone, phone1.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }

    @Override
    public String toString() {
        return getPhone();
    }
}
