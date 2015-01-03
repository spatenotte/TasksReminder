package com.sampa.tasksreminder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.joda.time.LocalDate;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;

@EActivity
@OptionsMenu(R.menu.main)
public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (PlaceholderFragment.customAdapter != null)
			PlaceholderFragment.customAdapter
					.changeCursor(PlaceholderFragment.taskHelper.getAllData());
		if (PlaceholderFragment.cal != null)
			PlaceholderFragment.highlightDates();
	}

	@OptionsItem
	void newTask() {
		NewTaskActivity.task = new Task();
		Intent intent = new Intent(this, NewTaskActivity_.class);
		startActivity(intent);
	}

	public static class PlaceholderFragment extends Fragment {

		private static final String ARG_SECTION_NUMBER = "section_number";
		public static TaskCursorAdapter customAdapter;
		public static TaskSQLiteHelper taskHelper;
		public static ListView listView;
		public static CalendarPickerView cal;

		public PlaceholderFragment() {
		}

		public static void highlightDates() {
			Task[] tasks = taskHelper.getAllTasks();
			Collection<Date> dates = new ArrayList<Date>();
			for (int i = 0; i < tasks.length; i++) {
				if (!tasks[i].getDate().isBefore(new LocalDate()))
					dates.add(tasks[i].getDate().toDate());
			}

			cal.init(new Date(), new LocalDate().plusYears(3).toDate())
					.withHighlightedDates(dates);
		}

		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			taskHelper = new TaskSQLiteHelper(getActivity(), "DBTasks", null, 1);

			if (getArguments().getInt(ARG_SECTION_NUMBER) == 1)
				return initListView(inflater, container);
			else
				return initCalendarView(inflater, container);
		}

		private View initCalendarView(LayoutInflater inflater,
				ViewGroup container) {
			View calendarView = inflater.inflate(R.layout.calendar_view,
					container, false);
			cal = (CalendarPickerView) calendarView.findViewById(R.id.calendar);
			final TaskSQLiteHelper taskHelper = new TaskSQLiteHelper(
					getActivity(), "DBTasks", null, 1);
			final LinearLayout linearLayout = (LinearLayout) calendarView
					.findViewById(R.id.linearLayout);
			Task[] tasks = taskHelper.getAllTasks();
			Collection<Date> dates = new ArrayList<Date>();
			
			for (int i = 0; i < tasks.length; i++) {
				if (!tasks[i].getDate().isBefore(new LocalDate()))
					dates.add(tasks[i].getDate().toDate());
			}
			cal.init(new Date(), new LocalDate().plusYears(3).toDate())
					.withHighlightedDates(dates);

			cal.setOnDateSelectedListener(new OnDateSelectedListener() {

				@Override
				public void onDateSelected(Date date) {
					linearLayout.removeAllViews();
					LayoutInflater inflater = (LayoutInflater) getActivity()
							.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					Task[] tasks = taskHelper.getAllTasksByDate(new LocalDate(
							cal.getSelectedDate()));
					Task task;

					for (int i = 0; i < tasks.length; i++) {
						View view = inflater.inflate(R.layout.single_task_item,
								null);
						TextView titleView = (TextView) view
								.findViewById(R.id.title);
						TextView dateView = (TextView) view
								.findViewById(R.id.date);
						TextView timeView = (TextView) view
								.findViewById(R.id.time);

						task = tasks[i];

						titleView.setText(tasks[i].getTitle());
						dateView.setText(tasks[i].dateToString());
						timeView.setText(tasks[i].timeToString());

						linearLayout.addView(view);
					}
				}

				@Override
				public void onDateUnselected(Date date) {
					// TODO Auto-generated method stub

				}
			});
			return calendarView;
		}

		private View initListView(LayoutInflater inflater, ViewGroup container) {
			View taskListView = inflater.inflate(R.layout.task_list_view,
					container, false);
			listView = (ListView) taskListView.findViewById(R.id.taskListView);
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					newTask(position);
					Intent intent = new Intent(getActivity(),
							NewTaskActivity_.class);
					intent.putExtra("taskReminder_DBid", id);
					intent.putExtra("taskReminder_modifyTask", true);
					startActivity(intent);
				}
			});

			new Handler().post(new Runnable() {
				@Override
				public void run() {
					customAdapter = new TaskCursorAdapter(getActivity(),
							taskHelper.getAllData());
					listView.setAdapter(customAdapter);
				}
			});

			return taskListView;
		}

		private void newTask(int position) {
			Cursor cursor = (Cursor) listView.getItemAtPosition(position);
			NewTaskActivity.task = taskHelper.populateTask(cursor);
		}
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public Fragment getItem(int position) {
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.tasks).toUpperCase(l);
			case 1:
				return getString(R.string.calendar).toUpperCase(l);
			}
			return null;
		}
	}

}
