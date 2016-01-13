package com.mooo.ziggypop.candconline;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

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

    Player.PlayersAdapter.LargeViewHolder viewHolder;

    public StatsHandler(Player.PlayersAdapter.LargeViewHolder viewHolder){
        this.viewHolder = viewHolder;

    }


    public static final String TAG = "StatsHandler";

    // Assemble the stats link: prefix + game_key + infix + player_name, MAYBE...
    private static String STATS_PREFIX = "http://www.shatabrick.com/cco/";
    private static String STATS_INFIX = "/index.php?g=kw&a=sp&name=";

    private class PlayerStats{
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
                           int totalLosses, int totalDisconnects, int totalDesyncs){
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

    }

    public void getStats(Player player){
        new StatsGetter().execute(player);
    }

    public class StatsGetter extends AsyncTask<Player, Integer, ArrayList<PlayerStats>> {

        @Override
        protected ArrayList<PlayerStats> doInBackground(Player... players) {
            ArrayList<PlayerStats> allAccounts = new ArrayList<>();
            try {
                String address = STATS_PREFIX + "kw" + STATS_INFIX + players[0].getNickname();
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
            //Todo: update a progressbar setting at the bottom of the card.
            viewHolder.progressBar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(ArrayList<PlayerStats> s) {
            super.onPostExecute(s);
            //Todo: probably launch an activity using the PlayerStats objects to construct it's views
            //viewHolder.progressBar.setProgress(0);
        }
    }


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

}
