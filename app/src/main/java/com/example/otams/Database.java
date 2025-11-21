package com.example.otams;

import static android.icu.text.MessagePattern.ArgType.SELECT;
import static android.provider.Contacts.SettingsColumns.KEY;

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

    private static final int DATABASE_VERSION = 9;

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
    private static final String SessionRequests = "sessionRequests";
    private static final String requestId = "requestId";
    private static final String tutorRating = "tutorRating";
    private static final String numberOfRatings = "numberofRatings";
    private static final String tutorId = "tutorId";









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
                tutorRating + " REAL DEFAULT 0, " +
                numberOfRatings + " INTEGER DEFAULT 0, " +

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
                + tutorId + " INTEGER, "
                + startTime + " TEXT, "
                + endTime + " TEXT, "
                + studentBooking + " TEXT, "
                + "FOREIGN KEY(" + slotID + ") REFERENCES " + slotTable + "(" + idSlot + "), "
                + "UNIQUE (" + slotID + ", " + startTime + ", " + endTime + ")"
                + ");";

        db.execSQL(CREATE_TABLE_PERIODS);
        String CREATE_TABLE_SESSION_REQUESTS = "CREATE TABLE " + SessionRequests + " (" +
                requestId + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                slotID + " INTEGER NOT NULL, " +
                Id + " INTEGER NOT NULL, " +
                periodID + " INTEGER, " +
                "requestDate TEXT, " +
                "status TEXT DEFAULT 'pending', " +
                "FOREIGN KEY(" + slotID + ") REFERENCES " + slotTable + "(" + idSlot + "), " +
                "FOREIGN KEY(" + Id + ") REFERENCES " + TABLE_USERS + "(" + Id + ")" +
                ");";
        db.execSQL(CREATE_TABLE_SESSION_REQUESTS);





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
        db.execSQL("DROP TABLE IF EXISTS " + SessionRequests);
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
    public String getUserEmailById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String email = null;

        Cursor cursor = db.rawQuery(
                "SELECT " + Email + " FROM " + TABLE_USERS + " WHERE " + Id + " = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(Email));
        }

        cursor.close();
        db.close();
        return email;
    }
    public String getStudentNameById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String fullName = null;

        Cursor cursor = db.rawQuery(
                "SELECT " + FirstName + ", " + LastName + " FROM " + TABLE_USERS + " WHERE " + Id + " = ?",
                new String[]{String.valueOf(id)}
        );

        if (cursor.moveToFirst()) {
            String firstName = cursor.getString(cursor.getColumnIndexOrThrow(FirstName));
            String lastName = cursor.getString(cursor.getColumnIndexOrThrow(LastName));
            fullName = firstName + " " + lastName;
        }

        cursor.close();
        db.close();

        return fullName;
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
                addPeriod((int) slotId,tutorId, currentStart, currentEnd);
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


    public long addPeriod(int slotId,int tutorId, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("slotID", slotId);
        values.put("tutorId", tutorId);
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
    public void updateAutoApprove(int periodId,int autoApproveValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("autoApprove", autoApproveValue);
        db.update("Periods", values, "periodID = ?", new String[]{String.valueOf(periodId)});
    }
    public Cursor getPeriodsBySlot(int slotId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                periodTable,
                new String[]{periodID, slotID, startTime, endTime, studentBooking},
                slotID + " = ?",
                new String[]{String.valueOf(slotId)},
                null,
                null,
                startTime + " ASC"
        );
    }
    public Cursor getPeriodById(int periodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                periodTable,
                new String[]{periodID, slotID, startTime, endTime, studentBooking},
                periodID + " = ?",
                new String[]{String.valueOf(periodId)},
                null,
                null,
                null
        );
    }
    // Check if student already has a pending or approved request for this period
    public boolean studentHasExistingRequestForPeriod(int studentId, int periodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sessionRequests WHERE id = ? AND periodID = ? AND status IN ('pending', 'approved')",
                new String[]{String.valueOf(studentId), String.valueOf(periodId)}
        );
        boolean exists = false;
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0;
        }
        cursor.close();
        return exists;
    }

    // Check if student has overlapping sessions (approved or pending) at the same date/time
    public boolean studentHasOverlappingSession(int studentId, int periodId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // First get the date and time of the period we're trying to book
        Cursor periodCursor = db.rawQuery(
                "SELECT s.date, p.startTime, p.endTime FROM periods p " +
                "JOIN slots s ON p.slotID = s.id WHERE p.periodID = ?",
                new String[]{String.valueOf(periodId)}
        );
        
        if (!periodCursor.moveToFirst()) {
            periodCursor.close();
            return false;
        }
        
        String date = periodCursor.getString(0);
        String startTime = periodCursor.getString(1);
        String endTime = periodCursor.getString(2);
        periodCursor.close();
        
        // Check for overlapping sessions
        Cursor overlapCursor = db.rawQuery(
                "SELECT COUNT(*) FROM sessionRequests sr " +
                "JOIN periods p ON sr.periodID = p.periodID " +
                "JOIN slots s ON p.slotID = s.id " +
                "WHERE sr.id = ? AND sr.status IN ('pending', 'approved') " +
                "AND s.date = ? " +
                "AND NOT (p.endTime <= ? OR p.startTime >= ?)",
                new String[]{String.valueOf(studentId), date, startTime, endTime}
        );
        
        boolean hasOverlap = false;
        if (overlapCursor.moveToFirst()) {
            hasOverlap = overlapCursor.getInt(0) > 0;
        }
        overlapCursor.close();
        return hasOverlap;
    }

    public long addSessionRequest(int periodId, int slotId, int studentId, String requestDate, boolean autoApprove) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(slotID, slotId);
        cv.put(periodID, periodId);
        cv.put("id", studentId);
        cv.put("requestDate", requestDate);
        if (autoApprove) {
            bookPeriod(periodId, String.valueOf(studentId));
            cv.put("status", "approved");
        } else {
            cv.put("status", "pending");
        }



        return db.insert(SessionRequests, null, cv);
    }
    public boolean approveRequest(int requestIdValue) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Query the student (Id) and slot for this request
        Cursor cursor = db.rawQuery(
                "SELECT Id, slotID,periodID FROM sessionRequests WHERE requestId = ?",
                new String[]{String.valueOf(requestIdValue)}
        );

        int studentId = -1;
        int slotId = -1;
        int periodId= -1;

        if (cursor.moveToFirst()) {
            studentId = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            slotId = cursor.getInt(cursor.getColumnIndexOrThrow("slotID"));
            periodId= cursor.getInt(cursor.getColumnIndexOrThrow("periodID"));
        }
        cursor.close();

        Log.d("Database", "approveRequest -> requestId=" + requestIdValue +
                ", studentId=" + studentId + ", slotId=" + slotId+",periodId="+periodId);
        boolean success = updateRequestStatus(requestIdValue, "approved");

        if (success) {
            bookPeriod(periodId, String.valueOf(studentId));

        }

        return success;
    }


    public boolean rejectRequest(int requestIdValue) {
        return updateRequestStatus(requestIdValue, "rejected");
    }

    public boolean cancelRequest(int requestIdValue) {
        return updateRequestStatus(requestIdValue, "cancelled");
    }

    private boolean updateRequestStatus(int requestIdValue, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("status", status);
        int updated = db.update(SessionRequests, cv, requestId + " = ?", new String[]{String.valueOf(requestIdValue)});
        return updated > 0;
    }
    public Cursor getPendingRequestsForTutor(int tutorId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT sr.requestId, sr.slotID, sr.id, sr.status, sr.requestDate, s.date, s.start_time, s.end_time " +
                        "FROM sessionRequests sr " +
                        "JOIN slots s ON sr.slotID = s.id " +
                        "WHERE s.tutorId = ? AND sr.status = 'pending'",
                new String[]{String.valueOf(tutorId)});
        return cursor;
    }


    public Cursor getBookedTutoringSessionsStudent(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT p.periodID, " + "s.date, p.startTime, p.endTime, " +
                "u.firstName || ' ' || u.lastName AS  tutorName " +
                "FROM  periods p " + "JOIN slots s ON p.slotID = s.id " +
                "JOIN users u ON s.tutorId = u.id " + "WHERE p.studentBooking = ? " +
                "ORDER BY  s.date ASC, p.startTime ASC";

        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    // Get ALL student sessions (pending/approved/rejected) - includes status
    public Cursor getAllStudentSessions(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT sr.requestId, p.periodID, s.date, p.startTime, p.endTime, " +
                "u.firstName || ' ' || u.lastName AS tutorName, sr.status " +
                "FROM sessionRequests sr " +
                "JOIN periods p ON sr.periodID = p.periodID " +
                "JOIN slots s ON p.slotID = s.id " +
                "JOIN users u ON s.tutorId = u.id " +
                "WHERE sr.id = ? " +
                "ORDER BY s.date ASC, p.startTime ASC";
        
        return db.rawQuery(query, new String[]{String.valueOf(studentId)});
    }

    // Check if a slot has any approved or pending sessions
    public boolean slotHasBookings(int slotId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sessionRequests WHERE slotID = ? AND status IN ('pending', 'approved')",
                new String[]{String.valueOf(slotId)}
        );
        boolean hasBookings = false;
        if (cursor.moveToFirst()) {
            hasBookings = cursor.getInt(0) > 0;
        }
        cursor.close();
        return hasBookings;
    }

    public boolean cancelTutoringSessionStudent(int periodId){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values=  new ContentValues();
        values.putNull("studentBooking");
        int updatedRows = db.update("Periods", values, "periodID = ?", new String[]{String.valueOf(periodId)});
        return updatedRows > 0;

    }
    public Cursor SearchperiodBycoursename(String courseName) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query =
                "SELECT p.periodID, " +
                        "       p.startTime, " +
                        "       p.endTime, " +
                        "       u.firstName || ' ' || u.lastName AS tutorName, " +
                        "       u.tutorRating, " +
                        "       s.date, " +
                        "       s.autoApprove, " +
                        "       p.slotID     "+
                        "FROM periods p " +
                        "JOIN users u ON p.tutorId = u.ID " +
                        "JOIN slots s ON p.slotID = s.id " +
                        "WHERE u.course = ? AND p.studentBooking IS NULL " +
                        "ORDER BY p.startTime ASC";

        return db.rawQuery(query, new String[]{courseName});
    }


    public void calculateTutorNewRating(int tutorID,int newRating) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT tutorRating FROM users WHERE id = ?", new String[]{String.valueOf(tutorID)});
        float currentRating=-1;
        int numberofRatings=-1;
        if (cursor.moveToFirst()) {
            currentRating = cursor.getInt(cursor.getColumnIndexOrThrow("tutorRating"));
            numberofRatings = cursor.getInt(cursor.getColumnIndexOrThrow("numberOfRatings"));

        }
        float newAverage = (currentRating * numberofRatings + newRating) / (numberofRatings + 1);
        numberofRatings++;

        updateTutorRating(tutorID, numberofRatings, newAverage);

        return ;
    }
    public void updateTutorRating(int tutorID,int numberOfRatings,float newAverage ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("tutorRating", newAverage);
        values.put("numberOfRatings", numberOfRatings);
        db.update("users", values, "id = ?", new String[]{String.valueOf(tutorID)});

    }






}







