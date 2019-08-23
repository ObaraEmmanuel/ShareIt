package com.triedcoders.shareit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 4;

    // Database Name
    private static final String DATABASE_NAME = "files_db";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

        // create files table
        db.execSQL(FileItem.CREATE_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + FileItem.TABLE_NAME);

        // Create tables again
        onCreate(db);
    }

    public void insertFileItem(FileItem item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(FileItem.COLUMN_PATH, item.getPath());
        values.put(FileItem.COLUMN_SIZE, item.getFileSize());
        values.put(FileItem.COLUMN_RATING, item.getRating());
        values.put(FileItem.COLUMN_DATE, item.getPostDate().getTime());

        long id = db.insert(FileItem.TABLE_NAME, null, values);

        db.close();
        item.id = id;
    }

    public FileItem getFileItem(long id) {
        // get readable database as we are not inserting anything
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(FileItem.TABLE_NAME,
                new String[]{FileItem.COLUMN_ID, FileItem.COLUMN_PATH, FileItem.COLUMN_RATING,
                FileItem.COLUMN_SIZE, FileItem.COLUMN_DATE},
                FileItem.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        if (cursor != null)
            cursor.moveToFirst();

        FileItem item = new FileItem(
                cursor.getInt(cursor.getColumnIndex(FileItem.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(FileItem.COLUMN_PATH)),
                cursor.getInt(cursor.getColumnIndex(FileItem.COLUMN_RATING)),
                cursor.getLong(cursor.getColumnIndex(FileItem.COLUMN_SIZE)),
                cursor.getLong(cursor.getColumnIndex(FileItem.COLUMN_DATE)));

        // close the db connection
        cursor.close();

        return item;
    }

    public ArrayList<FileItem> getAllFileItems() {
        ArrayList<FileItem> files = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT  * FROM " + FileItem.TABLE_NAME + " ORDER BY " +
                FileItem.COLUMN_DATE + " DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                FileItem file = new FileItem(
                        cursor.getInt(cursor.getColumnIndex(FileItem.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(FileItem.COLUMN_PATH)),
                        cursor.getInt(cursor.getColumnIndex(FileItem.COLUMN_RATING)),
                        cursor.getLong(cursor.getColumnIndex(FileItem.COLUMN_SIZE)),
                        cursor.getLong(cursor.getColumnIndex(FileItem.COLUMN_DATE)));
                files.add(file);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return files;
    }

    public int getFileItemCount() {
        String countQuery = "SELECT  * FROM " + FileItem.TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        return count;
    }

    public void deleteFileItem(FileItem file) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(FileItem.TABLE_NAME, FileItem.COLUMN_ID + " = ?",
                new String[]{String.valueOf(file.id)});
        db.close();
    }

    /*public int updateFileItem(FileItem file) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Note.COLUMN_NOTE, note.getNote());

        // updating row
        return db.update(Note.TABLE_NAME, values, Note.COLUMN_ID + " = ?",
                new String[]{String.valueOf(note.getId())});
    }*/
}
