package emu.lunarcore.data.excel;

import lombok.Getter;

import java.util.List;

@Getter
public class ActivitySchedulingExcel {
    private int activityId;
    private long beginTime;
    private long endTime;
    private int moduleId;
}
