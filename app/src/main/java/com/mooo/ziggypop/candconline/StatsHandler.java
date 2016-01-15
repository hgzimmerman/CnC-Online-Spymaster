package com.mooo.ziggypop.candconline;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * Created by ziggypop on 1/10/16.
 * Handles getting stats
 */
public class StatsHandler {
    public StatsHandler(Player.PlayerViewHolder viewHolder){
        this.viewHolder = viewHolder;

    }

    public static final String TAG = "StatsHandler";

    // Assemble the stats link: prefix + game_key + infix + player_name, MAYBE...
    private static final String STATS_PREFIX = "http://www.shatabrick.com/cco/";
    private static final String STATS_INFIX_1 = "/index.php?g=";
    private static final String STATS_INFIX_2 = "&a=sp&name=";

    Player.PlayerViewHolder viewHolder;


    //grab data for players and launch an activity showing them
    public void getPlayerStats(Player player){
        new StatsGetter().execute(player);
    }
    //grab data for ladders and launch an activity showing them
    public void getLadderStats(Player player){ new LadderStatsGetter().execute(player);}



    private String addressBuilder(Player.GameEnum gameEnum, String nickname){
        String address;
        //red alert three url slightly inconsistent.
        if (gameEnum != Player.GameEnum.RedAlert3) {
            String gameKey = Player.gameEnumToQueryString(gameEnum);
            address = STATS_PREFIX
                    + gameKey
                    + STATS_INFIX_1
                    + gameKey
                    + STATS_INFIX_2
                    + nickname;
        } else {
            address = STATS_PREFIX
                    + "ra3"
                    + STATS_INFIX_1
                    + "ra"
                    + STATS_INFIX_2
                    + nickname;
        }
        return address;
    }
    /**
     * Gets the stats for the general table.
     */
    public class StatsGetter extends AsyncTask<Player, Integer, ArrayList<PlayerStats>> {

        private static final int TIMEOUT = 10000; // ten seconds
        protected String errorMessage = "";

