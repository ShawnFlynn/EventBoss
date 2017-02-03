
package com.tssg.eventboss2;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.tssg.datastore.DatabaseHelper;
import com.tssg.datastore.DatastoreException;
import com.tssg.eventboss2.utils.misc.MakeToast;
import com.tssg.eventboss2.CalendarAppointment;
import com.tssg.eventsource.BELEvent;
import com.tssg.eventsource.BELEventlist;


/*   MainActivity  implements
 *      ActionBar, with 3 Tabs: CurrentList, SavedList & Search
 *      manages the fragments for the above mentioned entities and
 *      the other ActionBar items (save, delete, Calendar, search).
 *      On start up it reads the RSS feed an stores events into list.
 *      Handles a Settings activity (for any user selections we implement)
 */
@SuppressWarnings("deprecation")
public class EB2MainActivity  extends FragmentActivity implements ActionBar.TabListener, EventFragmentCoordinator {

	protected final static String TAG = "EB2MainActivity";

	public static boolean DEBUG = true;		// Controls Toast() messaging
	public static boolean bTRACE = false;	// en/disable tracing to device SD
	boolean bDEVELOPER_MODE = false;		// controls strictMode set to false for release

	private static ActionBar m_actionBar;
	public static DatabaseHelper mDbh;
	public Context context = this;

	// the URL selected in SettingsActivity can change mURLString and mRSSString
	// volatiles may be changed from doInBackground; don't think we need to synchronize, yet
	// static volatile public String mURLString = "http://www.bostoneventslist.com/us/nh/events/rss.xml";
	static volatile public String mURLString = null;
	static volatile public String mRSSString = null;

	// String to hold Current/Stored for tab 0 label
	static volatile public String tab0Label;

	static volatile int mFeedId = 1;    // start with New Hampshire, it's a short list
	int oldFeedId = mFeedId;            // backup copy

	// this is a bit klugey. OK if date is near-enough
	static volatile Date m_channelDate = new Date(System.currentTimeMillis());
	static public String m_mainEventText;            // RSS data as saved text; use for debugging
	// note: this string can not be passed in to our DOM parser, it fails
	// maybe because the parser expects an other access to the Internet for parameters.
	// -> check what the DOM parser needs.

	static volatile List<BELEvent> m_webEventsList = new ArrayList<BELEvent>();    // RSS data from feed

	// The use of this is to save the lists in an array indexed by feedId for rapid reload.
	static public CopyOnWriteArrayList<List<BELEvent>> m_webEventsListA = new CopyOnWriteArrayList<List<BELEvent>>();  // RSS data from feed

	static {
		// KLUGE ALERT: hard-coded sizing.
		// Maybe m_webEventsListA should not be static
		// final int n_copies = getResources().getStringArray(R.array.rss_src_names).length;
		m_webEventsListA.addAll(Collections.nCopies(SettingsActivity.RBG_CHOICES, Collections.<BELEvent>emptyList()));
	}

	// DB store for LastGoodRead, SavedList etc
	com.tssg.datastore.BELDatastore dataStore = new com.tssg.datastore.BELDatastoreImpl( this );

	public static CurrentSectionFragment currentData = null;
	public static SavedSectionFragment savedData = null;
	public static SearchSectionFragment searchData = null;

	/*
	 * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
	 * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	AppSectionsPagerAdapter mAppSectionsPagerAdapter;

	/*
	 * The {@link ViewPager} that will display the three primary sections
	 * of the app, one at a time.
	 */
	ViewPager mViewPager;

	//  private static boolean mDualPane;
	static boolean mDualPane;               // not public, why?
	int mTabSelected = -1;

	public static EventDetailFragment mEventFragment;

	public static boolean readingFromInternalFile = false;
	public static String internalFilePath = null;

	// Note: The file specified below must exist on the device's internal storage.
	String eventListFileName = "your_file_name";

	// resources
	public static Resources mResources = null;  // data to be set into the spinner
	private String[] mRSSname = null;           // for user: name
	private String[] mRSSURL  = null;           // for computer: URL

	/*
	 * setTabLabel in the action bar
	 */
	public static void setTabLabel(String labelText){
		Log.i(TAG, "setTabLabel(" + labelText + ")");

		Tab currentTab = m_actionBar.getSelectedTab();
		currentTab.setText(labelText);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// setContentView(R.layout.newLayout);
		// need to reuse the view which is currently shown: current, saved or search.
		// it might switch between portrait and landscape format
		// (phone or dual mode)

		Log.i(TAG, "onConfigurationChanged()");

		Log.d(TAG, mResources.getString(R.string.DetectedConfig));
		if (DEBUG)
			Toast.makeText(context, mResources.getString(R.string.DetectedConfig)
									+ " ", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Activity launched or recreated
		Log.i(TAG, "onCreate()");
		mDbh = new DatabaseHelper(context);

		if( bTRACE ) {
			// trace file is created in SD device
			Debug.startMethodTracing("trace.file");
			if (DEBUG)
			MakeToast.makeToast(this, mResources.getString(R.string.startTrace)
									+ " ", MakeToast.LEVEL_DEBUG);
		}

		// setContentView
		setContentView(R.layout.activity_main);

		// Setup mFeedID using Intent Extras
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			// problem: getInt returns 0 if key is absent, but key is a valid feed#
			int arg = extras.getInt("feedId", mFeedId);
			oldFeedId = mFeedId ;
			mFeedId = arg;
		}

		// Setup resources
		mResources = getResources();            // data to be set into the spinner
		mRSSname = mResources.getStringArray(R.array.rss_src_names); // for user: name
		mRSSURL  = mResources.getStringArray(R.array.eventslists);   // for computer: URL
		EB2MainActivity.mURLString = mRSSname[mFeedId];
		EB2MainActivity.mRSSString = mRSSURL[mFeedId];

		// Initialize tab 0 label
		tab0Label = mResources.getString(R.string.Current);

		// StrictMode is a developer tool which detects things you might be doing
		// but not intentionally
		if (bDEVELOPER_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads()
					.detectDiskWrites()
					.detectNetwork()   // or .detectAll() for all detectable problems
					.penaltyLog()
					.build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects()
					.detectLeakedClosableObjects()
					.penaltyLog()
					.penaltyDeath()
					.build());
		}

