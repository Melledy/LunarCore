package emu.lunarcore.game.inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import emu.lunarcore.GameConstants;
import emu.lunarcore.LunarCore;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.common.ItemParam;
import emu.lunarcore.data.common.ItemParam.ItemParamType;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.avatar.AvatarStorage;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.BasePlayerManager;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.server.packet.send.PacketScenePlaneEventScNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

public class Inventory extends BasePlayerManager {
    private final Long2ObjectMap<GameItem> store;
    private final Int2ObjectMap<InventoryTab> inventoryTypes;
    private int nextInternalUid;

    public Inventory(Player player) {
        super(player);

        this.store = new Long2ObjectOpenHashMap<>();
        this.inventoryTypes = new Int2ObjectOpenHashMap<>();

        this.createInventoryTab(ItemMainType.Equipment, new EquipInventoryTab(1500));
        this.createInventoryTab(ItemMainType.Relic, new EquipInventoryTab(1500));
        this.createInventoryTab(ItemMainType.Material, new MaterialInventoryTab(2000));
    }

    public AvatarStorage getAvatarStorage() {
        return this.getPlayer().getAvatars();
    }

    public Long2ObjectMap<GameItem> getItems() {
        return store;
    }

    public Int2ObjectMap<InventoryTab> getInventoryTypes() {
        return inventoryTypes;
    }

    public InventoryTab getInventoryTab(ItemMainType type) {
        return getInventoryTypes().get(type.getVal());
    }

    public void createInventoryTab(ItemMainType type, InventoryTab tab) {
        this.getInventoryTypes().put(type.getVal(), tab);
    }

    public int getNextItemInternalUid() {
        return ++nextInternalUid;
    }

    /* Returns an item using its internal uid
     * */
    public synchronized GameItem getItemByUid(int uid) {
        return this.getItems().get(uid);
    }

    public synchronized GameItem getMaterialByItemId(int id) {
        return this.getInventoryTab(ItemMainType.Material).getItemById(id);
    }

    public GameItem getItemByParam(ItemParam param) {
        if (param.getType() == ItemParamType.PILE) {
            return this.getMaterialByItemId(param.getId());
        } else if (param.getType() == ItemParamType.UNIQUE) {
            return this.getItemByUid(param.getId());
        }

        return null;
    }
    
    // Add/Remove items

    public boolean addItem(int itemId, int count) {
        ItemExcel itemExcel = GameData.getItemExcelMap().get(itemId);

        if (itemExcel == null) {
            return false;
        }
        
        return addItem(itemExcel, count);
    }
    
    public boolean addItem(ItemExcel itemExcel, int count) {
        GameItem item = new GameItem(itemExcel, count);
        return addItem(item);
    }

    public boolean addItem(GameItem item) {
        GameItem result = putItem(item);

        if (result != null) {
            getPlayer().sendPacket(new PacketPlayerSyncScNotify(result));
            return true;
        }

        return false;
    }
    
    public List<GameItem> addItems(Collection<GameItem> items) {
        return addItems(items, false);
    }
    
    public List<GameItem> addItems(Collection<GameItem> items, boolean showHint) {
        // Init results
        List<GameItem> results = new ArrayList<>(items.size());
        
        // Sanity
        if (items.size() == 0) {
            return results;
        }
        
        // Add to inventory
        for (GameItem item : items) {
            GameItem result = putItem(item);
            if (result != null) {
                results.add(result);
            }
        }
        
        // Send packet (update)
        if (results.size() > 0) {
            getPlayer().sendPacket(new PacketPlayerSyncScNotify(results));
            if (showHint) {
                getPlayer().sendPacket(new PacketScenePlaneEventScNotify(items));
            }
        }
        
        return results;
    }

