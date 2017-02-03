package com.tssg.eventboss2;

import com.tssg.datastore.DatabaseHelper;
import com.tssg.eventboss2.utils.misc.MakeToast;




//import android.content.Intent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
//import android.support.v4.app.NavUtils;
//import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
//import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

/**
 * An activity representing a single Event detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link EventListDisplayActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link EventDetailFragment}.
 */
public class EventDetailActivity extends FragmentActivity {

	static final String TAG = "EventDetailActivity";  // log's tag

	EventDetailFragment mDetailFragment = new EventDetailFragment();	////


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_event_detail);
		Log.e(TAG, "onCreate (contentView) " + R.layout.activity_event_detail);
		Log.e(TAG, "Fragment = " + mDetailFragment);

		String s = getIntent().getStringExtra(EventDetailFragment.SAVED_KEY); // "C" or "S"
		Log.e(TAG, "s = " + s);
		boolean savedValue;
		/*
		 this comparison does not work right !!!!!!
		 */
//		if (s == "C") savedValue = false; else savedValue = true;        // "false" or "true"
		savedValue = s.equals("S");
		Log.e(TAG, "for current / saved list = " + savedValue);
		mDetailFragment.setListType(savedValue);
		mDetailFragment.setEventId(getIntent().getStringExtra(EventDetailFragment.EVENTITEM_POS));


		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//

		
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Log.e(TAG, "(savedInstanceState)EventDetailFragment = " + EventDetailFragment.EVENTITEM_POS);

			getSupportFragmentManager().beginTransaction()
							.add(R.id.event_detail_container, mDetailFragment).commit();
		}
	}

    //public void onStart() {
    //    super.onStart();
     //   EventDetailFragment:refreshView();
    //}

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	// inflate menu items for the action bar
    	MenuInflater inflater= getMenuInflater();
    	inflater.inflate(R.menu.detail_activity_actions, menu);
    	return super.onCreateOptionsMenu(menu);
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this, EventDetailActivity.class));
			   MakeToast.makeToast(this, "Up Nav - not implemented", MakeToast.LEVEL_DEBUG);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
