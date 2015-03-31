package com.mooo.ziggypop.candconline;

/**
 * Created by ziggypop on 3/30/15.
 */
public class Player {
    private String nickname;
    private boolean isHeader = false;

    public Player(String nickname, boolean isHeader){
        this.nickname = nickname;
        this.isHeader = isHeader;
    }
    public String getNickname(){
        return nickname;
    }
    public boolean getIsHeader(){
        return isHeader;
    }
}
