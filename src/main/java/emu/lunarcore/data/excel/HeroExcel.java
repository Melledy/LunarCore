package emu.lunarcore.data.excel;

import emu.lunarcore.data.GameResource;
import emu.lunarcore.data.ResourceType;
import emu.lunarcore.game.player.PlayerGender;
import lombok.Getter;

@Getter
@ResourceType(name = {"HeroConfig.json"})
public class HeroExcel extends GameResource {
    private int HeroAvatarID;
    private PlayerGender Gender;

    @Override
    public int getId() {
        return HeroAvatarID;
    }

}
