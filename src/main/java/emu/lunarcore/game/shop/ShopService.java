package emu.lunarcore.game.shop;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.data.excel.ShopExcel;
import emu.lunarcore.data.excel.ShopGoodsExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;

public class ShopService extends BaseGameService {

    public ShopService(GameServer server) {
        super(server);
    }

    public List<GameItem> buyGoods(Player player, int shopId, int goodsId, int count) {
        // Get shop and goods excels
        ShopExcel shop = GameData.getShopExcelMap().get(shopId);
        if (shop == null) return null;
        
        ShopGoodsExcel goods = shop.getGoods().get(goodsId);
        if (goods == null) return null;
        
        ItemExcel itemExcel = GameData.getItemExcelMap().get(goods.getItemID());
        if (itemExcel == null) return null;
        
        // Verify item params
        if (!player.getInventory().verifyItems(goods.getCostList(), count)) {
            return null;
        }
        
        // Handle payment
        player.getInventory().removeItemsByParams(goods.getCostList(), count);
        
        // Buy items
        List<GameItem> items = new ArrayList<>();

        if (!itemExcel.isEquippable()) {
            GameItem item = new GameItem(itemExcel, goods.getItemCount() * count);
            items.add(item);
        } else {
            int num = goods.getItemCount() * count;
            for (int i = 0; i < num; i++) {
                GameItem item = new GameItem(itemExcel, 1);
                items.add(item);
            }
        }
        
        // Add to inventory
        player.getInventory().addItems(items);
        
        return items;
    }
}