        @Override
        protected ArrayList<PlayerStats> doInBackground(Player... players) {
            ArrayList<PlayerStats> allAccounts = new ArrayList<>();
            try {
                String address = addressBuilder(players[0].getGame(), players[0].getNickname());
                Log.d("JSwa", "Connecting to [" + address + "]");

                try {
                    Document doc = Jsoup.connect(address).timeout(TIMEOUT).get();

                    Log.d("JSwa", "Connected to [" + address + "]");
                    // Get document (HTML page) title
                    String title = doc.title();
                    Log.d("JSwA", "Title [" + title + "]");

                    Element tableBody;
                    if (players[0].getGame() == Player.GameEnum.None){
                        errorMessage = "Player Stored incorrectly";
                    }

                    Elements tables = doc.select("#calendar_wrap"); // there may be multiple
                    Element table = null;
                    //Do game specific parsing actions here
                    // Find the most populated table, avoid empty tables.
                    switch (players[0].getGame()){
                        case KanesWrath:
                            Log.d(TAG, "tables size = " + tables.size());
                            if (tables.size() == 0){
                                Log.e(TAG, "No tables found, the URL was probably malformed");
                                errorMessage = "Malformed URL";
                            } else if (tables.size() == 1) {
                                Log.e(TAG, "One table found, the player probably hasn't played a game");
                                errorMessage = "No Stats Available";
                            } else if (tables.size() == 2){
                                // The first possible table is non-existent, so get the first possible one
                                if (doc.getElementsContainingText("   Not found in Kanes Wrath Ladders").size() > 0){
                                    Log.v(TAG, "Ladder table not found, selecting first table");
                                    table = tables.get(0);
                                } else {
                                    Log.v(TAG, "Ladder table found, selecting the second table");
                                    table = tables.get(0);
                                }
                                tableBody = table.select("tbody").get(0);
                                allAccounts = parsePlayerStats(tableBody);
                            } else if (tables.size() == 3){
                                table = tables.get(1);
                                tableBody = table.select("tbody").get(0);
                                allAccounts = parsePlayerStats(tableBody);
                            }
                            break;
                        case CnC3:
                            Log.d(TAG, "tables size = " + tables.size());
                            if (tables.size() == 0){
                                Log.e(TAG, "No tables found, the URL was probably malformed");
                                errorMessage = "Malformed URL";
                            } else if (tables.size() == 1) {
                                Log.e(TAG, "One table found, the player probably hasn't played a game");
                                errorMessage = "No Stats Available";
                            } else if (tables.size() == 2){
                                // The first possible table is non-existent, so get the first possible one
                                if (doc.getElementsContainingText("   Not found in Tiberium Wars Ladders ").size() > 0){
                                    table = tables.get(0);
                                } else {
                                    table = tables.get(1);
                                }

                                tableBody = table.select("tbody").get(0);
                                allAccounts = parsePlayerStats(tableBody);
                            } else if (tables.size() == 3){
                                table = tables.get(1);
                                tableBody = table.select("tbody").get(0);
                                allAccounts = parsePlayerStats(tableBody);
                            }
                            break;
                        case Generals:
                            if (tables.size() == 0){
                                Log.e(TAG, "No tables found, the URL was probably malformed");
                                errorMessage = "Malformed URL";
                            } else if ( tables.size() == 1){
                                Log.e(TAG, "One table found, the player probably hasn't played a game");
                                errorMessage = "No Stats Available";
                            } else if (tables.size() == 2) {


                                table = tables.get(1);
                                tableBody = table.select("tbody").get(0);
                                errorMessage = "Generals does not support Player Statistics";
                                //generalsParcer(tableBody);

                            }
                            break;
                        case ZeroHour:
                            if (tables.size() == 0){
                                Log.e(TAG, "No tables found, the URL was probably malformed");
                                errorMessage = "Malformed URL";
                            } else if ( tables.size() == 1){
                                Log.e(TAG, "One table found, the player probably hasn't played a game");
                                errorMessage = "No Stats Available";
                            } else if (tables.size() == 2){
                                errorMessage = "Zero Hour does not support Player Statistics";
                                if (doc.getElementsContainingText(   "Not found in Generals" ).size() > 0) {
                                    table = tables.get(0);
                                } else {
                                    table = tables.get(1);
                                    tableBody = table.select("tbody").get(0);
                                }
                                // TODO: create another parser and stats type for generals and ZH
                                // TODO: just remove the button for those games.
                                //allAccounts = parsePlayerStats(tableBody);
                            }
                            break;
                        case RedAlert3:
                            switch (tables.size()){
                                case 0:
                                    Log.e(TAG, "No tables found, the URL was probably malformed");
                                    errorMessage = "Malformed URL";
                                    break;
                                case 1:
                                    Log.e(TAG, "One table found, the player probably hasn't played a game");
                                    errorMessage = "No Stats Available";
                                    break;
                                case 2:
                                    table = tables.get(0);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parsePlayerStats(tableBody);
                                    break;
                                case 3:
                                    table = tables.get(1);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parsePlayerStats(tableBody);
                            }

                            break;
                    }


                } catch (SocketTimeoutException e){
                    errorMessage = "Request Timed Out";
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return allAccounts;
        }

        private ArrayList<PlayerStats> parsePlayerStats(Element tableBody){
            Log.v(TAG, "parsing the table");
            ArrayList<PlayerStats> accounts = new ArrayList<>();
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
                accounts.add(playerAlias);
                //update the progressbar
                publishProgress((int) (( index/ (float) numberOfAliases) * 100));
            }
            return accounts;
        }



        @Override
        protected void onPreExecute() {
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

            //It returns an empty ArrayList
            if (s.size() == 0){
                viewHolder.progressBar.setProgress(0);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                // send a toast indicating failure
                Toast toast = Toast.makeText(viewHolder.bigView.getContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT);
                toast.show();

                // The ArrayList contains items
            } else {
                //viewHolder.progressBar.setProgress(0);
                Intent statsIntent = new Intent(viewHolder.bigView.getContext(), StatsViewerActivity.class);
                statsIntent.putParcelableArrayListExtra("statsPlayers", s);

            /*
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    // the context of the activity
                    Pair<View, String>viewHolder.name
            ) */
                viewHolder.bigView.getContext().startActivity(statsIntent);
            }
        }
    }

