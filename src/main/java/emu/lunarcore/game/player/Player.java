package emu.lunarcore.game.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.mongodb.client.model.Filters;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.config.FloorInfo;
import emu.lunarcore.data.config.PropInfo;
import emu.lunarcore.data.excel.InteractExcel;
import emu.lunarcore.data.excel.ItemUseExcel;
import emu.lunarcore.data.excel.MapEntranceExcel;
import emu.lunarcore.data.excel.MazePlaneExcel;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.avatar.AvatarStorage;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.avatar.AvatarMultiPath;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.challenge.ChallengeGroupReward;
import emu.lunarcore.game.challenge.ChallengeHistory;
import emu.lunarcore.game.challenge.ChallengeInstance;
import emu.lunarcore.game.challenge.ChallengeManager;
import emu.lunarcore.game.chat.ChatManager;
import emu.lunarcore.game.chat.ChatMessage;
import emu.lunarcore.game.enums.PlaneType;
import emu.lunarcore.game.enums.PropState;
import emu.lunarcore.game.enums.PropType;
import emu.lunarcore.game.friends.FriendList;
import emu.lunarcore.game.friends.Friendship;
import emu.lunarcore.game.gacha.PlayerGachaInfo;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.inventory.Inventory;
import emu.lunarcore.game.mail.Mail;
import emu.lunarcore.game.mail.Mailbox;
import emu.lunarcore.game.player.lineup.LineupManager;
import emu.lunarcore.game.player.lineup.PlayerExtraLineup;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.game.rogue.RogueInstance;
import emu.lunarcore.game.rogue.RogueManager;
import emu.lunarcore.game.rogue.RogueTalentData;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.SceneBuff;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.game.scene.entity.GameEntity;
import emu.lunarcore.proto.BoardDataSyncOuterClass.BoardDataSync;
import emu.lunarcore.proto.DisplayAvatarOuterClass.DisplayAvatar;
import emu.lunarcore.proto.FriendOnlineStatusOuterClass.FriendOnlineStatus;
import emu.lunarcore.proto.HeadIconOuterClass.HeadIcon;
import emu.lunarcore.proto.PlatformTypeOuterClass.PlatformType;
import emu.lunarcore.proto.PlayerBasicInfoOuterClass.PlayerBasicInfo;
import emu.lunarcore.proto.PlayerDetailInfoOuterClass.PlayerDetailInfo;
import emu.lunarcore.proto.PlayerSyncScNotifyOuterClass.PlayerSyncScNotify;
import emu.lunarcore.proto.RogueCurVirtualItemInfoOuterClass.RogueCurVirtualItemInfo;
import emu.lunarcore.proto.SimpleInfoOuterClass.SimpleInfo;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.game.Syncable;
import emu.lunarcore.server.game.Tickable;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.send.*;
import emu.lunarcore.util.Position;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import us.hebi.quickbuf.RepeatedInt;
import us.hebi.quickbuf.RepeatedMessage;

@Entity(value = "players", useDiscriminator = false)
@Getter
public class Player implements Tickable, Syncable {
    @Id private int uid;
    @Indexed private String accountUid;
    private String name;
    private String signature;
    private int birthday;
    private int headIcon;
    private int phoneTheme;
    private int chatBubble;
    private int currentBgm;
    @Setter private int petId;
    private Map<Integer, Integer> curAvatarPaths;
    private PlayerGender gender;
    
    private int level;
    private int exp; // Total exp
    private int worldLevel;
    private int scoin; // Credits
    private int hcoin; // Jade
    private int mcoin; // Crystals
    private int talentPoints; // Rogue talent points
    
    private int stamina;
    private double staminaReserve;
    private long nextStaminaRecover;

    private transient Battle battle;
    private transient Scene scene;
    private Position pos;
    private Position rot;
    private int planeId;
    private int floorId;
    private int entryId;
    private int worldId;
    
    private long lastActiveTime;
    
    // Player managers
    private transient GameSession session;
    private transient final AvatarStorage avatars;
    private transient final Inventory inventory;
    private transient final ChatManager chatManager;
    private transient final FriendList friendList;
    private transient final Mailbox mailbox;
    private transient final ChallengeManager challengeManager;
    private transient final RogueManager rogueManager;
    
