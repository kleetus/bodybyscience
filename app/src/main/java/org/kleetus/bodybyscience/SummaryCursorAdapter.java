package org.kleetus.bodybyscience;


import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SummaryCursorAdapter extends SimpleCursorAdapter {

    private int layout;

    public SummaryCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {

        super(context, layout, c, from, to, flags);
        this.layout = layout;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        return LayoutInflater.from(context).inflate(layout, null);

    }

    @Override
    public void bindView(@NonNull View view, final Context context, @NonNull Cursor cursor) {

        super.bindView(view, context, cursor);

        long date = cursor.getLong(2);

        String dateString = new SimpleDateFormat("MM/dd/yy").format(new Date(date * 1000));

        TextView dateView = (TextView) view.findViewById(R.id.workout_date);
        TextView workoutNumber = (TextView) view.findViewById(R.id.workout_number);
        TextView weight = (TextView) view.findViewById(R.id.weight_summary);
        TextView tul = (TextView) view.findViewById(R.id.tul_summary);

        dateView.setText(dateString);
        workoutNumber.setText("#" + cursor.getInt(1));
        weight.setText(cursor.getInt(3) + "lbs");
        tul.setText(cursor.getInt(4)/1000.0 + " sec total");

    }
}