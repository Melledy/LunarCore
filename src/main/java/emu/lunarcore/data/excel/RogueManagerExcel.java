package emu.lunarcore.data.excel;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import lombok.AccessLevel;
import lombok.Getter;

@Getter
@ResourceType(name = {"RogueManager.json"})
public class RogueManagerExcel extends GameResource {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private int ScheduleDataID;
    private int RogueSeason;

    @Getter(AccessLevel.NONE)
    private String BeginTime;
    @Getter(AccessLevel.NONE)
    private String EndTime;

    private int[] RogueAreaIDList;

    private transient long beginTime;
    private transient long endTime;

    @Override
    public int getId() {
        return ScheduleDataID;
    }

    @Override
    public void onLoad() {
        try {
            this.beginTime = LocalDateTime.from(formatter.parse(this.BeginTime))
                    .atOffset(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli();

            this.endTime = LocalDateTime.from(formatter.parse(this.EndTime))
                    .atOffset(ZoneOffset.UTC)
                    .toInstant()
                    .toEpochMilli();
        } catch (Exception e) {

        }
    }
}
