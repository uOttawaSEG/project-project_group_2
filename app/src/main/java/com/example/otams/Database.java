package com.example.otams;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String TABLE_USERS = "users";
    private static final String DATABASE_NAME = "userInfo.db";

    private static final int DATABASE_VERSION = 5;

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

    private static final String slotTable = "slots";
    private static final String idSlot = "id";
    private static final String tutorIdSlot = "tutorId";
    private static final String dateSlot = "date";
    private static final String startTimeSlot = "start_time";
    private static final String endTimeSlot = "end_time";
    private static final String studentBooking = "studentBooking";
    private static final String periodTable = "periods";
    private static final String autoApprove="autoApprove";
    private static final String periodID = "periodID";
    private static final String slotID = "slotID";
    private static final String startTime = "startTime";
    private static final String endTime = "endTime";




    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the database
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

                Course + " TEXT, " +

                Status + " TEXT DEFAULT 'pending approval', " +

                RegistrationDate + " REAL" + ")";
        db.execSQL(createTable);

        String CREATE_SLOTS_TABLE = "CREATE TABLE " + slotTable + " ("
                + idSlot + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + tutorIdSlot + " INTEGER NOT NULL, "
                + dateSlot + " TEXT, "
                + startTimeSlot + " TEXT, "
                + endTimeSlot + " TEXT, "
                + autoApprove + " INTEGER DEFAULT 0, "
                + "FOREIGN KEY(" + tutorIdSlot + ") REFERENCES " + TABLE_USERS + "(" + Id + "), "
                + "UNIQUE (" + tutorIdSlot + ", " + dateSlot + ", " + startTimeSlot + ", " + endTimeSlot + ")"
                + ");";
        db.execSQL(CREATE_SLOTS_TABLE);
        String CREATE_TABLE_PERIODS = "CREATE TABLE " + periodTable + " ("
                + periodID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + slotID + " INTEGER, "
                + startTime + " TEXT, "
                + endTime + " TEXT, "
                + studentBooking + " TEXT, "
                + "FOREIGN KEY(" + slotID + ") REFERENCES " + slotTable + "(" + idSlot + "), "
                + "UNIQUE (" + slotID + ", " + startTime + ", " + endTime + ")"
                + ");";

        db.execSQL(CREATE_TABLE_PERIODS);


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
        db.execSQL("DROP TABLE IF EXISTS " + slotTable);
        db.execSQL("DROP TABLE IF EXISTS " + periodTable);
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

        value.put(Status, "pending approval");


        if (user instanceof Student) {
            Student student = (Student) user;
            value.put(Program, student.getProgram());
        } else if (user instanceof Tutor) {
            Tutor tutor = (Tutor) user;
            value.put(Degree, tutor.getDegree());
            value.put(Course, tutor.getCourse());
        }
        db.insert(TABLE_USERS, null, value);

    }


    public boolean checkUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();


        String[] columns = {Id};

        String selection = Email + " = ? AND " + Password + " = ? AND " + Status + " = ?";

        String[] selectionArgs = {email, password, "Approved"};

        Cursor cursor = db.query(TABLE_USERS,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);

        int count = cursor.getCount();

        cursor.close();

        return count > 0;
    }


    public String getUserRole(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {Role};

        String selection = Email + " = ? AND " + Password + " = ? AND " + Status + " = ?";
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

        }

        cursor.close();
        return userRole;
    }

    public String getUserRegistrationStatus(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] columns = {Status};

        String selection = Email + " = ? AND " + Password + " = ?";

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
        return status;
    }

    public List<RegistrationRequest> getPendingRegistrationRequests() {
        return getRegistrationRequestsByStatus("pending approval");
    }

    public List<RegistrationRequest> getApprovedRegistrationRequests() {
        return getRegistrationRequestsByStatus("Approved");

    }

    public List<RegistrationRequest> getRejectedRegistrationRequests() {
        return getRegistrationRequestsByStatus("Rejected");
    }


    private List<RegistrationRequest> getRegistrationRequestsByStatus(String status) {
        List<RegistrationRequest> requests = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_USERS
                + " WHERE " + Status + " = ?"
                + " ORDER BY " + RegistrationDate + " DESC";

        Cursor cursor = db.rawQuery(query, new String[]{status});

        if (cursor.moveToFirst()) {
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

                //date
                request.setRegistrationDate(cursor.getLong(cursor.getColumnIndexOrThrow(RegistrationDate)));

                requests.add(request);
            } while (cursor.moveToNext());
        }

        cursor.close();

        return requests;
    }

    public boolean approveRegistrationRequest(int userId) {

        return updateRegistrationStatus(userId, "Approved");
    }

    public boolean rejecteRegistrationRequest(int userId) {
        return updateRegistrationStatus(userId, "Rejected");
    }

    public boolean setRegistrationToPending(int userId) {

        return updateRegistrationStatus(userId, "pending approval");
    }


    private boolean updateRegistrationStatus(int userId, String newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Status, newStatus);


        int updatedRows = db.update(TABLE_USERS, values, Id + " = ?", new String[]{String.valueOf(userId)});
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
            // CHANGE: Changed getDouble to getLong for timestamps.
            request.setRegistrationDate(cursor.getLong(cursor.getColumnIndexOrThrow(RegistrationDate)));
        }

        cursor.close();

        return request;
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + slotTable);
        db.execSQL("DROP TABLE IF EXISTS " + periodTable);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);

    }

    // Retrieve user id by email (returns -1 if not found)
    public int getUserIdByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        int result = -1;
        Cursor c = db.query(TABLE_USERS, new String[]{Id}, Email + " = ?", new String[]{email}, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                int idx = c.getColumnIndex(Id);
                if (idx >= 0) result = c.getInt(idx);
            }
            c.close();
        }
        return result;
    }

    // Add a tutor-specific availability slot (tutorId + date + time unique)
    public long addSlot(int tutorId, String date, String startTime, String endTime,int autoApproveValue) {
        SQLiteDatabase db = this.getWritableDatabase();

        // 1. Create the slot entry
        ContentValues values = new ContentValues();
        values.put("tutorID", tutorId);
        values.put("date", date);
        values.put("start_time", startTime);
        values.put("end_time", endTime);
        values.put("autoApprove",autoApproveValue );

        long slotId;
        try {
            slotId = db.insertOrThrow("slots", null, values);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Insert failed: " + e.getMessage());
            return -1;
        }
        int numPeriods = getPeriods(startTime, endTime);
        Log.d("DEBUG", "Number of periods: " + numPeriods);



        // 2. If slot was inserted successfully, create its periods
        if (slotId != -1) {
            String currentStart = startTime;
            String currentEnd;

            for (int i = 0; i < getPeriods(startTime, endTime); i++) {
                currentEnd = nextPeriod(currentStart);
                addPeriod((int) slotId, currentStart, currentEnd);
                currentStart = currentEnd;
            }
        }

        return slotId;
    }
    public boolean removeSlot(int slotID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {

            db.delete("Periods", "slotID = ?", new String[]{String.valueOf(slotID)});


            int deletedRows = db.delete("Slots", "id = ?", new String[]{String.valueOf(slotID)});


            db.setTransactionSuccessful();
            return deletedRows > 0;

        } catch (Exception e) {
            return false;

        } finally {
            db.endTransaction();
        }
    }


    public long addPeriod(int slotId, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("slotID", slotId);
        values.put("startTime", startTime);
        values.put("endTime", endTime);
        values.putNull("studentBooking");

        try {
            return db.insertOrThrow("Periods", null, values);
        } catch (Exception e) {
            Log.e("DB_ERROR", "Insert failed: " + e.getMessage());
            return -1;
        }
    }


    // Get slots for a specific tutor ordered by date/time
    public Cursor getSlotsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                slotTable,
                new String[]{idSlot, tutorIdSlot, dateSlot, startTimeSlot, endTimeSlot, autoApprove},
                tutorIdSlot + " = ?",
                new String[]{String.valueOf(tutorId)},
                null, null,
                dateSlot + " ASC, " + startTimeSlot + " ASC"
        );
    }


    // (Legacy) Get all slots - retained for backward compatibility, prefer getSlotsForTutor
    public Cursor getAllSlots() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + slotTable + " ORDER BY " + dateSlot + " ASC, " + startTimeSlot + " ASC", null);
    }

    public int ConvertToInt(String time) {
        String[] splitTime = time.split(":");
        int hour = Integer.parseInt(splitTime[0]);
        int minute = Integer.parseInt(splitTime[1]);
        return hour * 60 + minute;
    }

    public String ConvertToString(int time) {
        int hour = time / 60;
        int minute = time % 60;
        return hour + ":" + minute;

    }

    public String nextPeriod(String time) {
        int timeInt = ConvertToInt(time);
        timeInt += 30;
        return ConvertToString(timeInt);


    }

    public int getPeriods(String startTime, String endTime) {
        int timeInt = ConvertToInt(endTime) - ConvertToInt(startTime);
        return timeInt / 30;
    }
    public void bookPeriod(int periodId, String studentID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("studentBooking", studentID);
        db.update("Periods", values, "periodID = ?", new String[]{String.valueOf(periodId)});
    }
    public int cancelBooking(int periodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.putNull("studentBooking");
        return db.update("Periods", values, "periodID = ?", new String[]{ String.valueOf(periodId) });
    }
    public void autoApprove(int periodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("autoApprove", 1);
        db.update("Periods", values, "periodID = ?", new String[]{String.valueOf(periodId)});
    }
    public void NoautoApprove(int periodId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("autoApprove", 0);
        db.update("Periods", values, "periodID = ?", new String[]{String.valueOf(periodId)});
    }

}







