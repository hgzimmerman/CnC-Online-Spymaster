package com.mooo.ziggypop.candconline;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
    private static final String TAG = "Player";

    private static int NOTIFICATION_VALUE = 1;
    private static int FRIEND_VALUE = 2;
    private static int YOURSELF_VALUE = 4;

    private String nickname;
    private int id;
    private int pid;
    private boolean isFriend;
    private boolean isReceiveNotifications;
    private boolean isYourself;
    private String game;
    private String inGameName;



    public Player(String nickname, int id, int pid){
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = false;
        this.isReceiveNotifications = false;
        this.isYourself = false;
        this.game = "";
        this.inGameName = "";
    }

    public Player(String nickname, int id, int pid, boolean isFriend,
                  boolean isRecieveNotifications, boolean isYourself, String inGameName) {
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = isFriend;
        this.isReceiveNotifications = isRecieveNotifications;
        this.isYourself = isYourself;
        this.inGameName = inGameName;
    }





    public String getNickname(){
        return nickname;
    }
    public int getID(){return id;}
    public int getPID(){return pid;}
    public boolean getIsFriend(){return isFriend;}
    public boolean getIsRecieveNotifications(){return isReceiveNotifications;}
    public boolean getIsYourself(){return isYourself;}
    public String getInGameName(){ return inGameName;}

    public void setIsFriend(boolean isFriend){
        this.isFriend = isFriend;
    }
    public void setIsRecieveNotifications(boolean isRecieve){ this.isReceiveNotifications = isRecieve; }
    public void setIsYourself(boolean isYourself){
        this.isYourself = isYourself;
    }
    public void setInGameName(String inGameName) { this.inGameName = inGameName; }


    /**
     * Sorts the players by their case-insensitive nicknames.
     * @param another The other player to be compared against.
     * @return An int representing the comparison result.
     */
    @Override
    public int compareTo(@NonNull Object another) {
        Player rightPlayer = (Player) another;
        // This logic slows the method significantly, but, with logging, it still took about .050 seconds
        // to sort 100 elements (.0002s per compare) on a Nexus 5x, so this is /acceptable/. This is
        // instead of the .011 seconds without the logic, or .040 seconds if the preference is disabled.
        Context context = CnCSpymaster.getAppContext();
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean(
                context.getString(R.string.sort_players_by_friendship_first_pref), true)){
            Integer lPlayerValue = valueAdder(this);
            Integer rPlayerValue = valueAdder(rightPlayer);
            // we want the greater value of the two to be "Lesser", so this is opposite from normal.
            int returnValue = rPlayerValue.compareTo(lPlayerValue);
            if (returnValue != 0){
                return returnValue;
            }
        }

        return this.nickname.compareToIgnoreCase(rightPlayer.nickname);
    }

    /**
     * Assigns a value based on the flags set for a player
     * @param player The player to be evaluated.
     * @return The player's "value".
     */
    private int valueAdder(Player player){
        int sum = 0;
        if (player.getIsYourself())
            sum += YOURSELF_VALUE;
        if (player.getIsRecieveNotifications())
            sum += NOTIFICATION_VALUE;
        if (player.getIsFriend())
            sum += FRIEND_VALUE;
        return sum;

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
                    /*
                    TextView playerID = (TextView) dialogView.findViewById(R.id.players_id);
                    String playerIDText = player.id + ""; // avoid the IDE complaining about creating a string in the setText method below.
                    playerID.setText(playerIDText);
                    TextView playerPID = (TextView) dialogView.findViewById(R.id.players_pid);
                    String playerPIDText = player.pid + "";
                    playerPID.setText(playerPIDText);
                    */

                    final Button playerButton = (Button) dialogView.findViewById(R.id.player_link);
                    final String cncOnlineLink = "http://cnc-online.net/profiles/"  + player.pid + "/";
                    playerButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(cncOnlineLink));
                            getContext().startActivity(browserIntent);
                        }
                    });
                    /*
                    Button statsButton = (Button) dialogView.findViewById(R.id.stats_link);
                    String gameID = "kw";
                    final String statsLink = "http://www.shatabrick.com/cco/"+ gameID +"/index.php?g=kw&a=sp&name=" + player.nickname;
                    statsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent statsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(statsLink));
                            getContext().startActivity(statsIntent);
                        }
                    });
                    */

                    // Set the string to the username if the player is not found in the database.
                    if (player.getInGameName().equals("")
                            || player.getInGameName().equals(getContext().getString(R.string.profile))){
                        Log.d(TAG, "Player IGN not found, getting from website");
                        new RealUsernameHandler(cncOnlineLink, player.id+"", playerButton).getUsername();
                    } else {
                        playerButton.setText(player.getInGameName());
                    }


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
                            Log.v(TAG, playerButton.getText()+"");
                            player.setInGameName(playerButton.getText() + "");

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
                    AlertDialog dialog  = builder.create();

                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getContext().getResources().getColor(R.color.material_blue_grey_500));
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.material_blue_grey_500));

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
        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater,container,savedInstanceState);

            mAdapter = new PlayersAdapter(getActivity(), R.layout.players_layout, wordsArr);
            setListAdapter(mAdapter);

            this.inflater=inflater;
            this.container=container;
            this.savedInstanceState = savedInstanceState;

            rootView =  inflater.inflate(R.layout.fragment_list_view,container,false);
            setRetainInstance(true);//This prevents the GC-ing of the fragment.

            listView = (ListView) rootView.findViewById(android.R.id.list);
            //Because doing this the "right way" in xml breaks the logic in onScroll(), this is a way to add padding at the top.
            View padding = inflater.inflate(R.layout.padding_layout, null, false);
            listView.addHeaderView(padding);

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

                    // Hacky way of determining which activity this is being called from using Type Introspection
                    // HOORAY CODE REUSE!!!
                    //Todo: Type Introspection is less than ideal, find a better means of determining the parent (maybe set it from the parent Activity by getting a reference to the playerFragment???
                    if ( container.getClass().getSimpleName().equals("ViewPager")) { //if the parent activity is the main activity
                        MainActivity activity = (MainActivity) getActivity();
                        activity.setSafeToRefresh(enable, 0);
                    } else if (container.getClass().getSimpleName().equals("FrameLayout")){ // if the parent activity is the player db viewer activity
                        PlayerDatabaseViewerActivity activity
                                = (PlayerDatabaseViewerActivity) getActivity();
                        activity.setSafeToRefresh(enable);
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