		// Call the AsyncTask ExecListReader()
		// to get the specified feed data
		if (savedInstanceState == null) {
			try {
				Log.d(TAG, "URL: " + mURLString);
				// EventsListReader(URL) does the reading using the AsyncTask
				EventsListReader(new URL(mURLString));
			} catch (MalformedURLException e) {
				Log.e(TAG, "Invalid URL: " + mURLString);
				e.printStackTrace();
			}
		}

		// ********* This test is probably not right:
		//           Both versions (tablet and phone) will use 'event data'
		//           one in a single fragment, the other in separate fragments !!!!!!!
		// it must be like that because the APK file does not know onto what kind of a device it will be loaded!

		// Single or Dual pane?
		mDualPane = (findViewById(R.id.eventData) != null);

		// Create the adapter that will return a fragment for each of
		// the three primary sections (Tab) of the app.
		mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();     // ok within onCreate???
		m_actionBar = actionBar;

		// Check for valid actionBar
		if (actionBar != null) {

			// Specify that the Home/Up button should not be enabled, since there is no hierarchical parent.
			actionBar.setHomeButtonEnabled(false);

			// Specify that we will be displaying tabs in the action bar.
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

			// Set up the ViewPager, attaching the adapter and setting up a listener
			// for when the user swipes between sections.
			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mAppSectionsPagerAdapter);

			// onPageSelected
			mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

