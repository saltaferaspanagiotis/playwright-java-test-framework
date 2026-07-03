package org.playwright.toolshop.demo.config;

public enum Endpoint {
    HOME(BaseUrl.UI, ""),
    CONTACT(BaseUrl.UI, "/contact"),
    LOGIN(BaseUrl.UI, "/auth/login"),
    REGISTER_USER(BaseUrl.API, "/users/register"),
    LOGIN_USER(BaseUrl.API, "/users/login");

    private final BaseUrl baseUrl;
    private final String path;

    Endpoint(BaseUrl baseUrl, String path) {
        this.baseUrl = baseUrl;
        this.path = path;
    }

    public String url() {
        return baseUrl.value() + path;
    }

    public String path() {
        return path;
    }
}
