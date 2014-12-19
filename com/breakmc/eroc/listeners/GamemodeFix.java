package com.breakmc.eroc.listeners;

import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.*;

public class GamemodeFix implements Listener
{
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        e.getPlayer().setGameMode(GameMode.SURVIVAL);
    }
}
