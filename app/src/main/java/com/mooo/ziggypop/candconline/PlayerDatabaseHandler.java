package com.mooo.ziggypop.candconline;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ziggypop on 12/29/15.
 * Handles the Database
 */
public class PlayerDatabaseHandler extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 9;

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
                + KEY_PLAYER_IS_YOURSELF + " INTEGER"
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

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Player player = new Player(
                        cursor.getString(nicknameIndex),
                        cursor.getInt(idIndex),
                        cursor.getInt(pidIndex),
                        (cursor.getInt(friendIndex) == 1),
                        (cursor.getInt(notificationIndex) == 1),
                        (cursor.getInt(yourselfIndex) == 1));
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

            Player player = new Player(
                    cursor.getString(nicknameIndex),
                    cursor.getInt(idIndex),
                    cursor.getInt(pidIndex),
                    (cursor.getInt(friendIndex) == 1),
                    (cursor.getInt(notificationIndex) == 1),
                    (cursor.getInt(yourselfIndex) == 1));
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

        // updating row return
        int update = db.update(TABLE_PLAYERS, values, KEY_PLAYER_ID + " = ?",
                new String[] { String.valueOf(player.getID()) });
        db.close();
        return update;
    }


    public ArrayList<Player> augmentPlayers(ArrayList<Player> players){
        ArrayList<Player> newPlayers = new ArrayList<>();
        ArrayList<Player> dbPlayers =  getAllPlayers();
        for (Player player : players ){
            boolean isAdded = false;
            for (Player dbPlayer: dbPlayers){
                if (player.getID() == dbPlayer.getID()){
                    newPlayers.add(dbPlayer);
                    isAdded = true;
                }
            }
            if (!isAdded){
                newPlayers.add(player);
            }
        }


        return newPlayers;
    }

    public ArrayList<Player> getIntersectionOfPlayers(List<Player> players){
        ArrayList<Player> intersectedPlayers = new ArrayList<>();
        ArrayList<Player> dbPlayers = getAllPlayers();
        for (Player dbPlayer: dbPlayers) {
            for (Player player: players){
                if (player.getID() == dbPlayer.getID()){
                    intersectedPlayers.add(dbPlayer);
                }
            }
        }
        return intersectedPlayers;
    }


    public boolean isPlayerExists(SQLiteDatabase db, int id) {

        Cursor cursor = db.rawQuery("SELECT 1 FROM " + TABLE_PLAYERS
                + " WHERE " + KEY_PLAYER_ID + " = '" + id + "'", new String[] {});
        return (cursor.getCount() > 0);
    }


}
