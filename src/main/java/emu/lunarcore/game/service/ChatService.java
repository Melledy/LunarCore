package emu.lunarcore.game.service;

import emu.lunarcore.commands.PlayerCommands;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;

public class ChatService extends BaseGameService {

    public ChatService(GameServer server) {
        super(server);
    }

    public void sendPrivChat(Player player, int targetUid, String message) {
        // Sanity checks
        if (message == null || message.length() == 0) {
            return;
        }

        // Check if command
        if (message.charAt(0) == '!') {
            PlayerCommands.handle(player, message);
            return;
        }

        // Get target
        Player target = getServer().getPlayerByUid(targetUid);

        if (target == null) {
            return;
        }

        // Create chat packet TODO
    }

    public void sendPrivChat(Player player, int targetUid, int emote) {
        // Get target
        Player target = getServer().getPlayerByUid(targetUid);

        if (target == null) {
            return;
        }

        // Create chat packet TODO
    }
}
