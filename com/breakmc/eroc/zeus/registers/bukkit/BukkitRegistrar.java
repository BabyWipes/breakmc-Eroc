package com.breakmc.eroc.zeus.registers.bukkit;

import com.breakmc.eroc.zeus.registers.*;
import com.breakmc.eroc.zeus.*;
import org.bukkit.plugin.*;
import java.lang.annotation.*;
import java.util.*;
import com.breakmc.eroc.zeus.exceptions.*;
import org.bukkit.*;
import org.bukkit.command.*;
import java.lang.reflect.*;
import com.breakmc.eroc.zeus.annotations.*;

public class BukkitRegistrar implements Registrar
{
    static HashMap<String, Method> registeredCommands;
    static HashMap<String, BukkitZeusCommand> registeredZeusCommands;
    static HashMap<String, HashMap<String, BukkitZeusSubCommand>> registeredSubcommands;
    static HashMap<String, HashMap<String, Method>> rawRegisteredSubcommands;
    static HashMap<String, Method> realRegisteredSubcommands;
    static HashMap<String, BukkitZeusSubCommand> registeredZeusSubCommands;
    Plugin plugin;
    
    public BukkitRegistrar(final Plugin plugin) {
        super();
        this.plugin = plugin;
    }
    
    @Override
    public void registerCommand(final String name, final Object obj) {
        for (final Method m : obj.getClass().getMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                final Command command = m.getAnnotation(Command.class);
                if (command.name().equalsIgnoreCase(name)) {
                    try {
                        final Constructor<?> commandConstructor = BukkitZeusCommand.class.getDeclaredConstructor(String.class, String.class, String.class, List.class, Object.class, Plugin.class);
                        commandConstructor.setAccessible(true);
                        System.out.println("Successfully hooked into org.bukkit.command.Command");
                        final BukkitZeusCommand command2 = (BukkitZeusCommand)commandConstructor.newInstance(command.name(), command.desc(), command.usage(), Arrays.asList(command.aliases()), obj, this.plugin);
                        command2.setPermission(command.permission());
                        command2.setPermissionMessage(command.permissionMsg().isEmpty() ? command2.getPermissionMessage() : command.permissionMsg());
                        command2.setMaxArgs(command.maxArgs());
                        command2.setMinArgs(command.minArgs());
                        System.out.println("Successfully created new org.bukkit.command.Command.\nInjecting...");
                        if (m.getParameterTypes() == null || (!m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class)) {
                            throw new InvalidMethodException("Invalid parameter types!");
                        }
                        BukkitRegistrar.registeredCommands.put(command.name(), m);
                        BukkitRegistrar.registeredZeusCommands.put(command.name(), command2);
                        System.out.println(m.getDeclaringClass().getName());
                        final Field field = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
                        field.setAccessible(true);
                        final CommandMap map = (CommandMap)field.get(Bukkit.getServer().getPluginManager());
                        map.register(command.name(), (org.bukkit.command.Command)command2);
                        System.out.println("Successfully injected command '" + command.name() + "'.");
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    break;
                }
            }
        }
    }
    
