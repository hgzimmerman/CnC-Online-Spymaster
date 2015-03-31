package com.mooo.ziggypop.candconline;

/**
 * Created by ziggypop on 3/31/15.
 *
 */
public class DrawerItem {
    private String gameTitle;
    private int playerCount;

    public DrawerItem(String title, int playerCount){
        this.gameTitle = title;
        this.playerCount = playerCount;
    }

    public String getGameTitle(){
        return gameTitle;
    }
    public int getPlayerCount(){
        return playerCount;
    }
    public void updatePlayerCount(int i){
        playerCount = i;
    }
}
