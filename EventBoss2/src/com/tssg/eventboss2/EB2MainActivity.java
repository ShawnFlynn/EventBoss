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
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;

import com.tssg.datastore.DatastoreException;
import com.tssg.eventboss2.utils.misc.MakeToast;
import com.tssg.eventsource.BELEvent;
import com.tssg.eventsource.BELEventlist;
/**	MainActivity  implements 
 * 		ActionBar, with 3 Tabs: CurrentList, SavedList & Search
 * 		manages the fragments for the above mentioned entities and
 * 		the other ActionBar items (save, delete, Calendar, search).
 * 		On start up it reads the RSS feed an stores events into list.
 * 		Handles a Settings activity (for any user selections we implement)		
 */
public class EB2MainActivity  extends FragmentActivity implements ActionBar.TabListener, EventFragmentCoordinator  { 

	public Context context = this;
	public static final boolean bLOGGING = true;	// enable/disable logging
	public static final String TAG = "EBMainAct";	// log's tag
	public static boolean bTRACE = false;			// en/disable tracing to device SD
    boolean bDEVELOPER_MODE = false;				    // controls strictMode	set to false for release
    private static ActionBar m_actionBar;

	public static String statusMessage = null;		// status line content
	public static TextView m_statusView = null;		// view for status line

	// the URL selected in SettingsActivity can change mURLString and mRSSString
	// volatiles may be changed from doInBackground; doin't think we need to synchronize, yet
	// static volatile public String mURLString = "http://www.bostoneventslist.com/us/nh/events/rss.rxml";
	static volatile public String mURLString = "http://www.nheventslist.com/rss.xml";
	static volatile public String mRSSString = "New Hampshire";
	volatile static int mFeedId= 1;	// start with New Hampshire, it's a short list
	int oldFeedId = mFeedId;	// backup copy
	// this is a bit klugey. OK if date is near-enough
	static volatile Date m_channelDate = new Date(System.currentTimeMillis());
	static public String m_mainEventText;			// RSS data as saved text; use for debugging
					// note: this string can not be passed in to our DOM parser, it fails
					// maybe because the parser expects an other access to the Internet for parameters.
					// -> check what the DOM parser needs.
	
	static volatile List<BELEvent> m_webEventsList = new ArrayList<BELEvent>();	// RSS data from feed
	/** The intent of this is to save the lists in an array indexed by feedId for rapid reload.
	 * Should we just save to DB? */
	static private CopyOnWriteArrayList<List<BELEvent>> m_webEventsListA = new CopyOnWriteArrayList<List<BELEvent>>();	// RSS data from feed

	static {
		// KLUGE ALERT: hard-coded sizing.
		// Maybe m_webEventsListA should not be static
		// final int n_copies = getResources().getStringArray(R.array.rss_src_names).length;
		m_webEventsListA.addAll(Collections.nCopies(SettingsActivity.RBG_CHOICES, Collections.<BELEvent>emptyList()));
	}

	com.tssg.datastore.BELDatastore dataStore = new com.tssg.datastore.BELDatastoreImpl( this );          												// DB store for LastGoodRead, SavedList etc 
        												// DB store for LastGoodRead, SavedList etc 
	public static CurrentSectionFragment currentData = null;
	public static SavedSectionFragment savedData = null;
	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * three primary sections of the app. We use a {@link android.support.v4.app.FragmentPagerAdapter}
     * derivative, which will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
	private AppSectionsPagerAdapter mAppSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will display the three primary sections 
     * of the app, one at a time.
     */
    ViewPager mViewPager;
//	private static boolean mDualPane;
	static boolean mDualPane;				// not public?
	public static EventItemFragment mEventFragment;
	
	public static boolean readingFromInternalFile = false;
	public static String internalFilePath = null;
	//Note: The file specified below must exist on the device's
	//       internal storage. 
	private String eventListFileName = "your_file_name";
	private Resources mResources= null; 		// data to be set into the spinner 
	private String[] mRSSsources = null; // for user: name
	private String[] mRSSnames = null;		// for computer: URL

