package com.mooo.ziggypop.candconline;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ziggypop on 3/28/15.
 */
public class PlayersAdapter extends ArrayAdapter<Player>{

    public ArrayList<Player> myPlayers;
    private boolean hasSetCount = false;

    public PlayersAdapter(Context context, int resource) {
        super(context, resource);
    }
    public PlayersAdapter(Context context, int resource, ArrayList<Player> players) {
        super(context, resource, players);
        myPlayers = players;
    }
    public View getView(int position, View convertView, ViewGroup parent) {



        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.players_layout, null);
        }

        Player player = getItem(position);
        String nickname = player.getNickname();

        //Log.v("PLAYER_POS", position + "");


        TextView textView = (TextView) v.findViewById(R.id.text);

        if(player.getIsHeader() || true){
            textView.setText(nickname);

            LinearLayout ll = (LinearLayout) v.findViewById(R.id.outerLayout);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            //textView.setTextSize(14);

            hasSetCount = true;
        }


        return v;

    }

}
