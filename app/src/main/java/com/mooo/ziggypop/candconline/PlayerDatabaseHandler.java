package com.mooo.ziggypop.candconline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ziggypop on 12/29/15.
 * Handles the Database
 */
public class PlayerDatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 11;

    // Database Name
    private static final String DATABASE_NAME = "playerDB";

    // Contacts table name
    private static final String TABLE_PLAYERS = "players";

    // Contacts Table Columns names
    private static final String KEY_PLAYER_ID = "id";
    private static final String KEY_PLAYER_PID = "pid";
    private static final String KEY_PLAYER_NICKNAME = "nickname";
    private static final String KEY_PLAYER_IS_FRIEND = "friend";
    private static final String KEY_PLAYER_NOTIFICATIONS = "notifications";
    private static final String KEY_PLAYER_IS_YOURSELF = "yourself";
    private static final String KEY_PLAYER_IN_GAME_NAME = "ign";
    private static final String KEY_PLAYER_GAME = "game";



    public PlayerDatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RSS_TABLE = "CREATE TABLE " + TABLE_PLAYERS
                + "("
                + KEY_PLAYER_ID + " INTEGER PRIMARY KEY,"
                + KEY_PLAYER_PID + " INTEGER,"
                + KEY_PLAYER_NICKNAME + " TEXT,"
                + KEY_PLAYER_IS_FRIEND + " INTEGER,"
                + KEY_PLAYER_NOTIFICATIONS + " INTEGER,"
                + KEY_PLAYER_IS_YOURSELF + " INTEGER,"
                + KEY_PLAYER_IN_GAME_NAME + " TEXT,"
                + KEY_PLAYER_GAME + " INTEGER"
                + ")";
        db.execSQL(CREATE_RSS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This seems lazy TODO: don't break DB on updates
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        // Create tables again
        onCreate(db);
    }

    public void resetDB(SQLiteDatabase db){
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLAYERS);
        // Create tables again
        onCreate(db);
    }



    public ArrayList<Player> getAllPlayers() {
        ArrayList<Player> playerList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_PLAYERS
                + " ORDER BY id DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        int idIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_ID);
        int pidIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_PID);
        int nicknameIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_NICKNAME);
        int friendIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_IS_FRIEND);
        int notificationIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_NOTIFICATIONS);
        int yourselfIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_IS_YOURSELF);
        int ignIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_IN_GAME_NAME);
        int gameIndex = cursor.getColumnIndex(KEY_PLAYER_GAME);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Player player = new Player(
                        cursor.getString(nicknameIndex),
                        cursor.getInt(idIndex),
                        cursor.getInt(pidIndex),
                        (cursor.getInt(friendIndex) == 1),
                        (cursor.getInt(notificationIndex) == 1),
                        (cursor.getInt(yourselfIndex) == 1),
                        cursor.getString(ignIndex),
                        Player.intToGameEnum(cursor.getInt(gameIndex))
                );
                // Adding contact to list
                playerList.add(player);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        // return contact list
        return playerList;
    }


    public void addPlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_ID, player.getID());
        values.put(KEY_PLAYER_PID, player.getPID());
        values.put(KEY_PLAYER_NICKNAME, player.getNickname());
        values.put(KEY_PLAYER_IS_FRIEND, (player.getIsFriend())? 1 : 0);
        values.put(KEY_PLAYER_NOTIFICATIONS, (player.getIsRecieveNotifications())? 1 : 0);
        values.put(KEY_PLAYER_IS_YOURSELF, (player.getIsYourself())? 1 :0);
        values.put(KEY_PLAYER_IN_GAME_NAME, player.getUserName());
        values.put(KEY_PLAYER_GAME, Player.gameEnumToInt(player.getGame()));

        // Check if row already existed in database
        if (!isPlayerExists(db, player.getID())) {
            // if the player does not exist, create a new row
            db.insert(TABLE_PLAYERS, null, values);
            db.close();
        } else {
            // site already existed update the row
            updatePlayer(player);
            db.close();
        }
    }

    public Player getPlayer(Player oldPlayer) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_PLAYERS, new String[] { KEY_PLAYER_ID, KEY_PLAYER_PID,
                        KEY_PLAYER_NICKNAME, KEY_PLAYER_IS_FRIEND,
                KEY_PLAYER_NOTIFICATIONS ,KEY_PLAYER_IS_YOURSELF }, KEY_PLAYER_ID + "=?",
                new String[] { String.valueOf(oldPlayer.getID()) }, null, null, null, null);

        if (cursor != null
                && db.isOpen()
                && cursor.getCount() > 0) {
            cursor.moveToFirst();

            int idIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_ID);
            int pidIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_PID);
            int nicknameIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_NICKNAME);
            int friendIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_IS_FRIEND);
            int notificationIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_NOTIFICATIONS);
            int yourselfIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_IS_YOURSELF);
            int ignIndex = cursor.getColumnIndexOrThrow(KEY_PLAYER_IN_GAME_NAME);
            int gameIndex = cursor.getColumnIndex(KEY_PLAYER_GAME);

            Player player = new Player(
                    cursor.getString(nicknameIndex),
                    cursor.getInt(idIndex),
                    cursor.getInt(pidIndex),
                    (cursor.getInt(friendIndex) == 1),
                    (cursor.getInt(notificationIndex) == 1),
                    (cursor.getInt(yourselfIndex) == 1),
                    cursor.getString(ignIndex),
                    Player.intToGameEnum(cursor.getInt(gameIndex))
            );
            cursor.close();
            db.close();
            return player;
        } else {
            db.close();
            return oldPlayer;
        }
    }

    public void deletePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_PLAYERS, KEY_PLAYER_ID + " = ?",
                new String[] { String.valueOf(player.getID())});
        db.close();
    }

    public int updatePlayer(Player player) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PLAYER_ID, player.getID());
        values.put(KEY_PLAYER_PID, player.getPID());
        values.put(KEY_PLAYER_NICKNAME, player.getNickname());
        values.put(KEY_PLAYER_IS_FRIEND, (player.getIsFriend())? 1 : 0);
        values.put(KEY_PLAYER_NOTIFICATIONS, (player.getIsRecieveNotifications())? 1: 0);
        values.put(KEY_PLAYER_IS_YOURSELF, (player.getIsYourself())? 1 : 0);
        values.put(KEY_PLAYER_IN_GAME_NAME, player.getUserName());
        values.put(KEY_PLAYER_GAME, Player.gameEnumToInt(player.getGame()));

        // updating row return
        int update = db.update(TABLE_PLAYERS, values, KEY_PLAYER_ID + " = ?",
                new String[] { String.valueOf(player.getID()) });
        db.close();
        return update;
    }


    /**
     * Given a list of players online with their flags unset, replace the players also found in the DB.
     * This will be called when getting the list of players from the the remote website.
     * @param players The list of players currently online.
     * @return An ArrayList of Players that have their flags set.
     */
    public ArrayList<Player> augmentPlayers(ArrayList<Player> players){
        ArrayList<Player> newPlayers = new ArrayList<>();
        ArrayList<Player> dbPlayers =  getAllPlayers();
        //Todo: investigate if using a tree here would be faster, because nLog(n) is better than n^2
        for (Player player : players ){
            boolean isAdded = false;
            for (Player dbPlayer: dbPlayers){
                if (player.getID() == dbPlayer.getID()){
                    // if the name has changed, update the name. NOTE: This is hard for me to test
                    if ( ! player.getNickname().equals(dbPlayer.getNickname()) ) {
                        Player replacementPlayer = new Player(
                                player.getNickname(),
                                dbPlayer.getID(),
                                dbPlayer.getPID(),
                                dbPlayer.getIsFriend(),
                                dbPlayer.getIsRecieveNotifications(),
                                dbPlayer.getIsYourself(),
                                dbPlayer.getUserName(),
                                dbPlayer.getGame()
                        );
                        updatePlayer(replacementPlayer);
                        // add the db player with the correct name to the list
                        newPlayers.add(replacementPlayer);
                    } else {
                        // add the database player to the list if the name is the same.
                        newPlayers.add(dbPlayer);
                    }
                    // set a flag meaning that the player has been found in the db
                    isAdded = true;
                }
            }
            // if the player hasn't been found, add to the list
            if (!isAdded){
                newPlayers.add(player);
            }
        }

        return newPlayers;
    }

    /**
     * Gets the intersection of players that are online and those in the DB who have the
     * receiveNotification flag set.
     * @param players The list of players currently online.
     * @return An arrayList of marked players that are online currently.
     */
    public ArrayList<Player> getIntersectionOfPlayers(List<Player> players){
        ArrayList<Player> intersectedPlayers = new ArrayList<>();
        ArrayList<Player> dbPlayers = getAllPlayers();
        for (Player dbPlayer: dbPlayers) {
            for (Player player: players){
                if (player.getID() == dbPlayer.getID() && dbPlayer.getIsRecieveNotifications()){
                    intersectedPlayers.add(dbPlayer);
                }
            }
        }
        Collections.sort(intersectedPlayers);
        return intersectedPlayers;
    }


    public boolean isPlayerExists(SQLiteDatabase db, int id) {

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_PLAYERS
                + " WHERE " + KEY_PLAYER_ID + " = '" + id + "'", new String[] {});
        return (cursor.getCount() > 0);
    }


}