	/*
	 * setTabLabel
	 */
	public static void setTabLabel(String labelText){
        // Set up the action bar.
      Tab currentTab = m_actionBar.getSelectedTab();
      currentTab.setText(labelText);
	}

  	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	  super.onConfigurationChanged(newConfig);
//	  setContentView(R.layout.newLayout);
	  // need to reuse the view which is currently shown: current, saved or search.
	  // it might switch between portrait and landscape format
	  // (phone or dual mode)

	  Log.v("MainActivity", " detected Configurationchange");
		Toast.makeText(context,  "Detect Configuration change", Toast.LENGTH_SHORT).show();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		if( bTRACE )	{ 
			// trace file is created in SD device
			Debug.startMethodTracing("trace.file");
			MakeToast.makeToast(this, "onCreate, start trace", MakeToast.LEVEL_DEBUG);
		}
	 
        setContentView(R.layout.activity_main);
		Log.v(TAG, "start up, with feedId= " + mFeedId);
		Bundle extras = getIntent().getExtras();
		if(extras != null) {
			// problem: getInt returns 0 if key is absent, but key is a valid feed#
			int arg = extras.getInt("feedId", mFeedId);
			Log.i(TAG, "start up, change feedId from " + mFeedId + " to " + arg);
			oldFeedId = mFeedId ;
			mFeedId = arg;
   		}
		mResources = getResources();			// data to be set into the spinner 
		mRSSsources = mResources.getStringArray(R.array.rss_src_names); // for user: name
		mRSSnames = mResources.getStringArray(R.array.eventslists);		// for computer: URL
    	EB2MainActivity.mURLString = mRSSsources[mFeedId];
		EB2MainActivity.mRSSString = mRSSnames[mFeedId];

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

	   	Log.v("MainActivity:", "-> EventsListReader");
	   	if (savedInstanceState == null) {
	        try {
        		Log.v(TAG, "URL: " +mURLString);
		        // EventsListReader(URL) does the reading using the AsyncTask
				EventsListReader(new URL(mURLString));
			} catch (MalformedURLException e) {
				Log.e("MainActivity invalid URL: ", mURLString);
				e.printStackTrace();
			}
	   	}
	   	//
        // need a mechanism to signal valid data and a fresh read.
	   	// ********* This test is probably not right:
	   	// 			 Both versions (tablet and phone) will use 'event data'
	   	//           one in a single fragment, the other in separate fragments !!!!!!!
	   	// it must be like that because the APK file does not know onto what kind of a device it will be loaded!
	   	//
    	Log.v("MainActivity:", "EventsListReader-> ");
    	ViewGroup details = (ViewGroup) findViewById(R.id.eventData);
	  	if (details != null) {
	  		mDualPane = true;
	  	} else {
	  		mDualPane = false;
	  	}

        // Create the adapter that will return a fragment for each of 
        // the three primary sections (Tab) of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();		// ok within onCreate???
        m_actionBar = actionBar;

        // Specify that the Home/Up button should not be enabled, since there is no hierarchical
        // parent.
        actionBar.setHomeButtonEnabled(false);

        // Specify that we will be displaying tabs in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Set up the ViewPager, attaching the adapter and setting up a listener 
        // for when the user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
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
            case 0:
                actionBar.addTab( actionBar. newTab()
//                                .setText(mAppSectionsPagerAdapter.getPageTitle(i))
                        			.setText("Current")
                        			.setTabListener(this)); //, true);
////  --  need to attach the fragment // or not yet               
        		break;

        	case 1:
                actionBar.addTab( actionBar.newTab()
                					.setText("Saved")
                					.setTabListener(this));
        		break;

