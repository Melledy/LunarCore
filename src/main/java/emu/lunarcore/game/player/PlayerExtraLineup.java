package emu.lunarcore.game.player;

import emu.lunarcore.GameConstants;
import emu.lunarcore.server.packet.send.PacketSyncLineupNotify;

public class PlayerExtraLineup extends PlayerLineup {
    private int extraLineupType;
    private int mp;
    
    @Deprecated // Morphia only!
    public PlayerExtraLineup() {

    }
    
    public PlayerExtraLineup(Player player, int extraLineupType) {
        super(player, 0);
        this.extraLineupType = extraLineupType;
    }

    @Override
    public boolean isExtraLineup() {
        return true;
    }
    
    @Override
    public int getExtraLineupType() {
        return extraLineupType;
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
