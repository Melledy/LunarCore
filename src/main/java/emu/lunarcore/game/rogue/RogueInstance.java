package emu.lunarcore.game.rogue;

import java.util.*;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.config.AnchorInfo;
import emu.lunarcore.data.excel.RogueAeonExcel;
import emu.lunarcore.data.excel.RogueAreaExcel;
import emu.lunarcore.data.excel.RogueMapExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.enums.RogueBuffAeonType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.lineup.PlayerLineup;
import emu.lunarcore.proto.AvatarTypeOuterClass.AvatarType;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.proto.HandleRogueCommonPendingActionScRspOuterClass.HandleRogueCommonPendingActionScRsp;
import emu.lunarcore.proto.RogueAeonOuterClass.RogueAeon;
import emu.lunarcore.proto.RogueAvatarInfoOuterClass.RogueAvatarInfo;
import emu.lunarcore.proto.RogueBuffInfoOuterClass.RogueBuffInfo;
import emu.lunarcore.proto.RogueBuffSourceOuterClass.RogueBuffSource;
import emu.lunarcore.proto.RogueCommonPendingActionOuterClass.RogueCommonPendingAction;
import emu.lunarcore.proto.RogueCurrentInfoOuterClass.RogueCurrentInfo;
import emu.lunarcore.proto.RogueFinishInfoOuterClass.RogueFinishInfo;
import emu.lunarcore.proto.RogueMapInfoOuterClass.RogueMapInfo;
import emu.lunarcore.proto.RogueMiracleInfoOuterClass.RogueMiracleInfo;
import emu.lunarcore.proto.RogueRecordAvatarOuterClass.RogueRecordAvatar;
import emu.lunarcore.proto.RogueRecordInfoOuterClass.RogueRecordInfo;
import emu.lunarcore.proto.RogueRoomStatusOuterClass.RogueRoomStatus;
import emu.lunarcore.proto.RogueStatusOuterClass.RogueStatus;
import emu.lunarcore.proto.RogueVirtualItemOuterClass.RogueVirtualItem;
import emu.lunarcore.server.packet.send.*;
import emu.lunarcore.util.Utils;
import lombok.Getter;
import us.hebi.quickbuf.RepeatedInt;

@Getter
public class RogueInstance {
    private transient Player player;
    private transient RogueAreaExcel excel;
    private transient RogueEventManager eventManager;
    
    private int areaId;
    private int currentRoomProgress;
    private int currentSiteId;
    private int startSiteId;
    private TreeMap<Integer, RogueRoomData> rooms;
    
    private Set<Integer> baseAvatarIds;
    private Map<Integer, RogueBuffData> buffs;
    private Map<Integer, RogueMiracleData> miracles;
    
    private int pendingBuffSelects;
    private RogueBuffSelectMenu buffSelect;
    private int pendingMiracleSelects;
    private RogueMiracleSelectMenu miracleSelect;
    private int pendingBonusSelects;
    private RogueBonusSelectMenu bonusSelect;
    private RogueCommonPendingAction pendingAction;
    
    private int baseRerolls;
    private int aeonId;
    private int aeonBuffType;
    private int maxAeonBuffs;
    private int money;  // universal debris
    private int id = 2;  // idk what this is for, but it's needed for the packet
    
    private int roomScore;
    private int earnedTalentCoin;
    private boolean isWin;
    
    @Deprecated // Morphia only!
    public RogueInstance() {}
    
    public RogueInstance(Player player, RogueAreaExcel excel, RogueAeonExcel aeonExcel) {
        this.player = player;
        this.excel = excel;
        this.areaId = excel.getRogueAreaID();
        this.currentRoomProgress = 0;
        this.baseAvatarIds = new HashSet<>();
        this.buffs = new HashMap<>();
        this.miracles = new HashMap<>();
        this.maxAeonBuffs = 4;
        this.money = 100;
        this.eventManager = new RogueEventManager(this);
        
        if (aeonExcel != null) {
            this.aeonId = aeonExcel.getAeonID();
            this.aeonBuffType = aeonExcel.getRogueBuffType();
        }
        
        this.initRooms();
        this.initTalents();
    }
    
    public RogueStatus getStatus() {
        return RogueStatus.ROGUE_STATUS_DOING;
    }
    
