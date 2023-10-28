package emu.lunarcore.data;

import java.lang.reflect.Field;

import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.excel.*;
import emu.lunarcore.game.battle.MazeBuff;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

@SuppressWarnings("unused")
public class GameData {
    // Excels
    @Getter private static Int2ObjectMap<AvatarExcel> avatarExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<ItemExcel> itemExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<EquipmentExcel> equipExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<RelicExcel> relicExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<MonsterExcel> monsterExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<PropExcel> propExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<NpcExcel> npcExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<NpcMonsterExcel> npcMonsterExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<StageExcel> stageExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<MazePlaneExcel> mazePlaneExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<MapEntranceExcel> mapEntranceExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<HeroExcel> heroExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<ShopExcel> shopExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<ItemComposeExcel> itemComposeExcelMap = new Int2ObjectOpenHashMap<>();
    
    @Getter private static Int2ObjectMap<ChallengeExcel> challengeExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<ChallengeTargetExcel> challengeTargetExcelMap = new Int2ObjectOpenHashMap<>();
    
    @Getter private static Int2ObjectMap<RogueManagerExcel> rogueManagerExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<RogueTalentExcel> rogueTalentExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<RogueAreaExcel> rogueAreaExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<RogueRoomExcel> rogueRoomExcelMap = new Int2ObjectOpenHashMap<>();
    @Getter private static Int2ObjectMap<RogueMonsterExcel> rogueMonsterExcelMap = new Int2ObjectOpenHashMap<>();
    
    private static Int2ObjectMap<AvatarPromotionExcel> avatarPromotionExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<AvatarSkillTreeExcel> avatarSkillTreeExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<AvatarRankExcel> avatarRankExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<EquipmentPromotionExcel> equipmentPromotionExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<MazeBuffExcel> mazeBuffExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<CocoonExcel> cocoonExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<MonsterDropExcel> monsterDropExcelMap = new Int2ObjectOpenHashMap<>();
    
    private static Int2ObjectMap<PlayerLevelExcel> playerLevelExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<ExpTypeExcel> expTypeExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<EquipmentExpTypeExcel> equipmentExpTypeExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<RelicExpTypeExcel> relicExpTypeExcelMap = new Int2ObjectOpenHashMap<>();

    private static Int2ObjectMap<RelicMainAffixExcel> relicMainAffixExcelMap = new Int2ObjectOpenHashMap<>();
    private static Int2ObjectMap<RelicSubAffixExcel> relicSubAffixExcelMap = new Int2ObjectOpenHashMap<>();
    
    // Configs (Bin)
    @Getter private static Object2ObjectMap<String, FloorInfo> floorInfos = new Object2ObjectOpenHashMap<>();

    public static Int2ObjectMap<?> getMapForExcel(Class<?> resourceDefinition) {
        Int2ObjectMap<?> map = null;

        try {
            Field field = GameData.class.getDeclaredField(Utils.lowerCaseFirstChar(resourceDefinition.getSimpleName()) + "Map");
            field.setAccessible(true);

            map = (Int2ObjectMap<?>) field.get(null);

            field.setAccessible(false);
        } catch (Exception e) {

        }

        return map;
    }

    public static AvatarPromotionExcel getAvatarPromotionExcel(int id, int promotion) {
        return avatarPromotionExcelMap.get((id << 8) + promotion);
    }

    public static AvatarSkillTreeExcel getAvatarSkillTreeExcel(int skill, int level) {
        return avatarSkillTreeExcelMap.get((skill << 4) + level);
    }

    public static AvatarRankExcel getAvatarRankExcel(int rankId) {
        return avatarRankExcelMap.get(rankId);
    }

    public static EquipmentPromotionExcel getEquipmentPromotionExcel(int id, int promotion) {
        return equipmentPromotionExcelMap.get((id << 8) + promotion);
    }

    public static int getPlayerExpRequired(int level) {
        var excel = playerLevelExcelMap.get(level);
        return excel != null ? excel.getPlayerExp() : 0;
    }

    public static int getAvatarExpRequired(int expGroup, int level) {
        var excel = expTypeExcelMap.get((expGroup << 16) + level);
        return excel != null ? excel.getExp() : 0;
    }

    public static int getEquipmentExpRequired(int expGroup, int level) {
        var excel = equipmentExpTypeExcelMap.get((expGroup << 16) + level);
        return excel != null ? excel.getExp() : 0;
    }

    public static int getRelicExpRequired(int expGroup, int level) {
        var excel = relicExpTypeExcelMap.get((expGroup << 16) + level);
        return excel != null ? excel.getExp() : 0;
    }
    
    public static RelicMainAffixExcel getRelicMainAffixExcel(int groupId, int affixId) {
        return relicMainAffixExcelMap.get((groupId << 16) + affixId);
    }

    public static RelicSubAffixExcel getRelicSubAffixExcel(int groupId, int affixId) {
        return relicSubAffixExcelMap.get((groupId << 16) + affixId);
    }
    
    public static FloorInfo getFloorInfo(int planeId, int floorId) {
        return floorInfos.get("P" + planeId + "_F" + floorId);
    }

    public static MazeBuffExcel getMazeBuffExcel(int buffId, int level) {
        return mazeBuffExcelMap.get((buffId << 4) + level);
    }
    
    public static CocoonExcel getCocoonExcel(int cocoonId, int worldLevel) {
        return cocoonExcelMap.get((cocoonId << 8) + worldLevel);
    }
    
    public static MonsterDropExcel getMonsterDropExcel(int monsterNpcId, int worldLevel) {
        return monsterDropExcelMap.get((monsterNpcId << 4) + worldLevel);
    }
}
