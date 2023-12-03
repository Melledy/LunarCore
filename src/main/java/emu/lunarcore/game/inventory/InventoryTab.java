package emu.lunarcore.game.inventory;

public abstract class InventoryTab implements Iterable<GameItem>  {
    public abstract GameItem getItemById(int id);

    public abstract void onAddItem(GameItem item);

    public abstract void onRemoveItem(GameItem item);

    public abstract int getSize();

    public abstract int getMaxCapacity();

    public int getAvailableCapacity() {
        return Math.max(getMaxCapacity() - getSize(), 0);
    }
}
