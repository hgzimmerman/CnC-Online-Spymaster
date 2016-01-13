package com.mooo.ziggypop.candconline;


import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import static com.mooo.ziggypop.candconline.Player.PlayersAdapter.*;


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



    private static String PROFILE_PREFIX = "http://cnc-online.net/profiles/";

    private static final int TYPE_SMALL = 0;
    private static final int TYPE_LARGE = 1;


    private String nickname;
    private int id;
    private int pid;
    private boolean isFriend;
    private boolean isReceiveNotifications;
    private boolean isYourself;
    private String game;
    private String userName;
    private boolean isExpanded;



    public Player(String nickname, int id, int pid){
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = false;
        this.isReceiveNotifications = false;
        this.isYourself = false;
        this.game = "";
        this.userName = "";
    }

    public Player(String nickname, int id, int pid, boolean isFriend,
                  boolean isReceiveNotifications, boolean isYourself, String userName) {
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = isFriend;
        this.isReceiveNotifications = isReceiveNotifications;
        this.isYourself = isYourself;
        this.userName = userName;
    }





    public String getNickname(){
        return nickname;
    }
    public int getID(){return id;}
    public int getPID(){return pid;}
    public boolean getIsFriend(){return isFriend;}
    public boolean getIsRecieveNotifications(){return isReceiveNotifications;}
    public boolean getIsYourself(){return isYourself;}
    public String getUserName(){ return userName;}

    public void setIsFriend(boolean isFriend){
        this.isFriend = isFriend;
    }
    public void setIsRecieveNotifications(boolean isRecieve){ this.isReceiveNotifications = isRecieve; }
    public void setIsYourself(boolean isYourself){
        this.isYourself = isYourself;
    }
    public void setUserName(String userName) { this.userName = userName; }


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
        if (PreferenceManager.getDefaultSharedPreferences(context).getBoolean("sort_players_friendship_pref", true)){
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
    public static class PlayersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {



        public ArrayList<Player> myPlayers;
        private static PlayerDatabaseHandler db;

        public PlayersAdapter(Context context,ArrayList<Player> players){
            myPlayers = players;
            db = new PlayerDatabaseHandler(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup cardView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.player_card, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(lp);
            View smallView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_small_layout, null);
            switch (viewType){
                case TYPE_SMALL:
                    cardView.addView(smallView);
                    return new SmallViewHolder(cardView);
                case TYPE_LARGE:
                    View largeView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_big_layout, null);
                    cardView.addView(largeView);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        cardView.setElevation(16);
                    }
                    return new LargeViewHolder(cardView);
                default:
                    cardView.addView(smallView);
                    return new SmallViewHolder(cardView);
            }
        }


        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
            final Player player = myPlayers.get(position);
            switch (holder.getItemViewType()){
                //set up the view contents.
                case TYPE_SMALL:
                    SmallViewHolder smallViewHolder = (SmallViewHolder) holder;
                    smallViewHolder.nickname.setText(player.nickname);
                    if (player.isFriend){
                        smallViewHolder.friendMarker.setVisibility(View.VISIBLE);
                    } else {
                        smallViewHolder.friendMarker.setVisibility(View.INVISIBLE);
                    }
                    if (player.isReceiveNotifications){
                        smallViewHolder.notificationMarker.setVisibility(View.VISIBLE);
                    } else {
                        smallViewHolder.notificationMarker.setVisibility(View.INVISIBLE);
                    }
                    if (player.isYourself){
                        smallViewHolder.yourselfMarker.setVisibility(View.VISIBLE);
                    } else {
                        smallViewHolder.yourselfMarker.setVisibility(View.INVISIBLE);
                    }

                    ((SmallViewHolder) holder).holderView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "Small item clicked");
                            player.isExpanded = true;
                            //TODO: animate a transformation.
                            notifyItemChanged(position);
                            notifyDataSetChanged();
                        }
                    });
                    break;
                case TYPE_LARGE:
                    final LargeViewHolder largeViewHolder = (LargeViewHolder) holder;
                    largeViewHolder.nickname.setText(player.nickname);
                    //Set the username to invisible to make the prog bar look nice when the view is recycled.
                    largeViewHolder.username.setText("");
                    //Update the username
                    if (player.getUserName().equals("")){
                        String cncOnlineLink = PROFILE_PREFIX  + player.pid + "/";
                        Log.d(TAG, "Player IGN not found, getting from website");
                        LinearLayout progBarView =
                                (LinearLayout) ((LargeViewHolder) holder).holderView.findViewById(R.id.name_loading_progress_bar);
                        new RealUsernameHandler(
                                cncOnlineLink,
                                player,
                                largeViewHolder.username,
                                progBarView
                        ).getUsername();
                    } else {
                        largeViewHolder.username.setText(player.getUserName());
                    }

                    largeViewHolder.friendsCheckbox.setChecked(player.isFriend);
                    largeViewHolder.notificationsCheckbox.setChecked(player.isReceiveNotifications);
                    largeViewHolder.yourselfCheckbox.setChecked(player.isYourself);

                    largeViewHolder.friendsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            expandedViewUpdateDB(player, (LargeViewHolder) holder);
                        }
                    });
                    largeViewHolder.notificationsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            expandedViewUpdateDB(player, (LargeViewHolder) holder);
                        }
                    });
                    largeViewHolder.yourselfCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            expandedViewUpdateDB(player, (LargeViewHolder) holder);
                        }
                    });
                    largeViewHolder.statsButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Launch the statsHandler
                            StatsHandler sh = new StatsHandler(largeViewHolder);
                            sh.getStats(player);
                        }
                    });


                    ((LargeViewHolder) holder).holderView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.v(TAG, "Large item clicked");
                            player.isExpanded = false;
                            //TODO: fix bug: when multiple views are open, clicking on one may trigger another's listener.
                            //prevent this viewHolder from having the onCheckedChanged() method called on other viewHolder instances.
                            largeViewHolder.yourselfCheckbox.setOnCheckedChangeListener(null);
                            largeViewHolder.notificationsCheckbox.setOnCheckedChangeListener(null);
                            largeViewHolder.friendsCheckbox.setOnCheckedChangeListener(null);
                            largeViewHolder.progressBar.setProgress(0);
                            largeViewHolder.progressBar.setVisibility(View.INVISIBLE);
                            //Let the RecyclerView recognize that player is no longer expanded, and cause the row to be redrawn.
                            notifyItemChanged(position);

                        }
                    });

                    break;
            }
        }

        @Override
        public int getItemViewType(int position){
            if (myPlayers.get(position).isExpanded){
                return TYPE_LARGE;
            } else{
                return TYPE_SMALL;
            }
        }

        @Override
        public int getItemCount() {
            if (myPlayers != null){
                return myPlayers.size();
            } else
                return 0;
        }

        private static class SmallViewHolder extends RecyclerView.ViewHolder {
            TextView nickname;
            View friendMarker;
            View notificationMarker;
            View yourselfMarker;
            ViewGroup holderView;

            public SmallViewHolder(View itemView) {

                super(itemView);
                nickname = (TextView) itemView.findViewById(R.id.players_name);
                friendMarker = itemView.findViewById(R.id.friend_marker);
                notificationMarker = itemView.findViewById(R.id.notify_marker);
                yourselfMarker = itemView.findViewById(R.id.yourself_marker);
                holderView = (ViewGroup) itemView.findViewById(R.id.player_card);
            }

        }

        public static class LargeViewHolder extends RecyclerView.ViewHolder {

            TextView nickname;
            TextView username;
            CheckBox friendsCheckbox;
            CheckBox notificationsCheckbox;
            CheckBox yourselfCheckbox;
            Button statsButton;
            ProgressBar progressBar;
            ViewGroup holderView;

            public LargeViewHolder(final View itemView) {
                super(itemView);
                nickname = (TextView) itemView.findViewById(R.id.players_name);
                username = (TextView) itemView.findViewById(R.id.players_user_name);
                friendsCheckbox= (CheckBox) itemView.findViewById(R.id.friends_checkbox);
                notificationsCheckbox = (CheckBox) itemView.findViewById(R.id.notifications_checkbox);
                yourselfCheckbox = (CheckBox) itemView.findViewById(R.id.is_you_checkbox);
                statsButton = (Button) itemView.findViewById(R.id.stats_button);
                holderView = (ViewGroup) itemView.findViewById(R.id.player_card);
                progressBar = (ProgressBar) itemView.findViewById(R.id.horizontal_progress);
            }
        }


        private void expandedViewUpdateDB(Player player, LargeViewHolder holder){
            Log.v(TAG, "Updating the db for: " + player.nickname);
            CheckBox friendsCheckbox = (CheckBox) holder.holderView.findViewById(R.id.friends_checkbox);
            CheckBox notificationsCheckbox = (CheckBox) holder.holderView.findViewById(R.id.notifications_checkbox);
            CheckBox yourselfCheckbox = (CheckBox) holder.holderView.findViewById(R.id.is_you_checkbox);


            player.setIsFriend(friendsCheckbox.isChecked());
            player.setIsRecieveNotifications(notificationsCheckbox.isChecked());
            player.setIsYourself(yourselfCheckbox.isChecked());


            player.setUserName(((TextView)holder.holderView.findViewById(R.id.players_user_name)).getText() + "");

            // Commit the new player to the DB, or remove them if they are unchecked.
            if (!friendsCheckbox.isChecked() && !notificationsCheckbox.isChecked() && !yourselfCheckbox.isChecked()){
                db.deletePlayer(player);
            } else {
                db.addPlayer(player);
            }
        }



    }



    /**
     * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
     * {@link GridLayoutManager}.
     */
    public static class PlayersFragment extends RecyclerViewFragment {

        private static final String TAG = "RecyclerViewFragment";
        private static final String KEY_LAYOUT_MANAGER = "layoutManager";
        private static final int SPAN_COUNT = 2;




        protected RecyclerView mRecyclerView;
        protected PlayersAdapter mAdapter;
        protected RecyclerView.LayoutManager mLayoutManager;
        protected ArrayList<Player> mDataset;


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

            mAdapter = new PlayersAdapter(getActivity(), mDataset);
            Log.v(TAG, "setting the adapter for player");
            mRecyclerView.setAdapter( mAdapter );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater,container,savedInstanceState);

            Log.v(TAG, "onCreateView Called");
            View rootView = inflater.inflate(R.layout.fragment_list_view, container, false);
            rootView.setTag(TAG);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);

            // LinearLayoutManager is used here, this will layout the elements in a similar fashion
            // to the way ListView would layout elements. The RecyclerView.LayoutManager defines how
            // elements are laid out.
            mLayoutManager = new LinearLayoutManager(getActivity());

            mRecyclerView.setHasFixedSize(true);


            setRecyclerViewLayoutManager(mRecyclerView);

            mRecyclerView.addItemDecoration(new RecyclerViewFragment.VerticalSpaceItemDecoration(
                    (int) getResources().getDimension(R.dimen.recycle_spacing)));

            mAdapter = new PlayersAdapter(getContext(), mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            Log.v(TAG, "setting the adapter for player");
            mRecyclerView.setAdapter(mAdapter);

            return rootView;
        }



        public void refreshData(final ArrayList<Player> data, final Activity activity ){
            if(!isAdded())
                mAdapter = new PlayersAdapter(activity, mDataset);

            activity.runOnUiThread(new Runnable() {
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
