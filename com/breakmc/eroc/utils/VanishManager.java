package com.breakmc.eroc.utils;

import com.breakmc.eroc.*;
import org.bukkit.plugin.*;
import org.bukkit.command.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.breakmc.eroc.zeus.annotations.*;
import org.bukkit.event.block.*;
import org.bukkit.*;
import org.bukkit.event.*;
import org.bukkit.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.*;
import org.bukkit.block.*;
import org.bukkit.inventory.*;
import org.bukkit.event.entity.*;

public class VanishManager implements Listener, CommandExecutor
{
    public GhostFactory ghost;
    public static HashMap<UUID, Boolean> vanish;
    private static VanishManager instance;
    
    private VanishManager() {
        super();
        this.ghost = new GhostFactory((Plugin)Eroc.getPlugin((Class)Eroc.class));
    }
    
    public static VanishManager getInstance() {
        return VanishManager.instance;
    }
    
    @Command(name = "entity", aliases = { "en", "entities" }, permission = "eroc.listentity")
    public void enitity(final CommandSender sender, final String[] args) {
        for (final World world : Bukkit.getWorlds()) {
            sender.sendMessage("§aShowing entities in world: §6" + world.getName());
            for (final EntityType type : EntityType.values()) {
                if (type.isAlive() && type.isSpawnable()) {
                    sender.sendMessage(String.format("§a%s: §6%d", StringUtils.capitalize(type.name().toLowerCase()), world.getEntitiesByClass(type.getEntityClass()).size()));
                }
            }
        }
    }
    
    public boolean onCommand(final CommandSender sender, final org.bukkit.command.Command cmd, final String label, final String[] args) {
        if (sender instanceof Player) {
            final Player p = (Player)sender;
            if (p.hasPermission("eroc.vanish")) {
                if (!VanishManager.vanish.containsKey(p.getUniqueId()) || (VanishManager.vanish.containsKey(p.getUniqueId()) && !VanishManager.vanish.get(p.getUniqueId()))) {
                    VanishManager.vanish.put(p.getUniqueId(), true);
                    p.sendMessage("§aVanish has been enabled.");
                    this.ghost.setGhost(p, true);
                    for (final Player all : Bukkit.getOnlinePlayers()) {
                        if (!all.hasPermission("eroc.vanish.*")) {
                            if (all.canSee(p)) {
                                all.hidePlayer(p);
                            }
                        }
                        else {
                            this.ghost.addPlayer(p);
                        }
                    }
                }
                else {
                    VanishManager.vanish.put(p.getUniqueId(), false);
                    p.sendMessage("§cVanish has been disabled");
                    this.ghost.setGhost(p, false);
                    for (final Player all : Bukkit.getOnlinePlayers()) {
                        if (!all.canSee(p)) {
                            all.showPlayer(p);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    @EventHandler
    public void onInteract(final PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        final ItemStack item = p.getItemInHand();
        if ((e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) && item != null && item.getType() == Material.POTION && (item.getDurability() == 8238 || item.getDurability() == 8270 || item.getDurability() == 16430 || item.getDurability() == 16462)) {
            e.setCancelled(true);
            p.sendMessage("§cInvisibility potions are disabled.");
        }
    }
    
    @EventHandler
    public void onDrink(final PlayerItemConsumeEvent e) {
        if (e.getItem().getType() == Material.POTION && (e.getItem().getDurability() == 8238 || e.getItem().getDurability() == 8270)) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§cInvisibility potions are disabled.");
        }
    }
    
    @EventHandler
    public void onSplash(final PotionSplashEvent e) {
        final ThrownPotion pot = e.getEntity();
        if (pot.getItem().getType() == Material.POTION && (pot.getItem().getDurability() == 16430 || pot.getItem().getDurability() == 16462)) {
            e.getAffectedEntities().clear();
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Player p = e.getPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask((Plugin)Eroc.getPlugin((Class)Eroc.class), (Runnable)new Runnable() {
            @Override
            public void run() {
                if (p.hasPermission("eroc.vanish")) {
                    VanishManager.this.ghost.addPlayer(p);
                    VanishManager.vanish.put(p.getUniqueId(), false);
                }
                for (final Player all : Bukkit.getOnlinePlayers()) {
                    if (VanishManager.vanish.containsKey(p.getUniqueId()) && VanishManager.vanish.get(p.getUniqueId())) {
                        p.hidePlayer(all);
                    }
                }
            }
        }, 5L);
    }
    
    @EventHandler
    public void onLeave(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        for (final Player all : Bukkit.getOnlinePlayers()) {
            if (VanishManager.vanish.containsKey(p.getUniqueId())) {
                VanishManager.vanish.remove(p.getUniqueId());
                this.ghost.removePlayer(p);
                if (!all.canSee(p)) {
                    all.showPlayer(p);
                }
            }
        }
    }
    
    @EventHandler
    public void onItemPickup(final PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        if (VanishManager.vanish.containsKey(p.getUniqueId()) && VanishManager.vanish.get(p.getUniqueId())) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player v = (Player)e.getEntity();
            final Player d = (Player)e.getDamager();
            if (VanishManager.vanish.containsKey(d.getUniqueId()) && VanishManager.vanish.get(d.getUniqueId())) {
                e.setCancelled(true);
                d.sendMessage("§cYou cannot damage players while in vanish.");
            }
            if (VanishManager.vanish.containsKey(v.getUniqueId()) && VanishManager.vanish.get(v.getUniqueId())) {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void onOpen(final InventoryOpenEvent e) {
        final Player p = (Player)e.getPlayer();
        final Inventory inv = e.getInventory();
        if ((e.getInventory().getHolder() instanceof Chest || e.getInventory().getHolder() instanceof DoubleChest) && VanishManager.vanish.containsKey(p.getUniqueId()) && VanishManager.vanish.get(p.getUniqueId())) {
            e.setCancelled(true);
            final Inventory fakechest = Bukkit.createInventory((InventoryHolder)p, inv.getSize());
            fakechest.setContents(inv.getContents());
            p.openInventory(fakechest);
        }
    }
    
    @EventHandler
    public void onSpawn(final CreatureSpawnEvent e) {
        if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL) {
            e.getEntity().remove();
            e.setCancelled(true);
        }
    }
    
    static {
        VanishManager.vanish = new HashMap<UUID, Boolean>();
        VanishManager.instance = new VanishManager();
    }
}
