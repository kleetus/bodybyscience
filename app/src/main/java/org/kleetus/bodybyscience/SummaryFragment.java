package org.kleetus.bodybyscience;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SummaryFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private SummaryCursorAdapter adapter;
    private String[] projection;

    @Override
    public void onCreate(Bundle savedState) {

        super.onCreate(savedState);

        projection = new String[]{Constants.ROW_ID,
                Constants.WORKOUT_NUMBER_COLUMN,
                Constants.DATETIME_COLUMN,
                Constants.SUM_EXERCISE_WEIGHT,
                Constants.SUM_TUL};

        adapter = new SummaryCursorAdapter(getActivity(), R.layout.summary_item, null,
                projection, new int[]{0, R.id.workout_number,
                R.id.workout_date, R.id.weight_summary, R.id.tul_summary}, 0);

        setListAdapter(adapter);

        getLoaderManager().initLoader(1, null, this);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {

        return inflater.inflate(R.layout.summary_list, viewGroup, false);

    }

    @Override
    public void onPause() {

        super.onPause();
        getLoaderManager().destroyLoader(1);

    }

    @Override
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(1, null, this);

    }

    @Override
    public void onListItemClick(ListView l, View v, int i, long id) {

        SummaryItemDialogFragment frag = new SummaryItemDialogFragment();
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(i);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.WORKOUT_NUMBER_COLUMN,
                cursor.getInt(1));
        frag.setArguments(bundle);
        frag.show(getActivity().getFragmentManager(), "tag");
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(), Constants.LOG_CONTENTURI, projection, "", null,
                Constants.DATETIME_COLUMN + " ASC");

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

        adapter.swapCursor(cursor);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

        adapter.swapCursor(null);

    }
}
