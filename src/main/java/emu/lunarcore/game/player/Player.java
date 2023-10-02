package emu.lunarcore.game.player;

import java.util.HashSet;
import java.util.Set;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.annotations.Indexed;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarRail;
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
import emu.lunarcore.game.gacha.PlayerGachaInfo;
import emu.lunarcore.game.inventory.Inventory;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.game.scene.entity.EntityProp;
import emu.lunarcore.proto.BoardDataSyncOuterClass.BoardDataSync;
import emu.lunarcore.proto.HeadIconOuterClass.HeadIcon;
import emu.lunarcore.proto.PlayerBasicInfoOuterClass.PlayerBasicInfo;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.server.packet.send.PacketEnterSceneByServerScNotify;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.server.packet.send.PacketRevcMsgScNotify;
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
    private PlayerGender gender;
    private int curBasicType;

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
    private int planeId;
    private int floorId;
    private int entryId;
    
    private Set<Integer> unlockedHeadIcons;
    
    // Player managers
    private transient GameSession session;
    private transient final AvatarStorage avatars;
    private transient final Inventory inventory;

    // Database persistent data
    private LineupManager lineupManager;
    private PlayerGachaInfo gachaInfo;
    
    // Etc
    @Setter private transient boolean paused;
    private transient boolean inAnchorRange;
    private transient int nextBattleId;

    @Deprecated // Morphia only
    public Player() {
        this.curBasicType = GameConstants.TRAILBLAZER_AVATAR_ID;
        this.gender = PlayerGender.GENDER_MAN;
        this.avatars = new AvatarStorage(this);
        this.inventory = new Inventory(this);
    }

    // Called when player is created
    public Player(GameSession session) {
        this();
        this.session = session;
        this.accountUid = getAccount().getUid();
        this.name = GameConstants.DEFAULT_NAME;
        this.signature = "";
        this.headIcon = 200001;
        this.level = 1;
        this.stamina = GameConstants.MAX_STAMINA;

        this.pos = new Position(99, 62, -4800);
        this.planeId = 20001;
        this.floorId = 20001001;
        this.entryId = 2000101;

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
        this.getAvatars().addAvatar(avatar);
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
        this.getUnlockedHeadIcons().add(headIconId);
        this.sendPacket(new PacketPlayerSyncScNotify(this.toBoardData()));
    }
    
    public boolean setHeadIcon(int id) {
        if (this.getUnlockedHeadIcons().contains(id)) {
            this.headIcon = id;
            this.save();
            return true;
        }
        return false;
    }

    public boolean hasLoggedIn() {
        return this.getSession() != null && this.getSession().getState() != SessionState.WAITING_FOR_TOKEN;
    }

    public boolean addAvatar(GameAvatar avatar) {
        return getAvatars().addAvatar(avatar);
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

    private void initUid() {
        if (this.uid > 0) return;

        int nextUid = session.getAccount().getReservedPlayerUid();

        if (nextUid == GameConstants.SERVER_CONSOLE_UID) {
            nextUid = 0;
        }

        while (nextUid == 0 || LunarRail.getGameDatabase().checkIfObjectExists(Player.class, nextUid)) {
            nextUid = LunarRail.getGameDatabase().getNextObjectId(Player.class);
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
    
    public void onMove() {
        // Sanity
        if (this.getScene() == null) {
            return;
        }
        
        boolean anchorRange = false;
        
        // Check anchors. We can optimize this later.
        for (EntityProp anchor : this.getScene().getHealingSprings()) {
            long dist = getPos().getFast2dDist(anchor.getPos());
            if (dist > 25_000_000) continue; // 5000^2
            
            anchorRange = true;
            break;
        }
        
        // Only heal if player isnt already in anchor range
        if (anchorRange && anchorRange != this.inAnchorRange) {
            this.getCurrentLineup().heal(10000);
        }
        this.inAnchorRange = anchorRange;
    }
    
    public void moveTo(int entryId, Position pos) {
        this.entryId = entryId;
        this.moveTo(pos);
    }
    
    public void moveTo(Position pos) {
        this.getPos().set(pos);
        this.sendPacket(new PacketSceneEntityMoveScNotify(this));
    }
    
    public void enterScene(int entryId, int teleportId, boolean sendPacket) {
        // Get map entrance excel
        MapEntranceExcel entry = GameData.getMapEntranceExcelMap().get(entryId);
        if (entry == null) return;
        
        // Get floor info
        FloorInfo floor = GameData.getFloorInfo(entry.getPlaneID(), entry.getFloorID());
        if (floor == null) return;
        
        // Get teleport anchor info (contains position) from the entry id
        int startGroup = entry.getStartGroupID();
        int anchorId = entry.getStartAnchorID();
        if (teleportId != 0 || anchorId == 0) {
            PropInfo teleport = floor.getCachedTeleports().get(teleportId);
            if (teleport != null) {
                startGroup = teleport.getAnchorGroupID();
                anchorId = teleport.getAnchorID();
            }
        }
        
        AnchorInfo anchor = floor.getAnchorInfo(startGroup, anchorId);
        if (anchor == null) return;

        // Move player to scene
        this.loadScene(entry.getPlaneID(), entry.getFloorID(), entry.getId(), anchor.clonePos());
        
        // Send packet
        if (sendPacket) {
            this.sendPacket(new PacketEnterSceneByServerScNotify(this));
        }
    }

    private boolean loadScene(int planeId, int floorId, int entryId, Position pos) {
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

        // Set positions if player has logged in
        if (this.getSession().getState() != SessionState.WAITING_FOR_TOKEN) {
            this.getPos().set(pos);
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

    public void dropMessage(String message) {
        this.sendPacket(new PacketRevcMsgScNotify(this, message));
    }

    public void sendPacket(BasePacket packet) {
        if (this.hasLoggedIn()) {
            this.getSession().send(packet);
        }
    }

    public void save() {
        if (this.uid <= 0) {
            LunarRail.getLogger().error("Tried to save a player object without a uid!");
            return;
        }

        LunarRail.getGameDatabase().save(this);
    }

    public void onLogin() {
        // Load avatars and inventory first
        this.getAvatars().loadFromDatabase();
        this.getInventory().loadFromDatabase();

        // Load Etc
        this.getLineupManager().validate(this);
        this.getAvatars().setupHeroPaths();

        // Enter scene (should happen after everything else loads)
        this.loadScene(planeId, floorId, entryId, this.getPos());
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