    // Database persistent data
    private LineupManager lineupManager;
    private PlayerGachaInfo gachaInfo;
    private List<ObjectId> assistAvatars;
    private List<ObjectId> displayAvatars;

    // Instances
    @Setter private ChallengeInstance challengeInstance;
    @Setter private transient RogueInstance rogueInstance;
    
    // Etc
    private transient boolean isNew;
    private transient boolean loggedIn;
    private transient boolean inAnchorRange;
    private transient int nextBattleId;
    private transient PlayerUnlockData unlocks;
    private transient Int2ObjectMap<SceneBuff> foodBuffs;
    
    @Setter private transient boolean paused;
    
    @Deprecated // Morphia only
    public Player() {
        this.gender = PlayerGender.GENDER_MAN;
        this.foodBuffs = new Int2ObjectOpenHashMap<>();
        this.assistAvatars = new ArrayList<>();
        this.displayAvatars = new ArrayList<>();
        this.curAvatarPaths = new HashMap<>();
        
        this.avatars = new AvatarStorage(this);
        this.inventory = new Inventory(this);
        this.chatManager = new ChatManager(this);
        this.friendList = new FriendList(this);
        this.mailbox = new Mailbox(this);
        this.challengeManager = new ChallengeManager(this);
        this.rogueManager = new RogueManager(this);
    }

    // Called when player is created
    public Player(GameSession session) {
        this();
        this.session = session;
        this.accountUid = getAccount().getUid();
        this.isNew = true;
        this.initUid();
        this.resetPosition();
        this.setLevel(LunarCore.getConfig().getServerOptions().startTrailblazerLevel);
        
        // Setup player data
        this.name = GameConstants.DEFAULT_NAME;
        this.signature = "";
        this.phoneTheme = 221000;
        this.chatBubble = 220000;
        this.stamina = GameConstants.MAX_STAMINA;
        this.nextStaminaRecover = System.currentTimeMillis();

        this.currentBgm = 210000;
        
        this.lineupManager = new LineupManager(this);
        this.gachaInfo = new PlayerGachaInfo();
        this.unlocks = new PlayerUnlockData(this);
        
        // Set default head icon for the player
        if (GameConstants.DEFAULT_HEAD_ICONS.length > 0) {
            this.headIcon = GameConstants.DEFAULT_HEAD_ICONS[0];
        }
        
        // Setup hero paths
        this.getAvatars().validateMultiPaths();

        // Give us the main character
        // TODO script tutorial
        GameAvatar avatar = new GameAvatar(GameConstants.TRAILBLAZER_AVATAR_ID);
        this.addAvatar(avatar);
        this.getCurrentLineup().getAvatars().add(avatar.getAvatarId());
        this.getCurrentLineup().save();
    }

    public GameServer getServer() {
        return session.getServer();
    }

    public Account getAccount() {
        return session.getAccount();
    }

    public void setLevel(int lvl) {
        int oldLevel = this.level;
        int newLevel = Math.max(Math.min(lvl, GameConstants.MAX_TRAILBLAZER_LEVEL), 1);
        this.onLevelChange(oldLevel, newLevel);
        
        this.level = newLevel;
        this.exp = GameData.getPlayerExpRequired(this.level);
        
        if (this.isOnline()) {
            this.getSession().send(new PacketPlayerSyncScNotify(this));
            this.save();
        }
    }
    
    private void onLevelChange(int oldLevel, int newLevel) {
        // Auto upgrades the player's world level when they level up to the right level
        if (LunarCore.getConfig().getServerOptions().autoUpgradeWorldLevel) {
            int maxWorldLevel = 0;
            
            for (int i = 0; i < GameConstants.WORLD_LEVEL_UPGRADES.length; i++) {
                if (newLevel >= GameConstants.WORLD_LEVEL_UPGRADES[i]) {
                    maxWorldLevel = i;
                } else {
                    break;
                }
            }
            
            if (maxWorldLevel > this.getWorldLevel()) {
                this.setWorldLevel(maxWorldLevel);
            }
        }
    }
    
