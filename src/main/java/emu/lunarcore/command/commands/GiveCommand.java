package emu.lunarcore.command.commands;

import java.util.LinkedList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.util.Utils;

@Command(label = "give", aliases = {"g"}, permission = "player.give", desc = "/give [item id] x[amount]. Gives the targetted player an item.")
public class GiveCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        int itemId = Utils.parseSafeInt(args.get(0));
        int amount = Math.max(args.getAmount(), 1);
        
        ItemExcel itemData = GameData.getItemExcelMap().get(itemId);
        
        if (itemData == null) {
            this.sendMessage(sender, "Error: Item data not found");
            return;
        }
        
        // Setup items
        List<GameItem> items = new LinkedList<>();
        
        if (itemData.getItemMainType() == ItemMainType.AvatarCard) {
            // Add avatar
            GameAvatar avatar = new GameAvatar(itemData.getId());
            
            if (args.getTarget().addAvatar(avatar)) {
                // Change avatar properties
                args.setProperties(avatar);
            }
        } else if (itemData.isEquippable()) {
            for (int i = 0; i < amount; i++) {
                GameItem item = new GameItem(itemData);
                
                if (item.getExcel().isEquipment()) {
                    // Try to set level
                    if (args.getLevel() > 0) {
                        item.setLevel(Math.min(args.getLevel(), 80));
                        item.setPromotion(Utils.getMinPromotionForLevel(item.getLevel()));
                    }
                    
                    // Try to set promotion
                    if (args.getPromotion() >= 0) {
                        item.setPromotion(Math.min(args.getPromotion(), item.getExcel().getEquipmentExcel().getMaxPromotion()));
                    }
                    
                    // Try to set rank (superimposition)
                    if (args.getRank() >= 0) {
                        item.setRank(Math.min(args.getRank(), item.getExcel().getEquipmentExcel().getMaxRank()));
                    }
                }
                
                items.add(item);
            }
        } else {
            items.add(new GameItem(itemData, amount));
        }
        
        // Add and send message to player
        args.getTarget().getInventory().addItems(items, true);
        args.getTarget().sendMessage("Giving " + args.getTarget().getName() + " " + amount + " of " + itemId);
    }

}
