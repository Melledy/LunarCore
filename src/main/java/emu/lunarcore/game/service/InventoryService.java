package emu.lunarcore.game.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.excel.*;
import emu.lunarcore.data.excel.ItemComposeExcel.FormulaType;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.send.*;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;

public class InventoryService extends BaseGameService {

    public InventoryService(GameServer server) {
        super(server);
    }

    // === Avatars ===

    public void levelUpAvatar(Player player, int avatarId, Collection<ItemParam> items) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null) return;

        AvatarPromotionExcel promoteData = GameData.getAvatarPromotionExcel(avatarId, avatar.getPromotion());
        if (promoteData == null) return;

        // Exp gain
        int expGain = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getExcel().getAvatarExp() == 0 || item.getCount() < param.getCount()) return;

            expGain += item.getExcel().getAvatarExp() * param.getCount();
        }

        // Verify credits
        int cost = expGain / 10;
        if (player.getScoin() < cost) {
            player.sendPacket(new PacketAvatarExpUpScRsp());
            return;
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);
        player.addSCoin(-cost);

        // Level up
        int maxLevel = promoteData.getMaxLevel();
        int level = avatar.getLevel();
        int exp = avatar.getExp();
        int reqExp = GameData.getAvatarExpRequired(avatar.getExcel().getExpGroup(), level);

        while (expGain > 0 && reqExp > 0 && level < maxLevel) {
            // Do calculations
            int toGain = Math.min(expGain, reqExp - exp);
            exp += toGain;
            expGain -= toGain;
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

        // TODO add back leftover exp
        List<GameItem> returnItems = new ArrayList<>();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        player.sendPacket(new PacketAvatarExpUpScRsp(returnItems));
    }

    public void promoteAvatar(Player player, int avatarId) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null || avatar.getPromotion() >= avatar.getExcel().getMaxPromotion()) return;

        AvatarPromotionExcel promotion = GameData.getAvatarPromotionExcel(avatarId, avatar.getPromotion());
        // Sanity check
        if ((promotion == null) || avatar.getLevel() < promotion.getMaxLevel() || player.getLevel() < promotion.getPlayerLevelRequire() || player.getWorldLevel() < promotion.getWorldLevelRequire()) {
            return;
        }

        // Verify items
        for (ItemParam param : promotion.getPromotionCostList()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount()) return;
        }

        // Verify credits
        if (player.getScoin() < promotion.getPromotionCostCoin()) {
            player.sendPacket(new BasePacket(CmdId.PromoteAvatarScRsp));
            return;
        }

        // Pay items
        player.getInventory().removeItemsByParams(promotion.getPromotionCostList());
        player.addSCoin(-promotion.getPromotionCostCoin());

        // Promote
        avatar.setPromotion(avatar.getPromotion() + 1);

        avatar.save();
        player.save();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        player.sendPacket(new BasePacket(CmdId.PromoteAvatarScRsp));
    }

    public void unlockSkillTreeAvatar(Player player, int pointId) {
        // Hacky way to get avatar id
        int avatarId = pointId / 1000;

        // Get avatar + Skill Tree data
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null) return;

        int nextLevel = avatar.getSkills().getOrDefault(pointId, 0) + 1;

        AvatarSkillTreeExcel skillTree = GameData.getAvatarSkillTreeExcel(pointId, nextLevel);
        if (skillTree == null || skillTree.getAvatarID() != avatar.getExcel().getAvatarID()) return;

        // Verify items
        for (ItemParam param : skillTree.getMaterialList()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount()) {
                player.sendPacket(new PacketUnlockSkilltreeScRsp());
                return;
            }
        }

        // Verify credits
        if (player.getScoin() < skillTree.getMaterialCostCoin()) {
            player.sendPacket(new PacketUnlockSkilltreeScRsp());
            return;
        }

        // Pay items
        player.getInventory().removeItemsByParams(skillTree.getMaterialList());
        player.addSCoin(-skillTree.getMaterialCostCoin());

        // Add skill
        avatar.getSkills().put(pointId, nextLevel);

        avatar.save();
        player.save();

        // Send packets
        if (avatar.getHeroPath() != null) {
            player.sendPacket(new PacketPlayerSyncScNotify(avatar.getHeroPath()));
        } else {
            player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        }
        
        player.sendPacket(new PacketUnlockSkilltreeScRsp(avatarId, pointId, nextLevel));
    }

    public void rankUpAvatar(Player player, int avatarId) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null || avatar.getRank() >= avatar.getExcel().getMaxRank()) return;

        AvatarRankExcel rankData = GameData.getAvatarRankExcel(avatar.getExcel().getRankId(avatar.getRank()));
        if (rankData == null) return;

        // Verify items
        for (ItemParam param : rankData.getUnlockCost()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount()) {
                player.sendPacket(new BasePacket(CmdId.RankUpAvatarScRsp));
                return;
            }
        }

        // Pay items
        player.getInventory().removeItemsByParams(rankData.getUnlockCost());

        // Add rank
        avatar.setRank(avatar.getRank() + 1);
        avatar.save();

        // Send packets
        if (avatar.getHeroPath() != null) {
            player.sendPacket(new PacketPlayerSyncScNotify(avatar.getHeroPath()));
        } else {
            player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        }
        
        player.sendPacket(new BasePacket(CmdId.RankUpAvatarScRsp));
    }
    
    public void takePromotionRewardAvatar(Player player, int avatarId, int promotion) {
        // Get avatar
        GameAvatar avatar = player.getAvatarById(avatarId);
        if (avatar == null) {
            player.sendPacket(new PacketTakePromotionRewardScRsp());
            return;
        }
        
        // Sanity
        if (promotion <= 0 || promotion > avatar.getPromotion()) {
            player.sendPacket(new PacketTakePromotionRewardScRsp());
            return;
        }
        
        // Make sure promotion level is odd + Make sure promotion reward isnt already taken
        if (promotion % 2 == 0 || avatar.getTakenRewards().contains(promotion)) {
            player.sendPacket(new PacketTakePromotionRewardScRsp());
            return;
        }
        
        // Set reward as taken
        avatar.getTakenRewards().add(promotion);
        avatar.save();
        
        // Setup rewards
        List<GameItem> rewards = new ArrayList<>();
        rewards.add(new GameItem(101, 1)); // Promotion reward is in excels, but we will hardcode it for now as its easier

        // Add items to player inventory
        player.getInventory().addItems(rewards);
        
        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(avatar));
        player.sendPacket(new PacketTakePromotionRewardScRsp(rewards));
    }

    // === Equipment ===

    public void levelUpEquipment(Player player, int equipId, Collection<ItemParam> items) {
        // Get equipment
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquipment()) {
            player.sendPacket(new PacketExpUpEquipmentScRsp());
            return;
        }

        EquipmentPromotionExcel promoteData = GameData.getEquipmentPromotionExcel(equip.getItemId(), equip.getPromotion());
        if (promoteData == null) return;

        // Exp gain
        int cost = 0;
        int expGain = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            System.out.println(param.getId());
            if (item == null || item.isLocked() || item.getCount() < param.getCount()) {
                player.sendPacket(new PacketExpUpEquipmentScRsp());
                return;
            }

            if (item.getExcel().getEquipmentExp() > 0) {
                expGain += item.getExcel().getEquipmentExp() * param.getCount();
                cost += item.getExcel().getEquipmentExpCost() * param.getCount();
            }
        }

        // Verify credits
        if (player.getScoin() < cost) {
            player.sendPacket(new PacketExpUpEquipmentScRsp());
            return;
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);
        player.addSCoin(-cost);

        // Level up
        int maxLevel = promoteData.getMaxLevel();
        int level = equip.getLevel();
        int exp = equip.getExp();
        int reqExp = GameData.getEquipmentExpRequired(equip.getExcel().getEquipmentExcel().getExpType(), level);

        while (expGain > 0 && reqExp > 0 && level < maxLevel) {
            // Do calculations
            int toGain = Math.min(expGain, reqExp - exp);
            exp += toGain;
            expGain -= toGain;
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

        // TODO add back leftover exp
        List<GameItem> returnItems = new ArrayList<>();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        player.sendPacket(new PacketExpUpEquipmentScRsp(returnItems));
    }

    public void promoteEquipment(Player player, int equipId) {
        // Get equipment
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquipment() || equip.getPromotion() >= equip.getExcel().getEquipmentExcel().getMaxPromotion()) {
            player.sendPacket(new BasePacket(CmdId.PromoteEquipmentScRsp));
            return;
        }

        EquipmentPromotionExcel promotion = GameData.getEquipmentPromotionExcel(equip.getItemId(), equip.getPromotion());
        // Sanity check
        if ((promotion == null) || equip.getLevel() < promotion.getMaxLevel() || player.getLevel() < promotion.getPlayerLevelRequire() || player.getWorldLevel() < promotion.getWorldLevelRequire()) {
            player.sendPacket(new BasePacket(CmdId.PromoteEquipmentScRsp));
            return;
        }

        // Verify items
        for (ItemParam param : promotion.getPromotionCostList()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount()) {
                player.sendPacket(new BasePacket(CmdId.PromoteEquipmentScRsp));
                return;
            }
        }

        // Verify credits
        if (player.getScoin() < promotion.getPromotionCostCoin()) {
            player.sendPacket(new BasePacket(CmdId.PromoteEquipmentScRsp));
            return;
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
        player.sendPacket(new BasePacket(CmdId.PromoteEquipmentScRsp));
    }

    public void rankUpEquipment(Player player, int equipId, List<ItemParam> items) {
        // Get avatar
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquipment() || equip.getRank() >= equip.getExcel().getEquipmentExcel().getMaxRank()) {
            player.sendPacket(new BasePacket(CmdId.RankUpEquipmentScRsp));
            return;
        }

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || !equip.getExcel().getEquipmentExcel().isRankUpItem(item) || item.getCount() < param.getCount()) {
                player.sendPacket(new BasePacket(CmdId.RankUpEquipmentScRsp));
                return;
            }
        }

        // Pay items
        player.getInventory().removeItemsByParams(items);

        // Add rank
        equip.setRank(Math.min(equip.getRank() + items.size(), equip.getExcel().getEquipmentExcel().getMaxRank()));
        equip.save();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        player.sendPacket(new BasePacket(CmdId.RankUpEquipmentScRsp));
    }

    // === Relic ===

    public void levelUpRelic(Player player, int equipId, Collection<ItemParam> items) {
        // Get relic
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isRelic()) {
            player.sendPacket(new PacketExpUpRelicScRsp());
            return;
        }

        // Exp gain
        int cost = 0;
        int expGain = 0;

        // Verify items
        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.isLocked() || item.getCount() < param.getCount()) {
                player.sendPacket(new PacketExpUpRelicScRsp());
                return;
            }

            if (item.getExcel().getRelicExp() > 0) {
                expGain += item.getExcel().getRelicExp() * param.getCount();
                cost += item.getExcel().getRelicExpCost() * param.getCount();
            }

            if (item.getTotalExp() > 0) {
                expGain += (int) Math.floor(item.getTotalExp() * 0.80D);
            }
        }

        // Verify credits
        if (player.getScoin() < cost) {
            player.sendPacket(new PacketExpUpRelicScRsp());
            return;
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

        while (expGain > 0 && reqExp > 0 && level < maxLevel) {
            // Do calculations
            int toGain = Math.min(expGain, reqExp - exp);
            exp += toGain;
            totalExp += toGain;
            expGain -= toGain;
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

        // TODO add back leftover exp
        List<GameItem> returnItems = new ArrayList<>();

        // Send packets
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
        player.sendPacket(new PacketExpUpRelicScRsp(returnItems));
    }

    // === Etc ===

    public void lockEquip(Player player, int equipId, boolean locked) {
        GameItem equip = player.getInventory().getItemByUid(equipId);

        if (equip == null || !equip.getExcel().isEquippable()) {
            return;
        }

        equip.setLocked(locked);
        equip.save();

        //  Send packet
        player.sendPacket(new PacketPlayerSyncScNotify(equip));
    }

    public void sellItems(Player player, List<ItemParam> items) {
        // Verify items
        var returnItems = new Int2IntOpenHashMap();

        for (ItemParam param : items) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.isLocked() || item.getCount() < param.getCount()) {
                player.sendPacket(new PacketSellItemScRsp(null));
                return;
            }

            // Add return items
            for (ItemParam ret : item.getExcel().getReturnItemIDList()) {
                // Add to return items
                returnItems.put(ret.getId(), returnItems.getOrDefault(ret.getId(), 0) + ret.getCount());
            }
        }

        // Delete items
        player.getInventory().removeItemsByParams(items);

        // Add return items
        for (var returnItem : returnItems.int2IntEntrySet()) {
            player.getInventory().addItem(returnItem.getIntKey(), returnItem.getIntValue());
        }

        // Send packet
        player.sendPacket(new PacketSellItemScRsp(returnItems));
    }
    
    public List<GameItem> buyShopGoods(Player player, int shopId, int goodsId, int goodsNum) {
        // Get shop and goods excels
        ShopExcel shop = GameData.getShopExcelMap().get(shopId);
        if (shop == null) return null;
        
        ShopGoodsExcel goods = shop.getGoods().get(goodsId);
        if (goods == null) return null;
        
        // Verify items
        for (ItemParam param : goods.getCostList()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount() * goodsNum) {
                return null;
            }
        }

        // Verify credits
        if (player.getScoin() < goods.getCoinCost() * goodsNum) {
            return null;
        }
        
        // Pay items
        player.getInventory().removeItemsByParams(goods.getCostList(), goodsNum);
        player.addSCoin(goods.getCoinCost() * goodsNum);
        
        // Buy items
        List<GameItem> items = new ArrayList<>();

        ItemExcel itemExcel = GameData.getItemExcelMap().get(goods.getItemID());
        if (!itemExcel.isEquippable()) {
            GameItem item = new GameItem(itemExcel, goods.getItemCount());
            items.add(item);
        } else {
            int num = goods.getItemCount() * goodsNum;
            for (int i = 0; i < num; i++) {
                GameItem item = new GameItem(itemExcel, 1);
                items.add(item);
            }
        }
        
        // Add to inventory
        player.getInventory().addItems(items);
        
        return items;
    }
    
    public List<GameItem> composeItem(Player player, int composeId, int count) {
        // Sanity check
        if (count <= 0) return null;
        
        // Get item compose excel data
        ItemComposeExcel excel = GameData.getItemComposeExcelMap().get(composeId);
        if (excel == null || excel.getFormulaType() != FormulaType.Normal) {
            return null;
        }
        
        // Verify items
        for (ItemParam param : excel.getMaterialCost()) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount() * count) {
                return null;
            }
        }

        // Verify credits
        if (player.getScoin() < excel.getCoinCost() * count) {
            return null;
        }
        
        // Pay items
        player.getInventory().removeItemsByParams(excel.getMaterialCost(), count);
        player.addSCoin(-excel.getCoinCost() * count);
        
        // Compose item
        List<GameItem> items = new ArrayList<>();
        GameItem item = new GameItem(excel.getItemID(), count);
        items.add(item);
        
        // Add items to inventory
        player.getInventory().addItems(items);
        
        return items;
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
        
        // Build cost items
        List<ItemParam> costItems = new ArrayList<>();
        costItems.addAll(excel.getMaterialCost());
        
        // Check main affix
        if (mainAffix > 0) {
            for (int specialId : excel.getSpecialMaterialCost()) {
                costItems.add(new ItemParam(specialId, 1));
            }
        }
        
        // Verify items
        for (ItemParam param : costItems) {
            GameItem item = player.getInventory().getItemByParam(param);
            if (item == null || item.getCount() < param.getCount() * count) {
                return null;
            }
        }

        // Verify credits
        if (player.getScoin() < excel.getCoinCost() * count) {
            return null;
        }
        
        // Pay items
        player.getInventory().removeItemsByParams(costItems, count);
        player.addSCoin(-excel.getCoinCost() * count);
        
        // Compose item
        List<GameItem> items = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            GameItem item = new GameItem(relicId, 1);
            
            if (mainAffix > 0) {
                item.setMainAffix(mainAffix);
            }
            
            items.add(item);
        }
        
        // Add items to inventory
        player.getInventory().addItems(items);
        
        return items;
    }
}
