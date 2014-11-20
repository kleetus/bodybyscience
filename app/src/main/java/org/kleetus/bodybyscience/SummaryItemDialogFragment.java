package org.kleetus.bodybyscience;


import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SummaryItemDialogFragment extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private int workoutId;
    private CursorAdapter adapter;
    private String[] projection;
    private String title;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle saveInstanceState) {

        View view = layoutInflater.inflate(R.layout.workout_item_list, viewGroup, false);

        projection = new String[]{Constants.ROW_ID, Constants.EXERCISE_COLUMN, Constants.WEIGHT_COLUMN, Constants.TUL_COLUMN, Constants.DATETIME_COLUMN};

        Bundle bundle = getArguments();
        workoutId = bundle.getInt(Constants.WORKOUT_NUMBER_COLUMN);

        title = "Workout #" + workoutId;
        getDialog().setTitle(title);

        ListView listView = (ListView) view.findViewById(R.id.summary_list_view);

        adapter = new SimpleCursorAdapter(getActivity(),
                R.layout.workout_item,
                null,
                projection,
                new int[]{0, R.id.exercise_summary_item, R.id.weight_summary_item, R.id.tul_summary_item},
                0);

        listView.setAdapter(adapter);

        Button dismiss = (Button) view.findViewById(R.id.dismiss);

        dismiss.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                SummaryItemDialogFragment.this.dismiss();

            }

        });

        getLoaderManager().initLoader(Constants.SUMMARY_ITEM_LOADER, null, this);
        return view;

    }

    @Override
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(Constants.SUMMARY_ITEM_LOADER, null, this);

    }

    @Override
    public void onPause() {

        super.onResume();
        getLoaderManager().destroyLoader(Constants.SUMMARY_ITEM_LOADER);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(), Constants.LOG_CONTENTURI,
                projection,
                Constants.WORKOUT_NUMBER_COLUMN + " = ?",
                new String[]{String.valueOf(workoutId)},
                Constants.DATETIME_COLUMN + getResources().getString(R.string.asc));

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        getDialog().setTitle(title + " " + getCursorDates(cursor));
        adapter.swapCursor(cursor);

    }

    private String getCursorDates(Cursor cursor) {

        if(cursor.getCount() > 0) {

            cursor.moveToFirst();

            Date d = new Date(Long.parseLong(cursor.getString(4)) * 1000);

            SimpleDateFormat dateFormat = new SimpleDateFormat(getShortDate());
            return dateFormat.format(d);

        }

        return "";

    }

    private String getShortDate() {

        int date = LocaleManager.getInstance().useMetric() ? R.string.date_format_short_international :
                R.string.date_format_short;

        return getResources().getString(date);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        adapter.swapCursor(null);

    }
}