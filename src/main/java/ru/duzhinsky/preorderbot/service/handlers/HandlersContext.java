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
    public HandlersContext(List<UpdateHandler> handlerList) {
        handlerList.forEach(
                handler -> handler.getHandlerScope().forEach(scope -> this.handlers.put(scope, handler))
        );
    }

    public void handleUpdate(TgChat chat, Update update) {
        var state = chat.getChatState();
        handlers.get(state).handle(chat, update);
    }
}
