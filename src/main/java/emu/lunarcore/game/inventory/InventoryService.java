package emu.lunarcore.game.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.GameDepot;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.excel.*;
import emu.lunarcore.data.excel.ItemComposeExcel.FormulaType;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.enums.ItemRarity;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.send.*;

public class InventoryService extends BaseGameService {

    public InventoryService(GameServer server) {
        super(server);
    }

    // === Avatars ===

    public List<GameItem> levelUpAvatar(Player player, int avatarId, Collection<ItemParam> items) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null) return null;

        AvatarPromotionExcel promoteData = GameData.getAvatarPromotionExcel(avatarId, avatar.getPromotion());
        if (promoteData == null) return null;

        // Exp gain
        int amount = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getExcel().getAvatarExp() == 0 || item.getCount() < param.getCount()) {
                return null;
            }

            amount += item.getExcel().getAvatarExp() * param.getCount();
        }

        // Verify credits
        int cost = amount / 10;
        if (player.getScoin() < cost) {
            return null;
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);
        player.addSCoin(-cost);

        // Level up
        int maxLevel = promoteData.getMaxLevel();
        int level = avatar.getLevel();
        int exp = avatar.getExp();
        int reqExp = GameData.getAvatarExpRequired(avatar.getExcel().getExpGroup(), level);

        while (amount > 0 && reqExp > 0 && level < maxLevel) {
            // Do calculations
            int toGain = Math.min(amount, reqExp - exp);
            exp += toGain;
            amount -= toGain;
            // Level up
            if (exp >= reqExp) {
                // Exp
                exp = 0;
                level += 1;
                // Set req exp
                reqExp = GameData.getAvatarExpRequired(avatar.getExcel().getExpGroup(), level);
            }
        }

        // Done
        avatar.setLevel(level);
        avatar.setExp(exp);

        avatar.save();
        player.save();

        // Calculate leftover exp
        var leftoverItems = new ItemParamMap();
        
        while (GameDepot.getAvatarExpExcels().size() > 0) {
            int oldAmount = amount;
            for (var expExcel : GameDepot.getAvatarExpExcels()) {
                if (amount >= expExcel.getExp()) {
                    leftoverItems.put(expExcel.getItemID(), leftoverItems.get(expExcel.getItemID()) + 1);
                    amount -= expExcel.getExp();
                    break;
                }
            }
            if (oldAmount == amount) break;
        }
        
        // Create leftover exp items
        List<GameItem> returnItems = player.getInventory().addItems(leftoverItems);

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        return returnItems;
    }

    public boolean promoteAvatar(Player player, int avatarId) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null || avatar.getPromotion() >= avatar.getExcel().getMaxPromotion()) return false;

        AvatarPromotionExcel promotion = GameData.getAvatarPromotionExcel(avatarId, avatar.getPromotion());
        // Sanity check
        if ((promotion == null) || avatar.getLevel() < promotion.getMaxLevel() || player.getLevel() < promotion.getPlayerLevelRequire() || player.getWorldLevel() < promotion.getWorldLevelRequire()) {
            return false;
        }

        // Verify item params
        if (!player.getInventory().verifyItems(promotion.getPromotionCostList())) {
            return false;
        }

        // Pay items
        player.getInventory().removeItemsByParams(promotion.getPromotionCostList());

        // Promote
        avatar.setPromotion(avatar.getPromotion() + 1);

        avatar.save();
        player.save();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        return true;
    }

    public boolean unlockSkillTreeAvatar(Player player, int avatarId, int pointId) {
        // Get avatar + Skill Tree data
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null) return false;

        int nextLevel = avatar.getSkills().getOrDefault(pointId, 0) + 1;

        AvatarSkillTreeExcel skillTree = GameData.getAvatarSkillTreeExcel(pointId, nextLevel);
        if (skillTree == null || skillTree.getAvatarID() != avatar.getExcel().getAvatarID()) {
            return false;
        }

        // Verify item params
        if (!player.getInventory().verifyItems(skillTree.getMaterialList())) {
            return false;
        }

        // Pay items
        player.getInventory().removeItemsByParams(skillTree.getMaterialList());

        // Add skill
        avatar.getSkills().put(pointId, nextLevel);

        // Save player
        player.save();

        // Save avatar and send packets
        if (avatar.getHeroPath() != null) {
            avatar.getHeroPath().save();
            player.sendPacket(new PacketPlayerSyncScNotify(avatar.getHeroPath()));
        } else {
            avatar.save();
            player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        }
        
        return true;
    }

    public boolean rankUpAvatar(Player player, int avatarId) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null || avatar.getRank() >= avatar.getExcel().getMaxRank()) return false;
        
        AvatarRankExcel rankData = GameData.getAvatarRankExcel(avatar.getExcel().getRankId(avatar.getRank()));
        if (rankData == null) return false;
        
        // Verify items
        if (!player.getInventory().verifyItems(rankData.getUnlockCost())) {
            return false;
        }
        
        // Pay items
        player.getInventory().removeItemsByParams(rankData.getUnlockCost());

        // Add rank
        avatar.setRank(avatar.getRank() + 1);
        
        // Save avatar and send packets
        if (avatar.getHeroPath() != null) {
            avatar.getHeroPath().save();
            player.sendPacket(new PacketPlayerSyncScNotify(avatar.getHeroPath()));
        } else {
            avatar.save();
            player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        }
        
        return true;
    }
    
    public List<GameItem> takePromotionRewardAvatar(Player player, int avatarId, int promotion) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null) {
            return null;
        }
        
        // Sanity
        if (promotion <= 0 || promotion > avatar.getPromotion()) {
            return null;
        }
        
        // Make sure promotion level is odd + Make sure promotion reward isnt already taken
        if (promotion % 2 == 0 || avatar.hasTakenReward(promotion)) {
            return null;
        }
        
        // Set reward as taken
        avatar.takeReward(promotion);
        avatar.save();
        
        // Setup rewards
        List<GameItem> rewards = new ArrayList<>();
        rewards.add(new GameItem(101, 1)); // Promotion reward is in excels, but we will hardcode it for now as its easier

        // Add items to player inventory
        player.getInventory().addItems(rewards);
        
        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        return rewards;
    }

    // === Equipment ===

    public List<GameItem> levelUpEquipment(Player player, int equipId, Collection<ItemParam> items) {
        // Get equipment
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquipment()) {
            return null;
        }

        EquipmentPromotionExcel promoteData = GameData.getEquipmentPromotionExcel(equip.getItemId(), equip.getPromotion());
        if (promoteData == null) return null;

        // Exp gain
        int cost = 0;
        int amount = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.isLocked() || item.getCount() < param.getCount()) {
                return null;
            }

            if (item.getExcel().getEquipmentExp() > 0) {
                amount += item.getExcel().getEquipmentExp() * param.getCount();
                cost += item.getExcel().getEquipmentExpCost() * param.getCount();
            }
        }

        // Verify credits
        if (player.getScoin() < cost) {
            return null;
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);
        player.addSCoin(-cost);

        // Level up
        int maxLevel = promoteData.getMaxLevel();
        int level = equip.getLevel();
        int exp = equip.getExp();
        int reqExp = GameData.getEquipmentExpRequired(equip.getExcel().getEquipmentExcel().getExpType(), level);

        while (amount > 0 && reqExp > 0 && level < maxLevel) {
            // Do calculations
            int toGain = Math.min(amount, reqExp - exp);
            exp += toGain;
            amount -= toGain;
            // Level up
            if (exp >= reqExp) {
                // Exp
                exp = 0;
                level += 1;
                // Set req exp
                reqExp = GameData.getEquipmentExpRequired(equip.getExcel().getEquipmentExcel().getExpType(), level);
            }
        }

        // Done
        equip.setLevel(level);
        equip.setExp(exp);

        equip.save();
        player.save();

        // Calculate leftover exp
        var leftoverItems = new ItemParamMap();
        
        while (GameDepot.getEquipmentExpExcels().size() > 0) {
            int oldAmount = amount;
            for (var expExcel : GameDepot.getEquipmentExpExcels()) {
                if (amount >= expExcel.getExpProvide()) {
                    leftoverItems.put(expExcel.getItemID(), leftoverItems.get(expExcel.getItemID()) + 1);
                    amount -= expExcel.getExpProvide();
                    break;
                }
            }
            if (oldAmount == amount) break;
        }
        
        // Create leftover exp items
        List<GameItem> returnItems = player.getInventory().addItems(leftoverItems);

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        return returnItems;
    }

    public boolean promoteEquipment(Player player, int equipId) {
        // Get equipment
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquipment() || equip.getPromotion() >= equip.getExcel().getEquipmentExcel().getMaxPromotion()) {
            return false;
        }

        EquipmentPromotionExcel promotion = GameData.getEquipmentPromotionExcel(equip.getItemId(), equip.getPromotion());
        // Sanity check
        if ((promotion == null) || equip.getLevel() < promotion.getMaxLevel() || player.getLevel() < promotion.getPlayerLevelRequire() || player.getWorldLevel() < promotion.getWorldLevelRequire()) {
            return false;
        }

        // Verify items
        for (ItemParam param : promotion.getPromotionCostList()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount()) {
                return false;
            }
        }

        // Verify credits
        if (player.getScoin() < promotion.getPromotionCostCoin()) {
            return false;
        }

        // Pay items
        player.getInventory().removeItemsByParams(promotion.getPromotionCostList());
        player.addSCoin(-promotion.getPromotionCostCoin());

        // Promote
        equip.setPromotion(equip.getPromotion() + 1);

        equip.save();
        player.save();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        return true;
    }

    public boolean rankUpEquipment(Player player, int equipId, List<ItemParam> items) {
        // Get avatar
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquipment() || equip.getRank() >= equip.getExcel().getEquipmentExcel().getMaxRank()) {
            return false;
        }
        
        // Rank up amount
        int amount = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || !equip.getExcel().getEquipmentExcel().isRankUpItem(item) || item.getCount() < param.getCount()) {
                return false;
            }
            
            amount += item.getRank();
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);

        // Add rank
        equip.setRank(Math.min(equip.getRank() + amount, equip.getExcel().getEquipmentExcel().getMaxRank()));
        equip.save();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        return true;
    }

    // === Relic ===

    public List<GameItem> levelUpRelic(Player player, int equipId, Collection<ItemParam> items) {
        // Get relic
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isRelic()) {
            return null;
        }

        // Exp gain
        int cost = 0;
        int amount = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.isLocked() || item.getCount() < param.getCount()) {
                return null;
            }

            if (item.getExcel().getRelicExp() > 0) {
                amount += item.getExcel().getRelicExp() * param.getCount();
                cost += item.getExcel().getRelicExpCost() * param.getCount();
            }

            if (item.getTotalExp() > 0) {
                amount += (int) Math.floor(item.getTotalExp() * 0.80D);
            }
        }

        // Verify credits
        if (player.getScoin() < cost) {
            return null;
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);
        player.addSCoin(-cost);

        // Level up
        int maxLevel = equip.getExcel().getRelicExcel().getMaxLevel();
        int level = equip.getLevel();
        int exp = equip.getExp();
        int totalExp = equip.getTotalExp();
        int upgrades = 0;
        int reqExp = GameData.getRelicExpRequired(equip.getExcel().getRelicExcel().getExpType(), level);

        while (amount > 0 && reqExp > 0 && level < maxLevel) {
            // Do calculations
            int toGain = Math.min(amount, reqExp - exp);
            exp += toGain;
            totalExp += toGain;
            amount -= toGain;
            // Level up
            if (exp >= reqExp) {
                // Exp
                exp = 0;
                level += 1;
                // Check upgrades
                if (level % 3 == 0) {
                    upgrades++;
                }
                // Set req exp
                reqExp = GameData.getRelicExpRequired(equip.getExcel().getRelicExcel().getExpType(), level);
            }
        }

        // Add affixes
        if (upgrades > 0) {
            equip.addSubAffixes(upgrades);
        }

        // Done
        equip.setLevel(level);
        equip.setExp(exp);
        equip.setTotalExp(totalExp);

        equip.save();
        player.save();

        // Calculate leftover exp
        var leftoverItems = new ItemParamMap();

        while (GameDepot.getRelicExpExcels().size() > 0) {
            int oldAmount = amount;
            for (var expExcel : GameDepot.getRelicExpExcels()) {
                if (amount >= expExcel.getExpProvide()) {
                    leftoverItems.put(expExcel.getItemID(), leftoverItems.get(expExcel.getItemID()) + 1);
                    amount -= expExcel.getExpProvide();
                    break;
                }
            }
            if (oldAmount == amount) break;
        }
        
        // Create leftover exp items
        List<GameItem> returnItems = player.getInventory().addItems(leftoverItems);

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        return returnItems;
    }

    // === Etc ===

    public void lockItem(Player player, int equipId, boolean locked) {
        GameItem item = player.getInventory().getItemByUid(equipId);
        if (item == null || !item.getExcel().isEquippable()) {
            return;
        }

        item.setLocked(locked);
        item.save();

        //  Send packet
        player.sendPacket(new PacketPlayerSyncScNotify(item));
    }
    
    public void discardRelic(Player player, int equipId, boolean discarded) {
        GameItem relic = player.getInventory().getItemByUid(equipId);
        if (relic == null || !relic.getExcel().isRelic()) {
            return;
        }

        relic.setDiscarded(discarded);
        relic.save();

        //  Send packet
        player.sendPacket(new PacketPlayerSyncScNotify(relic));
    }

    public List<GameItem> sellItems(Player player, boolean toMaterials, List<ItemParam> items) {
        // Verify items
        var returnItems = new ItemParamMap();

        for (ItemParam param : items) {
            // Get item in inventory
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.isLocked() || item.getCount() < param.getCount()) {
                return null;
            }

            // Add return items
            if (item.getExcel().getRarity() == ItemRarity.SuperRare && !toMaterials) {
                // Relic remains
                returnItems.addTo(GameConstants.RELIC_REMAINS_ID, 10);
            } else {
                // Add basic return items
                for (ItemParam ret : item.getExcel().getReturnItemIDList()) {
                    returnItems.addTo(ret.getId(), ret.getCount());
                }
            }
        }

        // Delete items
        player.getInventory().removeItemsByParams(items);

        // Add return items
        return player.getInventory().addItems(returnItems);
    }
    
    public List<GameItem> composeItem(Player player, int composeId, int count, List<ItemParam> costItems) {
        // Sanity check
        if (count <= 0) return null;
        
        // Get item compose excel data
        ItemComposeExcel excel = GameData.getItemComposeExcelMap().get(composeId);
        if (excel == null) return null;
        
        // Composed item list
        List<GameItem> items = new ArrayList<>();
        
        if (excel.getFormulaType() == FormulaType.Normal) { // Material synthesis
            // Verify items + credits
            if (!player.getInventory().verifyItems(excel.getMaterialCost(), count) || !player.getInventory().verifyScoin(excel.getCoinCost() * count)) {
                return null;
            }
            
            // Pay items
            player.getInventory().removeItemsByParams(excel.getMaterialCost(), count);
            player.addSCoin(-excel.getCoinCost() * count);
            
            // Create item
            items.add(new GameItem(excel.getItemID(), count));
        } else if (excel.getFormulaType() == FormulaType.Sepcial) { // Material exchange
            // Verify items
            int totalAmount = 0;
            
            for (ItemParam param : costItems) {
                // Make sure param item is in special material cost
                if (!excel.getSpecialMaterialCost().contains(param.getId())) {
                    return null;
                }

                // Make sure we have enough
                GameItem costItem = player.getInventory().getItemByParam(param);
                if (costItem == null) return null;

                // Verify amount
                if (costItem.getCount() >= param.getCount()) {
                    totalAmount += param.getCount();
                }
            }

            // Sanity check the amount of materials were exchanging
            if (totalAmount != count * excel.getSpecialMaterialCostNumber()) {
                return null;
            }
            
            // Pay items
            player.getInventory().removeItemsByParams(costItems);
            
            // Create item
            items.add(new GameItem(excel.getItemID(), count));
        }
        
        // Add items to inventory
        if (items.size() > 0) {
            player.getInventory().addItems(items);
            return items;
        } else {
            return null;
        }
    }
    
    public List<GameItem> composeRelic(Player player, int composeId, int relicId, int mainAffix, int count) {
        // Sanity check
        if (count <= 0) return null;
        
        // Get item compose excel data
        ItemComposeExcel excel = GameData.getItemComposeExcelMap().get(composeId);
        if (excel == null || excel.getFormulaType() != FormulaType.SelectedRelic) {
            return null;
        }
        
        // Verify relic ids
        if (excel.getRelicList() == null || !excel.getRelicList().contains(relicId)) {
            return null;
        }
        
        // Get relic excel
        ItemExcel itemExcel = GameData.getItemExcelMap().get(relicId);
        if (itemExcel == null) return null;
        
        // Build cost items
        List<ItemParam> costItems = new ArrayList<>();
        costItems.addAll(excel.getMaterialCost());
        
        // Check main affix
        if (mainAffix > 0) {
            // TODO verify main affix on item
            
            for (int specialId : excel.getSpecialMaterialCost()) {
                costItems.add(new ItemParam(specialId, 1));
            }
        }
        
        // Verify items + credits
        if (!player.getInventory().verifyItems(costItems, count) || !player.getInventory().verifyScoin(excel.getCoinCost() * count)) {
            return null;
        }
        
        // Pay items
        player.getInventory().removeItemsByParams(costItems, count);
        player.addSCoin(-excel.getCoinCost() * count);
        
        // Compose item
        List<GameItem> items = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            GameItem item = new GameItem(itemExcel, 1, mainAffix);
            items.add(item);
        }
        
        // Add items to inventory
        player.getInventory().addItems(items);
        
        return items;
    }
}
