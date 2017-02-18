package kr.co.tacademy.mongsil.mongsil.Databases;

import android.provider.BaseColumns;

public final class UserDB {
   private UserDB(){}

   public static final class UserMark implements BaseColumns {
	   private UserMark(){}
	   
	   public static final String TABLE_MARK_NAME = "tbl_user_mark";
	   public static final String USER_MARK_LOCATION_NAME = "col_user_mark_name";
	   public static final String USER_MARK_UPPER = "col_user_mark_upper";
	   public static final String USER_MARK_MIDDLE = "col_user_mark_middle";
	   public static final String USER_MARK_LAT = "col_user_mark_lat" ;
	   public static final String USER_MARK_LON = "col_user_mark_lon" ;
	   public static final String SORT_ORDER = "col_user_mark_name ASC";
   }
}
