package org.playwright.toolshop.demo.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.RequestOptions;
import org.playwright.toolshop.demo.config.Endpoint;
import org.playwright.toolshop.demo.domain.User;

public class ToolShopAPI {

    private final Page page;

    public ToolShopAPI(Page page) {
        this.page = page;
    }

    public APIResponse registerUser(User user) {
       return  page.request().post(Endpoint.REGISTER_USER.url(),
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
                        .setData(user)
        );
    }

    public void loginAsUser(String email, String password) {
        JsonObject credentials = new JsonObject();
        credentials.addProperty("email", email);
        credentials.addProperty("password", password);

        APIResponse response = page.request().post(Endpoint.LOGIN_USER.url(),
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Accept", "application/json")
                        .setData(credentials)
        );
        String accessToken = new Gson().fromJson(response.text(), JsonObject.class)
                .get("access_token").getAsString();

        page.context().addInitScript(
                "window.localStorage.setItem('auth-token', " + new Gson().toJson(accessToken) + ");"
        );
    }
}
