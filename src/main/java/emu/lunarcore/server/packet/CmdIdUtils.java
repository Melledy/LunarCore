package emu.lunarcore.server.packet;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.util.JsonUtils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class CmdIdUtils {
    public static final Set<Integer> LOOP_PACKETS = Set.of(
        CmdId.PlayerHeartBeatCsReq,
        CmdId.PlayerHeartBeatScRsp,
        CmdId.SceneEntityMoveCsReq,
        CmdId.SceneEntityMoveScRsp,
        CmdId.GetQuestDataScRsp
    );

    private static Int2ObjectMap<String> opcodeMap;

    static {
        opcodeMap = new Int2ObjectOpenHashMap<>();

        Field[] fields = CmdId.class.getFields();

        for (Field f : fields) {
            if (f.getType().equals(int.class)) {
                try {
                    opcodeMap.put(f.getInt(null), f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getOpcodeName(int opcode) {
        if (opcode <= 0) return "UNKNOWN";
        return opcodeMap.getOrDefault(opcode, "UNKNOWN");
    }

    public static void dumpPacketIds() {
        try (FileWriter writer = new FileWriter("./PacketIds_" + GameConstants.VERSION + ".json")) {
            // Create sorted tree map
            Map<Integer, String> packetIds = opcodeMap.int2ObjectEntrySet().stream()
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
