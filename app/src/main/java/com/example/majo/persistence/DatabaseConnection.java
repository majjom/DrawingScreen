package com.example.majo.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by majo on 14-Dec-14.
 */
public class DatabaseConnection implements IDatabaseConnection {

    MyDatabaseHelper dbCreator;
    SQLiteDatabase db;

    public DatabaseConnection(Context context){
        this.dbCreator = new MyDatabaseHelper(context);
        this.db = dbCreator.getWritableDatabase();
    }

    @Override
    public void onDestroy() {
        if (this.db != null){
            this.db.close();
        }
    }

    @Override
    public SQLiteDatabase getDb(){
        return this.db;
    }
}
