package emu.lunarcore;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.util.Position;
import it.unimi.dsi.fastutil.ints.Int2IntMap;

public class GameConstants {
    public static String VERSION = "4.2.0";
    
    public static final ZoneOffset CURRENT_ZONEOFFSET = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
    public static final int CURRENT_TIMEZONE = CURRENT_ZONEOFFSET.getTotalSeconds() / 3600;

    // Game
    public static final String DEFAULT_NAME = "A Nameless";
    public static final int[] DEFAULT_HEAD_ICONS = {208001, 208002};
    public static final int DEFAULT_WORLD_ID = 101;
    public static final int TRAILBLAZER_AVATAR_ID = 8001;
    public static final int MAX_TRAILBLAZER_LEVEL = 70;
    public static final int[] WORLD_LEVEL_UPGRADES = {0, 20, 30, 40, 50, 60, 65};
    public static final int MAX_STAMINA = 300;
    public static final int MAX_STAMINA_RESERVE = 2400;
    public static final int MAX_AVATARS_IN_TEAM = 4;
    public static final int DEFAULT_TEAMS = 9;
    public static final int MAX_MP = 5; // Client doesnt like more than 5
    public static final int FARM_ELEMENT_STAMINA_COST = 30;
    
    public static final Int2IntMap BONUS_MAX_MP = Int2IntMap.ofEntries(
        Int2IntMap.entry(1408, 3)
    );

    // Mission
    public static final int MISSION_STARTUP = 1000101;

    // Chat/Social
    public static final int MAX_FRIENDSHIPS = 100;
    public static final int MAX_CHAT_HISTORY = 100; // Max chat messages per conversation

    // Inventory
    public static final int MATERIAL_HCOIN_ID = 1; // Material id for jades. DO NOT CHANGE
    public static final int MATERIAL_COIN_ID = 2; // Material id for credits. DO NOT CHANGE
    public static final int TRAILBLAZER_EXP_ID = 22;
    public static final int RELIC_REMAINS_ID = 235;
    
    public static final int INVENTORY_MAX_EQUIPMENT = 1500;
    public static final int INVENTORY_MAX_RELIC = 3000;
    public static final int INVENTORY_MAX_MATERIAL = 2000;
    
    public static final int RELIC_COMPOSE_SUB_AFFIX_ITEM = 237;
    public static final List<ItemParam> RELIC_REFORGE_COST = List.of(new ItemParam(238, 1));
    
    // Start position
    public static final int START_PLANE_ID = 20001;
    public static final int START_FLOOR_ID = 20001001;
    public static final int START_ENTRY_ID = 2000101;
    public static final Position START_POS = new Position(99, 62, -4800);
    
    // Battle
    public static final int BATTLE_AMBUSH_BUFF_ID = 1000102;
    
    // Gacha
    public static final int GACHA_CEILING_MAX = 300; // Yes, I know this is in an excel
    
    // Challenge
    public static final int CHALLENGE_ENTRANCE = 100000103;
    public static final int CHALLENGE_STORY_ENTRANCE = 102020107;
    public static final int CHALLENGE_BOSS_ENTRANCE = 1030402;
    public static final int CHALLENGE_PEAK_ENTRANCE = 100000352;
    
    public static final int CHALLENGE_PEAK_GROUP_ID = 6;
    
    // Rogue
    public static final boolean ENABLE_ROGUE = false;
    public static final int ROGUE_ENTRANCE = 801120102;
    public static final int ROGUE_START_COIN_COUNT = 100;
    public static final int ROGUE_COIN_ID = 31;
    public static final int ROGUE_TALENT_COIN_ID = 32;
    
    // Activity
    public static final int[] ENABLE_ACTIVITY_TYPES = { 18, 34 };
    
    // Custom
    public static final int SERVER_CONSOLE_UID = 99;
    public static final int EQUIPMENT_SLOT_ID = 100;
}
