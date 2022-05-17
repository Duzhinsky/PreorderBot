package ru.duzhinsky.preorderbot.persistence.entities.dao;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;

public class DAOFactory {
    private static Weld weld = new Weld();
    private static WeldContainer container = weld.initialize();

    public static TgChatDAO getTgChatDAO() {
        return container.select(TgChatDAO.class).get();
    }
}
