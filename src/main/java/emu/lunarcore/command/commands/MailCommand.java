package emu.lunarcore.command.commands;

import java.util.ArrayList;
import java.util.List;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.data.GameData;
import emu.lunarcore.data.excel.ItemExcel;
import emu.lunarcore.game.inventory.GameItem;
import emu.lunarcore.game.mail.Mail;

@Command(label = "mail", aliases = {"m"}, permission = "player.mail", requireTarget = true, desc = "/mail [content]. Sends the targeted player a system mail.")
public class MailCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        // Get attachments
        List<GameItem> attachments = new ArrayList<>();

        if (args.getMap() != null) {
            for (var entry : args.getMap().int2IntEntrySet()) {
                if (entry.getIntValue() <= 0) continue;
                
                ItemExcel itemExcel = GameData.getItemExcelMap().get(entry.getIntKey());
                if (itemExcel == null) continue;
                
                attachments.add(new GameItem(itemExcel, entry.getIntValue()));
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
        
        args.sendMessage("Sending mail to " + args.getTarget().getName());
    }

}
