package emu.lunarcore.game.gacha;

import emu.lunarcore.game.player.Player;
import emu.lunarcore.proto.GachaInfoOuterClass.GachaInfo;
import lombok.Getter;

@Getter
public class GachaBanner {
    private int id; // Id should match one of the ids in GachaBasicInfo.json
    private GachaType gachaType;
    private int beginTime;
    private int endTime;
    private int[] rateUpItems5;
    private int[] rateUpItems4;
    private int eventChance = 50;

    public GachaInfo toProto(GachaService service, Player player) {
        var info = GachaInfo.newInstance()
                .setGachaId(this.getId())
                .setDetailUrl("")
                .setHistoryUrl("");

        if (this.gachaType != GachaType.Normal) {
            info.setBeginTime(this.getBeginTime());
            info.setEndTime(this.getEndTime());
        }
        
        if (this.getId() == 1001) {
            info.setGachaCeiling(player.getGachaInfo().toGachaCeiling(player));
            
            info.addAllUpInfo(service.getPurpleAvatars());
            info.addAllUpInfo(service.getYellowAvatars());
            info.addAllUpInfo(service.getPurpleWeapons());
            info.addAllUpInfo(service.getYellowWeapons());
            
            info.addAllFeatured(service.getDefaultFeaturedIds());
        } else {
            if (this.getRateUpItems4().length > 0) {
                info.addAllUpInfo(getRateUpItems4());
            }

            if (this.getRateUpItems5().length > 0) {
                info.addAllUpInfo(getRateUpItems5());
                info.addAllFeatured(getRateUpItems5());
            }
        }

        return info;
    }
}
