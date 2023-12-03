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

@Command(
        label = "give", 
        aliases = {"g", "item"}, 
        permission = "player.give", 
        requireTarget = true, 
        desc = "/give [item id] x(amount). Gives the targeted player an item."
)
public class GiveCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Setup items
        List<GameItem> items = new LinkedList<>();
        
        // Get amount to give
        int amount = Math.max(args.getAmount(), 1);
        
        // Parse items
        for (String arg : args.getList()) {
            // Parse item id
            int itemId = Utils.parseSafeInt(arg);
            
            ItemExcel itemData = GameData.getItemExcelMap().get(itemId);
            if (itemData == null) {
                this.sendMessage(sender, "Item \"" + arg + "\" does not exist!");
                continue;
            }
            
            // Add item
            if (itemData.getItemMainType() == ItemMainType.AvatarCard) {
                // Add avatar to target
                GameAvatar avatar = new GameAvatar(itemData.getId());
                args.setProperties(avatar);
                args.getTarget().addAvatar(avatar);
            } else if (itemData.isEquippable()) {
                // Make sure we dont go over the inventory limit
                var tab = args.getTarget().getInventory().getInventoryTab(itemData.getItemMainType());
                amount = Math.min(amount, tab.getAvailableCapacity());
                
                // Add items
                for (int i = 0; i < amount; i++) {
                    GameItem item = new GameItem(itemData);
                    args.setProperties(item);
                    
                    items.add(item);
                }
            } else {
                items.add(new GameItem(itemData, amount));
            }
            
            // Send message
            this.sendMessage(sender, "Giving " + args.getTarget().getName() + " " + amount + " of " + itemId);
        }
        
        // Add to player inventory
        args.getTarget().getInventory().addItems(items, true);
    }
}
