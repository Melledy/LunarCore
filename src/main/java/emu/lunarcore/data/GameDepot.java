package emu.lunarcore.data;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.excel.RelicMainAffixExcel;
import emu.lunarcore.data.excel.RelicSubAffixExcel;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

// Game data that is parsed by the server goes here
public class GameDepot {
    private static Int2ObjectMap<List<RelicMainAffixExcel>> relicMainAffixDepot = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<List<RelicSubAffixExcel>> relicSubAffixDepot = new Int2ObjectOpenHashMap<>();

    public static void addRelicMainAffix(RelicMainAffixExcel affix) {
        List<RelicMainAffixExcel> list = relicMainAffixDepot.computeIfAbsent(affix.getGroupID(), k -> new ArrayList<>());
        list.add(affix);
    }

    public static void addRelicSubAffix(RelicSubAffixExcel affix) {
        List<RelicSubAffixExcel> list = relicSubAffixDepot.computeIfAbsent(affix.getGroupID(), k -> new ArrayList<>());
        list.add(affix);
    }

    public static RelicMainAffixExcel getRandomRelicMainAffix(int groupId) {
        var list = relicMainAffixDepot.get(groupId);
        if (list == null) return null;

        return list.get(Utils.randomRange(0, list.size() - 1));
    }

    public static List<RelicSubAffixExcel> getRelicSubAffixList(int groupId) {
        return relicSubAffixDepot.get(groupId);
    }
}
