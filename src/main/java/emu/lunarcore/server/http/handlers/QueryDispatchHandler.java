package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarCore;
import emu.lunarcore.server.http.HttpServer;

import io.javalin.http.Context;
import io.javalin.http.Handler;

public class QueryDispatchHandler implements Handler {
    private final HttpServer server;
    
    public QueryDispatchHandler(HttpServer server) {
        this.server = server;
    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Log
        if (LunarCore.getConfig().getLogOptions().connections) {
            LunarCore.getLogger().info("Client request: query_dispatch");
        }
        
        // Send region list to client
        ctx.result(server.getRegionList());
    }

}
