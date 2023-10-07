package emu.lunarcore;

import java.time.Instant;
import java.time.ZoneOffset;

public class GameConstants {
    public static String VERSION = "1.3.0";
    public static String MDK_VERSION = "5377911";
    
    public static final ZoneOffset CURRENT_OFFSET = ZoneOffset.systemDefault().getRules().getOffset(Instant.now());

    // Game
    public static final String DEFAULT_NAME = "Trailblazer";
    public static final int TRAILBLAZER_AVATAR_ID = 8001;
    public static final int MAX_TRAILBLAZER_LEVEL = 70;
    public static final int MAX_STAMINA = 240;
    public static final int MAX_AVATARS_IN_TEAM = 4;
    public static final int DEFAULT_TEAMS = 6;
    public static final int MAX_MP = 5; // Client doesnt like more than 5

    public static final int MAX_CHAT_HISTORY = 100; // Max chat messages per conversation
    
    // Custom
    public static final int SERVER_CONSOLE_UID = 99;
    public static final int EQUIPMENT_SLOT_ID = 100;
}
