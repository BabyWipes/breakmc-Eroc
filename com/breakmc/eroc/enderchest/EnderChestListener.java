package com.breakmc.eroc.enderchest;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import com.breakmc.eroc.*;
import com.breakmc.eroc.utils.*;
import org.json.simple.*;
import org.json.*;
import java.io.*;
import java.util.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;

public class EnderChestListener implements Listener, Runnable
{
    HashMap<String, Inventory> echests;
    HashSet<String> joined;
    static EnderChestListener instance;
    
    public void run() {
        this.saveInventories();
        this.joined.clear();
        for (final Player player : Bukkit.getOnlinePlayers()) {
            this.joined.add(player.getName());
        }
    }
    
    @EventHandler
    public void onEchestOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getType().equals((Object)InventoryType.ENDER_CHEST)) {
            event.setCancelled(true);
            if (this.echests.containsKey(event.getPlayer().getName())) {
                event.getPlayer().openInventory((Inventory)this.echests.get(event.getPlayer().getName()));
            }
            else {
                final Inventory playerInv = Bukkit.createInventory((InventoryHolder)event.getPlayer(), 9, "Ender Chest");
                event.getPlayer().openInventory(playerInv);
            }
        }
    }
    
    @EventHandler
    public void onEchestClose(final InventoryCloseEvent event) {
        if (event.getInventory().getName().equals("Ender Chest") && event.getView().getTopInventory().getSize() == 9) {
            this.echests.put(event.getPlayer().getName(), event.getInventory());
        }
    }
    
    @EventHandler
    public void join(final PlayerJoinEvent event) {
        this.joined.add(event.getPlayer().getName());
    }
    
    private EnderChestListener() {
        super();
        this.echests = new HashMap<String, Inventory>();
        this.joined = new HashSet<String>();
    }
    
    public void saveInventory(final String player) {
        if (!this.echests.containsKey(player)) {
            return;
        }
        final File invFile = new File(((Eroc)Eroc.getPlugin((Class)Eroc.class)).getEchestFolder(), player + ".json");
        if (!invFile.exists()) {
            try {
                invFile.createNewFile();
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        final List<String> jsstrings = Serialization.toString(this.echests.get(player));
        final JSONArray array = new JSONArray();
        for (final String str : jsstrings) {
            array.add((Object)str);
        }
        try {
            final FileWriter writer = new FileWriter(invFile);
            array.writeJSONString((Writer)writer);
            writer.flush();
            writer.close();
        }
        catch (Exception ex2) {
            ex2.printStackTrace();
        }
    }
    
    public void loadInventory(final File file) {
        try {
            final FileReader reader = new FileReader(file);
            final JSONTokener tokener = new JSONTokener(reader);
            final org.json.JSONArray jsonArray = new org.json.JSONArray(tokener);
            final List<String> data = new ArrayList<String>();
            for (int i = 0; i < jsonArray.length(); ++i) {
                data.add(jsonArray.getString(i));
            }
            final Inventory inv = Serialization.toInventory(data, 9);
            this.echests.put(file.getName().split("\\.")[0], inv);
            reader.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void saveInventories() {
        new BukkitRunnable() {
            public void run() {
                for (final String poss : EnderChestListener.this.joined) {
                    EnderChestListener.this.saveInventory(poss);
                }
            }
        }.runTaskAsynchronously((Plugin)Eroc.getPlugin((Class)Eroc.class));
    }
    
    public void saveAllInventories() {
        for (final String poss : this.echests.keySet()) {
            this.saveInventory(poss);
        }
    }
    
    public void loadInventories() {
        final File[] files = ((Eroc)Eroc.getPlugin((Class)Eroc.class)).getEchestFolder().listFiles();
        int loaded = 0;
        for (final File file : files) {
            this.loadInventory(file);
            ++loaded;
        }
    }
    
    public static EnderChestListener getInstance() {
        return EnderChestListener.instance;
    }
    
    public HashSet<String> getJoined() {
        return this.joined;
    }
    
    static {
        EnderChestListener.instance = new EnderChestListener();
    }
}
