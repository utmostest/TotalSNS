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

import com.enos.totalsns.data.Account;
import com.enos.totalsns.data.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates data to pre-populate the database
 */
public class DataGenerator {

    //TODO SNS계정 더미 데이터 수정
    public static List<Account> generateAccounts() {
        List<Account> accounts = new ArrayList<>(4);
        Account account = new Account(21338, "fake_facebook1", "", "", "", Constants.FACEBOOK, true);
        accounts.add(account);
        account = new Account(5164, "fake_instagram1", "", "", "", Constants.INSTAGRAM, true);
        accounts.add(account);
        account = new Account(44964, "fake_facebook2", "", "", "", Constants.FACEBOOK, false);
        accounts.add(account);
        account = new Account(8799, "fake_instagram2", "", "", "", Constants.INSTAGRAM, false);
        accounts.add(account);
        return accounts;
    }
//
//    public static List<CommentEntity> generateCommentsForProducts(
//            final List<ProductEntity> products) {
//        List<CommentEntity> comments = new ArrayList<>();
//        Random rnd = new Random();
//
//        for (Product product : products) {
//            int commentsNumber = rnd.nextInt(5) + 1;
//            for (int i = 0; i < commentsNumber; i++) {
//                CommentEntity comment = new CommentEntity();
//                comment.setProductId(product.getId());
//                comment.setText(COMMENTS[i] + " for " + product.getName());
//                comment.setPostedAt(new Date(System.currentTimeMillis()
//                        - TimeUnit.DAYS.toMillis(commentsNumber - i) + TimeUnit.HOURS.toMillis(i)));
//                comments.add(comment);
//            }
//        }
//
//        return comments;
//    }
}
