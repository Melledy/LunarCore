package emu.lunarcore.server.packet.recv;

import emu.lunarcore.data.GameData;
import emu.lunarcore.game.avatar.GameAvatar;
import emu.lunarcore.proto.SetAvatarEnhancedIdCsReqOuterClass.SetAvatarEnhancedIdCsReq;
import emu.lunarcore.server.game.GameSession;
import emu.lunarcore.server.packet.CmdId;
import emu.lunarcore.server.packet.Opcodes;
import emu.lunarcore.server.packet.PacketHandler;
import emu.lunarcore.server.packet.send.PacketPlayerSyncScNotify;
import emu.lunarcore.server.packet.send.PacketSetAvatarEnhancedIdScRsp;

@Opcodes(CmdId.SetAvatarEnhancedIdCsReq)
public class HandlerSetAvatarEnhancedIdCsReq extends PacketHandler {

    @Override
    public void handle(GameSession session, byte[] data) throws Exception {
        // Parse request
        var req = SetAvatarEnhancedIdCsReq.parseFrom(data);
        
        // Get avatar
        GameAvatar avatar = session.getPlayer().getAvatarById(req.getAvatarId());
        if (avatar == null) {
            session.send(new PacketSetAvatarEnhancedIdScRsp());
            return;
        }
        
        // Verify enhance excel if we are changing enhance state
        if (req.getAvatarEnhanceId() != 0) {
            var excel = GameData.getAvatarEnhanceExcelMap().get(req.getAvatarId(), req.getAvatarEnhanceId());
            if (excel == null) {
                session.send(new PacketSetAvatarEnhancedIdScRsp());
                return;
            }
        }
        
        // Set enhance id for avatar
        avatar.getData().setEnhanceId(req.getAvatarEnhanceId());
        avatar.save();
        
        // Sync and update on the client
        session.send(new PacketPlayerSyncScNotify(avatar));
        session.send(new PacketSetAvatarEnhancedIdScRsp(avatar));
    }

}
