package com.breakmc.eroc.automessage;

import org.bukkit.scheduler.*;
import com.breakmc.eroc.*;
import org.json.simple.*;
import java.util.*;
import org.json.simple.parser.*;
import java.io.*;
import org.bukkit.*;

public class AutoMessageManager extends BukkitRunnable
{
    private List<Message> messages;
    private String prefix;
    int count;
    static AutoMessageManager messageManager;
    Random random;
    int num;
    
    protected AutoMessageManager() {
        super();
        this.messages = new ArrayList<Message>();
        this.prefix = "[Default] ";
        this.count = this.messages.size();
        this.random = new Random();
        this.num = 0;
    }
    
    public void addMessage(final String message) {
        final Message message2 = new Message(ChatColor.translateAlternateColorCodes('&', message));
        this.messages.add(message2);
        this.count = this.messages.size();
        this.save(((Eroc)Eroc.getPlugin((Class)Eroc.class)).getMessageFile());
    }
    
    public void setPrefix(final String prefix) {
        this.prefix = prefix;
    }
    
    public void save(final File file) {
        final JSONObject baseObj = new JSONObject();
        baseObj.put((Object)"prefix", (Object)this.prefix);
        final JSONArray array = new JSONArray();
        for (final Message message : this.messages) {
            final JSONObject object = new JSONObject();
            object.put((Object)"message", (Object)message.getMessage());
            array.add((Object)object);
        }
        baseObj.put((Object)"messages", (Object)array);
        try {
            final FileWriter writer = new FileWriter(file);
            baseObj.writeJSONString((Writer)writer);
            writer.flush();
            writer.close();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void setCount(final int count) {
        this.count = count;
    }
    
    public void load(final File file) {
        final JSONParser parser = new JSONParser();
        try {
            final FileReader reader = new FileReader(file);
            final JSONObject object = (JSONObject)parser.parse((Reader)reader);
            this.setPrefix((String)object.get((Object)"prefix"));
            final JSONArray array = (JSONArray)object.get((Object)"messages");
            for (final Object obj : array) {
                final JSONObject serializedMessage = (JSONObject)obj;
                final String message = (String)serializedMessage.get((Object)"message");
                final Message message2 = new Message(message);
                this.messages.add(message2);
            }
            reader.close();
            this.setCount(this.messages.size());
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public static AutoMessageManager getInstance() {
        return AutoMessageManager.messageManager;
    }
    
    public void run() {
        if (this.messages.size() == 0) {
            System.out.println("Messages is empty! Not broadcasting!");
        }
        else {
            if (this.num >= this.messages.size()) {
                this.num = 0;
            }
            Bukkit.broadcastMessage(this.prefix + " " + this.messages.get(this.num).getMessage());
            ++this.num;
        }
    }
    
    public List<Message> getMessages() {
        return this.messages;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    static {
        AutoMessageManager.messageManager = new AutoMessageManager();
    }
}
