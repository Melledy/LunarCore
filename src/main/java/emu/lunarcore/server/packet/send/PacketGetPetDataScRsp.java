package emu.lunarcore.server.packet.send;

import java.util.ArrayList;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.enums.ItemMainType;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetPetDataScRspOuterClass.GetPetDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetPetDataScRsp extends BasePacket {

    public PacketGetPetDataScRsp(Player player) {
        super(CmdId.GetPetDataScRsp);
        
        var data = GetPetDataScRsp.newInstance()
                .setCurPetId(player.getPetId());
        
        ArrayList<Integer> petItemIdList = new ArrayList<>();
        player.getInventory().getItems().values().stream()
            .filter(item -> item.getItemMainType() == ItemMainType.Pet)
            .forEach(item -> petItemIdList.add(item.getItemId()));
        
        for (var excel: GameData.getPetExcelMap().values()) {
            if (petItemIdList.contains(excel.getPetItemID())) {
                data.addPetIdList(excel.getPetID());
            }
        }
        
        this.setData(data);
    }
}
