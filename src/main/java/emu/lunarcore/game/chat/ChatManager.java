package emu.lunarcore.game.chat;

import java.util.Collection;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.send.PacketRevcMsgScNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import lombok.AccessLevel;
import lombok.Getter;

public class ChatManager extends BasePlayerManager {
    @Getter(AccessLevel.PRIVATE)
    private final Int2ObjectMap<ObjectList<ChatMessage>> history;

    public ChatManager(Player player) {
        super(player);
        this.history = new Int2ObjectOpenHashMap<>();
    }
    
    public Collection<ChatMessage> getHistoryByUid(int uid) {
        return getHistory().get(uid);
    }
    
    public void addChatMessage(ChatMessage message) {
        // Get sender
        int targetPlayerUid = this.getPlayer().getUid() == message.getToUid() ? message.getFromUid() : message.getToUid();
        // Add to chat history
        if (GameConstants.MAX_CHAT_HISTORY > 0) {
            // Add to history list
            var list = getHistory().computeIfAbsent(targetPlayerUid, id -> new ObjectArrayList<>());
            list.add(message);
            // Check max size
            while (list.size() >= GameConstants.MAX_CHAT_HISTORY) {
                list.remove(0);
            }
        }
        // Send to client
        getPlayer().sendPacket(new PacketRevcMsgScNotify(message));
    }

    public void sendChat(int targetUid, String text) {
        // Sanity checks
        if (text == null || text.length() == 0) {
            return;
        }

        // Check if command
        if (text.charAt(0) == '!' || text.charAt(0) == '/') {
            LunarCore.getCommandManager().invoke(getPlayer(), text);
            return;
        }

        // Get target
        Player target = getPlayer().getServer().getOnlinePlayerByUid(targetUid);

        if (target == null) {
            return;
        }

        // Create chat packet
        ChatMessage message = new ChatMessage(this.getPlayer().getUid(), targetUid, text);
        
        // Send to both players
        this.addChatMessage(message);
        target.getChatManager().addChatMessage(message);
    }

    public void sendChat(int targetUid, int emote) {
        // Get target
        Player target = getPlayer().getServer().getOnlinePlayerByUid(targetUid);

        if (target == null) {
            return;
        }

        // Create chat packet
        ChatMessage message = new ChatMessage(this.getPlayer().getUid(), targetUid, emote);
        
        // Send to both players
        this.addChatMessage(message);
        target.getChatManager().addChatMessage(message);
    }
    
}
