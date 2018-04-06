package kushagra.com.paginationwithretrofiturlparsing.localDatabase;

/**
 * Created by Kushagra Saxena on 26/03/2018.
 */


        import android.content.ContentValues;
        import android.content.Context;
        import android.content.SharedPreferences;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;
        import android.util.Log;

        import com.google.gson.Gson;
        import com.google.gson.GsonBuilder;

        import java.text.SimpleDateFormat;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.List;
        import java.util.Locale;

        import kushagra.com.paginationwithretrofiturlparsing.dataModel.OlaData;

        import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.ITEM_PER_PAGE;
        import static kushagra.com.paginationwithretrofiturlparsing.Utils.Constants.printOlaData;

/**
 * Created by Kushagra Saxena on 11/10/2017.
 */


public class DatabaseControllerAllQuery extends SQLiteOpenHelper {

    Context context = null;
    private Boolean firstTime = null;

    public DatabaseControllerAllQuery(Context applicationcontext) {
        super(applicationcontext, "olaSongs.db", null, 1);

        context = applicationcontext;


    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase database) {

        String query;

        //home
        query = "CREATE TABLE IF NOT EXISTS Item( id INTEGER PRIMARY KEY, songName TEXT, artist TEXT , coverImageUrl TEXT , songUrl TEXT , playFrequency INTEGER DEFAULT 0,favorite INTEGER DEFAULT 0)";
        database.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old, int current_version) {
        String query;
        query = "DROP TABLE IF EXISTS Item";
        database.execSQL(query);
        onCreate(database);
    }


    /**
     * Inserts SONG into SQLite DB
     *
     * @param olaData
     */
    public void insertSong(OlaData olaData, String tableName) {

        if(tableName.compareTo("Item")==0) {

            SQLiteDatabase database = this.getWritableDatabase();

            Cursor cursor = null;
            String sql ="SELECT id FROM "+tableName+" where songName = '" + olaData.getSong() + "'";
            cursor= database.rawQuery(sql,null);
            Log.i("Count of insert tuple","song name"+olaData.getSong()+"Cursor Count : " + cursor.getCount());

            if(cursor.getCount()<=0)
            {

                //Song Not Found

                ContentValues values = new ContentValues();
                values.put("songName", olaData.getSong());
                values.put("artist", olaData.getArtists());
                values.put("coverImageUrl", olaData.getCoverImage());
                values.put("songUrl", olaData.getUrl());


                database.insert(tableName, null, values);

                Log.i("inserted Song=", values.getAsString("songName")+" "+ values.getAsString("artist") );


            }
            cursor.close();

            database.close();

        }


    }

