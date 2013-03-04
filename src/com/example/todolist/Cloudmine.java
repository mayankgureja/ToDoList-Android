package com.example.todolist;

import java.util.HashMap;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.cloudmine.api.CMApiCredentials;
import com.cloudmine.api.CMObject;
import com.cloudmine.api.CMUser;
import com.cloudmine.api.SimpleCMObject;
import com.cloudmine.api.rest.CMStore;
import com.cloudmine.api.rest.callbacks.CMObjectResponseCallback;
import com.cloudmine.api.rest.callbacks.CMResponseCallback;
import com.cloudmine.api.rest.callbacks.CreationResponseCallback;
import com.cloudmine.api.rest.callbacks.LoginResponseCallback;
import com.cloudmine.api.rest.callbacks.ObjectModificationResponseCallback;
import com.cloudmine.api.rest.response.CMObjectResponse;
import com.cloudmine.api.rest.response.CMResponse;
import com.cloudmine.api.rest.response.CreationResponse;
import com.cloudmine.api.rest.response.LoginResponse;
import com.cloudmine.api.rest.response.ObjectModificationResponse;
import com.cloudmine.api.rest.response.code.CMResponseCode;

public class Cloudmine
{
	private static Context context;
	private static Activity activity;
	private static final String APP_ID = "b44071ec3fe14f74abe36a8f38c9844c";
	private static final String TEST_KEY = "79fde2353729423d8985c93bf930a96b";
	private static CMUser user;

	public Cloudmine(Activity activity, Context context)
	{
		Log.d("Order", "Cloudmine Constructor");
		Cloudmine.context = context;
		Cloudmine.activity = activity;
	}

	public void initializeCredentials()
	{
		Log.d("Order", "Cloudmine Initialize Credentials");
		// This will initialize your credentials
		CMApiCredentials.initialize(APP_ID, TEST_KEY, context);
	}

