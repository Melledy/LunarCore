package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarRail;
import emu.lunarcore.proto.DispatchRegionDataOuterClass.DispatchRegionData;
import emu.lunarcore.proto.RegionEntryOuterClass.RegionEntry;
import emu.lunarcore.util.Utils;
import io.javalin.http.Context;
import io.javalin.http.Handler;

public class QueryDispatchHandler implements Handler {

    public QueryDispatchHandler() {

    }

    @Override
    public void handle(@NotNull Context ctx) throws Exception {
        // Get regions TODO get regions from database
        RegionEntry region = RegionEntry.newInstance()
                .setName(LunarRail.getConfig().getGameServer().getId())
                .setDispatchUrl(LunarRail.getConfig().getHttpServer().getDisplayAddress() + "/query_gateway")
                .setEnvType("2")
                .setDisplayName(LunarRail.getConfig().getGameServer().getName());

        // Build region list
        DispatchRegionData regions = DispatchRegionData.newInstance();
        regions.addRegionList(region);

        // Log
        LunarRail.getLogger().info("Client request: query_dispatch");

        // Encode to base64 and send to client
        ctx.result(Utils.base64Encode(regions.toByteArray()));
    }

}
