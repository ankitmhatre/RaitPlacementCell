package com.dragonide.raitplacementcell;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Ankit on 1/22/2017.
 */

public class CredentialsDb extends SQLiteOpenHelper {


    private static CredentialsDb sInstance;

    private SQLiteDatabase sqLiteDatabase;

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Sugges.db";

    public static final String _ID = "_id";
    public static final String LOGIN = "login";
    public static final String PASSWORD = "pass";

    public static final String LOGIN_TABLE = "LoginTable";


    public CredentialsDb(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public CredentialsDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public CredentialsDb(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }


    public static synchronized CredentialsDb getInstance(Context context) {
        if (sInstance == null)
            sInstance = new CredentialsDb(context.getApplicationContext());

        return sInstance;
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
        String[] logincols = { LOGIN, PASSWORD};

        String[] logincolstype = { "TEXT NOT NULL UNIQUE" , "TEXT"};

        String createEqualizerTable = buildCreateStatement(LOGIN_TABLE,
                logincols,
                logincolstype);

        db.execSQL(createEqualizerTable);
    }


    public Cursor getALlLogins(String filter) {
        String query = "SELECT * FROM " + LOGIN_TABLE + " WHERE "+ LOGIN + " LIKE '%"+filter+"%'"  ;
        return getDatabase().rawQuery(query, null);

    }

    public void addLoginValues( String log_add, String pass_add) {

        ContentValues values = new ContentValues();

        values.put(LOGIN, log_add);
        values.put(PASSWORD, pass_add);


        getDatabase().insert(LOGIN_TABLE, null, values);

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
        if (sqLiteDatabase==null)
            sqLiteDatabase = getWritableDatabase();

        return sqLiteDatabase;
    }

    private String buildCreateStatement(String tableName, String[] columnNames, String[] columnTypes) {
        String createStatement = "";
        if (columnNames.length==columnTypes.length) {
            createStatement += "CREATE TABLE IF NOT EXISTS " + tableName + "("
                    + _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ";

            for (int i=0; i < columnNames.length; i++) {

                if (i==columnNames.length-1) {
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


}
