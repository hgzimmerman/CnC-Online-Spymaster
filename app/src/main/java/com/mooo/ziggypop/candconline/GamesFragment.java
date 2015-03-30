package com.mooo.ziggypop.candconline;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziggypop on 3/28/15.
 */
public class GamesFragment extends ListFragment {
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

