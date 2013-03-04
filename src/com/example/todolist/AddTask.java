package com.example.todolist;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddTask extends Activity implements LocationListener
{
	private static Cloudmine CM;
	private LocationManager locationManager;
	private String provider;
	float lat, lng;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_task);

		CM = new Cloudmine(this, getApplicationContext());

		getLocation();
		createHandlers();

	}

	public void createHandlers()
	{
		EditText dueDateText = (EditText) findViewById(R.id.dueDateText);
		dueDateText.setInputType(InputType.TYPE_NULL);

		TextView locationText = (TextView) findViewById(R.id.locationText);
		locationText.setText("Lat:" + lat + ";Lng:" + lng);

		dueDateText.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				selectDate(v);
			}
		});

		findViewById(R.id.addTaskButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						EditText descriptionText = (EditText) findViewById(R.id.descriptionText);
						String description = descriptionText.getText()
								.toString().trim();

						EditText categoryText = (EditText) findViewById(R.id.categoryText);
						String category = categoryText.getText().toString()
								.trim();

						EditText dueDateText = (EditText) findViewById(R.id.dueDateText);
						String dueDate = dueDateText.getText().toString()
								.trim();

						TextView locationText = (TextView) findViewById(R.id.locationText);
						String location = locationText.getText().toString()
								.trim();

						EditText notesText = (EditText) findViewById(R.id.notesText);
						String notes = notesText.getText().toString().trim();

						// System.out.println(usernameText.getText().toString().trim()
						// + passwordText.getText().toString().trim());
						CM.addTask(description, category, dueDate, location,
								notes);
						Log.d("Order", "AddTask() done");
					}
				});
	}

	public void selectDate(View view)
	{
		DialogFragment newFragment = new SelectDateFragment();
		newFragment.show(getFragmentManager(), "DatePicker");
	}

	public void populateSetDate(int year, int month, int day)
	{
		EditText dueDateText = (EditText) findViewById(R.id.dueDateText);
		dueDateText.setText(month + "/" + day + "/" + year);
	}

	public class SelectDateFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener
	{
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState)
		{
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}

		public void onDateSet(DatePicker view, int yy, int mm, int dd)
		{
			populateSetDate(yy, mm + 1, dd);
		}
	}

	public void getLocation()
	{
		locationManager = (LocationManager) getSystemService(MainActivity.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);

		if (location != null)
		{
			System.out.println("Provider " + provider + " has been selected.");
			onLocationChanged(location);
		}
	}

	@Override
	public void onLocationChanged(Location location)
	{
		lat = (float) (location.getLatitude());
		lng = (float) (location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0)
	{
		Toast.makeText(this, "Disabled provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String arg0)
	{
		Toast.makeText(this, "Enabled new provider " + provider,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2)
	{
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_add_task, menu);
		return true;
	}

}
