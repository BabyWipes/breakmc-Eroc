package com.breakmc.eroc.automessage;

import org.bukkit.command.*;
import javax.annotation.*;
import org.apache.commons.lang.*;
import org.bukkit.*;
import java.util.*;
import com.breakmc.eroc.zeus.annotations.*;

public class AutoMessage
{
    @Command(name = "automessage", aliases = { "am", "autom" }, permission = "eroc.automessage", permissionMsg = "§cPermission Denied.", minArgs = 1, usage = "§c/<command> [add,remove,view,prefix] [args...]")
    public void automessage(@Nonnull final CommandSender sender, final String[] args) {
        if (args[0].equalsIgnoreCase("add")) {
            if (args.length == 1) {
                sender.sendMessage("§c/automessage add <message>");
                return;
            }
            final String message = ChatColor.translateAlternateColorCodes('&', StringUtils.join((Object[])args, " ", 1, args.length).trim());
            AutoMessageManager.getInstance().addMessage(message);
            sender.sendMessage("§cMessage added!");
        }
        else {
            if (!args[0].equalsIgnoreCase("view")) {
                if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length > 2 || args.length == 1) {
                        sender.sendMessage("§c/automessage remove <message id>");
                        return;
                    }
                    try {
                        final int index = Integer.valueOf(args[1]);
                        AutoMessageManager.getInstance().getMessages().remove(index);
                        sender.sendMessage("§aMessage has been removed!");
                        return;
                    }
                    catch (IndexOutOfBoundsException e) {
                        sender.sendMessage("§cCould not find any message at index: " + args[1]);
                        return;
                    }
                    catch (NumberFormatException e2) {
                        sender.sendMessage("§cArgument must be a integer!");
                        return;
                    }
                }
                if (!args[0].equalsIgnoreCase("prefix")) {
                    sender.sendMessage("§c/automessage [add,remove,view,prefix] [args...]");
                    return;
                }
                if (args.length > 2 || args.length == 1) {
                    sender.sendMessage("§c/automessage prefix <newPrefix>");
                    return;
                }
                final String newPrefix = ChatColor.translateAlternateColorCodes('&', args[1]);
                AutoMessageManager.getInstance().setPrefix(newPrefix);
                sender.sendMessage("§aPrefix set to: " + newPrefix);
                return;
            }
            if (AutoMessageManager.getInstance().getMessages().size() <= 0) {
                sender.sendMessage("§cThere are no messages!");
                return;
            }
            sender.sendMessage("§eThere are §6" + AutoMessageManager.getInstance().getMessages().size() + "§e messages.");
            sender.sendMessage("§eThe prefix is: " + AutoMessageManager.getInstance().getPrefix());
            int count = 0;
            for (final Message message2 : AutoMessageManager.getInstance().getMessages()) {
                sender.sendMessage("§eID: " + count + ", Message: " + message2.getMessage());
                sender.sendMessage("");
                ++count;
            }
        }
    }
}
