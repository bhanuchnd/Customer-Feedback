package com.feedback.db_helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import com.feedback.model.Feedback;
import com.feedback.model.Rating;
import com.feedback.model.Rating_;
import com.feedback.model.Staff_;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shridhar on 19/5/17.
 */

public class DbController extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "FeedBack_DB.db";
    private static final String TABLE_STAFF = "STAFF";
    private static final String STAFF_ID = "ID";
    private static final String STAFF_NAME = "NAME";
    private static final String STAFF_USERNAME = "USERNAME";
    private static final String STAFF_PASSWORD = "PASSWORD";
    private static final String STAFF_ROLE = "ROLE";
    private static final String STAFF_DESC = "DESCRIPTION";
    private static final String STAFF_DEGN = "DESIGNATION";
    private static final String STAFF_CREATE = "CREATED_AT";
    private static final String TABLE_RATING = "RATINGS";
    private static final String R_ID = "ID";
    private static final String R_TYPE = "RATING_TYPE";
    private static final String R_SCALE = "SCALE";
    private static final String R_CREATED = "CREATED_AT";
    private static final String TABLE_FB = "FEEDBACK";
    private static final String FB_ID = "ID";
    private static final String FB_CUSTOMER_NAME = "CUSTOMER_NAME";
    private static final String FB_RATING1 = "PERSON_RATING";
    private static final String FB_PHONE = "CONTACT_NO";
    private static final String FB_RATING2 = "OVERALL_RATING";
    private static final String FB_COMMENT = "COMMENT";
    private static final String FB_SERVICE_ON = "SERVICE_ON";
    private static final String FB_SERVICE_BY = "SERVICE_BY";

    private static final String CREATE_TABLE_RATING = "CREATE TABLE " + TABLE_RATING + " ( " + R_ID + " INTEGER PRIMARY KEY , "
            + R_TYPE + " TEXT NOT NULL, " + R_SCALE + "  INT(2) NOT NULL, " + R_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

    private static final String CREATE_TABLE_STAFF = "CREATE table " + TABLE_STAFF + " ( " + STAFF_ID + " INTEGER PRIMARY KEY , "
            + STAFF_NAME + " TEXT, "
            + STAFF_DESC + " TEXT, " + STAFF_CREATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + STAFF_USERNAME + " VARCHAR  NULL, "
            + STAFF_PASSWORD + " VARCHAR  NULL, " + STAFF_ROLE + " TEXT NOT NULL, " + STAFF_DEGN + " TEXT  NULL)";

    private static final String CREATE_TABLE_FB = "CREATE TABLE " + TABLE_FB + " ( " + FB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            FB_PHONE + " TEXT NULL, " + FB_RATING1 + " INT DEFAULT NULL, " + FB_RATING2 + " INT DEFAULT NULL, " + FB_CUSTOMER_NAME + " TEXT NOT NULL, " + FB_COMMENT +
            " TEXT NULL, " + FB_SERVICE_ON + " DATETIME DEFAULT CURRENT_TIMESTAMP, " + FB_SERVICE_BY + " INT NOT NULL,"
            + " FOREIGN KEY (" + FB_SERVICE_BY + ") REFERENCES " + TABLE_STAFF + " ( " + STAFF_ID + "), FOREIGN KEY (" + FB_RATING1 + ") REFERENCES "
            + TABLE_RATING + " (" + R_ID + "), FOREIGN KEY (" + FB_RATING2 + ") REFERENCES " + TABLE_RATING + " (" + R_ID + "));";

    private final Context context;
    private SQLiteDatabase db;

    public DbController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
        db = this.getWritableDatabase();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_STAFF);
        db.execSQL(CREATE_TABLE_FB);
        db.execSQL(CREATE_TABLE_RATING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATING);
        onCreate(db);
    }

    //    public long insertFeedBack(String name, String phone, int serviceRating, int overallRating, String comment, int serviceBy) {
    public long insertFeedBack(Feedback feedback) {
        ContentValues insertValues = new ContentValues();
        insertValues.put(FB_CUSTOMER_NAME, feedback.getCustomerName());
        insertValues.put(FB_PHONE, feedback.getPhone());
        insertValues.put(FB_COMMENT, feedback.getComments());
        insertValues.put(FB_RATING1, feedback.getServiceRating());
        insertValues.put(FB_RATING2, feedback.getOverallRating());
        insertValues.put(FB_SERVICE_BY, feedback.getServiceBy());
        return db.insert(TABLE_FB, null, insertValues);
    }

    //    public long insertStaff(String name, String description, String username, String password, String role, String designation) {
    public void insertStaff(List<Staff_> staff) {
        ContentValues insertValues = new ContentValues();
        for (Staff_ staff_ : staff) {
            insertValues.put(STAFF_ID,staff_.getId());
            insertValues.put(STAFF_NAME, staff_.getName());
            insertValues.put(STAFF_DESC, staff_.getDescription());
            insertValues.put(STAFF_USERNAME, staff_.getUsername());
            insertValues.put(STAFF_PASSWORD, staff_.getPassword());
            insertValues.put(STAFF_ROLE, staff_.getRole());
            insertValues.put(STAFF_DESC, staff_.getDesignation());
            db.insert(TABLE_STAFF, null, insertValues);
        }
        Log.d("DB", insertValues.toString());
    }

    public boolean isAuthenticated(String username, String password) {
        String query = "select " + STAFF_USERNAME + ", " + STAFF_PASSWORD + " from " + TABLE_STAFF + " where " + STAFF_ROLE + " = 'admin' ";
        Cursor cursor = db.rawQuery(query, null);
        boolean flag = true;
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            String user = cursor.getString(cursor.getColumnIndex(STAFF_USERNAME));
            String pass = cursor.getString(cursor.getColumnIndex(STAFF_PASSWORD));
            if (user.equals(username)) {
                if (pass.equals(password))
                    flag = true;

            }

        }
        return flag;
    }

    public void insertRatings(List<Rating_> rating_list) {
        ContentValues insertValues = new ContentValues();
        for (Rating_ list : rating_list) {
            insertValues.put(R_ID,list.getId());
            insertValues.put(R_TYPE, list.getRatingType());
            insertValues.put(R_SCALE, list.getScale());
            db.insert(TABLE_RATING, null, insertValues);
        }
    }

    public boolean isInsertedIntoFeedback() {
        String query = "select * from " + TABLE_FB;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() > 0) {
            return true;
        } else
            return false;
    }

    public List<Feedback> getFeedBack() {
        List<Feedback> fbList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_FB, new String[]{FB_ID, FB_SERVICE_BY, FB_RATING1, FB_RATING2, FB_COMMENT, FB_CUSTOMER_NAME, FB_PHONE, FB_SERVICE_ON}, null, null, null, null, null);
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Feedback data = storeFB(cursor);
                fbList.add(data);
                cursor.moveToNext();
            }
        }

        return fbList;
    }

    private Feedback storeFB(Cursor cursor) {
        Feedback fb = new Feedback();
        fb.setCustomerName(cursor.getString(cursor.getColumnIndex(FB_CUSTOMER_NAME)));
        fb.setId(cursor.getString(cursor.getColumnIndex(FB_ID)));
        fb.setPhone(cursor.getString(cursor.getColumnIndex(FB_PHONE)));
        fb.setComments(cursor.getString(cursor.getColumnIndex(FB_COMMENT)));
        fb.setOverallRating(cursor.getInt(cursor.getColumnIndex(FB_RATING2)));
        fb.setServiceRating(cursor.getInt(cursor.getColumnIndex(FB_RATING1)));
        fb.setServiceOn(cursor.getString(cursor.getColumnIndex(FB_SERVICE_ON)));
        fb.setServiceBy(cursor.getString(cursor.getColumnIndex(FB_SERVICE_BY)));
        return fb;
    }

    public List<Staff_> getStaff() {
        List<Staff_> staff = new ArrayList<>();
        Cursor cursor = db.query(TABLE_STAFF, new String[]{STAFF_NAME, STAFF_ID, STAFF_DEGN}, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Staff_ data = storeStaff(cursor);
            staff.add(data);
            cursor.moveToNext();
        }
        return staff;
    }

    public String getName(String id) {
        int ID = Integer.parseInt(id);
        String sql = "SELECT * FROM " + TABLE_STAFF + " WHERE " + STAFF_ID + " = " + ID;
        Cursor c = db.rawQuery(sql, null);
        if (c.getCount() > 0) {
            c.moveToFirst();
            return c.getString(c.getColumnIndex(STAFF_NAME));
        } else return null;

    }

    public List<Rating_> getRating() {

        Cursor cursor = db.query(TABLE_RATING, new String[]{R_TYPE, R_ID}, null, null, null, null, null);
        List<Rating_> rating = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Rating_ data = storeRating(cursor);
            rating.add(data);
            cursor.moveToNext();
        }
        return rating;
    }

    private Rating_ storeRating(Cursor cursor) {
        Rating_ rating_ = new Rating_();
        rating_.setRatingType(cursor.getString(cursor.getColumnIndex(R_TYPE)));
        rating_.setId(cursor.getInt(cursor.getColumnIndex(R_ID)));
        return rating_;
    }

    private Staff_ storeStaff(Cursor cursor) {
        Staff_ staff = new Staff_();
        staff.setName(cursor.getString(cursor.getColumnIndex(STAFF_NAME)));
        staff.setId(cursor.getString(cursor.getColumnIndex(STAFF_ID)));
        staff.setDesignation(cursor.getString(cursor.getColumnIndex(STAFF_DEGN)));
        return staff;
    }

    public void dropTables() {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAFF);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FB);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RATING);
        onCreate(db);
    }

    public void dropTable(String tableName) {
        String query = "DELETE FROM " + tableName;
        db.execSQL(query);

    }
}