        	case 2:
                actionBar.addTab( actionBar.newTab()
                					.setText("Search")
                					.setTabListener(this));
    			break;
        	case 3:
                actionBar.addTab( actionBar.newTab()
                					.setText("Event")
                					.setTabListener(this));
                break;
        	}	
        }

        if (readingFromInternalFile){
        	File file = getBaseContext().getFileStreamPath(eventListFileName);
        	if (file.exists()){
        		internalFilePath = file.getAbsolutePath();
        	}        
        }
    }	// ------- end OnCreate

	
    public void onTabUnselected(ActionBar.Tab tab, 
    					android.app.FragmentTransaction fragmentTransaction) {
    }

    public void onTabSelected(ActionBar.Tab tab, 
    		android.app.FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, 
    	// witch to the corresponding page in the ViewPager.
    	if (mDualPane){
    		Log.v("EB2MainActivity.onTabSelected (267):", " ->displayEventDetails (false)");
    		displayEventDetails("", false);
    	}
        mViewPager.setCurrentItem(tab.getPosition());
        Log.v(TAG, "onTabSelected " + tab.getPosition());
        if (tab.getPosition() == 1) {
        	savedData.updateList();
        }
        
    }

    /** Huh? This doesn't do anything */
    public void onTabReselected(ActionBar.Tab tab, 
    		android.app.FragmentTransaction fragmentTransaction) {
        Log.v("MainActivity:", "onTabReselected " + tab.getPosition());
        // here resume activity
    	switch (tab.getPosition()) {
        case 0:
//           startActivity(new Intent(this, EventListDisplayActivity.class));	// start this in tab
//        	fragmentTransaction.show(fragment);// shows hidden transaction
//			transaction = xxx;// transaction of EventListDisplayActivity
           break;
        case 1:
//           startActivity(new Intent(this, SaveListDisplayActivity.class));
           break;
        case 2:		
//		   startActivity(new Intent(this, SearchActivity.class)); 
           break;
    	}
//    	fragmentTransaction.show(fragment);// shows hidden transaction
    	
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

		public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            final String ARG_SECTION_NUMBER1 = "Current";
            final String ARG_SECTION_NUMBER2 = "Saved";
            final String ARG_SECTION_NUMBER3 = "Search";
        	Fragment fragment;
        	Bundle args;

        	switch (i) {
                case 0:
            Log.v("MainActivity:", "---"+ARG_SECTION_NUMBER1+": tab/0 *");
            		currentData = new CurrentSectionFragment();
            		fragment = currentData;
   //There are problems when the class is not static!!!
            Log.v("MainActivity:", "--- Current: tab/0 **");
              		args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER1, 1);
                    fragment.setArguments(args);
                    return fragment;

                case 1:
         	Log.v("MainActivity:", "---"+ARG_SECTION_NUMBER2+": tab/1 *");
                    savedData = new SavedSectionFragment();
                    fragment = savedData;
                 	Log.v("MainActivity:", "--- Saved: tab/1 **");
                    args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER2, 2);
                    fragment.setArguments(args);
                    return fragment;
                    
                case 2:
       		Log.v("MainActivity:", "---"+ARG_SECTION_NUMBER3+": tab/2 *");
                    fragment = new SearchSectionFragment();
            Log.v("MainActivity:", "--- Search: tab/2 **");
                    args = new Bundle();
                    args.putInt(ARG_SECTION_NUMBER3, 3);
                    fragment.setArguments(args);
                    return fragment;
                    
                default:
                	if (mDualPane) {
                		fragment = savedData;
                	} else {
	                	Log.v("MainActivity:", "--- Event: tab/3 *");
	         			mEventFragment = new EventItemFragment();
	                    fragment = mEventFragment;
	                    Log.v("MainActivity:", "--- Event: tab/3 **");
                	}
                    return fragment;
                    
        	}   // end switch()
        	
        }		// --- Fragment getItem()

        @Override
        public int getCount() {
        	if (mDualPane) {
        		return 3;
        	} else {
        		return 4;
        	}
        }

        @Override
        public CharSequence getPageTitle(int position) {
        	switch (position)  {
		    	case 0: return ("Current List");
		    	case 1: return ("Saved List");
		    	case 2: return ("Search");
		    	case 3: return ("Event");
        	};
        	return "Section " + (position + 1);
        }
    }	// ------- end AppSectionsPagerAdapter


	/**
	 * Write a message to the status window:	EB2MainActivity has no status view!
	 * Does nothing if there is no status window.
	 */
