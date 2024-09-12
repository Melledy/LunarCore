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
        var data = LunarCore.getHotfixData();
        
        // Build gateserver proto
        Gateserver gateserver = Gateserver.newInstance()
                .setRegionName(LunarCore.getConfig().getGameServer().getId())
                .setIp(LunarCore.getConfig().getGameServer().getPublicAddress())
                .setPort(LunarCore.getConfig().getGameServer().getPublicPort())
                .setMsg("Access verification failed. Please check if you have logged in to the correct account and server.") // in case there is some error idk
                .setEnableVersionUpdate(true)
                .setEnableDesignDataBundleVersionUpdate(true)
                .setEventTrackingOpen(true)
                .setNetworkDiagnostic(true);
        
        // Set hotfix urls
        if (data.assetBundleUrl != null && !data.assetBundleUrl.isBlank()) {
            gateserver.setAssetBundleUrl(data.assetBundleUrl);
        }
        if (data.exResourceUrl != null && !data.exResourceUrl.isBlank()) {
            gateserver.setExResourceUrl(data.exResourceUrl);
        }
        if (data.luaUrl != null && !data.luaUrl.isBlank()) {
            gateserver.setLuaUrl(data.luaUrl);
        }
        if (data.ifixUrl != null && !data.ifixUrl.isBlank()) {
            gateserver.setIfixUrl(data.ifixUrl);
        }
        
        // Set hotfix versions
        String mdkResVersion = data.getMdkResVersion();
        String ifixVersion = data.getIfixVersion();
        if (mdkResVersion != null) {
            gateserver.setMdkResVersion(mdkResVersion);
        }
        if (ifixVersion != null) {
            gateserver.setIfixVersion(ifixVersion);
        }
        
        // Log
        if (LunarCore.getConfig().getLogOptions().connections) {
            LunarCore.getLogger().info("Client request: query_gateway");
        }

        // Encode to base64 and send to client
        ctx.result(Utils.base64Encode(gateserver.toByteArray()));
    }

}
