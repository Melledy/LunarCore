package emu.lunarcore.data;

public abstract class GameResource implements Comparable<GameResource> {

    public abstract int getId();

    public void onLoad() {

    }
    
    public void onFinalize() {

    }

    @Override
    public int compareTo(GameResource o) {
        return this.getId() - o.getId();
    }
}
