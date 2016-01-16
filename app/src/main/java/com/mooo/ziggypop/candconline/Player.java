package com.mooo.ziggypop.candconline;


import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

    public enum GameEnum{
        None,
        KanesWrath,
        CnC3,
        Generals,
        ZeroHour,
        RedAlert3
    }
    public static int gameEnumToInt(GameEnum gameEnum){
        Log.v(TAG, "converting to int: " + gameEnum.toString());
        switch (gameEnum) {
            case None:
                Log.e(TAG, "Converting None gameEnum to 0 ");
                return 0;
            case KanesWrath:
                return 1;
            case CnC3:
                return 2;
            case Generals:
                return 3;
            case ZeroHour:
                return 4;
            case RedAlert3:
                return 5;
            default:
                Log.e(TAG, "Converting None gameEnum to 0, (default)");
                return 0;
        }
    }
    public static GameEnum intToGameEnum(int integer){
        switch (integer){
            case 1:
                return GameEnum.KanesWrath;
            case 2:
                return GameEnum.CnC3;
            case 3:
                return GameEnum.Generals;
            case 4:
                return GameEnum.ZeroHour;
            case 5:
                return GameEnum.RedAlert3;
            default:
                Log.e(TAG, "Converting int to None GameEnum");
                return GameEnum.None;
        }
    }
    public static String gameEnumToQueryString(GameEnum gameEnum){
        switch (gameEnum) {
            case None:
                Log.e(TAG, "Converting None gameEnum to empty string");
                return "";
            case KanesWrath:
                return "kw";
            case CnC3:
                return "tw";
            case Generals:
                return "ccg";
            case ZeroHour:
                return "zh";
            case RedAlert3:
                return "ra3";
            default:
                Log.e(TAG, "Converting None gameEnum to empty string, (default)");
                return "";
        }
    }


    public static class PlayerViewHolder extends RecyclerView.ViewHolder {
        //small Layout
        final TextView nickname;
        final View friendMarker;
        final View notificationMarker;
        final View yourselfMarker;
        final ViewGroup smallView;
        //big layout
        final TextView bigNickname;
        final TextView username;
        final CheckBox friendsCheckbox;
        final CheckBox notificationsCheckbox;
        final CheckBox yourselfCheckbox;
        final Button statsButton;
        final Button ladderButton;
        final ProgressBar progressBar;
        final ViewGroup bigView;
        //the top level layout that holds both smaller layouts
        final ViewGroup rootView;


        public PlayerViewHolder(View itemView) {

            super(itemView);
            //root views
            rootView = (ViewGroup) itemView.findViewById(R.id.player_root);
            smallView = (ViewGroup) itemView.findViewById(R.id.small_player_layout);
            bigView = (ViewGroup) itemView.findViewById(R.id.big_player_layout);
            //small views
            nickname = (TextView) itemView.findViewById(R.id.players_name);
            friendMarker = itemView.findViewById(R.id.friend_marker);
            notificationMarker = itemView.findViewById(R.id.notify_marker);
            yourselfMarker = itemView.findViewById(R.id.yourself_marker);
            //big views
            bigNickname = (TextView) itemView.findViewById(R.id.players_name_big);
            username = (TextView) itemView.findViewById(R.id.players_user_name);
            friendsCheckbox = (CheckBox) itemView.findViewById(R.id.friends_checkbox);
            notificationsCheckbox = (CheckBox) itemView.findViewById(R.id.notifications_checkbox);
            yourselfCheckbox = (CheckBox) itemView.findViewById(R.id.is_you_checkbox);
            progressBar = (ProgressBar) itemView.findViewById(R.id.horizontal_progress);
            statsButton = (Button) itemView.findViewById(R.id.stats_button);
            ladderButton = (Button) itemView.findViewById(R.id.ladder_button);


        }

    }



    private static final String TAG = "Player";

    private static final int NOTIFICATION_VALUE = 1;
    private static final int FRIEND_VALUE = 2;
    private static final int YOURSELF_VALUE = 4;

    private static final String PROFILE_PREFIX = "http://cnc-online.net/profiles/";


    private String nickname;
    private int id;
    private int pid;
    private boolean isFriend;
    private boolean isReceiveNotifications;
    private boolean isYourself;
    private GameEnum game;
    private String userName;
    private boolean isExpanded;



    public Player(String nickname, int id, int pid, GameEnum game){
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = false;
        this.isReceiveNotifications = false;
        this.isYourself = false;
        this.game = game;
        this.userName = "";
    }

    public Player(String nickname, int id, int pid, boolean isFriend,
                  boolean isReceiveNotifications, boolean isYourself,
                  String userName, GameEnum game) {
        this.nickname = nickname;
        this.id = id;
        this.pid = pid;
        this.isFriend = isFriend;
        this.isReceiveNotifications = isReceiveNotifications;
        this.isYourself = isYourself;
        this.userName = userName;
        this.game = game;
    }





    public String getNickname(){
        return nickname;
    }
    public int getID(){return id;}
    public int getPID(){return pid;}
    public boolean getIsFriend(){return isFriend;}
    public boolean getIsReceiveNotifications(){return isReceiveNotifications;}
    public boolean getIsYourself(){return isYourself;}
    public String getUserName(){ return userName;}
    public GameEnum getGame(){return game;}

    public void setIsFriend(boolean isFriend){
        this.isFriend = isFriend;
    }
    public void setIsRecieveNotifications(boolean isReceive){ this.isReceiveNotifications = isReceive; }
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
        if (player.getIsReceiveNotifications())
            sum += NOTIFICATION_VALUE;
        if (player.getIsFriend())
            sum += FRIEND_VALUE;
        return sum;

    }


    /**
     * Adapter used with the PlayersFragment
     */
    public static class PlayersAdapter extends RecyclerView.Adapter<PlayerViewHolder> {

        public ArrayList<Player> myPlayers;
        private static PlayerDatabaseHandler db;

        public PlayersAdapter(Context context,ArrayList<Player> players){
            myPlayers = players;
            db = new PlayerDatabaseHandler(context);
        }

        @Override
        public PlayerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup cardView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.player_card, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(lp);
            View smallView = LayoutInflater.from(parent.getContext()).inflate(R.layout.player_small_layout, null);

            cardView.addView(smallView);
            return new PlayerViewHolder(cardView);

        }


        private void setCheckboxes(final PlayerViewHolder holder, final Player player) {
            holder.friendsCheckbox.setOnCheckedChangeListener(null);
            holder.notificationsCheckbox.setOnCheckedChangeListener(null);
            holder.yourselfCheckbox.setOnCheckedChangeListener(null);


            holder.friendsCheckbox.setChecked(player.isFriend);
            holder.notificationsCheckbox.setChecked(player.isReceiveNotifications);
            holder.yourselfCheckbox.setChecked(player.isYourself);

            holder.friendsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    expandedViewUpdateDB(player,  holder);
                }
            });
            holder.notificationsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    expandedViewUpdateDB(player,  holder);
                }
            });
            holder.yourselfCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    expandedViewUpdateDB(player,  holder);
                }
            });



        }
        private void setFlagIcons(PlayerViewHolder holder, Player player){
            if (player.isFriend) {
                holder.friendMarker.setVisibility(View.VISIBLE);
            } else {
                holder.friendMarker.setVisibility(View.INVISIBLE);
            }
            if (player.isReceiveNotifications) {
                holder.notificationMarker.setVisibility(View.VISIBLE);
            } else {
                holder.notificationMarker.setVisibility(View.INVISIBLE);
            }
            if (player.isYourself) {
                holder.yourselfMarker.setVisibility(View.VISIBLE);
            } else {
                holder.yourselfMarker.setVisibility(View.INVISIBLE);
            }
        }

        private void setUsername(PlayerViewHolder holder, Player player){
            if (player.getUserName().equals("")){
                String cncOnlineLink = PROFILE_PREFIX  + player.pid + "/";
                Log.d(TAG, "Player IGN not found, getting from website");
                LinearLayout progBarView =
                        (LinearLayout)  holder.bigView.findViewById(R.id.name_loading_progress_bar);
                new RealUsernameHandler(
                        cncOnlineLink,
                        player,
                        holder.username, //big username
                        progBarView
                ).getUsername();
            } else {
                holder.username.setText(player.getUserName());
            }
        }

        @Override
        public void onBindViewHolder(final PlayerViewHolder holder, final int position) {
            final Player player = myPlayers.get(position);

            //Animator animator = AnimationUtils.loadAnimation(holder.rootView.getContext(), R.anim.grow);

            LayoutTransition transition = new LayoutTransition();
            transition.setDuration(410, LayoutTransition.CHANGE_DISAPPEARING); // this is a quick fix, I would like to set the priorities of how the view is expanded, > 150 feels the most natural but 110 hides the odd sliding
            //transition.setAnimator(LayoutTransition.CHANGING, animator);
            transition.setAnimateParentHierarchy(true);
            ((RelativeLayout)holder.smallView.getParent()).setLayoutTransition(transition);


            if (! player.isExpanded) {
                holder.bigView.setVisibility(View.GONE);
                holder.smallView.setVisibility(View.VISIBLE);

                setFlagIcons(holder, player);

            } else {
                holder.smallView.setVisibility(View.GONE);
                holder.bigView.setVisibility(View.VISIBLE);

                setUsername(holder, player);
                setCheckboxes(holder, player);

            }

            //====Small View Stuff====
            holder.nickname.setText(player.nickname);

            setFlagIcons(holder, player);

            holder.smallView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Small item clicked");
                    player.isExpanded = true;
                    //TODO: animate a transformation.

//                    final Scene bigScene = Scene.getSceneForLayout(holder.rootView, R.layout.player_big_layout, holder.rootView.getContext());
//                    TransitionManager.go(bigScene);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Slide slide = new Slide();
                        TransitionManager.beginDelayedTransition(holder.rootView, slide);
                    }

                    holder.smallView.setVisibility(View.GONE);
                    holder.bigView.setVisibility(View.VISIBLE);

                    setUsername(holder, player);
                    setCheckboxes(holder, player);
                }
            });

            //====Big View Stuff====
            holder.bigNickname.setText(player.nickname);
            //Set the username to invisible to make the prog bar look nice when the view is recycled.

            // disable the stats button if the game is Generals or ZH
            holder.statsButton.setEnabled(true);
            if (player.getGame() == GameEnum.Generals || player.getGame() == GameEnum.ZeroHour){
                holder.statsButton.setEnabled(false);
            }

            holder.statsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Launch the statsHandler
                    StatsHandler sh = new StatsHandler(holder);
                    sh.getPlayerStats(player);
                }
            });

            holder.ladderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    StatsHandler sh = new StatsHandler(holder);
                    sh.getLadderStats(player);
                }
            });


            holder.bigView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.v(TAG, "Large item clicked");
                    player.isExpanded = false;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        Slide slide = new Slide();
                        TransitionManager.beginDelayedTransition(holder.rootView, slide);
                    }

                    //holder.smallView.setVisibility(View.VISIBLE);
                    //holder.bigView.setVisibility(View.GONE);
                    //Todo: Actually find a way to animate this
                    // Cheap way of animating the view back to the default size
                    notifyItemChanged(position);

                    //prevent this viewHolder from having the onCheckedChanged() method called on other viewHolder instances.
                    holder.progressBar.setProgress(0);
                    holder.progressBar.setVisibility(View.INVISIBLE);


                    setFlagIcons(holder, player);
                    holder.username.setText("");

                }
            });
        }


        @Override
        public int getItemCount() {
            if (myPlayers != null){
                return myPlayers.size();
            } else
                return 0;
        }


        private void expandedViewUpdateDB(Player player, PlayerViewHolder holder){
            Log.v(TAG, "Updating the db for: " + player.nickname);


            player.setIsFriend(holder.friendsCheckbox.isChecked());
            player.setIsRecieveNotifications(holder.notificationsCheckbox.isChecked());
            player.setIsYourself(holder.yourselfCheckbox.isChecked());


            player.setUserName(((TextView)holder.bigView.findViewById(R.id.players_user_name)).getText() + "");

            // Commit the new player to the DB, or remove them if they are unchecked.
            if (!holder.friendsCheckbox.isChecked()
                    && !holder.notificationsCheckbox.isChecked()
                    && !holder.yourselfCheckbox.isChecked()
                    ){
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