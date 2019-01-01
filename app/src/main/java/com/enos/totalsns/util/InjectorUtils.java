/*
 * Copyright (C) 2017 The Android Open Source Project
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

package com.enos.totalsns.util;

import android.content.Context;

import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.data.source.local.TotalSnsDatabase;
import com.enos.totalsns.data.source.remote.TwitterManager;

/**
 * Provides static methods to inject the various classes needed for Sunshine
 */
public class InjectorUtils {

    public static TotalSnsRepository provideRepository(Context context) {
        TotalSnsDatabase database = provideDatabase(context);
        TwitterManager twitterSource = provideNetworkDataSource();
        return TotalSnsRepository.getInstance(database, twitterSource);
    }

    public static TotalSnsDatabase provideDatabase(Context context) {
        return TotalSnsDatabase.getInstance(context.getApplicationContext());
    }

    public static TwitterManager provideNetworkDataSource() {
        return TwitterManager.getInstance();
    }
}