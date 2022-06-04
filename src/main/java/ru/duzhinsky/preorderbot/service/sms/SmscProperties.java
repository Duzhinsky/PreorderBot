package ru.duzhinsky.preorderbot.service.sms;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix="smsc")
@Getter
@Setter
public class SmscProperties {
    private String username;
    private String password;
}
