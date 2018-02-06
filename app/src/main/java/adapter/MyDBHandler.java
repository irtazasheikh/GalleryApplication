package adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.privategalleryapplication.User;

import java.util.ArrayList;

public class MyDBHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "usersDB.db";
    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_TYPE = "type";

    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_USERS + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PASSWORD + " TEXT, "+ COLUMN_TYPE + " TEXT " +
                ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    //Add a new row to the database
    public void addUser(User user){
        ContentValues values = new ContentValues();
        values.put(COLUMN_PASSWORD, user.get_password());
        values.put(COLUMN_TYPE, user.get_type());
        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_USERS, null, values);
        db.close();
    }

    public void deleteUser(String password){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USERS + " WHERE " + COLUMN_PASSWORD + "=\"" + password + "\";");
    }

    public int isPresent(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE 1";

        Cursor recordSet = db.rawQuery(query, null);
        if(recordSet!=null && recordSet.getCount()>0){
            return 1;
        }
        return 0;
    }

    public void updateUser(String new_password,String old_password){
        SQLiteDatabase db = getWritableDatabase();
        String query = ("UPDATE "+ TABLE_USERS+" SET "+ COLUMN_PASSWORD+" = \""+new_password+
                "\" WHERE "+ COLUMN_PASSWORD + " = \""+old_password+"\";");
        db.execSQL(query);
    }


    public String typeAccount(String password){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE "+ COLUMN_PASSWORD + "=\"" + password + "\";";

        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();

        return recordSet.getString(recordSet.getColumnIndex("type"));
    }

    public String Directory(String pass){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE 1";
        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();

        while (!recordSet.isAfterLast()) {
            // null could happen if we used our empty constructor
            if (recordSet.getString(recordSet.getColumnIndex("password")) != null) {
                if (recordSet.getString(recordSet.getColumnIndex("password")).equals(pass)){
                    System.out.println("The id is "+recordSet.getString(recordSet.getColumnIndex("id")));
                    return recordSet.getString(recordSet.getColumnIndex("id"));
                }
            }
            recordSet.moveToNext();
        }
        db.close();
        return dbString;
    }

//    public String getParentDirectory(String password){
//        return Directory(getParentPassword(password));
//    }

//    public String getParentPassword(String pass) {
//        String dbString = "";
//        SQLiteDatabase db = getWritableDatabase();
//        String query = "SELECT * FROM " + TABLE_USERS + " WHERE 1";
//        Cursor recordSet = db.rawQuery(query, null);
//        recordSet.moveToFirst();
//
//        while (!recordSet.isAfterLast()) {
//            // null could happen if we used our empty constructor
//            if (recordSet.getString(recordSet.getColumnIndex("password")) != null) {
//                if (recordSet.getString(recordSet.getColumnIndex("password")).equals(pass)){
//                    return recordSet.getString(recordSet.getColumnIndex("parent_password"));
//                }
//            }
//            recordSet.moveToNext();
//        }
//        db.close();
//        return dbString;
//    }

    public boolean isUser(String pass){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE 1";
        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();

        while (!recordSet.isAfterLast()) {
            if (recordSet.getString(recordSet.getColumnIndex("password")) != null) {
                if (recordSet.getString(recordSet.getColumnIndex("password")).equals(pass)){
                    return true;
                }
            }
            recordSet.moveToNext();
        }
        return false;
    }

    public ArrayList<String> getPasswords(){
        Log.d("called","YO!");
        SQLiteDatabase db = getWritableDatabase();
        ArrayList<String> passwords=new ArrayList<String>();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE 1";
        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();
        while (!recordSet.isAfterLast()) {
            passwords.add(recordSet.getString(recordSet.getColumnIndex("password")));
            recordSet.moveToNext();
        }
        recordSet.close();
        db.close();
        Log.d("pass",passwords.get(0));
        return passwords;
    }
}
