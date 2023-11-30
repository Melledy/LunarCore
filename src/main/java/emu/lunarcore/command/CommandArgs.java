package emu.lunarcore.command;

import java.util.List;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.inventory.GameItem;
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
                    if (arg.startsWith("@")) { // Target UID
                        this.targetUid = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("x")) { // Amount
                        this.amount = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("lv")) { // Level
                        this.level = Utils.parseSafeInt(arg.substring(2));
                        it.remove();
                    } else if (arg.startsWith("r")) { // Rank
                        this.rank = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("e")) { // Eidolons
                        this.rank = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("p")) { // Promotion
                        this.promotion = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    } else if (arg.startsWith("s")) { // Stage or Superimposition
                        this.stage = Utils.parseSafeInt(arg.substring(1));
                        it.remove();
                    }
                }
            } catch (Exception e) {
                
            }
        }
        
        // Get target player
        if (targetUid != 0) {
            if (LunarCore.getGameServer() != null) {
                target = LunarCore.getGameServer().getOnlinePlayerByUid(targetUid);
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
    
    // Utility commands
    
    /**
     * Changes the properties of an avatar based on the arguments provided
     * @param avatar The targeted avatar to change
     * @return A boolean of whether or not any changes were made to the avatar
     */
    public boolean setProperties(GameAvatar avatar) {
        boolean hasChanged = false;

        // Try to set level
        if (this.getLevel() > 0) {
            avatar.setLevel(Math.min(this.getLevel(), 80));
            avatar.setPromotion(Utils.getMinPromotionForLevel(avatar.getLevel()));
            hasChanged = true;
        }
        
        // Try to set promotion (ascension level)
        if (this.getPromotion() >= 0) {
            avatar.setPromotion(Math.min(this.getPromotion(), avatar.getExcel().getMaxPromotion()));
            hasChanged = true;
        }
        
        // Try to set rank (eidolons)
        if (this.getRank() >= 0) {
            avatar.setRank(Math.min(this.getRank(), avatar.getExcel().getMaxRank()));
            hasChanged = true;
        }
        
        // Try to set skill trees
        if (this.getStage() > 0) {
            for (int pointId : avatar.getExcel().getSkillTreeIds()) {
                var skillTree = GameData.getAvatarSkillTreeExcel(pointId, 1);
                if (skillTree == null) continue;
                
                int minLevel = skillTree.isDefaultUnlock() ? 1 : 0;
                int pointLevel = Math.max(Math.min(this.getStage(), skillTree.getMaxLevel()), minLevel);
                
                avatar.getSkills().put(pointId, pointLevel);
            }
            hasChanged = true;
        }
        
        return hasChanged;
    }
    
    /**
     * Changes the properties of an item based on the arguments provided
     * @param item The targeted item to change
     * @return A boolean of whether or not any changes were made to the item
     */
    public boolean setProperties(GameItem item) {
        boolean hasChanged = false;
        
        if (item.getExcel().isEquipment()) {
            // Try to set level
            if (this.getLevel() > 0) {
                item.setLevel(Math.min(this.getLevel(), 80));
                item.setPromotion(Utils.getMinPromotionForLevel(item.getLevel()));
                hasChanged = true;
            }
            
            // Try to set promotion
            if (this.getPromotion() >= 0) {
                item.setPromotion(Math.min(this.getPromotion(), item.getExcel().getEquipmentExcel().getMaxPromotion()));
                hasChanged = true;
            }
            
            // Try to set rank (superimposition)
            if (this.getRank() >= 0) {
                item.setRank(Math.min(this.getRank(), item.getExcel().getEquipmentExcel().getMaxRank()));
                hasChanged = true;
            } else if (this.getStage() >= 0) {
                item.setRank(Math.min(this.getStage(), item.getExcel().getEquipmentExcel().getMaxRank()));
                hasChanged = true;
            }
        } else if (item.getExcel().isRelic()) {
            // Try to set level
            if (this.getLevel() > 0) {
                int oldLevel = item.getLevel();
                int upgrades = 0;
                
                item.setLevel(Math.min(this.getLevel(), 15));
                
                for (int i = oldLevel + 1; i <= item.getLevel(); i++) {
                    if (i % 3 == 0) {
                        upgrades++;
                    }
                }
                
                if (upgrades > 0) {
                    item.addSubAffixes(upgrades);
                }
                
                hasChanged = true;
            }
        }
        
        return hasChanged;
    }
}
