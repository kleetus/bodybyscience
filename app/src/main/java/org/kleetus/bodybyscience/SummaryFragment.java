package org.kleetus.bodybyscience;


import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
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
                Constants.WEIGHT_COLUMN,
                Constants.TUL_COLUMN,
                Constants.EXERCISE_COLUMN};

        adapter = new SummaryCursorAdapter(getActivity(), R.layout.summary_item, null,
                projection, new int[]{0, R.id.workout_number,
                R.id.workout_date, R.id.weight_summary, R.id.tul_summary, R.id.exercise_name}, 0);

        setListAdapter(adapter);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup viewGroup, Bundle savedState) {

        getLoaderManager().initLoader(Constants.SUMMARY_LOADER, null, this);
        return inflater.inflate(R.layout.summary_list, viewGroup, false);

    }

    @Override
    public void onPause() {

        super.onPause();
        getLoaderManager().destroyLoader(Constants.SUMMARY_LOADER);

    }

    @Override
    public void onResume() {

        super.onResume();
        getLoaderManager().restartLoader(Constants.SUMMARY_LOADER, null, this);

    }

    @Override
    public void onActivityCreated(Bundle bundle) {

        super.onActivityCreated(bundle);
        setupForMultiSelect();

    }

    private void setupForMultiSelect() {

        final ListView listView = getListView();

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                adapter.openEditMode();

            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {

                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.context_menu, menu);
                return true;

            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.menu_delete:
                        deleteItems();
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

                adapter.closeEditMode();

            }
        });

    }

    private void deleteItems() {

        if (adapter.checkedList.size() < 1) {
            return;
        }

        String toDeleteString = TextUtils.join(",", adapter.checkedList);

        getActivity().getContentResolver().delete(Constants.LOG_CONTENTURI,
                Constants.ROW_ID + " IN (" + toDeleteString + ")", null);

        adapter.checkedList.clear();

    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        return new CursorLoader(getActivity(), Constants.LOG_CONTENTURI, projection, null, null,
                Constants.DATETIME_COLUMN + getResources().getString(R.string.asc));

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
