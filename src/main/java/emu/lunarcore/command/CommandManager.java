package emu.lunarcore.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import emu.lunarcore.LunarRail;
import emu.lunarcore.game.player.Player;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(AccessLevel.PRIVATE)
public class CommandManager {
    private Object2ObjectMap<String, CommandHandler> labels;
    private Object2ObjectMap<String, CommandHandler> commands;
    
    public CommandManager() {
        this.labels = new Object2ObjectOpenHashMap<>();
        this.commands = new Object2ObjectOpenHashMap<>();
        
        // Scan for commands
        var commandClasses = new Reflections(CommandManager.class.getPackageName()).getTypesAnnotatedWith(Command.class);
        
        for (var cls : commandClasses) {
            if (!CommandHandler.class.isAssignableFrom(cls)) {
                continue;
            }
            
            try {
                CommandHandler handler = (CommandHandler) cls.getDeclaredConstructor().newInstance();
                this.registerCommand(handler);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public CommandManager registerCommand(CommandHandler handler) {
        Command command = handler.getClass().getAnnotation(Command.class);
        if (command == null) {
            return this;
        }
        
        this.getLabels().put(command.label(), handler);
        this.getCommands().put(command.label(), handler);
        
        for (String alias : command.aliases()) {
            this.getCommands().put(alias, handler);
        }
        
        return this;
    }
    
    public CommandManager unregisterCommand(String label) {
        CommandHandler handler = this.getLabels().get(label);
        if (handler == null) {
            return this;
        }
        
        Command command = handler.getClass().getAnnotation(Command.class);
        if (command == null) {
            return this;
        }
        
        this.getLabels().remove(command.label());
        this.getCommands().remove(command.label());
        
        for (String alias : command.aliases()) {
            this.getCommands().remove(alias);
        }
        
        return this;
    }
    
    private boolean checkPermission(Player sender, Command command) {
        if (sender == null || command.permission().isEmpty()) {
            return true;
        }
        
        return sender.getAccount().hasPermission(command.permission());
    }
    
    private boolean checkTargetPermission(Player sender, Command command) {
        if (sender == null || command.permission().isEmpty()) {
            return true;
        }
        
        return sender.getAccount().hasPermission("target." + command.permission());
    }
    
    public void invoke(Player sender, String message) {
        List<String> args = Arrays.stream(message.split(" ")).collect(Collectors.toCollection(ArrayList::new));
        
        // Get command label
        String label = args.remove(0).toLowerCase();
        
        if (label.startsWith("/") || label.startsWith("!")) {
            label = label.substring(1);
        }
        
        // Get handler
        CommandHandler handler = this.commands.get(label);

        // Execute
        if (handler != null) {
            // Command annotation data
            Command command = handler.getClass().getAnnotation(Command.class);
            // Check permission
            if (this.checkPermission(sender, command)) {
                // Check targetted permission
                CommandArgs cmdArgs = new CommandArgs(sender, args);
                if (sender != cmdArgs.getTarget() && !this.checkTargetPermission(sender, command)) {
                    handler.sendMessage(sender, "You do not have permission to use this command on another player");
                    return;
                }
                // Run command
                handler.execute(sender, cmdArgs);
            } else {
                handler.sendMessage(sender, "You do not have permission to use this command");
            }
        } else {
            if (sender != null) {
                sender.sendMessage("Inavlid Command!");
            } else {
                LunarRail.getLogger().info("Inavlid Command!");
            }
        }
    }
}