    @Override
    public void registerAll(final Object obj) {
        for (final Method m : obj.getClass().getMethods()) {
            if (m.isAnnotationPresent(Command.class)) {
                final Command command = m.getAnnotation(Command.class);
                try {
                    final Constructor<?> commandConstructor = BukkitZeusCommand.class.getDeclaredConstructor(String.class, String.class, String.class, List.class, Object.class, Plugin.class);
                    commandConstructor.setAccessible(true);
                    System.out.println("Successfully hooked into org.bukkit.command.Command");
                    final BukkitZeusCommand command2 = (BukkitZeusCommand)commandConstructor.newInstance(command.name(), command.desc(), command.usage(), Arrays.asList(command.aliases()), obj, this.plugin);
                    command2.setPermission(command.permission());
                    command2.setPermissionMessage(command.permissionMsg().isEmpty() ? command2.getPermissionMessage() : command.permissionMsg());
                    command2.setMaxArgs(command.maxArgs());
                    command2.setMinArgs(command.minArgs());
                    System.out.println("Successfully created new org.bukkit.command.Command.\nInjecting...");
                    if (m.getParameterTypes() == null || (!m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class)) {
                        throw new InvalidMethodException("Invalid parameter types!");
                    }
                    BukkitRegistrar.registeredCommands.put(command.name(), m);
                    BukkitRegistrar.registeredZeusCommands.put(command.name(), command2);
                    System.out.println(m.getDeclaringClass().getName());
                    final Field field = Bukkit.getServer().getPluginManager().getClass().getDeclaredField("commandMap");
                    field.setAccessible(true);
                    final CommandMap map = (CommandMap)field.get(Bukkit.getServer().getPluginManager());
                    map.register(command.name(), (org.bukkit.command.Command)command2);
                    System.out.println("Successfully injected command '" + command.name() + "'.");
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void registerAllSubCommands(final Object obj) {
        for (final Method m : obj.getClass().getMethods()) {
            if (m.isAnnotationPresent(SubCommand.class)) {
                final SubCommand sc = m.getAnnotation(SubCommand.class);
                if (!getRegisteredCommands().containsKey(sc.parent()) && !BukkitRegistrar.registeredZeusCommands.containsKey(sc.parent())) {
                    System.err.println("Bad parent!");
                    return;
                }
                try {
                    if (m.getParameterTypes() == null || (!m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class)) {
                        System.out.println("Bad parameters!");
                        return;
                    }
                    final BukkitZeusSubCommand subCommand = new BukkitZeusSubCommand(sc.parent(), sc.name(), sc.aliases(), obj);
                    BukkitRegistrar.realRegisteredSubcommands.put(sc.name(), m);
                    BukkitRegistrar.rawRegisteredSubcommands.put(sc.parent(), BukkitRegistrar.realRegisteredSubcommands);
                    BukkitRegistrar.registeredZeusSubCommands.put(sc.name(), subCommand);
                    BukkitRegistrar.registeredSubcommands.put(sc.parent(), BukkitRegistrar.registeredZeusSubCommands);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
    
    @Override
    public void registerSubCommand(final Object obj, final String name) {
        for (final Method m : obj.getClass().getMethods()) {
            if (m.isAnnotationPresent(SubCommand.class)) {
                final SubCommand sc = m.getAnnotation(SubCommand.class);
                if (sc.name().equalsIgnoreCase(name)) {
                    if (!getRegisteredCommands().containsKey(sc.parent()) && !BukkitRegistrar.registeredZeusCommands.containsKey(sc.parent())) {
                        System.err.println("Bad parent!");
                        return;
                    }
                    try {
                        if (m.getParameterTypes() == null || (!m.getParameterTypes()[0].isAssignableFrom(CommandSender.class) && m.getParameterTypes()[1] != String[].class)) {
                            System.out.println("Bad parameters!");
                            return;
                        }
                        final BukkitZeusSubCommand subCommand = new BukkitZeusSubCommand(sc.parent(), sc.name(), sc.aliases(), obj);
                        BukkitRegistrar.realRegisteredSubcommands.put(sc.name(), m);
                        BukkitRegistrar.rawRegisteredSubcommands.put(sc.parent(), BukkitRegistrar.realRegisteredSubcommands);
                        BukkitRegistrar.registeredZeusSubCommands.put(sc.name(), subCommand);
                        BukkitRegistrar.registeredSubcommands.put(sc.parent(), BukkitRegistrar.registeredZeusSubCommands);
                    }
                    catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    
    public static HashMap<String, Method> getRegisteredCommands() {
        return BukkitRegistrar.registeredCommands;
    }
    
    public static HashMap<String, HashMap<String, BukkitZeusSubCommand>> getRegisteredSubcommands() {
        return BukkitRegistrar.registeredSubcommands;
    }
    
    public static HashMap<String, HashMap<String, Method>> getRawRegisteredSubcommands() {
        return BukkitRegistrar.rawRegisteredSubcommands;
    }
    
    public static HashMap<String, BukkitZeusSubCommand> getRegisteredZeusSubCommands() {
        return BukkitRegistrar.registeredZeusSubCommands;
    }
    
    static {
        BukkitRegistrar.registeredCommands = new HashMap<String, Method>();
        BukkitRegistrar.registeredZeusCommands = new HashMap<String, BukkitZeusCommand>();
        BukkitRegistrar.registeredSubcommands = new HashMap<String, HashMap<String, BukkitZeusSubCommand>>();
        BukkitRegistrar.rawRegisteredSubcommands = new HashMap<String, HashMap<String, Method>>();
        BukkitRegistrar.realRegisteredSubcommands = new HashMap<String, Method>();
        BukkitRegistrar.registeredZeusSubCommands = new HashMap<String, BukkitZeusSubCommand>();
    }
}