    public boolean isOnline() {
        return this.getSession() != null && this.loggedIn;
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
        if (this.worldLevel == level) {
            return;
        }
        
        this.worldLevel = level;
        
        if (this.isOnline()) {
            this.save();
            this.getSession().send(new PacketPlayerSyncScNotify(this));
        }
    }

    public int getCurrentBgm() {
        if (this.currentBgm == 0) {
            this.currentBgm = 210000;
            this.save();
        }
        return this.currentBgm;
    }

    public void setCurrentBgm(int musicId) {
        this.currentBgm = musicId;
        this.save();
    }
    
    public void resetPosition() {
        if (this.isOnline()) {
            return;
        }
        
        this.pos = GameConstants.START_POS.clone();
        this.rot = new Position();
        this.planeId = GameConstants.START_PLANE_ID;
        this.floorId = GameConstants.START_FLOOR_ID;
        this.entryId = GameConstants.START_ENTRY_ID;
    }

    public boolean addAvatar(GameAvatar avatar) {
        return getAvatars().addAvatar(avatar);
    }

    public GameAvatar getAvatarById(int avatarId) {
        return getAvatars().getAvatarById(avatarId);
    }
    
    public GameAvatar getAvatarById(ObjectId id) {
        return getAvatars().getAvatarById(id);
    }
    
    public boolean setHeadIcon(int id) {
        if (this.getUnlocks().getHeadIcons().contains(id)) {
            this.headIcon = id;
            if (this.isLoggedIn()) {
                this.save();
            }
            return true;
        }
        return false;
    }
    
    public boolean setChatBubble(int id) {
        if (this.getUnlocks().getChatBubbles().contains(id)) {
            this.chatBubble = id;
            this.save();
            this.sendPacket(new PacketPlayerSyncScNotify(this));
            return true;
        }
        return false;
    }
    
    public boolean setPhoneTheme(int id) {
        if (this.getUnlocks().getPhoneThemes().contains(id)) {
            this.phoneTheme = id;
            this.save();
            this.sendPacket(new PacketPlayerSyncScNotify(this));
            return true;
        }
        return false;
    }
    
    public PlayerLineup getCurrentLineup() {
        return this.getLineupManager().getCurrentLineup();
    }
    
    public GameAvatar getCurrentLeaderAvatar() {
        return this.getLineupManager().getCurrentLeaderAvatar();
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
        int newAmount = Utils.safeAdd(this.scoin, amount);
        if (this.scoin != newAmount) {
            this.scoin = newAmount;
            this.sendPacket(new PacketPlayerSyncScNotify(this));
        }
    }

    public void addHCoin(int amount) {
        int newAmount = Utils.safeAdd(this.hcoin, amount);
        if (this.hcoin != newAmount) {
            this.hcoin = newAmount;
            this.sendPacket(new PacketPlayerSyncScNotify(this));
        }
    }

    public void addMCoin(int amount) {
        int newAmount = Utils.safeAdd(this.mcoin, amount);
        if (this.mcoin != newAmount) {
            this.mcoin = newAmount;
            this.sendPacket(new PacketPlayerSyncScNotify(this));
        }
    }
    
    public void addTalentPoints(int amount) {
        int newAmount = Utils.safeAdd(this.talentPoints, amount);
        if (this.talentPoints != newAmount) {
            this.talentPoints = newAmount;
            this.sendPacket(new PacketSyncRogueVirtualItemInfoScNotify(this));
        }
    }

    public void addExp(int amount) {
        // Setup
        int oldLevel = this.level;
        int expRequired = GameData.getPlayerExpRequired(level + 1);

        // Add exp
        this.exp += amount;

        // Check for level ups
        while (this.exp >= expRequired && expRequired > 0) {
            this.level += 1;
            expRequired = GameData.getPlayerExpRequired(this.level + 1);
        }

        // Update level and change property
        if (oldLevel != this.level) {
            this.onLevelChange(oldLevel, this.level);
        }
        
        // Save to database
        this.save();

        // Send packet
        this.sendPacket(new PacketPlayerSyncScNotify(this));
    }

    public int getDisplayExp() {
        return this.exp - GameData.getPlayerExpRequired(this.level);
    }
    
    public int getCurAvatarPathId(int baseAvatarId) {
        return this.getCurAvatarPaths().getOrDefault(baseAvatarId, 0);
    }
    
