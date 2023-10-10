package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarRail;
import emu.lunarcore.proto.GateserverOuterClass.Gateserver;
import emu.lunarcore.util.Utils;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class QueryGatewayHandler implements Handler {

    public QueryGatewayHandler() {

    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Build gateserver proto
        Gateserver gateserver = Gateserver.newInstance()
                .setRegionName(LunarRail.getConfig().getGameServer().getId())
                .setIp(LunarRail.getConfig().getGameServer().getPublicAddress())
                .setPort(LunarRail.getConfig().getGameServer().getPort())
                .setUnk1(true)
                .setUnk2(true)
                .setUnk3(true)
                .setMdkResVersion(GameConstants.MDK_VERSION);
        
        // Set streaming data urls
        var data = LunarRail.getConfig().getDownloadData();
        
        if (data.assetBundleUrl != null) {
            gateserver.setAssetBundleUrl(data.assetBundleUrl);
        }
        if (data.exResourceUrl != null) {
            gateserver.setAssetBundleUrl(data.exResourceUrl);
        }
        if (data.luaUrl != null) {
            gateserver.setAssetBundleUrl(data.luaUrl);
        }
        if (data.ifixUrl != null) {
            gateserver.setAssetBundleUrl(data.ifixUrl);
        }

        // Log
        if (LunarRail.getConfig().getLogOptions().connections) {
            LunarRail.getLogger().info("Client request: query_gateway");
        }

        // Encode to base64 and send to client
        ctx.result(Utils.base64Encode(gateserver.toByteArray()));
    }

}
