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
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;


public class SettingsActivity extends Activity
{
	public static final String TAG = "Settings";

	/** How many choices in location button group */
	public static final int RBG_CHOICES = 5;

	View mSettingsView = null;

	private boolean bNewFeed  = false;
	private Integer mFeedId = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/** sets outer class members according to which button clicked */
		class prefclick implements OnClickListener {
			private int iFeedId = 0;
			public prefclick(int feedId) { iFeedId = feedId; }

			public void onClick(View v) {
				// Save the selected Feed Id
				mFeedId = iFeedId;
				// Check if it is not current
				if (iFeedId != EB2MainActivity.mFeedId) {
					Log.i(TAG, "New feed selected: " + mFeedId);
					bNewFeed = true;
				} else {
					bNewFeed = false;
				}
			}
		}

		// Set the ContentView
		setContentView(R.layout.settings_screen);

		// Set up the RSS feed RadioGroup
		final RadioGroup rgFeed = (RadioGroup) findViewById(R.id.feedSelection);

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

		// Preselect the current feed Id
		//rgFeed.check( mFeedId );

		switch(EB2MainActivity.mFeedId)
		{
		case (0):
			rgFeed.check(R.id.choice1);
			break;
		case (1):
			rgFeed.check(R.id.choice2);
			break;
		case (2):
			rgFeed.check(R.id.choice3);
			break;
		case (3):
			rgFeed.check(R.id.choice4);
			break;
		case (4):
			rgFeed.check(R.id.choice5);
			break;
		default:
			rgFeed.check(R.id.choice1);
			break;
		}

		// Log the preselected item
		Log.i(TAG, "button " + EB2MainActivity.mFeedId + " preselected");

		rgFeed.setOnCheckedChangeListener ( new OnCheckedChangeListener() {
			public void onCheckedChanged( RadioGroup rg, int checkedId )
			{
				switch( checkedId )
				{
				case R.id.choice1:
					MakeToast.makeToast(getApplicationContext(),
							"set 1 id = " + Integer.toHexString(checkedId),
							MakeToast.LEVEL_USER);
					break;
				case R.id.choice2:
					MakeToast.makeToast(getApplicationContext(),
							"set 2 id = " + Integer.toHexString(checkedId),
							MakeToast.LEVEL_USER);
					break;
				case R.id.choice3:
					MakeToast.makeToast(getApplicationContext(),
							"set 3 id = " + Integer.toHexString(checkedId),
							MakeToast.LEVEL_USER);
					break;
				case R.id.choice4:
					MakeToast.makeToast(getApplicationContext(),
							"set 4 id = " + Integer.toHexString(checkedId),
							MakeToast.LEVEL_USER);
					break;
				case R.id.choice5:
					MakeToast.makeToast(getApplicationContext(),
							"set 5 id = " + Integer.toHexString(checkedId),
							MakeToast.LEVEL_USER);
					break;
				default:
					MakeToast.makeToast(getApplicationContext(),
							"RG "+rg+"; id " + checkedId,
							MakeToast.LEVEL_USER);
					break;
				}
			}
		}); 

		final Button doneBtn = (Button) findViewById(R.id.idDone);
		doneBtn.setOnClickListener(new Button.OnClickListener()  {
			public void onClick(View v) {
				Log.i(TAG, "on Click() - done");
				MakeToast.makeToast(getApplicationContext(),
						"pressed 'Done' -- bye bye", MakeToast.LEVEL_USER);

				// Don't reread the current Feed Id
				if (bNewFeed) {
					Log.i(TAG, "Reading feed: " + mFeedId);
					
					// EventsListReader(url) will do the reading using the AsyncTask
					MakeToast.makeToast(getApplicationContext(),
							"Read feed # " + mFeedId,
							MakeToast.LEVEL_USER);

					reloadfeed(mFeedId);
				}

				// Return
				finish();
			}
		} );

	}	// end OnCreate ()


	public boolean reloadfeed(int whichfeed) {
		boolean result = false;

		Log.i(TAG, "Replacing feed: " +  EB2MainActivity.mRSSString);

		// Generate and start a new EB2MainActivity
		Intent intent = new Intent(this, EB2MainActivity.class);
		intent.putExtra("feedId", whichfeed);
		startActivity(intent);

		return result;
	}


	/**
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 * 
	 * Called first time user clicks on the menu button
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_main_activity, menu);
		return true;
	};
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		final int itemId = item.getItemId();
		switch (itemId) { 
		
		case R.id.idDone:
		Log.i(TAG, "done button");
			MakeToast.makeToast(this,
				"pressed 'Done'", MakeToast.LEVEL_USER);
			finish();
			break;
		default:
			Log.e(TAG, "unknown button " + Integer.toHexString(itemId));
			MakeToast.makeToast(this,
				"pressed " + Integer.toHexString(itemId), MakeToast.LEVEL_USER);
			break;
		}

		return true; 
	}

}	// end - SettingsActivity

