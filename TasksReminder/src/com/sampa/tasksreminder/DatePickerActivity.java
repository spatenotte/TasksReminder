package com.sampa.tasksreminder;

import java.util.Date;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.joda.time.LocalDate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.timessquare.CalendarPickerView;

@EActivity
@OptionsMenu(R.menu.date_picker)
public class DatePickerActivity extends ActionBarActivity {

	Task task;
	@Extra("tasksReminder_dateCaller")
	byte caller;
	@ViewById
	CalendarPickerView calendar_view;

	@AfterViews
	public void initCalendar() {
		task = NewTaskActivity.task;
		if (caller == 1 && task.isDateSet()) {
			calendar_view.init(new Date(),
					LocalDate.now().plusYears(3).toDate()).withSelectedDate(
					task.getDate().toDate());
		} else if (caller == 2 && task.isDateReminderSet()) {
			calendar_view.init(new Date(),
					LocalDate.now().plusYears(3).toDate()).withSelectedDate(
					task.getReminderDate().toDate());
		} else {
			calendar_view.init(new Date(),
					LocalDate.now().plusYears(3).toDate()).withSelectedDate(
					new Date());
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_date_picker);
		initCalendar();
	}

	@OptionsItem
	void accept() {
		if (caller == 1) {
			task.setDate(LocalDate.fromDateFields(calendar_view
					.getSelectedDate()));
		} else
			task.setReminderDate(LocalDate.fromDateFields(calendar_view
					.getSelectedDate()));
		finish();
	}

	@OptionsItem
	void cancel() {
		finish();
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
			View rootView = inflater.inflate(R.layout.fragment_date_picker,
					container, false);
			return rootView;
		}
	}

}