    public AvatarMultiPath getCurAvatarPath(int baseAvatarId) {
        int pathId = this.getCurAvatarPathId(baseAvatarId);
        return this.getAvatars().getMultiPathById(pathId);
    }
    
    public int setAvatarPath(int pathId) {
        // Get excel
        var excel = GameData.getMultiplePathAvatarExcelMap().get(pathId);
        if (excel == null) return 0;
        
        // Get path data
        AvatarMultiPath path = this.getAvatars().getMultiPathById(pathId);
        if (path == null) return 0;
        
        // Get avatar data
        GameAvatar avatar = this.getAvatarById(excel.getBaseAvatarID());
        if (avatar == null) return 0;
        
        // Set new avatar path
        avatar.setMultiPath(path);
        
        // Set gender if we are changing the main character
        if (excel.getBaseAvatarID() == GameConstants.TRAILBLAZER_AVATAR_ID && excel.getGender() != null) {
            this.gender = excel.getGender();
        }
        
        // Set current avatar path and save to database
        this.getCurAvatarPaths().put(excel.getBaseAvatarID(), pathId);
        this.save();

        // Sync with client
        this.sendPacket(new PacketAvatarPathChangedNotify(avatar, path));
        this.sendPacket(new PacketPlayerSyncScNotify(avatar));
        
        // Success
        return pathId;
    }
    
    public int getNextBattleId() {
        return ++nextBattleId;
    }
    
    public boolean isInBattle() {
        return this.battle != null;
    }
    
    public void setBattle(Battle battle) {
        // Set battle first
        this.battle = battle;
        // Scene handler
        if (this.getScene() != null) {
            this.getScene().onBattleStart(battle);
        }
    }
    
    public void forceQuitBattle() {
        if (this.battle != null) {
            this.battle = null;
            this.getSession().send(CmdId.QuitBattleScNotify);  
        }
    }
    
    public void addStamina(int amount) {
        this.stamina = Utils.safeAdd(this.stamina, amount);
        this.sendPacket(new PacketStaminaInfoScNotify(this));
    }
    
    public void spendStamina(int amount) {
        if (!LunarCore.getConfig().getServerOptions().spendStamina) {
            return;
        }
        this.stamina = Math.max(this.stamina - amount, 0);
        this.sendPacket(new PacketStaminaInfoScNotify(this));
    }
    
    public int exchangeReserveStamina(int amount) {
        // Sanity checks
        if (amount <= 0 || this.staminaReserve < amount) {
            return 0;
        }
        
        this.staminaReserve -= amount;
        this.stamina += amount;
        
        // Update to client
        this.sendPacket(new PacketStaminaInfoScNotify(this));
        return amount;
    }
    
    private void updateStamina(long timestamp) {
        // Setup on change flag
        boolean hasChanged = false;
        
        // Check if we can add stamina
        while (timestamp >= this.nextStaminaRecover) {
            // Add stamina
            if (this.stamina < GameConstants.MAX_STAMINA) {
                this.stamina += 1;
                hasChanged = true;
            } else if (this.stamina < GameConstants.MAX_STAMINA_RESERVE) {
                double rate = LunarCore.getConfig().getServerOptions().getStaminaReserveRecoveryRate();
                double amount = (timestamp - this.nextStaminaRecover) / (rate * 1000D);
                this.staminaReserve = Math.min(this.staminaReserve + amount, GameConstants.MAX_STAMINA_RESERVE);
                hasChanged = true;
            }
            
            // Calculate next stamina recover time
            if (this.stamina >= GameConstants.MAX_STAMINA) {
                this.nextStaminaRecover = timestamp;
            }
            
            this.nextStaminaRecover += LunarCore.getConfig().getServerOptions().getStaminaRecoveryRate() * 1000;
        }
        
        // Send packet
        if (hasChanged && this.isOnline()) {
            this.getSession().send(new PacketStaminaInfoScNotify(this));
        }
    }
    