    private void initRooms() {
        if (this.rooms != null) return;
        
        this.rooms = new TreeMap<>();
        
        for (var mapExcel : this.getExcel().getSites()) {
            var roomData = new RogueRoomData(mapExcel);
            this.rooms.put(roomData.getSiteId(), roomData);
            
            if (mapExcel.isIsStart()) {
                this.startSiteId = roomData.getSiteId();
            }
        }
    }
    
    private void initTalents() {
        // Reset blessings
        if (player.getRogueManager().hasTalent(11)) {
            this.baseRerolls = 1;
        }
        // Extra blessings
        if (player.getRogueManager().hasTalent(21)) {
            this.pendingBuffSelects += 1;
        }
    }
    
    private RogueRoomData getRoomBySiteId(int siteId) {
        return this.rooms.get(siteId);
    }
    
    public RogueRoomData getCurrentRoom() {
        return this.getRoomBySiteId(this.getCurrentSiteId());
    }
    
    private boolean shouldAddAeonBuff() {
        int pathBuffs = 0; // Buffs on the current path
        int aeonBuffs = 0;
        
        for (var b : this.getBuffs().values()) {
            var excel = b.getExcel();
            if (excel == null) continue;
            
            if (excel.getRogueBuffType() == this.getAeonBuffType()) {
                if (excel.isAeonBuff()) {
                    aeonBuffs++;
                } else {
                    pathBuffs++;
                }
            }
        }
        
        // Skip if we are already at max aeon buffs
        if (aeonBuffs >= this.maxAeonBuffs) {
            return false;
        }
        
        switch (aeonBuffs) {
            case 0:
                return pathBuffs >= 3;
            case 1:
                return pathBuffs >= 6;
            case 2:
                return pathBuffs >= 10;
            case 3:
                return pathBuffs >= 14;
            default:
                return false;
        }
    }
    
    public synchronized void createBuffSelect(int amount) {
        this.pendingBuffSelects += amount;
        
        RogueBuffSelectMenu buffSelect = this.updateBuffSelect();
//        if (buffSelect != null) {
//            getPlayer().sendPacket(new PacketSyncRogueBuffSelectInfoScNotify(buffSelect));
//        }
    }
    
    public synchronized RogueBuffSelectMenu updateBuffSelect() {
        if (this.getBuffSelect() == null) {
            // Creates a new blessing selection menu if we have any pending buff selects
            if (this.pendingBuffSelects > 0) {
                // Regular blessing selection with 3 random blessings
                this.buffSelect = new RogueBuffSelectMenu(this, false);
                this.pendingBuffSelects--;
            } else if (this.getAeonId() != 0) {
                // Check if we should add aeon blessings
                if (shouldAddAeonBuff()) {
                    this.buffSelect = new RogueBuffSelectMenu(this, true);
                }
            }
            
            if (this.getBuffSelect() != null) {
                var proto = new PacketSyncRogueCommonPendingActionScNotify(this.buffSelect.toProto(), this.id);
                this.id += 2;
                this.pendingAction = proto.toProto();
                this.getPlayer().sendPacket(proto);
            }
            
            return this.buffSelect;
        }
        
        return null;
    }
    
    public synchronized RogueBuffSelectMenu rollBuffSelect() {
        if (getBuffSelect() != null && getBuffSelect().hasRerolls() && money >= 30) {
            this.getBuffSelect().reroll();
            this.setMoney(money - 30);
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.getBuffSelect().toProto(), this.id - 2));
            
