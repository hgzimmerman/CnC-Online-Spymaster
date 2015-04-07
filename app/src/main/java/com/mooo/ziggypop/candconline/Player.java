package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
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


    public static class PlayersAdapter extends ArrayAdapter<Player> {

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

            if (player.getIsHeader() || true) {
                textView.setText(nickname);

                LinearLayout ll = (LinearLayout) v.findViewById(R.id.outerLayout);
                ll.setGravity(Gravity.CENTER_HORIZONTAL);
                //textView.setTextSize(14);

                hasSetCount = true;
            }


            return v;

        }
    }




    public static class PlayersFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";


        private ArrayList<Player> wordsArr = new ArrayList<>();
        private ArrayAdapter<Player> mAdapter;
        private boolean isAttached = false;



        public PlayersFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);



            mAdapter = new PlayersAdapter(container.getContext(), R.layout.players_layout, wordsArr);

            setListAdapter(mAdapter);


            return rootView;
        }

        public void refreshData(final ArrayList<Player> data){
            //if (isAttached) {
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Log.v("PLAYER_FRAGMENT", "RUNNING");
                    mAdapter.clear();
                    //data.add(0, "Players Online: "+ data.size());
                    wordsArr.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }


            });
            //}
        }

        @Override
        public void onAttach(Activity activity){
            super.onAttach(activity);
            Log.v("PLAYERS", "IS_ATTACHED");
            isAttached = true;
        }




    }

}
