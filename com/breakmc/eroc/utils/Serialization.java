package com.breakmc.eroc.utils;

import org.json.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.configuration.serialization.*;

public class Serialization
{
    public static Map<String, Object> toMap(final JSONObject object) throws JSONException {
        final Map<String, Object> map = new HashMap<String, Object>();
        final Iterator<String> keys = (Iterator<String>)object.keys();
        while (keys.hasNext()) {
            final String key = keys.next();
            map.put(key, fromJson(object.get(key)));
        }
        return map;
    }
    
    private static Object fromJson(final Object json) throws JSONException {
        if (json == JSONObject.NULL) {
            return null;
        }
        if (json instanceof JSONObject) {
            return toMap((JSONObject)json);
        }
        if (json instanceof JSONArray) {
            return toList((JSONArray)json);
        }
        return json;
    }
    
    public static List<Object> toList(final JSONArray array) throws JSONException {
        final List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); ++i) {
            list.add(fromJson(array.get(i)));
        }
        return list;
    }
    
    public static List<String> toString(final Inventory inv) {
        final List<String> result = new ArrayList<String>();
        final List<ConfigurationSerializable> items = new ArrayList<ConfigurationSerializable>();
        Collections.addAll((Collection<? super ItemStack>)items, inv.getContents());
        for (final ConfigurationSerializable cs : items) {
            if (cs == null) {
                result.add("null");
            }
            else {
                result.add(new JSONObject(serialize(cs)).toString());
            }
        }
        return result;
    }
    
    public static Inventory toInventory(final List<String> stringItems, final int size) {
        final Inventory inv = Bukkit.createInventory((InventoryHolder)null, size, "Ender Chest");
        final List<ItemStack> contents = new ArrayList<ItemStack>();
        for (final String piece : stringItems) {
            if (piece.equalsIgnoreCase("null")) {
                contents.add(null);
            }
            else {
                try {
                    final ItemStack item = (ItemStack)deserialize(toMap(new JSONObject(piece)));
                    contents.add(item);
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        final ItemStack[] items = new ItemStack[contents.size()];
        for (int x = 0; x < contents.size(); ++x) {
            items[x] = contents.get(x);
        }
        inv.setContents(items);
        return inv;
    }
    
    public static Map<String, Object> serialize(final ConfigurationSerializable cs) {
        final Map<String, Object> returnVal = handleSerialization(cs.serialize());
        returnVal.put("==", ConfigurationSerialization.getAlias((Class)cs.getClass()));
        return returnVal;
    }
    
    private static Map<String, Object> handleSerialization(final Map<String, Object> map) {
        final Map<String, Object> serialized = recreateMap(map);
        for (final Map.Entry<String, Object> entry : serialized.entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable) {
                entry.setValue(serialize(entry.getValue()));
            }
            else if (entry.getValue() instanceof Iterable) {
                final List<Object> newList = new ArrayList<Object>();
                for (Object object : entry.getValue()) {
                    if (object instanceof ConfigurationSerializable) {
                        object = serialize((ConfigurationSerializable)object);
                    }
                    newList.add(object);
                }
                entry.setValue(newList);
            }
            else {
                if (!(entry.getValue() instanceof Map)) {
                    continue;
                }
                entry.setValue(handleSerialization(entry.getValue()));
            }
        }
        return serialized;
    }
    
    public static Map<String, Object> recreateMap(final Map<String, Object> original) {
        final Map<String, Object> map = new HashMap<String, Object>();
        map.putAll(original);
        return map;
    }
    
    public static ConfigurationSerializable deserialize(final Map<String, Object> map) {
        for (final Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map && entry.getValue().containsKey("==")) {
                entry.setValue(deserialize(entry.getValue()));
            }
            else {
                if (!(entry.getValue() instanceof Iterable)) {
                    continue;
                }
                entry.setValue(convertIterable(entry.getValue()));
            }
        }
        return ConfigurationSerialization.deserializeObject((Map)map);
    }
    
    private static List<?> convertIterable(final Iterable<?> iterable) {
        final List<Object> newList = new ArrayList<Object>();
        for (Object object : iterable) {
            if (object instanceof Map) {
                object = deserialize((Map<String, Object>)object);
            }
            else if (object instanceof List) {
                object = convertIterable((Iterable<?>)object);
            }
            newList.add(object);
        }
        return newList;
    }
}
