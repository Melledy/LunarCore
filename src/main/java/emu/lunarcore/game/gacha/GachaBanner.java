package emu.lunarcore.game.gacha;

import emu.lunarcore.proto.GachaCeilingOuterClass.GachaCeiling;
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

    public GachaInfo toProto() {
        var info = GachaInfo.newInstance()
                .setGachaId(this.getId())
                .setDetailUrl("")
                .setHistoryUrl("");

        if (this.gachaType == GachaType.Normal) {
            // Gacha ceiling
            info.setGachaCeiling(GachaCeiling.newInstance());
        } else {
            info.setBeginTime(this.getBeginTime());
            info.setEndTime(this.getEndTime());
        }

        if (this.getRateUpItems4().length > 0) {
            for (int id : getRateUpItems4()) {
                info.addUpInfo(id);
            }
        }

        if (this.getRateUpItems5().length > 0) {
            for (int id : getRateUpItems5()) {
                info.addUpInfo(id);
                info.addFeatured(id);
            }
        }

        return info;
    }
}
