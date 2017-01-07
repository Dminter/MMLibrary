package com.zncm.library.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.zncm.library.data.Fields;
import com.zncm.library.data.Items;
import com.zncm.library.data.Lib;
import com.zncm.library.data.LocLib;
import com.zncm.library.data.Options;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DATABASE_NAME = "lib.db";
    public static final int DATABASE_VERSION = 13;
    private RuntimeExceptionDao<Lib, Integer> libDao = null;
    private RuntimeExceptionDao<Fields, Integer> fieldsDao = null;
    private RuntimeExceptionDao<Items, Integer> itemsDao = null;
    private RuntimeExceptionDao<Options, Integer> optionsDao = null;
    private RuntimeExceptionDao<LocLib, Integer> locDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTableIfNotExists(connectionSource, Lib.class);
            TableUtils.createTableIfNotExists(connectionSource, Fields.class);
            TableUtils.createTableIfNotExists(connectionSource, Items.class);
            TableUtils.createTableIfNotExists(connectionSource, Options.class);
            TableUtils.createTableIfNotExists(connectionSource, LocLib.class);
        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            if (oldVersion < 13) {
                TableUtils.dropTable(connectionSource, Lib.class, true);
                TableUtils.dropTable(connectionSource, Fields.class, true);
                TableUtils.dropTable(connectionSource, Items.class, true);
                TableUtils.dropTable(connectionSource, Options.class, true);
                TableUtils.dropTable(connectionSource, LocLib.class, true);
            }
            onCreate(db, connectionSource);
        } catch (Exception e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }
    }


    public RuntimeExceptionDao<LocLib, Integer> getLocLibDao() {
        if (locDao == null) {
            locDao = getRuntimeExceptionDao(LocLib.class);
        }
        return locDao;
    }

    public RuntimeExceptionDao<Lib, Integer> getLibDao() {
        if (libDao == null) {
            libDao = getRuntimeExceptionDao(Lib.class);
        }
        return libDao;
    }

    public RuntimeExceptionDao<Fields, Integer> getFieldsDao() {
        if (fieldsDao == null) {
            fieldsDao = getRuntimeExceptionDao(Fields.class);
        }
        return fieldsDao;
    }

    public RuntimeExceptionDao<Items, Integer> getItemsDao() {
        if (itemsDao == null) {
            itemsDao = getRuntimeExceptionDao(Items.class);
        }
        return itemsDao;
    }

    public RuntimeExceptionDao<Options, Integer> getOptionsDao() {
        if (optionsDao == null) {
            optionsDao = getRuntimeExceptionDao(Options.class);
        }
        return optionsDao;
    }


    @Override
    public void close() {
        super.close();
        libDao = null;
        fieldsDao = null;
        itemsDao = null;
        optionsDao = null;
        locDao = null;
    }
}
