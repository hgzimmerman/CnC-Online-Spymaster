package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by ziggypop on 1/13/16.
 * Deals with the Statistics
 */
public class PlayerStats implements Parcelable {
    private final String nickname;
    private final int totalGames;
    private final int rankedOneVsOneWins;
    private final int rankedOneVsOneLosses;
    private final int rankedOneVsOneDisconnects;
    private final int rankedOneVsOneDesyncs;
    private final int rankedTwoVsTwoGames;
    private final int totalWins;
    private final int totalLosses;
    private final int totalDisconnects;
    private final int totalDesyncs;

    public PlayerStats(String nickname, int totalGames, int rankedOneVsOneWins,
                       int rankedOneVsOneLosses, int rankedOneVsOneDisconnects,
                       int rankedOneVsOneDesyncs, int rankedTwoVsTwoGames, int totalWins,
                       int totalLosses, int totalDisconnects, int totalDesyncs) {
        this.nickname = nickname;
        this.totalGames = totalGames;
        this.rankedOneVsOneWins = rankedOneVsOneWins;
        this.rankedOneVsOneLosses = rankedOneVsOneLosses;
        this.rankedOneVsOneDisconnects = rankedOneVsOneDisconnects;
        this.rankedOneVsOneDesyncs = rankedOneVsOneDesyncs;
        this.rankedTwoVsTwoGames = rankedTwoVsTwoGames;
        this.totalWins = totalWins;
        this.totalLosses = totalLosses;
        this.totalDisconnects = totalDisconnects;
        this.totalDesyncs = totalDesyncs;
    }

    public PlayerStats(Parcel in) {
        nickname = in.readString();
        totalGames = in.readInt();
        rankedOneVsOneWins = in.readInt();
        rankedOneVsOneLosses = in.readInt();
        rankedOneVsOneDisconnects = in.readInt();
        rankedOneVsOneDesyncs = in.readInt();
        rankedTwoVsTwoGames = in.readInt();
        totalWins = in.readInt();
        totalLosses = in.readInt();
        totalDisconnects = in.readInt();
        totalDesyncs = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickname);
        dest.writeInt(totalGames);
        dest.writeInt(rankedOneVsOneWins);
        dest.writeInt(rankedOneVsOneLosses);
        dest.writeInt(rankedOneVsOneDisconnects);
        dest.writeInt(rankedOneVsOneDesyncs);
        dest.writeInt(rankedTwoVsTwoGames);
        dest.writeInt(totalWins);
        dest.writeInt(totalLosses);
        dest.writeInt(totalDisconnects);
        dest.writeInt(totalDesyncs);
    }

    public static final Parcelable.Creator<PlayerStats> CREATOR = new Parcelable.Creator<PlayerStats>() {
        @Override
        public PlayerStats createFromParcel(Parcel in) {
            return new PlayerStats(in);
        }

        @Override
        public PlayerStats[] newArray(int size) {
            return new PlayerStats[size];
        }
    };

    public static class StatsAdapter extends RecyclerView.Adapter<StatsViewHolder> {

        public ArrayList<PlayerStats> myStats;


        public StatsAdapter(ArrayList<PlayerStats> playerStats) {
            this.myStats = playerStats;
        }

        @Override
        public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup cardView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.player_card, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(lp);
            View statsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_layout, null);
            cardView.addView(statsView);
            return new StatsViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(StatsViewHolder holder, int position) {
            final PlayerStats playerStats = myStats.get(position);
            holder.nameView.setText(playerStats.nickname);
            holder.totalGamesView.setText(String.format("%d", playerStats.totalGames));
            holder.rankedOneVsOneWinsView.setText(String.format("%d", playerStats.rankedOneVsOneWins));
            holder.rankedOneVsOneLossesView.setText(String.format("%d", playerStats.rankedOneVsOneLosses));
            holder.rankedOneVsOneDisconnectsView.setText(String.format("%d", playerStats.rankedOneVsOneDisconnects));
            holder.rankedOneVsOneDesyncsView.setText(String.format("%d", playerStats.rankedOneVsOneDesyncs));
            holder.rankedTwoVsTwoGamesView.setText(String.format("%d", playerStats.rankedTwoVsTwoGames ));
            holder.totalWinsView.setText(String.format("%d", playerStats.totalWins ));
            holder.totalLossesView.setText(String.format("%d", playerStats.totalLosses ));
            holder.totalDisconnectsView.setText(String.format("%d", playerStats.totalDisconnects ));
            holder.totalDesyncsView.setText(String.format("%d", playerStats.totalDesyncs ));
        }


        @Override
        public int getItemCount() {
            return myStats.size();
        }
    }

    private static class StatsViewHolder extends RecyclerView.ViewHolder {
        final TextView nameView;
        final TextView totalGamesView;
        final TextView rankedOneVsOneWinsView;
        final TextView rankedOneVsOneLossesView;
        final TextView rankedOneVsOneDisconnectsView;
        final TextView rankedOneVsOneDesyncsView;
        final TextView rankedTwoVsTwoGamesView;
        final TextView totalWinsView;
        final TextView totalLossesView;
        final TextView totalDisconnectsView;
        final TextView totalDesyncsView;

        public StatsViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.player_stat_name);
            totalGamesView = (TextView) itemView.findViewById(R.id.player_stat_total_games);
            rankedOneVsOneWinsView = (TextView) itemView.findViewById(R.id.player_stat_ranked_1_vs_1_wins);
            rankedOneVsOneLossesView = (TextView) itemView.findViewById(R.id.player_stat_ranked_1_vs_1_losses);
            rankedOneVsOneDisconnectsView = (TextView) itemView.findViewById(R.id.player_stat_ranked_1_vs_1_disconnects);
            rankedOneVsOneDesyncsView = (TextView) itemView.findViewById(R.id.player_stat_ranked_1_vs_1_desyncs);
            rankedTwoVsTwoGamesView = (TextView) itemView.findViewById(R.id.player_stat_ranked_2_vs_2_games);
            totalWinsView = (TextView) itemView.findViewById(R.id.player_stat_total_wins);
            totalLossesView = (TextView) itemView.findViewById(R.id.player_stat_total_losses);
            totalDisconnectsView = (TextView) itemView.findViewById(R.id.player_stat_total_disconnects);
            totalDesyncsView = (TextView) itemView.findViewById(R.id.player_stat_total_desyncs);
        }
    }


    public static class StatsFragment extends RecyclerViewFragment {
        private static final String TAG = "StatsFragment";

        protected RecyclerView mRecyclerView;
        protected StatsAdapter mAdapter;
        protected RecyclerView.LayoutManager mLayoutManager;
        protected ArrayList<PlayerStats> mDataset;


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

            mAdapter = new StatsAdapter(mDataset);
            Log.v(TAG, "setting the adapter for player");
            mRecyclerView.setAdapter( mAdapter );
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater, container, savedInstanceState);

            mDataset = new ArrayList<>();

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

            mAdapter = new StatsAdapter(mDataset);
            // Set CustomAdapter as the adapter for RecyclerView.
            Log.v(TAG, "setting the adapter for player");
            mRecyclerView.setAdapter(mAdapter);

            return rootView;

        }

        public void setData(final ArrayList<PlayerStats> data, final Activity activity){
            if(!isAdded())
                mAdapter = new StatsAdapter(mDataset);
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.v(TAG, "Setting data");
                    if (mDataset == null){
                        mDataset = new ArrayList<>();
                    }
                    mDataset.clear();
                    mDataset.addAll(data);
                    mAdapter.notifyDataSetChanged();
                }
            });

        }

    }

}
