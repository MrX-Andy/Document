```
package com.alex.andfun.service.huggles.dbhelper;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.alex.util.LogTrack;

/**
 * 作者：Alex
 * 时间：2017/12/21 18:20
 * 简述：
 */
@SuppressWarnings("WeakerAccess")
public class DBOpenHelper extends SQLiteOpenHelper {
    /**
     * 数据库名称
     */
    private static final String DB_NAME = "download_database.db";
    /**
     * 数据库版本
     */
    private static final int VERSION = 1;

    public DBOpenHelper(Context context) {
        this(context, DB_NAME, null, VERSION);
    }

    /**
     * 数据库的构造方法
     *
     * @param context 上下文
     * @param name    数据库名字 如 alexEnterprise.db
     * @param factory 游标工厂用于存放结果集，null表示使用默认游标
     * @param version 数据库版本号，初始值
     */
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    /**
     * 此方法只有在调用getWritableDatabase()或getReadableDatabase()方法时才会执行
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        LogTrack.e("执行创建语句");
        db.execSQL(SqlConstant.getCreateUserTable());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}

```