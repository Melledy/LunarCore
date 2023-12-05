package emu.lunarcore.command;

public interface CommandHandler {
    
    public default Command getData() {
        return this.getClass().getAnnotation(Command.class);
    }
    
    public default String getLabel() {
        return getData().label();
    }
    
    public void execute(CommandArgs args);
    
}