    /**
     * Gets the stats for the ladder table.
     */
    public class LadderStatsGetter extends AsyncTask<Player, Integer, ArrayList<LadderStats>> {
        private static final int TIMEOUT = 10000; // ten seconds
        protected String errorMessage = "";

        @Override
        protected ArrayList<LadderStats> doInBackground(Player... players) {
            ArrayList<LadderStats> allAccounts = new ArrayList<>();
            try {
                String address = addressBuilder(players[0].getGame(), players[0].getNickname());
                Log.d("JSwa", "Connecting to [" + address + "]");

                try {
                    Document doc = Jsoup.connect(address).timeout(TIMEOUT).get();

                    Log.d("JSwa", "Connected to [" + address + "]");
                    // Get document (HTML page) title
                    String title = doc.title();
                    Log.d("JSwA", "Title [" + title + "]");

                    Element tableBody;
                    if (players[0].getGame() == Player.GameEnum.None){
                        errorMessage = "Player Stored incorrectly";
                    }

                    Elements tables = doc.select("#calendar_wrap"); // there may be multiple
                    Element table = null;
                    Log.d(TAG, "tables size = " + tables.size());
                    //Do game specific parsing actions here
                    // Find the most populated table, avoid empty tables.
                    switch (players[0].getGame()){
                        case KanesWrath:
                            switch (tables.size()){
                                case 0:
                                    Log.e(TAG, "No tables found, the URL was probably malformed");
                                    errorMessage = "Malformed URL";
                                    break;
                                case 1:
                                    Log.e(TAG, "One table found, the player probably hasn't played a game");
                                    errorMessage = "No Ladder Stats Available";
                                    break;
                                case 2:
                                    Log.e(TAG, "Ladder Table not found");
                                    errorMessage = "Ladder Stats not Available";
                                    break;
                                case 3:
                                    table = tables.get(1);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parseLadderStats(tableBody, Player.GameEnum.KanesWrath);
                                    break;
                            }
                            break;
                        case CnC3:
                            switch (tables.size()){
                                case 0:
                                    Log.e(TAG, "No tables found, the URL was probably malformed");
                                    errorMessage = "Malformed URL";
                                    break;
                                case 1:
                                    Log.e(TAG, "One table found, the player probably hasn't played a game");
                                    errorMessage = "No Ladder Stats Available";
                                    break;
                                case 2:
                                    Log.e(TAG, "Ladder Table not found");
                                    errorMessage = "Ladder Stats not Available";
                                    break;
                                case 3:
                                    table = tables.get(1);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parseLadderStats(tableBody, Player.GameEnum.CnC3);
                                    break;
                            }
                            break;
                        case Generals:
                            switch (tables.size()){
                                case 0:
                                    Log.e(TAG, "No tables found, the URL was probably malformed");
                                    errorMessage = "Malformed URL";
                                    break;
                                case 1:
                                    Log.e(TAG, "One table found, the player probably hasn't played a game");
                                    errorMessage = "No Ladder Stats Available";
                                    break;
                                case 2:
                                    //Todo: verify that this handles missing tables correctly
                                    if (doc.getElementsContainingText("Not found in Generals").size() > 0){
                                        errorMessage = "Ladder Stats not available";
                                    } else {
                                        table = tables.get(0);
                                        tableBody = table.select("tbody").get(0);
                                        allAccounts = parseLadderStats(tableBody, Player.GameEnum.Generals);
                                    }
                                    break;
                                case 3:
                                    Log.v(TAG, "Three tables");
                                    table = tables.get(1);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parseLadderStats(tableBody, Player.GameEnum.Generals);
                            }
                            break;
                        case ZeroHour:
                            switch (tables.size()){
                                case 0:
                                    Log.e(TAG, "No tables found, the URL was probably malformed");
                                    errorMessage = "Malformed URL";
                                    break;
                                case 1:
                                    Log.e(TAG, "One table found, the player probably hasn't played a game");
                                    errorMessage = "No Ladder Stats Available";
                                    break;
                                case 2:
                                    //Todo: verify that this handles missing tables correctly
                                    if (doc.getElementsContainingText("Not found in Generals").size() > 0){
                                        table = tables.get(0);
                                        tableBody = table.select("tbody").get(0);
                                        allAccounts = parseLadderStats(tableBody, Player.GameEnum.Generals);
                                    } else {
                                        errorMessage = "Ladder Stats not available";
                                    }

                                    break;
                                case 3:
                                    Log.v(TAG, "Three tables");
                                    table = tables.get(2);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parseLadderStats(tableBody, Player.GameEnum.Generals);
                            }

                            break;
                        case RedAlert3:
                            switch (tables.size()){
                                case 0:
                                    Log.e(TAG, "No tables found, the URL was probably malformed");
                                    errorMessage = "Malformed URL";
                                    break;
                                case 1:
                                    Log.e(TAG, "One table found, the player probably hasn't played a game");
                                    errorMessage = "Ladder Stats Not Available";
                                    break;
                                case 2:
                                    Log.e(TAG, "Ladder Table not found");
                                    errorMessage = "Ladder Stats Not Available";
                                    break;
                                case 3:
                                    table = tables.get(1);
                                    tableBody = table.select("tbody").get(0);
                                    allAccounts = parseLadderStats(tableBody, Player.GameEnum.RedAlert3);
                            }

                            break;
                    }


                } catch (SocketTimeoutException e){
                    errorMessage = "Request Timed Out";
                }

            } catch (Throwable t) {
                t.printStackTrace();
            }

            return allAccounts;
        }

