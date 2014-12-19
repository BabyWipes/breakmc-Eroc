package com.breakmc.eroc.listeners;

import java.util.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.util.*;
import com.breakmc.eroc.*;
import org.bukkit.*;

public class SSJ implements Listener
{
    ArrayList<String> nofalldamage;
    ArrayList<String> nofalldamagewait;
    
    public SSJ() {
        super();
        this.nofalldamage = new ArrayList<String>();
        this.nofalldamagewait = new ArrayList<String>();
    }
    
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (!this.nofalldamage.contains(player.getName()) || !event.getCause().equals((Object)EntityDamageEvent.DamageCause.FALL)) {
                return;
            }
            event.setCancelled(true);
            this.nofalldamage.remove(player.getName());
        }
    }
    
    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location standBlock = player.getWorld().getBlockAt(player.getLocation().add(0.0, -0.1, 0.0)).getLocation();
        int xblock = 0;
        double xvel = 0.0;
        int yblock = -1;
        double yvel = 0.0;
        int zblock = 0;
        double zvel = 0.0;
        if (standBlock.getBlock().getTypeId() == 19) {
            while (standBlock.getBlock().getLocation().add((double)xblock, -1.0, 0.0).getBlock().getType().equals((Object)Material.SPONGE)) {
                --xblock;
                xvel += 0.8;
            }
            while (standBlock.getBlock().getLocation().add(0.0, (double)yblock, 0.0).getBlock().getType().equals((Object)Material.SPONGE)) {
                --yblock;
                ++yvel;
            }
            while (standBlock.getBlock().getLocation().add(0.0, -1.0, (double)zblock).getBlock().getType().equals((Object)Material.SPONGE)) {
                --zblock;
                zvel += 0.8;
            }
            xblock = 0;
            zblock = 0;
            while (standBlock.getBlock().getLocation().add((double)xblock, -1.0, 0.0).getBlock().getType().equals((Object)Material.SPONGE)) {
                ++xblock;
                xvel -= 0.8;
            }
            while (standBlock.getBlock().getLocation().add(0.0, -1.0, (double)zblock).getBlock().getType().equals((Object)Material.SPONGE)) {
                ++zblock;
                zvel -= 0.8;
            }
            if (standBlock.getBlock().getLocation().add(0.0, -1.0, 0.0).getBlock().getType().equals((Object)Material.SPONGE)) {
                player.setVelocity(new Vector(xvel, yvel, zvel));
            }
        }
        if (xvel == 0.0 && yvel == 0.0 && zvel == 0.0) {
            return;
        }
        if (((Eroc)Eroc.getPlugin((Class)Eroc.class)).getConfig().getBoolean("falldamage") || this.nofalldamage.contains(player.getName())) {
            return;
        }
        this.nofalldamage.add(player.getName());
    }
}
