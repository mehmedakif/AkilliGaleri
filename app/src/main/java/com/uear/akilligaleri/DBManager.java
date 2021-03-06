package com.uear.akilligaleri;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import static com.uear.akilligaleri.DatabaseHelper.RESIMLER_TABLOSU;
import static com.uear.akilligaleri.DatabaseHelper.YUZLER_TABLOSU;


public class DBManager {

    private DatabaseHelper dbHelper;
    private Context context;
    private static SQLiteDatabase database;

    DBManager(Context c) {
        context = c;
    }

    void open() throws SQLException
    {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
        Log.i("DB","DATABASE ACILDI");
        dbHelper.onCreate(database);
    }

    void close()
    {
        dbHelper.close();
    }

    public static void insertImg(int imgId, String imgPath, int faceCount, int processed)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.IMGID, imgId);
        contentValue.put(DatabaseHelper.PATH, imgPath);
        contentValue.put(DatabaseHelper.FACECOUNT, String.valueOf(faceCount));
        contentValue.put(DatabaseHelper.PROCESSED, processed);
        database.insertWithOnConflict(RESIMLER_TABLOSU, null, contentValue,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public static void insertFace(String faceId, String X, String Y, String faceOwner, double classAccur, String imgId)
    {
        ContentValues contentValue = new ContentValues();
        contentValue.put(DatabaseHelper.FACEID, faceId);
        contentValue.put(DatabaseHelper.POSITIONX, X);
        contentValue.put(DatabaseHelper.POSITIONY, Y);
        contentValue.put(DatabaseHelper.OWNER, faceOwner);
        contentValue.put(DatabaseHelper.IMGID, imgId);
        contentValue.put(DatabaseHelper.ACC, classAccur);
        database.insertWithOnConflict(DatabaseHelper.YUZLER_TABLOSU, null, contentValue,SQLiteDatabase.CONFLICT_IGNORE);
    }

    public static Cursor fetchCountPerson()
    {
        return database.rawQuery("SELECT COUNT(DISTINCT yuzSahibi) FROM " + YUZLER_TABLOSU + ";",null);
    }

    public static Cursor fetchPerson(String alinacak_kisi)
    {
        return database.rawQuery("SELECT resimAdres FROM " + RESIMLER_TABLOSU + " WHERE resimID = (SELECT DISTINCT resimID FROM yuzlerTablosu WHERE yuzSahibi = " + alinacak_kisi + ");",null);
    }

    //Analiz edilmemis tum fotograflarin resimAdreslerini(path) getirir.
    public static Cursor fetchNonProcessed()
    {
        return database.rawQuery("SELECT resimAdres FROM " + RESIMLER_TABLOSU + " WHERE analiz = 0 ",null);
    }

    public static Cursor fetchImgIdToUpload(String picPath)
    {
        return database.rawQuery("SELECT resimID FROM " + RESIMLER_TABLOSU + " WHERE resimAdres = '" + picPath +"';",null);
    }
    public static Cursor fetchDistinctFaces()
    {
        return database.rawQuery("SELECT DISTINCT yuzSahibi FROM " + YUZLER_TABLOSU + ";",null);
    }
    //Analize gonderilen fotografin processed alani guncellenir.
    @SuppressLint("Recycle")
    public static void updateProccesStatus(String path) {
        database.rawQuery("UPDATE " + RESIMLER_TABLOSU + " SET analiz = 1 WHERE resimAdres = '" + path +"';",null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PROCESSED, 1);
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
        contentValues.put(DatabaseHelper.PROCESSED, analiz);
        int i = database.update(RESIMLER_TABLOSU, contentValues, " resimID = " + imgId, null);
    }

    public int updateFace(long _id, String name, String desc)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.PATH, name);
        contentValues.put(DatabaseHelper.FACECOUNT, desc);
        return database.update(RESIMLER_TABLOSU, contentValues, DatabaseHelper.IMGID + " = " + _id, null);
    }
    public static void updateClass(String owner, String path)
    {
        database.rawQuery("UPDATE " + YUZLER_TABLOSU + " SET yuzSahibi = '" + owner + "' WHERE resimID = (SELECT resimID FROM resimlerTablosu WHERE resimAdres = '" + path + "');",null);
    }

    public static void deleteImg(String path)
    {
        database.delete(RESIMLER_TABLOSU, DatabaseHelper.PATH + "=" + "'"+path+"'", null);
    }
    public void deleteFace(long _id)
    {
        database.delete(DatabaseHelper.YUZLER_TABLOSU, DatabaseHelper.IMGID + "=" + _id, null);
    }


}