package org.kleetus.bodybyscience;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

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

        Cursor cursor = db.query(
                Constants.LOGS_TABLE,
                projection,
                selection, selectionArgs, null, null, sortOrder);

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

            contentValues.put(Constants.DATETIME_COLUMN, System.currentTimeMillis() / 1000L);
            id = db.insertOrThrow(Constants.LOGS_TABLE, null, contentValues);

            db.setTransactionSuccessful();

            getContext().getContentResolver().notifyChange(uri, null, false);

            return Uri.withAppendedPath(uri, String.valueOf(id));

        } finally {

            db.endTransaction();

        }

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase db = helper.getWritableDatabase();

        db.beginTransaction();

        try {

            int res = db.delete(Constants.LOGS_TABLE, selection, selectionArgs);

            getContext().getContentResolver().notifyChange(uri, null, false);
            db.setTransactionSuccessful();
            return res;

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
