package com.breakmc.eroc.zeus;

import org.bukkit.plugin.*;
import org.bukkit.command.*;
import com.breakmc.eroc.zeus.registers.bukkit.*;
import java.lang.reflect.*;
import java.util.*;

public class BukkitZeusCommand extends Command implements PluginIdentifiableCommand
{
    Object obj;
    int maxArgs;
    int minArgs;
    String[] args;
    Plugin plugin;
    
    public BukkitZeusCommand(final String name, final String description, final String usageMessage, final List<String> aliases, final Object obj, final Plugin plugin) {
        super(name, description, usageMessage, (List)aliases);
        this.obj = obj;
        this.plugin = plugin;
    }
    
    public boolean execute(final CommandSender sender, final String label, final String[] args) {
        this.args = args;
        if (!sender.hasPermission(this.getPermission()) && !this.getPermission().isEmpty()) {
            sender.sendMessage(this.getPermissionMessage());
            return true;
        }
        if (this.maxArgs >= 0 && args.length > this.maxArgs) {
            sender.sendMessage(this.getUsage().replace("<command>", label));
            return true;
        }
        if (this.minArgs >= 0 && args.length < this.minArgs) {
            sender.sendMessage(this.getUsage().replace("<command>", label));
            return true;
        }
        try {
            if (args.length != 0 && this.hasSubCommand(args)) {
                BukkitRegistrar.getRegisteredZeusSubCommands().get(args[0]).execute(sender, this.fixArgs(args));
                return true;
            }
            final Method m = BukkitRegistrar.getRegisteredCommands().get(this.getName());
            m.invoke(this.obj, sender, args);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    
    public Object getObj() {
        return this.obj;
    }
    
    public void setMaxArgs(final int maxArgs) {
        this.maxArgs = maxArgs;
    }
    
    public void setMinArgs(final int minArgs) {
        this.minArgs = minArgs;
    }
    
    public int getMaxArgs() {
        return this.maxArgs;
    }
    
    public int getMinArgs() {
        return this.minArgs;
    }
    
    public boolean hasSubCommand(final String[] args) {
        return BukkitRegistrar.getRawRegisteredSubcommands().containsKey(this.getName()) && BukkitRegistrar.getRawRegisteredSubcommands().get(this.getName()).containsKey(args[0]);
    }
    
    public String[] fixArgs(final String[] args) {
        final String[] subArgs = new String[args.length - 1];
        for (int i = 1; i < args.length; ++i) {
            subArgs[i - 1] = args[i];
        }
        return subArgs;
    }
    
    public Plugin getPlugin() {
        return this.plugin;
    }
    
    public String[] getArgs() {
        return this.args;
    }
}
