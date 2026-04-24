package emu.lunarcore.data;

import java.lang.reflect.Field;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import java.util.stream.Collectors;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.config.*;
import emu.lunarcore.data.excel.*;
import emu.lunarcore.data.resource.ExcelMap;
import emu.lunarcore.data.resource.MultiKeyExcelMap;
import emu.lunarcore.data.config.MissionInfo.SubMissionInfo;
import emu.lunarcore.game.battle.MazeBuff;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

@SuppressWarnings("unused")
public class GameData {
    // Excels
    @Getter private static ExcelMap<AvatarExcel> avatarExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<AvatarSkinExcel> avatarSkinExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<AvatarRankExcel> avatarRankExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<AvatarRelicRecommendExcel> avatarRelicRecommendExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<MultiplePathAvatarExcel> multiplePathAvatarExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<AvatarGlobalBuffExcel> avatarGlobalBuffExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ItemExcel> itemExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ItemUseExcel> itemUseExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<EquipmentExcel> equipExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<RelicExcel> relicExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<PropExcel> propExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<NpcExcel> npcExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<SummonUnitExcel> summonUnitExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<MonsterExcel> monsterExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<NpcMonsterExcel> npcMonsterExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<StageExcel> stageExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<MazePlaneExcel> mazePlaneExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<MapEntranceExcel> mapEntranceExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ShopExcel> shopExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<RewardExcel> rewardExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<InteractExcel> interactExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<PlayerIconExcel> playerIconExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ItemComposeExcel> itemComposeExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<BackGroundMusicExcel> backGroundMusicExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<QuestExcel> questExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<TextJoinExcel> textJoinExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ChatBubbleExcel> chatBubbleExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<PhoneThemeExcel> phoneThemeExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<PlayerOutfitBaseExcel> playerOutfitBaseExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<PetExcel> petExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ContentPackageExcel> contentPackageExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<OfferingTypeExcel> offeringTypeExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<AvatarDemoExcel> avatarDemoExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<SpecialAvatarExcel> specialAvatarExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<MainMissionExcel> mainMissionExcelMap = new ExcelMap<>();
    
    // Multikey excels
    @Getter private static MultiKeyExcelMap<AvatarPromotionExcel> avatarPromotionExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<AvatarSkillTreeExcel> avatarSkillTreeExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<AvatarEnhanceExcel> avatarEnhanceExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<EquipmentPromotionExcel> equipmentPromotionExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<MazeBuffExcel> mazeBuffExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<CocoonExcel> cocoonExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<FarmElementExcel> farmElementExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<PlaneEventExcel> planeEventExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<MappingInfoExcel> mappingInfoExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<MonsterDropExcel> monsterDropExcelMap = new MultiKeyExcelMap<>();

    // Levels and Exp data
    @Getter private static ExcelMap<PlayerLevelExcel> playerLevelExcelMap = new ExcelMap<>();
    @Getter private static MultiKeyExcelMap<ExpTypeExcel> expTypeExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<EquipmentExpTypeExcel> equipmentExpTypeExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<RelicExpTypeExcel> relicExpTypeExcelMap = new MultiKeyExcelMap<>();
    
    // Relics
    @Getter private static ExcelMap<RelicSetExcel> relicSetExcelMap = new ExcelMap<>();
    @Getter private static MultiKeyExcelMap<RelicMainAffixExcel> relicMainAffixExcelMap = new MultiKeyExcelMap<>();
    @Getter private static MultiKeyExcelMap<RelicSubAffixExcel> relicSubAffixExcelMap = new MultiKeyExcelMap<>();
    
    // Challenge
    @Getter private static ExcelMap<ChallengeGroupExcel> challengeGroupExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ChallengeExcel> challengeExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ChallengeTargetExcel> challengeTargetExcelMap = new ExcelMap<>();
    
    @Getter private static ExcelMap<ChallengePeakExcel> challengePeakExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<ChallengePeakGroupExcel> challengePeakGroupExcelMap = new ExcelMap<>();
    
    // Expeditions
    @Getter private static ExcelMap<ExpeditionExcel> expeditionExcelMap = new ExcelMap<>();
    
    // Pom-Pom
    @Getter private static ExcelMap<PomSkinExcel> pomSkinExcelMap = new ExcelMap<>();

    // Activity
    @Getter private static ExcelMap<ActivityPanelExcel> activityPanelExcelMap = new ExcelMap<>();
    
    // Tutorials
    @Getter private static ExcelMap<TutorialExcel> tutorialExcelMap = new ExcelMap<>();
    @Getter private static ExcelMap<TutorialGuideExcel> tutorialGuideExcelMap = new ExcelMap<>();

    // Configs (Bin)
    @Getter private static Object2ObjectMap<String, FloorInfo> floorInfos = new Object2ObjectOpenHashMap<>();
    @Getter private static Object2ObjectMap<Integer, MissionInfo> missionInfos = new Object2ObjectOpenHashMap<>();
    
    public static AvatarSkillTreeExcel getAvatarSkilltree(int avatarId, int enhanceId, int anchorPointId, int level) {
        long key = ((long) avatarId << 48) + ((long) enhanceId << 32) + ((long) anchorPointId << 16) + (long) level;
        return GameDepot.getAvatarSkillTreeExcels().get(key);
    }

