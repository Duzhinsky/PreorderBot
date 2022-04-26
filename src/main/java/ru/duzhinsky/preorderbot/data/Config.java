package ru.duzhinsky.preorderbot.data;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Properties;

public class Config {
    private static final Properties props = new Properties();

    static {
        try {
            String json = Files.readString(Path.of("src/main/java/ru/duzhinsky/preorderbot/data/config.json"));
            JSONObject configObject = new JSONObject(json);
            Iterator<String> keys = configObject.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Object value = configObject.get(key);
                props.setProperty(key,value.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static void list(PrintStream out) {
        props.list(out);
    }
}
