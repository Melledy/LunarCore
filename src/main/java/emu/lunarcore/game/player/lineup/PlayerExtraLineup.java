package emu.lunarcore.game.player.lineup;

import dev.morphia.annotations.Entity;
import emu.lunarcore.GameConstants;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;

@Entity(value = "lineupsExtra", useDiscriminator = false)
public class PlayerExtraLineup extends PlayerLineup {
    private int mp;
    
    @Deprecated // Morphia only!
    public PlayerExtraLineup() {}
    
    public PlayerExtraLineup(Player player, int extraLineupType) {
        super(player, extraLineupType);
    }

    @Override
    public boolean isExtraLineup() {
        return true;
    }
    
    @Override
    public int getIndex() {
        return 0;
    }
    
    @Override
    public int getExtraLineupType() {
        return this.index;
    }
    
    @Override
    public void addMp(int i) {
        this.mp = Math.min(this.mp + i, GameConstants.MAX_MP);
        this.getOwner().sendPacket(new PacketSyncLineupNotify(this.getOwner().getCurrentLineup()));
    }
    
    @Override
    public void setMp(int i) {
        this.mp = i;
    }
    
    @Override
    public void removeMp(int i) {
        this.mp = Math.max(this.mp - i, 0);
    }
    
    @Override
    public int getMp() {
        return this.mp;
    }
}