				@Override
				public void onPageSelected(int position) {
					// When swiping between different app sections, select the
					// corresponding tab. We can also use ActionBar.Tab#select()
					// to do this if we have a reference to the Tab.
					actionBar.setSelectedNavigationItem(position);
				}
			});

			// For each of the sections in the app, add a tab to the action bar.
			for (int i = 0; i < mAppSectionsPagerAdapter.getCount(); i++) {
				// Create a tab with text corresponding to the page title
				// defined by the adapter.  Also specify this Activity object,
				// which implements the TabListener interface, as the listener
				// for when this tab is selected.

				switch (i) {
					case 0:			// "Current Tab"
						actionBar.addTab(actionBar.newTab()
								 .setText(tab0Label)
								 .setTabListener(this));
						break;

					case 1:			// "Saved Tab"			
						actionBar.addTab(actionBar.newTab()
								 .setText(mResources.getString(R.string.Saved))
								 .setTabListener(this));
						break;

					case 2:			// "Search Tab"
						actionBar.addTab(actionBar.newTab()
								 .setText(mResources.getString(R.string.Search))
								 .setTabListener(this));
						break;

					case 3:			// Will have to use the display detail
						actionBar.addTab(actionBar.newTab()
								 .setText(mResources.getString(R.string.Event))
								 .setTabListener(this));
						break;
				}
			}
		}	// valid m_actionBar (not null)

		if (readingFromInternalFile){
			File file = getBaseContext().getFileStreamPath(eventListFileName);
			if (file.exists()){
				internalFilePath = file.getAbsolutePath();
			}
		}
	}	//	end --- OnCreate()

	@Override
	protected void onStart() {
		super.onStart();

		Log.i(TAG, "onStart()");	// Activity starts (after created)

	}	//  end --- onStart()

	@Override
	protected void onRestart() {
		super.onRestart();

		Log.i(TAG, "onRestart()");	// Activity re-starts (after it was stopped)

	}	//  end --- onRestart()

	@Override
	protected void onPause() {
		super.onPause();

		Log.i(TAG, "onPause()");	// Activity is paused
									// (because a higher priority activity needs memory)

	}	//  end --- onPause()

	@Override
	protected void onResume() {
		super.onResume();

		Log.i(TAG, "onResume()");	// Activity resumes after being paused

	}	//  end --- onResume()

	@Override
	protected void onStop() {
		super.onStop();

		Log.i(TAG, "onStop()");	// Activity is stopped ( it can resume or restart or
								//    it is destroyed
	}	//  end --- onStop()

	@Override
	protected void onDestroy() {

		Log.i(TAG, "onDestroy()");

		// Clear the cache events
		for (List<BELEvent> lbel : m_webEventsListA) {
			if (lbel != Collections.EMPTY_LIST)  {
				lbel.clear();
			}
		}

		// Clear the cache
		m_webEventsListA.clear();

		super.onDestroy();

	}	//  end - onDestroy()


	public void onTabUnselected(ActionBar.Tab tab,
						android.app.FragmentTransaction fragmentTransaction) {
		Log.i(TAG, "onTabUnselected(" + tab.getPosition() + ")");
	}

	public void onTabSelected(ActionBar.Tab tab,
						android.app.FragmentTransaction fragmentTransaction) {
		Log.i(TAG, "onTabSelected(" + tab.getPosition() + ")");

		// When the given tab is selected,
		// switch to the corresponding page in the ViewPager.
		if (mDualPane){
			Log.d(TAG, "displayEventDetails() -> int");
			/*
			 * Implements interface EventFragmentCoordinator, 
			 * displays the event details (fragment) 
			 * Specified by: displayEventDetails(...) in EventFragmentCoordinator
			 *		Parameters:	eventID, EventType 
			*/
			displayEventDetails("", tab.getPosition());
			// for specializing menu for current & saved
			mTabSelected = tab.getPosition();
			// if the tab is switched - load the appropriate menu
			invalidateOptionsMenu();
		}
		// ???
		mViewPager.setCurrentItem(tab.getPosition());
		Log.d(TAG, "onTabSelected() pos = " + tab.getPosition());

	}	//  end - onTabSelectede()

	/** Huh? This doesn't do anything */
	public void onTabReselected(ActionBar.Tab tab,
						android.app.FragmentTransaction fragmentTransaction) {

		Log.i(TAG, "onTabReselected(" + tab.getPosition() + ")");

		// here resume activity
		switch (tab.getPosition()) {
			case 0:
				// startActivity(new Intent(this, EventListDisplayActivity.class));	// start this in tab
				// fragmentTransaction.show(fragment);// shows hidden transaction
				// transaction = xxx;// transaction of EventListDisplayActivity
				break;
			case 1:
				// startActivity(new Intent(this, SaveListDisplayActivity.class));
				break;
			case 2:
				// startActivity(new Intent(this, SearchActivity.class));
				break;
		}
		// fragmentTransaction.show(fragment);// shows hidden transaction

	}	// end - onTabReselected()

	/*
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
	 * sections of the app.
	 */

	public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		protected final static String TAG = "AppSectionsPagerAdapter";

		final String[] humanReadableType = {"Current","Saved","Search"}; // like to use this globally in displayEventDetails

		public AppSectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {

			Log.i(TAG, "getItem(" +i+ ")");

			final String ARG_SECTION_NUMBER1 = "Current";
			final String ARG_SECTION_NUMBER2 = "Saved";
			final String ARG_SECTION_NUMBER3 = "Search";
			final String ARG_TAB_ID = "";
			Fragment fragment;
			Bundle args;

			switch (i) {
				case 0:
					Log.d(TAG, "---"+ARG_SECTION_NUMBER1+": tab=0 *");
					currentData = new CurrentSectionFragment();
					fragment = currentData;
					//There are problems when the class is not static!!!
					Log.d(TAG, "--- Current: tab/0 **");
					args = new Bundle();
					args.putInt(ARG_SECTION_NUMBER1, 1);
					args.putBoolean(ARG_TAB_ID,false);
					fragment.setArguments(args);
					return fragment;

				case 1:
					Log.d(TAG, "---"+ARG_SECTION_NUMBER2+": tab=1 *");
					savedData = new SavedSectionFragment();
					fragment = savedData;
					Log.d(TAG, "--- Saved: tab/1 **");
					args = new Bundle();
					args.putInt(ARG_SECTION_NUMBER2, 2);
					args.putBoolean(ARG_TAB_ID, true);
					fragment.setArguments(args);
					return fragment;

				case 2:
					Log.d(TAG, "---"+ARG_SECTION_NUMBER3+": tab=2 *");
					searchData = new SearchSectionFragment();
					fragment = searchData;
					Log.d(TAG, "--- Search: tab/2 **");
					args = new Bundle();
					args.putInt(ARG_SECTION_NUMBER3, 3);
					args.putBoolean(ARG_TAB_ID, false);
					fragment.setArguments(args);
					return fragment;

				default:
					if (mDualPane) {
						fragment = savedData;
					} else {
						Log.d(TAG, "--- Event: tab/3 *");
						mEventFragment = new EventDetailFragment();
						fragment = mEventFragment;
						Log.d(TAG, "--- Event: tab/3 **");
					}
					return fragment;

			}	//  end switch()

		}	//  end - getItem()

		@Override
		public int getCount() {
			if (mDualPane) {
				return 3;
			} else {
				return 3;
			}
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position)  {
				case 0: return ("Current List");
				case 1: return ("Saved List");
				case 2: return ("Search");
				case 3: return ("Event");
			}
			return "Section: " + (position + 1);
		}

	}	//  end - AppSectionsPagerAdapter


	public static void updateListHeader( String extraText )  {

		Log.i(TAG, "updateListHeader(" + extraText + ")");

		SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault() );
		String channelDate = EB2MainActivity.m_channelDate == null? "--" : simpFormat.format(EB2MainActivity.m_channelDate);

		// this should be the current date or the date when data was saved into the database
		CurrentSectionFragment.mListHeader.setText(extraText + EB2MainActivity.mRSSString
				+ " " + mResources.getString(R.string.ampersand)
				+ " " + channelDate);
		Log.d(TAG, "EB2MainActivity called update List header: " + extraText);

	}	//  end - updateListHeader()


