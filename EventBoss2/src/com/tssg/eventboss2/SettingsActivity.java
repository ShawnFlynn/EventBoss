package com.tssg.eventboss2;

import com.tssg.eventboss2.utils.misc.MakeToast;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


/** use shared preferences for EB settings like the selected Eventlist RSS feed
 * there may be more user options to be implemented in this module

      // We need an Editor object to make preference changes.

      // All objects are from android.context.Context
      SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
      SharedPreferences.Editor editor = settings.edit();
      editor.putBoolean("silentMode", mSilentMode);
      
      CHECK AT LINE 80: the Id m
       ust be 0, the value the selected feed name !!!!
      And the Resources are no longer needed; can remove 
 */

// TODO - unclear if the commit editor code is executed always
// TODO - calling EventsListReader(url)  is not working



public class SettingsActivity extends Activity // implements OnClickListener 
{ 
	public static final String TAG = "Settings";	// log's tag
	/** How many choices in location button group */
	public static final int RBG_CHOICES = 5;

	View mSettingsView = null;
	
	public static final String PREFS_NAME = "EB2Settings";
    SharedPreferences mSettings;
    private boolean mSilentMode = true;
    volatile Integer mFeedId = 0;
    private String mFeedValue = "";
    volatile boolean  bNewFeed  = false;

    @Override
	protected void onCreate(Bundle savedInstanceState) { // 
		super.onCreate(savedInstanceState);
		
	    /** sets outer class members according to which button clicked */
		class prefclick implements OnClickListener {
	    	private int iFeedId = 0;
	    	public prefclick(int feedId) { iFeedId = feedId; }
	    	
			public void onClick(View v) {
				bNewFeed  = true;
				mFeedId = iFeedId;  // the index if the selection
			}
		}
	    
        setContentView(R.layout.settings_screen);

		MakeToast.makeToast(this, "Settings Activity", MakeToast.LEVEL_USER);

	    // Restore preferences   ------------------------------------
	    mSettings = getSharedPreferences(PREFS_NAME, 0);
	    mSilentMode = mSettings.getBoolean("silentMode", true);
	    mFeedId = mSettings.getInt("FeedId", mFeedId);		// id is like the index into the resources list	
	    mFeedValue = mSettings.getString("FeedValue", mFeedValue);	
/*<<<<<<< .mine

		mResources = getResources();			// data to be set up in the RadioGroup rgFeed
		mRSSsources = mResources.getStringArray(R.array.rss_src_names); // for user: name
		mRSSnames = mResources.getStringArray(R.array.eventslists);		// for computer: URL
//=======
//>>>>>>> .r2034
*/	
		final RadioGroup rgFeed = (RadioGroup) findViewById(R.id.feedSelection);	// RadioGroup
		
//		rgFeed.setOnClickListener( new OnClickListener(whichButton) );

		
//		rgFeed.setOnCheckedChangeListener( new OnCheckedChangeListener listener ) {
//			mFeedId = rgFeed.getCheckedRadioButtonId();  };	// the index of the selection

		
		final RadioButton choice1 = (RadioButton) findViewById(R.id.choice1);
		choice1.setOnClickListener(new prefclick(0) );

		final RadioButton choice2 = (RadioButton) findViewById(R.id.choice2);
		choice2.setOnClickListener(new prefclick(1) );

		final RadioButton choice3 = (RadioButton) findViewById(R.id.choice3);
		choice3.setOnClickListener(new prefclick(2) );

		final RadioButton choice4 = (RadioButton) findViewById(R.id.choice4);
		choice4.setOnClickListener(new prefclick(3) );

		final RadioButton choice5 = (RadioButton) findViewById(R.id.choice5);
		choice5.setOnClickListener(new prefclick(4) );

		rgFeed.check( mFeedId );      
		Log.e(TAG, "button checked 2: "+mFeedId);

		rgFeed.setOnCheckedChangeListener ( new OnCheckedChangeListener() {
			public void onCheckedChanged( RadioGroup rg, int checkedId )
			{
				switch( checkedId )
				{
				case R.id.choice1:
					MakeToast.makeToast(getApplicationContext(), "set 1 id = "+Integer.toHexString(checkedId), MakeToast.LEVEL_USER);
					break;
				case R.id.choice2:
					MakeToast.makeToast(getApplicationContext(), "set 2 id = "+Integer.toHexString(checkedId), MakeToast.LEVEL_USER);
					break;
				case R.id.choice3:
					MakeToast.makeToast(getApplicationContext(), "set 3 id = "+Integer.toHexString(checkedId), MakeToast.LEVEL_USER);
					break;
				case R.id.choice4:
					MakeToast.makeToast(getApplicationContext(), "set 4 id = "+Integer.toHexString(checkedId), MakeToast.LEVEL_USER);
					break;
				case R.id.choice5:
					MakeToast.makeToast(getApplicationContext(), "set 5 id = "+Integer.toHexString(checkedId), MakeToast.LEVEL_USER);
					break;
				default:
					MakeToast.makeToast(getApplicationContext(), "RG "+rg+"; id "+checkedId, MakeToast.LEVEL_USER);
					break;
				}
			}
		}); 

		
		final Button doneBtn = (Button) findViewById(R.id.idDone);
		doneBtn.setOnClickListener(new Button.OnClickListener()  {
			public void onClick(View v) {
				Log.e(TAG, "on Click- done");
				MakeToast.makeToast(getApplicationContext(), "pressed 'Done' -- bye bye", MakeToast.LEVEL_USER);
				//** EventsListReader(url) will do the reading using the AsyncTask
				MakeToast.makeToast(getApplicationContext(), "Restart reading feed #" +mFeedId , MakeToast.LEVEL_USER);
//				Log.e(TAG+" --> read RSS:"," "+mRSSnames[mFeedId]);
				reloadfeed(mFeedId);
				finish();
			}
		} );
		
	}	// end OnCreate ()
	

