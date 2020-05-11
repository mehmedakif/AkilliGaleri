package com.uear.akilligaleri;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper
{
    // Table Name
    static final String RESIMLER_TABLOSU = "resimlerTablosu";
    // Table columns
    static final String IMGID = "resimID";
    static final String PATH = "resimAdres";
    static final String FACECOUNT = "yuzSayisi";
    static final String PROCESSED = "analiz";
    // Table Name
    static final String YUZLER_TABLOSU = "yuzlerTablosu";
    // Table columns
    static final String FACEID = "yuzID";
    static final String POSITIONX = "x";
    static final String POSITIONY = "y";
    static final String OWNER = "yuzSahibi";
    static final String ACC = "acc";
    private static String KISIID ;

    // Database Information
    private static final String DB_NAME = "AKILLI_GALERI.DB";
    // database version
    private static final int DB_VERSION = 1;
    // Resimler tablosunu olusturacak sorgu.

    private static final String CREATE_TABLE_IMG = "CREATE TABLE IF NOT EXISTS " + RESIMLER_TABLOSU + "(" + IMGID
            + " INTEGER PRIMARY KEY UNIQUE , " + PATH + " TEXT NOT NULL, " + FACECOUNT + " INTEGER,"+ PROCESSED +" INTEGER);";

    // Yuzler tablosunu olusturacak sorgu.
    private static final String CREATE_TABLE_FACE = "CREATE TABLE IF NOT EXISTS " + YUZLER_TABLOSU + "(" + FACEID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + IMGID + " INTEGER NOT NULL, " + POSITIONX + " TEXT,"+ POSITIONY + " TEXT," + OWNER + " TEXT," + ACC + " REAL, FOREIGN KEY ("+IMGID+") REFERENCES " +RESIMLER_TABLOSU+"("+IMGID+"));";


    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_TABLE_IMG);
        db.execSQL(CREATE_TABLE_FACE);
        Log.i("CREATE_TABLE_IMG","OLUSTU !");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + RESIMLER_TABLOSU);
        db.execSQL("DROP TABLE IF EXISTS " + YUZLER_TABLOSU);
        Log.i("TABLES"," DROPPED !");
        onCreate(db);
    }

    DatabaseHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
}
