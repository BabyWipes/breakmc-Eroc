package com.breakmc.eroc.listeners;

import java.util.*;
import com.breakmc.eroc.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class AntiSpamBot implements Listener
{
    HashMap<String, Integer> currentAccounts;
    ArrayList<String> offlineAccounts;
    
    public AntiSpamBot() {
        super();
        this.currentAccounts = new HashMap<String, Integer>();
        this.offlineAccounts = new ArrayList<String>();
    }
    
    @EventHandler
    public void onLogin(final PlayerLoginEvent e) {
        final Player p = e.getPlayer();
        ((Eroc)Eroc.getPlugin((Class)Eroc.class)).getServer().getScheduler().scheduleSyncDelayedTask((Plugin)Eroc.getPlugin((Class)Eroc.class), (Runnable)new Runnable() {
            @Override
            public void run() {
                final String ip = e.getAddress().getHostAddress();
                if (!AntiSpamBot.this.currentAccounts.containsKey(ip)) {
                    AntiSpamBot.this.currentAccounts.put(ip, 0);
                }
                e.allow();
                if (AntiSpamBot.this.currentAccounts.get(ip) < 3) {
                    AntiSpamBot.this.currentAccounts.put(ip, AntiSpamBot.this.currentAccounts.get(ip) + 1);
                }
                else {
                    AntiSpamBot.this.offlineAccounts.add(ip);
                    e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cYou already have 3 accounts logged into that IP address.\n§cPlease log at least one of the accounts off before logging another on.");
                }
            }
        }, 5L);
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        final String ip = p.getAddress().getAddress().getHostAddress();
        if (this.currentAccounts.containsKey(ip) && !this.offlineAccounts.contains(ip)) {
            this.currentAccounts.put(ip, this.currentAccounts.get(ip) - 1);
            if (this.currentAccounts.get(ip) <= 0) {
                this.currentAccounts.remove(ip);
            }
        }
        else {
            this.offlineAccounts.remove(ip);
        }
    }
}
