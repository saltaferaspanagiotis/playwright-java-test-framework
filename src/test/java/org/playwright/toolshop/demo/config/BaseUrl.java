package org.playwright.toolshop.demo.config;

public enum BaseUrl {
    UI("https://practicesoftwaretesting.com"),
    API("https://api.practicesoftwaretesting.com");

    private final String value;

    BaseUrl(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }
}
