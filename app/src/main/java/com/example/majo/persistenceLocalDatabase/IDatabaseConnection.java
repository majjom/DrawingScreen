package com.example.majo.persistenceLocalDatabase;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IDatabaseConnection {
    void destroy();
    SQLiteDatabase getDb();
}
