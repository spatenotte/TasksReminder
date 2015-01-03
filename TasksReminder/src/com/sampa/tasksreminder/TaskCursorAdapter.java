package com.sampa.tasksreminder;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class TaskCursorAdapter extends CursorAdapter {

	public TaskCursorAdapter(Context context, Cursor c) {
		super(context, c, FLAG_REGISTER_CONTENT_OBSERVER);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView titleView = (TextView) view.findViewById(R.id.title);
		titleView.setText(cursor.getString(cursor.getColumnIndex("title")));

		String dateString = cursor.getString(cursor.getColumnIndex("date"));

		LocalDate date = LocalDate.parse(dateString.substring(0, 10));
		LocalTime time = LocalTime.parse(dateString.substring(11,
				dateString.length() - 1));

		DateTimeFormatter fmt = DateTimeFormat.forPattern("dd/MM/yyyy");
		TextView dateView = (TextView) view.findViewById(R.id.date);
		dateView.setText(date.toString(fmt));

		fmt = DateTimeFormat.forPattern("HH:mm");
		TextView timeView = (TextView) view.findViewById(R.id.time);
		timeView.setText(time.toString(fmt));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// when the view will be created for first time,
		// we need to tell the adapters, how each item will look
		LayoutInflater inflater = LayoutInflater.from(parent.getContext());
		return inflater.inflate(R.layout.single_task_item, parent, false);
	}
}