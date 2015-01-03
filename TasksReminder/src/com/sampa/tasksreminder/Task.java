package com.sampa.tasksreminder;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task {

	long id;
	String title, description;
	LocalDate date, dateReminder;
	LocalTime time, timeReminder;
	int frequency;
	/*
	 * 0 -> hours 1 -> days 2-> weeks 3 -> months 4 -> years
	 */
	byte frequencyUnit;
	boolean dateSet, timeSet, dateReminderSet, timeReminderSet;
	boolean useReminder;

	public Task() {
	}

	public Task(String title, String description, LocalDateTime date) {
		super();
		this.title = title;
		this.description = description;
		this.date = date.toLocalDate();
		this.time = date.toLocalTime();
		this.dateSet = true;
		this.timeSet = true;
		this.useReminder = false;
	}

	public Task(String title, String description, LocalDateTime date,
			LocalDateTime dateReminder, int frequency, byte frequencyUnit) {
		this.title = title;
		this.description = description;
		this.date = date.toLocalDate();
		this.time = date.toLocalTime();
		this.dateReminder = dateReminder.toLocalDate();
		this.timeReminder = dateReminder.toLocalTime();
		this.frequency = frequency;
		this.frequencyUnit = frequencyUnit;
		this.dateSet = true;
		this.timeSet = true;
		this.dateReminderSet = true;
		this.timeReminderSet = true;
		this.useReminder = true;
	}

	public String dateReminderToString() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd MMMM yyyy");
		return dateReminder.toString(fmt);
	}

	public String dateToString() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("EEEE dd MMMM yyyy");
		return date.toString(fmt);
	}

	public LocalDateTime getCompleteDate() {
		LocalDateTime date = new LocalDateTime();
		date = date.withFields(this.getDate());
		return date = date.withFields(this.getTime());
	}

	public LocalDateTime getCompleteReminderDate() {
		LocalDateTime date = new LocalDateTime();
		date = date.withFields(this.getReminderDate());
		return date = date.withFields(this.getReminderTime());
	}

	public LocalDate getDate() {
		return date;
	}

	public String getDescription() {
		return description;
	}

	public int getFrequency() {
		return frequency;
	}

	public byte getFrequencyUnit() {
		return frequencyUnit;
	}

	public long getID() {
		return id;
	}

	public LocalDate getReminderDate() {
		return dateReminder;
	}

	public LocalTime getReminderTime() {
		return timeReminder;
	}

	public LocalTime getTime() {
		return time;
	}

	public String getTitle() {
		return title;
	}

	public boolean getUseReminder() {
		return useReminder;
	}

	public boolean isDateReminderSet() {
		return dateReminderSet;
	}

	public boolean isDateSet() {
		return dateSet;
	}

	public boolean isTimeReminderSet() {
		return timeReminderSet;
	}

	public boolean isTimeSet() {
		return timeSet;
	}

	public String reminderDateToString() {
		return dateReminder.toString("EEEE dd MMMM yyyy");
	}

	public void setDate(LocalDate dateSelected) {
		dateSet = true;
		this.date = dateSelected;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public void setFrequencyUnit(byte frequencyUnit) {
		this.frequencyUnit = frequencyUnit;
	}

	public void setID(long id) {
		this.id = id;
	}

	public LocalDateTime setNextAlarm() {
		LocalDateTime date = getCompleteReminderDate();
		switch (frequencyUnit) {
		case 0:
			date = date.plusHours(getFrequency());
			break;
		case 1:
			date = date.plusDays(getFrequency());
			break;
		case 2:
			date = date.plusWeeks(getFrequency());
			break;
		case 3:
			date = date.plusMonths(getFrequency());
			break;
		case 4:
			date = date.plusYears(getFrequency());
			break;
		}

		if (date.isAfter(getCompleteDate())) {
			useReminder = false;
			return null;
		}

		dateReminder = date.toLocalDate();
		timeReminder = date.toLocalTime();
		return date;
	}

	public void setReminderDate(LocalDate reminderSelected) {
		dateReminderSet = true;
		this.dateReminder = reminderSelected;
	}

	public void setReminderTime(int hour, int minute) {
		timeReminderSet = true;
		timeReminder = new LocalTime(hour, minute);
	}

	public void setTime(int hour, int minute) {
		timeSet = true;
		time = new LocalTime(hour, minute);
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setUseReminder(boolean useReminder) {
		this.useReminder = useReminder;
	}

	public String timeReminderToString() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
		return timeReminder.toString(fmt);
	}

	public String timeToString() {
		DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm");
		return time.toString(fmt);
	}

}
