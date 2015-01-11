package com.example.majo.persistence;

import android.database.sqlite.SQLiteDatabase;

/**
 * Created by majo on 14-Dec-14.
 */
public interface IDatabaseConnection {
    void onDestroy();
    SQLiteDatabase getDb();
}
