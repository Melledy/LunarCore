package emu.lunarcore.game.player;

import java.util.HashSet;
import java.util.Set;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.MapEntranceExcel;
import emu.lunarcore.data.excel.MazePlaneExcel;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.avatar.AvatarStorage;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.avatar.HeroPath;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.challenge.ChallengeInstance;
import emu.lunarcore.game.challenge.ChallengeManager;
import emu.lunarcore.game.chat.ChatManager;
import emu.lunarcore.game.chat.ChatMessage;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.gacha.PlayerGachaInfo;
import emu.lunarcore.game.inventory.Inventory;
import emu.lunarcore.game.mail.Mailbox;
import emu.lunarcore.game.rogue.RogueInstance;
import emu.lunarcore.game.rogue.RogueManager;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.game.scene.triggers.PropTriggerType;
import emu.lunarcore.proto.BoardDataSyncOuterClass.BoardDataSync;
import emu.lunarcore.proto.HeadIconOuterClass.HeadIcon;
import emu.lunarcore.proto.PlayerBasicInfoOuterClass.PlayerBasicInfo;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.server.packet.send.PacketEnterSceneByServerScNotify;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.server.packet.send.PacketSceneEntityMoveScNotify;
import emu.lunarcore.util.Position;

import lombok.Getter;
import lombok.Setter;

@Entity(value = "players", useDiscriminator = false)
@Getter
public class Player {
    @Id private int uid;
    @Indexed private String accountUid;
    private String name;
    private String signature;
    private int headIcon;
    private int birthday;
    private int curBasicType;
    @Setter private PlayerGender gender;

    private int level;
    private int exp;
    private int worldLevel;
    private int stamina;
    private int scoin; // Credits
    private int hcoin; // Jade
    private int mcoin; // Crystals

    private transient Battle battle;
    private transient Scene scene;
    private Position pos;
    private Position rot;
    private int planeId;
    private int floorId;
    private int entryId;
    
    private Set<Integer> unlockedHeadIcons;
    
    // Player managers
    private transient GameSession session;
    private transient final AvatarStorage avatars;
    private transient final Inventory inventory;
    private transient final ChatManager chatManager;
    private transient final Mailbox mailbox;
    private transient final ChallengeManager challengeManager;
    private transient final RogueManager rogueManager;
    
    // Database persistent data
    private LineupManager lineupManager;
    private PlayerGachaInfo gachaInfo;
    
    // Etc
    private transient boolean inAnchorRange;
    private transient int nextBattleId;
    
    @Setter private transient boolean paused;
    @Setter private transient ChallengeInstance challengeInstance;
    @Setter private transient RogueInstance rogueInstance;
    
    @Deprecated // Morphia only
    public Player() {
        this.curBasicType = GameConstants.TRAILBLAZER_AVATAR_ID;
        this.gender = PlayerGender.GENDER_MAN;
        this.avatars = new AvatarStorage(this);
        this.inventory = new Inventory(this);
        this.chatManager = new ChatManager(this);
        this.mailbox = new Mailbox(this);
        this.challengeManager = new ChallengeManager(this);
        this.rogueManager = new RogueManager(this);
    }

    // Called when player is created
    public Player(GameSession session) {
        this();
        this.resetPosition();
        this.session = session;
        this.accountUid = getAccount().getUid();
        this.name = GameConstants.DEFAULT_NAME;
        this.signature = "";
        this.headIcon = 200001;
        this.level = 1;
        this.stamina = GameConstants.MAX_STAMINA;

        this.unlockedHeadIcons = new HashSet<>();
        this.lineupManager = new LineupManager(this);
        this.gachaInfo = new PlayerGachaInfo();

        // Setup uid
        this.initUid();
        
        // Setup hero paths
        this.getAvatars().setupHeroPaths();

        // Give us a starter character and add it to our main lineup.
        // TODO script tutorial
        GameAvatar avatar = new GameAvatar(this.getCurHeroPath());
        this.addAvatar(avatar);
        this.getCurrentLineup().getAvatars().add(avatar.getAvatarId());
    }

