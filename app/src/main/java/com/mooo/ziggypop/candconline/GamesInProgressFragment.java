package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

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
    private static ArrayList<GameInLobby> games = new ArrayList<>();
    private boolean isAttached = false;
    private static ArrayList<GameInLobby> failedToLoadGames = new ArrayList<>();



    public GamesInProgressFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.players_fragment, container, false);

        mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
        setListAdapter(mAdapter);



        refreshData(failedToLoadGames);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        isAttached = true;
    }

    public void refreshData(final ArrayList<GameInLobby> data){
        if(isAttached) {
            Activity mActivity = getActivity();
            mActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.v("GamesInProgressFragment", "RUNNING");
                    mAdapter.clear();
                    games.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }

            });
        } else {
            failedToLoadGames = data;
        }


    }

}