        private ArrayList<LadderStats> parseLadderStats(Element tableBody, Player.GameEnum gameEnum){
            Log.v(TAG, "parsing the table");
            ArrayList<LadderStats> accounts = new ArrayList<>();

            // The tables do not have a Rank Icon at the beginning for Generals or ZH.
            // So to get the same stats, the index of the table must be one less for those games.
            int offset = 0;
            if (gameEnum == Player.GameEnum.Generals || gameEnum == Player.GameEnum.ZeroHour){
                offset = 1;
            }

            int numberOfAliases =  tableBody.select("tr").size();
            int index = 0;

            for (Element tr: tableBody.select("tr")) {
                index++;
                Elements tds = tr.select("td");
                String nickname = tds.get(1 - offset).text();
                int rank = Integer.parseInt(tds.get(2 - offset).text());
                String ladderWL = tds.get(3 - offset).text();
                int elo = Integer.parseInt(tds.get(4 - offset).text());
                int games = Integer.parseInt(tds.get(5 - offset).text());
                int wins = Integer.parseInt(tds.get(6 - offset).text());
                int losses = Integer.parseInt(tds.get(7 - offset).text());
                int disconnects = Integer.parseInt(tds.get(8 - offset).text());
                int desyncs = Integer.parseInt(tds.get(9 -offset).text());

                Log.v(TAG, nickname + " : " + ladderWL);
                LadderStats playerAlias = new LadderStats(nickname,
                        rank,
                        ladderWL,
                        elo,
                        games,
                        wins,
                        losses,
                        disconnects,
                        desyncs);
                accounts.add(playerAlias);
                //update the progressbar
                publishProgress((int) (( index/ (float) numberOfAliases) * 100));
            }
            return accounts;
        }

        @Override
        protected void onPreExecute() {
            viewHolder.progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            viewHolder.progressBar.setProgress(progress[0]);
        }


        @Override
        protected void onPostExecute(ArrayList<LadderStats> s) {
            super.onPostExecute(s);

            //It returns an empty ArrayList
            if (s.size() == 0){
                viewHolder.progressBar.setProgress(0);
                viewHolder.progressBar.setVisibility(View.INVISIBLE);
                // send a toast indicating failure
                Toast toast = Toast.makeText(viewHolder.bigView.getContext(),
                        errorMessage,
                        Toast.LENGTH_SHORT);
                toast.show();

                // The ArrayList contains items
            } else {
                //viewHolder.progressBar.setProgress(0);
                Intent statsIntent = new Intent(viewHolder.bigView.getContext(), StatsViewerActivity.class);
                statsIntent.putParcelableArrayListExtra("statsLadder", s);

            /*
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    // the context of the activity
                    Pair<View, String>viewHolder.name
            ) */
                viewHolder.bigView.getContext().startActivity(statsIntent);
            }
        }


    }
}