    private synchronized GameItem putItem(GameItem item) {
        // Dont add items that dont have a valid item definition.
        if (item.getExcel() == null) {
            return null;
        }

        // Add item to inventory store
        ItemMainType type = item.getExcel().getItemMainType();
        ItemSubType subType = item.getExcel().getItemSubType();
        InventoryTab tab = getInventoryTab(type);

        // Add
        switch (type) {
        case Equipment:
        case Relic:
            if (tab.getSize() >= tab.getMaxCapacity()) {
                return null;
            }
            // Duplicates cause problems
            item.setCount(1);
            // Adds to inventory
            this.putItem(item, tab);
            // Set ownership and save to database
            item.save();
            return item;
        case Virtual:
            // Handle
            this.addVirtualItem(item.getItemId(), item.getCount());
            return item;
        case AvatarCard:
            // Add avatar
            AvatarExcel avatarExcel = GameData.getAvatarExcelMap().get(item.getItemId());
            if (avatarExcel != null && !getPlayer().getAvatars().hasAvatar(avatarExcel.getId())) {
                getPlayer().addAvatar(new GameAvatar(avatarExcel));
            }
            return null;
        case Usable:
            if (subType == ItemSubType.HeadIcon) {
                getPlayer().addHeadIcon(item.getItemId());
                return item;
            }
            return null;
        case Material:
            if (tab == null) {
                return null;
            }
            
            GameItem existingItem = tab.getItemById(item.getItemId());
            
            if (existingItem == null) {
                // Item type didnt exist before, we will add it to main inventory map if there is enough space
                if (tab.getSize() >= tab.getMaxCapacity()) {
                    return null;
                }
                // Put item to inventory
                this.putItem(item, tab);
                // Set ownership and save to db
                item.save();
                return item;
            } else {
                // Add count
                existingItem.setCount(Math.min(existingItem.getCount() + item.getCount(), item.getExcel().getPileLimit()));
                existingItem.save();
                return existingItem;
            }
        default:
            return null;
        }
    }

    private synchronized void putItem(GameItem item, InventoryTab tab) {
        // Set owner and internal uid first
        item.setOwner(this.getPlayer());

        // Add if tab exists
        if (tab != null) {
            // Put in item store
            getItems().put(item.getInternalUid(), item);
            // Add to tab
            tab.onAddItem(item);
        }
    }

    private void addVirtualItem(int itemId, int count) {
        switch (itemId) {
        case 1: // Stellar Jade
            getPlayer().addHCoin(count);
            break;
        case 2: // Credit
            getPlayer().addSCoin(count);
            break;
        case 3: // Oneiric Shard
            getPlayer().addMCoin(count);
            break;
        case 11: // Trailblaze Power
            getPlayer().addStamina(count);
            break;
        case 22: // Trailblaze EXP
            getPlayer().addExp(count);
            break;
        case GameConstants.ROGUE_TALENT_POINT_ITEM_ID: // Rogue talent points
            getPlayer().addTalentPoints(count);
            break;
        }
    }
    
    public void removeItemsByParams(Collection<ItemParam> items) {
        removeItemsByParams(items, 1);
    }

    public void removeItemsByParams(Collection<ItemParam> items, int multiplier) {
        // Sanity
        if (items.size() == 0) {
            return;
        }
        
        // Init results and remove items from inventory
        List<GameItem> results = new ArrayList<GameItem>(items.size());
        
        for (ItemParam param : items) {
            // Check param type
            if (param.getId() == GameConstants.MATERIAL_COIN_ID) {
                // Remove credits
                getPlayer().addSCoin(-param.getCount() * multiplier);
            } else if (param.getId() == GameConstants.ROGUE_TALENT_POINT_ITEM_ID) {
                // Remove credits
                getPlayer().addTalentPoints(-param.getCount() * multiplier);
            } else {
                // Remove param items
                GameItem item = this.getItemByParam(param);
                if (item == null) continue;
                
                GameItem result = this.deleteItem(item, param.getCount() * multiplier);
                if (result != null) {
                    results.add(result);
                }
            }
        }
        
        // Send packet (update)
        if (results.size() > 0) {
            getPlayer().sendPacket(new PacketPlayerSyncScNotify(results));
        }
    }
    
    public void removeItems(Collection<GameItem> items) {
        // Sanity
        if (items.size() == 0) {
            return;
        }
        
        // Init results and remove items from inventory
        List<GameItem> results = new ArrayList<GameItem>(items.size());
        
        for (GameItem item : items) {
            GameItem result = deleteItem(item, item.getCount());
            if (result != null) {
                results.add(result);
            }
        }
        
        // Send packet (update)
        if (results.size() > 0) {
            getPlayer().sendPacket(new PacketPlayerSyncScNotify(results));
        }
    }

