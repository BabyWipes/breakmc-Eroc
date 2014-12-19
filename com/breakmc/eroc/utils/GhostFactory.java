package com.breakmc.eroc.utils;

import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import org.bukkit.*;
import org.bukkit.scoreboard.*;
import org.bukkit.entity.*;
import org.bukkit.potion.*;
import java.util.*;

public class GhostFactory
{
    private static final String GHOST_TEAM_NAME = "Ghosts";
    private static final long UPDATE_DELAY = 20L;
    private static final OfflinePlayer[] EMPTY_PLAYERS;
    private Team ghostTeam;
    private BukkitTask task;
    private boolean closed;
    private Set<String> ghosts;
    
    public GhostFactory(final Plugin plugin) {
        super();
        this.ghosts = new HashSet<String>();
        this.createTask(plugin);
        this.createGetTeam();
    }
    
    private void createGetTeam() {
        final Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        this.ghostTeam = board.getTeam("Ghosts");
        if (this.ghostTeam == null) {
            this.ghostTeam = board.registerNewTeam("Ghosts");
        }
        this.ghostTeam.setCanSeeFriendlyInvisibles(true);
    }
    
    private void createTask(final Plugin plugin) {
        this.task = Bukkit.getScheduler().runTaskTimer(plugin, (Runnable)new Runnable() {
            @Override
            public void run() {
                for (final OfflinePlayer member : GhostFactory.this.getMembers()) {
                    final Player player = member.getPlayer();
                    if (player != null) {
                        GhostFactory.this.setGhost(player, GhostFactory.this.isGhost(player));
                    }
                    else {
                        GhostFactory.this.ghosts.remove(member.getName());
                        GhostFactory.this.ghostTeam.removePlayer(member);
                    }
                }
            }
        }, 20L, 20L);
    }
    
    public void clearMembers() {
        if (this.ghostTeam != null) {
            for (final OfflinePlayer player : this.getMembers()) {
                this.ghostTeam.removePlayer(player);
            }
        }
    }
    
    public void addPlayer(final Player player) {
        this.validateState();
        if (!this.ghostTeam.hasPlayer((OfflinePlayer)player)) {
            this.ghostTeam.addPlayer((OfflinePlayer)player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        }
    }
    
    public boolean isGhost(final Player player) {
        return player != null && this.hasPlayer(player) && this.ghosts.contains(player.getName());
    }
    
    public boolean hasPlayer(final Player player) {
        this.validateState();
        return this.ghostTeam.hasPlayer((OfflinePlayer)player);
    }
    
    public void setGhost(final Player player, final boolean isGhost) {
        if (!this.hasPlayer(player)) {
            this.addPlayer(player);
        }
        if (isGhost) {
            this.ghosts.add(player.getName());
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 15));
        }
        else if (!isGhost) {
            this.ghosts.remove(player.getName());
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
    
    public void removePlayer(final Player player) {
        this.validateState();
        if (this.ghostTeam.removePlayer((OfflinePlayer)player)) {
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
        }
    }
    
    public OfflinePlayer[] getGhosts() {
        this.validateState();
        final Set<OfflinePlayer> players = new HashSet<OfflinePlayer>(this.ghostTeam.getPlayers());
        final Iterator<OfflinePlayer> it = players.iterator();
        while (it.hasNext()) {
            if (!this.ghosts.contains(it.next().getName())) {
                it.remove();
            }
        }
        return this.toArray(players);
    }
    
    public OfflinePlayer[] getMembers() {
        this.validateState();
        return this.toArray(this.ghostTeam.getPlayers());
    }
    
    private OfflinePlayer[] toArray(final Set<OfflinePlayer> players) {
        if (players != null) {
            return players.toArray(new OfflinePlayer[0]);
        }
        return GhostFactory.EMPTY_PLAYERS;
    }
    
    public void close() {
        if (!this.closed) {
            this.task.cancel();
            this.ghostTeam.unregister();
            this.closed = true;
        }
    }
    
    public boolean isClosed() {
        return this.closed;
    }
    
    private void validateState() {
        if (this.closed) {
            throw new IllegalStateException("Ghost factory has closed. Cannot reuse instances.");
        }
    }
    
    static {
        EMPTY_PLAYERS = new OfflinePlayer[0];
    }
}
