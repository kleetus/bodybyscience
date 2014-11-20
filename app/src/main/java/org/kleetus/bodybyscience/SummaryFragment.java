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
import android.widget.CheckBox;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNotNull;

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

    @Override
    public void onListItemClick(ListView l, View v, int i, long id) {

        SummaryItemDialogFragment frag = new SummaryItemDialogFragment();
        Cursor cursor = adapter.getCursor();
        cursor.moveToPosition(i);

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.WORKOUT_NUMBER_COLUMN,
                cursor.getInt(1));
        frag.setArguments(bundle);
        frag.show(getActivity().getFragmentManager(), null);
    }

    private void setupForMultiSelect() {

        final ListView listView = getListView();

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {

                setVisibility(View.VISIBLE);

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
                        deleteSelectedItems();
                        actionMode.finish();
                        return true;
                    default:
                        return false;
                }

            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

                setVisibility(View.GONE);

            }
        });

    }

    private void setVisibility(int visibility) {

        ListView lv = getListView();

        for (int i = 0; i < lv.getCount(); i++) {

            View view = lv.getChildAt(i);
            CheckBox cb = (CheckBox) view.findViewById(R.id.entry_checked);
            cb.setVisibility(visibility);

        }

    }

    private void deleteSelectedItems() {

        ListView lv = getListView();
        List<Integer> toDelete = new ArrayList<>();


        for (int i = 0; i < lv.getCount(); i++) {

            View view = lv.getChildAt(i);
            assertNotNull(view);
            CheckBox chk = (CheckBox) view.findViewById(R.id.entry_checked);

            if (chk.isChecked()) {

                Cursor cursor = adapter.getCursor();
                cursor.moveToPosition(i);
                toDelete.add(cursor.getInt(1));

            }

        }

        if (toDelete.size() > 0) {
            deleteItem(toDelete);
        }

    }

    private void deleteItem(List<Integer> toDelete) {

        String toDeleteString = TextUtils.join(",", toDelete);

        getActivity().getContentResolver().delete(Constants.LOG_CONTENTURI,
                Constants.WORKOUT_NUMBER_COLUMN + " IN ( " + toDeleteString + " )", null);

        getLoaderManager().restartLoader(Constants.SUMMARY_LOADER, null, this);

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
