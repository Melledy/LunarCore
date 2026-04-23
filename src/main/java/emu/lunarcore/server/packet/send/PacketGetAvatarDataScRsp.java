package emu.lunarcore.server.packet.send;

import emu.lunarcore.game.avatar.AvatarMultiPath;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GetAvatarDataScRspOuterClass.GetAvatarDataScRsp;
import emu.lunarcore.server.packet.BasePacket;
import emu.lunarcore.server.packet.CmdId;

public class PacketGetAvatarDataScRsp extends BasePacket {

    public PacketGetAvatarDataScRsp(Player player) {
        super(CmdId.GetAvatarDataScRsp);

        var data = GetAvatarDataScRsp.newInstance()
                .setIsGetAll(true);

        for (GameAvatar avatar : player.getAvatars()) {
            var avatarProto = avatar.toProto();
            
            if (!avatar.hasMultiPath()) {
                // Add unique info
                data.addAvatarPathInfoList(avatar.toPathInfoProto());
            }
            
            data.addAvatarList(avatarProto);
        }
        
        for (AvatarMultiPath path : player.getAvatars().getMultiPaths().values()) {
            data.addAvatarPathInfoList(path.toPathInfoProto());
            data.addBasicTypeIdList(path.getExcelId());
        }
        
        // Avatar skins
        for (int skinId : player.getUnlocks().getAvatarSkins()) {
            data.addOwnedSkinList(skinId);
        }
        
        // Set player outfit data
        data.getMutablePlayerOutfitData();

        this.setData(data);
    }

}