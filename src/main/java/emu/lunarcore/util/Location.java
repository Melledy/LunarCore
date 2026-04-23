package emu.lunarcore.util;

import emu.lunarcore.game.player.Player;
import lombok.Getter;

@Getter
public class Location {
    private int planeId;
    private int floorId;
    private int entryId;
    private Position pos;
    private Position rot;
    
    public Location() {
        this.pos = new Position();
        this.rot = new Position();
    }
    
    public Location(Player player) {
        this.planeId = player.getPlaneId();
        this.floorId = player.getFloorId();
        this.entryId = player.getEntryId();
        this.pos = player.getPos().clone();
        this.rot = player.getRot().clone();
    }
}
