/**
 * 
 */
package com.tssg.eventboss2;

import android.app.Activity;
import android.support.v4.app.ListFragment;

/**
 * @author jjeremie
 * 
 * Represents the base abstraction of a fragment to be displayed in
 * the Event Boss application.
 *
 */
public abstract class EventBossListFragment extends ListFragment {
	
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
	
	

}
