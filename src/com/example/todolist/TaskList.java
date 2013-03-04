package com.example.todolist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import android.app.ListActivity;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class TaskList extends ListActivity implements LocationListener
{
	private static Cloudmine CM;
	private static HashMap<String, HashMap<String, String>> Tasks;
	private LocationManager locationManager;
	private String provider;
	float lat, lng;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_list);

		CM = new Cloudmine(this, getApplicationContext());

		getLocation();

		Intent intent = getIntent();
		Tasks = (HashMap<String, HashMap<String, String>>) intent
				.getSerializableExtra("Tasks");
		fillListView(Tasks);

		createHandlers();

	}

	public void createHandlers()
	{
		findViewById(R.id.addTaskButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						Intent intent = new Intent(TaskList.this, AddTask.class);
						startActivity(intent);
					}
				});

		findViewById(R.id.refreshButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						CM.fetchAllUserObjects();
					}
				});

		findViewById(R.id.logOutButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						CM.logOutUser();
					}
				});

		final ListView lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{
				findID(lv.getItemAtPosition(position).toString());
			}
		});
	}

	public void findID(String description)
	{
		Set<Entry<String, HashMap<String, String>>> entrySet = Tasks.entrySet();
		for (Entry<String, HashMap<String, String>> entry : entrySet)
		{
			if (entry.getValue().get("Description").endsWith(description))
			{
				CM.fetchObject(entry.getKey());
			}
			Log.d("TaskList EditTask", "id:" + entry.getKey());
		}
	}

	public void fillListView(HashMap<String, HashMap<String, String>> Tasks)
	{
		ArrayList<String> DisplayInfo = new ArrayList<String>();

		Set<Entry<String, HashMap<String, String>>> entrySet = Tasks.entrySet();
		for (Entry<String, HashMap<String, String>> entry : entrySet)
		{
			if (entry.getValue().get("CompletedFlag").equals("No")
					&& (isInArea(entry.getValue().get("Location"))))
				DisplayInfo.add(entry.getValue().get("Description"));
			Log.d("TaskList", "id:" + entry.getKey());
			Log.d("TaskList", "details:" + entry.getValue().get("Description"));
		}

		TaskList.this.setListAdapter(new ArrayAdapter<String>(TaskList.this,
				android.R.layout.simple_list_item_1, DisplayInfo));
	}

	public boolean isInArea(String location)
	{
		int radius = 1000; // meters
		int R = 6371;
		float lat1 = lat;
		float lon1 = lng;
		float lat2 = Float.parseFloat(location.split(";")[0].split(":")[1]);
		float lon2 = Float.parseFloat(location.split(";")[1].split(":")[1]);
		Log.d("Lat", Float.toString(lat2));
		System.out.println("Lat: " + Float.toString(lat2));
		Log.d("Lng", Float.toString(lon2));
		System.out.println("Lng: " + Float.toString(lon2));
		float dLat = (float) ((lat2 - lat1) * Math.PI / 180);
		float dLon = (float) ((lon2 - lon1) * Math.PI / 180);
		float a = (float) (Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math
				.cos(lat1 * Math.PI / 180)
				* Math.cos(lat2 * Math.PI / 180)
				* Math.sin(dLon / 2) * Math.sin(dLon / 2));
		float c = (float) (2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a)));
		float d = R * c;
		return (d * 1000 <= radius) ? true : false;
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
		getMenuInflater().inflate(R.menu.activity_task_list, menu);
		return true;
	}

	public void onStop()
	{
		super.onStop();
	}
}
