package emu.lunarcore.data.resource;

public abstract class GameResource extends ResourceBase implements Comparable<GameResource> {

    public abstract int getId();
    
    @Override
    public int compareTo(GameResource o) {
        return this.getId() - o.getId();
    }
    
}
