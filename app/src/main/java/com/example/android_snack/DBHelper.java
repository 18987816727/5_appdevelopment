package com.example.android_snack;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "locationDatabase.db";
    private static final int DATABASE_VERSION = 1;

    // 表名
    private static final String TABLE_LOCATIONS = "time_position";

    // 列名
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_GANZHI = "gan_zhi";
    private static final String COLUMN_ISPUSH = "is_push";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建表的 SQL 语句
        String CREATE_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_TIMESTAMP + " TEXT NOT NULL,"
                + COLUMN_LATITUDE + " REAL NOT NULL,"
                + COLUMN_LONGITUDE + " REAL NOT NULL,"
                + COLUMN_GANZHI + " TEXT,"
                + COLUMN_ISPUSH +" INTEGER);";
//        这里语言要用空格和双引号隔开一下，原理是拼接成sql，连在一起会有问题!!

        // 执行创建表的语句
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 如果数据库版本更新，则删除旧表并重新创建
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
        onCreate(db);
    }

    // 插入数据
    public long insertLocation(String timestamp, double latitude, double longitude, String ganZhi,int is_push) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(COLUMN_TIMESTAMP,timestamp);
        contentValues.put(COLUMN_LATITUDE,latitude);
        contentValues.put(COLUMN_LONGITUDE,longitude);
        contentValues.put(COLUMN_GANZHI,ganZhi);
        contentValues.put(COLUMN_ISPUSH,is_push);
        long i=db.insert(TABLE_LOCATIONS,null,contentValues);
        db.close();
        return i;
    }

    // 删除数据
    public int deleteLocationById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i=db.delete(TABLE_LOCATIONS, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
        return i;
    }

    // 查询所有数据
    public List<TimePositionDataEntity> queryAllLocations() {
        List<TimePositionDataEntity> list=new ArrayList<TimePositionDataEntity>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_TIMESTAMP, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_GANZHI,COLUMN_ISPUSH};
        Cursor cursor = db.query(TABLE_LOCATIONS, columns, null, null, null, null, COLUMN_TIMESTAMP+ " DESC");
        while (cursor.moveToNext()){
            list.add(new TimePositionDataEntity(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }
        db.close();
        return list;
    }

    // 查询未推送数据
    public List<TimePositionDataEntity> queryNoPushLocations() {
        List<TimePositionDataEntity> list=new ArrayList<TimePositionDataEntity>();
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = {COLUMN_ID, COLUMN_TIMESTAMP, COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_GANZHI,COLUMN_ISPUSH};
//String selection: 这是用来指定选择条件的字符串，类似于 SQL 查询中的 WHERE 子句。如果你传入 null，那么将不会应用任何条件过滤。
//String[] selectionArgs: 如果你在 selection 参数中使用了占位符（例如，"WHERE name = ?"），那么这个数组就会用来填充这些占位符。它通常用于防止SQL注入攻击
        Cursor cursor = db.query(TABLE_LOCATIONS, columns, "is_push = 0", null, null, null, COLUMN_TIMESTAMP+ " DESC");
        while (cursor.moveToNext()){
            list.add(new TimePositionDataEntity(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getDouble(2),
                    cursor.getDouble(3),
                    cursor.getString(4),
                    cursor.getInt(5)
            ));
        }
        db.close();
        return list;
    }

    // 更新is_push
    public void updateIsPushForObjects(List<TimePositionDataEntity> objects) {
        SQLiteDatabase db = this.getWritableDatabase();

        for (TimePositionDataEntity obj : objects) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_ISPUSH, 1); // 将 is_push 设置为 1

            String whereClause = COLUMN_ID + " = ?";
            String[] whereArgs = { String.valueOf(obj.getId()) };

            int rowsUpdated = db.update(
                    TABLE_LOCATIONS,
                    values,
                    whereClause,
                    whereArgs
            );

            if (rowsUpdated > 0) {
                Log.d("Update", "Updated row with ID: " + obj.getId());
            } else {
                Log.e("Update", "No rows updated for ID: " + obj.getId());
            }
        }

        db.close();
    }
}
