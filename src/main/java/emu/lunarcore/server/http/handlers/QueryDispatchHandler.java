package emu.lunarcore.server.http.handlers;

import org.jetbrains.annotations.NotNull;

import emu.lunarcore.LunarRail;
import emu.lunarcore.proto.DispatchRegionDataOuterClass.DispatchRegionData;
import emu.lunarcore.proto.RegionEntryOuterClass.RegionEntry;
import emu.lunarcore.server.game.RegionInfo;
import emu.lunarcore.server.http.HttpServer;
import emu.lunarcore.util.Utils;
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
        LunarRail.getLogger().info("Client request: query_dispatch");
        
        // Build region list
        DispatchRegionData regions = DispatchRegionData.newInstance();
        
        // Get regions
        var regionMap = server.getRegions();
        
        synchronized (regionMap) {
            regionMap.values().stream().map(RegionInfo::toProto).forEach(regions::addRegionList);
        }
        
        // Encode to base64 and send to client
        ctx.result(Utils.base64Encode(regions.toByteArray()));
    }

}
