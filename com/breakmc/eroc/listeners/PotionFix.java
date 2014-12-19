package com.breakmc.eroc.listeners;

import org.bukkit.plugin.*;
import org.bukkit.potion.*;
import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import com.breakmc.eroc.*;
import java.util.*;

public class PotionFix implements Listener
{
    private boolean[] EnabledFixes;
    private int[] Amounts;
    
    public PotionFix(final Plugin plugin) {
        super();
        this.EnabledFixes = new boolean[3];
        this.Amounts = new int[2];
        this.EnabledFixes[0] = plugin.getConfig().getBoolean("Strength-Fix-Enabled");
        this.EnabledFixes[1] = plugin.getConfig().getBoolean("Health-Fix-Enabled");
        this.EnabledFixes[2] = plugin.getConfig().getBoolean("Regeneration-Fix-Enabled");
        this.Amounts[0] = plugin.getConfig().getInt("Strength-Power-Half-Hearts");
        this.Amounts[1] = plugin.getConfig().getInt("Health-Power-Half-Hearts");
    }
    
    @EventHandler
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        if (this.EnabledFixes[0] && event.getDamager() instanceof Player) {
            final Player player = (Player)event.getDamager();
            if (player.hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
                for (final PotionEffect Effect : player.getActivePotionEffects()) {
                    if (Effect.getType().equals((Object)PotionEffectType.INCREASE_DAMAGE)) {
                        final double Division = (Effect.getAmplifier() + 1) * 1.3 + 1.0;
                        int NewDamage;
                        if (event.getDamage() / Division <= 1.0) {
                            NewDamage = (Effect.getAmplifier() + 1) * 3 + 1;
                        }
                        else {
                            NewDamage = (int)(event.getDamage() / Division) + (int)((Effect.getAmplifier() + 1) * (this.Amounts[0] * 0.5));
                        }
                        event.setDamage((double)NewDamage);
                        break;
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onRegen(final EntityRegainHealthEvent event) {
        if (this.EnabledFixes[1] || this.EnabledFixes[2]) {
            final LivingEntity entity = (LivingEntity)event.getEntity();
            int lvl = 0;
            final Collection<PotionEffect> Effects = (Collection<PotionEffect>)entity.getActivePotionEffects();
            for (final PotionEffect effect : Effects) {
                if (effect.getType().getName() == "REGENERATION" || effect.getType().getName() == "HEAL") {
                    lvl = effect.getAmplifier() + 1;
                    break;
                }
            }
            if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC_REGEN && event.getAmount() == 1.0 && lvl > 0) {
                if (this.EnabledFixes[2]) {
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask((Plugin)Eroc.getPlugin((Class)Eroc.class), (Runnable)new Runnable() {
                        @Override
                        public void run() {
                            if (entity.getMaxHealth() >= entity.getHealth() + 1.0) {
                                entity.setHealth(entity.getHealth() + 1.0);
                            }
                        }
                    }, 50L / (lvl * 2));
                }
            }
            else if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.MAGIC && event.getAmount() > 1.0 && lvl > 0 && this.EnabledFixes[1]) {
                event.setAmount(event.getAmount() * (this.Amounts[1] * 0.25));
            }
        }
    }
}