    public synchronized boolean addFoodBuff(int type, ItemUseExcel itemUseExcel) {
        // Get maze excel
        var excel = GameData.getMazeBuffExcel(itemUseExcel.getMazeBuffID(), 1);
        if (excel == null) return false;
        
        // Create new buff
        var buff = new SceneBuff(itemUseExcel.getMazeBuffID());
        buff.setCount(Math.max(itemUseExcel.getActivityCount(), 1));
        
        int avatarEntityId = getCurrentLeaderAvatar().getEntityId();
        var oldBuff = this.getFoodBuffs().put(type, buff);
        
        // Send packets
        if (oldBuff != null) {
            this.sendPacket(new PacketSyncEntityBuffChangeListScNotify(avatarEntityId, oldBuff.getBuffId()));
        }
        
        this.sendPacket(new PacketSyncEntityBuffChangeListScNotify(avatarEntityId, buff));
        return true;
    }
    
    public synchronized boolean removeFoodBuffs(int amount) {
        // Sanity check
        if (getFoodBuffs().size() == 0) return false;
        
        // Cache current avatar entity id
        int avatarEntityId = getCurrentLeaderAvatar().getEntityId();
        
        // Remove and send packet for each buff removed
        for (var it = getFoodBuffs().int2ObjectEntrySet().iterator(); it.hasNext();) {
            var entry = it.next();
            var buff = entry.getValue();
            
            if (buff.decrementAndGet() <= 0) {
                it.remove();
                this.sendPacket(new PacketSyncEntityBuffChangeListScNotify(avatarEntityId, buff.getBuffId()));
            } else {
                this.sendPacket(new PacketSyncEntityBuffChangeListScNotify(avatarEntityId, buff));
            }
        }
        
        return true;
    }
    
    public EntityProp interactWithProp(int interactId, int propEntityId) {
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
        
        // Get interact handler
        InteractExcel interactExcel = GameData.getInteractExcelMap().get(interactId);
        if (interactExcel == null) {
            return prop;
        }
        
        // Validate
        if (interactExcel.getSrcState() != null && prop.getState() != interactExcel.getSrcState()) {
            return prop;
        }
        
        // Save old state
        PropState oldState = prop.getState();
        PropState newState = interactExcel.getTargetState();
        
        // Set group and prop state
        this.sendPacket(new PacketGroupStateChangeScNotify(getEntryId(), prop.getGroupId(), newState));
        prop.setState(newState);
        
        // Handle any extra interaction actions
        switch (prop.getExcel().getPropType()) {
            case PROP_DESTRUCT -> {
                if (prop.getState() == PropState.Closed) {
                    // Open chest
                    prop.setState(PropState.Open);
                } else {
                    prop.setState(PropState.Closed);
                }
            }
            case PROP_TREASURE_CHEST -> {
                if (oldState == PropState.ChestClosed && newState == PropState.ChestUsed) {
                    // Handle drops
                    var drops = this.getServer().getDropService().calculateDropsFromProp(prop.getPropId());
                    this.getInventory().addItems(drops, true);
                }
            }
            case PROP_MAZE_PUZZLE -> {
                // Trigger event
                if (newState == PropState.Open || newState == PropState.Closed) {
                    // Unlock everything in the prop's group
                    for (var p : getScene().getEntitiesByGroup(EntityProp.class, prop.getGroupId())) {
                        if (p.getPropType() == PropType.PROP_TREASURE_CHEST) {
                            p.setState(PropState.ChestClosed);
                        } else if (p.getPropType() == PropType.PROP_MAZE_PUZZLE) {
                            // Skip
                        } else {
                            p.setState(PropState.Open);
                        }
                    }
                }
            }
            case PROP_ORDINARY -> {
                if (prop.getPropInfo() != null && prop.getPropInfo().isCommonConsole()) {
                    // Set platform states
                    getScene().getEntitiesByGroup(EntityProp.class, prop.getGroupId())
                        .stream()
                        .filter(p -> p.getExcel().getPropType() == PropType.PROP_PLATFORM)
                        .forEach(p -> p.setState(newState));
                }
            }
            default -> {
                // Skip
            }
        }
        
        // Return prop when we are done
        return prop;
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
            var group = floor.getGroupInfoByIndex(floor.getStartGroupIndex());
            if (group == null) return false;
            
            startGroup = group.getId();
            anchorId = floor.getStartAnchorID();
        }
        
        AnchorInfo anchor = floor.getAnchorInfo(startGroup, anchorId);
        if (anchor == null) return false;

