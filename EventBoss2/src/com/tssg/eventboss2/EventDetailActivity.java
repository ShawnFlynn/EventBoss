package com.tssg.eventboss2;

import com.tssg.datastore.DatabaseHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

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
//		setContentView(R.layout.activity_event_detail);

//		Log.e("enter EventDetailActivity: ", "onCreate (item_fragment) "+R.layout.eventData);
		Log.e("enter EventDetailActivity: ", "onCreate (item_fragment) "+R.layout.item_fragment);
		
		setContentView(R.layout.item_fragment);
		
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

			Log.e("enter EventDetailActivity: ", "EventItemFragment = "+EventItemFragment.EVENTITEM_POS);

			Bundle arguments = new Bundle();
			arguments.putString(EventItemFragment.EVENTITEM_POS, getIntent()
					.getStringExtra(EventItemFragment.EVENTITEM_POS));
			Log.e("enter EventDetailActivity: ", "savedInstanceState (null) ");

			EventItemFragment fragment = new EventItemFragment();   //???
			Log.e("enter EventDetailActivity.onCreate ", "EventItemFragment = "+fragment);
			// any init ??

			fragment.setArguments(arguments);
			Log.e("enter EventDetailActivity: ", " arguments: "+fragment.getArguments());
			getSupportFragmentManager().beginTransaction()
					.add(R.id.itemfragment, fragment).commit();

			Log.e("enter EventDetailActivity:onCreate ", "fragment+arguments"+ fragment +" | "+arguments);

		}
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
//			NavUtils.navigateUpTo(this, new Intent(this,
//					EventListActivity.class));
			   MakeToast.makeToast(this, "Up Navnot implemented", MakeToast.LEVEL_DEBUG);

			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