	public boolean reloadfeed(int whichfeed) {
		boolean result = false;
		Log.i("SettingsActivity  ", "reloading feed: " +  EB2MainActivity.mRSSString);
		Intent intent = new Intent(this, EB2MainActivity.class);
		intent.putExtra("feedId", whichfeed);
		startActivity(intent);
				
		return result;
	}
	

    @Override
    protected void onStop(){
       super.onStop();

      // We need an Editor object to make preference changes.
      // All objects are from android.context.Context
      Log.e(TAG, "onStop "+mSettings);
      SharedPreferences.Editor editor = mSettings.edit();
      Log.e(TAG, "onStop "+editor);
      editor.putBoolean("silentMode", mSilentMode);
      Log.e(TAG, "onStop "+mSilentMode);
      editor.putInt("FeedId", mFeedId);			// the currently selected (RSS)feed id
      editor.putString("FeedValue",mFeedValue); //   and Value

      Log.e(TAG, "onStop "+mFeedId+" "+mFeedValue);

      editor.commit();
    }

	
	/**
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 * 
	 * Called first time user clicks on the menu button
	 */
/*
 * 	@Override(non-Javadoc)
 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	  MenuInflater inflater = getMenuInflater();   // 
	  inflater.inflate(R.menu.eb_settings, menu);         // 
	  return true; // 
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		switch (itemId) { 
		
		case R.id.idDone:
		Log.e(TAG, "done  button");
			MakeToast.makeToast(this, "pressed 'Done'", MakeToast.LEVEL_USER);
			finish();
			break;
		default:
			Log.e(TAG, "unknown button " + Integer.toHexString(itemId));
			MakeToast.makeToast(this, "pressed " + Integer.toHexString(itemId), MakeToast.LEVEL_USER);
			break;
		}

		return true; 
	}

}	// PrefsActivity