            var proto = new PacketSyncRogueCommonPendingActionScNotify(this.buffSelect.toProto(), this.id);
            this.id += 2;
            pendingAction = proto.toProto();
            return this.getBuffSelect();
        } else {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
        }
        
        return null;
    }
    
    public synchronized RogueBuffData selectBuff(int buffId) {
        // Sanity
        if (this.getBuffSelect() == null) {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
            return null;
        }
        
        // Validate buff from buff select menu
        RogueBuffData buff = this.getBuffSelect().getBuffs()
                .stream()
                .filter(b -> b.getId() == buffId)
                .findFirst()
                .orElse(null);
        
        if (buff == null) {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
            return null;
        }
        
        // Add buff
        this.buffSelect = null;
        this.pendingAction = null;
        this.getBuffs().put(buff.getId(), buff);
        //getPlayer().sendPacket(new PacketAddRogueBuffScNotify(buff, RogueBuffSource.ROGUE_BUFF_SOURCE_TYPE_SELECT));
        this.updateBuffSelect();
        
        this.getPlayer().sendPacket(new PacketSyncRogueCommonActionResultScNotify(RogueBuffSource.ROGUE_BUFF_SOURCE_TYPE_SELECT, buff.toDataProto()));
        
        var data = HandleRogueCommonPendingActionScRsp.newInstance();
        data.getMutableRogueBuffSelect();
        data.setTimes(this.id - 2);
        this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(data));
        return buff;
    }
    
    public synchronized void createMiracleSelect(int amount) {
        this.pendingMiracleSelects += amount;
        
        RogueMiracleSelectMenu miracleSelect = this.updateMiracleSelect();
//        if (miracleSelect != null) {
//            getPlayer().sendPacket(new PacketSyncRogueMiracleSelectInfoScNotify(miracleSelect));
//        }
    }
    
    public synchronized RogueMiracleSelectMenu updateMiracleSelect() {
        if (this.pendingMiracleSelects > 0 && this.getMiracleSelect() == null) {
            this.miracleSelect = new RogueMiracleSelectMenu(this);
            this.pendingMiracleSelects--;
            var proto = new PacketSyncRogueCommonPendingActionScNotify(this.miracleSelect.toProto(), this.id);
            this.id += 2;
            this.pendingAction = proto.toProto();
            this.getPlayer().sendPacket(proto);
            return this.miracleSelect;
        }
        
        return null;
    }
    
    public synchronized RogueMiracleData selectMiracle(int miracleId) {
        if (this.getMiracleSelect() == null) {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
            return null;
        }
        
        RogueMiracleData miracle = this.getMiracleSelect().getMiracles()
                .stream()
                .filter(b -> b.getId() == miracleId)
                .findFirst()
                .orElse(null);
        
        if (miracle == null) {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
            return null;
        }
        
        this.miracleSelect = null;
        this.pendingAction = null;
        this.getMiracles().put(miracle.getId(), miracle);
        //getPlayer().sendPacket(new PacketAddRogueMiracleScNotify(miracle, RogueMiracleSource.ROGUE_MIRACLE_SOURCE_TYPE_SELECT));
        this.updateMiracleSelect();
        this.getPlayer().sendPacket(new PacketSyncRogueCommonActionResultScNotify(RogueBuffSource.ROGUE_BUFF_SOURCE_TYPE_SELECT, miracle.toDataProto()));
        
        var data = HandleRogueCommonPendingActionScRsp.newInstance();
        data.getMutableMiracleSelect();
        data.setTimes(this.id - 2);
        this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(data));
        return miracle;
    }
    
    public synchronized void createBonusSelect(int amount) {
        this.pendingBonusSelects += amount;

        RogueBonusSelectMenu bonusSelect = this.updateBonusSelect();
    }
    
    public synchronized RogueBonusSelectMenu updateBonusSelect() {
        if (this.pendingBonusSelects > 0 && this.getBonusSelect() == null) {
            this.bonusSelect = new RogueBonusSelectMenu(this);
            this.pendingBonusSelects--;
            var proto = new PacketSyncRogueCommonPendingActionScNotify(this.bonusSelect.toProto(), this.id);
            this.id += 2;
            this.pendingAction = proto.toProto();
            this.getPlayer().sendPacket(proto);
            return this.bonusSelect;
        }
        
        return null;
    }
    
    public synchronized RogueBonusData selectBonus(int bonusId) {
        if (this.getBonusSelect() == null)  {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
            return null;
        }
        
        RogueBonusData bonus = this.getBonusSelect().getBonuses()
                .stream()
                .filter(b -> b.getId() == bonusId)
                .findFirst()
                .orElse(null);
        
        if (bonus == null)  {
            this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(this.id - 2));
            return null;
        }
        
        this.bonusSelect = null;
        this.pendingAction = null;
        // every bonus is an event
        // TODO: add event
        var data = HandleRogueCommonPendingActionScRsp.newInstance();
        data.getMutableBonusSelect();
        data.setTimes(this.id - 2);
        this.getPlayer().sendPacket(new PacketHandleRogueCommonPendingActionScRsp(data));
        this.onSelectDialogue(bonus.getEventId());
        return bonus;
    }
    
    public synchronized void setMoney(int money) {
        this.money = money;
        getPlayer().sendPacket(new PacketSyncRogueVirtualItemInfoScNotify(this.getPlayer()));
    }
    
    public synchronized void pickAvatar(RepeatedInt avatarId) {
        var newAvatarIds = new HashSet<Integer>();
        for (int id : avatarId) {
            this.baseAvatarIds.add(id);
            newAvatarIds.add(id);
        }
        this.getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_ROGUE_VALUE).getAvatars().addAll(newAvatarIds);  // TODO: check if this is correct
        this.getPlayer().sendPacket(new PacketPickRogueAvatarScRsp(newAvatarIds));
    }
    
    public synchronized RogueRoomData enterRoom(int siteId) {
        // Set status on previous room
        RogueRoomData prevRoom = this.getCurrentRoom();
        if (prevRoom != null) {
            // Make sure the site we want to go into is connected to the current room we are in
            if (!Utils.arrayContains(prevRoom.getNextSiteIds(), siteId)) {
                return null;
            }
            // Update status
            prevRoom.setStatus(RogueRoomStatus.ROGUE_ROOM_STATUS_FINISH);
        }
        
        // Get next room
        RogueRoomData nextRoom = this.getRoomBySiteId(siteId);
        if (nextRoom == null) return null;
        
        // Enter room
        this.currentRoomProgress++;
        this.currentSiteId = nextRoom.getSiteId();
        nextRoom.setStatus(RogueRoomStatus.ROGUE_ROOM_STATUS_PLAY);
        
        // Enter scene
        boolean success = getPlayer().enterScene(nextRoom.getRoomExcel().getMapEntrance(), 0, false);
        if (!success) return null;
        
        // Move player to rogue start position
        AnchorInfo anchor = getPlayer().getScene().getFloorInfo().getAnchorInfo(nextRoom.getExcel().getGroupID(), 1);
        if (anchor != null) {
            getPlayer().getPos().set(anchor.getPos());
            getPlayer().getRot().set(anchor.getRot());
        }
        
        // Send packet if we are not entering the rogue instance for the first time
        if (prevRoom != null) {
            getPlayer().sendPacket(new PacketSyncRogueMapRoomScNotify(this, prevRoom));
            getPlayer().sendPacket(new PacketSyncRogueMapRoomScNotify(this, nextRoom));
        }
        
        return nextRoom;
    }
    
    public void onFinish() {
        // Calculate completed rooms
        int completedRooms = Math.max(this.currentRoomProgress - (this.isWin() ? 0 : 1), 0);
        
        // Calculate score and talent point rewards
        this.roomScore = this.getExcel().getScoreMap().get(completedRooms);
        this.earnedTalentCoin = this.roomScore / 10;
        
        // Add coins to player
        if (this.earnedTalentCoin > 0) {
            this.getPlayer().addTalentPoints(this.earnedTalentCoin);
            this.getPlayer().save();
        }
    }
    
    // Dialogue stuff
    
    public void onSelectDialogue(int dialogueEventId) {
        this.eventManager.handleEvent(dialogueEventId);
    }
    
    // Battle
    
    public synchronized void onBattleStart(Battle battle) {
        // Add rogue blessings as battle buffs
        for (var buff : this.getBuffs().values()) {
            // Convert blessing to battle buff
            battle.addBuff(buff.toMazeBuff());
            // Set battle buff energy to max
            if (buff.getExcel().getBattleEventBuffType() == RogueBuffAeonType.BattleEventBuff) {
                RogueBuffType type = RogueBuffType.getById(getAeonBuffType());
                if (type != null && type.getBattleEventSkill() != 0) {
                    battle.getBattleEvents().add(type.getBattleEventSkill());
                }
            }
        }
        // Set monster level for battle
        RogueMapExcel mapExcel = GameData.getRogueMapExcel(this.getExcel().getMapId(), this.getCurrentSiteId());
        if (mapExcel != null && mapExcel.getLevelList() != null && mapExcel.getLevelList().length >= 1) {
            battle.setCustomLevel(mapExcel.getLevelList()[0]);
        }
    }
    
    public synchronized void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats) {
        if (result == BattleEndStatus.BATTLE_END_WIN) {
            int roomType = this.getCurrentRoom().getExcel().getRogueRoomType();
            if (roomType == RogueRoomType.BOSS.getVal()) {
                // Final boss
                this.isWin = true;
            } else {
                // Give blessings to player
                int amount = battle.getNpcMonsters().size();
                this.createBuffSelect(amount);
            }
        }
    }
    
    // Database
    
    public void onLoad(Player player) {
        this.player = player;
        this.excel = GameData.getRogueAreaExcelMap().get(areaId);
        
        if (this.getBuffSelect() != null) {
            this.getBuffSelect().onLoad(this);
        }
        if (this.getMiracleSelect() != null) {
            this.getMiracleSelect().onLoad(this);
        }
    }
    
    // Serialization

    public RogueCurrentInfo toProto() {
        var proto = RogueCurrentInfo.newInstance()
                .setStatus(this.getStatus())
                .setRogueAvatarInfo(this.toAvatarInfoProto())
                .setRoomMap(this.toMapInfoProto())
                .setRogueBuffInfo(this.toBuffInfoProto())
                .setRogueMiracleInfo(this.toMiracleInfoProto())
                .setRogueAeon(this.toAeonProto())
                .setRogueVirtualItem(this.toVirtualItemProto());
        
        if (pendingAction != null) {
            proto.setPendingAction(pendingAction);
        }
        return proto;
    }
    
    public RogueAvatarInfo toAvatarInfoProto() {
        var proto = RogueAvatarInfo.newInstance();
        
        for (int id : this.getBaseAvatarIds()) {
            proto.addBaseAvatarIdList(id);
        }
        
        return proto;
    }
    
    public RogueMapInfo toMapInfoProto() {
        var room = this.getCurrentRoom();

        var proto = RogueMapInfo.newInstance()
                .setAreaId(this.getExcel().getId())
                .setMapId(this.getExcel().getMapId())
                .setCurSiteId(room.getSiteId())
                .setCurRoomId(room.getRoomId());
        
        for (var roomData : this.getRooms().values()) {
            proto.addRoomList(roomData.toProto());
        }
        
        return proto;
    }
    
    public RogueBuffInfo toBuffInfoProto() {
        var proto = RogueBuffInfo.newInstance();
        
        for (var buff : this.getBuffs().values()) {
            proto.addMazeBuffList(buff.toProto());
        }
        
        return proto;
    }
    
    public RogueMiracleInfo toMiracleInfoProto() {
        var proto = RogueMiracleInfo.newInstance();
        
        // Set flag for this so it gets serialized
        proto.getMutableRogueMiracleInfo();
        
        for (var miracle : this.getMiracles().values()) {
            proto.getMutableRogueMiracleInfo().addRogueMiracleList(miracle.toInfoProto());
        }
        
        return proto;
    }
    
    public RogueAeon toAeonProto() {
        var proto = RogueAeon.newInstance()
                .setOFBCBEIEAEC(this.getAeonId());
        
        return proto;
    }
    
    public RogueVirtualItem toVirtualItemProto() {
        var proto = RogueVirtualItem.newInstance()
                .setMoney(this.getMoney());
        
        return proto;
    }

    public RogueFinishInfo toFinishInfoProto() {
        // Rogue record info
        var recordInfo = RogueRecordInfo.newInstance();
        
        for (var buff : this.getBuffs().values()) {
            recordInfo.addBuffList(buff.toProto());
        }
        
        for (var miracle : this.getMiracles().values()) {
            recordInfo.addRogueMiracleList(miracle.getId());
        }
        
        PlayerLineup lineup = getPlayer().getLineupManager().getExtraLineupByType(ExtraLineupType.LINEUP_ROGUE_VALUE);
        if (lineup != null) {
            for (int i = 0; i < lineup.getAvatars().size(); i++) {
                var recordAvatar = RogueRecordAvatar.newInstance()
                        .setId(lineup.getAvatars().get(i))
                        .setSlot(i)
                        .setAvatarType(AvatarType.AVATAR_FORMAL_TYPE);
                
                recordInfo.addAvatarList(recordAvatar);
            }
        }
        
        // Create rogue finish info
        var proto = RogueFinishInfo.newInstance()
                .setTotalScore(this.getRoomScore())
                //.setTalentCoin(this.getEarnedTalentCoin())
                .setAreaId(this.getAreaId())
                .setIsWin(this.isWin())
                .setPassRoomCount(this.getCurrentSiteId())
                .setReachRoomCount(this.getCurrentRoomProgress())
                .setRecordInfo(recordInfo);
        
        return proto;
    }
    
}
