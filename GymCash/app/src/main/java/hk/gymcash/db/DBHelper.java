package hk.gymcash.db;

/**
 * Created by mahender.yadav on 10/20/2016.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.UUID;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "musi.db";
    public static final String TABLE_NAME = "songs";
    public static final String COLUMN_ID = "id";

    public static final String SEARCH_KEYWORD = "searchkeyword";
    public static final String SONG_NAME = "songName";

    public static final String SONG_STATUS = "songstatus";
    public static final String FILE_LOC = "fileLoc";

    private HashMap hp;

    public DBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table songs " +
                        "(id integer primary key, songName text,searchkeyword text,fileLoc text, songstatus text, songId text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS songs");
        onCreate(db);
    }

    public SongInfo updateSong  (String songId)
    {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from songs where songId='"+songId+"'", null );
        res.moveToFirst();



            SongInfo songInfo = new SongInfo();
            songInfo.setKeyword(res.getString(res.getColumnIndex(SEARCH_KEYWORD)));

            songInfo.setSongName(res.getString(res.getColumnIndex(SONG_NAME)));

            songInfo.setStatus(DownloadStatus.valueOf(res.getString(res.getColumnIndex(SONG_STATUS))));

            songInfo.setPath(res.getString(res.getColumnIndex(FILE_LOC)));


        return songInfo;



    }

    public void updateSong(String songId, String fileName, DownloadStatus done, String saveFilePath) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "delete from songs where songId='"+songId+"'", null );

       SongInfo songInfo =  updateSong(songId);

        res.moveToFirst();

        insertSong(songInfo.getKeyword(), fileName, DownloadStatus.DONE, saveFilePath, UUID.randomUUID().toString());


    }

    public boolean insertSong  (String searchkeyword, String songName, DownloadStatus songstatus, String fileLoc, String songId)
    {
        SQLiteDatabase db = this.getWritableDatabase();


        ContentValues contentValues = new ContentValues();
        contentValues.put("searchkeyword", searchkeyword);
        contentValues.put("songName", songName);

        contentValues.put("songstatus", songstatus.name());
        contentValues.put("fileLoc", fileLoc);
        contentValues.put("songId", songId);

        db.insert("songs", null, contentValues);
        return true;
    }

    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from songs where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
        return numRows;
    }



    public Integer deleteSong (Integer id)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("songs",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public ArrayList<SongInfo> getAllSongs()
    {
        ArrayList<SongInfo> array_list = new ArrayList<SongInfo>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from songs", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){

            SongInfo songInfo = new SongInfo();
            songInfo.setKeyword(res.getString(res.getColumnIndex(SEARCH_KEYWORD)));

            songInfo.setSongName(res.getString(res.getColumnIndex(SONG_NAME)));

            songInfo.setStatus(DownloadStatus.valueOf(res.getString(res.getColumnIndex(SONG_STATUS))));

            songInfo.setPath(res.getString(res.getColumnIndex(FILE_LOC)));
            songInfo.setId(res.getInt(res.getColumnIndex(COLUMN_ID)));
            res.moveToNext();

            array_list.add(songInfo);
        }
        return array_list;
    }


    public void removeSong(Integer id) {

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "delete from songs where id="+id, null );



        res.moveToFirst();

    }
}
