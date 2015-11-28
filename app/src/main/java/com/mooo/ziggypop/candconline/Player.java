package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ziggypop on 3/30/15.
 *
 * Holds the data for the players in the game
 * Wraps Adapter and Fragment Subclasses
 *
 */
public class Player {

    private String nickname;
    private String id;
    private String pid;

    public Player(String nickname, String id, String pid){
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
    }

    public String getNickname(){
        return nickname;
    }


    /*
     * Adapter used with the PlayersFragment
     */
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



            TextView textView = (TextView) v.findViewById(R.id.text);

            textView.setText(nickname);

            LinearLayout ll = (LinearLayout) v.findViewById(R.id.outerLayout);
            ll.setGravity(Gravity.CENTER_HORIZONTAL);

            hasSetCount = true;


            return v;

        }
    }




    /*
     * TODO when clicked, expand to show other player information (Id vs name)
     */
    public static class PlayersFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";


        public ArrayList<Player> wordsArr = new ArrayList<>();
        private PlayersAdapter mAdapter;
        private boolean isAttached = false;
        private Activity mActivity;


        LayoutInflater inflater;
        ViewGroup container;
        Bundle savedInstanceState;
        View rootView;




        public PlayersFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mAdapter = new PlayersAdapter(getActivity(), R.layout.players_layout, wordsArr);
            setListAdapter(mAdapter);

            this.inflater=inflater;
            this.container=container;
            this.savedInstanceState = savedInstanceState;

            rootView =inflater.inflate(R.layout.fragment_list_view,container,false);

            setRetainInstance(true);//This prevents the GCing of the fragment.
            return rootView;
        }

        public boolean refreshData(final ArrayList<Player> data, final MainActivity activity){

            if(!isAdded())
                mAdapter = new PlayersAdapter(activity, R.layout.players_layout, wordsArr);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.v("PLAYER_FRAGMENT", "RUNNING");
                    mAdapter.clear();
                    wordsArr.addAll(data);
                    mAdapter.notifyDataSetChanged();
                    Log.e("Num_PLAYERS_ADAPTER", mAdapter.getCount() + "");
                }
            });
            return true;

        }

        @Override
        public void onResume() {
            super.onResume();
            Log.e("Players", "Resumed");
        }

        @Override
        public void onAttach(Activity activity){
            super.onAttach(activity);
            Log.e("PLAYERS", "IS_ATTACHED");
            Log.e("PLAYERS", activity.toString());
            //refreshData(wordsArr,(MainActivity) activity);

            //isAttached = true;
            //mActivity = activity;
        }


    }

}
