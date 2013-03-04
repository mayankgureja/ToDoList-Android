ToDoList-Android
================

An Android to-do list app that uses Cloudmine.me for data storage

How To Run
----------

The ToDoList.apk is located inside the /bin folder. Simply sideload it onto your Android phone or emulator and you're good to go.

Note: A test user called test@test.com is prefilled into the login page. Testers are free to use this to test the application, although creating a new user is highly recommended. Also note that in this release, passwords are not salted and hashed. So don't create an account with a password you care about :P

Coding Environment
------------------

Written, built and tested on [Eclipse-ADT (Android Development Tools)](http://developer.android.com/tools/sdk/eclipse-adt.html)

Description
-----------

ToDoList is an Android 4.2 To-Do List app that uses the [Cloudmine](http://cloudmine.me) service as a cloud storage solution. It allows users to create an account and manage their task list easily. The user can create a new account, log in, view tasks, edit tasks, mark tasks complete.

ToDoList has a few unique features. It will only display tasks that were created near the location where you are currently at. You can only view tasks that you've added when within that range of your current lat/long (picture viewing only tasks that you created while at work vs. viewing only tasks that you created while at home). In future releases, users will be able to turn this feature off. 

The application itself contains several different activities for different tasks, as well as a central Cloudmine class that contains all the data storage and gathering methods, in an effort to separate the functionality and make it easier to maintain/update in the future.

Since the time taken to GET information from a web resource can vary wildly, a ProgressDialog is displayed to give the user some feedback as to what is happening. Instead of waiting on a blank page for random periods of time, the user is shown a "Loading..." message until the application is ready.

Known Issues
------------

This is very much a beta product and there are various issues with this release.

Many of the ProgressDialogs are not handled properly and going back and forth in the app using the soft buttons can cause it to crash. 

The UI leaves much to be desired. The app was cobbled together in a very short amount of time, so the look and feel is neither constant nor appealing. However, the functionality is mostly sound.

Screenshots
-----------

Screenshots of the application in action are available in the /screenshots folder.

Dependencies/Requirements
-------------------------

[CloudMine Android Library](https://cloudmine.me/docs/java)
* cloudmine-android-v0.5.jar

Everything else is part of the standard libraries.