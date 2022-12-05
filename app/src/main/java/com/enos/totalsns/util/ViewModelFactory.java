/*
 *  Copyright 2017 Google Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.enos.totalsns.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.enos.totalsns.ContentsViewModel;
import com.enos.totalsns.accounts.AccountsViewModel;
import com.enos.totalsns.data.source.TotalSnsRepository;
import com.enos.totalsns.intro.IntroViewModel;
import com.enos.totalsns.login.LoginViewModel;
import com.enos.totalsns.mention.MentionListViewModel;
import com.enos.totalsns.message.detail.MessageDetailViewModel;
import com.enos.totalsns.message.list.MessageListViewModel;
import com.enos.totalsns.message.send.MessageSendViewModel;
import com.enos.totalsns.nearby.NearbyArticleViewModel;
import com.enos.totalsns.profile.ProfileViewModel;
import com.enos.totalsns.search.SearchViewModel;
import com.enos.totalsns.timeline.detail.TimelineDetailViewModel;
import com.enos.totalsns.timeline.list.TimelineListViewModel;
import com.enos.totalsns.timeline.write.TimelineWriteViewModel;
import com.enos.totalsns.userlist.UserListViewModel;

/**
 * A creator is used to inject the product ID into the ViewModel
 * <p>
 * This creator is to showcase how to inject dependencies into ViewModels. It's not
 * actually necessary in this case, as the product ID can be passed in a public method.
 */
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @SuppressLint("StaticFieldLeak")
    private static volatile ViewModelFactory INSTANCE;

    private final Context mContext;

    private final TotalSnsRepository totalSnsRepository;

    public static ViewModelFactory getInstance(Context context) {

        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory(context,
                            InjectorUtils.provideRepository(context.getApplicationContext()));
                }
            }
        }
        return INSTANCE;
    }

    @VisibleForTesting
    public static void destroyInstance() {
        INSTANCE = null;
    }

    private ViewModelFactory(Context context, TotalSnsRepository repository) {
        mContext = context;
        totalSnsRepository = repository;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(IntroViewModel.class)) {
            //noinspection unchecked
            return (T) new IntroViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(AccountsViewModel.class)) {
            //noinspection unchecked
            return (T) new AccountsViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            //noinspection unchecked
            return (T) new LoginViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(TimelineWriteViewModel.class)) {
            //noinspection unchecked
            return (T) new TimelineWriteViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(ContentsViewModel.class)) {
            //noinspection unchecked
            return (T) new ContentsViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(TimelineDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new TimelineDetailViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(TimelineListViewModel.class)) {
            //noinspection unchecked
            return (T) new TimelineListViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(MessageListViewModel.class)) {
            //noinspection unchecked
            return (T) new MessageListViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(MentionListViewModel.class)) {
            //noinspection unchecked
            return (T) new MentionListViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(MessageDetailViewModel.class)) {
            //noinspection unchecked
            return (T) new MessageDetailViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(SearchViewModel.class)) {
            //noinspection unchecked
            return (T) new SearchViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            //noinspection unchecked
            return (T) new ProfileViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(UserListViewModel.class)) {
            //noinspection unchecked
            return (T) new UserListViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(MessageSendViewModel.class)) {
            //noinspection unchecked
            return (T) new MessageSendViewModel(mContext, totalSnsRepository);
        } else if (modelClass.isAssignableFrom(NearbyArticleViewModel.class)) {
            //noinspection unchecked
            return (T) new NearbyArticleViewModel(mContext, totalSnsRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
