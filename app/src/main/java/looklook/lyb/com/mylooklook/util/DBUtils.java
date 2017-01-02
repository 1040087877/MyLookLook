package looklook.lyb.com.mylooklook.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import looklook.lyb.com.mylooklook.config.Config;

/**
 * Created by 10400 on 2016/12/30.
 */

public class DBUtils {
    public static final String CREATE_TABLE_IF_NOT_EXISTS = "create table if not exists %s "
            + "(id integer primary key autoincrement,key text unique,is_read integer)";
    private static DBUtils sDBUtils;
    private SQLiteDatabase mSQLiteDatabase;

    public DBUtils(Context context) {
        mSQLiteDatabase = new DBHelper(context, Config.DB__IS_READ_NAME + ".db").getWritableDatabase();
    }

    public static synchronized DBUtils getDB(Context context) {
        if (sDBUtils == null) {
            sDBUtils = new DBUtils(context);
        }
        return sDBUtils;
    }

    public void insertHasRead(String table,String key,int value){
        Cursor cursor=mSQLiteDatabase.query(table,null,null,null,null,null,"id asc");
        if(cursor.getCount()>200 && cursor.moveToNext()){
            mSQLiteDatabase.delete(table,"id=?",new String[]{String.valueOf(cursor.getInt(cursor.getColumnIndex("id")))});
        }
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("key",key);
        //1表示已经阅读过
        contentValues.put("is_read",value);
        mSQLiteDatabase.insertWithOnConflict(table,null,contentValues,SQLiteDatabase.CONFLICT_REPLACE);
    }

    public boolean isRead(String table,String key,int value){
        boolean isRead=false;
        Cursor cursor=mSQLiteDatabase.query(table,null,"key=?",new String[]{key},null,null,null);
        if(cursor.moveToNext() && cursor.getInt(cursor.getColumnIndex("is_read"))==value){
            isRead=true;
        }
        cursor.close();
        return isRead;
    }

    public class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context, String name) {
            super(context, name, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL((String.format(CREATE_TABLE_IF_NOT_EXISTS, Config.ZHIHU)));
            db.execSQL((String.format(CREATE_TABLE_IF_NOT_EXISTS, Config.TOPNEWS)));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }
}
