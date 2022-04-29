package ru.duzhinsky.preorderbot.utils;

public class PhoneValidator {
    public static String prepare(String input) {
        StringBuilder sb = new StringBuilder(input.length());
        for(int i = 0; i < input.length(); ++i) {
            char ch = input.charAt(i);
            if(i == 0 && ch == '8')
                sb.append("7");
            else if(Character.isDigit(ch))
                sb.append(input.charAt(i));
        }
        return sb.toString();
    }

    public static boolean validate(String input) {
        String regex = "7\\d{10}";
        return input.matches(regex);
    }
}