/*
	void ProcessShare(MenuItem item) {

		ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		Toast.makeText(context, mResources.getString(R.string.doTheShare) + " ", Toast.LENGTH_SHORT).show();

		// collect data for sharing - this sends an MMS  ?????
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT,"enters text to sent!");
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_EMAIL,"this is an extra string");
		String str[] = {"qwerty", "asdfgh"};
		shareIntent.putExtra(Intent.EXTRA_EMAIL,str);

		mShareActionProvider.setShareIntent(shareIntent);

		Log.d("ProcessShare", " shareIntent "+shareIntent);

		// send off shared data
		startActivity(Intent.createChooser(shareIntent, "Events List"));
		Log.d("ProcessShare", " after chooser "+shareIntent);

	}	//  end - ProcessShare()
*/
// ==========================================================================
// PK 11/9/2014   -- EventsListReader contains the code following here, about 180 lines --
//
//		This code should contain no references to data belonging to the
//		current EB2MainActivity (i.e. it is given a URL and it returns
//		a pointer to a BELEventsList (which can be null if the read fails).
//		that is all it needs to do, optionally it could also return some error code.
//		It is the callers job to sort out the rest (save successful reads, inform user of result, etc.)
//		I would like to take this code out of EB2MainActivity.

	/* Data handling routines, ExecFeedReader is run asynchronously:
	 * <p>
	 *   {@link #EventsListReader(URL)}
	 *      Data read from BostonEventsList RSS
	 *      returns m_webEventsList  (the address of the events array)
	 *      The code has currently a fixed URL,
	 *      The Settings activity must be made to select one URL from a list of
	 *      current BostonEventsList URLs; and to use the one passed in as an argument.
	 * <p>
	 *   {@link EB2MainActivity#ReadEventsFromText()}	// Data BostonEventsList RSS converted to string
	 *      Retrieve data from a text file, stored in 'Assets', captured from RSS feed.
	 *      this is for test purposes, it should be feed to the DOM parser (which
	 *      currently chokes on it).
	 */
	public class ExecFeedReader extends AsyncTask<URL, Integer, List<BELEvent> > {

		static final String TAG = "ExecFeedRdr";  // log's tag

		// Progress Dialog
		private ProgressDialog pDialog = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			Log.i(TAG,  "onPreExecute(" + mFeedId + ")");

			pDialog = new ProgressDialog(EB2MainActivity.this);
			pDialog.setMessage(mResources.getString(R.string.ReadingRSSFeed)
								+mFeedId+ " "
								+mRSSString+
								mResources.getString(R.string.PleaseWait));
			pDialog.setIndeterminate(false);

			// see http://stackoverflow.com/questions/5253621/android-back-button-and-progress-dialog
			pDialog.setCancelable(true);	// not sure about this
			pDialog.show();

		}	//  end - onPreExecute()

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);

			if (null != pDialog) {
				switch(progress.length) {
					case 0:
						pDialog.setMessage( mResources.getString(R.string.gettingStarted)
											+ " " + progress[0]);
						break;
					case 1:
						pDialog.setMessage( mResources.getString(R.string.digesting)
											+ " " + progress[0]);
						break;
					case 2:
						pDialog.setMessage( mResources.getString(R.string.digesting)
											+ " " +progress[0]+ " " +
											mResources.getString(R.string.of)
											+ " " +progress[1]);
						break;
					default:
						if (progress[2] == 3) {
							pDialog.setMessage( mResources.getString(R.string.storing)
												+ " " +progress[0]+ " " +
												mResources.getString(R.string.of)
												+ " " +progress[1]+
												mResources.getString(R.string.phase)
												+ " " +progress[2]);
						} else {
							pDialog.setMessage( mResources.getString(R.string.digesting)
												+ " " +progress[0]+ " " +
												mResources.getString(R.string.phase)
												+ " " +progress[2]);
						}
						break;
				}
			}
		}	//  end - onProgressUpdate()

		@Override
		protected void onPostExecute( List<BELEvent> RSS_List ) {
			super.onPostExecute(RSS_List);

			Log.i(TAG, "onPostExecute(" + RSS_List.size() + ")");

			// Copy returned list to global list
			m_webEventsList = RSS_List;

			if (DEBUG)
				MakeToast.makeToast(EB2MainActivity.this,
									mResources.getString(R.string.ReadRSS)
									+ " ", MakeToast.LEVEL_DEBUG);

			if (!RSS_List.isEmpty() ) {
				String  txt =   mResources.getString(R.string.RSS)
										+ " " +RSS_List.size()
										+ " "
										+ mResources.getString(R.string.colon)
										+ " ";
				EB2MainActivity.currentData.updateList();
				EB2MainActivity.updateListHeader( txt );
			} else {
				// What should we say when feed has nothing to show? Could be bad feed,
				// connectivity issue, or really no events.
				Log.e(TAG, "No events at selected feed - so not changing view.");
				if (DEBUG)
					Toast.makeText( getApplicationContext(),
									mResources.getString(R.string.NoEvents),
									Toast.LENGTH_LONG).show();
			}

			//  remove progress indicator
			if (null != pDialog) {
				pDialog.hide();
				pDialog.cancel();
				pDialog = null;
			}

		}	//  end - onPostExecute()

