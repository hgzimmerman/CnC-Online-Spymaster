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
public class PlayersAdapter extends ArrayAdapter<String>{

    public ArrayList<String> myPlayers;
    private boolean hasSetCount = false;

    public PlayersAdapter(Context context, int resource) {
        super(context, resource);
    }
    public PlayersAdapter(Context context, int resource, ArrayList<String> players) {
        super(context, resource, players);
        myPlayers = players;
    }
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {

            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_card, null);
        }

        String player = getItem(position);



        // If the player name is long enough, check to see if it says: "Players Online:" and
        // if so, format it to make it stand out.
        /*
        if (!hasSetCount) {
            TextView textView = (TextView) v.findViewById(R.id.text);
            textView.setText(player);
            Log.v("PLAYER_MATCHED", player);
            LinearLayout ll = (LinearLayout) v.findViewById(R.id.outerLayout);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(20);
            hasSetCount=true;
        }else {
        */
            TextView textView = (TextView) v.findViewById(R.id.text);
            textView.setText(player);

        //}


        return v;

    }
    @Override
    public int getCount(){
        return myPlayers.size();

    }
}
