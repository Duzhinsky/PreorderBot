package ru.duzhinsky.preorderbot.service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.duzhinsky.preorderbot.persistence.entities.tgchat.TgChat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HandlersContext {
    private final Map<ChatState, UpdateHandler> handlers = new HashMap<>();

    @Autowired
    public HandlersContext(List<UpdateHandler> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.getHandlerScope(), handler));
    }

    public void handleUpdate(TgChat chat, Update update) {
        var state = chat.getChatState();
        if(isDefaultState(state))
            handlers.get(ChatState.DEFAULT).handle(chat, update);
        else if(isAuthenticationState(state))
            handlers.get(ChatState.AUTHENTICATION).handle(chat, update);
        else if(isLoginState(state))
            handlers.get(ChatState.LOGIN).handle(chat, update);
    }

    private boolean isLoginState(ChatState state) {
        return state == ChatState.LOGIN ||
                state == ChatState.LOGIN_WAIT_PHONE ||
                state == ChatState.LOGIN_WAIT_CODE;
    }

    private boolean isAuthenticationState(ChatState state) {
        return state == ChatState.AUTHENTICATION ||
                state == ChatState.AUTHENTICATION_WAIT_REPLY;
    }

    private boolean isDefaultState(ChatState state) {
        return state == null ||
                state == ChatState.DEFAULT;
    }
}
