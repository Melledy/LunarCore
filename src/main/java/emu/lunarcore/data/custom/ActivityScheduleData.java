package emu.lunarcore.data.custom;

import lombok.Getter;

@Getter
public class ActivityScheduleData {
    private int activityId;
    private long beginTime;
    private long endTime;
    private int moduleId;
}
