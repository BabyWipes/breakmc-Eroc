package com.breakmc.eroc.utils;

import org.bukkit.*;
import org.bukkit.entity.*;

public class Common
{
    public static void broadcast(final String message, final String permission, final boolean ignoreIfHasPermission) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission(permission)) {
                if (!ignoreIfHasPermission) {
                    player.sendMessage(message);
                }
            }
            else if (!player.hasPermission(permission) && ignoreIfHasPermission) {
                player.sendMessage(message);
            }
        }
    }
}
