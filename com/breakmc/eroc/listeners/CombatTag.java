package com.breakmc.eroc.listeners;

import java.util.*;
import org.bukkit.scheduler.*;
import com.breakmc.eroc.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import org.bukkit.scoreboard.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.*;
import org.bukkit.event.entity.*;

public class CombatTag implements Listener
{
    private HashMap<String, Integer> inCombat;
    private HashMap<String, BukkitRunnable> inCombatTag;
    
    public CombatTag() {
        super();
        this.inCombat = new HashMap<String, Integer>();
        this.inCombatTag = new HashMap<String, BukkitRunnable>();
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onDamage(final EntityDamageByEntityEvent e) {
        if (e.isCancelled()) {
            System.out.println("Event is cancelled. Ignoring...");
            return;
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            final Player damaged = (Player)e.getEntity();
            final Player damager = (Player)e.getDamager();
            if (!this.inCombat.containsKey(damaged.getName())) {
                damaged.sendMessage(ChatColor.RED + "You are now in combat!");
                final Scoreboard scoreboard = damaged.getScoreboard();
                final Objective objective = (scoreboard.getObjective("ct") == null) ? scoreboard.registerNewObjective("ct", "dummy") : scoreboard.getObjective("ct");
                objective.setDisplayName("§6Timers");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                final Score combatTag = objective.getScore("§aCombat Tag");
                combatTag.setScore(60);
                this.inCombatTag.put(damaged.getName(), new BukkitRunnable() {
                    public void run() {
                        if (CombatTag.this.inCombat.get(damaged.getName()) != null) {
                            if (CombatTag.this.inCombat.get(damaged.getName()) > 0) {
                                CombatTag.this.inCombat.put(damaged.getName(), CombatTag.this.inCombat.get(damaged.getName()) - 1);
                                combatTag.setScore((int)CombatTag.this.inCombat.get(damaged.getName()));
                            }
                            else {
                                CombatTag.this.inCombat.remove(damaged.getName());
                                damaged.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                scoreboard.resetScores("§aCombat Tag");
                                this.cancel();
                                CombatTag.this.inCombatTag.remove(damager.getName());
                            }
                        }
                    }
                });
                this.inCombatTag.get(damaged.getName()).runTaskTimerAsynchronously((Plugin)Eroc.getPlugin((Class)Eroc.class), 20L, 20L);
            }
            if (!this.inCombat.containsKey(damager.getName())) {
                damager.sendMessage(ChatColor.RED + "You are now in combat!");
                final Scoreboard scoreboard2 = damager.getScoreboard();
                final Objective objective2 = (scoreboard2.getObjective("ct") == null) ? scoreboard2.registerNewObjective("ct", "dummy") : scoreboard2.getObjective("ct");
                objective2.setDisplayName("§6Timers");
                objective2.setDisplaySlot(DisplaySlot.SIDEBAR);
                final Score combatTag2 = objective2.getScore("§aCombat Tag");
                combatTag2.setScore(60);
                this.inCombatTag.put(damager.getName(), new BukkitRunnable() {
                    public void run() {
                        if (CombatTag.this.inCombat.get(damager.getName()) != null) {
                            if (CombatTag.this.inCombat.get(damager.getName()) > 0) {
                                CombatTag.this.inCombat.put(damager.getName(), CombatTag.this.inCombat.get(damager.getName()) - 1);
                                combatTag2.setScore((int)CombatTag.this.inCombat.get(damager.getName()));
                            }
                            else {
                                CombatTag.this.inCombat.remove(damager.getName());
                                damager.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                scoreboard2.resetScores("§aCombat Tag");
                                this.cancel();
                                CombatTag.this.inCombatTag.remove(damager.getName());
                            }
                        }
                    }
                });
                this.inCombatTag.get(damager.getName()).runTaskTimerAsynchronously((Plugin)Eroc.getPlugin((Class)Eroc.class), 20L, 20L);
            }
            this.inCombat.put(damaged.getName(), 60);
            this.inCombat.put(damager.getName(), 60);
        }
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Projectile) {
            final Player damaged = (Player)e.getEntity();
            final Projectile projectile = (Projectile)e.getDamager();
            if (projectile.getShooter() instanceof Player) {
                final Player damager2 = (Player)projectile.getShooter();
                if (damager2.equals(damaged)) {
                    return;
                }
                if (!this.inCombat.containsKey(damaged.getName())) {
                    damaged.sendMessage(ChatColor.RED + "You are now in combat!");
                    final Scoreboard scoreboard3 = damaged.getScoreboard();
                    final Objective objective3 = (scoreboard3.getObjective("ct") == null) ? scoreboard3.registerNewObjective("ct", "dummy") : scoreboard3.getObjective("ct");
                    objective3.setDisplayName("§6Timers");
                    objective3.setDisplaySlot(DisplaySlot.SIDEBAR);
                    final Score combatTag3 = objective3.getScore("§aCombat Tag");
                    combatTag3.setScore(60);
                    this.inCombatTag.put(damaged.getName(), new BukkitRunnable() {
                        public void run() {
                            if (CombatTag.this.inCombat.get(damaged.getName()) != null) {
                                if (CombatTag.this.inCombat.get(damaged.getName()) > 0) {
                                    CombatTag.this.inCombat.put(damaged.getName(), CombatTag.this.inCombat.get(damaged.getName()) - 1);
                                    combatTag3.setScore((int)CombatTag.this.inCombat.get(damaged.getName()));
                                }
                                else {
                                    CombatTag.this.inCombat.remove(damaged.getName());
                                    damaged.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                    scoreboard3.resetScores("§aCombat Tag");
                                    this.cancel();
                                    CombatTag.this.inCombatTag.remove(damager2.getName());
                                }
                            }
                        }
                    });
                    this.inCombatTag.get(damaged.getName()).runTaskTimerAsynchronously((Plugin)Eroc.getPlugin((Class)Eroc.class), 20L, 20L);
                }
                if (!this.inCombat.containsKey(damager2.getName())) {
                    damager2.sendMessage(ChatColor.RED + "You are now in combat!");
                    final Scoreboard scoreboard4 = damager2.getScoreboard();
                    final Objective objective4 = (scoreboard4.getObjective("ct") == null) ? scoreboard4.registerNewObjective("ct", "dummy") : scoreboard4.getObjective("ct");
                    objective4.setDisplayName("§6Timers");
                    objective4.setDisplaySlot(DisplaySlot.SIDEBAR);
                    final Score combatTag4 = objective4.getScore("§aCombat Tag");
                    combatTag4.setScore(60);
                    this.inCombatTag.put(damager2.getName(), new BukkitRunnable() {
                        public void run() {
                            if (CombatTag.this.inCombat.get(damager2.getName()) != null) {
                                if (CombatTag.this.inCombat.get(damager2.getName()) > 0) {
                                    CombatTag.this.inCombat.put(damager2.getName(), CombatTag.this.inCombat.get(damager2.getName()) - 1);
                                    combatTag4.setScore((int)CombatTag.this.inCombat.get(damager2.getName()));
                                }
                                else {
                                    CombatTag.this.inCombat.remove(damager2.getName());
                                    damager2.sendMessage(ChatColor.GREEN + "You are no longer in combat.");
                                    scoreboard4.resetScores("§aCombat Tag");
                                    this.cancel();
                                    CombatTag.this.inCombatTag.remove(damager2.getName());
                                }
                            }
                        }
                    });
                    this.inCombatTag.get(damager2.getName()).runTaskTimerAsynchronously((Plugin)Eroc.getPlugin((Class)Eroc.class), 20L, 20L);
                }
                this.inCombat.put(damaged.getName(), 60);
                this.inCombat.put(damager2.getName(), 60);
            }
        }
    }
    
    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        final Player p = e.getPlayer();
        final String name = p.getName();
        if (this.inCombat.containsKey(p.getName()) && this.inCombatTag.containsKey(name)) {
            if (p.hasPermission("eroc.combattag.bypass")) {
                return;
            }
            p.setHealth(0.0);
            this.inCombat.remove(p.getName());
            if (this.inCombatTag.get(name) != null) {
                this.inCombatTag.get(name).cancel();
            }
            this.inCombatTag.remove(name);
        }
    }
    
    @EventHandler
    public void onJoin(final PlayerJoinEvent e) {
        final Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        e.getPlayer().setScoreboard(scoreboard);
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        if (this.inCombat.containsKey(event.getEntity().getName()) && this.inCombatTag.containsKey(event.getEntity().getName())) {
            this.inCombat.remove(event.getEntity().getName());
            this.inCombatTag.get(event.getEntity().getName()).cancel();
            this.inCombatTag.remove(event.getEntity().getName());
        }
    }
}
