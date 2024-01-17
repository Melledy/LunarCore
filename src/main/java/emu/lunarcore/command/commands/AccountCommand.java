package emu.lunarcore.command.commands;

import emu.lunarcore.command.Command;
import emu.lunarcore.command.CommandArgs;
import emu.lunarcore.command.CommandHandler;
import emu.lunarcore.game.account.Account;
import emu.lunarcore.game.account.AccountHelper;
import emu.lunarcore.util.Utils;

@Command(label = "account", permission = "admin.account", desc = "/account {create | delete} [username] (reserved player uid). Creates or deletes an account.")
public class AccountCommand implements CommandHandler {

    @Override
    public void execute(CommandArgs args) {
        if (args.size() < 2) {
            args.sendMessage("Invalid amount of args");
            return;
        }

        String command = args.get(0).toLowerCase();
        String username = args.get(1);

        switch (command) {
            case "create" -> {
                // Reserved player uid
                int reservedUid = 0;

                if (args.size() >= 3) {
                    reservedUid = Utils.parseSafeInt(args.get(2));
                }

                if (Account.AccountHelper.createAccount(username, null, reservedUid) != null) {
                    args.sendMessage("Account created");
                } else {
                    args.sendMessage("Account already exists");
                }
            }
            case "delete" -> {
                if (Account.AccountHelper.deleteAccount(username)) {
                    args.sendMessage("Account deleted");
                } else {
                    args.sendMessage("Account doesnt exist");
                }
            }
        }
    }

}
