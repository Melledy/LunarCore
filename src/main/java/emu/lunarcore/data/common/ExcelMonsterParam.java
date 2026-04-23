package emu.lunarcore.data.common;

import lombok.Getter;

@Getter
public class ExcelMonsterParam {
    private int configId;
    private int npcMonsterId;
    private int eventId;

    public ExcelMonsterParam(int configId, int npcMonsterId, int eventId) {
        this.configId = configId;
        this.npcMonsterId = npcMonsterId;
        this.eventId = eventId;
    }
}
