package ru.duzhinsky.preorderbot.service.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HandlersContext {
    private Map<ChatState, UpdateHandler> handlers = new HashMap<>();

    @Autowired
    public HandlersContext(List<UpdateHandler> handlers) {
        handlers.forEach(handler -> this.handlers.put(handler.getHandlerScope(), handler));
    }

    public void handleUpdate(ChatState state, Update update) {
        if(isDefaultState(state))
            handlers.get(ChatState.DEFAULT).handle(update);
        else if(isAuthenticationState(state))
            handlers.get(ChatState.AUTHENTICATION).handle(update);
        else if(isLoginState(state))
            handlers.get(ChatState.LOGIN).handle(update);
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
