package com.mooo.ziggypop.candconline;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziggypop on 3/28/15.
 */
public class PlayersFragment extends ListFragment {



    private ArrayAdapter<PlayerItem> listAdapter;
    private ListView listView;
    private Adapter mAdapter;

    private static final String[] WORDS = { "Lorem", "ipsum", "dolor", "sit",
            "amet", "consectetur", "adipiscing", "elit", "Fusce", "pharetra",
            "luctus", "sodales" };
    private List myList;

    private static final String TAG = "com.mooo.ziggypop.candconline.PlayersFragment";


     /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";


    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PlayersFragment newInstance(int sectionNumber) {
        PlayersFragment fragment = new PlayersFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlayersFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.players_fragment, container, false);

        myList = new ArrayList();
        for( String element : WORDS){
            myList.add(new PlayerItem(element));
        }

        listAdapter = new PlayerListAdapter(getActivity(), myList);




        return rootView;
    }


}
