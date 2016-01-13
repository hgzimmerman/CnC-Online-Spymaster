package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by ziggypop on 1/13/16.
 */
public class PlayerStats implements Parcelable {
    private String nickname;
    private int totalGames;
    private int rankedOneVsOneWins;
    private int rankedOneVsOneLosses;
    private int rankedOneVsOneDisconnects;
    private int rankedOneVsOneDesyncs;
    private int rankedTwoVsTwoGames;
    private int totalWins;
    private int totalLosses;
    private int totalDisconnects;
    private int totalDesyncs;

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

    public static class StatsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public ArrayList<PlayerStats> myStats;


        public StatsAdapter(ArrayList<PlayerStats> playerStats) {
            this.myStats = playerStats;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return myStats.size();
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
