package com.example.otams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.NonNull;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_USERS = "users";
    private static final String DATABASE_NAME = "userInfo.db";
    private static final int DATABASE_VERSION = 4;

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
    private static final String RegistrationDate = "registrationDate";
    private static final String Status = "status";

    public Database(Context context) {
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
                Course + " TEXT " +
                Status + " TEXT DEFAULT 'Under Review', " +
                RegistrationDate + "DOUBLE, " + ')';
        db.execSQL(createTable);
        ContentValues adminValues = getContentValues();
        db.insert(TABLE_USERS, null, adminValues);
    }

    @NonNull
    private static ContentValues getContentValues() {
        ContentValues adminValues = new ContentValues();
        adminValues.put(Role, "admin");
        adminValues.put(FirstName, "Admin");
        adminValues.put(LastName, "User");
        adminValues.put(Email, "admin@otams.ca");
        adminValues.put(Password, "admin");
        adminValues.put(PhoneNum, "0000000000");
        adminValues.put(Program, "");
        adminValues.put(Degree, "");
        adminValues.put(Course, "");
        adminValues.put(Status, "Approved");
        adminValues.put(RegistrationDate, System.currentTimeMillis());

        return adminValues;
    }

    //Update the database everytime there is a change in data
    public void onUpgrade(SQLiteDatabase db, int Previous, int New) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(Role, user.getRole());
        value.put(FirstName, user.getFirstName());
        value.put(LastName, user.getLastName());
        value.put(Email, user.getEmail());
        value.put(Password, user.getPassword());
        value.put(PhoneNum, user.getPhoneNum());
        value.put(RegistrationDate, System.currentTimeMillis());
        value.put(Status, "approval is pending"); //waiting for admin approval for every new User


        if (user instanceof Student) {
            Student student = (Student) user;
            value.put(Program, student.getProgram());
        } else if (user instanceof Tutor) {
            Tutor tutor = (Tutor) user;
            value.put(Degree, tutor.getDegree());
            value.put(Course, tutor.getCourse());
        }
        db.insert(TABLE_USERS, null, value);
        db.close();
    }

    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {Email, Password};
        String query = (Email + " = ? AND " + Password + " =? AND" + Status + " = ?");
        String[] args = {email, password, "Approved"};      //if approved == possible to log in

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



    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {Role};
        String selection = Email + " = ? AND " + Password + " = ? AND" + Status + " = ?";
        String[] selectionArgs = {email, password, "Approved"};

        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        String userRole = null;
        if (cursor.moveToFirst()) {
            userRole = cursor.getString(cursor.getColumnIndexOrThrow(Role));
            cursor.close();
            db.close();
            return userRole;
        }

        cursor.close();
        db.close();
        return userRole;
    }


    public String getUserRegistrationStatus(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {Status};
        String selection = Email +" = ? AND " + Password + " = ? AND" +Status + " = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        String status = null;
        if (cursor.moveToFirst()) {
            status = cursor.getString(cursor.getColumnIndexOrThrow(Status));


        }
        cursor.close();
        db.close();
        return status;
    }
        public List<RegistrationRequest> getPendingRegistrationRequests () {return getRegistrationRequestsByStatus("Under Review");}
        public List<RegistrationRequest> getApprovedRegistrationRequests (){return getRegistrationRequestsByStatus("Approved");}
        public List<RegistrationRequest> getRejectedRegistrationRequests (){return getRegistrationRequestsByStatus("Rejected");}





        //sort different request by status for the admin
        private List<RegistrationRequest> getRegistrationRequestsByStatus(String status) {
            List<RegistrationRequest> requests = new ArrayList<>();
            SQLiteDatabase db =this.getReadableDatabase();

            String query = "SELECT * FROM " + TABLE_USERS
                    + " WHERE " + Status + " = ?"
                    + " ORDER BY "+RegistrationDate + " DESC";

            Cursor cursor = db.rawQuery(query, new String[]{status});

            if (cursor.moveToFirst()){
                do {
                    RegistrationRequest request = new RegistrationRequest();
                    request.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(Id)));
                    request.setRole(cursor.getString(cursor.getColumnIndexOrThrow(Role)));
                    request.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(FirstName)));
                    request.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(LastName)));
                    request.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(Email)));
                    request.setPhoneNum(cursor.getString(cursor.getColumnIndexOrThrow(PhoneNum)));
                    request.setProgram(cursor.getString(cursor.getColumnIndexOrThrow(Program)));
                    request.setDegree(cursor.getString(cursor.getColumnIndexOrThrow(Degree)));
                    request.setCourse(cursor.getString(cursor.getColumnIndexOrThrow(Course)));
                    request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(Status)));
                    request.setRegistrationDate(cursor.getDouble(cursor.getColumnIndexOrThrow(RegistrationDate))) ;


                    requests.add(request);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();
            return requests;
        }



        public boolean approveRegistrationRequest(int userId) {
            return updateRegistrationStatus(userId, "Approved");
        }
        public  boolean rejectedRegistrationRequest(int userId){
            return updateRegistrationStatus(userId, "Rejected");
        }

        public boolean  updateRegistrationRequest(int userId) {
            return updateRegistrationStatus(userId, "Approval is pending");
        }


        private boolean updateRegistrationStatus(int userId, String status){
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(Status, status );

            int updatedRows = db.update(TABLE_USERS,values, Id + " = ?" , new String[]{String.valueOf(userId)});
            db.close();
            return updatedRows > 0;
    }





    public RegistrationRequest getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE " + Id + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});
        RegistrationRequest request = null;
        if (cursor.moveToFirst()) {
            request = new RegistrationRequest();
            request.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(Id)));
            request.setRole(cursor.getString(cursor.getColumnIndexOrThrow(Role)));
            request.setFirstName(cursor.getString(cursor.getColumnIndexOrThrow(FirstName)));
            request.setLastName(cursor.getString(cursor.getColumnIndexOrThrow(LastName)));
            request.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(Email)));
            request.setPhoneNum(cursor.getString(cursor.getColumnIndexOrThrow(PhoneNum)));
            request.setProgram(cursor.getString(cursor.getColumnIndexOrThrow(Program)));
            request.setDegree(cursor.getString(cursor.getColumnIndexOrThrow(Degree)));
            request.setCourse(cursor.getString(cursor.getColumnIndexOrThrow(Course)));
            request.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(Status)));
            request.setRegistrationDate(cursor.getDouble(cursor.getColumnIndexOrThrow(RegistrationDate)));

        }

        cursor.close();
        db.close();
        return request;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
        db.close();
    }




}






