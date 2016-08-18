package kr.co.tacademy.mongsil.mongsil;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLData;

public class UserDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "UserDB.db";
    private static final int DB_VERSION = 1;

    public UserDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    //DB에서 사용 할 스키마을 생성 함
    @Override
    public void onCreate(SQLiteDatabase database) {
        StringBuilder markTBLSQL = new StringBuilder();
        markTBLSQL
                .append("CREATE TABLE ")
                .append(UserDB.UserMark.TABLE_MARK_NAME)
                .append(" ( ")
                .append(UserDB.UserMark._ID)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT ,")
                .append(UserDB.UserMark.USER_MARK_LOCATION)
                .append(" TEXT ,")
                .append(UserDB.UserMark.USER_MARK_UPPER)
                .append(" TEXT ,")
                .append(UserDB.UserMark.USER_MARK_LAT)
                .append(" TEXT ,")
                .append(UserDB.UserMark.USER_MARK_LON)
                .append(" TEXT ")
                .append(" ); ");
        database.execSQL(markTBLSQL.toString());
    }

    /*public static void searchDB(SQLiteDatabase database) {
        StringBuilder markTBLSQL = new StringBuilder();
        markTBLSQL
                .append("SELECT * FROM ")
                .append(UserDB.UserMark.TABLE_MARK_NAME)
                .append(" WHERE ")
                .append(UserDB.UserMark.USER_MARK_LOCATION)
                .append(" ; ");
        database.execSQL(markTBLSQL.toString());
    }*/

    //필요하다면 DB Instance를 업데이트 한다
    @Override
    public void onUpgrade(SQLiteDatabase UserDB,
                          int oldVersion, int newVersion) {
    }

    @Override
    public void onOpen(SQLiteDatabase UserDB) {
        super.onOpen(UserDB);
    }
}