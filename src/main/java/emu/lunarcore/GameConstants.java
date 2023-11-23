package emu.lunarcore;

import java.time.Instant;
import java.time.ZoneOffset;

import emu.lunarcore.util.Position;

public class GameConstants {
    public static String VERSION = "1.5.0";
    
    public static final ZoneOffset CURRENT_ZONEOFFSET = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());
    public static final int CURRENT_TIMEZONE = CURRENT_ZONEOFFSET.getTotalSeconds() / 3600;

    // Game
    public static final String DEFAULT_NAME = "Trailblazer";
    public static final int TRAILBLAZER_AVATAR_ID = 8001;
    public static final int MAX_TRAILBLAZER_LEVEL = 70;
    public static final int MAX_STAMINA = 240;
    public static final int MAX_STAMINA_RESERVE = 2400;
    public static final int MAX_AVATARS_IN_TEAM = 4;
    public static final int MAX_FRIENDSHIPS = 100;
    public static final int DEFAULT_TEAMS = 6;
    public static final int MAX_MP = 5; // Client doesnt like more than 5

    public static final int MAX_CHAT_HISTORY = 100; // Max chat messages per conversation
    
    public static final int START_PLANE_ID = 20001;
    public static final int START_FLOOR_ID = 20001001;
    public static final int START_ENTRY_ID = 2000101;
    public static final Position START_POS = new Position(99, 62, -4800);
    
    public static final int MATERIAL_HCOIN_ID = 1; // Material id for jades. DO NOT CHANGE
    public static final int MATERIAL_COIN_ID = 2; // Material id for credits. DO NOT CHANGE
    public static final int TRAILBLAZER_EXP_ID = 22;
    
    // Challenge
    public static final int CHALLENGE_ENTRANCE = 100000103;
    
    // Rogue
    public static final int ROGUE_ENTRANCE = 801120102;
    public static final int ROGUE_TALENT_POINT_ITEM_ID = 32;
    
    // Custom
    public static final int SERVER_CONSOLE_UID = 99;
    public static final int EQUIPMENT_SLOT_ID = 100;
}
