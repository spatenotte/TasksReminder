package com.sampa.tasksreminder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

@EActivity
public class ShowTaskActivity extends Activity {

	@ViewById
	TextView title;
	@ViewById
	TextView titleText;
	@ViewById
	TextView description;
	@ViewById
	TextView descriptionText;
	@ViewById
	TextView endTask;
	@ViewById
	TextView endTaskText;
	@ViewById
	Button cancelButton;
	@ViewById
	Button deleteTaskButton;

	long taskID;
	TaskSQLiteHelper taskHelper;

	@Click(R.id.cancelButton)
	public void cancel() {
		finish();
	}

	@Click(R.id.deleteTaskButton)
	public void deleteTask() {
		SQLiteDatabase db = taskHelper.getWritableDatabase();
		db.execSQL("DELETE FROM DBTasks WHERE _id=" + taskID);
		db.close();
		finish();
	}

	@AfterViews
	public void init() {
		taskHelper = new TaskSQLiteHelper(this, "DBTasks", null, 1);
		taskID = getIntent().getLongExtra("taskReminder_taskID", -1);
		Task task = taskHelper.populateTask(taskID);
		title.setText(getString(R.string.title));
		titleText.setText(task.getTitle() + "\n");
		description.setText(getString(R.string.description));
		descriptionText.setText(task.getDescription() + "\n");
		endTask.setText(getString(R.string.date));
		String endTask = task.dateToString() + " ";
		endTask += getString(R.string.time) + " ";
		endTask += task.timeToString() + "\n";
		endTaskText.setText(endTask);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_task);
		init();
	}
}