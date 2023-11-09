package emu.lunarcore.data.config;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * The equivalent of the TaskConfig class in anime game.
 */
@Getter
public class TaskInfo {
    @Getter(AccessLevel.NONE)
    private String $type;
    
    @SerializedName(value = "ID", alternate = {"SummonUnitID"})
    private int ID;
    
    private DynamicFloat LifeTime;
    
    private List<TaskInfo> OnAttack;
    private List<TaskInfo> SuccessTaskList;
    
    public String getType() {
        return this.$type;
    }
    
    public int getLifeTime() {
        if (this.LifeTime == null) {
            return 20; // TODO change
        }
        
        return (int) this.LifeTime.getValue();
    }
}
