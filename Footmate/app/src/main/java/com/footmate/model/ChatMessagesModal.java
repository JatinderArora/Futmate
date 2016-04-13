package com.footmate.model;

/**
 * Created by patas tech on 4/8/2016.
 */
public class ChatMessagesModal {
    String DateTimeOfMessage;
    String DeviceId;
    String ImageUrl;
    String MatchId;
    String Message;
    String PlayerId;
    String PlayerName;

    public String getDateTimeOfMessage() {
        return DateTimeOfMessage;
    }

    public void setDateTimeOfMessage(String dateTimeOfMessage) {
        DateTimeOfMessage = dateTimeOfMessage;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
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

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
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
