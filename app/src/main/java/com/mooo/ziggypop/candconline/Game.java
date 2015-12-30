package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ziggypop on 3/28/15.
 * Deals with the fragments that display info about games in lobbies and in progress.
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
    //TODO Adapt these into a 2xn/2 arrangement instead of inserting newlines.
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

                TextView gameTitleText = (TextView) v.findViewById(R.id.game_title);
                TextView slotsText = (TextView) v.findViewById(R.id.slots);
                ImageView lockImage = (ImageView) v.findViewById(R.id.lock);

                TextView playersText = (TextView) v.findViewById(R.id.players);
                TextView mapNameText = (TextView) v.findViewById(R.id.map);

                if (gameTitleText != null) {
                    gameTitleText.setText(p.getTitle());
                }
                if (slotsText != null) {
                    slotsText.setText(p.getSlots());
                }
                if (mapNameText != null) {
                    mapNameText.setText(p.getMap());
                }
                if (lockImage != null) {
                    if (p.getLockStatus()){
                        lockImage.setVisibility(View.VISIBLE);
                    } else {
                        lockImage.setVisibility(View.GONE);
                    }
                }
                if (playersText != null) {
                    playersText.setText(p.getPlayersFormat());
                }
            }

            return v;

        }
    }



    public static class GamesFragment extends ListFragment {
        private static final String TAG = "GamesInLobbyFragment";

        private GamesAdapter mAdapter;
        private static ArrayList<Game> games = new ArrayList<>();
        private ListView listView;


        public GamesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

            mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
            setListAdapter(mAdapter);

            listView = (ListView) rootView.findViewById(android.R.id.list);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) { }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable;
                    if(listView != null && listView.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }else {
                        enable = true;
                    }
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setSafeToRefresh(enable, 1);
                }
            });


            return rootView;
        }

        public void refreshData(final ArrayList<Game> data){
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.v(TAG, "Refreshing");
                        mAdapter.clear();
                        games.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }

                });
            }
        }
    }



    public static class GamesInProgressFragment extends ListFragment {
        private static final String TAG = "GamesInProgressFragment";

        private GamesAdapter mAdapter;
        private static ArrayList<Game> games = new ArrayList<>();
        private ListView listView;

        public GamesInProgressFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

            mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
            setListAdapter(mAdapter);

            listView = (ListView) rootView.findViewById(android.R.id.list);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // Do nothing.
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable;
                    if(listView != null && listView.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    } else {
                        enable = true;
                    }
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setSafeToRefresh(enable, 2);
                }
            });



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
            activity.runOnUiThread(new Runnable() {
                public void run(){
                    Log.v(TAG, "refreshing");
                    mAdapter.clear();
                    games.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }



            });


        }

    }

}
