package emu.lunarcore.game.challenge;

import java.util.stream.Stream;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerLineup;
import emu.lunarcore.proto.ExtraLineupTypeOuterClass.ExtraLineupType;
import emu.lunarcore.server.packet.send.PacketStartChallengeScRsp;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

@Getter
public class ChallengeManager extends BasePlayerManager {
    private Int2ObjectMap<ChallengeHistory> history;
    
    public ChallengeManager(Player player) {
        super(player);
        this.history = new Int2ObjectOpenHashMap<>();
    }
    
    public void startChallenge(int challengeId) {
        // Get challenge excel
        ChallengeExcel excel = GameData.getChallengeExcelMap().get(challengeId);
        if (excel == null) {
            getPlayer().sendPacket(new PacketStartChallengeScRsp());
            return;
        }
        
        // Sanity check lineups
        if (excel.getStageNum() >= 1) {
            // Get lineup
            PlayerLineup lineup = getPlayer().getLineupManager().getLineupByIndex(0, ExtraLineupType.LINEUP_CHALLENGE_VALUE);
            // Make sure this lineup has avatars set
            if (lineup.getAvatars().size() == 0) return;
            // Reset hp/sp
            lineup.forEachAvatar(avatar -> {
                avatar.setCurrentHp(lineup, 10000);
                avatar.setCurrentSp(lineup, avatar.getMaxSp() / 2);
            });
            // Set technique points
            lineup.setMp(5);
        }
        if (excel.getStageNum() >= 2) {
            PlayerLineup lineup = getPlayer().getLineupManager().getLineupByIndex(0, ExtraLineupType.LINEUP_CHALLENGE_2_VALUE);
            // Make sure this lineup has avatars set
            if (lineup.getAvatars().size() == 0) return;
            // Reset hp/sp
            lineup.forEachAvatar(avatar -> {
                avatar.setCurrentHp(lineup, 10000);
                avatar.setCurrentSp(lineup, avatar.getMaxSp() / 2);
            });
            // Set technique points
            lineup.setMp(5);
        }
        
        // Set first lineup before we enter scenes
        getPlayer().getLineupManager().setCurrentExtraLineup(ExtraLineupType.LINEUP_CHALLENGE_VALUE, false);
        
        // Enter scene
        boolean success = getPlayer().enterScene(excel.getMapEntranceID(), 0, false);
        if (success == false) {
            return;
        }
        
        // Set challenge data for player
        ChallengeData data = new ChallengeData(getPlayer(), excel);
        getPlayer().setChallengeData(data);

        // Send packet
        getPlayer().sendPacket(new PacketStartChallengeScRsp(getPlayer(), challengeId));
    }
    
    public void addHistory(int challengeId, int stars) {
        // Get history info
        var info = this.getHistory().computeIfAbsent(challengeId, id -> new ChallengeHistory(getPlayer(), id));

        // Set
        info.setStars(stars);
        info.save();
    }
    
    public void loadFromDatabase() {
        Stream<ChallengeHistory> stream = LunarCore.getGameDatabase().getObjects(ChallengeHistory.class, "ownerUid", this.getPlayer().getUid());

        stream.forEach(info -> {
            this.getHistory().put(info.getChallengeId(), info);
        });
    }
}