    public GameServer getServer() {
        return session.getServer();
    }

    public Account getAccount() {
        return session.getAccount();
    }

    public void setSession(GameSession session) {
        if (this.session == null) {
            this.session = session;
        }
    }
    
    public boolean setNickname(String nickname) {
        if (nickname != this.name && nickname.length() <= 32) {
            this.name = nickname;
            this.sendPacket(new PacketPlayerSyncScNotify(this));
            this.save();
            return true;
        }
        
        return false;
    }
    
    public void setSignature(String signature) {
        if (signature.length() > 50) { // Client's max signature length is 50
            signature = signature.substring(0, 49);
        }
        this.signature = signature;
        this.save();
    }
    
    public int setBirthday(int birthday) {
        if (this.birthday == 0) {
            int month = birthday / 100;
            int day = birthday % 100;
            
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                this.birthday = birthday;
                this.save();
                return this.birthday;
            }
        }
        
        return 0;
    }
    
    public void setWorldLevel(int level) {
        this.worldLevel = level;
        this.save();
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }
    
    public Set<Integer> getUnlockedHeadIcons() {
        if (this.unlockedHeadIcons == null) {
            this.unlockedHeadIcons = new HashSet<>();
        }
        return this.unlockedHeadIcons;
    }
    
    public void addHeadIcon(int headIconId) {
        boolean success = this.getUnlockedHeadIcons().add(headIconId);
        if (success) {
            this.sendPacket(new PacketPlayerSyncScNotify(this.toBoardData()));
        }
    }
    
    public boolean setHeadIcon(int id) {
        if (this.getUnlockedHeadIcons().contains(id)) {
            this.headIcon = id;
            this.save();
            return true;
        }
        return false;
    }
    
    public void resetPosition() {
        if (this.hasLoggedIn()) {
            return;
        }
        
        this.pos = GameConstants.START_POS.clone();
        this.rot = new Position();
        this.planeId = GameConstants.START_PLANE_ID;
        this.floorId = GameConstants.START_FLOOR_ID;
        this.entryId = GameConstants.START_ENTRY_ID;
    }

    public boolean hasLoggedIn() {
        return this.getSession() != null && this.getSession().getState() != SessionState.WAITING_FOR_TOKEN;
    }

    public boolean addAvatar(GameAvatar avatar) {
        boolean success = getAvatars().addAvatar(avatar);
        if (success) {
            // Add profile picture of avatar
            int headIconId = 200000 + avatar.getAvatarId();
            if (GameData.getItemExcelMap().containsKey(headIconId)) {
                this.addHeadIcon(headIconId);
            }
        }
        return success;
    }

    public GameAvatar getAvatarById(int avatarId) {
        // Check if we are trying to retrieve the hero character
        if (GameData.getHeroExcelMap().containsKey(avatarId)) {
            avatarId = GameConstants.TRAILBLAZER_AVATAR_ID;
        }
        
        return getAvatars().getAvatarById(avatarId);
    }
    
    public PlayerLineup getCurrentLineup() {
        return this.getLineupManager().getCurrentLineup();
    }
    
    public GameAvatar getCurrentLeaderAvatar() {
        try {
            int avatarId = getCurrentLineup().getAvatars().get(this.getLineupManager().getCurrentLeader());
            return this.getAvatarById(avatarId);
        } catch (Exception e) {
            return null;
        }
    }

    private void initUid() {
        if (this.uid > 0) return;

        int nextUid = session.getAccount().getReservedPlayerUid();

        if (nextUid == GameConstants.SERVER_CONSOLE_UID) {
            nextUid = 0;
        }

        while (nextUid == 0 || LunarCore.getGameDatabase().checkIfObjectExists(Player.class, nextUid)) {
            nextUid = LunarCore.getGameDatabase().getNextObjectId(Player.class);
        }

        this.uid = nextUid;
    }

    public void addSCoin(int amount) {
        this.scoin += amount;
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }

    public void addHCoin(int amount) {
        this.hcoin += amount;
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }

    public void addMCoin(int amount) {
        this.mcoin += amount;
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }

    public void addStamina(int amount) {
        this.stamina = Math.min(this.stamina + amount, GameConstants.MAX_STAMINA);
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }

    public void addExp(int amount) {
        // Required exp
        int reqExp = GameData.getPlayerExpRequired(level + 1);

        // Add exp
        this.exp += amount;

        while (this.exp >= reqExp && reqExp > 0) {
            this.level += 1;
            reqExp = GameData.getPlayerExpRequired(this.level + 1);
        }

        // Save
        this.save();

        // Send packet
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }

    public int getDisplayExp() {
        return this.exp - GameData.getPlayerExpRequired(this.level);
    }
    
    public HeroPath getCurHeroPath() {
        return this.getAvatars().getHeroPathById(this.getCurBasicType());
    }
    
    public void setHeroBasicType(int heroType) {
        HeroPath path = this.getAvatars().getHeroPathById(heroType);
        if (path == null) return;
        
        GameAvatar mainCharacter = this.getAvatarById(GameConstants.TRAILBLAZER_AVATAR_ID);
        if (mainCharacter == null) return;
        
        // Set new hero and cur basic type
        mainCharacter.setHeroPath(path);
        this.curBasicType = heroType;
    }
    
    public int getNextBattleId() {
        return ++nextBattleId;
    }
    
    public boolean isInBattle() {
        return this.battle != null;
    }
    
    public void setBattle(Battle battle) {
        this.battle = battle;
    }
    
    public EntityProp interactWithProp(int propEntityId) {
        // Sanity
        if (this.getScene() == null) return null;
        
        // Get entity so we can cast it to a prop
        GameEntity entity = getScene().getEntityById(propEntityId);
        
        EntityProp prop = null;
        if (entity instanceof EntityProp) {
            prop = (EntityProp) entity;
        } else {
            return null;
        }
        
        // Handle prop interaction action
        switch (prop.getExcel().getPropType()) {
            case PROP_TREASURE_CHEST -> {
                if (prop.getState() == PropState.ChestClosed) {
                    // Open chest
                    prop.setState(PropState.ChestUsed);
                    // TODO handle drops
                    return prop;
                } else {
                    return null;
                }
            }
            case PROP_MAZE_PUZZLE -> {
                // Finish puzzle
                prop.setState(PropState.Locked);
                // Trigger event
                this.getScene().fireTrigger(PropTriggerType.PUZZLE_FINISH, prop.getGroupId(), prop.getInstId());
                //
                return prop;
            }
            default -> {
                return null;
            }
        }
    }
    
    public void onMove() {
        // Sanity
        if (this.getScene() == null) return;

        // Check anchors. We can optimize this later.
        EntityProp nearestAnchor = this.getScene().getNearestSpring(25_000_000); // 5000^2
        boolean isInRange = nearestAnchor != null;
        
        // Only heal if player isnt already in anchor range
        if (isInRange && isInRange != this.inAnchorRange) {
            this.getCurrentLineup().heal(10000, true);
        }
        this.inAnchorRange = isInRange;
    }
    
    public void moveTo(int entryId, Position pos) {
        this.entryId = entryId;
        this.moveTo(pos);
    }
    
    public void moveTo(Position pos) {
        this.getPos().set(pos);
        this.sendPacket(new PacketSceneEntityMoveScNotify(this));
    }
    
    public void moveTo(Position pos, Position rot) {
        this.getPos().set(pos);
        this.getRot().set(rot);
        this.sendPacket(new PacketSceneEntityMoveScNotify(this));
    }
    
    public boolean enterScene(int entryId, int teleportId, boolean sendPacket) {
        // Get map entrance excel
        MapEntranceExcel entry = GameData.getMapEntranceExcelMap().get(entryId);
        if (entry == null) return false;
        
        // Get floor info
        FloorInfo floor = GameData.getFloorInfo(entry.getPlaneID(), entry.getFloorID());
        if (floor == null) return false;
        
        // Get teleport anchor info (contains position) from the entry id
        int startGroup = entry.getStartGroupID();
        int anchorId = entry.getStartAnchorID();
        
        if (teleportId != 0) {
            PropInfo teleport = floor.getCachedTeleports().get(teleportId);
            if (teleport != null) {
                startGroup = teleport.getAnchorGroupID();
                anchorId = teleport.getAnchorID();
            }
        } else if (anchorId == 0) {
            startGroup = floor.getStartGroupID();
            anchorId = floor.getStartAnchorID();
        }
        
        AnchorInfo anchor = floor.getAnchorInfo(startGroup, anchorId);
        if (anchor == null) return false;

        // Move player to scene
        boolean success = this.loadScene(entry.getPlaneID(), entry.getFloorID(), entry.getId(), anchor.getPos(), anchor.getRot());
        
        // Send packet
        if (success && sendPacket) {
            this.sendPacket(new PacketEnterSceneByServerScNotify(this));
        }
        
        // Success
        return success;
    }

    private boolean loadScene(int planeId, int floorId, int entryId, Position pos, Position rot) {
        // Get maze plane excel
        MazePlaneExcel planeExcel = GameData.getMazePlaneExcelMap().get(planeId);
        if (planeExcel == null) {
            return false;
        }
        
        // Get scene that we want to enter
        Scene nextScene = null;
        
        if (getScene() != null && getScene().getPlaneId() == planeId && getScene().getFloorId() == floorId) {
            // Don't create a new scene if were already in the one we want to teleport to
            nextScene = this.scene;
        } else {
            nextScene = new Scene(this, planeExcel, floorId);
        }
        
        // Clear any extra data the player might have
        this.setChallengeInstance(null);

        // Set positions if player has logged in
        if (this.getSession().getState() != SessionState.WAITING_FOR_TOKEN) {
            this.getPos().set(pos);
            this.getRot().set(rot);
            this.planeId = planeId;
            this.floorId = floorId;
            this.entryId = entryId;
            this.save();
        }
        
        // Set player scene
        this.scene = nextScene;
        this.scene.setEntryId(entryId);
        
        // Done, return success
        return true;
    }

    public void sendMessage(String message) {
        var msg = new ChatMessage(GameConstants.SERVER_CONSOLE_UID, this.getUid(), message);
        this.getChatManager().addChatMessage(msg);
    }

    public void sendPacket(BasePacket packet) {
        if (this.hasLoggedIn()) {
            this.getSession().send(packet);
        }
    }

    public void save() {
        if (this.uid <= 0) {
            LunarCore.getLogger().error("Tried to save a player object without a uid!");
            return;
        }

        LunarCore.getGameDatabase().save(this);
    }

    public void onLogin() {
        // Validate
        if (this.getRot() == null) this.rot = new Position();
        
        // Load avatars and inventory first
        this.getAvatars().loadFromDatabase();
        this.getInventory().loadFromDatabase();
        this.getMailbox().loadFromDatabase();
        this.getChallengeManager().loadFromDatabase();

        // Load Etc
        this.getLineupManager().validate(this);
        this.getAvatars().setupHeroPaths();

        // Enter scene (should happen after everything else loads)
        this.loadScene(planeId, floorId, entryId, this.getPos(), this.getRot());
    }

    // Proto
    
    public PlayerBasicInfo toProto() {
        var proto = PlayerBasicInfo.newInstance()
                .setNickname(this.getName())
                .setLevel(this.getLevel())
                .setExp(this.getDisplayExp())
                .setWorldLevel(this.getWorldLevel())
                .setScoin(this.getScoin())
                .setHcoin(this.getHcoin())
                .setMcoin(this.getMcoin())
                .setStamina(this.getStamina());
        
        return proto;
    }
    
    public BoardDataSync toBoardData() {
        var proto = BoardDataSync.newInstance()
                .setSignature(this.getSignature());
        
        for (int id : this.getUnlockedHeadIcons()) {
            proto.addUnlockedHeadIconList(HeadIcon.newInstance().setId(id));
        }
        
        return proto;
    }
}
