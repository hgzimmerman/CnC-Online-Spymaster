package com.mooo.ziggypop.candconline;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by ziggypop on 1/10/16.
 * Handles getting stats
 */
public class StatsHandler {
    public StatsHandler(Player.PlayersAdapter.LargeViewHolder viewHolder){
        this.viewHolder = viewHolder;

    }

    public static final String TAG = "StatsHandler";

    // Assemble the stats link: prefix + game_key + infix + player_name, MAYBE...
    private static final String STATS_PREFIX = "http://www.shatabrick.com/cco/";
    private static final String STATS_INFIX_1 = "/index.php?g=";
    private static final String STATS_INFIX_2 = "&a=sp&name=";

    Player.PlayersAdapter.LargeViewHolder viewHolder;



    public void getStats(Player player){
        new StatsGetter().execute(player);
    }

    public class StatsGetter extends AsyncTask<Player, Integer, ArrayList<PlayerStats>> {

        @Override
        protected ArrayList<PlayerStats> doInBackground(Player... players) {
            ArrayList<PlayerStats> allAccounts = new ArrayList<>();
            try {
                String gameKey = Player.gameEnumToQueryString(players[0].getGame());
                String address = STATS_PREFIX
                        + gameKey
                        + STATS_INFIX_1
                        + gameKey
                        + STATS_INFIX_2
                        + players[0].getNickname();

                Log.d("JSwa", "Connecting to [" + address+ "]");
                Document doc = Jsoup.connect(address).get();
                Log.d("JSwa", "Connected to [" + address + "]");
                // Get document (HTML page) title
                String title = doc.title();
                Log.d("JSwA", "Title [" + title + "]");

                Element table = doc.select("#calendar_wrap").get(1); // for some awful reason they use multiple id's
                Element tableBody = table.select("tbody").get(0);

                int numberOfAliases =  tableBody.select("tr").size();
                int index = 0;

                for (Element tr: tableBody.select("tr")) {
                    index++;
                    Elements tds = tr.select("td");
                    String nickname = tds.get(1).text();
                    int totalGames = Integer.parseInt(tds.get(2).text());
                    int rankedOneVsOneWins =Integer.parseInt(tds.get(3).text());
                    int rankedOneVsOneLosses = Integer.parseInt(tds.get(4).text());
                    int rankedOneVsOneDisconnects = Integer.parseInt(tds.get(5).text());
                    int rankedOneVsOneDesyncs = Integer.parseInt(tds.get(6).text());
                    int rankedTwoVsTwoGames = Integer.parseInt(tds.get(7).text());
                    int totalWins = Integer.parseInt(tds.get(8).text());
                    int totalLosses = Integer.parseInt(tds.get(9).text());
                    int totalDisconnects = Integer.parseInt(tds.get(10).text());
                    int totalDesyncs = Integer.parseInt(tds.get(11).text());
                    Log.v(TAG, nickname + " : " + totalGames);
                    PlayerStats playerAlias = new PlayerStats(nickname, totalGames,
                            rankedOneVsOneWins, rankedOneVsOneLosses, rankedOneVsOneDisconnects,
                            rankedOneVsOneDesyncs, rankedTwoVsTwoGames, totalWins, totalLosses,
                            totalDisconnects, totalDesyncs);
                    allAccounts.add(playerAlias);
                    //update the progressbar
                    publishProgress((int) (( index/ (float) numberOfAliases) * 100));
                }



            } catch (Throwable t) {
                t.printStackTrace();
            }

            return allAccounts;


        }


        @Override
        protected void onPreExecute() {
            //Todo: maybe hide some views or show a progress bar
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            viewHolder.progressBar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(ArrayList<PlayerStats> s) {
            super.onPostExecute(s);

            if (s.size() == 0){
                viewHolder.progressBar.setProgress(0);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                //TODO: send a toast or something
                Toast toast = Toast.makeText(viewHolder.holderView.getContext(),
                        "Error Getting Stats",
                        Toast.LENGTH_SHORT);
                toast.show();
            } else {
                //viewHolder.progressBar.setProgress(0);
                Intent statsIntent = new Intent(viewHolder.holderView.getContext(), StatsViewerActivity.class);
                statsIntent.putParcelableArrayListExtra("statsPlayers", s);

            /*
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    // the context of the activity
                    Pair<View, String>viewHolder.name


            )
            */


                viewHolder.holderView.getContext().startActivity(statsIntent);
            }
        }
    }
}



