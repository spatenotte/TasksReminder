package com.sampa.tasksreminder;

import java.util.Calendar;

import org.androidannotations.annotations.EFragment;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

@EFragment
public class TimePickerFragment extends DialogFragment implements
		TimePickerDialog.OnTimeSetListener {

	Task task;
	byte caller;

	public TimePickerFragment() {
	}

	public void init(Task task, byte caller) {
		this.task = NewTaskActivity.task;
		this.caller = caller;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (caller == 1 && task.isTimeSet()) {
			return new TimePickerDialog(getActivity(), this, task.getTime()
					.getHourOfDay(), task.getTime().getMinuteOfHour(),
					DateFormat.is24HourFormat(getActivity()));
		} else if (caller == 2 && task.isTimeReminderSet()) {
			return new TimePickerDialog(getActivity(), this, task
					.getReminderTime().getHourOfDay(), task.getReminderTime()
					.getMinuteOfHour(),
					DateFormat.is24HourFormat(getActivity()));
		} else {
			// Use the current time as the default values for the picker
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
			// Create a new instance of TimePickerDialog and return it
			return new TimePickerDialog(getActivity(), this, hour, minute,
					DateFormat.is24HourFormat(getActivity()));
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		NewTaskActivity.timeSet(caller);
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		if (caller == 1) {
			task.setTime(hourOfDay, minute);
		} else {
			task.setReminderTime(hourOfDay, minute);
		}
	}
}