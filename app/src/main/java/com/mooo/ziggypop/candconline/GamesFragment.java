package com.mooo.ziggypop.candconline;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
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
public class GamesFragment extends ListFragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";




    private List myList;
    private GamesAdapter mAdapter;



    public GamesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.players_fragment, container, false);
        ListView listView = (ListView) getActivity().findViewById(android.R.id.list);

        ArrayList<String> playersEx = new ArrayList<String>();
        playersEx.add("Ziggypop");

        ArrayList<String> playersEx2 = new ArrayList<String>();
        playersEx2.add("Scrin2Win");
        playersEx2.add("SOME_SCRUB");

        GameInLobby game1 = new GameInLobby("Ziggy's Game", "(1/2)",
            playersEx , false, "TwistedRift");
        GameInLobby game2 = new GameInLobby("Mids/Highs", "(2/2)", playersEx2,
                true, "RedZoneRampage");


        ArrayList<GameInLobby> games = new ArrayList<>();
        games.add(game1);
        games.add(game2);

        mAdapter = new GamesAdapter(getActivity(), R.layout.games_layout, games);
        setListAdapter(mAdapter);


        return rootView;
    }


}
