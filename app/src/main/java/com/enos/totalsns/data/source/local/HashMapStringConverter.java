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
        int size = hashMap.size();
        int last = size - 1;

        StringBuilder sb = new StringBuilder();
        int keyN = 0;
        for (String key : hashMap.keySet()) {
            sb.append(key);
            if (keyN < last) sb.append(";");
            keyN++;
        }

        sb.append(";;");

        int valueN = 0;
        for (String value : hashMap.values()) {
            sb.append(value);
            if (valueN < last) sb.append(";");
            valueN++;
        }
        return sb.toString();
    }
}
