package com.wozia.nophonezone;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataHelper {

   private static final String DATABASE_NAME = "NoPhoneZone.db";
   private static final int DATABASE_VERSION = 4;

   private Context context;
   private SQLiteDatabase db;

   public DataHelper(Context context) {
       this.context = context;
       OpenHelper openHelper = new OpenHelper(this.context);
       this.db = openHelper.getWritableDatabase();
   }
   
   /** Start: Settings **/
   public Cursor fetchSetting(String varname) throws SQLException {
       Cursor c = db.query(true, "settings", new String[] { "varvalue" }, "`varname` = ?", new String[] { varname }, null, null, null, "1");
       if (c != null) {
           c.moveToFirst();
       }
       return c;
   }

   public void set(String name, String value) {
	   String sql = "UPDATE `settings` SET `varvalue` = ? WHERE `varname` = ?";
	   db.execSQL(sql, new Object[] { value, name } );
   }
   
   public String get(String name) {
	   Cursor c = fetchSetting(name);
	   
	   String rval = "";
	   
	   if (c.getCount() != 0) {
		   rval = c.getString(c.getColumnIndex("varvalue"));
	   }
	   c.close();
	   
	   return rval;
   }
   /** End: Settings **/
   
   /** Start: Private Group **/
   public Cursor fetchContact(String phone) throws SQLException {
       Cursor c = db.query(true, "private_group", new String[] { "name", "phone" }, "`phone` = ?", new String[] { phone }, null, null, null, "1");
       if (c != null) {
           c.moveToFirst();
       }
       return c;
   }

   public void addContact(String name, String phone) {
	   String sql = "INSERT INTO `private_group` (`id`, `name`, `phone`) VALUES (NULL, ?, ?)";
	   db.execSQL(sql, new Object[] { name, phone } );
   }
   
   public void deleteContact(String phone) {
	   String sql = "DELETE FROM `private_group` WHERE `phone` = ?";
	   db.execSQL(sql, new Object[] { phone } );
   }
   
   public String[] getContact(String phone) {
	   Cursor c = fetchContact(phone);
	   
	   String[] rval = {"", ""};
	   
	   if (c.getCount() != 0) {
		   rval[0] = c.getString(c.getColumnIndex("name"));
		   rval[1] = c.getString(c.getColumnIndex("phone"));
	   }
	   c.close();
	   
	   return rval;
   }
   
   public String[] getContactAt(int index) {
	   Cursor c = db.query(true, "private_group", new String[] { "name", "phone" }, null, null, null, null, "name asc, phone asc", null);
       if (c != null) {
           c.moveToPosition(index);
       }
	   
	   String[] rval = {"", ""};
	   
	   if (c.getCount() != 0) {
		   rval[0] = c.getString(c.getColumnIndex("name"));
		   rval[1] = c.getString(c.getColumnIndex("phone"));
	   }
	   c.close();
	   
	   return rval;
   }
   
   public List<String> getAllContacts() {
	   List<String> list = new ArrayList<String>();
	   Cursor c = this.db.query("private_group", new String[] { "name", "phone" }, 
	     null, null, null, null, "name asc, phone asc");
	   if (c.moveToFirst()) {
	      do {
	         list.add(c.getString(0) + " (" + c.getString(1) + ")"); 
	      } while (c.moveToNext());
	   }
	   if (c != null && !c.isClosed()) {
	      c.close();
	   }
	   return list;
   }
   /** End: Private Group **/
   
   /** Start: Warnings **/
   public boolean warningExists(String phone, int minutes) {
	   Date curDate = new Date();
	   String date = "";
	   
	   Calendar cal = Calendar.getInstance();
	   cal.setTime(curDate);
	   cal.add(Calendar.MINUTE, -minutes);
	   curDate = cal.getTime();
	   
	   date = String.valueOf(curDate.getTime());
	   
	   Cursor c = db.query(true, "warnings", new String[] { "id" }, "`phone` = ? AND `date` >= ?", new String[] { phone, date }, null, null, null, "1");
       if (c != null) {
           c.moveToFirst();
       }
	   
	   boolean rval = false;
	   
	   if (c.getCount() != 0) {
		   rval = true;
	   }
	   c.close();
	   
	   return rval;
   }
   
   public void addWarning(String phone) {
	   Date curDate = new Date();
	   long date = curDate.getTime(); 
	   
	   String sql = "INSERT INTO `warnings` (`id`, `phone`, `date`) VALUES (NULL, ?, ?)";
	   db.execSQL(sql, new Object[] { phone, date } );
   }
   /** End: Warnings **/

   public void deleteAll() {
       this.db.delete("settings", null, null);
       this.db.delete("private_group", null, null);
       this.db.delete("warnings", null, null);
   }
   
   public void clearWarnings() {
	   db.execSQL("DELETE FROM `warnings`");
   }

   private static class OpenHelper extends SQLiteOpenHelper {
	   
	   private static final String DATABASE_CREATE_1 = "CREATE TABLE IF NOT EXISTS `settings` (`varname` VARCHAR(20) PRIMARY KEY, `varvalue` TEXT)";
	   private static final String DATABASE_CREATE_2 = "CREATE TABLE IF NOT EXISTS `private_group` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `name` VARCHAR(50), `phone` VARCHAR(30))";
	   private static final String DATABASE_CREATE_3 = "CREATE TABLE IF NOT EXISTS `warnings` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `phone` VARCHAR(30), `date` INTEGER)";
	   private static final String DATABASE_INSERT_1 = "INSERT INTO `settings` VALUES('entry_txt','')";
	   private static final String DATABASE_INSERT_2 = "INSERT INTO `settings` VALUES('silent','')";
	   private static final String DATABASE_INSERT_3 = "INSERT INTO `settings` VALUES('contacts_only','')";
	   private static final String DATABASE_INSERT_4 = "INSERT INTO `settings` VALUES('disclaimer','')";
	   private static final String DATABASE_INSERT_5 = "INSERT INTO `settings` VALUES('less_warnings','')";

      OpenHelper(Context context) {
         super(context, DATABASE_NAME, null, DATABASE_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL(DATABASE_CREATE_1);
         db.execSQL(DATABASE_CREATE_2);
         db.execSQL(DATABASE_CREATE_3);
         db.execSQL(DATABASE_INSERT_1);
         db.execSQL(DATABASE_INSERT_2);
         db.execSQL(DATABASE_INSERT_3);
         db.execSQL(DATABASE_INSERT_4);
         db.execSQL(DATABASE_INSERT_5);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    	  db.execSQL("DROP TABLE IF EXISTS `settings`");
    	  //db.execSQL("DROP TABLE IF EXISTS `private_group`");
    	  onCreate(db);
      }
   }
}