// time to activate the display the List in the fragment (ListDisplayFragment)

//  1) Trying to embed an activity in the fragment is not successful:
//      EventListDisplayActivity.doDisplayEventList();

//  2) this way will start ListDisplayFragment all over (of course)
//      ListDisplayFragment.showWebEventsList();

//  3) implement the status display


//  4) activate the list in the fragment
//  fragment should have a status line and a list (for the events)
//  if the fragment is set-up right this should work:
//  pass the list of events to the adapter:
//  files   MainAppScreen and MainAppScreenImpl, EventlistAdapter
//  set the list in the adapter to the current values


		@Override
		protected List<BELEvent> doInBackground(URL... params) {

			Log.i(TAG, "doInBackground(" + params + ")");

			// Local Event List
			List<BELEvent> dIBEventList;

			// Initialize tab 0 label = Current
			tab0Label = mResources.getString(R.string.Current);

			// Read from cached feedId
			dIBEventList = m_webEventsListA.get(mFeedId);

			// If cache entry was empty
			if (dIBEventList.isEmpty()) {

				// Get Connection manager and network info
				ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

				// If we have network info and a connection
				if (networkInfo != null && networkInfo.isConnected()) {

					// Get a new empty event list
					BELEventlist eventSource = new BELEventlist(this);
					try {
						// Get a new or empty list from RSS feedId
						dIBEventList = eventSource.getBELEventlist(params);

						// If this list is not empty
						if (!dIBEventList.isEmpty()) {
							Log.d(TAG, "retrieved "
										+ dIBEventList.size()
										+ " new events from RSS feedId "
										+ mFeedId);
							// Copy to cache
							EB2MainActivity.m_webEventsListA.set(EB2MainActivity.mFeedId,
																 dIBEventList);
							Log.d(TAG, "saved " + dIBEventList.size()
												+ " events in cache feedId "
												+ mFeedId);
						}
					} catch (Exception excp) {
						Log.e(TAG, "doInBackground - exception "
								+ excp.toString()
								+ " in read RSS phase, feedId changed from "
								+ mFeedId
								+ " to "
								+ oldFeedId, excp);
						mFeedId = oldFeedId;
						dIBEventList = EB2MainActivity.m_webEventsListA.get(EB2MainActivity.mFeedId);
					}
				}
			} else {
				Log.d(TAG, "used " + dIBEventList.size() 
							+ " saved events from cache feedId "
							+ mFeedId);
			}

			// If we received something from the cache or RSS feed
			if ((dIBEventList != null) && (!dIBEventList.isEmpty()) ) {
				// Save in DB for future use in case next time there is nothing read from the feed.
				// First throw out previous BD entries
				try {
					com.tssg.datastore.BELDatastore dataStore = new com.tssg.datastore.BELDatastoreImpl(getBaseContext());
					dataStore.deleteAllWebEvents();
				} catch (DatastoreException e) {
					Log.e(TAG, "doInBackground: dataStore.deleteAllWebEvents: ", e);
				}
				// Second make a copy in the DB of the current list of events
				int kk = 0, size = dIBEventList.size();
				for (BELEvent next : dIBEventList) {
					try {
						next.setFeed(mFeedId);
						dataStore.saveWebEvent( next );
						publicProgressCallback(++kk, size, 3);
					} catch (DatastoreException dataExp) {
						Log.e(TAG, "doInBackground: Caught exception trying to save to the database: ", dataExp);
					}
				}
				Log.d(TAG, "stored " + dIBEventList.size() 
									 + " events into the database feedId "
									 + mFeedId);
			} else {
				BELEvent next;

				// Get current DB entries
				dIBEventList = dataStore.getAllWebEvents();

				// Check if we received anything
				if (!dIBEventList.isEmpty()) {
					// Get the first event record
					next = dIBEventList.get(0);
					if (null != next) {
						// Get and update the feedId
						mFeedId = next.getFeed();
						oldFeedId = mFeedId;
						// Update the resources
						EB2MainActivity.mURLString = mRSSname[mFeedId];
						EB2MainActivity.mRSSString = mRSSURL[mFeedId];

						// If this is not a recent database store from cache
						if (EB2MainActivity.m_webEventsListA.get(EB2MainActivity.mFeedId ).isEmpty()) {
							// Set tab 0 label = Stored
							EB2MainActivity.tab0Label = mResources.getString(R.string.Stored);
						}

						Log.d(TAG, "used " +dIBEventList.size()+ " stored events from database feedId " + mFeedId);
					}
				} else {
					Log.d(TAG, "nothing found in the database");
				}
			}

			// Return the list
			return dIBEventList;

		}	//  end - doInBackground()

		/** Because {@link AsyncTask#publishProgress} is protected, need a callback to update status. */
		public void publicProgressCallback(Integer... values) {
			this.publishProgress(values);
		}

	}	//  end - ExecFeedReader()

	/*
	 * This is the code  that must be be executed in an AsyncTask
	 * (and the code there must be removed from EventListDisplayActivity  done! ).
	 * Uses {@link ExecFeedReader}
	 */
	void EventsListReader(URL url)  {

		Log.i(TAG, "EventsListReader(" + url + ")");

		Log.d(TAG, "before call ExecFeedReader  ");
		new ExecFeedReader().execute(url);
		Log.d(TAG, "after call ExecFeedReader  ");

	}	//  end - EventsListReader()

