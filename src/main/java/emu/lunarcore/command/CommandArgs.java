package emu.lunarcore.command;

import java.util.List;

import emu.lunarcore.LunarRail;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
public class CommandArgs {
    private List<String> list;
    private Player target;
    
    private int targetUid;
    private int amount;
    private int level = -1;
    private int rank = -1;
    private int promotion = -1;
    private int stage = -1;
    
    private static String EMPTY_STRING = "";

    public CommandArgs(Player sender, List<String> args) {
        this.list = args;
        
        // Parse args. Maybe regex is better.
        var it = this.list.iterator();
        while (it.hasNext()) {
            // Lower case first
            String arg = it.next().toLowerCase();
            
            try {
                if (arg.length() >= 2 && !Character.isDigit(arg.charAt(0)) && Character.isDigit(arg.charAt(arg.length() - 1))) {
                    if (arg.startsWith("@")) {
                        this.targetUid = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("x")) {
                        this.amount = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("lv")) {
                        this.level = Utils.parseSafeInt(arg.substring(2));
                        it.remove();
                    } else if (arg.startsWith("r")) {
                        this.rank = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("p")) {
                        this.promotion = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("s")) {
                        this.stage = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    }
                }
            } catch (Exception e) {
                
            }
        }
        
        // Get target player
        if (targetUid != 0) {
            if (LunarRail.getGameServer() != null) {
                target = LunarRail.getGameServer().getOnlinePlayerByUid(targetUid);
            }
        } else {
            target = sender;
        }
        
        if (target != null) {
            this.targetUid = target.getUid();
        }
    }
    
    public int size() {
        return this.list.size();
    }
    
    public String get(int index) {
        if (index < 0 || index >= list.size()) {
            return EMPTY_STRING;
        }
        return this.list.get(index);
    }
}
