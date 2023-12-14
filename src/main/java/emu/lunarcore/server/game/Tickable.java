package emu.lunarcore.server.game;

public interface Tickable {

    public void onTick(long timestamp, long delta);

}
