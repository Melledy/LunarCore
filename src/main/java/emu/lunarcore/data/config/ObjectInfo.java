package emu.lunarcore.data.config;

import emu.lunarcore.util.Position;
import lombok.Getter;

/**
 *  Original name: LevelObjectInfo
 */
@Getter
public class ObjectInfo {
    public int ID;
    public float PosX;
    public float PosY;
    public float PosZ;
    public boolean IsDelete;
    public String Name;
    public float RotY;
    
    // Cached position and rotation to avoid recalculating positions all the time
    protected transient Position pos;
    protected transient Position rot;
    
    public Position getPos() {
        if (this.pos == null) {
            this.pos = new Position((int) (this.PosX * 1000f), (int) (this.PosY * 1000f), (int) (this.PosZ * 1000f));
        }
        return this.pos;
    }

    public Position getRot() {
        if (this.rot == null) {
            this.rot = new Position(0, (int) (this.RotY * 1000f), 0);
        }
        return this.rot;
    }
}
