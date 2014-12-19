package com.breakmc.eroc.listeners;

import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.block.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class Soup implements Listener
{
    @EventHandler
    public void onSoup(final PlayerInteractEvent e) {
        final Action action = e.getAction();
        final Player p = e.getPlayer();
        if (action.name().contains("RIGHT")) {
            if (p.getHealth() < p.getMaxHealth()) {
                if (e.getItem() != null && e.getItem().getType() == Material.MUSHROOM_SOUP) {
                    e.setCancelled(true);
                    p.setHealth((p.getHealth() + 7.0 > p.getMaxHealth()) ? p.getMaxHealth() : (p.getHealth() + 7.0));
                    e.getItem().setType(Material.BOWL);
                }
            }
            else if (p.getFoodLevel() < 20 && e.getItem() != null && e.getItem().getType() == Material.MUSHROOM_SOUP) {
                e.setCancelled(true);
                p.setFoodLevel((p.getFoodLevel() + 7 > 20) ? 20 : (p.getFoodLevel() + 7));
                e.getItem().setType(Material.BOWL);
            }
        }
    }
}
