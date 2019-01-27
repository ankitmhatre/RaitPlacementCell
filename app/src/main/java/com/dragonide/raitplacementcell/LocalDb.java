package com.dragonide.raitplacementcell;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.TIMESTAMP;


/**
 * Created by Ankit on 1/22/2017.
 */

public class LocalDb extends SQLiteOpenHelper {


    private static LocalDb sInstance;
    Context c;
    private SQLiteDatabase sqLiteDatabase;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Notif.db";

    public static final String _ID = "n_id";
    public static final String TITLE = "n_title";

    public static final String CONTENT = "n_content";
    public static final String READ = "n_read";

    public static final String NOTIF_TABLE = "NotifTable";


    public LocalDb(Context c) {

        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        this.c = c;
    }

    public LocalDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.c = context;
    }

    public LocalDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
        this.c = context;
    }


    public static synchronized LocalDb getInstance(Context context) {

        if (sInstance == null)
            sInstance = new LocalDb(context.getApplicationContext());

        return sInstance;
    }

    public Cursor getAllEntries() {
        String query = "SELECT * FROM " + NOTIF_TABLE ;
        return getDatabase().rawQuery(query, null);

    }
    public void deleteALl(){
        String que = "delete from " + NOTIF_TABLE;
        getWritableDatabase().execSQL(que);
        Log.d("drtbase1", getCount() + ""  );
    }

    public int getCount() {


        return getAllEntries().getCount();

    }

    @Override
    public String getDatabaseName() {
        return super.getDatabaseName();
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public synchronized void close() {
        super.close();
    }

    @Override
    public SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String[] logincols = {TITLE, CONTENT, READ};

        String[] logincolstype = {"TEXT NOT NULL ", "TEXT NOT NULL UNIQUE ", "INTEGER DEFAULT 0 "};

        String createEqualizerTable = buildCreateStatement(NOTIF_TABLE,
                logincols,
                logincolstype);

        db.execSQL(createEqualizerTable);
    }


    /*  public Cursor getALlLogins(String filter) {
          String query = "SELECT * FROM " + LOGIN_TABLE + " WHERE "+ LOGIN + " LIKE '%"+filter+"%'"  ;
          return getDatabase().rawQuery(query, null);

      }*/
    public String getTopMostItemCOntent() {
        Cursor cursor = getAllEntries();
        String content = null;
        if (cursor.moveToFirst()) {

            content = cursor.getString(cursor.getColumnIndex(CONTENT));

        }
        cursor.close();
        return content;

    }

    public String getTopMostItemTitle() {
        Cursor cursor = getAllEntries();
        String content = null;
        if (cursor.moveToFirst()) {

            content = cursor.getString(cursor.getColumnIndex(TITLE));

        }
        cursor.close();
        return content;

    }

    public void addNotifications(String title, String content
                                 //, String read
    ) {

        ContentValues values = new ContentValues();

        values.put(TITLE, title);
        values.put(CONTENT, content);
        values.put(READ, 0);


        getDatabase().insert(NOTIF_TABLE, "", values);
        Log.d("drtbase", title + " " + content + " ");
    }

    @Override
    protected void finalize() {
        try {
            getDatabase().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private synchronized SQLiteDatabase getDatabase() {
        if (sqLiteDatabase == null)
            sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase;
    }

    private String buildCreateStatement(String tableName, String[] columnNames, String[] columnTypes) {
        String createStatement = "";
        if (columnNames.length == columnTypes.length) {
            createStatement += "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";

            for (int i = 0; i < columnNames.length; i++) {

                if (i == columnNames.length - 1) {
                    createStatement += columnNames[i]
                            + " "
                            + columnTypes[i]
                            + ")";
                } else {
                    createStatement += columnNames[i]
                            + " "
                            + columnTypes[i]
                            + ", ";
                }

            }

        }

        return createStatement;
    }


    public List<NotifItem> search(String query) {
        return search(getInstance(c).getDatabase(), query);
    }

    public Cursor searchcursor(String query) {
        return searchcursor(getInstance(c).getDatabase(), query);

    }


    public Cursor searchcursor(SQLiteDatabase database, String searchQuery) {

        Object[] keywords = searchQuery.split(" ");
        String query = "";
        for (int i = 0; i < keywords.length; ++i) {
            String keyword = String.valueOf(i + 1);
            query = query + "(" + TITLE + " LIKE %" + keyword +
                    "$s OR " + CONTENT + " LIKE %" + keyword +
                    "$s) AND ";
            keywords[i] = "'%" + ((String) keywords[i]).replaceAll("'", "''") + "%'";
        }

        try {
            query = query.substring(0, query.length() - 5);
        } catch (Exception e) {
            e.printStackTrace();
            query= "";
        }


        return database.query(NOTIF_TABLE, null, String.format(query,
                keywords), null, null, null, null);
    }

    public List<NotifItem> search(SQLiteDatabase database, String searchQuery) {
        List<NotifItem> results;
        Object[] keywords = searchQuery.split(" ");
        String query = "";
        for (int i = 0; i < keywords.length; ++i) {
            String keyword = String.valueOf(i + 1);
            query = query + "(" + TITLE + " LIKE %" + keyword +
                    "$s OR " + CONTENT + " LIKE %" + keyword +
                    "$s) AND ";
            keywords[i] = "'%" + ((String) keywords[i]).replaceAll("'", "''") + "%'";
        }

        query = query.substring(0, query.length() - 5);

        Cursor cursor = database.query(NOTIF_TABLE, null, String.format(query,
                keywords), null, null, null, null);
        results = new ArrayList<>(cursor.getCount());
        while (cursor.moveToNext()) {
            NotifItem l = new NotifItem();
            l.setN_title(cursor.getString(cursor.getColumnIndex(TITLE)));
            l.setN_content(cursor.getString(cursor.getColumnIndex(CONTENT)));
            l.setN_read(cursor.getString(cursor.getColumnIndex(READ)));


            results.add(l);
        }
        cursor.close();
        return results;
    }


}
