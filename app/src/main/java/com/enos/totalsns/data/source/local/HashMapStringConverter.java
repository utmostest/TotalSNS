package com.enos.totalsns.data.source.local;

import android.arch.persistence.room.TypeConverter;

import java.util.HashMap;

public class HashMapStringConverter {
    @TypeConverter
    public static HashMap<String, String> toHashMap(String hashMapString) {
        if (hashMapString == null) return null;

        HashMap<String, String> hashMap = new HashMap<>();
        String[] keyValue = hashMapString.split(";;");

        if (keyValue.length < 2) return null;

        String[] keys = keyValue[0].split(";");
        String[] values = keyValue[1].split(";");
        for (int i = 0; i < keys.length; i++) {
            hashMap.put(keys[i], values[i]);
        }
        return hashMap;
    }

    @TypeConverter
    public static String toString(HashMap<String, String> hashMap) {
        if (hashMap == null) return null;

        StringBuilder sb = new StringBuilder();
        for (String key : hashMap.keySet()) {
            sb.append(key).append(";");
        }
        sb.append(";");
        for (String value : hashMap.values()) {
            sb.append(value).append(";");
        }
        return sb.toString();
    }
}