    public synchronized boolean removePileItem(int uid, int count) {
        GameItem item = this.getMaterialByItemId(uid);

        if (item == null) {
            return false;
        }

        return removeItem(item, count);
    }

    public synchronized boolean removeUniqueItem(int uid, int count) {
        GameItem item = this.getItemByUid(uid);

        if (item == null) {
            return false;
        }

        return removeItem(item, count);
    }
    
    public synchronized boolean removeItem(GameItem item, int count) {
        GameItem result = deleteItem(item, count);
        
        if (result != null) {
            getPlayer().sendPacket(new PacketPlayerSyncScNotify(result));
            return true;
        }
        
        return false;
    }

    private synchronized GameItem deleteItem(GameItem item, int count) {
        // Sanity check
        if (count <= 0 || item == null || item.getOwnerUid() != getPlayer().getUid()) {
            return null;
        }

        if (item.getExcel() == null || item.getExcel().isEquippable()) {
            item.setCount(0);
        } else {
            item.setCount(item.getCount() - count);
        }

        if (item.getCount() <= 0) {
            // Remove from inventory tab too
            InventoryTab tab = null;
            if (item.getExcel() != null) {
                tab = getInventoryTab(item.getExcel().getItemMainType());
                
                if (tab != null) {
                    tab.onRemoveItem(item);
                }
            }
            // Remove from items map
            getItems().remove(item.getInternalUid());
        }

        // Update in db
        item.save();

        // Returns true on success
        return item;
    }
    
    // Verifying items
    
    public boolean verifyItems(Collection<ItemParam> params) {
        return verifyItems(params, 1);
    }
    
    public boolean verifyItems(Collection<ItemParam> params, int multiplier) {
        for (ItemParam param : params) {
            // Check param type
            if (param.getId() == GameConstants.MATERIAL_COIN_ID) {
                // Check credits
                if (!verifyScoin(param.getCount() * multiplier)) {
                    return false;
                }
            } else if (param.getId() == GameConstants.ROGUE_TALENT_POINT_ITEM_ID) {
                return this.getPlayer().getTalentPoints() >= param.getCount() * multiplier;
            } else {
                // Check param items
                GameItem item = this.getItemByParam(param);
                if (item == null || item.getCount() < param.getCount() * multiplier) {
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public boolean verifyScoin(int cost) {
        return this.getPlayer().getScoin() >= cost;
    }

    // Equips

    public boolean equipItem(int avatarId, int equipId) {
        GameAvatar avatar = getPlayer().getAvatarById(avatarId);
        GameItem item = this.getItemByUid(equipId);

        if (avatar != null && item != null) {
            return avatar.equipItem(item);
        }

        return false;
    }

    public boolean unequipItem(int avatarId, int slot) {
        GameAvatar avatar = getPlayer().getAvatars().getAvatarById(avatarId);

        if (avatar != null) {
            GameItem unequipped = avatar.unequipItem(slot);
            if (unequipped != null) {
                getPlayer().sendPacket(new PacketPlayerSyncScNotify(avatar, unequipped));
                return true;
            }
        }

        return false;
    }

    // Database

    public void loadFromDatabase() {
        Stream<GameItem> stream = LunarCore.getGameDatabase().getObjects(GameItem.class, "ownerUid", this.getPlayer().getUid());

        stream.forEach(item -> {
            // Should never happen
            if (item.getId() == null) {
                return;
            }

            // Load item excel data
            ItemExcel excel = GameData.getItemExcelMap().get(item.getItemId());
            if (excel == null) {
                // Delete item if it has no excel data
                item.setCount(0);
                item.save();
                return;
            }

            // Set ownerships
            item.setExcel(excel);

            // Put in inventory
            InventoryTab tab = getInventoryTab(item.getExcel().getItemMainType());
            putItem(item, tab);

            // Equip to a character if possible
            if (item.isEquipped()) {
                GameAvatar avatar = getPlayer().getAvatarById(item.getEquipAvatar());
                boolean hasEquipped = false;

                if (avatar != null) {
                    hasEquipped = avatar.equipItem(item);
                }

                if (!hasEquipped) {
                    // Unset equipped flag on item since we couldnt find an avatar to equip it to
                    item.setEquipAvatar(0);
                    item.save();
                }
            }
        });
    }
}