        // Move player to scene
        return this.loadScene(entry.getPlaneID(), entry.getFloorID(), entry.getId(), anchor.getPos(), anchor.getRot(), sendPacket);
    }

    public boolean loadScene(int planeId, int floorId, int entryId, Position pos, Position rot, boolean sendPacket) {
        // Get maze plane excel
        MazePlaneExcel planeExcel = GameData.getMazePlaneExcelMap().get(planeId);
        if (planeExcel == null) return false;

        // Unstuck check
        if (planeExcel.getPlaneType() == PlaneType.Challenge) {
            if (this.getChallengeInstance() == null) {
                return enterScene(GameConstants.CHALLENGE_ENTRANCE, 0, false);
            }
        } else {
            this.setChallengeInstance(null);
        }
        
        if (planeExcel.getPlaneType() == PlaneType.Rogue) {
            if (this.getRogueInstance() == null) {
                return enterScene(GameConstants.ROGUE_ENTRANCE, 0, false);
            }
        }
        
        // Get scene that we want to enter
        Scene nextScene = null;
        
        if (getScene() != null && getScene().getPlaneId() == planeId && getScene().getFloorId() == floorId && getScene().getPlaneType() != PlaneType.Rogue) {
            // Don't create a new scene if were already in the one we want to teleport to
            nextScene = this.scene;
        } else {
            nextScene = new Scene(this, planeExcel, floorId);
        }
        
        // Set world id
        if (planeExcel.getPlaneType() == PlaneType.Town || planeExcel.getPlaneType() == PlaneType.Maze) {
            this.worldId = planeExcel.getWorldID();
        }
        
        // Set player position
        this.getPos().set(pos);
        this.getRot().set(rot);

        // Save if scene has changed
        if (this.planeId != planeId || this.floorId != floorId || this.entryId != entryId) {
            this.planeId = planeId;
            this.floorId = floorId;
            this.entryId = entryId;
            this.save();
        }
        
        // Set player scene
        this.scene = nextScene;
        this.scene.setEntryId(entryId);
        
        // Send packet
        if (sendPacket) {
            this.sendPacket(new PacketEnterSceneByServerScNotify(this));
        }
        
        // Done, return success
        return true;
    }
    
    public void setAssistAvatars(RepeatedInt avatars) {
        // Check size
        if (avatars.length() > 3) {
            return;
        }
        
        // Clear
        this.getAssistAvatars().clear();
        
        // Parse list from proto
        for (int id : avatars) {
            GameAvatar avatar = this.getAvatarById(id);
            if (avatar == null) continue;
            
            this.getAssistAvatars().add(avatar.getId());
        }
        
        // Save player
        this.save();
    }
    
    public void setDisplayAvatars(RepeatedMessage<DisplayAvatar> avatars) {
        // Check size
        if (avatars.length() > 5) {
            return;
        }
        
        // Clear
        this.getDisplayAvatars().clear();
        
        // Parse list from proto
        for (int i = 0; i < avatars.length(); i++) {
            var info = avatars.get(i);
            
            if (info.getAvatarId() > 0) {
                GameAvatar avatar = this.getAvatarById(info.getAvatarId());
                if (avatar == null) continue;
                
                this.getDisplayAvatars().add(avatar.getId());
            } else {
                this.getDisplayAvatars().add(null);
            }
        }
        
        // Save player
        this.save();
    }

    public void sendMessage(String message) {
        var msg = new ChatMessage(GameConstants.SERVER_CONSOLE_UID, this.getUid(), message);
        this.getChatManager().addChatMessage(msg);
    }

    public void sendPacket(BasePacket packet) {
        if (this.isOnline()) {
            this.getSession().send(packet);
        }
    }
    
    public void onTick(long timestamp, long delta) {
        // Update stamina
        this.updateStamina(timestamp);
        // Scene update
        if (this.getScene() != null) {
            this.getScene().onTick(timestamp, delta);
        }
    }
    
    public void onLogin() {
        // Set up lineup manager
        this.getLineupManager().setPlayer(this);

        // Load avatars and inventory first
        this.getAvatars().loadFromDatabase();
        this.getInventory().loadFromDatabase();
        this.getLineupManager().loadFromDatabase();
        this.getFriendList().loadFromDatabase();
        this.getMailbox().loadFromDatabase();
        this.getChallengeManager().loadFromDatabase();
        this.getRogueManager().loadFromDatabase();
        
        // Load unlockables
        this.loadUnlocksFromDatabase();
        
        // Update stamina
        this.updateStamina(System.currentTimeMillis());

        // Check instances
        if (this.getChallengeInstance() != null && !this.getChallengeInstance().validate(this)) {
            // Delete instance if it failed to validate (example: missing an excel)
            this.challengeInstance = null;
        }
        
        // Set default world id if we don't have it
        if (this.worldId == 0) {
            this.worldId = GameConstants.DEFAULT_WORLD_ID;
        }
        
        // Unstuck check, dont load player into raid scenes
        MazePlaneExcel planeExcel = GameData.getMazePlaneExcelMap().get(planeId);
        if (planeExcel == null || planeExcel.getPlaneType().getVal() >= PlaneType.Raid.getVal()) {
            this.resetPosition();
        }
        
        // Load into saved scene (should happen after everything else loads)
        this.loadScene(planeId, floorId, entryId, this.getPos(), this.getRot(), false);
        
        // Reset position to starting scene in case we couldn't load the scene
        if (this.getScene() == null) {
            this.enterScene(GameConstants.START_ENTRY_ID, 0, false);
        }
        
        // Make sure the current lineup's leader exists
        var lineup = this.getCurrentLineup();
        if (lineup.size() == 0) {
            lineup.getAvatars().add(GameConstants.TRAILBLAZER_AVATAR_ID);
            lineup.setLeader(0);
            lineup.save();
        } else if (lineup.getLeader() >= lineup.size()) {
            lineup.setLeader(0);
            lineup.save();
        }
        
        // Sanity check lineup to prevent the player from getting stuck in a loading screen if they loaded into the game with an avatar that had 0 hp
        var leader = this.getCurrentLeaderAvatar();
        if (leader != null && leader.getCurrentHp(this.getCurrentLineup()) <= 0) {
            leader.setCurrentHp(this.getCurrentLineup(), 2000);
            leader.save();
        }
        
        // Send welcome mail after we load managers from the database
        if (this.isNew) {
            this.getMailbox().sendWelcomeMail();
        }
        
        // Set logged in flag
        this.lastActiveTime = System.currentTimeMillis() / 1000;
        this.loggedIn = true;
    }

    public void onLogout() {
        this.loggedIn = false;
        this.lastActiveTime = System.currentTimeMillis() / 1000;
    }
    
    // Database

    public void save() {
        if (this.uid <= 0) {
            LunarCore.getLogger().error("Tried to save a player object without a uid!");
            return;
        }

        LunarCore.getGameDatabase().save(this);
    }
    
    public void delete() {
        // Close session first
        if (this.getSession() != null) {
            this.getSession().getPlayer().sendPacket(new PacketPlayerKickOutScNotify(5));
        }
        
        // Cache filter object so we can reuse it for our delete queries
        var filter = Filters.eq("ownerUid", uid);
        var datastore = LunarCore.getGameDatabase().getDatastore();
        
        // Delete data from collections
        datastore.getCollection(GameAvatar.class).deleteMany(filter);
        datastore.getCollection(ChallengeHistory.class).deleteMany(filter);
        datastore.getCollection(ChallengeGroupReward.class).deleteMany(filter);
        datastore.getCollection(AvatarMultiPath.class).deleteMany(filter);
        datastore.getCollection(GameItem.class).deleteMany(filter);
        datastore.getCollection(PlayerLineup.class).deleteMany(filter);
        datastore.getCollection(PlayerExtraLineup.class).deleteMany(filter);
        datastore.getCollection(Mail.class).deleteMany(filter);
        datastore.getCollection(RogueTalentData.class).deleteMany(filter);
        datastore.getCollection(PlayerUnlockData.class).deleteOne(filter);
        
        // Delete friendships
        datastore.getCollection(Friendship.class).deleteMany(Filters.or(Filters.eq("ownerUid", uid), Filters.eq("friendUid", uid)));
        
        // Delete the player last
        LunarCore.getGameDatabase().delete(this);
    }
    
    private void loadUnlocksFromDatabase() {
        this.unlocks = LunarCore.getGameDatabase().getObjectByField(PlayerUnlockData.class, "ownerUid", this.getUid());
        
        if (this.unlocks == null) {
            this.unlocks = new PlayerUnlockData(this);
        } else {
            this.unlocks.setOwner(this);
        }
    }
    
    // Player sync
    
    public void onSync(PlayerSyncScNotify proto) {
        proto.setBasicInfo(this.toProto());
    }
    
    // Protobuf serialization
    
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
    
    public PlayerDetailInfo toDetailInfo() {
        var proto = PlayerDetailInfo.newInstance()
                .setUid(this.getUid())
                .setNickname(this.getName())
                .setSignature(this.getSignature())
                .setLevel(this.getLevel())
                .setWorldLevel(this.getWorldLevel())
                .setPlatformType(PlatformType.PC)
                .setShowDisplayAvatars(true)
                .setHeadIcon(this.getHeadIcon());
        
        proto.getMutableRecordInfo().getMutableCollectionInfo();
        proto.getMutableDisplaySettings();
        
        for (int i = 0; i < this.getAssistAvatars().size(); i++) {
            ObjectId objectId = this.getAssistAvatars().get(i);
            
            GameAvatar avatar = this.getAvatarById(objectId);
            if (avatar == null && this.getSession() == null) {
                avatar = this.getAvatars().loadAvatarByObjectId(objectId);
            }
            
            if (avatar == null) continue;
            
            var info = avatar.toDisplayAvatarProto();
            info.setPos(i);
            
            proto.addAssistAvatarList(info);
        }
        
        for (int i = 0; i < this.getDisplayAvatars().size(); i++) {
            ObjectId objectId = this.getDisplayAvatars().get(i);
            if (objectId == null) continue;
            
            GameAvatar avatar = this.getAvatarById(objectId);
            if (avatar == null && this.getSession() == null) {
                avatar = this.getAvatars().loadAvatarByObjectId(objectId);
            }
            
            if (avatar == null) continue;
            
            var info = avatar.toDisplayAvatarProto();
            info.setPos(i);
            
            proto.addDisplayAvatarList(info);
        }
        
        return proto;
    }
    
    public SimpleInfo toSimpleInfo() {
        var proto = SimpleInfo.newInstance()
                .setUid(this.getUid())
                .setNickname(this.getName())
                .setSignature(this.getSignature())
                .setLevel(this.getLevel())
                .setChatBubbleId(this.getChatBubble())
                .setOnlineStatus(this.isOnline() ? FriendOnlineStatus.FRIEND_ONLINE_STATUS_ONLINE : FriendOnlineStatus.FRIEND_ONLINE_STATUS_OFFLINE)
                .setPlatformType(PlatformType.PC)
                .setLastActiveTime(this.getLastActiveTime())
                .setHeadIcon(this.getHeadIcon());
        
        for (int i = 0; i < this.getAssistAvatars().size(); i++) {
            ObjectId objectId = this.getAssistAvatars().get(i);
            
            GameAvatar avatar = this.getAvatarById(objectId);
            if (avatar == null && this.getSession() == null) {
                avatar = this.getAvatars().loadAvatarByObjectId(objectId);
            }
            
            if (avatar == null) continue;
            
            var info = avatar.toAssistSimpleProto();
            info.setPos(i);
            
            proto.addAssistSimpleInfo(info);
        }
        
        return proto;
    }
    
    public BoardDataSync toBoardData() {
        var proto = BoardDataSync.newInstance()
                .setSignature(this.getSignature());
        
        for (int id : this.getUnlocks().getHeadIcons()) {
            proto.addUnlockedHeadIconList(HeadIcon.newInstance().setId(id));
        }
        
        return proto;
    }
    
    public RogueCurVirtualItemInfo getCurRogueVirtualItem() {
        var proto = RogueCurVirtualItemInfo.newInstance()
                .setCurTalentCoin(this.getTalentPoints());
        
        if (this.getRogueInstance() != null) {
            proto.setCurRogueCoin(this.getRogueInstance().getCoin());
        }
        
        return proto;
    }
}
