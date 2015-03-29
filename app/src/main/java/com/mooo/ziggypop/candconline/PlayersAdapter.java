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
    public PlayersAdapter(Context context, int resource) {
        super(context, resource);
    }
    public PlayersAdapter(Context context, int resource, String[] players) {
        super(context, resource, players);
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
}
