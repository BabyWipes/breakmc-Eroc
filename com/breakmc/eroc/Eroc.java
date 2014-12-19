package com.breakmc.eroc;

import org.bukkit.plugin.java.*;
import com.breakmc.eroc.zeus.registers.bukkit.*;
import java.io.*;
import org.bukkit.plugin.*;
import com.breakmc.eroc.enderchest.*;
import com.breakmc.eroc.utils.*;
import com.breakmc.eroc.listeners.*;
import com.breakmc.eroc.commands.*;
import com.breakmc.eroc.automessage.*;
import org.bukkit.entity.*;
import org.bukkit.command.*;
import org.bukkit.*;
import com.breakmc.eroc.zeus.annotations.*;
import org.bukkit.event.player.*;
import org.bukkit.event.*;

public class Eroc extends JavaPlugin implements Listener
{
    BukkitRegistrar registrar;
    AdminCommands ADMIN_COMMAND_INSTANCE;
    File messageFile;
    File echestFolder;
    
    public Eroc() {
        super();
        this.registrar = new BukkitRegistrar((Plugin)this);
        this.ADMIN_COMMAND_INSTANCE = new AdminCommands();
        this.messageFile = new File(this.getDataFolder(), "messages.json");
        this.echestFolder = new File(this.getDataFolder() + File.separator + "echests" + File.separator);
    }
    
