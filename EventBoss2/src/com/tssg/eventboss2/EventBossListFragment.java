/**
 * 
 */
package com.tssg.eventboss2;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.View;
import android.util.Log;

/**
 * @author jjeremie
 * 
 * Represents the base abstraction of a fragment to be displayed in
 * the Event Boss application.
 *
 */
public abstract class EventBossListFragment extends ListFragment {
	
    static final String TAG = "EventBossListFragment";  // log's tag
	/** Indirectly (via an {@link Activity } invokes operations on other fragments **/
	protected EventFragmentCoordinator eventFragmentCoordinator;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		super.onAttach(activity);
		try {
			eventFragmentCoordinator = (EventFragmentCoordinator) activity;
		} catch (ClassCastException e){
			throw new ClassCastException(activity.toString()+" must implement onListSelect");
		}
	}

	public void onLoadFinished() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		Log.v(TAG, "onPrepareOptionsMenu "+menu);
		/*
		 * This class represents the basic building block for user interface components. 
		 * A View occupies a rectangular area on the screen and is responsible for drawing and event handling. 
		 * View is the base class for widgets, which are used to create interactive UI components (buttons, text fields, etc.). 
		 * The android.view.ViewGroup subclass is the base class for layouts, which are invisible containers that hold other Views 
		 * (or other ViewGroups) and define their layout properties. 
		 */
		// TODO Auto-generated method stub
		return;
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	

}
