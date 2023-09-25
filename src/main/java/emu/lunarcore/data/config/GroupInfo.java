package emu.lunarcore.data.config;

import java.util.List;

import lombok.Getter;

@Getter
public class GroupInfo {
    private transient int id;
    private GroupLoadSide LoadSide;
    private boolean LoadOnInitial;
    
    private List<AnchorInfo> AnchorList;
    private List<MonsterInfo> MonsterList;
    private List<PropInfo> PropList;
    
    public void setId(int id) {
        if (this.id == 0) this.id = id;
    }
    
    public static enum GroupLoadSide {
        Client, Server;
    }
}
