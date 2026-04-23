package emu.lunarcore.game.avatar;

import org.bson.types.ObjectId;

import emu.lunarcore.GameConstants;
import emu.lunarcore.data.excel.AvatarExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.game.Syncable;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

public abstract class BaseAvatar implements Syncable {
    // Properties
    
    public abstract ObjectId getId();
    
    public abstract Player getOwner();
    
    public abstract AvatarData getData();
    
    public abstract int getEnhanceId();
    
    // Excels
    
    public abstract int getExcelId();
    
    public abstract AvatarExcel getExcel();
    
    // Equip handlers
    
    public Int2ObjectMap<GameItem> getEquips() {
        return this.getData().getEquips();
    }
    
    public GameItem getEquipBySlot(int slot) {
        return this.getEquips().get(slot);
    }
    
    public GameItem getEquipment() {
        return this.getEquips().get(GameConstants.EQUIPMENT_SLOT_ID);
    }
    
    public boolean equipItem(GameItem item) {
        // Sanity check
        int slot = item.getEquipSlot();
        if (slot == 0) return false;

        // Check if other avatars have this item equipped
        BaseAvatar otherAvatar = item.getEquipAvatar();
        if (otherAvatar != null) {
            // Unequip this item from the other avatar
            if (otherAvatar.unequipItem(slot) != null) {
                otherAvatar.getOwner().sendPacket(new PacketPlayerSyncScNotify(otherAvatar));
            }
            // Swap with other avatar
            if (getEquips().containsKey(slot)) {
                GameItem toSwap = this.getEquipBySlot(slot);
                otherAvatar.equipItem(toSwap);
            }
        } else if (getEquips().containsKey(slot)) {
            // Unequip item in current slot if it exists
            GameItem unequipped = unequipItem(slot);
            if (unequipped != null) {
                getOwner().sendPacket(new PacketPlayerSyncScNotify(unequipped));
            }
        }

        // Set equip
        getEquips().put(slot, item);

        // Save equip if equipped avatar was changed
        if (item.setEquipAvatar(this)) {
            item.save();
        }

        // Send packet
        getOwner().sendPacket(new PacketPlayerSyncScNotify(this, item));
        
        return true;
    }

    public GameItem unequipItem(int slot) {
        GameItem item = getEquips().remove(slot);

        if (item != null) {
            item.setEquipAvatar(null);
            item.save();
            return item;
        }

        return null;
    }
}
