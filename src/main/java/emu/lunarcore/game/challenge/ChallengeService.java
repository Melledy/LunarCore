package emu.lunarcore.game.challenge;

import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ChallengeExcel;
import emu.lunarcore.game.player.Player;
import emu.lunarcore.server.game.BaseGameService;
import emu.lunarcore.server.game.GameServer;
import emu.lunarcore.server.packet.send.PacketStartChallengeScRsp;

public class ChallengeService extends BaseGameService {

    public ChallengeService(GameServer server) {
        super(server);
    }

    public void startChallenge(Player player, int challengeId) {
        ChallengeExcel excel = GameData.getChallengeExcelMap().get(challengeId);
        if (excel == null) {
            player.sendPacket(new PacketStartChallengeScRsp());
            return;
        }
        
        // Sanity check lineups TODO
        
        // Enter scene
        boolean success = player.enterScene(excel.getMapEntranceID(), 0, false);
        if (success == false) {
            return;
        }
        
        // Set challenge data for player
        ChallengeData data = new ChallengeData(player, excel);
        
        player.setChallengeData(data);
        player.getLineupManager().setCurrentExtraLineup(data.getCurrentExtraLineup(), false);
        
        // Send packet
        player.sendPacket(new PacketStartChallengeScRsp(player, challengeId));
    }
}
