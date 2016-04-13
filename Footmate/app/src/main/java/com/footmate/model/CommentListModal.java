package com.footmate.model;

import java.io.Serializable;

/**
 * Created by d_RaMaN on 2/16/2016.
 */
public class CommentListModal implements Serializable {
    String Comment = "";
    String DateTimeOfComment = "";
    String ImageUrl = "";
    String MatchId = "";
    String PlayerId = "";
    String PlayerName = "";

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getDateTimeOfComment() {
        return DateTimeOfComment;
    }

    public void setDateTimeOfComment(String dateTimeOfComment) {
        DateTimeOfComment = dateTimeOfComment;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getMatchId() {
        return MatchId;
    }

    public void setMatchId(String matchId) {
        MatchId = matchId;
    }

    public String getPlayerId() {
        return PlayerId;
    }

    public void setPlayerId(String playerId) {
        PlayerId = playerId;
    }

    public String getPlayerName() {
        return PlayerName;
    }

    public void setPlayerName(String playerName) {
        PlayerName = playerName;
    }
}
