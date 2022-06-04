package ru.duzhinsky.preorderbot.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramUtils {
    public static Long getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.hasInlineQuery()) {
            return update.getInlineQuery().getFrom().getId();
        } else if (update.hasChannelPost()) {
            return update.getChannelPost().getChatId();
        } else if (update.hasEditedChannelPost()) {
            return update.getEditedChannelPost().getChatId();
        } else if (update.hasEditedMessage()) {
            return update.getEditedMessage().getChatId();
        } else if (update.hasChosenInlineQuery()) {
            return update.getChosenInlineQuery().getFrom().getId();
        } else if (update.hasShippingQuery()) {
            return update.getShippingQuery().getFrom().getId();
        } else if (update.hasPreCheckoutQuery()) {
            return update.getPreCheckoutQuery().getFrom().getId();
        } else if (update.hasPollAnswer()) {
            return update.getPollAnswer().getUser().getId();
        } else if (update.hasMyChatMember()) {
            return update.getMyChatMember().getChat().getId();
        } else if (update.hasChatMember()) {
            return update.getChatMember().getChat().getId();
        } else if (update.hasChatJoinRequest()) {
            return update.getChatJoinRequest().getChat().getId();
        } else {
            throw new IllegalStateException("Could not retrieve originating chat ID from update");
        }
    }
}
