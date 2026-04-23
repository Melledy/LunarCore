package emu.lunarcore.data.excel;

import emu.lunarcore.data.resource.GameResource;
import emu.lunarcore.data.resource.ResourceType;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;

@Getter
@ResourceType(name = {"TextJoinConfig.json"})
public class TextJoinExcel extends GameResource {
    private int TextJoinID;
    private int DefaultItem;
    private IntArrayList TextJoinItemList;

    @Override
    public int getId() {
        return TextJoinID;
    }

    public IntArrayList getTextJoinItemList() {
        return TextJoinItemList;
    }
}
