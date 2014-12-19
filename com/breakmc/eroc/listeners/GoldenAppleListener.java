package com.breakmc.eroc.listeners;

import java.util.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import java.io.*;

public class GoldenAppleListener implements Listener
{
    HashMap<String, Long> coolDown;
    long interval;
    
    public GoldenAppleListener() {
        super();
        this.coolDown = new HashMap<String, Long>();
        this.interval = 300000L;
    }
    
    @EventHandler
    public void onEat(final PlayerItemConsumeEvent e) {
        final Player p = e.getPlayer();
        final ItemStack item = e.getItem();
        if (item.getData().getItemType().equals((Object)Material.GOLDEN_APPLE) && item.getDurability() >= 1 && p.getWorld().getEnvironment().equals((Object)World.Environment.THE_END)) {
            final long now = System.currentTimeMillis();
            final String name = e.getPlayer().getName();
            final Long lastChat = this.coolDown.get(e.getPlayer().getName());
            if (lastChat != null) {
                final long earliestNext = lastChat + this.interval;
                if (now < earliestNext) {
                    final long timeRemaining = earliestNext - now;
                    p.sendMessage("§cOn cooldown for another " + this.formatTime(timeRemaining));
                    e.setCancelled(true);
                    return;
                }
            }
            this.coolDown.put(p.getName(), now);
        }
    }
    
    public String formatTime(final long time) {
        final long second = 1000L;
        final long minute = 60000L;
        final long minutes = time / minute;
        final long seconds = (time - minutes * minute) / second;
        if (seconds < 10L) {
            final String newSeconds = "0" + seconds;
            return minutes + "m" + newSeconds + "s";
        }
        return minutes + "m" + seconds + "s";
    }
    
    public static String getTime(long ms) {
        final double dub = Double.parseDouble(ms + "") / 1000.0;
        final String str = "" + Math.ceil(dub);
        ms = Long.parseLong(str.substring(0, str.length() - 1 - 1).replace(".", ""));
        StringBuilder sb = new StringBuilder(40);
        if (ms / 31449600L > 0L) {
            final long years = ms / 31449600L;
            if (years > 100L) {
                return "Never";
            }
            sb.append(years + ((years == 1L) ? " year " : " years "));
            ms -= years * 31449600L;
        }
        if (ms / 2620800L > 0L) {
            final long months = ms / 2620800L;
            sb.append(months + ((months == 1L) ? " month " : " months "));
            ms -= months * 2620800L;
        }
        if (ms / 604800L > 0L) {
            final long weeks = ms / 604800L;
            sb.append(weeks + ((weeks == 1L) ? " week " : " weeks "));
            ms -= weeks * 604800L;
        }
        if (ms / 86400L > 0L) {
            final long days = ms / 86400L;
            sb.append(days + ((days == 1L) ? " day " : " days "));
            ms -= days * 86400L;
        }
        if (ms / 3600L > 0L) {
            final long hours = ms / 3600L;
            sb.append(hours + ((hours == 1L) ? " hour " : " hours "));
            ms -= hours * 3600L;
        }
        if (ms / 60L > 0L) {
            final long minutes = ms / 60L;
            sb.append(minutes + ((minutes == 1L) ? " minute " : " minutes "));
            ms -= minutes * 60L;
        }
        if (ms > 0L) {
            sb.append(ms + ((ms == 1L) ? " second " : " seconds "));
        }
        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        }
        else {
            sb = new StringBuilder("N/A");
        }
        return sb.toString();
    }
}
