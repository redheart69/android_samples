package com.novoda.imageloader.demo.provider;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.novoda.imageloader.demo.activity.BigImages;
import com.novoda.imageloader.demo.activity.ImageLongList;
import com.novoda.imageloader.demo.activity.LongSmallImageList;
import com.novoda.imageloader.demo.activity.base.ImageLoaderBaseActivity;

public class DatabaseManager extends SQLiteOpenHelper {

    private Context context;

    public DatabaseManager(Context context) {
        super(context, "com.novoda.imageloader.demo", null, 20);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        create(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        drop(db);
        onCreate(db);
    }

    private void create(SQLiteDatabase db) {
        List<String> stms = new ArrayList<String>();
        addCreateStm(stms, ImageLongList.class);
        addCreateStm(stms, BigImages.class);
        addCreateStm(stms, LongSmallImageList.class);
        AssetManager mngr = context.getAssets();
        try {
            stms.addAll(SqlFile.statementsFrom(new InputStreamReader(mngr.open("contents/images.sql"))));
        } catch (Exception e) {
            Log.e("Exception", "Error while Inserting editions", e);
        }
        exec(db, stms);
    }

    private void addCreateStm(List<String> stms, Class<? extends ImageLoaderBaseActivity> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        stms.add("create table if not exists " + name + "(_id integer primary key autoincrement, " + "url text);");
    }

    private void addDropStm(List<String> stms, Class<? extends ImageLoaderBaseActivity> clazz) {
        String name = clazz.getSimpleName().toLowerCase();
        stms.add("drop table if exists " + name + ";");
    }

    private void drop(SQLiteDatabase db) {
        List<String> stms = new ArrayList<String>();
        addDropStm(stms, ImageLongList.class);
        addDropStm(stms, BigImages.class);
        addDropStm(stms, LongSmallImageList.class);
        exec(db, stms);
    }

    private static void exec(SQLiteDatabase db, List<String> staments) {
        for (String stm : staments) {
            Log.v("exec", stm);
            try {
                db.execSQL(stm);
            } catch (RuntimeException re) {
                Log.e("exec", "RuntimeException", re);
            }
        }
    }

}
