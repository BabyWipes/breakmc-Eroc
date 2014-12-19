package com.breakmc.eroc.listeners;

import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class EggListener implements Listener
{
    @EventHandler
    public void onHit(final EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Egg && event.getEntity() instanceof Villager && (event.getEntity().getLocation().getX() <= 58.0 || event.getEntity().getLocation().getX() <= -58.0) && (event.getEntity().getLocation().getZ() <= 58.0 || event.getEntity().getLocation().getZ() <= -58.0)) {
            event.setCancelled(true);
        }
    }
}
