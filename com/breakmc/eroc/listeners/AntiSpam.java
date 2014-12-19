package com.breakmc.eroc.listeners;

import java.util.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import com.breakmc.eroc.utils.*;
import org.apache.commons.lang.*;

public class AntiSpam implements Listener
{
    HashMap<String, List<Message>> messages;
    HashMap<String, Message> lastMessage;
    HashMap<String, Integer> muted;
    HashMap<String, Long> timeMuted;
    HashMap<String, Long> banned;
    HashMap<String, Integer> violations;
    static AntiSpam instance;
    
    private AntiSpam() {
        super();
        this.messages = new HashMap<String, List<Message>>();
        this.lastMessage = new HashMap<String, Message>();
        this.muted = new HashMap<String, Integer>();
        this.timeMuted = new HashMap<String, Long>();
        this.banned = new HashMap<String, Long>();
        this.violations = new HashMap<String, Integer>();
    }
    
    @EventHandler
    public void onChat(final PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event.getPlayer().hasPermission("eroc.antispam.bypass")) {
            return;
        }
        if (!this.muted.containsKey(event.getPlayer().getName())) {
            if (this.lastMessage.get(event.getPlayer().getName()) != null) {
                if (this.stripSymbols(this.lastMessage.get(event.getPlayer().getName()).getMessage()).equalsIgnoreCase(this.stripSymbols(event.getMessage()))) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("§cMessage was not sent. Detected as spam.");
                    return;
                }
                if ((System.currentTimeMillis() - this.lastMessage.get(event.getPlayer().getName()).getTimeSent()) / 1000L + 1.0 <= 1.5) {
                    this.violations.put(event.getPlayer().getName(), this.violations.containsKey(event.getPlayer().getName()) ? (this.violations.get(event.getPlayer().getName()) + 1) : 1);
                    switch (this.violations.get(event.getPlayer().getName()) + 1) {
                        case 5: {
                            event.getPlayer().sendMessage("§c=========================================\nYou have been muted for spamming. [60 Seconds]\n10 WARNINGS REMAIN.\nYou will be temp-banned if you continue spamming!\n=========================================");
                            this.muted.put(event.getPlayer().getName(), 10);
                            this.timeMuted.put(event.getPlayer().getName(), System.currentTimeMillis());
                            this.violations.remove(event.getPlayer().getName());
                            event.setCancelled(true);
                            break;
                        }
                        default: {
                            if (this.muted.containsKey(event.getPlayer().getName())) {
                                return;
                            }
                            break;
                        }
                    }
                }
            }
            this.lastMessage.put(event.getPlayer().getName(), new Message(event.getMessage(), System.currentTimeMillis()));
            return;
        }
        if (this.timeMuted.get(event.getPlayer().getName()) != null && (System.currentTimeMillis() - this.timeMuted.get(event.getPlayer().getName())) / 1000L + 1L >= 60L) {
            this.timeMuted.remove(event.getPlayer().getName());
            this.muted.remove(event.getPlayer().getName());
            return;
        }
        event.setCancelled(true);
        if (this.muted.get(event.getPlayer().getName()) - 1 <= 0) {
            this.muted.remove(event.getPlayer().getName());
            this.banned.put(event.getPlayer().getName(), System.currentTimeMillis());
            event.getPlayer().kickPlayer("§cYou have been temp-banned for 30 minutes because: §fSpam");
            return;
        }
        final int warnings = this.muted.get(event.getPlayer().getName()) - 1;
        final int time = (int)((System.currentTimeMillis() - this.timeMuted.get(event.getPlayer().getName())) / 1000L) + 1;
        final int timeLeft = 60 - time;
        event.getPlayer().sendMessage(String.format("§c=========================================\nYou have been muted for spamming. [%d Seconds]\n%d WARNINGS REMAIN.\nYou will be temp-banned if you continue spamming!\n=========================================", timeLeft, warnings));
        this.muted.put(event.getPlayer().getName(), warnings);
    }
    
    @EventHandler
    public void onLogin(final AsyncPlayerPreLoginEvent event) {
        if (this.banned.containsKey(event.getName())) {
            final long time = System.currentTimeMillis() - this.banned.get(event.getName());
            if (time >= TimeUnit.MINUTE.getTime() * 30L) {
                this.banned.remove(event.getName());
                event.allow();
                return;
            }
            final long timeLeft = TimeUnit.MINUTE.getTime() * 30L - time;
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "\n§cYou have been temporarily banned for spamming.\n\n§f" + this.formatTime(timeLeft) + "§c minutes remain.");
        }
    }
    
    public String formatTime(final long time) {
        final long second = 1000L;
        final long minute = 60000L;
        final long minutes = time / minute;
        final long seconds = (time - minutes * minute) / second;
        if (seconds < 10L) {
            final String newSeconds = "0" + seconds;
            return minutes + ":" + newSeconds;
        }
        return minutes + ":" + seconds;
    }
    
    private String stripSymbols(final String withSymbols) {
        return StringUtils.deleteWhitespace(withSymbols).replaceAll("[^A-Za-z0-9]", "");
    }
    
    public static AntiSpam getInstance() {
        return AntiSpam.instance;
    }
    
    static {
        AntiSpam.instance = new AntiSpam();
    }
    
    private class Message
    {
        String message;
        Long timeSent;
        
        public String getMessage() {
            return this.message;
        }
        
        public Long getTimeSent() {
            return this.timeSent;
        }
        
        public void setMessage(final String message) {
            this.message = message;
        }
        
        public void setTimeSent(final Long timeSent) {
            this.timeSent = timeSent;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof Message)) {
                return false;
            }
            final Message other = (Message)o;
            if (!other.canEqual(this)) {
                return false;
            }
            final Object this$message = this.getMessage();
            final Object other$message = other.getMessage();
            Label_0065: {
                if (this$message == null) {
                    if (other$message == null) {
                        break Label_0065;
                    }
                }
                else if (this$message.equals(other$message)) {
                    break Label_0065;
                }
                return false;
            }
            final Object this$timeSent = this.getTimeSent();
            final Object other$timeSent = other.getTimeSent();
            if (this$timeSent == null) {
                if (other$timeSent == null) {
                    return true;
                }
            }
            else if (this$timeSent.equals(other$timeSent)) {
                return true;
            }
            return false;
        }
        
        protected boolean canEqual(final Object other) {
            return other instanceof Message;
        }
        
        @Override
        public int hashCode() {
            final int PRIME = 59;
            int result = 1;
            final Object $message = this.getMessage();
            result = result * 59 + (($message == null) ? 0 : $message.hashCode());
            final Object $timeSent = this.getTimeSent();
            result = result * 59 + (($timeSent == null) ? 0 : $timeSent.hashCode());
            return result;
        }
        
        @Override
        public String toString() {
            return "AntiSpam.Message(message=" + this.getMessage() + ", timeSent=" + this.getTimeSent() + ")";
        }
        
        public Message(final String message, final Long timeSent) {
            super();
            this.message = message;
            this.timeSent = timeSent;
        }
    }
}
