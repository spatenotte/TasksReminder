package com.sampa.tasksreminder;

import org.joda.time.LocalDateTime;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

public class AlarmManagerHelper extends BroadcastReceiver {

	public static void cancelAlarm(PendingIntent intent, Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(intent);
	}

	public static void setAlarm(long id, Task task, PendingIntent intent,
			Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(intent);
		if (task.isDateReminderSet()) {
			am.set(AlarmManager.RTC_WAKEUP, task.getCompleteReminderDate()
					.toDate().getTime(), intent);
			TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(context,
					"DBTasks", null, 1);
			LocalDateTime nextAlarm = task.setNextAlarm();
			SQLiteDatabase db = taskHelper.getWritableDatabase();
			if (nextAlarm != null) {
				db.execSQL("UPDATE DBTasks " + "SET reminderDate='"
						+ task.getCompleteReminderDate().toString() + "'"
						+ "WHERE _id='" + task.getID() + "'");
			} else {
				db.execSQL("UPDATE DBTasks " + "SET useReminder='false'"
						+ "WHERE _id='" + task.getID() + "'");
			}
		} else {
			am.set(AlarmManager.RTC_WAKEUP, task.getCompleteDate().toDate()
					.getTime(), intent);
		}
	}

	public static void setAlarms(Context context) {
		TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(context, "DBTasks",
				null, 1);
		Task[] tasks = taskHelper.getAllTasks();
		if (tasks != null) {
			Task task;
			for (int i = 0; i < tasks.length; i++) {
				task = tasks[i];
				PendingIntent pIntent = taskHelper.createPendingIntent(context,
						task);
				setAlarm(task.getID(), task, pIntent, context);

			}
		}

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		setAlarms(context);
	}
}