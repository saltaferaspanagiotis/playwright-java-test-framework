package org.playwright.toolshop.demo.fixtures;

import org.playwright.toolshop.demo.domain.User;

public class ScenarioContext {

    private static final ThreadLocal<User> user = new ThreadLocal<>();

    public static void setUser(User value) {
        user.set(value);
    }

    public static User getUser() {
        return user.get();
    }

    public static void clear() {
        user.remove();
    }
}