	public void logInUser(String username, String password)
	{
		Log.d("Order", "Cloudmine logInUser");
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Logging in...");
		mDialog.setCancelable(false);
		mDialog.show();

		user = new CMUser(username, password);
		user.login(new LoginResponseCallback()
		{
			public void onCompletion(LoginResponse response)
			{
				if (response.wasSuccess())
				{
					Log.d("logInUser", "User login was a success!");
					Log.d("logInUser", "Now our user has a session token? "
							+ user.isLoggedIn());

					// configure the store with the user so that it can perform
					// user-centric operations
					CMStore.getStore().setUser(user);

					Toast.makeText(context, "Login successful",
							Toast.LENGTH_LONG).show();
					if (mDialog.isShowing())
						mDialog.dismiss();
					fetchAllUserObjects();
				}
				else
				{
					Log.d("logInUser", "We failed to log in because of: "
							+ response.getResponseCode());
					Toast.makeText(context, "Login unsuccessful",
							Toast.LENGTH_LONG).show();
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
			}

			public void onFailure(Throwable t, String msg)
			{
				Log.d("logInUser", "The call was never made because of: " + msg);
				Toast.makeText(context, "Login unsuccessful", Toast.LENGTH_LONG)
						.show();
				if (mDialog.isShowing())
					mDialog.dismiss();
			}
		});

	}

	public void logOutUser()
	{
		Log.d("Order", "Cloudmine logOutUser");
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Logging out...");
		mDialog.setCancelable(false);
		mDialog.show();

		user.logout(new CMResponseCallback()
		{
			public void onCompletion(CMResponse response)
			{
				if (response.wasSuccess())
				{
					Log.d("logOutUser",
							"We logged out the user: " + !user.isLoggedIn());

					Toast.makeText(context, "Logout successful",
							Toast.LENGTH_LONG).show();
					if (mDialog.isShowing())
						mDialog.dismiss();

					Intent intent = new Intent(context, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
				}
				else
				{
					Log.d("logOutUser",
							"We failed because of: "
									+ response.getResponseCode());

					Toast.makeText(context, "Logout unsuccessful",
							Toast.LENGTH_LONG).show();
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
			}
		});
	}

	public void createUser(final String username, final String password)
	{
		Log.d("Order", "Cloudmine createUser");
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Creating user...");
		mDialog.setCancelable(false);
		mDialog.show();

		final CMUser user = new CMUser(username, password);

		user.createUser(new CreationResponseCallback()
		{
			public void onCompletion(CreationResponse response)
			{
				Log.d("createUser",
						"was user created? " + response.wasSuccess());
				boolean userAlreadyExists = response.getResponseCode().equals(
						CMResponseCode.EMAIL_ALREADY_EXISTS);
				if (userAlreadyExists)
				{
					Log.d("createUser",
							"User with that e-mail address already exists; user was not created");

					Toast.makeText(context, "User already exists",
							Toast.LENGTH_LONG).show();
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
				else
				{
					// configure the store with the user so that it can perform
					// user-centric operations
					// CMStore.getStore().setUser(user);
					logInUser(username, password);

					Toast.makeText(context, "Registered successfully",
							Toast.LENGTH_LONG).show();
					if (mDialog.isShowing())
						mDialog.dismiss();
				}
			}
		});

	}

	public void addTask(String description, String category, String dueDate,
			String location, String notes)
	{
		SimpleCMObject o = new SimpleCMObject();
		o.add("UserID", user.getEmail().toString());
		o.add("Category", category);
		o.add("DueDate", dueDate);
		o.add("Description", description);
		o.add("Notes", notes);
		o.add("CompletedFlag", "No");
		o.add("Location", location);

		addObject(o);
	}

	public void addObject(SimpleCMObject o)
	{
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Adding task...");
		mDialog.setCancelable(false);
		mDialog.show();

		final String objectId = o.getObjectId();

		o.saveWithUser(user, new ObjectModificationResponseCallback()
		{
			public void onCompletion(ObjectModificationResponse response)
			{
				Log.d("addObject",
						"Response was a success? " + response.wasSuccess());
				Log.d("addObject", "We: " + response.getKeyResponse(objectId)
						+ " the simpleObject");

				if (mDialog.isShowing())
					mDialog.dismiss();

				Toast.makeText(context, "Added task", Toast.LENGTH_LONG).show();

				fetchAllUserObjects();
			}

			public void onFailure(Throwable e, String msg)
			{
				Log.d("addObject", "We failed: " + e.getMessage());
				if (mDialog.isShowing())
					mDialog.dismiss();
				Toast.makeText(context, "Error adding task", Toast.LENGTH_LONG)
						.show();
			}
		});
	}

	public void updateTask(String id, final String description,
			final String category, final String dueDate, final String location,
			final String notes, final String completedFlag)
	{
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Updating task...");
		mDialog.setCancelable(false);
		mDialog.show();

		// Update an object
		CMStore.getStore(user).loadUserObjectsSearch("[__id__=\"" + id + "\"]",
				new CMObjectResponseCallback()
				{
					public void onCompletion(CMObjectResponse response)
					{
						for (CMObject object : response.getObjects())
						{
							SimpleCMObject task = (SimpleCMObject) object;
							// System.out.println("Found course: "
							// + course.getString("title"));
							final String objectId = task.getObjectId();

							task.add("UserID", user.getEmail().toString());
							task.add("Category", category);
							task.add("DueDate", dueDate);
							task.add("Description", description);
							task.add("Notes", notes);
							task.add("CompletedFlag", completedFlag);
							task.add("Location", location);

							task.save(new ObjectModificationResponseCallback()
							{
								public void onCompletion(
										ObjectModificationResponse response)
								{
									System.out
											.println("Response was a success? "
													+ response.wasSuccess());
									System.out.println("We: "
											+ response.getKeyResponse(objectId)
											+ " the simple object");

									if (mDialog.isShowing())
										mDialog.dismiss();

									Toast.makeText(context, "Updated task",
											Toast.LENGTH_LONG).show();

									fetchAllUserObjects();
								}

								public void onFailure(Throwable e, String msg)
								{
									System.out.println("We failed: "
											+ e.getMessage());

									if (mDialog.isShowing())
										mDialog.dismiss();

									Toast.makeText(context,
											"Error updating task",
											Toast.LENGTH_LONG).show();

								}
							});
						}
					}

					public void onFailure(Throwable e, String msg)
					{
						System.out.println("We failed: " + e.getMessage());

						if (mDialog.isShowing())
							mDialog.dismiss();

						Toast.makeText(context, "Error updating task",
								Toast.LENGTH_LONG).show();
					}
				});
	}

	public void fetchObject(String id)
	{
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Loading...");
		mDialog.setCancelable(false);
		mDialog.show();

		CMStore.getStore(user).loadUserObjectsSearch("[__id__=\"" + id + "\"]",
				new CMObjectResponseCallback()
				{
					public void onCompletion(CMObjectResponse response)
					{
						for (CMObject object : response.getObjects())
						{
							HashMap<String, String> Task = new HashMap<String, String>();
							SimpleCMObject task = (SimpleCMObject) object;
							System.out.println("Found task: "
									+ task.getString("__id__"));
							Task.put("UserID", task.getString("UserID"));
							Task.put("Category", task.getString("Category"));
							Task.put("DueDate", task.getString("DueDate"));
							Task.put("Description",
									task.getString("Description"));
							Task.put("Notes", task.getString("Notes"));
							Task.put("CompletedFlag",
									task.getString("CompletedFlag"));
							Task.put("Location", task.getString("Location"));
							Task.put("id", task.getString("__id__"));

							if (mDialog.isShowing())
								mDialog.dismiss();

							Intent intent = new Intent(context, EditTask.class);
							intent.putExtra("Task", Task);
							intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							context.startActivity(intent);
						}
					}
				});
	}

	public void fetchAllUserObjects()
	{
		final ProgressDialog mDialog = new ProgressDialog(activity);
		mDialog.setMessage("Loading...");
		mDialog.setCancelable(false);
		mDialog.show();

		final HashMap<String, HashMap<String, String>> Tasks = new HashMap<String, HashMap<String, String>>();

		CMStore.getStore(user).loadAllUserObjects(
				new CMObjectResponseCallback()
				{
					public void onCompletion(CMObjectResponse response)
					{
						for (CMObject object : response.getObjects())
						{
							HashMap<String, String> Details = new HashMap<String, String>();
							SimpleCMObject task = (SimpleCMObject) object;
							System.out.println("Found task: "
									+ task.getString("__id__")
									+ task.getString("Notes"));
							Details.put("UserID", task.getString("UserID"));
							Details.put("Category", task.getString("Category"));
							Details.put("DueDate", task.getString("DueDate"));
							Details.put("Description",
									task.getString("Description"));
							Details.put("Notes", task.getString("Notes"));
							Details.put("CompletedFlag",
									task.getString("CompletedFlag"));
							Details.put("Location", task.getString("Location"));

							Tasks.put(task.getString("__id__"), Details);
						}

						if (mDialog.isShowing())
							mDialog.dismiss();

						Intent intent = new Intent(context, TaskList.class);
						intent.putExtra("Tasks", Tasks);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						context.startActivity(intent);
					}

				});

	}

}
