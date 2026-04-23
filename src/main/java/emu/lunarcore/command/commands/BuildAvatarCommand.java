package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.AvatarPropertyType;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.enums.RelicType;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.inventory.GameItemSubAffix;
import emu.lunarcore.util.Utils;

@Command(label = "buildavatar", aliases = {"build"}, requireTarget = true, permission = "player.give", desc = "/buildavatar [{avatar id} | cur | all | lineup]. Creates a set of relics for the selected avatars")
public class BuildAvatarCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Temp avatar list
        List<GameAvatar> changeList = new ArrayList<>();
        
        // Handle optional arguments
        String arg = args.get(0).toLowerCase();
        
        if (arg.isBlank()) {
            // Select current avatar if we dont specify any extra args
            changeList.add(args.getTarget().getCurrentLeaderAvatar());
        } else {
            // Handle
            switch (arg) {
                case "all":
                    args.getTarget().getAvatars().forEach(changeList::add);
                    break;
                case "lineup":
                    args.getTarget().getCurrentLineup().forEachAvatar(changeList::add);
                    break;
                case "cur":
                    changeList.add(args.getTarget().getCurrentLeaderAvatar());
                    break;
                default:
                    int avatarId = Utils.parseSafeInt(arg);
                    var avatar = args.getTarget().getAvatarById(avatarId);
                    if (avatar != null) {
                        changeList.add(avatar);
                    }
                    break;
            }
        }
        
        // Try to set properties of avatars
        Iterator<GameAvatar> it = changeList.iterator();
        while (it.hasNext()) {
            GameAvatar avatar = it.next();
            this.createRelics(args, avatar);
        }
        
        if (changeList.size() == 0) {
            args.sendMessage("No avatars selected");
        }
    }

    private void createRelics(CommandArgs args, GameAvatar avatar) {
        // Get recommended relics
        var excel = GameData.getAvatarRelicRecommendExcelMap().get(avatar.getExcelId());
        if (excel == null) {
            args.sendMessage("Recommended relics for avatar " + avatar.getExcelId() + " not found");
            return;
        }
        
        // Delete old relics if flag is set
        if (args.hasFlag("-delete")) {
            List<GameItem> deleteList = new ArrayList<>();
            for (var type : RelicType.values()) {
                var item = avatar.unequipItem(type.getVal());
                if (item != null) {
                    deleteList.add(item);
                }
            }
            // Delete
            args.getTarget().getInventory().removeItems(deleteList);
        }
        
        // Create relics
        List<GameItem> relics = new ArrayList<>();
        
        // Head
        this.generateRelic(args, relics, excel.getMainSetId(), 1, null, excel.getSubAffixPropertyList());
        
        // Hand
        this.generateRelic(args, relics, excel.getMainSetId(), 2, null, excel.getSubAffixPropertyList());
        
        // Body
        this.generateRelic(args, relics, excel.getMainSetId(), 3, excel.getProp3(), excel.getSubAffixPropertyList());
        
        // Foot
        this.generateRelic(args, relics, excel.getMainSetId(), 4, excel.getProp4(), excel.getSubAffixPropertyList());
        
        // Neck
        this.generateRelic(args, relics, excel.getPlanarSetId(), 5, excel.getProp5(), excel.getSubAffixPropertyList());
        
        // Object
        this.generateRelic(args, relics, excel.getPlanarSetId(), 6, excel.getProp6(), excel.getSubAffixPropertyList());
        
        // Give to player
        args.getTarget().getInventory().addItems(relics);
        
        // Equip to avatar
        for (var item : relics) {
            avatar.equipItem(item);
        }
        
        // Complete
        args.sendMessage("Created recommended relics for avatar " + avatar.getExcelId());
    }
    
    private void generateRelic(CommandArgs args, List<GameItem> relics, int setId, int relicType, AvatarPropertyType mainProp, Set<AvatarPropertyType> subProps) {
        // Generate item id
        int rarity = (ItemRarity.SuperRare.getVal() + 1) * 10000;
        int set = setId * 10;
        int itemId = rarity + set + relicType;
        
        // Create relic
        GameItem item = new GameItem(itemId);
        if (item.getExcel() == null) return;
        
        // Relic excel
        var excel = item.getExcel().getRelicExcel();
        if (excel == null) return;

        // Set main affix
        if (mainProp != null) {
            for (var affix : GameDepot.getRelicMainAffixesByGroup(excel.getMainAffixGroup())) {
                if (affix.getProperty() == mainProp) {
                    item.setMainAffix(affix.getAffixID());
                    break;
                }
            }
        }
        
        // Set sub affixes
        item.resetSubAffixes();
    
        for (var affix : GameDepot.getRelicSubAffixesByGroup(excel.getSubAffixGroup())) {
            // Skip main affix
            if (affix.getProperty() == mainProp) {
                continue;
            }
            
            // Create sub affix
            if (subProps.contains(affix.getProperty())) {
                item.addSubAffix(new GameItemSubAffix(affix));
                
                // Check limit
                if (item.getSubAffixListSize() >= 4) {
                    break;
                }
            }
        }
        
        // Set max level
        item.setLevel(excel.getMaxLevel());
        
        // Quality flag - forces rolls to be on favored sub affixes first
        if (args.hasFlag("-quality")) {
            int maxUniqueSubAffixes = Math.max(item.getExcel().getRarity().getVal() - 1, item.getCurrentSubAffixCount());
            int upgrades = item.getMaxNormalSubAffixCount() - maxUniqueSubAffixes;
            for (int i = 0; i < upgrades;i++) {
                item.upgradeRandomSubAffix();
            }
        }
        
        // Apply sub stat upgrades to the relic
        int upgrades = item.getMaxNormalSubAffixCount() - item.getCurrentSubAffixCount();
        if (upgrades > 0) {
            item.addRandomSubAffixes(upgrades);
        }
        
        // Sort sub affixes
        item.sortSubAffixes();
        
        // Handle flags
        if (args.hasFlag("-maxsteps")) {
            item.getSubAffixes().forEach(subAffix -> subAffix.setStep(subAffix.getCount() * 2));
        }
        
        // Add to list
        relics.add(item);
    }
}
