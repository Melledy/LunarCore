package emu.lunarcore.game.rogue;

import lombok.Getter;

@Getter
public class RogueBonusData {
    private int id;
    private int eventId;
    
    public RogueBonusData(int id, int eventId) {
        this.id = id;
        this.eventId = eventId;
    }
}
