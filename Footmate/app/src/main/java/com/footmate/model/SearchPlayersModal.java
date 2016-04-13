package com.footmate.model;

import java.io.Serializable;

/**
 * Created by patas tech on 3/30/2016.
 */
public class SearchPlayersModal implements Serializable {
    String About;
    String EmailId;
    String FollowStatus;
    String Id;
    String ImageUrl;
    String LoginType;
    String UserName;

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getFollowStatus() {
        return FollowStatus;
    }

    public void setFollowStatus(String followStatus) {
        FollowStatus = followStatus;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getLoginType() {
        return LoginType;
    }

    public void setLoginType(String loginType) {
        LoginType = loginType;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