    public boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = context.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            //int second=mPreferences.edit();
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }




    //get  favourite status
    public boolean getFavStatus(OlaData olaData,String tableName) {

        String fav = "";
        String selectQuery = "SELECT  * FROM " + tableName + " where id = '" + olaData.getId() + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Log.i("mobileId=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));

                fav = cursor.getString(cursor.getColumnIndex("favorite"));


            } while (cursor.moveToNext());
        }
        cursor.close();

        database.close();
        if(Integer.parseInt(fav.trim())==1)
        {
            return true;
        }
        else
        {
            return false;
        }



    }
    //get  id corresponding to songName
    public String getId(String songName, String tableName) {

        String mobileId = "";
        String selectQuery = "SELECT  * FROM " + tableName + " where songName = '" + songName + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Log.i("mobileId=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));

                mobileId = cursor.getString(cursor.getColumnIndex("id"));


            } while (cursor.moveToNext());
        }
        cursor.close();

        database.close();


        return mobileId.trim();
    }






    /**
     * Set favourite song into SQLite DB
     *
     * @param olaData
     */
    public void setSongsFavoriteStatus(OlaData olaData) {


        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update Item set favorite = '" + "1"
               + "' where id=" + "'" + olaData.getId() + "'";

        Log.i("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
    /**
     * Inserts User into SQLite DB
     *
     * @param olaData
     */
    public void incPlayFrequency(OlaData olaData) {
        String currentPlayFrequency = "";
        String selectQuery = "SELECT  * FROM " + "Item" + " where id = '" + olaData.getId() + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                // Log.i("mobileId=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));

                currentPlayFrequency = cursor.getString(cursor.getColumnIndex("playFrequency"));


            } while (cursor.moveToNext());
        }

        database.close();


         database = this.getWritableDatabase();
         int newFrequency =Integer.parseInt(currentPlayFrequency)+1;
        String updateQuery = "Update Item set playFrequency = '" + newFrequency
                + "' where id=" + "'" + olaData.getId() + "'";

        Log.i("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }
    /**
     * Inserts User into SQLite DB
     *
     * @param olaData
     */
    public void removeSongsFavoriteStatus(OlaData olaData) {


        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "Update Item set favorite = '" + "0"
                + "' where id=" + "'" + olaData.getId() + "'";

        Log.i("query", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }



    /**
     * Get list of Recent Songs from SQLite DB as  List
     *
     * @return List<OlaData>
     */
    public List<OlaData>  getRecentSongsOfPage(String tableName,int page) {
        Log.i("getRecentSongsOfPage =", page+" " );

        int start = (page-1)*ITEM_PER_PAGE;
        int itemToDisplay =ITEM_PER_PAGE;
        int totalTuple=numberOfRecentTuple(tableName);
        if((start+itemToDisplay)>totalTuple)
        {
            itemToDisplay=totalTuple-start;
        }

        Log.i("start index  =", start+" " );
        Log.i("itemToDisplay index  =", itemToDisplay+" " );

        List<OlaData> resultSongList  = new ArrayList<>();;

        String selectQuery = "SELECT  * FROM " + tableName+" where playFrequency > '" + "0" + "'"+" ORDER BY playFrequency DESC" +"  LIMIT " + itemToDisplay + " OFFSET " + start;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                OlaData olaData = new OlaData();
                olaData.setId(Integer.parseInt( cursor.getString(0)));
                olaData.setSong(( cursor.getString(1)));
                olaData.setArtists(( cursor.getString(2)));
                olaData.setCoverImage( cursor.getString(3));
                olaData.setUrl(( cursor.getString(4)));
                olaData.setPlayedCount(Integer.parseInt( cursor.getString(5)));
                if(Integer.parseInt( cursor.getString(6))==1)
                {
                    olaData.setFavourite(true);
                }
                else
                {
                    olaData.setFavourite(false);
                }

                //log to see
                printOlaData(olaData);





                // Log.i("search=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));
                resultSongList.add(olaData);
            } while (cursor.moveToNext());
        }
        cursor.close();


        database.close();
        return resultSongList;
    }
    /**
     * Get list of Songs from SQLite DB as  List
     *
     * @return List<OlaData>
     */
    public List<OlaData>  getFavoriteSongsOfPage(String tableName,int page) {
        Log.i("getFavoriteSongsOfPage ", page+" " );

        int start = (page-1)*ITEM_PER_PAGE;
        int itemToDisplay =ITEM_PER_PAGE;
        int totalTuple=numberOfFavoriteTuple(tableName);
        if((start+itemToDisplay)>totalTuple)
        {
            itemToDisplay=totalTuple-start;
        }

        Log.i("start index  =", start+" " );
        Log.i("itemToDisplay index  =", itemToDisplay+" " );

        List<OlaData> resultSongList  = new ArrayList<>();;

        String selectQuery = "SELECT  * FROM " + tableName+ " where favorite = '" + "1" + "'"+"  LIMIT " + itemToDisplay + " OFFSET " + start;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                OlaData olaData = new OlaData();
                olaData.setId(Integer.parseInt( cursor.getString(0)));
                olaData.setSong(( cursor.getString(1)));
                olaData.setArtists(( cursor.getString(2)));
                olaData.setCoverImage( cursor.getString(3));
                olaData.setUrl(( cursor.getString(4)));
                olaData.setPlayedCount(Integer.parseInt( cursor.getString(5)));
                if(Integer.parseInt( cursor.getString(6))==1)
                {
                    olaData.setFavourite(true);
                }
                else
                {
                    olaData.setFavourite(false);
                }

                //log to see
                printOlaData(olaData);





                // Log.i("search=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));
                resultSongList.add(olaData);
            } while (cursor.moveToNext());
        }
        cursor.close();


        database.close();
        return resultSongList;
    }
    /**
     * Get list of Songs from SQLite DB as  List
     *
     * @return List<OlaData>
     */
    public List<OlaData>  getSongOfPage(String tableName,int page) {
        Log.i("getSongOfPage =", page+" " );

        int start = (page-1)*ITEM_PER_PAGE;
        int itemToDisplay =ITEM_PER_PAGE;
        int totalTuple=numberOfTuple(tableName);
        if((start+itemToDisplay)>totalTuple)
        {
            itemToDisplay=totalTuple-start;
        }

        Log.i("start index  =", start+" " );
        Log.i("itemToDisplay index  =", itemToDisplay+" " );

        List<OlaData> resultSongList  = new ArrayList<>();;

        String selectQuery = "SELECT  * FROM " + tableName+ "  LIMIT " + itemToDisplay + " OFFSET " + start;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                OlaData olaData = new OlaData();
                olaData.setId(Integer.parseInt( cursor.getString(0)));
                olaData.setSong(( cursor.getString(1)));
                olaData.setArtists(( cursor.getString(2)));
                olaData.setCoverImage( cursor.getString(3));
                olaData.setUrl(( cursor.getString(4)));
                olaData.setPlayedCount(Integer.parseInt( cursor.getString(5)));
                if(Integer.parseInt( cursor.getString(6))==1)
                {
                    olaData.setFavourite(true);
                }
                else
                {
                    olaData.setFavourite(false);
                }

                //log to see
                printOlaData(olaData);





                // Log.i("search=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));
                resultSongList.add(olaData);
            } while (cursor.moveToNext());
        }
        cursor.close();


        database.close();
        return resultSongList;
    }
    /**
     * Get list of searched Songs from SQLite DB as  List
     *
     * @return List<OlaData>
     */
    public List<OlaData>  getSearchedSongByPage(String tableName,int page,String searchText) {
        Log.i("getSearchedSongByPage =", page+" " );

        int start = (page-1)*ITEM_PER_PAGE;
        int itemToDisplay =ITEM_PER_PAGE;
        int totalTuple=numberOfTupleInSearchResult(tableName,searchText);
        if((start+itemToDisplay)>totalTuple)
        {
            itemToDisplay=totalTuple-start;
        }

        Log.i("start index  =", start+" " );
        Log.i("itemToDisplay index  =", itemToDisplay+" " );

        List<OlaData> resultSongList  = new ArrayList<>();
        if(itemToDisplay<=0)
        {
            return  resultSongList;
        }

        String selectQuery = "SELECT  * FROM " + tableName+ "  WHERE songName LIKE '%" +searchText + "%' "+"  LIMIT " + itemToDisplay + " OFFSET " + start; ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                OlaData olaData = new OlaData();
                olaData.setId(Integer.parseInt( cursor.getString(0)));
                olaData.setSong(( cursor.getString(1)));
                olaData.setArtists(( cursor.getString(2)));
                olaData.setCoverImage( cursor.getString(3));
                olaData.setUrl(( cursor.getString(4)));
                olaData.setPlayedCount(Integer.parseInt( cursor.getString(5)));
                if(Integer.parseInt( cursor.getString(6))==1)
                {
                    olaData.setFavourite(true);
                }
                else
                {
                    olaData.setFavourite(false);
                }

                //log to see
                printOlaData(olaData);





                // Log.i("search=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));
                resultSongList.add(olaData);
            } while (cursor.moveToNext());
        }

