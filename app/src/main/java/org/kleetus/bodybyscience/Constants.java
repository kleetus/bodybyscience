package org.kleetus.bodybyscience;


import android.net.Uri;

public class Constants {

    public static final String RESTART = "start";
    public static final String STOP = "stop";
    public static final String LOGS_TABLE = "Logs";
    public static final String ROW_ID = "_id";
    public static final String DATETIME_COLUMN = "datetime";
    public static final String WEIGHT_COLUMN = "weight";
    public static final String TUL_COLUMN = "tul";
    public static final String AUTHORITY = "org.kleetus.bodybyscience";
    public static final int LOGS_INDEX = 1;
    public static final String DATABASE_NAME = "bodybyscience";
    public static final String SCHEME = "content";
    public static final Uri CONTENT_URI = Uri.parse(SCHEME + "://" + AUTHORITY);
    public static final Uri LOG_CONTENTURI = Uri.withAppendedPath(CONTENT_URI, LOGS_TABLE);
    public static final String ACTIVE_TAB = "active_tab";
    public static final String EXERCISE_COLUMN = "execise";
    public static final String WORKOUT_NUMBER_COLUMN = "workoutnumber";
    public static final String SUM_EXERCISE_WEIGHT = "SUM(" + WEIGHT_COLUMN + ")";
    public static final String SUM_TUL = "SUM(" + TUL_COLUMN + ")";
    public static final String TIME_ON_CLOCK = "timeOnClock";
    public static final String START_BUTTON_STATE = "startButtonState";
    public static final String PRIMARY_KEY_TYPE = "INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL";
    public static final String UNIQUE_INT_TYPE = "INT UNIQUE NOT NULL";
    public static final String INTEGER_TYPE = "INTEGER NOT NULL";
    public static final String TEXT_TYPE = "TEXT NOT NULL";
    public static final int EXERCISE_LOADER = 0;
    public static final int SUMMARY_LOADER = 1;
    public static final int SUMMARY_ITEM_LOADER = 2;

    public static final String CREATE_LOGS = "CREATE TABLE" + " " +
            Constants.LOGS_TABLE
            + " " + "(" + " " +
            Constants.ROW_ID
            + " " +
            PRIMARY_KEY_TYPE
            + ", " +
            Constants.DATETIME_COLUMN
            + " " +
            INTEGER_TYPE
            + ", " +
            Constants.WEIGHT_COLUMN
            + " " +
            INTEGER_TYPE
            + ", " +
            Constants.TUL_COLUMN
            + " " +
            INTEGER_TYPE
            + ", " +
            Constants.EXERCISE_COLUMN
            + " " +
            TEXT_TYPE
            + ", " +
            Constants.WORKOUT_NUMBER_COLUMN
            + " " +
            INTEGER_TYPE
            + ")";


    public static final int DATABASE_VERSION = 14;



}
