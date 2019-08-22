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


import android.content.Context;

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Article;
import com.enos.totalsns.data.Message;

import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {Account.class, Article.class, Message.class}, version = 1)
@TypeConverters({DateConverter.class, StringArrayConverter.class, HashMapStringConverter.class})
public abstract class TotalSnsDatabase extends RoomDatabase {

    private static volatile TotalSnsDatabase sInstance;
    private static final Object LOCK = new Object();

    @VisibleForTesting
    public static final String DATABASE_NAME = "total-sns-db";

    public abstract AccountDao accountDao();

    public abstract ArticleDao articleDao();

    public abstract MessageDao messageDao();

    public static TotalSnsDatabase getInstance(final Context context) {
        if (sInstance == null) {
            synchronized (LOCK) {
                if (sInstance == null) {
                    sInstance = Room.databaseBuilder(context.getApplicationContext(),
                            TotalSnsDatabase.class, TotalSnsDatabase.DATABASE_NAME).
                            addMigrations(MIGRATION_1_2, MIGRATION_2_3).build();
                }
            }
        }
        return sInstance;
    }

    public void updateCurrentUser(Account current, int snsType) {

        if (current == null) return;

        accountDao().updateSignOutBySns(snsType);

        if (!current.isCurrent()) current.setCurrent(true);
        accountDao().insertAccount(current);
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Fruit` (`id` INTEGER, "
                    + "`name` TEXT, PRIMARY KEY(`id`))");
        }
    };

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE Book "
                    + " ADD COLUMN pub_year INTEGER");
        }
    };
}
