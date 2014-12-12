package org.kleetus.bodybyscience;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SummaryCursorAdapter extends SimpleCursorAdapter {

    private int layout;
    private boolean showCheckBox = false;
    public List<Integer> checkedList;

    public SummaryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {

        super(context, layout, c, from, to, flags);
        this.layout = layout;
        checkedList = new ArrayList<>();

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(layout, null);

    }

    @Override
    public void bindView(@NonNull View view, final Context context, @NonNull final Cursor cursor) {

        long date = cursor.getLong(2);
        final Integer id = cursor.getInt(0);

        int date_int = LocaleManager.getInstance().useMetric() ? R.string.date_format_international :
                R.string.date_format;

        String dateString = new SimpleDateFormat(context.getString(date_int)).format(new Date(date * 1000));

        CheckBox editCheckBox = (CheckBox) view.findViewById(R.id.entry_checked);

        if (showCheckBox) {

            editCheckBox.setVisibility(View.VISIBLE);

        } else {

            editCheckBox.setVisibility(View.GONE);
            checkedList.clear();

        }

        editCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                if (checkedList.contains(id)) {

                    checkedList.remove(id);

                } else {

                    checkedList.add(id);

                }

            }
        });

        if (checkedList.contains(id)) {

            editCheckBox.setChecked(true);

        } else {

            editCheckBox.setChecked(false);

        }

        TextView index = (TextView) view.findViewById(R.id.summary_row_index);
        TextView dateView = (TextView) view.findViewById(R.id.workout_date);
        TextView workoutNumber = (TextView) view.findViewById(R.id.workout_number);
        TextView weight = (TextView) view.findViewById(R.id.weight_summary);
        TextView tul = (TextView) view.findViewById(R.id.tul_summary);
        TextView exerciseName = (TextView) view.findViewById(R.id.exercise_name);

        index.setText(String.valueOf(cursor.getPosition() + 1));
        exerciseName.setText(cursor.getString(5));
        dateView.setText(dateString);
        workoutNumber.setText("Workout: " + cursor.getInt(1));
        int format = LocaleManager.getInstance().useMetric() ? R.string.weight_format_metric : R.string.weight_format;
        weight.setText(cursor.getInt(3) + context.getString(format));
        tul.setText(cursor.getInt(4) / 1000.0 + context.getString(R.string.seconds_total));

    }

    public void openEditMode() {

        showCheckBox = true;
        notifyDataSetChanged();

    }

    public void closeEditMode() {

        showCheckBox = false;
        notifyDataSetChanged();

    }
}
