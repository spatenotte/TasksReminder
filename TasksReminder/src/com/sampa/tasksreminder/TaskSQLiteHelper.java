package com.sampa.tasksreminder;

import java.util.Arrays;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TaskSQLiteHelper extends SQLiteOpenHelper {

	String sqlCreate = "CREATE TABLE DBTasks (_id INTEGER PRIMARY KEY, title TEXT NOT NULL, "
			+ "description TEXT, date TEXT NOT NULL, "
			+ "useReminder BOOLEAN NOT NULL, reminderDate TEXT, freqReminder INTEGER, freqReminderUnit TINYINT)";
	Context context;

	public TaskSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.context = context;
	}

	public PendingIntent createPendingIntent(Context context, Task task) {
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra("taskReminder_id", task.getID());
		if (task.getUseReminder()) {
			intent.putExtra("taskReminder_reminderSet", true);
			return PendingIntent.getBroadcast(context, (int) task.getID(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
		} else {
			intent.putExtra("taskReminder_reminderSet", false);
			return PendingIntent.getBroadcast(context, (int) task.getID(),
					intent, PendingIntent.FLAG_UPDATE_CURRENT);
		}
	}

	public Cursor getAllData() {
		String buildSQL = "SELECT * FROM DBTasks ORDER BY date";
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor c = database.rawQuery(buildSQL, null);
		return c;
	}

	public Task[] getAllTasks() {
		Cursor cursor = getAllData();
		Task[] tasks = new Task[cursor.getCount()];

		cursor.moveToFirst();
		for (int i = 0; i < tasks.length; i++) {
			tasks[i] = populateTask(cursor);
			cursor.moveToNext();
		}

		return tasks;
	}

	public Task[] getAllTasksByDate(LocalDate date) {
		Cursor cursor = getAllData();
		Task[] tasks = new Task[cursor.getCount()];
		Task task;
		int itemCount = 0;

		cursor.moveToFirst();
		for (int i = 0; i < tasks.length; i++) {
			task = populateTask(cursor);
			if (task.getDate().equals(date)) {
				tasks[itemCount] = task;
				itemCount++;
			}
			cursor.moveToNext();
		}

		if (itemCount != 0) {
			 Task[] finalTasks = Arrays.copyOf(tasks, itemCount);
			return finalTasks;
		}

		else
			return new Task[0];
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(sqlCreate);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	public Task populateTask(Cursor c) {
		Task task = new Task();
		task.setID(c.getLong(c.getColumnIndex("_id")));

		task.setTitle(c.getString(c.getColumnIndex("title")));

		task.setDescription(c.getString(c.getColumnIndex("description")));

		LocalDateTime cal = stringToDate(c);
		task.setDate(cal.toLocalDate());
		task.setTime(cal.getHourOfDay(), cal.getMinuteOfHour());

		if (c.getInt(c.getColumnIndex("useReminder")) == 1) {

			task.setUseReminder(true);

			cal = stringToDateReminder(c);
			task.setReminderDate(cal.toLocalDate());
			task.setReminderTime(cal.getHourOfDay(), cal.getMinuteOfHour());

			task.setFrequency(c.getInt(c.getColumnIndex("freqReminder")));

			task.setFrequencyUnit((byte) c.getInt(c
					.getColumnIndex("freqReminderUnit")));
		}

		else
			task.setUseReminder(false);
		return task;
	}

	public Task populateTask(long id) {
		SQLiteDatabase database = this.getWritableDatabase();
		Cursor c = database.rawQuery("SELECT * FROM DBTasks WHERE _id=" + id,
				null);

		c.moveToFirst();
		Task task = new Task();
		task.setID(c.getLong(0));

		task.setTitle(c.getString(1));

		task.setDescription(c.getString(2));

		LocalDateTime cal = stringToDate(c);
		task.setDate(cal.toLocalDate());
		task.setTime(cal.getHourOfDay(), cal.getMinuteOfHour());

		if (c.getInt(4) == 1) {

			task.setUseReminder(true);

			cal = stringToDateReminder(c);
			task.setReminderDate(cal.toLocalDate());
			task.setReminderTime(cal.getHourOfDay(), cal.getMinuteOfHour());

			task.setFrequency(c.getInt(6));

			task.setFrequencyUnit((byte) c.getInt(c
					.getColumnIndex("freqReminderUnit")));
		}

		else
			task.setUseReminder(false);
		return task;
	}

	public LocalDateTime stringToDate(Cursor cursor) {
		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String dateString = cursor.getString(3);
		return LocalDateTime.parse(dateString, fmt);
	}

	public LocalDateTime stringToDateReminder(Cursor cursor) {
		DateTimeFormatter fmt = DateTimeFormat
				.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
		String dateString = cursor.getString(5);
		return LocalDateTime.parse(dateString, fmt);
	}
}