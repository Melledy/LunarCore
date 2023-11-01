package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarCore;
import emu.lunarcore.proto.GateserverOuterClass.Gateserver;
import emu.lunarcore.util.Utils;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class QueryGatewayHandler implements Handler {

    public QueryGatewayHandler() {

    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Get streaming data from config
        var data = LunarCore.getConfig().getDownloadData();
        
        // Build gateserver proto
        Gateserver gateserver = Gateserver.newInstance()
                .setRegionName(LunarCore.getConfig().getGameServer().getId())
                .setIp(LunarCore.getConfig().getGameServer().getPublicAddress())
                .setPort(LunarCore.getConfig().getGameServer().getPort())
                .setUnk1(true)
                .setUnk2(true)
                .setUnk3(true);
        
        // Set streaming data
        if (data.mdkVersion != null) {
            gateserver.setMdkResVersion(data.mdkVersion);
        } else {
            gateserver.setMdkResVersion("");
        }
        
        if (data.assetBundleUrl != null) {
            gateserver.setAssetBundleUrl(data.assetBundleUrl);
        }
        if (data.exResourceUrl != null) {
            gateserver.setExResourceUrl(data.exResourceUrl);
        }
        if (data.luaUrl != null) {
            gateserver.setLuaUrl(data.luaUrl);
        }
        if (data.ifixUrl != null) {
            gateserver.setIfixUrl(data.ifixUrl);
        }

        // Log
        if (LunarCore.getConfig().getLogOptions().connections) {
            LunarCore.getLogger().info("Client request: query_gateway");
        }

        // Encode to base64 and send to client
        ctx.result(Utils.base64Encode(gateserver.toByteArray()));
    }

}
