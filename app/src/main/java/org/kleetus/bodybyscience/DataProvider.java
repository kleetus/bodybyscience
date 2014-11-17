package org.kleetus.bodybyscience;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.SystemClock;

public class DataProvider extends ContentProvider {

    private SQLiteOpenHelper helper;

    static {
        UriMatcher uriMatcher = new UriMatcher(0);

        uriMatcher.addURI(
                Constants.AUTHORITY,
                Constants.LOGS_TABLE,
                Constants.LOGS_INDEX);

    }


    public boolean onCreate() {

        helper = new DataProviderHelper(getContext());
        return true;

    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase db = helper.getReadableDatabase();

        String groupBy = null;

        if (selection.length() < 1) {
            groupBy = Constants.WORKOUT_NUMBER_COLUMN;
        }

        Cursor cursor = db.query(
                Constants.LOGS_TABLE,
                projection,
                selection, selectionArgs, groupBy, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;

    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();

        long id;

        try {

            contentValues.put(Constants.WORKOUT_NUMBER_COLUMN, getWorkoutNumber());
            contentValues.put(Constants.DATETIME_COLUMN, System.currentTimeMillis() / 1000L);
            id = db.insertOrThrow(Constants.LOGS_TABLE, null, contentValues);

            db.setTransactionSuccessful();

            getContext().getContentResolver().notifyChange(uri, null, false);

            return Uri.withAppendedPath(uri, String.valueOf(id));

        } finally {

            db.endTransaction();

        }

    }

    private int getWorkoutNumber() {

        int workoutNumber = 1;

        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor cursor = db.query(
                Constants.LOGS_TABLE,
                new String[]{Constants.WORKOUT_NUMBER_COLUMN, Constants.DATETIME_COLUMN},
                null, null, null, null, Constants.DATETIME_COLUMN + " DESC", "1");

        if (cursor.getCount() > 0) {

            cursor.moveToFirst();
            workoutNumber = cursor.getInt(0);

            long lastTime = cursor.getLong(1);
            long timeNow = System.currentTimeMillis() / 1000L;
            long diff = (timeNow - lastTime);

            if (diff > Constants.TWENTY_FOUR_HOURS) {

                workoutNumber++;

            }

        }

        return workoutNumber;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();

        try {

            db.delete(Constants.LOGS_TABLE, selection, selectionArgs);

            getContext().getContentResolver().notifyChange(uri, null, false);
            db.setTransactionSuccessful();
            return 0;

        } finally {

            db.endTransaction();

        }

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();

        try {

            db.update(Constants.LOGS_TABLE, contentValues, selection, selectionArgs);

            db.setTransactionSuccessful();
            getContext().getContentResolver().notifyChange(uri, null, false);
            return 0;

        } finally {

            db.endTransaction();

        }

    }


    private class DataProviderHelper extends SQLiteOpenHelper {

        public DataProviderHelper(Context context) {
            super(context,
                    Constants.DATABASE_NAME,
                    null,
                    Constants.DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            sqLiteDatabase.execSQL(Constants.CREATE_LOGS);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {

            dropTables(sqLiteDatabase);
            onCreate(sqLiteDatabase);

        }

        private void dropTables(SQLiteDatabase db) {

            db.execSQL("DROP TABLE IF EXISTS " + Constants.LOGS_TABLE);

        }
    }
}
