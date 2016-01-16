package com.mooo.ziggypop.candconline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;

/**
 * Created by ziggypop on 3/31/15.
 * Adapts Drawers
 */
public class DrawerAdapter extends ArrayAdapter<DrawerItem> {

    public DrawerAdapter(Context context, int resource, ArrayList<DrawerItem> items){
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.drawer_item_layout, null);
        }

        DrawerItem p = getItem(position);

        if (p != null) {
            TextView title = (TextView) v.findViewById(R.id.drawer_game_title);
            TextView playerCount = (TextView) v.findViewById(R.id.drawer_player_count);

            title.setText(p.getGameTitle());
            String playerCountText = String.format("(%s)",p.getPlayerCount());
            playerCount.setText(playerCountText);

        }
        return v;
    }
}
