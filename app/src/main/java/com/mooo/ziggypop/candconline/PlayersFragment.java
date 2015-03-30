package com.mooo.ziggypop.candconline;

/**
 * Created by ziggypop on 3/29/15.
 */

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/**
     * A placeholder fragment containing a simple view.
     */
    public class PlayersFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";


        private static ArrayList<String> wordsArr = new ArrayList<>();
        private ArrayAdapter<String> mAdapter;
        private boolean isAttached = false;



    public PlayersFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.players_fragment, container, false);



            mAdapter = new PlayersAdapter(getActivity(), R.layout.list_item_card, wordsArr);

            setListAdapter(mAdapter);


            return rootView;
        }

        public void refreshData(final ArrayList<String> data){
            if (isAttached) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        Log.v("PLAYER_FRAGMENT", "RUNNING");
                        mAdapter.clear();
                        data.add(0, "Players Online: "+ data.size());
                        wordsArr.addAll(data);
                        mAdapter.notifyDataSetChanged();
                    }


                });
            }
        }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        isAttached = true;
    }




    }
