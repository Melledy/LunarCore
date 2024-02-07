package emu.lunarcore.data;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.custom.ActivityScheduleData;
import emu.lunarcore.data.excel.*;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

// Game data that is parsed by the server goes here
public class GameDepot {
    // Activity
    @Getter private static List<ActivityScheduleData> activityScheduleExcels = new ArrayList<>();
    
    // Exp
    @Getter private static List<AvatarExpItemExcel> avatarExpExcels = new ArrayList<>();
    @Getter private static List<EquipmentExpItemExcel> equipmentExpExcels = new ArrayList<>();
    @Getter private static List<RelicExpItemExcel> relicExpExcels = new ArrayList<>();
    
    // Relics
    private static Int2ObjectMap<List<RelicMainAffixExcel>> relicMainAffixDepot = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<List<RelicSubAffixExcel>> relicSubAffixDepot = new Int2ObjectOpenHashMap<>();
    
    // Challenges
    @Getter private static Int2ObjectMap<List<ChallengeRewardExcel>> challengeRewardLines = new Int2ObjectOpenHashMap<>();

    // Rogue
    @Getter private static Int2ObjectMap<int[]> rogueMapGen = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<RogueBuffExcel> rogueAeonBuffs = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<List<RogueBuffExcel>> rogueAeonEnhanceBuffs = new Int2ObjectOpenHashMap<>();
    @Getter private static List<RogueBuffExcel> rogueRandomBuffList = new ArrayList<>();
    @Getter private static List<RogueBonusExcel> rogueRandomCommonBonusList = new ArrayList<>();
    @Getter private static List<RogueMiracleExcel> rogueRandomMiracleList = new ArrayList<>();
    @Getter private static List<RogueNPCExcel> rogueRandomNpcList = new ArrayList<>();
    private static Int2ObjectMap<List<RogueMapExcel>> rogueMapDepot = new Int2ObjectOpenHashMap<>();
    
    public static void addRelicMainAffix(RelicMainAffixExcel affix) {
        relicMainAffixDepot
            .computeIfAbsent(affix.getGroupID(), k -> new ArrayList<>())
            .add(affix);
    }

    public static void addRelicSubAffix(RelicSubAffixExcel affix) {
        relicSubAffixDepot
            .computeIfAbsent(affix.getGroupID(), k -> new ArrayList<>())
            .add(affix);
    }

    public static RelicMainAffixExcel getRandomRelicMainAffix(int groupId) {
        var list = relicMainAffixDepot.get(groupId);
        if (list == null) return null;

        return list.get(Utils.randomRange(0, list.size() - 1));
    }

    public static List<RelicSubAffixExcel> getRelicSubAffixList(int groupId) {
        return relicSubAffixDepot.get(groupId);
    }
    
    // TODO cache this so we don't have to run this function every time we get the schedule
    public static RogueManagerExcel getCurrentRogueSchedule() {
        long time = System.currentTimeMillis() - (GameConstants.CURRENT_ZONEOFFSET.getTotalSeconds() * 1000);
        
        for (var schedule : GameData.getRogueManagerExcelMap().values()) {
            if (time >= schedule.getBeginTime() && time < schedule.getEndTime()) {
                return schedule;
            }
        }
        
        return null;
    }
    
    public static List<RogueMapExcel> getRogueMapsById(int mapId) {
        return rogueMapDepot.computeIfAbsent(mapId, id -> new ArrayList<>());
    }
    
}