//	static public void showStatus(String statusMessage) {
//		if (m_statusView != null) {
//			m_statusView.setText(statusMessage);
//		}
//	}
	
	public static void updateListHeader( String extraText )  {
		
		//CurrentSectionFragment.updateListHeader( extraText );
		

			// Create a list-header (TextView) and add it to the list like this:
//			mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null); 
//			mLV.addHeaderView(mListHeader);
			
 
			SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US );
			String channelDate = EB2MainActivity.m_channelDate == null? "--" : simpFormat.format(EB2MainActivity.m_channelDate);
			
			CurrentSectionFragment.mListHeader.setText( extraText + EB2MainActivity.mRSSString 
					+" @ "  + channelDate );			// this should be the current date
													// or the date when saved data was saved into the database
	}

    
/*
 * (non-Javadoc)
 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
 */
	@Override
	public boolean onCreateOptionsMenu(Menu main_activity_action) {
	  MenuInflater inflater = getMenuInflater();   // 
		// TODO Name???
	  inflater.inflate(R.menu.listdisplay_activity_action, main_activity_action);         // 
	  return true; // 
	}

	
	/** Called when an options item is clicked.
	 * Handles  itemPrefs, punts on idDeleteSelected, idSaveSelected, or anything else.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int optionSelected = item.getItemId();
		switch (optionSelected) {
		  case R.id.itemPrefs:
			Log.v("Prefs ", "Settings ");
  			startActivity(new Intent(this,SettingsActivity.class));
			break;
		  case R.id.idDeleteSelected:			  
			Log.v("Prefs ", "Settings - unimplemented idDeleteSelected pressed");
			Toast.makeText(context,  "Settings - unimplemented idDeleteSelected pressed", Toast.LENGTH_SHORT).show();
			break;
		  case R.id.idSaveSelected:
			Log.v("Prefs ", "Settings - unimplemented idSaveSelected pressed");
			Toast.makeText(context,  "Settings - unimplemented idSaveSelected pressed", Toast.LENGTH_SHORT).show();
			break;
		  case R.id.idCalendar:
			Log.v("Main menu", " - unimplemented idCalendar pressed");
			Toast.makeText(context,  "Settings - unimplemented idCalendar pressed", Toast.LENGTH_SHORT).show();
			break;
		  case R.id.idShare:
			Log.v("Main menu", " - idShare pressed");
			Toast.makeText(context,  "Main menu - implemented idShare pressed", Toast.LENGTH_SHORT).show();
			Log.d("Main ", " item: " +  item);
			ProcessShare(item.getActionProvider(), item);
			break;
  default:
			Log.d("Main ", " - unimplemented " + Integer.toHexString(optionSelected) + " pressed");
			Toast.makeText(context,  "Settings - unimplemented " + Integer.toHexString(optionSelected) + " pressed", Toast.LENGTH_SHORT).show();
			break;
		}
		return true;   
	}	
	
	
	void ProcessShare(ActionProvider actionProvider, MenuItem item) {
//		boolean bReturn = false;
		ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		
		Toast.makeText(context,  "MainActivity" + " do the Share", Toast.LENGTH_SHORT).show();

		// collect data for sharing - this sends an MMS  ?????
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT,"enters text to send!");
		shareIntent.setType("text/plain");
		
		shareIntent.putExtra(Intent.EXTRA_EMAIL,"this is an extra string");
		String str[] = {"qwerty", "asdfgh"};
		shareIntent.putExtra(Intent.EXTRA_EMAIL,str);

		 mShareActionProvider.setShareIntent(shareIntent);

		Log.d("ProcessShare", " shareIntent "+shareIntent);
		 
		// send off shared data
		startActivity(Intent.createChooser(shareIntent, "Events List"));
		Log.d("ProcessShare", " after chooser "+shareIntent);
//		return bReturn;
	}

// ==========================================================================
// PK 11/9/2014   -- EventsListReader contains the code following here, about 180 lines --
//	
//		This code should contain no references to data belonging to the 
//		current EB2MainActivity (i.e. it is given a URL and it returns  
//		a pointer to a BELEventsList (which can be null if the read fails).
//      that is all it needs to do, optionally it could also return some error code.
// 		It is the callers job to sort out the rest (save successful reads, inform user of result, etc.) 	
//      I would like to take this code out of EB2MainActivity.
	
	/**	Data handling routines, ExecFeedReader is run asynchronously:
	 * <p> 
	 *	 {@link #EventsListReader(URL)}	// Data read from BostonEventsList RSS 	
	 *   	returns m_webEventsList  (the address of the events array)
	 * 		The code has currently a fixed URL, 
	 *   	The Settings activity must be made to select one URL from a list of 
	 *   	current BostonEventsList URLs; and to use the one passed in as an argument.		
	 * <p>
	 *   {@link EB2MainActivity#ReadEventsFromText()}	// Data BostonEventsList RSS converted to string
	 *		Retrieve data from a text file, stored in 'Assets', captured from RSS feed.
	 * 		this is for test purposes, it should be feed to the DOM parser (which 
	 * 		currently chokes on it).
	 */
	public class ExecFeedReader extends AsyncTask<URL, Integer, Integer> {

		// Progress Dialog
	   private ProgressDialog pDialog = null;

	   @Override
	   protected void onPreExecute() {
			super.onPreExecute();
	        pDialog = new ProgressDialog(EB2MainActivity.this);
	        pDialog.setMessage("Reading RSS feed #" + mFeedId + " " + mRSSString +", Please wait...");
	        pDialog.setIndeterminate(false);
			// see http://stackoverflow.com/questions/5253621/android-back-button-and-progress-dialog
	        pDialog.setCancelable(true);	// not sure about this
	        pDialog.show();
	        // have to get it into the  top window
	   }

	   @Override
	   protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			if (null != pDialog) {
				switch(progress.length) {
				case 0: 
					pDialog.setMessage("getting started");
					break;
				case 1:
					pDialog.setMessage("digesting " + progress[0]);
					break;
				case 2:
					pDialog.setMessage("digesting " + progress[0] + " of " + progress[1]);
					break;
				default:
					pDialog.setMessage("digesting " + progress[0] + " of " + progress[1] + ", phase " + progress[2]);
					break;
				}
				if (progress[0] > 10) {	// attempt early response
					currentData.updateList();	
				}
			}
	   }
	   
	   @Override
	   protected void onPostExecute( Integer result ) {
		   super.onPostExecute(result);
		   MakeToast.makeToast(EB2MainActivity.this, "Main: ReadRSS ", MakeToast.LEVEL_DEBUG);
			Log.i(TAG, "onPostExecute, s = " + result );

			if (!m_webEventsList.isEmpty() ) {
				currentData.updateList();
				String  txt = "RSS "+m_webEventsList.size()+": ";
				EB2MainActivity.updateListHeader( txt );

	        } else {
				// What should we say when feed has nothing to show? Could be bad feed, 
				// connectivity issue, or really no events.
	        	Log.e(TAG, "No events at selected feed - so not changing view.");
				Toast.makeText(getApplicationContext(), "No events at selected feed - so not changing view.", Toast.LENGTH_LONG).show();
			}
			
			
			//  remove progress indicator
	        if (null != pDialog) {
	        	pDialog.hide();
	        	pDialog.cancel();
		        pDialog = null;
	        }

// time to activate the display the List in the fragment (ListDisplayFragment)
			
			
////	1) Trying to embed an activity in the fragment is not successful:
////				EventListDisplayActivity.doDisplayEventList();
			
////    2) this way will start ListDisplayFragment all over (of course)			
////				ListDisplayFragment.showWebEventsList();

////    3) implement the status display

			
// TODO   4) activate the list in the fragment 
//           fragment should have a status line and a list (for the events)
//  if the fragment is set-up right this should work:
//  pass the list of events to the adapter: 
//	files   MainAppScreen and MainAppScreenImpl, EventlistAdapter
// 			set the list in the adapter to the current values
			
	   }   ///  end ---   onPostExecute(..)

		@Override
		protected Integer doInBackground(URL... params) {
			// TODO Auto-generated method stub
	        ConnectivityManager connMgr = (ConnectivityManager) 
	            getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	        if (networkInfo != null && networkInfo.isConnected())
	        {
				BELEventlist eventSource= new BELEventlist(this);
				try {
					m_webEventsList = m_webEventsListA.get(mFeedId);
					if (m_webEventsList.isEmpty() ) {
						m_webEventsList = eventSource.getBELEventlist(); // new reads or empty list
						Log.i("got new events for feed " + mFeedId + ", size = " , " " + m_webEventsList.size() );					
					} else {
						mFeedId = oldFeedId;
						Log.d("reused events from feed " + mFeedId + ", size = " , " " + m_webEventsList.size() );					
					}
			        if (EB2MainActivity.m_webEventsListA.get(EB2MainActivity.mFeedId ).isEmpty()
			        		&& ! m_webEventsList.isEmpty()) {
			        	EB2MainActivity.m_webEventsListA.set(EB2MainActivity.mFeedId,  m_webEventsList);
			        	Log.i(TAG, "doInBackground, saved feed with " + m_webEventsList.size() + " events in slot " + mFeedId );
			        }
				} catch(Exception excp) {
					Log.e(TAG, "doInBackground - exception " + excp.toString() 
						+ " in read RSS phase, feedid changed from " +mFeedId 
						+ " to " + oldFeedId, excp );
					mFeedId = oldFeedId;
					m_webEventsList = EB2MainActivity.m_webEventsListA.get(EB2MainActivity.mFeedId);
				}
	        }
	        
			if (!m_webEventsList.isEmpty() ) {
				// Save them for future use in 
				// case next time there is nothing read from the Feed.
				// First throw out previous Saved-Event messages
				try {
					com.tssg.datastore.BELDatastore dataStore = 
						new com.tssg.datastore.BELDatastoreImpl(getBaseContext());
					dataStore.deleteAllWebEvents();
				} catch (DatastoreException e) {
					Log.v(TAG, "doInBackground: dataStore.deleteAllWebEvents: ", e);
				} 
				// Second make a copy of the current list of events
				int kk = 0, len = m_webEventsList.size();
				for (BELEvent next : m_webEventsList) {
					try {
						dataStore.saveWebEvent( next );
						publicProgressCallback(++kk, len, 3);
					} catch (DatastoreException dataExp) {
						Log.e(TAG, "doInBackground: Caught exception trying to save: ", dataExp);
					}
				}
			} else {
				m_webEventsList = dataStore.getAllWebEvents();
				Log.i(TAG, "reused events from datastore, size = " + m_webEventsList.size() );
			}
	
	        return  m_webEventsList.size();
		}
		
		/** Because {@link AsyncTask#publishProgress} is protected, need a callback to update status. */
		public void publicProgressCallback(Integer... values) {
			this.publishProgress(values);
		}
	
	  }	// end --- ExecFeedReader 	