// ===================   end of EventsListReader code =========================================


	/**
	 * Reading a Text-file which is stored in (android) Assets (directory)
	 * This  file is RSS data, read from the RSS source
	 * Is intended to be feed to the RSS processing procedure at a suitable point
	 * (for decoding the feed data into BELEvents)
	 *
	 * @return m_mainEventText
	 *
	 * There is somewhere another read (text) routine (from Jeremy)
	 * to be manually inserted (I don't remember where).
	 *
	 * Note: these routines are intended to provide input to EventBoss2
	 *       as if it was coming from the RSS feed. This is suitable to save
	 *       data from a problem feed for continuous testing.
	 *
	 */

	String ReadEventsFromText() {
		// Read a test-file name from  Assets
		String [] files;
		try {
			files = getAssets().list("");
		} catch (IOException e) {
			String message = "Failed to getAssets list";
			Log.e(TAG, message, e);
			throw new RuntimeException( message, e);
		}
		InputStream stream = null;
		String rxmlFile = null;
		for (String file : files) {
			Log.d(TAG, "Found Assets item: " + file);
			if (file.contains("rxml")) {
				rxmlFile = file;
				Log.d(TAG, "Found rxml file: " + rxmlFile);
				try {
					stream = getAssets().open(rxmlFile);
				} catch (IOException e) {
					String message = "Failed to open file: " + rxmlFile;
					Log.e(TAG, message, e);
					throw new RuntimeException( message, e);
				}
				break;
			}
		}
		// this is just a check that we actually got a stream. Really won't work
		// for anything because we didn't open the rxmlFile the first time around.
		if (stream == null) {
			stream = getInputStream(rxmlFile);
		}

		try {
			m_mainEventText = convertStreamToString(stream);
		} finally {
			try {
				stream.close();
			} catch (IOException excp) {
				// drop it
			}
		}

		//  log.i("EventListDisplay", "rxml file: " + m_mainEventText);
		return m_mainEventText;

	}	//  end - ReadEventsFromText()

	// eventText is the same as xmlString  in BellSourcedForEvents (lines 101 - 106)
	//

	/*
	 * Opens a file somewhere on the device and returns the InputStream pointer
	 * @param fileName - the path to the file to be opened
	 * @return InputStream - the stream that is opened
	 * @throws RuntimeException in response to IOException
	 */
	protected InputStream getInputStream(String fileName) {
		try {
			return new FileInputStream(fileName);
		} catch (IOException e) {
			String message = "Failed to open file: " + fileName;
			Log.e(TAG, message);
			throw new RuntimeException( message, e);
		}
	}	//  end - getInputStream()

	/*
	 * function: convertStreamToString
	 * This function was found in StackOverflow.com question 309424.
	 *
	 * @param is - an opened InputStream
	 * @return String - returns a text string
	 */
	@SuppressWarnings("resource")
	String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}	//  end - convertStreamToString()


	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu main_activity_action) {
		Log.i(TAG, "onCreateOptionsMenu()");

		MenuInflater inflater = getMenuInflater();
		if( mDualPane ) {
			// ?use different menu depending on Tab
			// current can only save	-> menu_current_fragment
			// saved can only delete	-> menu_saved_current
			// search ?iterate over different searches  	
			switch (mTabSelected) {
				case 0 :
					inflater.inflate(R.menu.menu_dual_activity_cur,
										main_activity_action);
					Log.d("tab selected is :",
							" " + mTabSelected);
					break;
				case 1 :
					inflater.inflate(R.menu.menu_dual_activity_sav, main_activity_action);
					Log.d("tab selected is :",
							" " + mTabSelected);
					break;
					// probably need a case for 3 (search) too
				case 2 :
					inflater.inflate(R.menu.menu_dual_activity_cur, main_activity_action);
					// > can we save a selected item - like with current ? <<
					Log.d("tab selected is :",
							" " + mTabSelected);
					break;
				default: 
					Log.d("tab selected is :",
							"invalid! " + mTabSelected);
					inflater.inflate(R.menu.menu_main_activity, main_activity_action);
					// just has a preferences button
					return false;
			}
			return true;
		}

		// not a DualPane  
		Log.d("onCreateOptionsMenu ",
				" for activity single pane, tab " + mTabSelected);
		inflater.inflate(R.menu.menu_main_activity, main_activity_action);
		return true;

	}	//  end - onCreateOptionsMenu()

	/* Called when an options item is clicked.
	 * Handles  itemPrefs, punts on idDeleteSelected, idSaveSelected, or anything else.
	 *
	 * 'Save'       only if the CurrentSectionFragment is active (Tab0)
	 * 'Delete'     only if the SavedSectionFragment is active (Tab1)
	 *  currentTab = m_actionBar.getSelectedTab();   <--- could use this
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int optionSelected = item.getItemId();

		Log.i(TAG, "onOptionItemSelected()");

		switch (optionSelected) {
			case R.id.itemPrefs:	// idSettings:
				Log.d(TAG, " - Settings Selected");
				startActivity(new Intent(this,SettingsActivity.class));
				break;

			case R.id.action_save:	// idSaveSelected:
				// Do this only if in CurrentSectionFragment (& dual)
				Log.d(TAG, " - Save Selected");
				if (DEBUG)
					Toast.makeText(context, "EBMain - Save Selected Current",
											Toast.LENGTH_LONG).show();
				String strEvent = String.format(Locale.getDefault(), "%d",
												CurrentSectionFragment.mId); 
				Log.d(TAG, "strEvent: " + strEvent
						+ " from mId :"
						+ CurrentSectionFragment.mId);
				mDbh.saveEvent(String.format(Locale.getDefault(),strEvent));
				// now update the list
				savedData.updateList();
				break;

			case R.id.action_delete:	// idDeleteSelected:
				// Do this only if in SavedSectionFragment  (& dual)
				Log.d(TAG, " - Delete Selected");
				if (DEBUG)
					Toast.makeText(context, "EBMain - Delete Selected Saved",
											Toast.LENGTH_LONG).show();
				strEvent = String.format(Locale.getDefault(), "%d",
											SavedSectionFragment.mId); 
				Log.d(TAG, "strEvent: "+ strEvent
						+ " from mId :"
						+ SavedSectionFragment.mId);
				mDbh.deleteSavedEvent(strEvent);
				// the SavedSectionFragment must reload the data table
				savedData.updateList();
				// should now close the just deleted detail view
				//  since it is no longer in the list	 
				break;

			case R.id.action_calendar:	// idCalendarSelected
				strEvent = String.format(Locale.getDefault(),
										"%d", CurrentSectionFragment.mId);
				if (DEBUG)
					Toast.makeText(context, TAG + " Calendar " + strEvent,
											Toast.LENGTH_SHORT).show();
				BELEvent event = mDbh.getEventById(strEvent);
				Log.d(TAG, " Calendar: event "+event);
				// call 'CalendarAppointment' as implemented
				Intent intent = CalendarAppointment.makeCalendarAppointment(event);
				startActivity(intent);
				break;

//					CalendarAppointment(event);                // moved to  its own class

//					String title = event.getTitle();
//					String location = event.getLocation();
//					Date start = (Date) event.getStartDate();   // event uses java.util.Date !!!
//					//java.lang.ClassCastException: java.util.Date cannot be cast to java.sql.Date
//					Log.d(TAG,start.toString()+" "+event.getStartDate());
//					Date end = (Date) event.getEndDate();
//					Date start = m_channelDate;		
//					Log.d(TAG,start.toString());	// 2015-10-30
//					Date end = m_channelDate;
//					makeAppointment(title, location, start, end );
//
//					CalendarAppointment(BELEvent event) {
//					}

			case R.id.action_share:		//idShareSelected
				Log.d(TAG, " - Share Selected");
				if (DEBUG)
					Toast.makeText(context,  TAG + " Share",
											Toast.LENGTH_SHORT).show();
				Log.d(TAG, " item: " +  CurrentSectionFragment.mId);
				ProcessShare(item);
				break;

			default:
				Log.d(TAG, " " + mResources.getString(R.string.unimplemented)
						+ " "
						+ Integer.toHexString(optionSelected)
						+ " " + mResources.getString(R.string.pressed));
				if (DEBUG)
					Toast.makeText(context,  " "
								+ Integer.toHexString(optionSelected)
								+ " " + mResources.getString(R.string.pressed),
								Toast.LENGTH_SHORT).show();
				Log.d(TAG, " " + Integer.toHexString(optionSelected)
						 + " " + mResources.getString(R.string.pressed));
				break;
		}

		return true;
	}	//  end - onOptionsItemSelected()


/*
	private void CalendarAppointment(BELEvent event) {
//		CalendarAppointment(event);		// old
		
		Intent intent =  new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI); // Events is a n import

			String title = event.getTitle();
			String location = event.getLocation();
			String start = event.getStartTime();
			String end = event.getEndTime();

//			long startL, endL;		/// old code gets the current time (in sql.date), should be util.date //
//			if (null != start) {
//				startL = (long) start.setStartDate();
//			else
//				startL = (long) start;
//			if (null != end) {
//				endL = (long) start.setEndDate();
//			else
//				endL = (long) start;

			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
			intent.putExtra(Events.ALL_DAY, false);
			intent.putExtra(Events.TITLE, title);
			intent.putExtra(Events.EVENT_LOCATION, location);

			Log.d("makeAppointment", " " + intent);
			startActivity(intent);

	}	//  end  - CalendarAppointment()

//	private void CalendarAppointment(BELEvent event) {
// **********   this should run in its own class for both types of variants (phone and tablet)
//}
*/

	void ProcessShare(MenuItem item) {
		Log.i(TAG, "ProcessShare()");

		ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
/*
 * This class is a mediator for accomplishing a given task, for example
 *  sharing a file. It is responsible for creating a view that performs
 *  an action that accomplishes the task. This class also implements 
 *  other functions such a performing a default action. 
 * An ActionProvider can be optionally specified for a MenuItem and 
 * in such a case it will be responsible for creating the action view 
 * that appears in the android.app.ActionBar as a substitute for 
 * the menu item when the item is displayed as an action item. 
 * Also the provider is responsible for performing a default action
 *  if a menu item placed on the overflow menu of the ActionBar is selected 
 *  and none of the menu item callbacks has handled the selection. 
 *  For this case the provider can also optionally provide a sub-menu 
 *  for accomplishing the task at hand. 

 * There are two ways for using an action provider for creating and 
 * handling of action views: 

 * Setting the action provider on a MenuItem directly by calling 
 * MenuItem.setActionProvider(ActionProvider). 
 * Declaring the action provider in the menu XML resource. For example: 

	<item android:id="@+id/my_menu_item"
		android:title="Title"
		android:icon="@drawable/my_menu_item_icon"
		android:showAsAction="ifRoom"
		android:actionProviderClass="foo.bar.SomeActionProvider" />

See Also:
MenuItem.setActionProvider(ActionProvider)
MenuItem.getActionProvider()
 */
		if (DEBUG)
			Toast.makeText(this, "doTheShare" + " ",
								Toast.LENGTH_SHORT).show();

		// collect data for sharing - this sends an MMS  ?????
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, " text to sent!");
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_EMAIL, " extra string");
		String str[] = {"qwerty", "asdfgh"};
		shareIntent.putExtra(Intent.EXTRA_EMAIL, str);

		mShareActionProvider.setShareIntent(shareIntent);

		Log.d("ProcessShare", " shareIntent " + shareIntent);

		// send off shared data
		Log.d("ProcessShare", " after chooser " + shareIntent);
		startActivity(Intent.createChooser(shareIntent, "Events List"));

	}	//  end - ProcessShare()

