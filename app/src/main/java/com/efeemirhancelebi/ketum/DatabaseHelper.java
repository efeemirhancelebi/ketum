package com.efeemirhancelebi.ketum;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "borc_gelir_db";
    private static final int DATABASE_VERSION = 2;

    // Table and column names for income
    private static final String TABLE_INCOME = "income";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_INCOME = "income_amount";

    // Table and column names for debts
    private static final String TABLE_DEBT = "debts";
    private static final String COLUMN_DEBT_NAME = "debt_name";
    private static final String COLUMN_DEBT_AMOUNT = "debt_amount";
    private static final String COLUMN_DEBT_DUE_DATE = "debt_due_date";
    private static final String COLUMN_DEBT_DUE_DATE_MONTH = "debt_due_date_month";
    private static final String COLUMN_DEBT_DUE_DATE_YEAR = "debt_due_date_year";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create income table
        String CREATE_INCOME_TABLE = "CREATE TABLE " + TABLE_INCOME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_INCOME + " INTEGER)";
        db.execSQL(CREATE_INCOME_TABLE);

        // Create debts table
        String CREATE_DEBT_TABLE = "CREATE TABLE " + TABLE_DEBT + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_DEBT_NAME + " TEXT, " +
                COLUMN_DEBT_AMOUNT + " INTEGER, " +
                COLUMN_DEBT_DUE_DATE + " TEXT, " +
                COLUMN_DEBT_DUE_DATE_MONTH + " INTEGER, " +
                COLUMN_DEBT_DUE_DATE_YEAR + " INTEGER)";
        db.execSQL(CREATE_DEBT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add due date month and year columns to debts table
            db.execSQL("ALTER TABLE " + TABLE_DEBT + " ADD COLUMN " + COLUMN_DEBT_DUE_DATE_MONTH + " INTEGER");
            db.execSQL("ALTER TABLE " + TABLE_DEBT + " ADD COLUMN " + COLUMN_DEBT_DUE_DATE_YEAR + " INTEGER");
        }
    }

    // Save income to the database
    public void saveGelir(int gelir) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_INCOME, gelir);
        db.insert(TABLE_INCOME, null, contentValues);
        db.close();
    }

    // Update income in the database
    public void updateGelir(int gelir) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_INCOME, gelir);
        db.update(TABLE_INCOME, values, null, null); // Update all rows (1 income record expected)
        db.close();
    }

    // Get income from the database
    public Cursor getGelir() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_INCOME, null);
    }

    // Insert debt data
    public void insertDebt(String debtName, int debtAmount, String dueDate, int dueDateMonth, int dueDateYear) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_DEBT_NAME, debtName);
        values.put(COLUMN_DEBT_AMOUNT, debtAmount);
        values.put(COLUMN_DEBT_DUE_DATE, dueDate);
        values.put(COLUMN_DEBT_DUE_DATE_MONTH, dueDateMonth);
        values.put(COLUMN_DEBT_DUE_DATE_YEAR, dueDateYear);

        long result = db.insert(TABLE_DEBT, null, values);
        if (result == -1) {
            Log.e("Database", "Borç eklenemedi");
        } else {
            Log.i("Database", "Borç başarıyla eklendi");
        }
        db.close();
    }

    // Retrieve debts from the database
    public Cursor getDebts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_DEBT, null);
    }

    // Add multiple debts (example)
    public void addDebt(int x, int borcMiktari) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_DEBT_NAME, "Borç " + x);
        contentValues.put(COLUMN_DEBT_AMOUNT, borcMiktari);
        contentValues.put(COLUMN_DEBT_DUE_DATE, "2025-12-31"); // Default date
        contentValues.put(COLUMN_DEBT_DUE_DATE_MONTH, 12);
        contentValues.put(COLUMN_DEBT_DUE_DATE_YEAR, 2025);

        long result = db.insert(TABLE_DEBT, null, contentValues);
        if (result == -1) {
            Log.e("Database", "Borç eklenemedi");
        } else {
            Log.i("Database", "Borç başarıyla eklendi");
        }
        db.close();
    }
}
