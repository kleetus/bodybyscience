package org.kleetus.bodybyscience;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

public class ExerciseFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private Boolean startButtonState = false;
    private long timeOnClock = 0;
    private Chronometer chrono;
    private Button startButton;
    private long timePaused = 0;
    private EditText weight;
    private String activeTab;
    private int workoutNumber = 1;

    @Override
    public void onCreate(Bundle state) {

        super.onCreate(state);

        Bundle args = getArguments();
        activeTab = args.getString(Constants.ACTIVE_TAB, "Chest Press");
        getLoaderManager().initLoader(0, null, this);

    }

    @Override
    public void onResume() {

        super.onResume();

        if (timePaused > 0) {

            chrono.setBase(timePaused - timeOnClock);
            timePaused = 0;

        } else {

            chrono.setBase(SystemClock.elapsedRealtime() - timeOnClock);

        }


        if (startButtonState) {

            startButton.setText(Constants.STOP);
            chrono.start();
        }

        getLoaderManager().restartLoader(0, null, this);

    }

    @Override
    public void onPause() {

        super.onPause();

        if (startButtonState) {

            timePaused = SystemClock.elapsedRealtime();
            timeOnClock = timePaused - chrono.getBase();

        }

        getLoaderManager().destroyLoader(0);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(Constants.TIME_ON_CLOCK, timeOnClock);
        outState.putBoolean(Constants.START_BUTTON_STATE, startButtonState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle state) {

        View fragmentView = inflater.inflate(R.layout.exercise, viewGroup, false);

        startButton = (Button) fragmentView.findViewById(R.id.start);
        weight = (EditText) fragmentView.findViewById(R.id.weight);
        chrono = (Chronometer) fragmentView.findViewById(R.id.timer);

        Button logButton = (Button) fragmentView.findViewById(R.id.log);

        Button resetButton = (Button) fragmentView.findViewById(R.id.reset);

        Button plusButton = (Button) fragmentView.findViewById(R.id.increase_button);
        Button minusButton = (Button) fragmentView.findViewById(R.id.decrease_button);


        if (null != state) {

            Boolean shouldStart = state.getBoolean(Constants.START_BUTTON_STATE);
            timeOnClock = state.getLong(Constants.TIME_ON_CLOCK);

            if (shouldStart) {
                startChrono();
            } else {
                stopChrono();
            }

        }

        logButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                stopChrono();
                saveFormInfo();

            }

        });


        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (startButtonState) {

                    stopChrono();

                } else {

                    startChrono();

                }
            }

        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chrono.setBase(SystemClock.elapsedRealtime());
                timeOnClock = 0;
                timePaused = 0;

            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment(view);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                increment(view);

            }
        });

        return fragmentView;
    }

    private void stopChrono() {

        if (!startButtonState) {
            return;
        }

        startButtonState = false;
        startButton.setText(Constants.RESTART);
        chrono.stop();
        timeOnClock = SystemClock.elapsedRealtime() - chrono.getBase();

    }

    private void startChrono() {

        if (startButtonState) {
            return;
        }

        startButtonState = true;
        startButton.setText(Constants.STOP);
        chrono.setBase(SystemClock.elapsedRealtime() - timeOnClock);
        chrono.start();

    }


    private void saveFormInfo() {

        if (timeOnClock == 0 || getWeight() == 0) {

            Toast.makeText(getActivity(), getResources().getString(R.string.exercise_not_saved), Toast.LENGTH_LONG).show();
            return;

        }

        getActivity().getContentResolver().insert(Constants.LOG_CONTENTURI, getFormValues());

        Toast.makeText(getActivity(), getResources().getString(R.string.exercise_saved), Toast.LENGTH_LONG).show();

    }

    private ContentValues getFormValues() {

        ContentValues cv = new ContentValues();
        cv.put(Constants.WEIGHT_COLUMN, getWeight());
        cv.put(Constants.TUL_COLUMN, timeOnClock);
        cv.put(Constants.EXERCISE_COLUMN, activeTab);
        return cv;

    }

    private int getWeight() {

        String ret = weight.getText().toString();
        if (ret.length() > 0) {
            return Integer.parseInt(ret);
        } else {
            return 0;
        }

    }

    public void increment(View view) {

        String weightEntry = weight.getText().toString();

        if (weightEntry.length() < 1) {
            return;
        }

        int incrementValue;

        switch (view.getId()) {

            case R.id.decrease_button:
                incrementValue = -5;
                break;
            default:
                incrementValue = 5;

        }

        int newWeight = Integer.parseInt(weightEntry) + incrementValue;

        if (newWeight < 0) {

            newWeight = 0;

        }

        weight.setText(String.valueOf(newWeight));

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(), Constants.LOG_CONTENTURI,
                new String[]{Constants.WEIGHT_COLUMN, Constants.WORKOUT_NUMBER_COLUMN, Constants.DATETIME_COLUMN},
                Constants.EXERCISE_COLUMN + " = ?",
                new String[]{activeTab},
                Constants.DATETIME_COLUMN + " DESC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        if (cursor.getCount() < 1) {
            return;
        }

        cursor.moveToFirst();

        if (null != weight) {

            weight.setText(String.valueOf(cursor.getInt(0)));

        }

        workoutNumber = cursor.getInt(1);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
}
