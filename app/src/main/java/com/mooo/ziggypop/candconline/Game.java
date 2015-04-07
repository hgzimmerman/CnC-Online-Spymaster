package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziggypop on 3/28/15.
 */
public class Game {
    private String gameTitle;
    private String lobbySlots;
    private ArrayList<String> players;
    private boolean isLocked;
    private String map;


    public Game(String title, String slots, ArrayList<String> players,
                boolean isLocked, String map) {
        gameTitle = title;
        lobbySlots = slots;
        this.map = map;
        this.players = players;
        this.isLocked = isLocked;
    }

    public String getTitle(){
        return gameTitle;
    }
    public String getSlots() {
        return lobbySlots;
    }
    public String getMap(){
        return map;
    }
    public ArrayList<String> getPlayersArray(){
        return players;
    }
    public String getPlayersFormat(){
        String retVal = "";
        for(String element: players) {
            retVal += element +"\n";
        }
        retVal = retVal.substring(0, retVal.length()-2);
        return retVal;
    }
    public boolean getLockStatus(){
        return isLocked;
    }










    public static class GamesAdapter extends ArrayAdapter<Game> {
        public GamesAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        public GamesAdapter( Context context, int resource, ArrayList<Game> games) {
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

            Game p = getItem(position);

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



    public static class GamesFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";




        private List myList;
        private GamesAdapter mAdapter;
        private static ArrayList<Game> games = new ArrayList<>();




        public GamesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

            mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
            setListAdapter(mAdapter);


            return rootView;
        }

        public void refreshData(final ArrayList<Game> data){
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.v("GAMES_IN_LOBBY", "RUNNING");
                        mAdapter.clear();
                        games.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }


                });

            }
        }

    }



    public static class GamesInProgressFragment extends ListFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";




    private GamesAdapter mAdapter;
    private static ArrayList<Game> games = new ArrayList<>();
    private static ArrayList<Game> failedToLoadGames = new ArrayList<>();



    public GamesInProgressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

        mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
        setListAdapter(mAdapter);

        return rootView;
    }

    /*
     * Refreshes the data used in this "tab" fragment.
     */
    public void refreshData(final ArrayList<Game> data, Activity activity){
        // This is a hacky way of avoiding a crash caused by this fragment not being added
        // fast enough- calling the getActivity() method will return null.
        // So this method takes an activity as well, in order to create a new adapter if
        // this hasn't attached yet.
        if(!isAdded()){
            mAdapter = new GamesAdapter(activity, R.layout.games_layout, games);
        }
        Log.v("GamesInProgressFragment", "RUNNING");
        mAdapter.clear();
        games.addAll(data);
        mAdapter.notifyDataSetChanged();

    }

}




}
