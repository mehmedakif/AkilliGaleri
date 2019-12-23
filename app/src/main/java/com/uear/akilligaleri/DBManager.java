package com.uear.akilligaleri;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.uear.akilligaleri.DatabaseHelper.RESIMLER_TABLOSU;


public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private static SQLiteDatabase database;

    DBManager(Context c) {
        context = c;
    }


    DBManager open() throws SQLException {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        Log.i("DB","DATABASE ACILDI");
        dbHelper.onCreate(database);
        return this;
    }

    void close() {
        dbHelper.close();
    }

    public static void insertImg(int imgId, String imgPath,int faceCount,int proccessed)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.IMGID, imgId);
        contentValue.put(DatabaseHelper.PATH, imgPath);
        contentValue.put(DatabaseHelper.FACECOUNT, String.valueOf(faceCount));
        contentValue.put(DatabaseHelper.PROCCESSED, proccessed);
        database.insertWithOnConflict(RESIMLER_TABLOSU, null, contentValue,SQLiteDatabase.CONFLICT_REPLACE);
    }
    public static void insertFace(String faceId,String X,String Y, String faceOwner,String imgId)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.FACEID, faceId);
        contentValue.put(DatabaseHelper.POSITIONX, X);
        contentValue.put(DatabaseHelper.POSITIONY, Y);
        contentValue.put(DatabaseHelper.OWNER, faceOwner);
        contentValue.put(DatabaseHelper.IMGID, imgId);
        database.insertWithOnConflict(DatabaseHelper.YUZLER_TABLOSU, null, contentValue,SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static Cursor fetchImg(int img_id)
    {
        String[] columns = new String[] {DatabaseHelper.PROCCESSED};
        Cursor cursor = database.query(RESIMLER_TABLOSU, columns, "resimID = " + img_id, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }
    public Cursor fetchFace()
    {
        String[] columns = new String[] { DatabaseHelper.FACEID, DatabaseHelper.POSITIONX,DatabaseHelper.POSITIONY, DatabaseHelper.OWNER,DatabaseHelper.IMGID };
        Cursor cursor = database.query(DatabaseHelper.YUZLER_TABLOSU, columns, null, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        return cursor;
    }

    public static void updateImg(int imgId, int analiz)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PROCCESSED, analiz);
        int i = database.update(RESIMLER_TABLOSU, contentValues, " resimID = " + imgId, null);
    }
    public int updateFace(long _id, String name, String desc)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PATH, name);
        contentValues.put(DatabaseHelper.FACECOUNT, desc);
        int i = database.update(RESIMLER_TABLOSU, contentValues, DatabaseHelper.IMGID + " = " + _id, null);
        return i;
    }

    public void deleteImg(long _id)
    {
        database.delete(RESIMLER_TABLOSU, DatabaseHelper.IMGID + "=" + _id, null);
    }
    public void deleteFace(long _id)
    {
        database.delete(DatabaseHelper.YUZLER_TABLOSU, DatabaseHelper.IMGID + "=" + _id, null);
    }


}