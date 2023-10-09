package emu.lunarcore.command.commands;

import java.util.LinkedList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.util.Utils;

@Command(label = "give", aliases = {"g"}, permission = "player.give")
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
        
        if (itemData.isEquippable()) {
            List<GameItem> items = new LinkedList<>();
            for (int i = 0; i < amount; i++) {
                items.add(new GameItem(itemData));
            }
            args.getTarget().getInventory().addItems(items);
        } else {
            GameItem item = new GameItem(itemData, amount);
            args.getTarget().getInventory().addItem(item);
        }

        args.getTarget().sendMessage("Giving " + args.getTarget().getName() + " " + amount + " of " + itemId);
    }

}
