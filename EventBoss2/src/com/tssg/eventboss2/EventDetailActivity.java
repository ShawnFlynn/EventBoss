package com.tssg.eventboss2;

import com.tssg.datastore.DatabaseHelper;
import com.tssg.eventboss2.utils.misc.MakeToast;







import android.content.Context;
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
import android.widget.ShareActionProvider;
import android.widget.Toast;

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
	
    public Context context = this;
	public static long mId;
	private DatabaseHelper mDbh;		// = new DatabaseHelper(getActivity())

	EventDetailFragment mDetailFragment = new EventDetailFragment();	////


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_event_detail);
		Log.e(TAG, "onCreate (contentView) " + R.layout.activity_event_detail);
		Log.v(TAG, "Fragment = " + mDetailFragment);

		mDbh = new DatabaseHelper(this);
		
//		String s = getIntent().getStringExtra(EventDetailFragment.SAVED_KEY); // "C" or "S"
//		Log.v(TAG, "s = " + s);
//		boolean savedValue = s.equals("S");
//		Log.v(TAG, "for current / saved list = " + savedValue);
///		int s = getIntent().getStringExtra(EventDetailFragment.LIST_TYPE); // 0, 1, 2

		int s = getIntent().getIntExtra(EventDetailFragment.LIST_TYPE, 0); // 0, 1, 2
		Log.v(TAG, "s = " + s);
		Log.v(TAG, "for current / saved list = " + s);
			mDetailFragment.setListType(s);		// the type (current, saved, search) List to use
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
			Log.v(TAG, "(savedInstanceState)EventDetailFragment = " + EventDetailFragment.EVENTITEM_POS);

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
    	inflater.inflate(R.menu.menu_detail_activity, menu);
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
			MakeToast.makeToast(this, "Up Nav - implemented", MakeToast.LEVEL_DEBUG);
			break;

		case R.id.idSaveSelected:
            /* can do this only if in CurrentSectionFragment */
            Log.v(TAG, "Save Selected - "+CurrentSectionFragment.mId);
			if(CurrentSectionFragment.mId == 0)  {
				Toast.makeText(context, TAG+" - Save only from currentTab", Toast.LENGTH_LONG).show();
				break; }
            Toast.makeText(context, TAG+" - Save selected event", Toast.LENGTH_LONG).show();
            String strEvent = String.format("%d", CurrentSectionFragment.mId); 
            Log.v(TAG, "strEvent: "+strEvent +" from mId :"+ CurrentSectionFragment.mId);
            mDbh.saveEvent(strEvent);
            // 	the CurrentSectionFragment must reload the data table
            Log.v(TAG, "saveSelected  updateList()"+ CurrentSectionFragment.mId);
            break;

		case R.id.idDeleteSelected:
            /* ca do this only if in SavedSectionFragment */
			if(SavedSectionFragment.mId == 0)  {
				Toast.makeText(context, TAG+" - Save only from SavedTab", Toast.LENGTH_LONG).show();
				break; }
            Log.v(TAG, "Delete Selected - "+SavedSectionFragment.mId);
            Toast.makeText(context, TAG+" - Delete Selected Saved", Toast.LENGTH_LONG).show();
            strEvent = String.format("%d", SavedSectionFragment.mId); 
            Log.v(TAG, "strEvent: "+strEvent +" from mId :"+ SavedSectionFragment.mId);
            mDbh.deleteSavedEvent(strEvent);
 			// the SavedSectionFragment must reload the data table
            Log.v(TAG, "DeleteSelected call  updateList(), after del "+ SavedSectionFragment.mId);
            break;
            
        case R.id.idCalendar:
            Log.v(TAG, " Calendar");
            Toast.makeText(context, TAG + " Calendar", Toast.LENGTH_SHORT).show();
            // TODO  code to save in fragment - ... an mId
            break;
            
        case R.id.action_share:
            Log.v(TAG, " - idShare pressed");
            Toast.makeText(context,  TAG + " Share", Toast.LENGTH_SHORT).show();
            // TODO  must send an mId to the share
            Log.d(TAG, " item: " +  SavedSectionFragment.mId);  // TODO or from SavedSectionFragment
            ProcessShare(item);
        return true;
		}
		return super.onOptionsItemSelected(item);
	} //end   onOptionsItemSelected(MenuItem ..)	
    
	void ProcessShare(MenuItem item) {

		ShareActionProvider mShareActionProvider = (ShareActionProvider) item.getActionProvider();

		//Toast.makeText(this, mResources.getString(R.string.doTheShare) + " ", Toast.LENGTH_SHORT).show();
		Toast.makeText(this, "doTheShare" + " ", Toast.LENGTH_SHORT).show();

		// collect data for sharing - this sends an MMS  ?????
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.putExtra(Intent.EXTRA_TEXT, "enters text to sent!");
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_EMAIL, "this is an extra string");
		String str[] = {"qwerty", "asdfgh"};
		shareIntent.putExtra(Intent.EXTRA_EMAIL, str);

		mShareActionProvider.setShareIntent(shareIntent);

		Log.d("ProcessShare", " shareIntent " + shareIntent);

		// send off shared data
		startActivity(Intent.createChooser(shareIntent, "Events List"));
		Log.d("ProcessShare", " after chooser " + shareIntent);

	}   // end --- ProcessShare

}  // end ---