    public static List<Integer> getRecommendAvatarIdForRelicSet(int setId) {
        return avatarRelicRecommendExcelMap.values().stream()
            .filter(excel -> Arrays.stream(excel.getSet4IDList()).anyMatch(id -> id == setId) || Arrays.stream(excel.getSet2IDList()).anyMatch(id -> id == setId))
            .map(AvatarRelicRecommendExcel::getAvatarID)
            .toList();
    }

    public static int getRelicSetFromId(int relicId) {
        RelicExcel relicExcel = GameData.getRelicExcelMap().get(relicId);

        if (relicExcel == null) {
            return 0;
        }
        
        return relicExcel.getSetId();
    }

    public static List<Integer> getAllMusicIds() {
        List<Integer> allIds = new ArrayList<>();

        for (Int2ObjectMap.Entry<BackGroundMusicExcel> entry : backGroundMusicExcelMap.int2ObjectEntrySet()) {
            BackGroundMusicExcel backGroundMusicExcel = entry.getValue();
            allIds.add(backGroundMusicExcel.getId());
        }

        return allIds;
    }

    public static int TextJoinItemFromId(int id) {
        for (Int2ObjectMap.Entry<TextJoinExcel> entry : textJoinExcelMap.int2ObjectEntrySet()) {
            TextJoinExcel textJoinExcel = entry.getValue();
            if (textJoinExcel.getId() == id) {
                IntArrayList textJoinItemList = textJoinExcel.getTextJoinItemList();
                if (textJoinItemList.size() > 0) {
                    return textJoinItemList.getInt(textJoinItemList.size() - 1);
                }
            }
        }
        return id * 10; // or return a default value if needed
    }
    
    public static List<Integer> getAllMonsterIds() {
        List<Integer> allIds = new ArrayList<>();

        for (Int2ObjectMap.Entry<MonsterExcel> entry : monsterExcelMap.int2ObjectEntrySet()) {
            MonsterExcel monsterExcel = entry.getValue();
            allIds.add(monsterExcel.getId());
        }

        return allIds;
    }

    public static int getMusicGroupId(int musicId) {
        var excel = backGroundMusicExcelMap.get(musicId);
        return excel != null ? excel.getGroupId() : 0;
    }

    public static int getPlayerExpRequired(int level) {
        var excel = playerLevelExcelMap.get(level);
        return excel != null ? excel.getPlayerExp() : 0;
    }

    public static int getAvatarExpRequired(int expGroup, int level) {
        var excel = expTypeExcelMap.get(expGroup, level);
        return excel != null ? excel.getExp() : 0;
    }

    public static int getEquipmentExpRequired(int expGroup, int level) {
        var excel = equipmentExpTypeExcelMap.get(expGroup, level);
        return excel != null ? excel.getExp() : 0;
    }

    public static int getRelicExpRequired(int expGroup, int level) {
        var excel = relicExpTypeExcelMap.get(expGroup, level);
        return excel != null ? excel.getExp() : 0;
    }
    
    public static FloorInfo getFloorInfo(int planeId, int floorId) {
        return floorInfos.get("P" + planeId + "_F" + floorId);
    }

    public static MissionInfo getMainMissionInfos(int mainMissionID) {
        if (!missionInfos.containsKey(mainMissionID)) {
            return null;
        }
        return missionInfos.get(mainMissionID);
    }

    public static SubMissionInfo getSubMissionById(int id) {
        for (var missionInfos : missionInfos.values()) {
            for (SubMissionInfo subMission : missionInfos.getSubMissionList()) {
                if (subMission.getId() == id) {
                    return subMission;
                }
            }
        }
        return null;
    }

    public static boolean isRelatedMissions(int idSub, int idMain) {
        for (var missionInfos : missionInfos.values()) {
            for (SubMissionInfo subMission : missionInfos.getSubMissionList()) {
                if (subMission.getMainMissionID() == idMain) {
                    if (subMission.getId() == idSub) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static List<Integer> getMainMissionIds() {
        List<Integer> allIds = new ArrayList<>();

        for (Int2ObjectMap.Entry<MainMissionExcel> entry : mainMissionExcelMap.int2ObjectEntrySet()) {
            MainMissionExcel mainMissionExcel = entry.getValue();
            allIds.add(mainMissionExcel.getId());
        }

        return allIds;
    }

    public static List<Integer> getSubMissionIds() {
        List<Integer> allIds = new ArrayList<>();

        for (var missionInfos : missionInfos.values()) {
            for (SubMissionInfo subMission : missionInfos.getSubMissionList()) {
                allIds.add(subMission.getId());
            }
        }

        return allIds;
    }

    public static List<MissionInfo> getAllMainMissionInfos() {
        List<MissionInfo> allIds = new ArrayList<>();

        for (var missionInfo : missionInfos.values()) {
            allIds.add(missionInfo);
        }

        return allIds;
    }

    public static MainMissionExcel getMainMissionExcelByID(int mMisionId) {
        return mainMissionExcelMap.get(mMisionId);
    }

    public static MissionInfo getMissionInfos(int mainMissionID) {
        return missionInfos.get(mainMissionID);
    }

    public static MainMissionExcel getMainMissionByID(int mMisionId) {
        return mainMissionExcelMap.get(mMisionId);
    }

    public static int getPetItemId(int petId) {
        PetExcel petExcel = petExcelMap.get(petId);
        return petExcel != null ? petExcel.getPetItemID() : 0;
    }
}
