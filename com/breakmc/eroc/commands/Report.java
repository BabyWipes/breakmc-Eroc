package com.breakmc.eroc.commands;

import java.util.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.apache.commons.lang.*;
import com.breakmc.eroc.zeus.annotations.*;

public class Report
{
    HashMap<UUID, Long> reportCooldown;
    
    public Report() {
        super();
        this.reportCooldown = new HashMap<UUID, Long>();
    }
    
    @Command(name = "report", usage = "§cUsage: /report <Name> <Reason>", minArgs = 2)
    public void onCommand(final CommandSender sender, final String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("You do not need to report someone!");
            return;
        }
        final Player p = (Player)sender;
        if (this.reportCooldown.containsKey(p.getUniqueId())) {
            final int reportingCooldown = 60;
            final long secondsLeft = this.reportCooldown.get(p.getUniqueId()) / 1000L + reportingCooldown - System.currentTimeMillis() / 1000L;
            if (secondsLeft > 0L) {
                p.sendMessage(ChatColor.GRAY + "Please wait " + ChatColor.GREEN + secondsLeft + ChatColor.GRAY + " seconds before doing this again.");
                return;
            }
        }
        if (args.length < 2) {
            return;
        }
        final Player t = Bukkit.getServer().getPlayer(args[0]);
        if (t == null) {
            p.sendMessage(ChatColor.RED + "Player specified could not be found.");
            return;
        }
        Bukkit.broadcast(ChatColor.RED + "(REPORT) " + p.getName() + " says " + t.getName() + " is: " + StringUtils.join((Object[])args, " ", 1, args.length), "eroc.report.receive");
        this.reportCooldown.put(p.getUniqueId(), System.currentTimeMillis());
        p.sendMessage(ChatColor.GRAY + "Player " + t.getName() + " has been reported.");
    }
}
