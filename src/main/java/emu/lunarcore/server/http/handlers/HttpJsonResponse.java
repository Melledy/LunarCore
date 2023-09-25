package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class HttpJsonResponse implements Handler {
    private final String json;

    public HttpJsonResponse(String jsonString) {
        this.json = jsonString;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        ctx.status(200);
        ctx.contentType(ContentType.APPLICATION_JSON);
        ctx.result(json);
    }
}
