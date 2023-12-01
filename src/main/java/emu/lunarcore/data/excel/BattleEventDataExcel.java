package emu.lunarcore.data.excel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.rogue.RogueBuffType;
import emu.lunarcore.util.Utils;
import lombok.Getter;

@Getter
@ResourceType(name = {"BattleEventData.json"})
public class BattleEventDataExcel extends GameResource {
    private int BattleEventID;
    private String Config;

    private static final Pattern roguePattern = Pattern.compile("(?<=Avatar_RogueBattleevent)(.*?)(?=_Config.json)");

    @Override
    public int getId() {
        return BattleEventID;
    }

    @Override
    public void onLoad() {
        try {
            Matcher matcher = roguePattern.matcher(this.Config);

            if (matcher.find()) {
                int rogueBuffType = Utils.parseSafeInt(matcher.group(0));
                var type = RogueBuffType.getById(rogueBuffType);

                if (type != null) {
                    type.setBattleEventSkill(this.BattleEventID);
                }
            }
        } catch (Exception e) {
            // Ignored
        }
    }
}
