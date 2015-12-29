package com.mooo.ziggypop.candconline;


import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by ziggypop on 3/30/15.
 *
 * Holds the data for the players in the game
 * Wraps Adapter and Fragment Subclasses
 *
 */
public class Player {

    private String nickname;
    private int id;
    private int pid;
    private boolean isFriend;
    private boolean isRecieveNotifications;
    private boolean isYourself;

    public Player(String nickname, int id, int pid){
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = false;
        this.isRecieveNotifications = false;
        this.isYourself = false;
    }

    public Player(String nickname, int id, int pid, boolean isFriend,
                  boolean isRecieveNotifications, boolean isYourself) {
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = isFriend;
        this.isRecieveNotifications = isRecieveNotifications;
        this.isYourself = isYourself;
    }


    public String getNickname(){
        return nickname;
    }
    public int getID(){return id;}
    public int getPID(){return pid;}
    public boolean getIsFriend(){return isFriend;}
    public boolean getIsRecieveNotifications(){return isRecieveNotifications;}
    public boolean getIsYourself(){return isYourself;}


    /**
     * Adapter used with the PlayersFragment
     */
    public static class PlayersAdapter extends ArrayAdapter<Player> {

        public ArrayList<Player> myPlayers;

        public PlayersAdapter(Context context, int resource) {
            super(context, resource);
        }

        public PlayersAdapter(Context context, int resource, ArrayList<Player> players) {
            super(context, resource, players);
            myPlayers = players;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {

                LayoutInflater vi;
                vi = LayoutInflater.from(getContext());
                v = vi.inflate(R.layout.players_layout, null);

            }


            final Player player = getItem(position);
            String nickname = player.nickname;
            TextView textView = (TextView) v.findViewById(R.id.text);
            textView.setText(nickname);

            final LayoutInflater vi = LayoutInflater.from(getContext());
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    View dialogView = vi.inflate(R.layout.player_alert_dialog, null);
                    TextView playerNickname = (TextView) dialogView.findViewById(R.id.players_name);
                    playerNickname.setText(player.nickname);
                    TextView playerID = (TextView) dialogView.findViewById(R.id.players_id);
                    playerID.setText(player.id + "");
                    TextView playerPID = (TextView) dialogView.findViewById(R.id.players_pid);
                    playerPID.setText(player.pid + "");


                    builder.setView(dialogView);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // register the changes
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
                    builder.show();
                }
            });






            return v;
        }
    }




    /**
     * TODO when clicked, expand to show other player information (Id vs name)
     */
    public static class PlayersFragment extends ListFragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";
        public static final String TAG = "PlayersFragment";


        public ArrayList<Player> wordsArr = new ArrayList<>();
        private PlayersAdapter mAdapter;


        LayoutInflater inflater;
        ViewGroup container;
        Bundle savedInstanceState;
        View rootView;
        private ListView listView;



        public PlayersFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            mAdapter = new PlayersAdapter(getActivity(), R.layout.players_layout, wordsArr);
            setListAdapter(mAdapter);

            this.inflater=inflater;
            this.container=container;
            this.savedInstanceState = savedInstanceState;

            rootView =  inflater.inflate(R.layout.fragment_list_view,container,false);
            setRetainInstance(true);//This prevents the GC-ing of the fragment.

            listView = (ListView) rootView.findViewById(android.R.id.list);

            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // Do nothing.
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem,
                                     int visibleItemCount, int totalItemCount) {
                    boolean enable = false;
                    if(listView != null && listView.getChildCount() > 0){
                        // check if the first item of the list is visible
                        boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                        // check if the top of the first item is visible
                        boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
                        // enabling or disabling the refresh layout
                        enable = firstItemVisible && topOfFirstItemVisible;
                    } else if (listView == null) {
                        enable = true;
                    }
                    MainActivity activity = (MainActivity) getActivity();
                    activity.setSafeToRefresh(enable, 0);
                }
            });
            return rootView;

        }

        /**
         * Refresh the adapter with new Player objects
         * @param data An ArrayList of Player objects to display in the adapter.
         * @param activity A reference to the main activity used to create a new PlayersAdapter
         *                 if that adapter happens to be null.
         */
        public void refreshData(final ArrayList<Player> data, final MainActivity activity){
            if(!isAdded())
                mAdapter = new PlayersAdapter(activity, R.layout.players_layout, wordsArr);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.v(TAG, "Refreshing");
                    mAdapter.clear();
                    wordsArr.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }

}
