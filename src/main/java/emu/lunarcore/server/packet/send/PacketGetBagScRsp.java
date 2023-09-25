package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.inventory.InventoryTab;
import emu.lunarcore.game.inventory.ItemMainType;
import emu.lunarcore.proto.GetBagScRspOuterClass.GetBagScRsp;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetBagScRsp extends BasePacket {

    public PacketGetBagScRsp(GameSession session) {
        super(CmdId.GetBagScRsp);

        var data = GetBagScRsp.newInstance();

        InventoryTab tabMaterial = session.getPlayer().getInventory().getInventoryTab(ItemMainType.Material);
        for (GameItem item : tabMaterial) {
            data.addMaterialList(item.toMaterialProto());
        }

        InventoryTab tabRelic = session.getPlayer().getInventory().getInventoryTab(ItemMainType.Relic);
        for (GameItem item : tabRelic) {
            data.addRelicList(item.toRelicProto());
        }

        InventoryTab tabEquipment = session.getPlayer().getInventory().getInventoryTab(ItemMainType.Equipment);
        for (GameItem item : tabEquipment) {
            data.addEquipmentList(item.toEquipmentProto());
        }

        this.setData(data);
    }
}
