package emu.lunarcore.command;

import java.util.List;

import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.inventory.GameItemSubAffix;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.util.Utils;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import lombok.Getter;

@Getter
public class CommandArgs {
    private String raw;
    private List<String> list;
    private Player sender;
    private Player target;
    
    private int targetUid;
    private int amount;
    private int level = -1;
    private int rank = -1;
    private int promotion = -1;
    private int stage = -1;
    
    private Int2IntMap map;
    private ObjectSet<String> flags;

    public CommandArgs(Player sender, List<String> args) {
        this.sender = sender;
        this.raw = String.join(" ", args);
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
                } else if (arg.startsWith("-")) { // Flag
                    if (this.flags == null) this.flags = new ObjectOpenHashSet<>();
                    this.flags.add(arg);
                    it.remove();
                } else if (arg.contains(":") || arg.contains(",")) {
                    String[] split = arg.split("[:,]");
                    if (split.length >= 2) {
                        int key = Integer.parseInt(split[0]);
                        int value = Integer.parseInt(split[1]);
                        
                        if (this.map == null) this.map = new Int2IntOpenHashMap();
                        this.map.put(key, value);
                        
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
            return "";
        }
        return this.list.get(index);
    }
    
    /**
     * Sends a message to the command sender
     * @param message
     */
    public void sendMessage(String message) {
        if (sender != null) {
            sender.sendMessage(message);
        } else {
            LunarCore.getLogger().info(message);
        }
    }
    
    public boolean hasFlag(String flag) {
        if (this.flags == null) return false;
        return this.flags.contains(flag);
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
        
        // Handle flags
        if (this.hasFlag("-takerewards")) {
            if (avatar.setRewards(0b00101010)) {
                hasChanged = true;
            }
        } else if (this.hasFlag("-clearrewards")) {
            if (avatar.setRewards(0)) { // Note: Requires the player to restart their game to show
                hasChanged = true;
            }
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
            // Sub stats
            if (this.getMap() != null) {
                // Reset substats first
                item.resetSubAffixes();
                
                int maxCount = (int) Math.floor(LunarCore.getConfig().getServerOptions().maxCustomRelicLevel / 3) + 1;
                hasChanged = true;
                
                for (var entry : this.getMap().int2IntEntrySet()) {
                    if (entry.getIntValue() <= 0) continue;
                    
                    var subAffix = GameData.getRelicSubAffixExcel(item.getExcel().getRelicExcel().getSubAffixGroup(), entry.getIntKey());
                    if (subAffix == null) continue;
                    
                    // Set count
                    int count = Math.min(entry.getIntValue(), maxCount);
                    item.getSubAffixes().add(new GameItemSubAffix(subAffix, count));
                }
            }
            
            // Main stat
            if (this.getStage() > 0) {
                var mainAffix = GameData.getRelicMainAffixExcel(item.getExcel().getRelicExcel().getMainAffixGroup(), this.getStage());
                if (mainAffix != null) {
                    item.setMainAffix(mainAffix.getAffixID());
                    hasChanged = true;
                }
            }
            
            // Try to set level
            if (this.getLevel() > 0) {
                // Set relic level
                item.setLevel(Math.min(this.getLevel(), LunarCore.getConfig().getServerOptions().maxCustomRelicLevel));
                
                // Apply sub stat upgrades to the relic
                int upgrades = item.getMaxNormalSubAffixCount() - item.getCurrentSubAffixCount();
                if (upgrades > 0) {
                    item.addSubAffixes(upgrades);
                }
                
                hasChanged = true;
            }
            
            // Handle flags
            if (this.hasFlag("-maxsteps")) {
                if (item.getSubAffixes() == null) {
                    item.resetSubAffixes();
                }
                
                item.getSubAffixes().forEach(subAffix -> subAffix.setStep(subAffix.getCount() * 2));
            }
        }
        
        return hasChanged;
    }
}
