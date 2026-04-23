package emu.lunarcore.game.trial;

import emu.lunarcore.data.excel.AvatarDemoExcel;
import emu.lunarcore.game.battle.Battle;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.game.player.PlayerGameInstance;
import emu.lunarcore.game.scene.entity.EntityMonster;
import emu.lunarcore.proto.BattleEndStatusOuterClass.BattleEndStatus;
import emu.lunarcore.proto.BattleStatisticsOuterClass.BattleStatistics;
import emu.lunarcore.server.packet.send.PacketCurTrialActivityScNotify;
import emu.lunarcore.util.Location;
import lombok.Getter;

@Getter
public class TrialInstance extends PlayerGameInstance {
    private transient AvatarDemoExcel excel;
    
    private int stageId;
    private boolean finished;
    
    // Player's last location
    private Location lastLocation;
    
    public TrialInstance(Player player, AvatarDemoExcel excel) {
        this.setPlayer(player);
        this.excel = excel;
        this.stageId = excel.getId();
        
        // Save player's location
        this.lastLocation = new Location(player);
    }

    // Battle events
    
    @Override
    public void onBattleStart(Battle battle) {
        
    }

    @Override
    public void onBattleFinish(Battle battle, BattleEndStatus result, BattleStatistics stats) {
        // Check if all monsters are dead
        long monsters = getPlayer().getScene().getEntities().values().stream().filter(e -> e instanceof EntityMonster).count();
        
        if (monsters == 0) {
            this.finished = true;
        }
        
        // End trial TODO
        if (this.finished) {
            // Send player back to where they were
            getPlayer().getLineupManager().setCurrentExtraLineup(0, false);
            getPlayer().loadScene(this.getLastLocation(), true);
            // Reset instance
            getPlayer().setInstance(null);
        }
    }

    @Override
    public void onLeave() {
        // Update state
        this.finished = true;
        // Send trial status
        getPlayer().sendPacket(new PacketCurTrialActivityScNotify(this));
        // TODO clean up trial avatars when leaving
    }

}
