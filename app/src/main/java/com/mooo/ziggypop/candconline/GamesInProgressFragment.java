package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ziggypop on 3/28/15.
 */
public class GamesInProgressFragment extends ListFragment {
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
