package com.breakmc.eroc.zeus;

import org.bukkit.command.*;
import java.lang.reflect.*;
import java.util.*;
import com.breakmc.eroc.zeus.registers.bukkit.*;

public class BukkitZeusSubCommand
{
    String parent;
    String name;
    String[] aliases;
    Object instance;
    
    public BukkitZeusSubCommand(final String parent, final String name, final String[] aliases, final Object instance) {
        super();
        this.name = name;
        this.parent = parent;
        this.aliases = aliases;
        this.instance = instance;
    }
    
    public void execute(final CommandSender sender, final String[] args) {
        try {
            final Method method = BukkitRegistrar.getRawRegisteredSubcommands().get(this.parent).get(this.name);
            method.invoke(this.instance, sender, args);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