/*
	public void makeAppointment(String title, String location, Date start, Date end ) {
		Intent intent =  new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);

		long startL, endL;
		if (null != start) {
			startL = start.getTime();
			intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startL);
		}
		if (null != end) {
			endL = end.getTime();
			intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endL);
			}

		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.ALL_DAY, false);
		intent.putExtra(Events.EVENT_LOCATION, location);

		Log.d("makeAppointment", " " + intent);
		startActivity(intent);
	}	//  end - makeAppointment()
*/


/** ----------------------------------------------------------------------------
	**      Implements interface {@link EventFragmentCoordinator},
	**                 displays the event details (fragment)
	**      caller must set 'isSavedEvent' to false (if called from tab current,
	**                                     to true  (if called from tab saved.
	**/
	//----------------------------------------------------
	//                          use int (the Tab number)  |
	//                                                    V
//    public void displayEventDetails(String eventID, boolean isSavedEvent) {
	public void displayEventDetails(String eventID, int EventType) {

		Log.i(TAG, "displayEventDetail(" + eventID + ")");

// old:   Log.v(TAG, "displayEventDetails(String eventID, boolean isSavedEvent)");
//        Log.v(TAG, "EventID, isSavedEvent are passed to the EventDetailFragment");
//        Log.v(TAG, "the boolean is passed in as a string value for true/false ");
//        Log.v(TAG, "the translation to string then back to boolean does not work");
		String humanReadableType[] = {"Current","Saved","Search"};
		Log.d(TAG, "displayEventDetails(String eventID = "
					+ eventID +", EventType = "
					+ humanReadableType[EventType]);

		if (mDualPane) {
			// for a dual-pane view (tablet) - the fragment implements the detail-view

			EventDetailFragment mEventItemFrag = new EventDetailFragment();
			mEventItemFrag.setEventId(eventID);
			mEventItemFrag.setListType(EventType);
			mEventItemFrag.setDBhelper(new DatabaseHelper(context));
			Log.d(TAG, "displayEventDetails: DualPane:"
					+ " eventID: " + eventID
					+ " +eventType: " + EventType + humanReadableType);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.eventData, mEventItemFrag); 
			Integer i = R.id.eventData;
			Log.d(TAG, "id.eventData: " + Integer.toHexString(i)+","
					+ " eventItem " + mEventItemFrag);
			//transaction.addToBackStack(null);
			transaction.commit();
 
		} else {
			// for a single-pane view (phone) - Start the detail-view activity which
			//                                  controls the detail-view (fragment)
			Log.d(TAG, "displayEventDetails: SinglePane," + eventID);
			Log.d(TAG, "displayEventDetails: start EventDetailActivity " + eventID);

			// In single-pane mode, start the detail event activity for the selected item ID:
			Intent detailIntent = new Intent(this, EventDetailActivity.class);
//			detailIntent.putExtra(EventDetailFragment.SAVED_KEY, String.valueOf(isSavedEvent));
//			detailIntent.putExtra(EventDetailFragment.DB_HELPER, mDbh);
//			String SorC;
//			if (isSavedEvent)
//				SorC = "S";
//			else
//				SorC = "C";

//			detailIntent.putExtra(EventDetailFragment.SAVED_KEY, isSavedEvent); // boolean
//			Log.d(TAG, "displayEventDetails: EventType: " + SorC);  //the boolean as string
//			detailIntent.putExtra(EventDetailFragment.SAVED_KEY, SorC); // !boolean
			Log.d(TAG, "displayEventDetails: EventType: " + EventType);  //the boolean as string
			detailIntent.putExtra(EventDetailFragment.LIST_TYPE, EventType); // !boolean anymore
			detailIntent.putExtra(EventDetailFragment.EVENTITEM_POS, eventID);
			detailIntent.putExtra(EventDetailFragment.DB_HELPER, String.valueOf(mDbh));  // ?works?


			Log.d(TAG, "displayEventDetails: w/intent: "
					+ detailIntent.toString());
			startActivity(detailIntent);
		}

	}	//  end - displayEventDetails()

}	//  end - MainActivity
