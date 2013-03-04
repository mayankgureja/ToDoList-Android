package com.example.todolist;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private static Cloudmine CM;
	private LocationManager locationManager;
	private boolean onCreateRunned = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		onCreateRunned = true;

		CM = new Cloudmine(this, getApplicationContext());

		CM.initializeCredentials();

		createHandlers();
		enableGPS();
	}

	public void createHandlers()
	{
		EditText usernameText = (EditText) findViewById(R.id.usernameText);
		EditText passwordText = (EditText) findViewById(R.id.passwordText);

		usernameText.setText("test@test.com");
		passwordText.setText("test");

		findViewById(R.id.loginButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{

						EditText usernameText = (EditText) findViewById(R.id.usernameText);
						EditText passwordText = (EditText) findViewById(R.id.passwordText);

						// System.out.println(usernameText.getText().toString().trim()
						// + passwordText.getText().toString().trim());
						CM.logInUser(usernameText.getText().toString().trim(),
								passwordText.getText().toString().trim());
						Log.d("Order", "Main Activity LogInUser() done");
					}
				});

		findViewById(R.id.registerButton).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View arg0)
					{
						EditText usernameText = (EditText) findViewById(R.id.usernameText);
						EditText passwordText = (EditText) findViewById(R.id.passwordText);

						CM.createUser(usernameText.getText().toString().trim(),
								passwordText.getText().toString().trim());
						Log.d("Order", "Main Activity LogInUser() done");
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	public void enableGPS()
	{
		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(MainActivity.this, "GPS is enabled",
					Toast.LENGTH_LONG).show();
			// getLocation();
		}
		else
		{
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("To use this app, turn on GPS.")
					.setTitle("Enable GPS")
					.setCancelable(false)
					.setPositiveButton("Settings",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int id)
								{
									startActivityForResult(
											new Intent(
													Settings.ACTION_LOCATION_SOURCE_SETTINGS),
											1);
								}
							})
					.setNegativeButton("Skip",
							new DialogInterface.OnClickListener()
							{
								public void onClick(DialogInterface dialog,
										int id)
								{
									Toast.makeText(MainActivity.this,
											"GPS is NOT enabled",
											Toast.LENGTH_LONG).show();
									findViewById(R.id.loginButton)
											.setVisibility(View.INVISIBLE);
								}
							}).show();
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
		{
			Toast.makeText(MainActivity.this, "GPS is NOT enabled",
					Toast.LENGTH_LONG).show();
			findViewById(R.id.loginButton).setVisibility(View.INVISIBLE);
		}
		else
		{
			Toast.makeText(MainActivity.this, "GPS is enabled",
					Toast.LENGTH_LONG).show();
		}

	}

	@Override
	protected void onRestart()
	{
		super.onResume();
		setTitle(R.string.app_name);
		Log.d("Restart Main", "Restarting...");
		if (!onCreateRunned)
		{
			CM.logOutUser();
		}
		enableGPS();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		setTitle(R.string.app_name);
		Log.d("Resume Main", "Resuming...");
		if (!onCreateRunned)
		{
			CM.logOutUser();
		}
		enableGPS();

	}

}
