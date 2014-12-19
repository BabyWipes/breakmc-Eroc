package org.json;

import java.lang.reflect.*;
import java.util.*;
import java.io.*;

public class JSONArray
{
    private final ArrayList<Object> myArrayList;
    
    public JSONArray() {
        super();
        this.myArrayList = new ArrayList<Object>();
    }
    
    public JSONArray(final JSONTokener x) throws JSONException {
        this();
        if (x.nextClean() != '[') {
            throw x.syntaxError("A JSONArray text must start with '['");
        }
        if (x.nextClean() == ']') {
            return;
        }
        x.back();
        while (true) {
            if (x.nextClean() == ',') {
                x.back();
                this.myArrayList.add(JSONObject.NULL);
            }
            else {
                x.back();
                this.myArrayList.add(x.nextValue());
            }
            switch (x.nextClean()) {
                case ',':
                case ';': {
                    if (x.nextClean() == ']') {
                        return;
                    }
                    x.back();
                    continue;
                }
                case ']': {}
                default: {
                    throw x.syntaxError("Expected a ',' or ']'");
                }
            }
        }
    }
    
    public JSONArray(final String source) throws JSONException {
        this(new JSONTokener(source));
    }
    
    public JSONArray(final Collection<?> collection) {
        super();
        this.myArrayList = new ArrayList<Object>();
        if (collection != null) {
            final Iterator<?> iter = collection.iterator();
            while (iter.hasNext()) {
                this.myArrayList.add(JSONObject.wrap(iter.next()));
            }
        }
    }
    
    public JSONArray(final Object array) throws JSONException {
        this();
        if (array.getClass().isArray()) {
            for (int length = Array.getLength(array), i = 0; i < length; ++i) {
                this.put(JSONObject.wrap(Array.get(array, i)));
            }
            return;
        }
        throw new JSONException("JSONArray initial value should be a string or collection or array.");
    }
    
