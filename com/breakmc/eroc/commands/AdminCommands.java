package com.breakmc.eroc.commands;

import java.util.*;
import org.bukkit.command.*;
import com.breakmc.eroc.zeus.annotations.*;
import com.breakmc.eroc.utils.*;
import org.bukkit.entity.*;
import org.apache.commons.lang.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class AdminCommands implements Listener
{
    boolean chatMuted;
    boolean slowChat;
    ArrayList<String> inStaffChat;
    private final Map<String, Long> times;
    
    public AdminCommands() {
        super();
        this.chatMuted = false;
        this.slowChat = false;
        this.inStaffChat = new ArrayList<String>();
        this.times = new HashMap<String, Long>();
    }
    
    @Command(name = "clearchat", aliases = { "cc" }, permission = "eroc.clearchat")
    public void clearChat(final CommandSender sender, final String[] args) {
        for (int i = 0; i < 120; ++i) {
            Bukkit.broadcastMessage("");
        }
        Bukkit.broadcastMessage("Chat has been cleared by " + sender.getName() + ".");
    }
    
    @Command(name = "mutechat", aliases = { "mc", "mutec" }, permission = "eroc.mutechat")
    public void muteChat(final CommandSender sender, final String[] args) {
        if (this.chatMuted) {
            Common.broadcast("§cGlobal chat is no longer muted.", "eroc.mutechat.*", true);
            Common.broadcast("§cGlobal chat is no longer muted. [" + sender.getName() + "§c]", "eroc.mutechat.*", false);
            this.chatMuted = false;
        }
        else {
            Common.broadcast("§cGlobal chat has been muted.", "eroc.mutechat.*", true);
            Common.broadcast("§cGlobal chat has been muted. [" + sender.getName() + "§c]", "eroc.mutechat.*", false);
            this.chatMuted = true;
        }
    }
    
    @Command(name = "staffchat", aliases = { "sc" }, permission = "eroc.staffchat", usage = "§cUsage: /<command> [message]")
    public void staffchat(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            return;
        }
        if (args.length == 0) {
            if (this.inStaffChat.contains(sender.getName())) {
                this.inStaffChat.remove(sender.getName());
                sender.sendMessage("§aYou are no longer speaking in §2staff §achannel.");
            }
            else {
                this.inStaffChat.add(sender.getName());
                sender.sendMessage("§aYou are now speaking in §2staff §achannel.");
            }
            return;
        }
        Bukkit.broadcast("§b[Staff] " + sender.getName() + ": §f" + StringUtils.join((Object[])args, " ").trim(), "eroc.staffchat");
    }
    
    @Command(name = "slowchat", permission = "eroc.slowchat", usage = "§cUsage: /<command>")
    public void slowChat(final CommandSender sender, final String[] args) {
        if (this.slowChat) {
            Common.broadcast("§cChat is no longer slowed.", "eroc.slowchat.*", true);
            Common.broadcast("§cChat is no longer slowed. [" + sender.getName() + "§c]", "eroc.slowchat.*", false);
            this.slowChat = false;
        }
        else {
            Common.broadcast("§cChat is now slowed for 10 seconds.", "eroc.slowchat.*", this.slowChat = true);
            Common.broadcast("§cChat is now slowed for 10 seconds. [" + sender.getName() + "§c]", "eroc.slowchat.*", false);
        }
    }
    
    @Command(name = "warn", aliases = { "w" }, permission = "eroc.warn", minArgs = 2, usage = "§cUsage: /<command> <Player> <Reason>")
    public void warn(final CommandSender sender, final String[] args) {
        final Player warned = Bukkit.getPlayer(args[0]);
        if (warned == null) {
            sender.sendMessage("§cYou cannot warn an offline player");
        }
        else {
            Bukkit.broadcastMessage("§c" + sender.getName() + " §7has warned " + warned.getDisplayName() + " §7for: §c" + StringUtils.join((Object[])args, " ", 1, args.length));
        }
    }
    
    @EventHandler
    public void onChat(final AsyncPlayerChatEvent event) {
        if (this.inStaffChat.contains(event.getPlayer().getName())) {
            if (event.getPlayer().hasPermission("eroc.staffchat")) {
                event.setCancelled(true);
                Bukkit.broadcast("§b[Staff] " + event.getPlayer().getName() + ": §f" + event.getMessage(), "eroc.staffchat");
                return;
            }
            this.inStaffChat.remove(event.getPlayer().getName());
        }
        if (this.slowChat && !this.chatMuted && !event.getPlayer().hasPermission("eroc.slowchat.bypass")) {
            final long now = System.currentTimeMillis();
            final String name = event.getPlayer().getName();
            final Long lastChat = this.times.get(event.getPlayer().getName());
            if (lastChat != null) {
                final long earliestNext = lastChat + 10000L;
                if (now < earliestNext) {
                    final int timeRemaining = (int)((earliestNext - now) / 1000L) + 1;
                    event.getPlayer().sendMessage(ChatColor.RED + "You can not talk for " + timeRemaining + " more second" + ((timeRemaining > 1) ? "s" : ""));
                    event.setCancelled(true);
                    return;
                }
            }
            this.times.put(name, now);
        }
        if (this.chatMuted) {
            if (event.getPlayer().hasPermission("eroc.mutechat.bypass")) {
                return;
            }
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cChat is currently muted.");
        }
    }
}
