package com.footmate.model;

import java.io.Serializable;

/**
 * Created by patas tech on 3/14/2016.
 */
public class GamesTypeModal implements Serializable {
    String GameType = "";
    String GameNumbers = "";

    public String getGameType() {
        return GameType;
    }

    public void setGameType(String gameType) {
        GameType = gameType;
    }

    public String getGameNumbers() {
        return GameNumbers;
    }

    public void setGameNumbers(String gameNumbers) {
        GameNumbers = gameNumbers;
    }
}
