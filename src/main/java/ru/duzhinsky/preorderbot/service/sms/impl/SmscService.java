package ru.duzhinsky.preorderbot.service.sms.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.duzhinsky.preorderbot.service.sms.SmsService;
import ru.duzhinsky.preorderbot.service.sms.SmsStatus;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service("SmscSerivce")
public class SmscService implements SmsService {
    private final UriBuilder sendSMSURIBuilder;

    @Autowired
    public SmscService(SmscProperties properties) {
        this.sendSMSURIBuilder = UriBuilder.fromPath("https://smsc.ru/sys/send.php")
                .queryParam("login", properties.getUsername())
                .queryParam("psw", properties.getPassword());
    }

    @Override
    public void sendSms(String phoneNumber, String text, Consumer<SmsStatus> callback) {
        URI requestURI = sendSMSURIBuilder.queryParam("phones", phoneNumber).queryParam("mes", text).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(requestURI)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        CompletableFuture<HttpResponse<String>> response = HttpClient.newBuilder()
                .build()
                .sendAsync(request, HttpResponse.BodyHandlers.ofString());
        response.thenAccept(r -> callback.accept(responseHandler(r)));
    }

    private SmsStatus responseHandler(HttpResponse<String> response) {
        if (response.statusCode() == 200) return SmsStatus.DELIVERED;
        else return SmsStatus.ERROR;
    }
}
