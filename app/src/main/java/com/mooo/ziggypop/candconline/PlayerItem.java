package com.mooo.ziggypop.candconline;

/**
 * Created by ziggypop on 3/28/15.
 */
class PlayerItem {
    private String itemTitle;

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public PlayerItem(String title){
        this.itemTitle = title;
    }
}
