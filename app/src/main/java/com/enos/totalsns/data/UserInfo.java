package com.enos.totalsns.data;

import com.enos.totalsns.search.Search;

// 룸에 저장하지 않음
public class UserInfo implements Search {

    private long longUserId;
    private String userId;
    private String userName;
    private String message;
    private String profileImg;
    private String profileBackImg;
    private String profileBackColor;
    private int snsType;

    public UserInfo(){

    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public String getUserName() {
        return userName;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getProfileImg() {
        return profileImg;
    }

    @Override
    public int getSnsType() {
        return snsType;
    }
}
