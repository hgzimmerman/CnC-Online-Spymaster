package com.mooo.ziggypop.candconline;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ziggypop on 4/7/15.
 */
public class JsonHandler {


    public static final String TAG = "JSON";


    private JSONObject jsonCache;
    private MainActivity mainActivity;
    public JsonHandler(MainActivity activity){
        mainActivity = activity;
    }

    public void updateViews(){
        if (jsonCache != null) {
            try {

                Log.v(TAG, mainActivity.getQueryJsonString());
                Log.v(TAG, mainActivity.toString());
                JSONObject game = jsonCache.getJSONObject(mainActivity.getQueryJsonString());

            /*===START_PLAYERS===*/
                ArrayList<Player> usersArray = getPlayers(game);

                JSONObject gameClasses = game.getJSONObject("games");
            /*===START_LOBBIES===*/
                ArrayList<Game> gamesInLobbyArrList = getGames(gameClasses, "staging");
                ArrayList<Game> gamesInProgressArrList = getGames(gameClasses, "playing");


                //Refresh the data for each view.
                Player.PlayersFragment playerFrag = mainActivity.mSectionsPagerAdapter.player;
                playerFrag.refreshData(usersArray);

                Game.GamesFragment gamesFrag = mainActivity.mSectionsPagerAdapter.lobby;
                gamesFrag.refreshData(gamesInLobbyArrList);

                Game.GamesInProgressFragment progressFrag = mainActivity.mSectionsPagerAdapter.inGame;
                progressFrag.refreshData(gamesInProgressArrList, mainActivity);


                getPlayersPerGame(jsonCache, mainActivity);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        }


    public void refreshAndUpdateViews(){

        new JsonGetter(mainActivity).execute();
        //updateViews();
    }


    /*
     * Helper method for onPostExecute()
     * This gets and assembles the json data into an arraylist of Players
     */
    public static ArrayList<Player> getPlayers(JSONObject gameObject){
        ArrayList<Player> returnArr = new ArrayList<>();

        JSONObject usersObject = null;
        try {
            usersObject = gameObject.getJSONObject("users");
        } catch(JSONException e){
            e.printStackTrace();
        }


        Iterator<String> iter = usersObject.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject value = (JSONObject) usersObject.get(key);
                returnArr.add(new Player(key,false));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return returnArr;
    }


    /*
     * Helper method for onPostExecute()
     * This gets and assembles all of the json data into an arraylist of games.
     */
    public static ArrayList<Game> getGames(JSONObject gameClasses, String typeOfGame) {
        ArrayList<Game> returnArr = new ArrayList<>();

        try {
            JSONArray lobbies = gameClasses.getJSONArray(typeOfGame);
            //Log.v(TAG, lobbies.toString());
            for (int i = 0; i < lobbies.length(); i++) {
                JSONObject lobby = lobbies.getJSONObject(i);
                String title = lobby.get("title").toString();
                String slots = "(" + lobby.get("numRealPlayers").toString() + "/"
                        + lobby.get("maxRealPlayers").toString() + ")";


                JSONArray playersJSON = lobby.getJSONArray("players");
                ArrayList<String> players = new ArrayList<>();
                for (int j = 0; j < playersJSON.length(); j++) {
                    players.add(playersJSON.getJSONObject(j).get("nickname").toString());
                }
                //get the map name and remove all characters before the last "/" or "\"
                String map = lobby.get("map").toString();
                map = map.substring(map.lastIndexOf('/')+1);
                map = map.substring(map.lastIndexOf('\\')+1);

                //Set up the lock.
                String lock = lobby.get("pw").toString();
                boolean isLocked = false;
                if (lock.equals("1")){ isLocked = true;}

                //Add a new Game to the array.
                returnArr.add(new Game(title, slots, players, isLocked, map));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnArr;
    }


    /*
     * Given the top level JSON Object, find the player count of each game and return them
     * in an arraylist.
     */
    public static ArrayList<Integer> getPlayersPerGame(JSONObject jsonAll, MainActivity myActivity){
        ArrayList<Integer> returnArr = new ArrayList<>();
        try {
            //Initialize the array of queries to iterate through.
            ArrayList<String> gameQueries = new ArrayList<>();
            gameQueries.add(myActivity.getString(R.string.KanesWrathJSON));
            gameQueries.add(myActivity.getString(R.string.CandC3JSON));
            gameQueries.add(myActivity.getString(R.string.GeneralsJSON));
            gameQueries.add(myActivity.getString(R.string.ZeroHourJSON));
            gameQueries.add(myActivity.getString(R.string.RedAlert3JSON));

            // get create arrays of the players for each game and give
            for( String query : gameQueries){
                JSONObject game = jsonAll.getJSONObject(query);
                ArrayList<Player> playerArray = getPlayers(game);
                returnArr.add(playerArray.size());
            }


            myActivity.mNavigationDrawerFragment.updateNames(returnArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnArr;
    }

    public class JsonGetter extends AsyncTask<URL, Integer, JSONObject> {


         JSONObject jobj = null;
         String json = "";

        MainActivity myActivity;

        public JsonGetter(MainActivity activity){
            myActivity = activity;
        }


        @Override
        public JSONObject doInBackground(URL... params) {
            try {
                URL url = new URL("http", "online.the3rdage.net", 29998, "index.html");
                Log.v("URL", url.toString());
                InputStream is = null;

                try{
                    is = url.openStream();
                } catch (IOException e){
                    Log.e("InputStream", "Could not open stream" );
                }
                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            is, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while((line = reader.readLine()) != null){
                        sb.append(line + "\n");
                    }
                    is.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result: " + e.toString());
                }

                try {
                    jobj = new JSONObject(json);
                } catch (JSONException e){
                    Log.e("JSON Parser", "Error parsing data: " + e.toString());
                }

            } catch (MalformedURLException e) {
                Log.e("????", "Malformed URL");
            }
            //Log.v("JSON", jobj.toString());
            return jobj;
        }

        @Override
        protected void onPreExecute() {
            myActivity.findViewById(R.id.llProgBar)
                    .setVisibility(View.VISIBLE);
            myActivity.findViewById(R.id.pager).setVisibility(View.INVISIBLE);
        }

        @Override
        public void onPostExecute(JSONObject result) {
            jsonCache = result;
            if (result != null){
                myActivity.findViewById(R.id.pager).setVisibility(View.VISIBLE);
                myActivity.findViewById(R.id.llProgBar)
                        .setVisibility(View.GONE);
            }
            updateViews();
        }
    }
}
