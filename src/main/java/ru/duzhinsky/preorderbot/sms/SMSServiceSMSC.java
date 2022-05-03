package ru.duzhinsky.preorderbot.sms;

import ru.duzhinsky.preorderbot.data.Config;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class SMSServiceSMSC implements SMSService {
    public static final UriBuilder sendSMSURIBuilder;

    static
    {
        String user = Config.getProperty("smscUsername", "");
        String password = Config.getProperty("smscPassword", "");
        sendSMSURIBuilder = UriBuilder.fromPath("https://smsc.ru/sys/send.php")
                .queryParam("login",user)
                .queryParam("psw",password);
    }

    @Override
    public void sendSMS(String phoneNumber, String text) {
        URI requestURI = sendSMSURIBuilder.queryParam("phones", phoneNumber).queryParam("mes", text).build();
        System.out.println(requestURI);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestURI)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        CompletableFuture<HttpResponse<String>> response = HttpClient.newBuilder()
                .build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
