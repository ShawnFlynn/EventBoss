package com.tssg.eventboss2;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tssg.eventboss2.utils.misc.MakeToast;
import com.tssg.eventsource.BELEvent;
import com.tssg.find.SearchActivity;


/**	EventlistDisplayActivity - main activity for Eventboss2
 * 
 * There is a plan to implement 2 possible sources for Eventlist items:
 *  1) read BostonEventslist RSS feed (as before),
 *  2) substitute an XML file (saved in assets) to send to the parser.
 *  variant 2 is not working (the parser tries to read a 'protocol' from the XML file,
 *  so the XML file can not be passed in to the Document Builder.
 *  String m_mainEventText contains the XML string or is null;
 */
// this activity is to be replaced by a fragment:  ListDisplayFragment 
// in EB2MainActivity: lines 531 +
public class EventListDisplayActivity extends FragmentActivity {

	public View m_mainAppView = null;		
	public static MainAppScreen mainAppScreen = null;
//====vvvvvv	(probably just in EB2Main
	public static final String INTENT_LOG = "com.tssg.eventboss.bLOGGING";
	static public TextView m_statusView = null;
	public boolean bLOGGING = true;				// enable/disable logging
	public static final String TAG = "EDAct";	// log's tag
	public static boolean bTrace = false;		// enable/disable tracing to device SD
	
	public static boolean m_readEventText = false; // if true do read XML
	public static String m_mainEventText = null;   // use an XML file for Eventsource
//====^^^^^^^^^
	public static List<BELEvent> m_webEventsList = new ArrayList<BELEvent>();
//	public static String statusLine = null;	// in mainApp is statusMessage
											// should use showStatus(" ... ") exclusively?
	public static Context context = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// get optional parameters from the intent
		Intent intent = this.getIntent();
		bLOGGING = intent.getBooleanExtra(INTENT_LOG, bLOGGING);
		context = this;
		
		mainAppScreen = new MainAppScreenImpl();
		mainAppScreen.setupLogging(bLOGGING, TAG);
		mainAppScreen.setUp(this, bLOGGING, TAG);
		m_mainAppView = mainAppScreen.getView();
		this.setContentView(m_mainAppView);
		m_statusView = (TextView) (m_mainAppView.findViewById(R.id.status_line));
		

		showStatus("Reading from Boston Events List's RSS feed");
		MakeToast.makeToast(this, "Reading BostonEventsList's RSS feed", MakeToast.LEVEL_USER);
// do some display: i.e.  a progress indicator in this view ?
//                         or can this be done in main
	}		// end OnCreate( ... )		

	/**
	 *   Display or redisplay the eventlist 
	 *   eventlist is read in an async task
	 */
	static void doDisplayEventList () {
// questionable if saving to database should be done here

		m_webEventsList = EB2MainActivity.m_webEventsList;
		Log.i("doDispEventList -> q = ", " " + m_webEventsList.size() );

		showStatus("Showing " + m_webEventsList.size() + " from current Web events");
	} // -------  end:   doDisplayEventList ()

	/**
	 *   If one of the Items displayed in the Event List 
	 *   is clicked/selected do something with it
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		EventListAdapter adapter = mainAppScreen.getAdapter();
		adapter.notifyDataSetInvalidated();
		String str = "Event select at: " + "pos: "+position+",  id: "+id;//
		Toast.makeText(this, str, Toast.LENGTH_LONG).show();
        Log.v("Click :",str);
	}
	   
    
	/**
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 * 
	 * Called first time user clicks on the menu button
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  MenuInflater inflater = getMenuInflater();   // 
	  inflater.inflate(R.menu.menu_listdisplay_activity, menu);
	  // implement save, delete, settings
	  return super.onCreateOptionsMenu(menu);
	}
	
	// Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {		// 
		
		case R.id.itemPrefs:
			startActivity(new Intent(this, SettingsActivity.class));  // 
			break;

		case R.id.idSearchNew:
			Intent searchIntent = new Intent(this, SearchActivity.class);
//			can't add it to intent  -- but search activity can see it
//			searchIntent.getIntExtra("EventList",listAddr);
			startActivity(searchIntent);  // 
			break;

		case R.id.idCalendar:
			Log.i("EventSource", " onOptionsItemSelected idCalendar");
			break;
			
		case R.id.action_share:
			Log.i("EventListDispActivity", " onOptionsItemSelected idShare");
			break;
		}
		return true;   
	}

	
	/**
	 * Opens a file somewhere on the device and returns the InputStream pointer
	 * @param fileName - the path to the file to be opened
	 * @return InputStream - the stream that is opened
	 */
	protected InputStream getInputStream(String fileName) {

		try {
			FileInputStream fileStream = new FileInputStream (fileName);
			return fileStream;

		} catch (IOException e) {

			String message = "Failed to open file: " + fileName;
			Log.e("EventSource", message);
			throw new RuntimeException( message, e);
		}
	}	

	/**
	 * function: convertStreamToString
	 * This function was found in StackOverflow.com question 309424.
	 * 
	 * @param is - an opened InputStream
	 * @return String - returns a text string
	 */
	protected String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}

	/**
	 * Write a message to the status window. 
	 * Does nothing if there is no status window.
	 */
	static public void showStatus(String statusMessage) {
		if (m_statusView != null) {
			m_statusView.setText(statusMessage);
		}
	}

/*
 * (non-Javadoc)
 *	Implement Callbacks for state-changing events during the 
 *  Android App Lifecycle.
 *
 */
		@Override
		protected void onStart() {
			// TODO implement onStart

			super.onStart();

			// updateFromSavedState();

			MakeToast.makeToast(this, "onStart", MakeToast.LEVEL_DEBUG);

		}

		@Override
		protected void onPause() {
			// TODO implement onPause
			MakeToast.makeToast(this, "onPause", MakeToast.LEVEL_DEBUG);
			
//			if (progressDialog.isShowing()) {
//				progressDialog.dismiss();
//			}

			super.onPause();
		}

		@Override
		protected void onRestart() {
			// TODO implement onRestart
			super.onRestart();

			MakeToast.makeToast(this, "onRestart", MakeToast.LEVEL_USER);
		}

		@Override
		protected void onResume() {
			// TODO implement onResume
			super.onResume();

			MakeToast.makeToast(this, "onResume", MakeToast.LEVEL_DEBUG);
		}

		@Override
		protected void onStop() {
			// TODO implement onStop
			super.onStop();
			
			if( bTrace )	{
				Debug.stopMethodTracing();
				MakeToast.makeToast(this, "onStop, stop trace", MakeToast.LEVEL_DEBUG);
			}

			MakeToast.makeToast(this, "onStop", MakeToast.LEVEL_DEBUG);
		}

    @Override
    protected void onDestroy() {
        // TODO implement onDestroy
        super.onDestroy();

        MakeToast.makeToast(this, "onDestroy", MakeToast.LEVEL_DEBUG);

//			DatabaseHelper.closeDB();
        MakeToast.makeToast(this, "closeDB", MakeToast.LEVEL_DEBUG);

    }


}   /*  EventListDisplayActivity */
