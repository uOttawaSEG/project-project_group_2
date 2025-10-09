package com.example.otams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_USERS = "users";
    private static final String DATABASE_NAME = "userInfo.db";
    private static final int DATABASE_VERSION = 1;

    private static final String Id = "id";
    private static final String Role = "role";
    private static final String FirstName = "firstName";
    private static final String LastName = "lastName";
    private static final String Email = "email";
    private static final String Password = "password";
    private static final String PhoneNum = "phoneNum";

    private static final String Degree = "degree";
    private static final String Course = "course";
    private static final String Program = "program";

    public Database(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the initial database among with all the variable type
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_USERS + "(" +
                Id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Role + " TEXT, " +
                FirstName + " TEXT, " +
                LastName + " TEXT, " +
                Email + " TEXT UNIQUE, " +
                Password + " TEXT, " +
                PhoneNum + " TEXT, " +
                Program + " TEXT, " +
                Degree + " TEXT, " +
                Course + " TEXT " + ")";
        db.execSQL(createTable);
        ContentValues adminValues = getContentValues();
        db.insert(TABLE_USERS, null, adminValues);
    }

    @NonNull
    private  static ContentValues getContentValues() {
        ContentValues adminValues = new ContentValues();
        adminValues.put(Role, "admin");
        adminValues.put(FirstName, "Admin");
        adminValues.put(LastName, "User");
        adminValues.put(Email, "admin");
        adminValues.put(Password, "admin");
        adminValues.put(PhoneNum, "0000000000");
        adminValues.put(Program, "");
        adminValues.put(Degree, "");
        adminValues.put(Course, "");
        return adminValues;
    }

    //Update the database everytime there is a change in data
    public void onUpgrade(SQLiteDatabase db, int Previous, int New){
        db.execSQL("DROP TABLE IF EXISTS "  + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(User user){
        SQLiteDatabase db= this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(Role, user.getRole());
        value.put(FirstName, user.getFirstName());
        value.put(LastName, user.getLastName());
        value.put(Email, user.getEmail());
        value.put(Password, user.getPassword());
        value.put(PhoneNum, user.getPhoneNum());

        if (user instanceof Student){
            Student student = (Student) user;
            value.put(Program, student.getProgram());
        } else if (user instanceof Tutor){
            Tutor tutor = (Tutor) user;
            value.put(Degree, tutor.getDegree());
            value.put(Course, tutor.getCourse());
        }
        db.insert(TABLE_USERS, null, value);
        db.close();
    }

    public boolean checkUser(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();

        String [] columns = {Email, Password};
        String query = (Email + " = ? AND " + Password + " = ?");
        String[] args = {email, password};

        Cursor cursor = db.query(TABLE_USERS,
                columns,
                query,
                args,
                null,
                null,
                null);

        int count = cursor.getCount();

        cursor.close();
        db.close();

        return count > 0;
    }
    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db); // onCreate will create the table and insert admin
        db.close();
    }
    public String getUserRole(String email, String password){
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {Role};
        String selection = Email + " = ? AND " + Password + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        String userRole = null;
        if(cursor.moveToFirst()){
            userRole = cursor.getString(cursor.getColumnIndexOrThrow(Role));
        }

        cursor.close();
        db.close();
        return userRole;
    }
}

