package com.mooo.ziggypop.candconline;

import android.os.AsyncTask;
import android.util.Log;


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
 * Created by ziggypop on 3/29/15.
 */
public class JsonGetter extends AsyncTask<URL, Integer, JSONObject> {

    public static final String TAG = "JSON";

    static JSONObject jobj = null;
    static String json = "";

    MainActivity myActivity;

    public JsonGetter(MainActivity activity){
        myActivity = activity;
    }


    @Override
    public JSONObject doInBackground(URL... params) {

        try {
            URL url = new URL("http", "online.the3rdage.net", 29998, "index.html");
            Log.v("URL",url.toString());
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
                String line = null;
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
    public void onPostExecute(JSONObject result){
        try {

            Log.v(TAG, myActivity.queryJsonString);
            JSONObject game = result.getJSONObject(myActivity.queryJsonString);

            /*===START_PLAYERS===*/
            JSONObject usersForGame = game.getJSONObject("users");

            ArrayList<String> usersArray = new ArrayList<>();
            Iterator<String> iter = usersForGame.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject value = (JSONObject) usersForGame.get(key);
                    usersArray.add(key);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
            /*===END_PLAYERS===*/

            JSONObject gameClasses = game.getJSONObject("games");
            /*===START_LOBBY===*/
            ArrayList<Game> gamesInLobbyArrList = getGames(gameClasses, "staging");
            /*===END_LOBBY===*/
            /*===Start_IN_PROGRESS===*/
            ArrayList<Game> gamesInProgressArrList = getGames(gameClasses, "playing");
             /*==End_IN_Progress===*/


            //Refresh the data for each view.
            PlayersFragment playerFrag = myActivity.mSectionsPagerAdapter.player;
            playerFrag.refreshData(usersArray);

            GamesFragment gamesFrag = myActivity.mSectionsPagerAdapter.lobby;
            gamesFrag.refreshData(gamesInLobbyArrList);


            GamesInProgressFragment progressFrag = myActivity.mSectionsPagerAdapter.inGame;
            progressFrag.onAttach(myActivity);

            progressFrag.refreshData(gamesInProgressArrList, myActivity);

        } catch (JSONException e){
            e.printStackTrace();
        }

    }

    /*
     * Helper method for onPostExecute()
     * This gets and assembles all of the json data into an arraylist of games.
     */
    public ArrayList<Game> getGames(JSONObject gameClasses, String typeOfGame) {
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
                //get the map name and remove all characters before the last "/"
                String map = lobby.get("map").toString();
                map = map.substring(map.lastIndexOf('/')+1);

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
}
