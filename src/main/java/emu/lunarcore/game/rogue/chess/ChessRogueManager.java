package emu.lunarcore.game.rogue.chess;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.HashSet;
import java.util.Set;

public class ChessRogueManager extends BasePlayerManager {
    public ChessRogueManager(Player player) {
        super(player);
    }
    
    public Int2ObjectMap<Set<Integer>> getRogueDefaultDice() {
        var map = new Int2ObjectOpenHashMap<Set<Integer>>();
        for (var entry: GameData.getRogueNousDiceBranchExcelMap().values()) {
            var set = new HashSet<Integer>();
            set.add(entry.getDefaultUltraSurface());
            set.addAll(entry.getDefaultCommonSurfaceList());
            map.put(entry.getBranchId(), set);
        }
        return map;
    }
}
