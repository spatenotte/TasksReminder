package com.sampa.tasksreminder;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.CheckedChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@EActivity
@OptionsMenu(R.menu.new_task)
public class NewTaskActivity extends ActionBarActivity {

	public static Task task;

	@ViewById
	static EditText titleInput;
	@ViewById
	static EditText descriptionInput;
	@ViewById
	static Button dateButton;
	@ViewById
	static Button timeButton;
	@ViewById
	static Button dateReminderButton;
	@ViewById
	static Button timeReminderButton;
	@ViewById
	static EditText reminderInput;
	@ViewById
	static Spinner reminderSpinner;
	@ViewById
	static CheckBox toggleReminderCheckBox;
	@ViewById
	TextView startReminderText;
	@ViewById
	TextView reminderFrequencyText;
	@ViewById
	static Button deleteButton;

	public NewTaskActivity() {

	}

	public static void setButtons() {
		titleInput.setText(task.getTitle());
		descriptionInput.setText(task.getDescription());
		dateButton.setText(task.dateToString());
		timeButton.setText(task.timeToString());
		if (task.getUseReminder()) {
			toggleReminderCheckBox.setChecked(true);
			dateReminderButton.setText(task.dateReminderToString());
			timeReminderButton.setText(task.timeReminderToString());
			reminderInput.setText(Integer.toString(task.getFrequency()));
			reminderSpinner.setSelection(task.getFrequencyUnit());
		} else {
			toggleReminderCheckBox.setChecked(false);
		}
		deleteButton.setVisibility(View.VISIBLE);
	}

	public static void timeSet(int caller) {
		if (caller == 1 && task.isTimeSet())
			timeButton.setText(task.timeToString());
		else if (caller == 2 && task.isTimeReminderSet())
			timeReminderButton.setText(task.timeReminderToString());
	}

	@Click(R.id.deleteButton)
	public void deleteTask() {
		TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(
				getApplicationContext(), "DBTasks", null, 1);
		SQLiteDatabase db = taskHelper.getWritableDatabase();
		PendingIntent pIntent = taskHelper.createPendingIntent(
				getApplicationContext(), task);
		AlarmManagerHelper.cancelAlarm(pIntent, getApplicationContext());
		db.execSQL("DELETE FROM DBTasks WHERE _id=" + task.getID());
		Toast.makeText(getApplicationContext(), R.string.task_deleted,
				Toast.LENGTH_LONG).show();
		finish();
	}

	private boolean isEmpty(EditText field) {
		return field.getText().toString().trim().length() == 0;
	}

	private void setAlarm(long id) {
		TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(
				getApplicationContext(), "DBTasks", null, 1);
		Task task = taskHelper.populateTask(id);
		PendingIntent pendingIntent = taskHelper.createPendingIntent(
				getApplicationContext(), task);
		AlarmManagerHelper.setAlarm(id, task, pendingIntent,
				getApplicationContext());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_new_task);
		if (task == null)
			task = new Task();
		boolean modifyTask = getIntent().getBooleanExtra(
				"taskReminder_modifyTask", false);
		if (modifyTask) {
			setButtons();
			this.setTitle(R.string.modify_task);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (task.isDateSet())
			dateButton.setText(task.dateToString());
		if (task.isDateReminderSet())
			dateReminderButton.setText(task.dateReminderToString());
	}

	@OptionsItem
	void accept() {
		boolean error = false;

		if (isEmpty(titleInput)) {
			titleInput.setError(getString(R.string.no_title));
			error = true;
			return;
		} else if (!task.isDateSet()) {
			dateButton.setError(getString(R.string.no_date));
			error = true;
			return;
		} else if (!task.isTimeSet()) {
			timeButton.setError(getString(R.string.no_time));
			error = true;
			return;
		}
		if (task.getDate().isBefore(new LocalDate())) {
			dateButton.setError(getString(R.string.date_time_before_today));
			error = true;
			return;
		}
		if (task.getCompleteDate().isBefore(new LocalDateTime())) {
			timeButton.setError(getString(R.string.date_time_before_today));
			error = true;
			return;
		}
		if (toggleReminderCheckBox.isChecked()) {
			if (!task.isDateReminderSet()) {
				dateReminderButton.setError(getString(R.string.no_date));
				error = true;
				return;
			} else if (!task.isTimeReminderSet()) {
				timeReminderButton.setError(getString(R.string.no_time));
				error = true;
				return;
			} else if (task.getCompleteReminderDate().isAfter(
					task.getCompleteDate())
					|| task.getCompleteReminderDate().isBefore(
							new LocalDateTime())) {
				timeReminderButton
						.setError(getString(R.string.reminder_date_time_before_today));
				error = true;
			}
		}
		if (!error) {
			long id = saveDB();
			if (id != -1) {
				task.setID(id);
				setAlarm(id);
				finish();
			} else
				Toast.makeText(getApplicationContext(), R.string.fail_save_db,
						Toast.LENGTH_LONG).show();
		}
	}

