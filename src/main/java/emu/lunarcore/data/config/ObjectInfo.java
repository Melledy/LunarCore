package emu.lunarcore.data.config;

import emu.lunarcore.util.Position;
import lombok.Getter;

@Getter
public class ObjectInfo {
    public int ID;
    public float PosX;
    public float PosY;
    public float PosZ;
    public String Name;
    public float RotY;
    
    /*
     * Returns a new Position object
     */
    public Position clonePos() {
        return new Position((int) (this.PosX * 1000f), (int) (this.PosY * 1000f), (int) (this.PosZ * 1000f));
    }
}
