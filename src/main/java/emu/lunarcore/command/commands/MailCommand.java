package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
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
        
        String content = String.join(" ", args.getList());
        Mail mail = new Mail("Test", "System Mail", content);
        
        args.getTarget().getMailbox().sendMail(mail);
        
        sender.sendMessage("Sending mail to " + args.getTarget().getName());
    }

}
