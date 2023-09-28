package emu.lunarcore.game.player;

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
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.avatar.AvatarStorage;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.avatar.HeroPath;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.gacha.PlayerGachaInfo;
import emu.lunarcore.game.inventory.Inventory;
import emu.lunarcore.game.scene.Scene;
import emu.lunarcore.proto.PlayerBasicInfoOuterClass.PlayerBasicInfo;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.SessionState;
import emu.lunarcore.server.packet.send.PacketEnterSceneByServerScNotify;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.server.packet.send.PacketRevcMsgScNotify;
import emu.lunarcore.server.packet.send.PacketSetHeroBasicTypeScRsp;
import emu.lunarcore.util.Position;

import lombok.Getter;

@Entity(value = "players", useDiscriminator = false)
@Getter
public class Player {
    private transient GameSession session;

    @Id private int uid;
    @Indexed private String accountUid;
    private String name;
    private String signature;
    private PlayerGender gender;
    private int birthday;
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

    // Player managers
    private transient final AvatarStorage avatars;
    private transient final Inventory inventory;

    // Database persistent data
    private LineupManager lineupManager;
    private PlayerGachaInfo gachaInfo;

    @Deprecated // Morphia only
    public Player() {
        this.curBasicType = 8001;
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
        this.level = 1;
        this.stamina = GameConstants.MAX_STAMINA;

        this.pos = new Position(99, 62, -4800);
        this.planeId = 20001;
        this.floorId = 20001001;
        this.entryId = 2000101;

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
        this.getLineupManager().getCurrentLineup().getAvatars().add(avatar.getAvatarId());
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
    
    public boolean isInBattle() {
        return this.battle != null;
    }
    
    public void setBattle(Battle battle) {
        this.battle = battle;
    }
    
    public void enterScene(int entryId, int teleportId) {
        // Get map entrance excel
        MapEntranceExcel entry = GameData.getMapEntranceExcelMap().get(entryId);
        if (entry == null) return;
        
        // Get floor info
        FloorInfo floor = GameData.getFloorInfo(entry.getPlaneID(), entry.getFloorID());
        if (floor == null) return;
        
        // Get teleport anchor
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
        
        // Set position
        this.getPos().set(
                (int) (anchor.getPosX() * 1000f), 
                (int) (anchor.getPosY() * 1000f), 
                (int) (anchor.getPosZ() * 1000f)
        );
        this.planeId = entry.getPlaneID();
        this.floorId = entry.getFloorID();
        this.entryId = entry.getId();
        
        // Save player
        this.save();

        // Move to scene
        loadScene(entry.getPlaneID(), entry.getFloorID(), entry.getId());
    }

    private void loadScene(int planeId, int floorId, int entryId) {
        // Sanity check
        if (this.scene != null && this.scene.getPlaneId() == planeId) {
            // Don't create a new scene if were already in the one we want to teleport to
        } else {
            this.scene = new Scene(this, planeId, floorId, entryId);
        }

        // TODO send packet
        if (this.getSession().getState() != SessionState.WAITING_FOR_TOKEN) {
            this.sendPacket(new PacketEnterSceneByServerScNotify(this));
        }
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
        this.loadScene(planeId, floorId, entryId);
    }

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
}
