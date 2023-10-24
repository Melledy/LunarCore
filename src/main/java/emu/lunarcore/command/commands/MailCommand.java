package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.mail.Mail;
import emu.lunarcore.game.player.Player;

@Command(label = "mail", aliases = {"m"}, permission = "player.mail", desc = "/mail [content]. Sends the targeted player a system mail.")
public class MailCommand implements CommandHandler {

    @Override
    public void execute(Player sender, CommandArgs args) {
        // Check target
        if (args.getTarget() == null) {
            this.sendMessage(sender, "Error: Targeted player not found or offline");
            return;
        }
        
        // Get attachments
        List<GameItem> attachments = new ArrayList<>();
        
        var it = args.getList().iterator();
        while (it.hasNext()) {
            try {
                String str = it.next();
                
                if (str.contains(":")) {
                    String[] split = str.split(":");
                    
                    int itemId = Integer.parseInt(split[0]);
                    int count = Integer.parseInt(split[1]);
                    
                    attachments.add(new GameItem(itemId, count));
                    
                    it.remove();
                }
            } catch (Exception e) {
                
            }
        }
        
        // Build mail
        String content = String.join(" ", args.getList());
        Mail mail = new Mail("Test", "System Mail", content);
        
        for (GameItem item : attachments) {
            mail.addAttachment(item);
        }
        
        // Send to target
        args.getTarget().getMailbox().sendMail(mail);
        
        this.sendMessage(sender, "Sending mail to " + args.getTarget().getName());
    }

}
