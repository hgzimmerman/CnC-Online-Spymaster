package com.mooo.ziggypop.candconline;

import java.util.ArrayList;

/**
 * Created by ziggypop on 3/28/15.
 */
public class Game {
    private String gameTitle;
    private String lobbySlots;
    private ArrayList<String> players;
    private boolean isLocked;
    private String map;


    public Game(String title, String slots, ArrayList<String> players,
                boolean isLocked, String map) {
        gameTitle = title;
        lobbySlots = slots;
        this.map = map;
        this.players = players;
        this.isLocked = isLocked;
    }

    public String getTitle(){
        return gameTitle;
    }
    public String getSlots() {
        return lobbySlots;
    }
    public String getMap(){
        return map;
    }
    public ArrayList<String> getPlayersArray(){
        return players;
    }
    public String getPlayersFormat(){
        String retVal = "";
        for(String element: players) {
            retVal += element +"\n";
        }
        return retVal;
    }
    public boolean getLockStatus(){
        return isLocked;
    }
}
