package emu.lunarcore.server.packet;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.util.JsonUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

public class CmdIdUtils {
    /**
     * Packet ids that will NOT be logged if "filterLoopingPackets" is true in the config
     */
    public static final IntSet IGNORED_LOG_PACKETS = IntOpenHashSet.of(
        CmdId.PlayerHeartBeatCsReq,
        CmdId.PlayerHeartBeatScRsp,
        CmdId.SceneEntityMoveCsReq,
        CmdId.SceneEntityMoveScRsp,
        CmdId.GetQuestDataScRsp
    );
    
    /**
     * Packet ids that will NOT be caught by the spam filter
     */
    public static final IntSet ALLOWED_FILTER_PACKETS = IntOpenHashSet.of(
        CmdId.PlayerHeartBeatCsReq,
        CmdId.GetMissionStatusCsReq,
        CmdId.GetMainMissionCustomValueCsReq
    );

    private static Int2ObjectMap<String> cmdIdMap;

    static {
        cmdIdMap = new Int2ObjectOpenHashMap<>();

        Field[] fields = CmdId.class.getFields();

        for (Field f : fields) {
            if (f.getType().equals(int.class)) {
                try {
                    cmdIdMap.put(f.getInt(null), f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getCmdIdName(int opcode) {
        if (opcode <= 0) return "UNKNOWN";
        return cmdIdMap.getOrDefault(opcode, "UNKNOWN");
    }

    public static void dumpPacketIds() {
        try (FileWriter writer = new FileWriter("./PacketIds_" + GameConstants.VERSION + ".json")) {
            // Create sorted tree map
            Map<Integer, String> packetIds = cmdIdMap.int2ObjectEntrySet().stream()
                    .filter(e -> e.getIntKey() > 0)
                    .collect(Collectors.toMap(Int2ObjectMap.Entry::getIntKey, Int2ObjectMap.Entry::getValue, (k, v) -> v, TreeMap::new));
            // Write to file
            writer.write(JsonUtils.encode(packetIds));
            LunarCore.getLogger().info("Dumped packet ids.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
