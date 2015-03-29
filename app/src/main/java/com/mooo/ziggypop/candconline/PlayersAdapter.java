package com.mooo.ziggypop.candconline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ziggypop on 3/28/15.
 */
public class PlayersAdapter extends ArrayAdapter<String>{

    public ArrayList<String> myPlayers;

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

        TextView textView = (TextView) v.findViewById(R.id.text);

        textView.setText(player);




        return v;

    }
    @Override
    public int getCount(){
        return myPlayers.size();

    }
}