    public void onEnable() {
        this.getServer().getPluginManager().registerEvents((Listener)new GamemodeFix(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new AntiSpamBot(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new CombatTag(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new EggListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new GoldenAppleListener(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new HungerFix(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new PotionFix((Plugin)this), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new Soup(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)new SSJ(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)EnderChestListener.getInstance(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)this.ADMIN_COMMAND_INSTANCE, (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)VanishManager.getInstance(), (Plugin)this);
        this.getServer().getPluginManager().registerEvents((Listener)AntiSpam.getInstance(), (Plugin)this);
        this.registrar.registerAll(this.ADMIN_COMMAND_INSTANCE);
        this.registrar.registerAll(new Report());
        this.registrar.registerAll(new AutoMessage());
        this.registrar.registerAll(VanishManager.getInstance());
        this.registrar.registerAll(this);
        this.getCommand("vanish").setExecutor((CommandExecutor)VanishManager.getInstance());
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        if (!this.echestFolder.exists()) {
            this.echestFolder.mkdir();
        }
        EnderChestListener.getInstance().loadInventories();
        Bukkit.getScheduler().runTaskTimerAsynchronously((Plugin)this, (Runnable)EnderChestListener.getInstance(), 20L, 36000L);
        if (this.messageFile.exists()) {
            AutoMessageManager.getInstance().load(this.messageFile);
            AutoMessageManager.getInstance().runTaskTimerAsynchronously((Plugin)this, 20L, 2400L);
        }
        else {
            AutoMessageManager.getInstance().addMessage("Default Message!");
            AutoMessageManager.getInstance().runTaskTimerAsynchronously((Plugin)this, 20L, 2400L);
        }
        if (Bukkit.getOnlinePlayers().length > 0) {
            for (final Player player : Bukkit.getOnlinePlayers()) {
                EnderChestListener.getInstance().getJoined().add(player.getName());
            }
        }
    }
    
    public void onDisable() {
        EnderChestListener.getInstance().saveAllInventories();
        if (!this.messageFile.exists()) {
            try {
                this.messageFile.createNewFile();
                AutoMessageManager.getInstance().save(this.messageFile);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        else {
            AutoMessageManager.getInstance().save(this.messageFile);
        }
    }
    
    public File getMessageFile() {
        return this.messageFile;
    }
    
    public File getEchestFolder() {
        return this.echestFolder;
    }
    
    public int getSpawnRadius() {
        return this.getConfig().getInt("spawn-radius", 58);
    }
    
    @Command(name = "rules", aliases = { "erules" })
    public void rules(final CommandSender sender, final String[] args) {
        sender.sendMessage(ChatColor.GRAY + "====================================================");
        sender.sendMessage(ChatColor.AQUA + "AdvancedPvP " + ChatColor.WHITE + "Rule Page");
        sender.sendMessage(ChatColor.GRAY + "====================================================");
        sender.sendMessage(ChatColor.AQUA + "Cheating" + ChatColor.WHITE + " - Using client modifications to cheat is not allowed.");
        sender.sendMessage(ChatColor.AQUA + "Threats" + ChatColor.WHITE + " -  Any threats directed to another player is prohibited.");
        sender.sendMessage(ChatColor.AQUA + "Advertising" + ChatColor.WHITE + " - Advertising other servers is not allowed.");
        sender.sendMessage(ChatColor.AQUA + "Links" + ChatColor.WHITE + " - Do not post any links unaffiliated with BreakMC.");
        sender.sendMessage(ChatColor.AQUA + "Privacy" + ChatColor.WHITE + " - Do not share other players' personal information.");
        sender.sendMessage(ChatColor.AQUA + "Common Sense" + ChatColor.WHITE + " -  Use common sense! Respect other players!");
        sender.sendMessage(ChatColor.GRAY + "====================================================");
    }
    
    @Command(name = "help", aliases = { "?", "ehelp" })
    public void help(final CommandSender sender, final String[] args) {
        sender.sendMessage(ChatColor.GRAY + "====================================================");
        sender.sendMessage(ChatColor.AQUA + "AdvancedPvP " + ChatColor.WHITE + "Help Page");
        sender.sendMessage(ChatColor.GRAY + "====================================================");
        sender.sendMessage(ChatColor.AQUA + "/track" + ChatColor.WHITE + " - Displays the commands for tracking.");
        sender.sendMessage(ChatColor.AQUA + "/team" + ChatColor.WHITE + " - Displays the commands for teams");
        sender.sendMessage(ChatColor.AQUA + "/kits" + ChatColor.WHITE + " - Displays the kits available to you.");
        sender.sendMessage(ChatColor.AQUA + "/who" + ChatColor.WHITE + " - List the players that are currently online.");
        sender.sendMessage(ChatColor.AQUA + "/rules" + ChatColor.WHITE + " - Displays the server rules.");
        sender.sendMessage(ChatColor.AQUA + "/buy /sell" + ChatColor.WHITE + " - Sell or buy an item off the in game market.");
        sender.sendMessage(ChatColor.GRAY + "====================================================");
    }
    
    @EventHandler
    public void help(final PlayerCommandPreprocessEvent event) {
        if (event.getMessage().toLowerCase().startsWith("/help") || event.getMessage().toLowerCase().startsWith("/ehelp") || event.getMessage().toLowerCase().startsWith("/essentials:help") || event.getMessage().toLowerCase().startsWith("/essentials:ehelp")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "====================================================");
            event.getPlayer().sendMessage(ChatColor.AQUA + "AdvancedPvP " + ChatColor.WHITE + "Help Page");
            event.getPlayer().sendMessage(ChatColor.GRAY + "====================================================");
            event.getPlayer().sendMessage(ChatColor.AQUA + "/track" + ChatColor.WHITE + " - Displays the commands for tracking.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "/team" + ChatColor.WHITE + " - Displays the commands for teams");
            event.getPlayer().sendMessage(ChatColor.AQUA + "/kits" + ChatColor.WHITE + " - Displays the kits available to you.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "/who" + ChatColor.WHITE + " - List the players that are currently online.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "/rules" + ChatColor.WHITE + " - Displays the server rules.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "/buy /sell" + ChatColor.WHITE + " - Sell or buy an item off the in game market.");
            event.getPlayer().sendMessage(ChatColor.GRAY + "====================================================");
        }
        if (event.getMessage().toLowerCase().startsWith("/rules") || event.getMessage().toLowerCase().startsWith("/erules") || event.getMessage().toLowerCase().startsWith("/essentials:rules") || event.getMessage().toLowerCase().startsWith("/essentials:erules")) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(ChatColor.GRAY + "====================================================");
            event.getPlayer().sendMessage(ChatColor.AQUA + "AdvancedPvP " + ChatColor.WHITE + "Rule Page");
            event.getPlayer().sendMessage(ChatColor.GRAY + "====================================================");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Cheating" + ChatColor.WHITE + " - Using client modifications to cheat is not allowed.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Threats" + ChatColor.WHITE + " -  Any threats directed to another player is prohibited.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Advertising" + ChatColor.WHITE + " - Advertising other servers is not allowed.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Links" + ChatColor.WHITE + " - Do not post any links unaffiliated with BreakMC.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Privacy" + ChatColor.WHITE + " - Do not share other players' personal information.");
            event.getPlayer().sendMessage(ChatColor.AQUA + "Common Sense" + ChatColor.WHITE + " -  Use common sense! Respect other players!");
            event.getPlayer().sendMessage(ChatColor.GRAY + "====================================================");
        }
    }
}