cursor.close();
        database.close();
        return resultSongList;
    }
    /**
     * Get list of searched Favourite Songs from SQLite DB as  List
     *
     * @return List<OlaData>
     */
    public List<OlaData>  getSearchedFavouriteSongByPage(String tableName,int page,String searchText) {
        Log.i("getSearchedSongByPage =", page+" " );

        int start = (page-1)*ITEM_PER_PAGE;
        int itemToDisplay =ITEM_PER_PAGE;
        int totalTuple=numberOfTupleInFavouriteSearchResult(tableName,searchText);
        if((start+itemToDisplay)>totalTuple)
        {
            itemToDisplay=totalTuple-start;
        }

        Log.i("start index  =", start+" " );
        Log.i("itemToDisplay index  =", itemToDisplay+" " );

        List<OlaData> resultSongList  = new ArrayList<>();
        if(itemToDisplay<=0)
        {
            return  resultSongList;
        }

        String selectQuery = "SELECT  * FROM " + tableName+ "  WHERE songName LIKE '%" +searchText + "%' AND favorite  = '" + "1" + "'"+"  LIMIT " + itemToDisplay + " OFFSET " + start; ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                OlaData olaData = new OlaData();
                olaData.setId(Integer.parseInt( cursor.getString(0)));
                olaData.setSong(( cursor.getString(1)));
                olaData.setArtists(( cursor.getString(2)));
                olaData.setCoverImage( cursor.getString(3));
                olaData.setUrl(( cursor.getString(4)));
                olaData.setPlayedCount(Integer.parseInt( cursor.getString(5)));
                if(Integer.parseInt( cursor.getString(6))==1)
                {
                    olaData.setFavourite(true);
                }
                else
                {
                    olaData.setFavourite(false);
                }

                //log to see
                printOlaData(olaData);





                // Log.i("search=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));
                resultSongList.add(olaData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return resultSongList;
    }
    /**
     * Get list of searched Recent Songs from SQLite DB as  List
     *
     * @return List<OlaData>
     */
    public List<OlaData>  getSearchedRecentSongByPage(String tableName,int page,String searchText) {
        Log.i("getSearchedSongByPage =", page+" " );

        int start = (page-1)*ITEM_PER_PAGE;
        int itemToDisplay =ITEM_PER_PAGE;
        int totalTuple=numberOfTupleInRecentSearchResult(tableName,searchText);
        if((start+itemToDisplay)>totalTuple)
        {
            itemToDisplay=totalTuple-start;
        }

        Log.i("start index  =", start+" " );
        Log.i("itemToDisplay index  =", itemToDisplay+" " );

        List<OlaData> resultSongList  = new ArrayList<>();
        if(itemToDisplay<=0)
        {
            return  resultSongList;
        }

        String selectQuery = "SELECT  * FROM " + tableName+ "  WHERE songName LIKE '%" +searchText + "%' AND "+" playFrequency > '" + "0" + "'" +" ORDER BY playFrequency DESC"+"  LIMIT " + itemToDisplay + " OFFSET " + start ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        cursor.getCount();
        if (cursor.moveToFirst()) {
            do {
                OlaData olaData = new OlaData();
                olaData.setId(Integer.parseInt( cursor.getString(0)));
                olaData.setSong(( cursor.getString(1)));
                olaData.setArtists(( cursor.getString(2)));
                olaData.setCoverImage( cursor.getString(3));
                olaData.setUrl(( cursor.getString(4)));
                olaData.setPlayedCount(Integer.parseInt( cursor.getString(5)));
                if(Integer.parseInt( cursor.getString(6))==1)
                {
                    olaData.setFavourite(true);
                }
                else
                {
                    olaData.setFavourite(false);
                }

                //log to see
                printOlaData(olaData);





                // Log.i("search=", cursor.getString(0)+" "+ cursor.getString(1)+" "+ cursor.getString(2)+" "+ cursor.getString(3));
                resultSongList.add(olaData);
            } while (cursor.moveToNext());
        }

        cursor.close();
        database.close();
        return resultSongList;
    }

    /**
     * Get SQLite records table column values
     *
     * @return
     */
    public String[] columnArray(String TABLE_NAME) {


        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();

        database.close();
        return columnNames;
    }
    /**
     * Get SQLite number of records
     *
     * @return
     */
    public int numberOfTuple(String TABLE_NAME) {


        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        int totalRecords = cursor.getCount();
        cursor.close();
        database.close();
        return totalRecords;
    }
    /**
     * Get SQLite number of recent records
     *
     * @return
     */
    public int numberOfRecentTuple(String TABLE_NAME) {


        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where playFrequency > '" + "0" + "'" +" ORDER BY playFrequency DESC";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int totalRecords=cursor.getCount();
        cursor.close();
        database.close();
        return totalRecords;
    }
    /**
     * Get SQLite number of favorite records
     *
     * @return
     */
    public int numberOfFavoriteTuple(String TABLE_NAME) {


        String selectQuery = "SELECT  * FROM " + TABLE_NAME+" where favorite = '" + "1" + "'" ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int totalRecords=cursor.getCount();
        cursor.close();
        database.close();
        return totalRecords;
    }
    /**
     * Get SQLite number of records in Recent search
     *
     * @return
     */
    public int numberOfTupleInRecentSearchResult(String TABLE_NAME,String searchText) {

        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ "  WHERE songName LIKE '%" +searchText + "%' AND "+" playFrequency > '" + "0" + "'" +" ORDER BY playFrequency DESC" ;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int totalRecords=cursor.getCount();
        cursor.close();
        database.close();
        return totalRecords;
    }
    /**
     * Get SQLite number of records in search
     *
     * @return
     */
    public int numberOfTupleInSearchResult(String TABLE_NAME,String searchText) {


        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ "  WHERE songName LIKE '%" +searchText + "%' " ;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int totalRecords=cursor.getCount();
        cursor.close();
        database.close();
        return totalRecords;
    }
    /**
     * Get SQLite number of records in search
     *
     * @return
     */
    public int numberOfTupleInFavouriteSearchResult(String TABLE_NAME,String searchText) {

        String selectQuery = "SELECT  * FROM " + TABLE_NAME+ "  WHERE songName LIKE '%" +searchText + "%' AND favorite  = '" + "1" + "'" ;

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        int totalRecords=cursor.getCount();
        cursor.close();
        database.close();
        return totalRecords;
    }
    //give timestamp
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }




}