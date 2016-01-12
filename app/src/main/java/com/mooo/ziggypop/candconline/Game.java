package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

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
        retVal = retVal.substring(0, retVal.length()-1);
        return retVal;
    }
    public boolean getLockStatus(){
        return isLocked;
    }





    public static class GamesAdapter extends RecyclerView.Adapter {
        public GamesAdapter(Context context, int textViewResourceId) {
            //super(context, textViewResourceId);
        }

        public GamesAdapter( Context context, int resource, ArrayList<Game> games) {
            //super(context, resource, games);
        }
        public GamesAdapter(ArrayList<Game> games){}
        public GamesAdapter(Context context, ArrayList<Game> games){}

/*
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
        */

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }



    public static class GamesFragment extends RecyclerViewFragment {
        private static final String TAG = "GamesInLobbyFragment";

        private GamesAdapter mAdapter;
        private static ArrayList<Game> games = new ArrayList<>();
        private RecyclerView recyclerView;


        public GamesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

            mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);

            recyclerView = (RecyclerView) rootView.findViewById(android.R.id.list);

            View padding = inflater.inflate(R.layout.padding_layout, null, false);
            /*
            recyclerView.addHeaderView(padding);


            recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) { }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable;
                    if(recyclerView != null && recyclerView.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = recyclerView.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    }else {
                        enable = true;
                    }
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setSafeToRefresh(enable, 1);
                }
            });
            */


            return rootView;
        }


        public void refreshData(final ArrayList<Game> data){
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.v(TAG, "Refreshing");
                        //mAdapter.clear();
                        games.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }

                });
            }
        }
    }



    public static class GamesInProgressFragment extends RecyclerViewFragment {
        private static final String TAG = "GamesInProgressFragment";

        private GamesAdapter mAdapter;
        private static ArrayList<Game> games = new ArrayList<>();
        private ListView listView;

        public GamesInProgressFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            /*
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);

            mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
            //setListAdapter(mAdapter);

            recyclerView = (ListView) rootView.findViewById(android.R.id.list);
            View padding = inflater.inflate(R.layout.padding_layout, null, false);
            recyclerView.addHeaderView(padding);

            recyclerView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // Do nothing.
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable;
                    if(recyclerView != null && recyclerView.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = recyclerView.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    } else {
                        enable = true;
                    }
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setSafeToRefresh(enable, 2);
                }
            });

*/

            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
            rootView.setTag(TAG);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity());

            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;

            if (savedInstanceState != null) {
                // Restore saved layout manager type.
                mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER);
            }
            setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

            mAdapter = new GamesAdapter(getActivity(), mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            Log.v(TAG, "Setting adapter for game");
            mRecyclerView.setAdapter(mAdapter);

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
                    //mAdapter.clear();
                    games.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }



            });


        }

    }

}
