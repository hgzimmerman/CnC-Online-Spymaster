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
        retVal = retVal.substring(0, retVal.length()-1);
        return retVal;
    }
    public boolean getLockStatus(){
        return isLocked;
    }






    public static class GamesAdapter extends RecyclerView.Adapter {
        ArrayList<Game> games;

        public GamesAdapter(Context context, int textViewResourceId) {
            //super(context, textViewResourceId);
        }

        public GamesAdapter( Context context, int resource, ArrayList<Game> games) {
            //super(context, resource, games);
            this.games = games;
        }
        public GamesAdapter(ArrayList<Game> games){}
        public GamesAdapter(Context context, ArrayList<Game> games){
            this.games = games;
        }

        public class GamesViewHolder extends RecyclerView.ViewHolder {
            TextView gameTitleText;
            TextView slotsText;
            ImageView lockImage;
            TextView playersText;
            TextView mapNameText;

            public GamesViewHolder(View itemView) {
                super(itemView);
                gameTitleText = (TextView) itemView.findViewById(R.id.game_title);
                slotsText = (TextView) itemView.findViewById(R.id.slots);
                lockImage = (ImageView) itemView.findViewById(R.id.lock);

                playersText = (TextView) itemView.findViewById(R.id.players);
                mapNameText = (TextView) itemView.findViewById(R.id.map);
            }
        }


        @Override
        public GamesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View gameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.games_layout, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            gameView.setLayoutParams(lp);
            return new GamesViewHolder(gameView);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Game game = games.get(position);
            GamesViewHolder gameHolder = (GamesViewHolder) holder;

            if (gameHolder.gameTitleText != null) {
                gameHolder.gameTitleText.setText(game.getTitle());
            }
            if (gameHolder.slotsText != null) {
                gameHolder.slotsText.setText(game.getSlots());
            }
            if (gameHolder.mapNameText != null) {
                gameHolder.mapNameText.setText(game.getMap());
            }
            if (gameHolder.lockImage != null) {
                if (game.getLockStatus()){
                    gameHolder.lockImage.setVisibility(View.VISIBLE);
                } else {
                    gameHolder.lockImage.setVisibility(View.GONE);
                }
            }
            if (gameHolder.playersText != null) {
                gameHolder.playersText.setText(game.getPlayersFormat());
            }
        }

        @Override
        public int getItemCount() {
            return games.size();
        }
    }


    public static class GamesFragment extends RecyclerViewFragment {
        private static final String TAG = "GamesInLobbyFragment";

        private GamesAdapter mAdapter;
        private static ArrayList<Game> mDataset = new ArrayList<>();


        public GamesFragment() {
        }


        @Override
        public void onCreate(Bundle bundle){
            super.onCreate(bundle);

            Log.v(TAG, "onCreate called");
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_list_view, null, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            mRecyclerView.setLayoutManager(llm);
            mDataset = new ArrayList<>();

            mAdapter = new GamesAdapter(getActivity(), mDataset);
            Log.v(TAG, "setting the adapter for game");
            mRecyclerView.setAdapter( mAdapter );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
            rootView.setTag(TAG);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity());



            setRecyclerViewLayoutManager(mRecyclerView);
            mRecyclerView.addItemDecoration(new RecyclerViewFragment.VerticalSpaceItemDecoration(
                    (int) getResources().getDimension(R.dimen.recycle_spacing)));

            mAdapter = new GamesAdapter(getActivity(), mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            Log.v(TAG, "Setting adapter for game");
            mRecyclerView.setAdapter(mAdapter);

            return rootView;
        }


        public void refreshData(final ArrayList<Game> data){
            if (isAdded()) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.v(TAG, "Refreshing");
                        mDataset.clear();
                        mDataset.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }

                });
            }
        }
    }


    public static class GamesInProgressFragment extends RecyclerViewFragment {
        private static final String TAG = "GamesInProgressFragment";

        private GamesAdapter mAdapter;
        private static ArrayList<Game> mDataset = new ArrayList<>();

        public GamesInProgressFragment() {
        }

        @Override
        public void onCreate(Bundle bundle){
            super.onCreate(bundle);

            Log.v(TAG, "onCreate called");
            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.fragment_list_view, null, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            mRecyclerView.setLayoutManager(llm);
            mDataset = new ArrayList<>();

            mAdapter = new GamesAdapter(getActivity(), mDataset);
            Log.v(TAG, "setting the adapter for gameinprogress");
            mRecyclerView.setAdapter( mAdapter );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
            rootView.setTag(TAG);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity());


            setRecyclerViewLayoutManager(mRecyclerView);
            mRecyclerView.addItemDecoration(new RecyclerViewFragment.VerticalSpaceItemDecoration(
                    (int) getResources().getDimension(R.dimen.recycle_spacing)));


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
                mAdapter = new GamesAdapter(activity, R.layout.games_layout, mDataset);
            }
            activity.runOnUiThread(new Runnable() {
                public void run(){
                    Log.v(TAG, "refreshing");
                    mDataset.clear();
                    mDataset.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }



            });


        }

    }

}
