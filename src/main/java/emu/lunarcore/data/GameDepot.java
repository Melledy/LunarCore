package emu.lunarcore.data;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.excel.*;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import lombok.Getter;

// Game data that is parsed by the server goes here
public class GameDepot {
    // Exp
    @Getter private static List<AvatarExpItemExcel> avatarExpExcels = new ArrayList<>();
    @Getter private static List<EquipmentExpItemExcel> equipmentExpExcels = new ArrayList<>();
    @Getter private static List<RelicExpItemExcel> relicExpExcels = new ArrayList<>();
    
    // Relics
    private static Int2ObjectMap<List<RelicMainAffixExcel>> relicMainAffixDepot = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<List<RelicSubAffixExcel>> relicSubAffixDepot = new Int2ObjectOpenHashMap<>();
    
    // Challenges
    @Getter private static Int2ObjectMap<List<ChallengeRewardExcel>> challengeRewardLines = new Int2ObjectOpenHashMap<>();
    
    // Avatar skilltrees
    @Getter private static Long2ObjectMap<AvatarSkillTreeExcel> avatarSkillTreeExcels = new Long2ObjectOpenHashMap<>();
    
    public static void addRelicMainAffix(RelicMainAffixExcel affix) {
        relicMainAffixDepot
            .computeIfAbsent(affix.getGroupID(), x -> new ArrayList<>())
            .add(affix);
    }

    public static void addRelicSubAffix(RelicSubAffixExcel affix) {
        relicSubAffixDepot
            .computeIfAbsent(affix.getGroupID(), x -> new ArrayList<>())
            .add(affix);
    }
    
    public static List<RelicMainAffixExcel> getRelicMainAffixesByGroup(int groupId) {
        return relicMainAffixDepot.get(groupId);
    }
    
    public static List<RelicSubAffixExcel> getRelicSubAffixesByGroup(int groupId) {
        return relicSubAffixDepot.get(groupId);
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
