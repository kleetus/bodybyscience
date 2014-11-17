package org.kleetus.bodybyscience.tests.org.kleetus.bodybyscience.tests.functional;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.os.SystemClock;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;

import org.kleetus.bodybyscience.Constants;
import org.kleetus.bodybyscience.ExerciseActivity;
import org.kleetus.bodybyscience.R;

import java.util.Random;


public class ExerciseActivityTest extends ActivityInstrumentationTestCase2<ExerciseActivity> {

    private ExerciseActivity activity;

    public ExerciseActivityTest() {
        super(ExerciseActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
    }

    //do the log, plus, minus, start, reset buttons, weight edit text and chrono show up
    @UiThreadTest
    public void testLayoutParts() {
        activity.getExerciseFragmentPagerAdapter().getViewPager().setCurrentItem(0);
        Button logButton = (Button) activity.findViewById(R.id.log);
        Button startButton = (Button) activity.findViewById(R.id.start);
        Button resetButton = (Button) activity.findViewById(R.id.reset);
        Button minusButton = (Button) activity.findViewById(R.id.decrease_button);
        Button plusButton = (Button) activity.findViewById(R.id.increase_button);
        Chronometer chrono = (Chronometer) activity.findViewById(R.id.timer);
        EditText weightText = (EditText) activity.findViewById(R.id.weight);

        assertNotNull(logButton);
        assertNotNull(weightText);
        assertNotNull(startButton);
        assertNotNull(minusButton);
        assertNotNull(plusButton);
        assertNotNull(resetButton);
        assertNotNull(chrono);

        assertEquals("Incorrect label of the button", "Log", logButton.getText());
        assertEquals("Incorrect label of the button", "start", startButton.getText());
        assertEquals("Incorrect label of the button", "reset", resetButton.getText());
        assertEquals("Incorrect label of the button", "-", minusButton.getText());
        assertEquals("Incorrect label of the button", "+", plusButton.getText());

    }

    //do we have the right tabs?
    public void testForTheRightTabs() {
        ExerciseActivity.ExerciseFragmentPagerAdapter adapter = activity.getExerciseFragmentPagerAdapter();

        String[] exercises = adapter.getExercises();

        for (int i = 0; i < exercises.length; i++) {

            Bundle bundle = adapter.getTabs().get(i).getArgs();
            assertEquals(exercises[i], bundle.get(Constants.ACTIVE_TAB));

        }
    }

    //does the app open up to the last tab selected?
    @UiThreadTest
    public void testForLastSavedTab() {

        Random rand = new Random();
        int randInt = rand.nextInt(activity.getExerciseFragmentPagerAdapter().getExercises().length);
        activity.getExerciseFragmentPagerAdapter().getViewPager().setCurrentItem(randInt);

        activity.finish();
        ExerciseActivity newActivity = getActivity();
        assertEquals(newActivity
                .getExerciseFragmentPagerAdapter()
                .getViewPager()
                .getCurrentItem(), randInt);

    }


    //does the exercise fragment pre-populate the last weight saved?
    @UiThreadTest
    public void testForPopulationOfLastWeightUsed() {

        Random rand = new Random();
        int randInt = rand.nextInt(100);
        activity.getExerciseFragmentPagerAdapter().getViewPager().setCurrentItem(0);
        EditText weightEdit = (EditText) activity.findViewById(R.id.weight);
        weightEdit.setText(String.valueOf(randInt));
        Button logButton = (Button) activity.findViewById(R.id.log);
        logButton.performClick();
        activity.finish();
        ExerciseActivity newActivity = getActivity();
        EditText newWeightEdit = (EditText) newActivity.findViewById(R.id.weight);

        assertEquals(Integer.parseInt(newWeightEdit.getText().toString()), randInt);
    }

    //does the workout number in the database get incremented if the last workout was more than 24 hours ago
    public void testForWorkoutNumberIncremented() {

        insertRecord();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                activity.getExerciseFragmentPagerAdapter().getViewPager().setCurrentItem(0);
                EditText weightEdit = (EditText) activity.findViewById(R.id.weight);
                weightEdit.setText(String.valueOf(100));

                Chronometer chronometer = (Chronometer) activity.findViewById(R.id.timer);
                chronometer.start();

                Button logButton = (Button) activity.findViewById(R.id.log);
                logButton.performClick();

            }
        });

        getInstrumentation().waitForIdleSync();

        //check the database for a new record with the incremented workout number
        Cursor cursor = queryRecords();
        assertEquals(1, cursor.getCount());
        cursor.close();


    }

    private void insertRecord() {

        long time = (SystemClock.currentThreadTimeMillis() / 1000L) - 86500;
        DataProviderHelper helper = new DataProviderHelper(activity);
        SQLiteDatabase db = helper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(Constants.WORKOUT_NUMBER_COLUMN, 1);
        cv.put(Constants.DATETIME_COLUMN, time);
        cv.put(Constants.WEIGHT_COLUMN, 100);
        cv.put(Constants.TUL_COLUMN, 1000);
        cv.put(Constants.EXERCISE_COLUMN, "Chest Press");
        db.insert(Constants.LOGS_TABLE, null, cv);

    }

    private Cursor queryRecords() {

        DataProviderHelper helper = new DataProviderHelper(activity);
        SQLiteDatabase db = helper.getReadableDatabase();

        return db.query(Constants.LOGS_TABLE,
                new String[]{Constants.WEIGHT_COLUMN, Constants.TUL_COLUMN, Constants.EXERCISE_COLUMN},
                Constants.WORKOUT_NUMBER_COLUMN + " = 2",
                null, null, null, null);

    }


    private class DataProviderHelper extends SQLiteOpenHelper {

        public DataProviderHelper(Context context) {
            super(context,
                    Constants.TEST_DATABASE_NAME,
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
