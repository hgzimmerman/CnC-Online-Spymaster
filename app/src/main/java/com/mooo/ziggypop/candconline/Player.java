package com.mooo.ziggypop.candconline;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CheckBox;

import android.widget.CompoundButton;
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
public class Player implements Comparable{
    private static final String TAG = "Player";

    private static int NOTIFICATION_VALUE = 1;
    private static int FRIEND_VALUE = 2;
    private static int YOURSELF_VALUE = 4;

    // Assemble the stats link: prefix + game_key + infix + player_name, MAYBE...
    private static String STATS_PREFIX = "http://www.shatabrick.com/cco/";
    private static String STATS_INFIX = "/index.php?g=kw&a=sp&name=";

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


        public PlayersAdapter(Context context, int resource) {
            //super(context, resource);
            db = new PlayerDatabaseHandler(context);
        }

        public PlayersAdapter(Context context, int resource, ArrayList<Player> players) {
            //super(context, resource, players);
            myPlayers = players;
            db = new PlayerDatabaseHandler(context);
        }

        public PlayersAdapter(Context context,ArrayList<Player> players){
            myPlayers = players;
            db = new PlayerDatabaseHandler(context);
        }


        /*
        public View getView(final int position, View convertView, ViewGroup parent) {

            final SmallViewHolder holder;

            final LayoutInflater vi = LayoutInflater.from(getContext());

            final Player player = getItem(position);

            // Try fiddling around with this block here to see if I can prevent the notification states persisting
            if (convertView == null) { // The view does not exist and you need to set up and save it
                Log.v(TAG, "View not created, creating...");
                convertView = vi.inflate(R.layout.player_card, null);
                LinearLayout dummyLayout = (LinearLayout) convertView.findViewById(R.id.dummy_layout);

                LinearLayout smallPlayerView = (LinearLayout) vi.inflate(R.layout.player_small_layout, null);
                dummyLayout.addView(smallPlayerView);


                holder = new SmallViewHolder();
                holder.nickname = (TextView) convertView.findViewById(R.id.players_name);
                holder.friendMarker = convertView.findViewById(R.id.friend_marker);
                holder.notificationMarker = convertView.findViewById(R.id.notify_marker);
                holder.yourselfMarker = convertView.findViewById(R.id.yourself_marker);
                holder.holderView = (ViewGroup) convertView.findViewById(R.id.dummy_layout);
                convertView.setTag(holder);
            } else { // The view already exists and you can reuse it.
                holder = (SmallViewHolder) convertView.getTag();

                if (! player.isExpanded ) { // if the player is normal.
                    ViewGroup dummyLayout =  holder.holderView;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) { // use transitions.
                        Scene smallScene = Scene.getSceneForLayout(holder.holderView, R.layout.player_small_layout, holder.holderView.getContext());
                        TransitionManager.go(smallScene, new AutoTransition().setDuration(0)); // setting duration to 0 makes the transition "instant"
                        Log.v(TAG,"Setting small layout on scroll: " + player.getNickname());
                    } else {
                        dummyLayout.removeAllViews();
                        LinearLayout smallPlayerView = (LinearLayout) vi.inflate(R.layout.player_small_layout, null);
                        dummyLayout.addView(smallPlayerView);
                    }

                    // set the holder's variables.
                    holder.nickname = (TextView) convertView.findViewById(R.id.players_name);
                    holder.friendMarker = convertView.findViewById(R.id.friend_marker);
                    holder.notificationMarker = convertView.findViewById(R.id.notify_marker);
                    holder.yourselfMarker = convertView.findViewById(R.id.yourself_marker);
                } else { // if the player has been set to expand by being clicked before.
                    ViewGroup dummyLayout =  holder.holderView;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) { // use transitions.
                        Scene bigScene = Scene.getSceneForLayout(dummyLayout, R.layout.player_big_layout, getContext());
                        TransitionManager.go(bigScene);
                        Log.v(TAG, "Transition to big scene on scroll");
                        setUpLargeView(player, vi, holder);
                    } else {
                        dummyLayout.removeAllViews();
                        LinearLayout smallPlayerView = (LinearLayout) vi.inflate(R.layout.player_big_layout, null);
                        dummyLayout.addView(smallPlayerView);
                    }
                }


                //When the View is being recycled later, set these to be invisible by default, they can be repopulated by the code below.
                holder.notificationMarker.setVisibility(View.INVISIBLE);
                holder.friendMarker.setVisibility(View.INVISIBLE);
                holder.yourselfMarker.setVisibility(View.INVISIBLE);
            }



            //Create the dialog
            holder.holderView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        Scene smallScene = Scene.getSceneForLayout(holder.holderView, R.layout.player_small_layout, holder.holderView.getContext());
                        smallScene.setEnterAction(new Runnable() {
                            @Override
                            public void run() {
                                setUpSmallView(player,holder,vi);
                                player.isExpanded = false;
                            }
                        });
                        Scene largeScene = Scene.getSceneForLayout(holder.holderView, R.layout.player_big_layout, holder.holderView.getContext());
                        largeScene.setEnterAction(new Runnable() {
                            @Override
                            public void run() {
                                View newView = setUpLargeView(player, vi, holder);
                                player.isExpanded = true;
                                notifyDataSetChanged();
                                //holder.holderView.removeAllViews();
                                //holder.holderView.addView(newView);
                            }
                        });
                        if (player.isExpanded) {
                            Log.v(TAG, "already expanded, shrinking");
                            TransitionManager.go(smallScene, new Fade());
                        } else {
                            Log.v(TAG, "small layout, growing");
                            TransitionManager.go(largeScene, new Fade());
                        }
                    } // if the phone is older than KitKat
                    else if (player.isExpanded) {
                        setUpSmallView(player, holder, vi);
                        holder.holderView.removeAllViews();
                        LinearLayout smallPlayerView = (LinearLayout) vi.inflate(R.layout.player_small_layout, null);
                        holder.holderView.addView(smallPlayerView);
                    } else {
                        View newView = setUpLargeView(player, vi, holder);
                        holder.holderView.removeAllViews();
                        holder.holderView.addView(newView);

                    }
/*
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(cncOnlineLink));
                            getContext().startActivity(browserIntent);



                }
            });

            // set the views for the default layout.
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
*/

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
                    return new LargeViewHolder(cardView);
                default:
                    cardView.addView(smallView);
                    return new SmallViewHolder(cardView);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Player player = myPlayers.get(position);
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
                    break;
                case TYPE_LARGE:
                    break;
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
            //Todo: actually fill this out.
            public SmallViewHolder(View itemView) {
                super(itemView);
                nickname = (TextView) itemView.findViewById(R.id.players_name);
                friendMarker = itemView.findViewById(R.id.friend_marker);
                notificationMarker = itemView.findViewById(R.id.notify_marker);
                yourselfMarker = itemView.findViewById(R.id.yourself_marker);
                holderView = (ViewGroup) itemView.findViewById(R.id.dummy_layout);
                holderView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // swap to the big layout
                    }
                });
            }
        }

        private static class LargeViewHolder extends RecyclerView.ViewHolder {
            TextView nickname;
            CheckBox friendsCheckbox;
            CheckBox notificationsCheckbox;
            CheckBox yourselfCheckbox;
            //Todo: actually fill this out.
            public LargeViewHolder(View itemView) {
                super(itemView);
                nickname = (TextView) itemView.findViewById(R.id.players_name);
                friendsCheckbox= (CheckBox) itemView.findViewById(R.id.friends_checkbox);
                notificationsCheckbox = (CheckBox) itemView.findViewById(R.id.notifications_checkbox);
                yourselfCheckbox = (CheckBox) itemView.findViewById(R.id.is_you_checkbox);
            }
        }




        private void expandedViewUpdateDB(Player player, SmallViewHolder holder){

            CheckBox friendsCheckbox = (CheckBox) holder.holderView.findViewById(R.id.friends_checkbox);
            CheckBox notificationsCheckbox = (CheckBox) holder.holderView.findViewById(R.id.notifications_checkbox);
            CheckBox yourselfCheckbox = (CheckBox) holder.holderView.findViewById(R.id.is_you_checkbox);

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

            player.setUserName(((TextView)holder.holderView.findViewById(R.id.players_user_name)).getText() + "");

            // Commit the new player to the DB, or remove them if they are unchecked.
            if (!friendsCheckbox.isChecked() && !notificationsCheckbox.isChecked() && !yourselfCheckbox.isChecked()){
                db.deletePlayer(player);
            } else {
                db.addPlayer(player);
            }
        }

        private View setUpLargeView(final Player player, LayoutInflater vi, final SmallViewHolder holder) {
            //TODO: 99% sure this breaks on build version < 19
            View dialogView =  holder.holderView;
            TextView playerNickname = (TextView) dialogView.findViewById(R.id.players_name);

            playerNickname.setText(player.nickname);


            final String cncOnlineLink = PROFILE_PREFIX  + player.pid + "/";
            final TextView playerUserNameText = (TextView) dialogView.findViewById(R.id.players_user_name);

            // Set the string to the username if the player is not found in the database.
            if (player.getUserName().equals("")){
                Log.d(TAG, "Player IGN not found, getting from website");
                LinearLayout progBarView = (LinearLayout) dialogView.findViewById(R.id.name_loading_progress_bar);
                new RealUsernameHandler(
                        cncOnlineLink,
                        player,
                        playerUserNameText,
                        progBarView
                ).getUsername();
            } else {
                playerUserNameText.setText(player.getUserName());
            }

            CheckBox friendsCheckbox = (CheckBox) dialogView.findViewById(R.id.friends_checkbox);
            if (player.isFriend){
                friendsCheckbox.setChecked(true);
            }
            CheckBox notificationsCheckbox = (CheckBox) dialogView.findViewById(R.id.notifications_checkbox);
            if (player.isReceiveNotifications){
                notificationsCheckbox.setChecked(true);
            }
            CheckBox yourselfCheckbox = (CheckBox) dialogView.findViewById(R.id.is_you_checkbox);
            if (player.isYourself){
                yourselfCheckbox.setChecked(true);
            }
            friendsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                expandedViewUpdateDB(player, holder);
                }
            });
            notificationsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    expandedViewUpdateDB(player, holder);
                }
            });
            yourselfCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    expandedViewUpdateDB(player, holder);
                }
            });

            return dialogView;
        }


        private void setUpSmallView(Player player, SmallViewHolder holder, LayoutInflater vi){

            holder.nickname = (TextView) holder.holderView.findViewById(R.id.players_name);
            holder.friendMarker = holder.holderView.findViewById(R.id.friend_marker);
            holder.notificationMarker = holder.holderView.findViewById(R.id.notify_marker);
            holder.yourselfMarker = holder.holderView.findViewById(R.id.yourself_marker);

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
        }


    }




    /*
* Copyright (C) 2014 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

    /**
     * Demonstrates the use of {@link RecyclerView} with a {@link LinearLayoutManager} and a
     * {@link GridLayoutManager}.
     */
    public static class PlayersFragment extends RecyclerViewFragment {

        private static final String TAG = "RecyclerViewFragment";
        private static final String KEY_LAYOUT_MANAGER = "layoutManager";
        private static final int SPAN_COUNT = 2;




        protected LayoutManagerType mCurrentLayoutManagerType;

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

            mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
            mRecyclerView.setHasFixedSize(true);

            if (savedInstanceState != null) {
                // Restore saved layout manager type.
                mCurrentLayoutManagerType = (LayoutManagerType) savedInstanceState
                        .getSerializable(KEY_LAYOUT_MANAGER);
            }
            setRecyclerViewLayoutManager(mCurrentLayoutManagerType);

            mAdapter = new PlayersAdapter(getContext(), mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            Log.v(TAG, "setting the adapter for player");
            mRecyclerView.setAdapter(mAdapter);

            return rootView;
        }

        /**
         * Set RecyclerView's LayoutManager to the one given.
         *
         * @param layoutManagerType Type of layout manager to switch to.
         */
        public void setRecyclerViewLayoutManager(LayoutManagerType layoutManagerType) {
            int scrollPosition = 0;
            // If a layout manager has already been set, get current scroll position.
            if (mRecyclerView.getLayoutManager() != null) {
                scrollPosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                        .findFirstCompletelyVisibleItemPosition();
            }

            switch (layoutManagerType) {
                case GRID_LAYOUT_MANAGER:
                    mLayoutManager = new GridLayoutManager(getActivity(), SPAN_COUNT);
                    mCurrentLayoutManagerType = LayoutManagerType.GRID_LAYOUT_MANAGER;
                    break;
                case LINEAR_LAYOUT_MANAGER:
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
                    break;
                default:
                    mLayoutManager = new LinearLayoutManager(getActivity());
                    mCurrentLayoutManagerType = LayoutManagerType.LINEAR_LAYOUT_MANAGER;
            }

            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.scrollToPosition(scrollPosition);
        }

        @Override
        public void onSaveInstanceState(Bundle savedInstanceState) {
            // Save currently selected layout manager.
            savedInstanceState.putSerializable(KEY_LAYOUT_MANAGER, mCurrentLayoutManagerType);
            super.onSaveInstanceState(savedInstanceState);
        }


        public void refreshData(final ArrayList<Player> data, final Activity activity ){
            if(!isAdded())
                mAdapter = new PlayersAdapter(activity, mDataset);

            activity.runOnUiThread(new Runnable() {
                public void run() {
                    Log.v(TAG, "Refreshing");
                    //mAdapter.clear();
                    mDataset.clear();
                    mDataset.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }


//
//    public static class PlayersFragment extends ListFragment {
//        /**
//         * The fragment argument representing the section number for this
//         * fragment.
//         */
//        public static final String ARG_SECTION_NUMBER = "section_number";
//        public static final String TAG = "PlayersFragment";
//
//
//        public ArrayList<Player> wordsArr = new ArrayList<>();
//        private PlayersAdapter mAdapter;
//
//
//        LayoutInflater inflater;
//        ViewGroup container;
//        Bundle savedInstanceState;
//        View rootView;
//        private RecyclerView recyclerView;
//        boolean isMainActivity;
//
//
//
//        public PlayersFragment() {
//        }
//
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, final ViewGroup container,
//                                 Bundle savedInstanceState) {
//            super.onCreateView(inflater,container,savedInstanceState);
//
//            mAdapter = new PlayersAdapter(getActivity(), R.layout.player_card, wordsArr);
//            //setListAdapter(mAdapter);
//
//            this.inflater=inflater;
//            this.container=container;
//            this.savedInstanceState = savedInstanceState;
//
//            rootView =  inflater.inflate(R.layout.fragment_list_view,container,false);
//            setRetainInstance(true);//This prevents the GC-ing of the fragment.
//
//            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
//            //Because doing this the "right way" in xml breaks the logic in onScroll(), this is a way to add padding at the top.
//            View padding = inflater.inflate(R.layout.padding_layout, null, false);
//            //recyclerView.addHeaderView(padding);
//
//            /*
//            recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//
//
//                @Override
//                public void onScroll(AbsListView view, int firstVisibleItem,
//                                     int visibleItemCount, int totalItemCount) {
//                    boolean enable = false;
//                    if(recyclerView != null && recyclerView.getChildCount() > 0){
//                        // check if the first item of the list is visible
//                        boolean firstItemVisible = true; //recyclerView.getFirstVisiblePosition() == 0;
//                        // check if the top of the first item is visible
//                        boolean topOfFirstItemVisible = recyclerView.getChildAt(0).getTop() == 0;
//                        // enabling or disabling the refresh layout
//                        enable = firstItemVisible && topOfFirstItemVisible;
//                    } else if (recyclerView == null) {
//                        enable = true;
//                    }
//
//                    // Hacky way of determining which activity this is being called from using Type Introspection
//                    // HOORAY CODE REUSE!!!
//                    //Todo: Type Introspection is less than ideal, find a better means of determining the parent (maybe set it from the parent Activity by getting a reference to the playerFragment???
//                    if ( container.getClass().getSimpleName().equals("ViewPager")) { //if the parent activity is the main activity
//                        MainActivity activity = (MainActivity) getActivity();
//                        activity.setSafeToRefresh(enable, 0);
//                    } else if (container.getClass().getSimpleName().equals("FrameLayout")){ // if the parent activity is the player db viewer activity
//                        PlayerDatabaseViewerActivity activity
//                                = (PlayerDatabaseViewerActivity) getActivity();
//                        activity.setSafeToRefresh(enable);
//                    }
//                }
//            });
//            */
//            return rootView;
//        }
//
//        /**
//         * Refresh the adapter with new Player objects
//         * @param data An ArrayList of Player objects to display in the adapter.
//         * @param activity A reference to the main activity used to create a new PlayersAdapter
//         *                 if that adapter happens to be null.
//         */
//        public void refreshData(final ArrayList<Player> data, final Activity activity ){
//            if(!isAdded())
//                mAdapter = new PlayersAdapter(activity, R.layout.player_card, wordsArr);
//
//            activity.runOnUiThread(new Runnable() {
//                public void run() {
//                    Log.v(TAG, "Refreshing");
//                    //mAdapter.clear();
//                    wordsArr.addAll(data);
//                    mAdapter.notifyDataSetChanged();
//                }
//            });
//        }
//
//    }


}
