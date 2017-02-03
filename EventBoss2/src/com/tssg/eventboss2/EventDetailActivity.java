package com.tssg.eventboss2;

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
 * side-by-side with a list of items in a {@link EventListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link EventDetailFragment}.
 */
public class EventDetailActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_event_detail);
		Log.e("-> EventDetailAct: ", "onCreate (item_frag) "+R.layout.activity_event_detail);

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
            Log.e("EventDetailAct", "savedInstanceState");
			Log.e("EventDetailAct", "EventDetailFragment = "+EventDetailFragment.EVENTITEM_POS);

			Bundle arguments = new Bundle();
			arguments.putString(EventDetailFragment.EVENTITEM_POS, getIntent()
					.getStringExtra(EventDetailFragment.EVENTITEM_POS));
			Log.e("EventDetailAct: ", "savedInstanceState (null) ");

			EventDetailFragment fragment = new EventDetailFragment();
			Log.e("EventDetAct:", "EventItemFragment = "+fragment);
			// any init ??

			fragment.setArguments(arguments);
			Log.e("EventDetailActivity: ", " arguments: "+fragment.getArguments());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.event_detail_container, fragment).commit();

			Log.e("EventDetAct", "fragment "+ fragment +"+arguments: "+arguments);
		}
	}

    public void onStart() {
        super.onStart();
     //   EventDetailFragment:refreshView();
    }

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
