package com.mooo.ziggypop.candconline;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

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
public class Player implements Comparable{

    private String nickname;
    private int id;
    private int pid;
    private boolean isFriend;
    private boolean isReceiveNotifications;
    private boolean isYourself;



    public Player(String nickname, int id, int pid){
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = false;
        this.isReceiveNotifications = false;
        this.isYourself = false;

    }

    public Player(String nickname, int id, int pid, boolean isFriend,
                  boolean isRecieveNotifications, boolean isYourself) {
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = isFriend;
        this.isReceiveNotifications = isRecieveNotifications;
        this.isYourself = isYourself;
    }





    public String getNickname(){
        return nickname;
    }
    public int getID(){return id;}
    public int getPID(){return pid;}
    public boolean getIsFriend(){return isFriend;}
    public boolean getIsRecieveNotifications(){return isReceiveNotifications;}
    public boolean getIsYourself(){return isYourself;}

    public void setIsFriend(boolean isFriend){
        this.isFriend = isFriend;
    }
    public void setIsRecieveNotifications(boolean isRecieve){
        this.isReceiveNotifications = isRecieve;
    }
    public void setIsYourself(boolean isYourself){
        this.isYourself = isYourself;
    }


    /**
     * Sorts the players by their case-insensitive nicknames.
     * @param another The other player to be compared against.
     * @return An int representing the comparison result.
     */
    @Override
    public int compareTo(@NonNull Object another) {
        Player rightPlayer = (Player) another;
        return this.nickname.compareToIgnoreCase(rightPlayer.nickname);
    }


    /**
     * Adapter used with the PlayersFragment
     */
    public static class PlayersAdapter extends ArrayAdapter<Player> {

        public ArrayList<Player> myPlayers;
        private static PlayerDatabaseHandler db;


        public PlayersAdapter(Context context, int resource) {
            super(context, resource);
            db = new PlayerDatabaseHandler(context);
        }

        public PlayersAdapter(Context context, int resource, ArrayList<Player> players) {
            super(context, resource, players);
            myPlayers = players;
            db = new PlayerDatabaseHandler(context);
        }

        public void setPlayerList( ArrayList<Player> list ){
            this.myPlayers = list;
            notifyDataSetChanged();
        }

        public View getView(int position, View convertView, ViewGroup parent) {


            final ViewHolder holder;

            // Try fiddling around with this block here to see if I can prevent the notification states persisting
            if (convertView == null) {

                LayoutInflater vi = LayoutInflater.from(getContext());
                convertView = vi.inflate(R.layout.players_layout, null);
                holder = new ViewHolder();
                holder.nickname = (TextView) convertView.findViewById(R.id.text);
                holder.friendMarker = convertView.findViewById(R.id.friend_marker);
                holder.friendMarker.setVisibility(View.INVISIBLE);
                holder.notificationMarker = convertView.findViewById(R.id.notify_marker);
                holder.notificationMarker.setVisibility(View.INVISIBLE);
                holder.yourselfMarker = convertView.findViewById(R.id.yourself_marker);
                holder.yourselfMarker.setVisibility(View.INVISIBLE);
                holder.holderView = convertView.findViewById(R.id.players_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
                //When the View is being recycled later, set these to be invisible by default, they can be repopulated by the code below.
                holder.notificationMarker.setVisibility(View.INVISIBLE);
                holder.friendMarker.setVisibility(View.INVISIBLE);
                holder.yourselfMarker.setVisibility(View.INVISIBLE);
            }


            //Set up the Player's card
            final Player player = getItem(position);



            //Create the dialog
            final LayoutInflater vi = LayoutInflater.from(getContext());
            holder.holderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    View dialogView = vi.inflate(R.layout.player_alert_dialog, null);
                    TextView playerNickname = (TextView) dialogView.findViewById(R.id.players_name);
                    playerNickname.setText(player.nickname);
                    TextView playerID = (TextView) dialogView.findViewById(R.id.players_id);
                    playerID.setText(player.id + "");
                    TextView playerPID = (TextView) dialogView.findViewById(R.id.players_pid);
                    playerPID.setText(player.pid + "");

                    final CheckBox friendsCheckbox = (CheckBox) dialogView.findViewById(R.id.friends_checkbox);
                    if (player.isFriend){
                        friendsCheckbox.setChecked(true);
                    }
                    final CheckBox notificationsCheckbox = (CheckBox) dialogView.findViewById(R.id.notifications_checkbox);
                    if (player.isReceiveNotifications){
                        notificationsCheckbox.setChecked(true);
                    }
                    final CheckBox yourselfCheckbox = (CheckBox) dialogView.findViewById(R.id.is_you_checkbox);
                    if (player.isYourself){
                        yourselfCheckbox.setChecked(true);
                    }

                    builder.setView(dialogView);
                    builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // construct a new player object to be committed to the DB
                            // alter the new player and update ui to reflect its state
                            if (friendsCheckbox.isChecked()) {
                                player.setIsFriend(true);
                                holder.friendMarker.setVisibility(View.VISIBLE);
                            } else {
                                player.setIsFriend(false);
                                holder.friendMarker.setVisibility(View.INVISIBLE);
                            }
                            if (notificationsCheckbox.isChecked()) {
                                player.setIsRecieveNotifications(true);
                                holder.notificationMarker.setVisibility(View.VISIBLE);
                            } else {
                                player.setIsRecieveNotifications(false);
                                holder.notificationMarker.setVisibility(View.INVISIBLE);
                            }
                            if (yourselfCheckbox.isChecked()) {
                                player.setIsYourself(true);
                                holder.yourselfMarker.setVisibility(View.VISIBLE);
                            } else {
                                player.setIsYourself(false);
                                holder.yourselfMarker.setVisibility(View.INVISIBLE);
                            }
                            //notifyDataSetChanged();

                            // Commit the new player to the DB.
                            if (!friendsCheckbox.isChecked() && !notificationsCheckbox.isChecked() && !yourselfCheckbox.isChecked()){
                                db.deletePlayer(player);
                            } else {
                                db.addPlayer(player);
                            }

                        }

                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // do nothing
                        }
                    });
                    builder.show();
                }
            });


            holder.nickname.setText(player.nickname);
            if (player.isFriend){
                holder.friendMarker.setVisibility(View.VISIBLE);
            }
            if (player.isReceiveNotifications){
                holder.notificationMarker.setVisibility(View.VISIBLE);
            }
            if (player.isYourself){
                holder.yourselfMarker.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

        private static class ViewHolder {
            TextView nickname;
            View friendMarker;
            View notificationMarker;
            View yourselfMarker;
            View holderView;
        }

    }



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
        boolean isMainActivity;



        public PlayersFragment() {
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater,container,savedInstanceState);
            // Set a flag to determine if the parent view is the mainActivity so onScroll can interact properly
            // Todo: find a more elegant solution to this.
            isMainActivity = (getActivity().findViewById(R.id.activity_main) != null);

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

                    if ( isMainActivity) { //if the parent activity is the main activity
                        MainActivity activity = (MainActivity) getActivity();
                        activity.setSafeToRefresh(enable, 0);
                    }

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
        public void refreshData(final ArrayList<Player> data, final Activity activity ){
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
