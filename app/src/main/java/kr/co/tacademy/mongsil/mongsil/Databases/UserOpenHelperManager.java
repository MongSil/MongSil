
package kr.co.tacademy.mongsil.mongsil.Databases;

import android.content.Context;

public class UserOpenHelperManager {
   private static UserDBHelper dbHandler;

   private UserOpenHelperManager(Context context){
	   dbHandler = new UserDBHelper(context);
   }
   public static UserDBHelper generateUserOpenHelper(Context context){
	   if( dbHandler != null){
		   return dbHandler;
	   }else{
		   new UserOpenHelperManager(context);
	   }
	   return generateUserOpenHelper(null);
   }
   public static void  setClose(UserDBHelper dbHelper){
	   if( dbHelper != null)
	   dbHelper.close();
   }
}
