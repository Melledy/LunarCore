package emu.lunarcore.data.config;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 *  Original name: LevelRogueDialogueEvent
 */
@Getter
public class RogueDialogueEventInfo {
    private List<RogueDialogueEvent> OnInitSequece = new ArrayList<>();
    private List<RogueDialogueEvent> OnStartSequece = new ArrayList<>();
    
    @Getter
    public static class RogueDialogueEvent {
        private List<TaskListInfo> TaskList = new ArrayList<>();
    }
    
    @Getter
    public static class TaskListInfo {
        @SerializedName("$type") public String Type = "";
        private List<OptionListInfo> OptionList = new ArrayList<>();
        private CustomStringInfo CustomString = new CustomStringInfo();
    }

    @Getter
    public static class OptionListInfo {
        private String TriggerCustomString;
        private int DialogueEventID;
    }

    @Getter
    public static class CustomStringInfo {
        private String Value;
    }
}
