package com.mooo.ziggypop.candconline;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziggypop on 3/28/15.
 */
public class GamesAdapter extends ArrayAdapter<GameInLobby> {
    public GamesAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public GamesAdapter( Context context, int resource, ArrayList<GameInLobby> games) {
        super(context, resource, games);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    View v = convertView;

    if (v == null) {

        LayoutInflater vi;
        vi = LayoutInflater.from(getContext());
        v = vi.inflate(R.layout.games_layout, null);

    }

    GameInLobby p = getItem(position);

    if (p != null) {

        TextView tt = (TextView) v.findViewById(R.id.game_title);
        TextView tt1 = (TextView) v.findViewById(R.id.slots);
        ImageView tt3 = (ImageView) v.findViewById(R.id.lock);

        TextView tt4 = (TextView) v.findViewById(R.id.players);
        TextView tt2 = (TextView) v.findViewById(R.id.map);

        if (tt != null) {
            tt.setText(p.getTitle());
        }
        if (tt1 != null) {
            tt1.setText(p.getSlots());
        }
        if (tt2 != null) {
            tt2.setText(p.getMap());
        }
        if (tt3 != null) {
            if (p.getLockStatus()){
                tt3.setVisibility(View.VISIBLE);
            } else {
                tt3.setVisibility(View.GONE);
            }
        }
        if (tt4 != null) {
            tt4.setText(p.getPlayersFormat());
        }
    }

    return v;

    }
}
