package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.enums.ItemSubType;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;

@Command(label = "giveall", aliases = {"ga"}, permission = "player.give", requireTarget = true, desc = "/giveall {materials | avatars | lightcones | relics}. Gives the targeted player items.")
public class GiveAllCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        Player target = args.getTarget();
        String type = args.get(0).toLowerCase();

        switch (type) {
            default -> this.sendMessage(sender, "Error: Invalid type");
            case "m", "materials", "mats" -> {
                List<GameItem> items = new ArrayList<>();

                // Character/Relic/Lightcone upgrade materials
                for (ItemExcel excel : GameData.getItemExcelMap().values()) {
                    int purpose = excel.getPurposeType();
                    if ((purpose >= 1 && purpose <= 7) || purpose == 10) {
                        items.add(new GameItem(excel, 1000));
                    }
                }

                // Credits
                items.add(new GameItem(2, 50_000_000));

                // Add to target's inventory
                target.getInventory().addItems(items, true);

                // Send message
                this.sendMessage(sender, "Giving " + target.getName() + " " + items.size() + " items");
            }
            case "lc", "lightcones" -> {
                // Get lightcones
                List<GameItem> items = GameData.getItemExcelMap().values()
                        .stream()
                        .filter(ItemExcel::isEquipment)
                        .map(excel -> {
                            var item = new GameItem(excel, 1);
                            args.setProperties(item);
                            return item;
                        })
                        .toList();

                // Add to target's inventory
                target.getInventory().addItems(items, true);

                // Send message
                this.sendMessage(sender, "Giving " + target.getName() + " " + items.size() + " light cones");
            }
            case "ic", "icons" -> {
                // Get UnlockedHeads
                for (var iconhead : GameData.getPlayerIconExcelMap().values()) {
                    // This function will handle any duplicate head icons
                    target.addHeadIcon(iconhead.getId());
                }

                // Send message
                this.sendMessage(sender, "Added all icons to " + target.getName());
            }
            case "r", "relics" -> {
                // Get relics
                List<GameItem> items = GameData.getItemExcelMap().values()
                        .stream()
                        .filter(excel -> excel.isRelic() && excel.getRarity() == ItemRarity.SuperRare)
                        .map(excel -> {
                            var item = new GameItem(excel, 1);
                            args.setProperties(item);
                            return item;
                        })
                        .toList();

                // Add to target's inventory
                target.getInventory().addItems(items, true);

                // Send message
                this.sendMessage(sender, "Giving " + target.getName() + " " + items.size() + " relics");
            }
            case "a", "characters", "avatars" -> {
                // All avatars and their eidolons
                for (ItemExcel excel : GameData.getItemExcelMap().values()) {
                    if (excel.getItemMainType() == ItemMainType.AvatarCard) {
                        // Skip if target already has this avatar
                        if (target.getAvatars().hasAvatar(excel.getId())) {
                            continue;
                        }

                        // Add avatar
                        var avatarExcel = GameData.getAvatarExcelMap().get(excel.getId());
                        if (avatarExcel != null) {
                            GameAvatar avatar = new GameAvatar(avatarExcel);
                            args.setProperties(avatar); // Set avatar properties

                            target.getAvatars().addAvatar(avatar);
                        }
                    } else if (excel.getItemSubType() == ItemSubType.Eidolon) {
                        // Add eidolons
                        target.getInventory().addItem(excel, 6);
                    }
                }

                // Send message
                this.sendMessage(sender, "Giving " + target.getName() + " all avatars");
            }
        }
    }

}