/**
 * This is the code  that must be be executed in an AsyncTask
 * (and the code there must be removed from EventListDisplayActivity  done! ).
 * Uses {@link ExecFeedReader}
*/
    void EventsListReader(URL url)	{
		Log.w(TAG, "before call ExecFeedReader  ");
	    new ExecFeedReader().execute(url);	
		Log.w(TAG, "after call ExecFeedReader  ");
    }	// end of EventsListReader()	
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
	//	if ( m_readEventText == true ) {// begin of  ---- this is for test input
		// Read a test-file name from  Assets  
		String [] files;
		try {
			files = getAssets().list("");
		} catch (IOException e) {
			String message = "Failed to getAssets list";
			Log.e("EventSource", message, e);
			throw new RuntimeException( message, e);
		}
		InputStream stream = null;
		String rxmlFile = null;
		for (String file : files) {
			Log.i("EventSource", "Found Assets item: " + file);
			if (file.contains("rxml")) {
				rxmlFile = file;
				Log.i("EventSource", "Found rxml file: " + rxmlFile);
				try {
					stream = getAssets().open(rxmlFile);
				} catch (IOException e) {
					String message = "Failed to open file: " + rxmlFile;
					Log.e("EventSource", message, e);
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
				;	// drop it
			}
		}
	//
	//	Log.i("EventListDisplay", "rxml file: " + m_mainEventText);
		return 	m_mainEventText;
	 }

	//
	//  eventText is the same as xmlString  in BellSourcedForEvents (lines 101 - 106)
	//
	
	/**
	 * Opens a file somewhere on the device and returns the InputStream pointer
	 * @param fileName - the path to the file to be opened
	 * @return InputStream - the stream that is opened
	 * @throws RuntimeException in response to IOException
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
	String convertStreamToString(java.io.InputStream is) {
		try {
			return new java.util.Scanner(is).useDelimiter("\\A").next();
		} catch (java.util.NoSuchElementException e) {
			return "";
		}
	}  // the end of  ReadEventsFromText  ---  this is for test input 


	/**
	 **      Implements interface {@link EventFragmentCoordinator}, displays the event details (fragment) 
	 **/
	public void displayEventDetails(String eventID, boolean isSavedEvent) {
	//		EventItemFragment mEventItem = null;
			Bundle args = new Bundle();
			args.putString(com.tssg.eventboss2.EventItemFragment.SAVED_KEY, String.valueOf(isSavedEvent));
			EventItemFragment mEventItem = new EventItemFragment();
			mEventItem.setArguments(args);
			mEventItem.setId(eventID);
			Log.i("displayEventDetails begin: ", "w/EventId: "+eventID);
	
		if (mDualPane) {
			Log.i("displayEventDetails: ", "DualPane,"+eventID);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.eventData, mEventItem);				// eventData is used layout-large/activity_main.xml
		Integer i = R.id.eventData;    
		Log.i("id.eventData: ", " "+Integer.toHexString(i));
		Log.i("eventItem ", " "+mEventItem);
			//transaction.addToBackStack(null);
			transaction.commit();
			
		} else {
			
			Log.i("displayEventDetails: ", "|DualPane,"+eventID);
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			transaction.replace(R.id.eventData_s, mEventItem);    // eventData_s is used layout/activity_main.xml
//																  //  work not, crash not	
//			//transaction.addToBackStack(null);
			transaction.commit();
			
		Log.i("eventItem ", " "+mEventItem);
		Log.i("displayEventDetails: ", "start EventDetailActivity "+eventID);
			// In single-pane mode, start the detail event activity for the selected item ID:
			Intent detailIntent = new Intent(this, EventItemFragment.class);
			detailIntent.putExtra(EventItemFragment.EVENTITEM_POS, eventID);

		Log.i("displayEventDetails: ", "w/intent: "+detailIntent.toString());
		Log.i("displayEventDetails: ", "w/EventItem "+mEventItem.toString());

		}
	}
	
	@Override 
	protected void onDestroy() {	// may be superfluous
		super.onDestroy();
		for (List<BELEvent> lbel : m_webEventsListA) {
			if (lbel != Collections.EMPTY_LIST)  {
				lbel.clear();
			}
		}
			
		m_webEventsListA.clear();
	}
	
	// Handle Share processing

}	// ------- end MainActivity
