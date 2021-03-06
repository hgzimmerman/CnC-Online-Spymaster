package com.mooo.ziggypop.candconline;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ziggypop on 4/7/15.
 * Handles getting data from the website and updating the set of fragments based on that data.
 */
class JsonHandler {




    public static final String TAG = "JsonHandler";

    public static final String KW_JSON = "cnc3kw";
    public static final String CNC3_JSON = "cnc3";
    public static final String GENERALS_JSON = "generals";
    public static final String ZH_JSON = "generalszh";
    public static final String RA3_JSON = "ra3";



    public static final String PROTOCOL = "http";
    public static final String HOST = "server.cnc-online.net";
    public static final int PORT = 29998;
    public static final String FILE = "index.html";

    private JSONObject jsonCache;
    //private MainActivity mainActivity;

    private static PlayerDatabaseHandler db;


    public JsonHandler(Context context){
        db = new PlayerDatabaseHandler(context);
    }



    // updates the fragments with (offline) data based on the query string (the game to be viewed)
    public void updateViews(MainActivity mainActivity){
        if (jsonCache != null) {
            try {

                Log.v(TAG, "query String = " + mainActivity.getQueryJsonString());

                /*===Get_Base_Game_Json===*/
                String gameQueryString = "";
                Player.GameEnum gameType = mainActivity.getQueryJsonString();
                switch (gameType){
                    case None:
                        break;
                    case KanesWrath:
                        gameQueryString = KW_JSON;
                        break;
                    case CnC3:
                        gameQueryString = CNC3_JSON;
                        break;
                    case Generals:
                        gameQueryString = GENERALS_JSON;
                        break;
                    case ZeroHour:
                        gameQueryString = ZH_JSON;
                        break;
                    case RedAlert3:
                        gameQueryString = RA3_JSON;
                }
                JSONObject game = jsonCache.getJSONObject(gameQueryString);


                /*===START_PLAYERS===*/
                ArrayList<Player> usersArray = getAndAugmentPlayers(game, gameType);
                Log.v(TAG, "NUM_OF_PLAYERS: " + usersArray.size());

                /*===START_LOBBIES===*/
                JSONObject gameClasses = game.getJSONObject("games");
                ArrayList<Game> gamesInLobbyArrList = getGames(gameClasses, "staging");
                ArrayList<Game> gamesInProgressArrList = getGames(gameClasses, "playing");


                //Refresh the data for each view.
                Player.PlayersFragment playerFrag = mainActivity.mSectionsPagerAdapter.player;
                playerFrag.refreshData(usersArray, mainActivity);

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


    public void refreshAndUpdateViews(MainActivity mainActivity){
        new MainActivityJsonGetter(mainActivity).execute();
        //updateViews();
    }


    /**
     * Helper method for onPostExecute()
     * This gets and assembles the json data into an ArrayList of Player objects.
     */
    public static ArrayList<Player> getPlayers(JSONObject gameObject, Player.GameEnum game){
        ArrayList<Player> returnArr = new ArrayList<>();

        JSONObject usersObject;
        try {
            usersObject = gameObject.getJSONObject("users");

            Iterator<String> iterator = usersObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                try {
                    JSONObject value = (JSONObject) usersObject.get(key);
                    Player newPlayer = new Player(value.getString("nickname"),
                            Integer.parseInt(value.getString("id")),
                            Integer.parseInt(value.getString("pid"))
                            ,game);
                    returnArr.add(newPlayer);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
        }

        return returnArr;
    }

    public static ArrayList<Player> getAndAugmentPlayers(JSONObject gameObject, Player.GameEnum game){
        ArrayList<Player> players = getPlayers(gameObject, game);
        players = db.augmentPlayers(players);
        Collections.sort(players);

        return players;
    }

    private static ArrayList<Player> getIntersectionOfPlayersAllGames(JSONObject gameObject, Context context) {
        ArrayList<Player> returnArr = new ArrayList<>();

        try {
            List<JSONObject> gameJSONs = new ArrayList<>();
            gameJSONs.add(gameObject.getJSONObject(KW_JSON));
            gameJSONs.add(gameObject.getJSONObject(CNC3_JSON));
            gameJSONs.add(gameObject.getJSONObject(GENERALS_JSON));
            gameJSONs.add(gameObject.getJSONObject(ZH_JSON));
            gameJSONs.add(gameObject.getJSONObject(RA3_JSON));

            List<Player> allOnlinePlayers = new ArrayList<>();
            for ( JSONObject gameJSON: gameJSONs) {
                allOnlinePlayers.addAll(getPlayers(gameJSON, Player.GameEnum.None)); // Do not care what game we are dealing with
            }
            // For some reason, the service was crashing on a null reference here.
            if (db == null){
                db = new PlayerDatabaseHandler(context);
            }
            returnArr = db.getIntersectionOfPlayers(allOnlinePlayers);

        } catch (JSONException e ){
            Log.e(TAG, "JSONException:" + e.toString());
        }

        return returnArr;
    }


    /**
     * Helper method for onPostExecute()
     * This gets and assembles all of the json data into an ArrayList of Game objects.
     */
    public static ArrayList<Game> getGames(JSONObject gameClasses, String typeOfGame) {
        ArrayList<Game> returnArr = new ArrayList<>();

        try {
            JSONArray lobbies = gameClasses.getJSONArray(typeOfGame);
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

                //Set up the lock icon.
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


    /**
     * Given the top level JSON Object, find the player count of each game and return them
     * in an ArrayList.
     */
    public static ArrayList<Integer> getPlayersPerGame(JSONObject jsonAll, MainActivity myActivity){
        ArrayList<Integer> returnArr = new ArrayList<>();
        try {
            //Initialize the array of queries to iterate through.
            ArrayList<String> gameQueries = new ArrayList<>();
            gameQueries.add(KW_JSON);
            gameQueries.add(CNC3_JSON);
            gameQueries.add(GENERALS_JSON);
            gameQueries.add(ZH_JSON);
            gameQueries.add(RA3_JSON);

            // get create arrays of the players for each game and give
            for( String query : gameQueries){
                JSONObject game = jsonAll.getJSONObject(query);
                ArrayList<Player> playerArray = getPlayers(game, Player.GameEnum.None); // do not care about what game they come from.
                returnArr.add(playerArray.size());
            }


            myActivity.mNavigationDrawerFragment.updateNames(returnArr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return returnArr;
    }

    public class MainActivityJsonGetter extends AsyncTask<URL, Integer, JSONObject> {


         JSONObject jObj = null;
         String json = "";

        MainActivity myActivity;

        public MainActivityJsonGetter(MainActivity activity){
            myActivity = activity;
        }


        @Override
        public JSONObject doInBackground(URL... params) {
            try {
                URL url = new URL(PROTOCOL, HOST, PORT, FILE);
                Log.v(TAG, "URL = "+url.toString());
                InputStream is;
                try{
                    is = url.openStream();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                is, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while((line = reader.readLine()) != null){
                            sb.append(line).append("\n");
                        }
                        is.close();
                        json = sb.toString();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result: " + e.toString());
                    }
                    try {
                        jObj = new JSONObject(json);
                    } catch (JSONException e){
                        Log.e("JSON Parser", "Error parsing data: " + e.toString());
                    }

                } catch (IOException e){
                    Log.e("InputStream", "Could not open stream" );
                }

            } catch (MalformedURLException e) {
                Log.e("TAG", "Malformed URL");
            }
            return jObj;
        }

        @Override
        protected void onPreExecute() {
            myActivity.findViewById(R.id.pager).setVisibility(View.GONE);
            myActivity.mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        public void onPostExecute(JSONObject result) {
            jsonCache = result; // update the cache with new data
            myActivity.mSwipeRefreshLayout.setRefreshing(false);
            myActivity.findViewById(R.id.pager).setVisibility(View.VISIBLE);
            if (result == null) {
                //TOAST
                Context mContext = myActivity.getApplicationContext();
                Toast toast = Toast.makeText(mContext,
                        mContext.getString(R.string.connection_error),
                        Toast.LENGTH_LONG);
                toast.show();
            }
            updateViews(myActivity); //fill the pager with new content
        }
    }




    public static class ServiceJsonGetter extends AsyncTask<URL, Integer, JSONObject> {


        JSONObject jObj = null;
        String json = "";
        Context context;

        public ServiceJsonGetter(Context context){
            this.context = context;
        }


        @Override
        public JSONObject doInBackground(URL... params) {
            try {
                URL url = new URL(PROTOCOL, HOST, PORT, FILE);
                Log.v(TAG, "URL = "+url.toString());
                InputStream is;
                try{
                    is = url.openStream();
                    try {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(
                                is, "UTF-8"), 8);
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while((line = reader.readLine()) != null){
                            sb.append(line).append("\n");
                        }
                        is.close();
                        json = sb.toString();
                    } catch (Exception e) {
                        Log.e("Buffer Error", "Error converting result: " + e.toString());
                    }
                    try {
                        jObj = new JSONObject(json);
                    } catch (JSONException e){
                        Log.e("JSON Parser", "Error parsing data: " + e.toString());
                    }

                } catch (IOException e){
                    Log.e("InputStream", "Could not open stream" );
                }

            } catch (MalformedURLException e) {
                Log.e("TAG", "Malformed URL");
            }
            return jObj;
        }

        @Override
        protected void onPreExecute() {
            //do nothing
        }

        @Override
        public void onPostExecute(JSONObject result) {
            if (result != null) {
                ArrayList<Player> players = getIntersectionOfPlayersAllGames(result, context);
                Log.v(TAG, "Size of intersection of db players and players online: " + players.size());
                NotificationMessage.showNotification(context, players);
            }
        }

    }




}
