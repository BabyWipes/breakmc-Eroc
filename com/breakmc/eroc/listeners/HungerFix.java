package com.breakmc.eroc.listeners;

import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import java.util.*;
import org.bukkit.event.*;

public class HungerFix implements Listener
{
    @EventHandler
    public void onHungerLoss(final FoodLevelChangeEvent e) {
        if (e.getFoodLevel() < ((Player)e.getEntity()).getFoodLevel() && new Random().nextInt(100) > 4) {
            e.setCancelled(true);
        }
    }
}