	@OptionsItem
	void cancel() {
		finish();
	}

	@CheckedChange(R.id.toggleReminderCheckBox)
	void checkedChangedOnHelloCheckBox() {
		boolean checked = toggleReminderCheckBox.isChecked();
		startReminderText.setEnabled(checked);
		dateReminderButton.setEnabled(checked);
		timeReminderButton.setEnabled(checked);
		reminderFrequencyText.setEnabled(checked);
		reminderInput.setEnabled(checked);
		reminderSpinner.setEnabled(checked);
	}

	@Click(R.id.dateButton)
	void chooseDate() {
		dateButton.setError(null);
		if (task.isDateSet() && task.getDate().isBefore(new LocalDate()))
			task.setDate(new LocalDate());
		Intent intent = new Intent(this, DatePickerActivity_.class);
		intent.putExtra("tasksReminder_dateCaller", (byte) 1);
		startActivity(intent);
	}

	@Click(R.id.dateReminderButton)
	void chooseReminderDate() {
		dateReminderButton.setError(null);
		if (task.isDateReminderSet()
				&& task.getReminderDate().isBefore(new LocalDate()))
			task.setReminderDate(new LocalDate());
		Intent intent = new Intent(this, DatePickerActivity_.class);
		intent.putExtra("tasksReminder_dateCaller", (byte) 2);
		startActivity(intent);
	}

	@Click(R.id.timeReminderButton)
	void chooseReminderTime() {
		timeReminderButton.setError(null);
		TimePickerFragment timeFragment = new TimePickerFragment_();
		timeFragment.init(task, (byte) 2);
		timeFragment.show(getSupportFragmentManager(), "timePicker");
	}

	@Click(R.id.timeButton)
	void chooseTime() {
		timeButton.setError(null);
		TimePickerFragment timeFragment = new TimePickerFragment_();
		timeFragment.init(task, (byte) 1);
		timeFragment.show(getSupportFragmentManager(), "timePicker");
	}

	@AfterViews
	void init() {
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
				this, R.array.spinnerRemindersOptions,
				android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		reminderSpinner.setAdapter(adapter);
		titleInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS
				| InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
	}

	long saveDB() {
		long id = getIntent().getLongExtra("taskReminder_DBid", -1);
		TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(this, "DBTasks",
				null, 1);
		SQLiteDatabase db = taskHelper.getWritableDatabase();

		ContentValues newTask = new ContentValues();
		newTask.put("title", titleInput.getText().toString().trim());
		if (!isEmpty(descriptionInput))
			newTask.put("description", descriptionInput.getText().toString()
					.trim());
		else
			newTask.put("description", "");
		newTask.put("date", task.getCompleteDate().toString());
		if (toggleReminderCheckBox.isChecked()) {
			newTask.put("useReminder", true);
			newTask.put("reminderDate", task.getCompleteReminderDate()
					.toString());
			newTask.put("freqReminder",
					Integer.parseInt((reminderInput.getText().toString())));
			newTask.put("freqReminderUnit",
					reminderSpinner.getSelectedItemPosition());
		} else {
			newTask.put("useReminder", false);
			newTask.putNull("reminderDate");
			newTask.putNull("freqReminder");
			newTask.putNull("freqReminderUnit");
		}

		if (id != -1) {
			newTask.put("_id", id);
			db.update("DBTasks", newTask, "_id=" + id, null);
		} else {
			id = db.insert("DBTasks", null, newTask);
			Cursor c = db.rawQuery("SELECT * FROM DBtasks WHERE rowid=" + id,
					null);
			c.moveToFirst();
			id = c.getLong(0);
		}
		return id;
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_new_task,
					container, false);
			return rootView;
		}
	}
}
