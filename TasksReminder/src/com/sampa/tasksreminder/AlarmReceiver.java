package com.sampa.tasksreminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		NotificationManager mNM;
		mNM = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// The PendingIntent to launch our activity if the user selects this
		// notification

		long id = intent.getLongExtra("taskReminder_id", -1);
		TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(context, "DBTasks",
				null, 1);
		Task task = taskHelper.populateTask(id);
		setNextAlarm(context, taskHelper, task);

		Intent newIntent = new Intent(context, ShowTaskActivity_.class);
		newIntent.putExtra("taskReminder_taskID", task.getID());
		PendingIntent pendingIntent = PendingIntent.getActivity(context,
				(int) task.getID(), newIntent, 0);
		// Set the icon, scrolling text and timestamp
		Builder builder = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_notification)
				.setContentTitle(task.getTitle())
				.setSound(
						RingtoneManager
								.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
				.setContentText(task.getDescription())
				.setContentIntent(pendingIntent);

		Notification notification = builder.build();
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		mNM.notify((int) task.getID(), notification);
	}

	private void setNextAlarm(Context context, TaskSQLiteHelper taskHelper,
			Task task) {
		// long id, Task task, PendingIntent intent, Context context
		PendingIntent pIntent = taskHelper.createPendingIntent(context, task);
		AlarmManagerHelper.setAlarm(task.getID(), task, pIntent, context);
	}
}
