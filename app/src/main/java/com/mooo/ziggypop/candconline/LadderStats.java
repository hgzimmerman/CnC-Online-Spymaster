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
 * Created by ziggypop on 1/15/16.
 * LadderStats are used to display data from the ladder view
 */
public class LadderStats implements Parcelable {
    private String nickname;
    private int rankOneVsOne;
    private String ladderWinOverLoss;
    private int elo;
    private int games;
    private int wins;
    private int losses;
    private int disconnects;
    private int desyncs;

    public LadderStats(String nickname, int rankOneVsOne, String ladderWinOverLoss, int elo,
                       int games, int wins, int losses, int disconnects, int desyncs){
        this.nickname = nickname;
        this. rankOneVsOne = rankOneVsOne;
        this.ladderWinOverLoss = ladderWinOverLoss;
        this.elo = elo;
        this.games = games;
        this. wins = wins;
        this.losses = losses;
        this.disconnects = disconnects;
        this.desyncs = desyncs;
    }



    protected LadderStats(Parcel in) {
        nickname = in.readString();
        rankOneVsOne = in.readInt();
        ladderWinOverLoss = in.readString();
        elo = in.readInt();
        games = in.readInt();
        wins = in.readInt();
        losses = in.readInt();
        disconnects = in.readInt();
        desyncs = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(nickname);
        dest.writeInt(rankOneVsOne);
        dest.writeString(ladderWinOverLoss);
        dest.writeInt(elo);
        dest.writeInt(games);
        dest.writeInt(wins);
        dest.writeInt(losses);
        dest.writeInt(disconnects);
        dest.writeInt(desyncs);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LadderStats> CREATOR = new Parcelable.Creator<LadderStats>() {
        @Override
        public LadderStats createFromParcel(Parcel in) {
            return new LadderStats(in);
        }

        @Override
        public LadderStats[] newArray(int size) {
            return new LadderStats[size];
        }
    };


    private static class LadderStatsViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView rankOneVsOneView;
        TextView ladderWinOverLossView;
        TextView eloView;
        TextView gamesView;
        TextView winsView;
        TextView lossesView;
        TextView disconnectsView;
        TextView desyncsView;


        public LadderStatsViewHolder(View itemView) {
            super(itemView);
            nameView = (TextView) itemView.findViewById(R.id.ladder_stat_name);
            rankOneVsOneView = (TextView) itemView.findViewById(R.id.ladder_stat_rank_one_vs_one);
            ladderWinOverLossView = (TextView) itemView.findViewById(R.id.ladder_stat_win_over_loss);
            eloView = (TextView) itemView.findViewById(R.id.ladder_stat_elo);
            gamesView = (TextView) itemView.findViewById(R.id.ladder_stat_games);
            winsView = (TextView) itemView.findViewById(R.id.ladder_stat_wins);
            lossesView = (TextView) itemView.findViewById(R.id.ladder_stat_losses);
            disconnectsView = (TextView) itemView.findViewById(R.id.ladder_stat_disconnects);
            desyncsView = (TextView) itemView.findViewById(R.id.ladder_stat_desyncs);

        }
    }


    public static class StatsAdapter extends RecyclerView.Adapter<LadderStatsViewHolder> {

        public ArrayList<LadderStats> myStats;


        public StatsAdapter(ArrayList<LadderStats> ladderStats) {
            this.myStats = ladderStats;
        }

        @Override
        public LadderStatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewGroup cardView = (ViewGroup) LayoutInflater.from(parent.getContext()).inflate(R.layout.player_card, null);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cardView.setLayoutParams(lp);
            View statsView = LayoutInflater.from(parent.getContext()).inflate(R.layout.stats_ladder_layout, null);
            cardView.addView(statsView);
            return new LadderStatsViewHolder(cardView);
        }

        @Override
        public void onBindViewHolder(LadderStatsViewHolder holder, int position) {
            final LadderStats playerStats = myStats.get(position);
            holder.nameView.setText(playerStats.nickname);
            holder.rankOneVsOneView.setText(playerStats.rankOneVsOne + "");
            holder.ladderWinOverLossView.setText(playerStats.ladderWinOverLoss);
            holder.eloView.setText(playerStats.elo + "");
            holder.gamesView.setText(playerStats.games + "");
            holder.winsView.setText(playerStats.wins + "");
            holder.lossesView.setText(playerStats.losses + "");
            holder.disconnectsView.setText(playerStats.disconnects + "");
            holder.desyncsView.setText(playerStats.desyncs + "");

        }


        @Override
        public int getItemCount() {
            return myStats.size();
        }
    }


    public static class LadderStatsFragment extends RecyclerViewFragment {
        private static final String TAG = "StatsFragment";

        protected RecyclerView mRecyclerView;
        protected StatsAdapter mAdapter;
        protected RecyclerView.LayoutManager mLayoutManager;
        protected ArrayList<LadderStats> mDataset;


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

        public void setData(final ArrayList<LadderStats> data, final Activity activity){
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