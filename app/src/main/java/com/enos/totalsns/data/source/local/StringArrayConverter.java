/*
 * Copyright 2017, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.enos.totalsns.data.source.local;

import android.arch.persistence.room.TypeConverter;

public class StringArrayConverter {
    @TypeConverter
    public static String[] toStringArray(String arrayString) {
        return arrayString == null ? null : arrayString.split(";");
    }

    @TypeConverter
    public static String toString(String[] stringArray) {
        if (stringArray == null) return null;
        StringBuilder sb = new StringBuilder();
        int size = stringArray.length;
        int last = size - 1;
        for (int i = 0; i < size; i++) {
            sb.append(stringArray[i]);
            if (i < last) sb.append(";");
        }
        return sb.toString();
    }
}
