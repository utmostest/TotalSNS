package com.enos.totalsns;

import com.enos.totalsns.data.source.local.HashMapStringConverter;
import com.enos.totalsns.data.source.local.StringArrayConverter;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    @Test
    public void convert_isCorret() {
        checkConvertedStringArrayEqual();
        hash_map_convert_isCorret();
    }

    private void checkConvertedStringArrayEqual() {
        String[] strs = new String[]{"hello", "hi", "there", "where", "am", "i"};
        String[] strs2 = StringArrayConverter.toStringArray(StringArrayConverter.toString(strs));
        assertEquals(strs.length, strs2.length);
        for (int i = 0; i < strs2.length; i++) {
            assertEquals(strs[i], strs2[i]);
        }
    }

    private void hash_map_convert_isCorret() {
        HashMap<String, String> hashMap = new HashMap<>();
        for (int i = 0; i < 5; i++) {
            hashMap.put("key" + i, "value" + i);
        }
        HashMap<String, String> hashMap2 = HashMapStringConverter.toHashMap(HashMapStringConverter.toString(hashMap));
        assertEquals(hashMap.size(), hashMap2.size());
        for (String key2 : hashMap2.keySet()) {
            assertTrue(hashMap.containsKey(key2));
        }
        for (String value2 : hashMap2.values()) {
            assertTrue(hashMap.containsValue(value2));
        }
    }
}