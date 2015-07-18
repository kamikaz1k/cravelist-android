package com.imperialtechnologies.theeatlist_3;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class DBTools extends SQLiteOpenHelper {

    public final static String TAG = "DBTools";
    public final static String EATEN = "eaten";
    public final static int EATEN_COL = 6;

    public DBTools(Context applicationContext){

        super(applicationContext, "foodList.db", null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        //Create FoodList Table
        String query = "CREATE TABLE foodList ( "
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "foodItemName TEXT, "
                + "foodItemLocation TEXT, "
                + "foodItemRating TEXT, "
                + "foodItemReview TEXT, "
                + "foodItemPicture TEXT, "
                + "foodItemEaten TEXT, "
                + "foodType TEXT)";

        database.execSQL(query);

        //Create FriendList Table
        query = "CREATE TABLE friendList ( "
                + "friendId INTEGER PRIMARY KEY, "
                + "firstName TEXT, "
                + "lastName TEXT, "
                + "cityLocation TEXT)";

        database.execSQL(query);

    }

    public void insert(){

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("friendId", "1");
        values.put("firstName", "Kaiser");
        values.put("lastName", "Dandangi");
        values.put("cityLocation", "Brampton");

        database.insert("friendList", null, values);

        values.clear();

        values.put("friendId", "2");
        values.put("firstName", "Ella");
        values.put("lastName", "Chan");
        values.put("cityLocation", "Markham");

        database.insert("friendList", null, values);

        values.clear();

        values.put("friendId", "3");
        values.put("firstName", "Satwick");
        values.put("lastName", "Sharma");
        values.put("cityLocation", "Windsor");

        database.insert("friendList", null, values);

        database.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {

        String query = "DROP TABLE IF EXISTS foodList";

        database.execSQL(query);
        onCreate(database);

    }

    public void clearAllRows() {

        SQLiteDatabase database = this.getWritableDatabase();

        String query = "DELETE FROM foodList";

        database.execSQL(query);

        database.close();

    }

    /** Takes a String eaten that can either be TRUE or FALSE. Database will delete all rows that match the string value under the column 'foodItemEaten' */
    public void clearAllEatenFoods(String eaten){

        SQLiteDatabase database = this.getWritableDatabase();

        String query = "DELETE * FROM foodList WHERE foodItemEaten='" + eaten + "'";

        database.execSQL(query);

        database.close();

    }

    public ArrayList<HashMap<String, String>> getAllEatenFoodItems(String eaten){
        //Returns foodItems based on eaten column

        ArrayList<HashMap<String, String>> foodItemArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM foodList WHERE foodItemEaten='" + eaten + "'" + " ORDER BY _id";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            do{

                HashMap<String, String> foodItemMap = new HashMap<String, String>();

                foodItemMap.put("_id", cursor.getString(0));
                foodItemMap.put("foodItemName", cursor.getString(1));
                foodItemMap.put("foodItemLocation", cursor.getString(2));
                foodItemMap.put("foodItemRating", cursor.getString(3));
                foodItemMap.put("foodItemReview", cursor.getString(4));
                foodItemMap.put("foodItemPicture", cursor.getString(5));
                foodItemMap.put("foodItemEaten", cursor.getString(6));
                foodItemMap.put("foodType", cursor.getString(7));

                foodItemArrayList.add(foodItemMap);

            } while(cursor.moveToNext());

        }

        return foodItemArrayList;

    }

    public void insertFoodItem(HashMap<String, String> queryValues){

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("foodItemName", queryValues.get("foodItemName"));
        values.put("foodItemLocation", queryValues.get("foodItemLocation"));
        values.put("foodItemRating", queryValues.get("foodItemRating"));
        values.put("foodItemReview", queryValues.get("foodItemReview"));
        values.put("foodItemPicture", queryValues.get("foodItemPicture"));
        values.put("foodItemEaten", queryValues.get("foodItemEaten"));
        values.put("foodType", queryValues.get("foodType"));

        long rowId = database.insert("foodList", null, values);

        Log.d("DBTools-InsertFoodItem", "Inserted foodType: " + queryValues.get("foodType"));
        Log.d("DBTools-InsertFoodItem", "Inserted foodItemName: " + queryValues.get("foodItemName"));
        Log.d("DBTools-InsertFoodItem", "Inserted foodItemEaten: " + queryValues.get("foodItemLocation"));
        Log.d("DBTools-InsertFoodItem", "Inserted foodItemImage: " + queryValues.get("foodItemPicture"));

        String selectQuery = "SELECT * FROM foodList where _id=" + Long.toString(rowId);

        Cursor cursor = database.rawQuery(selectQuery, null);
        Log.d("AfterInsert", "Cursor value: " + cursor.toString());

        database.close();

    }

    public int updateFoodItem(HashMap<String, String> queryValues){

        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("foodItemName", queryValues.get("foodItemName"));
        values.put("foodItemLocation", queryValues.get("foodItemLocation"));
        values.put("foodItemRating", queryValues.get("foodItemRating"));
        values.put("foodItemReview", queryValues.get("foodItemReview"));
        values.put("foodItemPicture", queryValues.get("foodItemPicture"));
        values.put("foodItemEaten", queryValues.get("foodItemEaten"));
        values.put("foodType", queryValues.get("foodType"));

        int rowsUpdated = database.update("foodList", values,
                "_id" + " = ?", new String[] {queryValues.get("_id") });

        Log.d("DBTools-updateFoodItem", "Updated foodItemName: " + queryValues.get("foodItemName"));
        Log.d("DBTools-updateFoodItem", "Updated foodItemEaten: " + queryValues.get("foodItemLocation"));
        Log.d("DBTools-updateFoodItem", "Updated foodItemImage: " + queryValues.get("foodItemPicture"));

        return rowsUpdated;

    }

    public void deleteFoodItem(String id){

        SQLiteDatabase database = this.getWritableDatabase();

        String deleteQuery = "DELETE FROM foodList WHERE _id='" + id + "'";

        database.execSQL(deleteQuery);

    }

    public ArrayList<HashMap<String, String>> getAllFriends(){

        ArrayList<HashMap<String, String>> friendsArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM friendList ORDER BY lastName";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            do{

                HashMap<String, String> friendMap = new HashMap<String, String>();

                friendMap.put("friendId", cursor.getString(0));
                friendMap.put("firstName", cursor.getString(1));
                friendMap.put("lastName", cursor.getString(2));
                friendMap.put("cityLocation", cursor.getString(3));

                friendsArrayList.add(friendMap);

            } while(cursor.moveToNext());

        }

        return friendsArrayList;

    }

    public ArrayList<HashMap<String, String>> getAllFoodItems(){

        ArrayList<HashMap<String, String>> foodItemArrayList = new ArrayList<HashMap<String, String>>();

        String selectQuery = "SELECT * FROM foodList ORDER BY _id";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            do{

                HashMap<String, String> foodItemMap = new HashMap<String, String>();

                foodItemMap.put("_id", cursor.getString(0));
                foodItemMap.put("foodItemName", cursor.getString(1));
                foodItemMap.put("foodItemLocation", cursor.getString(2));
                foodItemMap.put("foodItemRating", cursor.getString(3));
                foodItemMap.put("foodItemReview", cursor.getString(4));
                foodItemMap.put("foodItemPicture", cursor.getString(5));

                foodItemArrayList.add(foodItemMap);

            } while(cursor.moveToNext());

        }

        return foodItemArrayList;

    }

    public Cursor getCursorAllFoodItems(){

        String selectQuery = "SELECT * FROM foodList ORDER BY _id";

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            return cursor;

        } else {

            return null;

        }

    }

    public Cursor getCursorAllFilteredFoodItems(String constraint){

        String selectQuery = "SELECT * FROM foodList WHERE foodItemName LIKE '%" + constraint + "%'" + " ORDER BY _id";

        Log.i(TAG,"Running wildcard query...");
        Log.d(TAG,selectQuery);

        SQLiteDatabase database = this.getWritableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            return cursor;

        } else {

            return null;

        }

    }

    public HashMap<String, String> getFoodItemDetails(String id){

        HashMap<String, String> foodItemMap = new HashMap<String, String>();

        SQLiteDatabase database = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM foodList WHERE _id='" + id + "'";

        Cursor cursor = database.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){

            do{

                foodItemMap.put("_id", cursor.getString(0));
                foodItemMap.put("foodItemName", cursor.getString(1));
                foodItemMap.put("foodItemLocation", cursor.getString(2));
                foodItemMap.put("foodItemRating", cursor.getString(3));
                foodItemMap.put("foodItemReview", cursor.getString(4));
                foodItemMap.put("foodItemPicture", cursor.getString(5));
                foodItemMap.put("foodItemEaten", cursor.getString(6));
                foodItemMap.put("foodType", cursor.getString(7));

                Log.d("DBTools-getFoodItem", "Retrieved foodItemName: " + cursor.getString(2));
                Log.d("DBTools-getFoodItem", "Retrieved foodItemEaten: " + cursor.getString(6));
                Log.d("DBTools-getFoodItem", "Retrieved foodItemImage: " + cursor.getString(5));

            } while(cursor.moveToNext());

        }

        return foodItemMap;

    }

}