    public Object get(final int index) throws JSONException {
        final Object object = this.opt(index);
        if (object == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return object;
    }
    
    public boolean getBoolean(final int index) throws JSONException {
        final Object object = this.get(index);
        if (object.equals(Boolean.FALSE) || (object instanceof String && ((String)object).equalsIgnoreCase("false"))) {
            return false;
        }
        if (object.equals(Boolean.TRUE) || (object instanceof String && ((String)object).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONArray[" + index + "] is not a boolean.");
    }
    
    public double getDouble(final int index) throws JSONException {
        final Object object = this.get(index);
        try {
            return (object instanceof Number) ? ((Number)object).doubleValue() : Double.parseDouble((String)object);
        }
        catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }
    
    public int getInt(final int index) throws JSONException {
        final Object object = this.get(index);
        try {
            return (object instanceof Number) ? ((Number)object).intValue() : Integer.parseInt((String)object);
        }
        catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }
    
    public JSONArray getJSONArray(final int index) throws JSONException {
        final Object object = this.get(index);
        if (object instanceof JSONArray) {
            return (JSONArray)object;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
    }
    
    public JSONObject getJSONObject(final int index) throws JSONException {
        final Object object = this.get(index);
        if (object instanceof JSONObject) {
            return (JSONObject)object;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
    }
    
    public long getLong(final int index) throws JSONException {
        final Object object = this.get(index);
        try {
            return (object instanceof Number) ? ((Number)object).longValue() : Long.parseLong((String)object);
        }
        catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }
    
    public String getString(final int index) throws JSONException {
        final Object object = this.get(index);
        if (object instanceof String) {
            return (String)object;
        }
        throw new JSONException("JSONArray[" + index + "] not a string.");
    }
    
    public boolean isNull(final int index) {
        return JSONObject.NULL.equals(this.opt(index));
    }
    
    public String join(final String separator) throws JSONException {
        final int len = this.length();
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            if (i > 0) {
                sb.append(separator);
            }
            sb.append(JSONObject.valueToString(this.myArrayList.get(i)));
        }
        return sb.toString();
    }
    
    public int length() {
        return this.myArrayList.size();
    }
    
    public Object opt(final int index) {
        return (index < 0 || index >= this.length()) ? null : this.myArrayList.get(index);
    }
    
    public boolean optBoolean(final int index) {
        return this.optBoolean(index, false);
    }
    
    public boolean optBoolean(final int index, final boolean defaultValue) {
        try {
            return this.getBoolean(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public double optDouble(final int index) {
        return this.optDouble(index, Double.NaN);
    }
    
    public double optDouble(final int index, final double defaultValue) {
        try {
            return this.getDouble(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public int optInt(final int index) {
        return this.optInt(index, 0);
    }
    
    public int optInt(final int index, final int defaultValue) {
        try {
            return this.getInt(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public JSONArray optJSONArray(final int index) {
        final Object o = this.opt(index);
        return (o instanceof JSONArray) ? ((JSONArray)o) : null;
    }
    
    public JSONObject optJSONObject(final int index) {
        final Object o = this.opt(index);
        return (o instanceof JSONObject) ? ((JSONObject)o) : null;
    }
    
    public long optLong(final int index) {
        return this.optLong(index, 0L);
    }
    
    public long optLong(final int index, final long defaultValue) {
        try {
            return this.getLong(index);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }
    
    public String optString(final int index) {
        return this.optString(index, "");
    }
    
    public String optString(final int index, final String defaultValue) {
        final Object object = this.opt(index);
        return JSONObject.NULL.equals(object) ? defaultValue : object.toString();
    }
    
    public JSONArray put(final boolean value) {
        this.put(value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONArray put(final Collection<?> value) {
        this.put(new JSONArray(value));
        return this;
    }
    
    public JSONArray put(final double value) throws JSONException {
        final Double d = new Double(value);
        JSONObject.testValidity(d);
        this.put(d);
        return this;
    }
    
    public JSONArray put(final int value) {
        this.put(new Integer(value));
        return this;
    }
    
    public JSONArray put(final long value) {
        this.put(new Long(value));
        return this;
    }
    
    public JSONArray put(final Map<?, ?> value) {
        this.put(new JSONObject(value));
        return this;
    }
    
    public JSONArray put(final Object value) {
        this.myArrayList.add(value);
        return this;
    }
    
    public JSONArray put(final int index, final boolean value) throws JSONException {
        this.put(index, value ? Boolean.TRUE : Boolean.FALSE);
        return this;
    }
    
    public JSONArray put(final int index, final Collection<?> value) throws JSONException {
        this.put(index, new JSONArray(value));
        return this;
    }
    
    public JSONArray put(final int index, final double value) throws JSONException {
        this.put(index, new Double(value));
        return this;
    }
    
    public JSONArray put(final int index, final int value) throws JSONException {
        this.put(index, new Integer(value));
        return this;
    }
    
    public JSONArray put(final int index, final long value) throws JSONException {
        this.put(index, new Long(value));
        return this;
    }
    
    public JSONArray put(final int index, final Map<?, ?> value) throws JSONException {
        this.put(index, new JSONObject(value));
        return this;
    }
    
    public JSONArray put(final int index, final Object value) throws JSONException {
        JSONObject.testValidity(value);
        if (index < 0) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        if (index < this.length()) {
            this.myArrayList.set(index, value);
        }
        else {
            while (index != this.length()) {
                this.put(JSONObject.NULL);
            }
            this.put(value);
        }
        return this;
    }
    
    public Object remove(final int index) {
        final Object o = this.opt(index);
        this.myArrayList.remove(index);
        return o;
    }
    
    public JSONObject toJSONObject(final JSONArray names) throws JSONException {
        if (names == null || names.length() == 0 || this.length() == 0) {
            return null;
        }
        final JSONObject jo = new JSONObject();
        for (int i = 0; i < names.length(); ++i) {
            jo.put(names.getString(i), this.opt(i));
        }
        return jo;
    }
    
    @Override
    public String toString() {
        try {
            return this.toString(0);
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public String toString(final int indentFactor) throws JSONException {
        final StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            return this.write(sw, indentFactor, 0).toString();
        }
    }
    
    public Writer write(final Writer writer) throws JSONException {
        return this.write(writer, 0, 0);
    }
    
    Writer write(final Writer writer, final int indentFactor, final int indent) throws JSONException {
        try {
            boolean commanate = false;
            final int length = this.length();
            writer.write(91);
            if (length == 1) {
                JSONObject.writeValue(writer, this.myArrayList.get(0), indentFactor, indent);
            }
            else if (length != 0) {
                final int newindent = indent + indentFactor;
                for (int i = 0; i < length; ++i) {
                    if (commanate) {
                        writer.write(44);
                    }
                    if (indentFactor > 0) {
                        writer.write(10);
                    }
                    JSONObject.indent(writer, newindent);
                    JSONObject.writeValue(writer, this.myArrayList.get(i), indentFactor, newindent);
                    commanate = true;
                }
                if (indentFactor > 0) {
                    writer.write(10);
                }
                JSONObject.indent(writer, indent);
            }
            writer.write(93);
            return writer;
        }
        catch (IOException e) {
            throw new JSONException(e);
        }
    }
}
