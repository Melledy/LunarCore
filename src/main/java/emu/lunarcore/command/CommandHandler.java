package emu.lunarcore.command;

import emu.lunarcore.LunarCore;
import emu.lunarcore.game.player.Player;

public interface CommandHandler {
    
    public default Command getData() {
        return this.getClass().getAnnotation(Command.class);
    }
    
    public default String getLabel() {
        return getData().label();
    }
    
    public default void sendMessage(Player player, String message) {
        if (player != null) {
            player.sendMessage(message);
        } else {
            LunarCore.getLogger().info(message);
        }
    }
    
    public void execute(Player sender, CommandArgs args);
    
}
