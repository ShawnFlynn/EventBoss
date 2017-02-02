package com.tssg.eventboss2;


import com.tssg.eventsource.BELEvent;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A list fragment representing a list of Events. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link EventDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
//public class EventListFragment extends ListFragment {
	public class EventItemFragment extends ListFragment {   ///*******************

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventItemFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// TODO: replace with a real list adapter.
///		setListAdapter(new ArrayAdapter<EventContent.BELEvent>(getActivity(),
///				android.R.layout.simple_list_item_activated_1,
///				android.R.id.text1, EventContent.EVENT_ITEMS));
		
	
        setListAdapter(new ArrayAdapter<BELEvent>(getActivity(),
        					android.R.layout.simple_list_item_activated_1,
        					EventListDisplayActivity.m_webEventsList));


	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
//*****		mCallbacks.onItemSelected(EventContent.EVENT_ITEMS.get(position).mId);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}

/*
import java.util.Date;




import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tssg.datastore.DatabaseHelper;
import com.tssg.eventboss2.utils.misc.MakeToast;
import com.tssg.eventsource.BELEvent;

/** displays and handles the details of an event * /
public class EventItemFragment extends Fragment {
	final public static String EVENTITEM_POS = "position"; // compiler suggested taking off
												// 'static'
	public static final String SAVED_KEY = "isSaved";
	int mEventItemPosition = -1;
	private DatabaseHelper dbh;
	private String mId;
	private BELEvent mEvent;
	private TextView mTitleText;
	private TextView mStartText;
	private TextView mEndText;
	private TextView mTypeText;
	private TextView mLinkText;
	private TextView mOrganizerText;
	private TextView mLocationText;
	private TextView mDescriptionText;
	private boolean m_isSavedEvent;
	private Button m_forward = null;
	private Button m_addToCalendar;
	
	public EventItemFragment() {
		m_isSavedEvent = false;
	}


	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		
		if (null != savedInstance && null != savedInstance.get(SAVED_KEY) 
				&& Boolean.parseBoolean((String)savedInstance.get(SAVED_KEY))) {
			m_isSavedEvent = true;
		}
		dbh = new DatabaseHelper(getActivity());		
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle SavedInstanceState) {
		Log.v("EventItemFragment:", " inflate: -> item_fragment, container:"+container);		

		View view = inflater.inflate(R.layout.item_fragment, container, false);
		if (null != SavedInstanceState && null != SavedInstanceState.get(SAVED_KEY) 
				&& Boolean.parseBoolean((String)SavedInstanceState.get(SAVED_KEY))) {
			m_isSavedEvent = true;
		}
		Log.v("EventItemFragment:", " view: " + view);
		
		mTitleText = (TextView) view.findViewById(R.id.titleText);
		mStartText = (TextView) view.findViewById(R.id.startText);
		mEndText = (TextView) view.findViewById(R.id.endText);
		mTypeText = (TextView) view.findViewById(R.id.typeText);
		mLinkText = (TextView) view.findViewById(R.id.linkText);
		mOrganizerText = (TextView) view.findViewById(R.id.organizerText);
		mLocationText = (TextView) view.findViewById(R.id.locationText);
		mDescriptionText = (TextView) view.findViewById(R.id.descriptionText);
		m_forward = (Button)view.findViewById(R.id.forward);
		m_addToCalendar = (Button)view.findViewById(R.id.addtocalendar);
		
		Log.v("EventItemFragment:", " text: " +mTitleText);   // (TextView)
		Log.v("EventItemFragment:", " star: " +mStartText);
		Log.v("EventItemFragment:", " end: " +mEndText);
		Log.v("EventItemFragment:", " type: " +mTypeText);
		Log.v("EventItemFragment:", " link: " +mLinkText);
		Log.v("EventItemFragment:", " org : " +mOrganizerText);
		Log.v("EventItemFragment:", " loc: " +mLocationText);
		Log.v("EventItemFragment:", " desc: " +mDescriptionText);
		
		refreshView();
		
		return view;
	}
	
	public void setId(String id) {
		mId = id;
	}
	
	public void refreshView() {
		// this will reload the data in the view
		if ((mId == null) || (mId.equals(""))) {
			// Handle no data
		} else {
			if (!m_isSavedEvent){
				mEvent = dbh.getEventById(mId);
			}
			else{
				mEvent = dbh.getSavedEventById(mId);
			}
			
			mTitleText.setText(mEvent.getTitle());
			mStartText.setText(mEvent.getStartTime());
			mEndText.setText(mEvent.getEndTime());
			mTypeText.setText(mEvent.getEventType());
			mLinkText.setText(mEvent.getLinkToGroup());
			mLocationText.setText(mEvent.getLocation());
			mDescriptionText.setText(mEvent.getLongDescription());

			Log.v("EventItemFragment:", "in refreshView: ");
			Log.v("EventItemFragment:", " text: " +mTitleText);   // (TextView)
			Log.v("EventItemFragment:", " star: " +mStartText);
			Log.v("EventItemFragment:", " end: " +mEndText);
			Log.v("EventItemFragment:", " type: " +mTypeText);
			Log.v("EventItemFragment:", " link: " +mLinkText);
			Log.v("EventItemFragment:", " org : " +mOrganizerText);
			Log.v("EventItemFragment:", " loc: " +mLocationText);
			Log.v("EventItemFragment:", " desc: " +mDescriptionText);

	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {	// Intents might not work earlier
	        	final EventItemFragment outerThis = this;
		        m_addToCalendar.setOnClickListener(
	        		new View.OnClickListener() {
	        			public void onClick(View v) {
			            	Log.i("EventListDisplayFragment", "want to make an appointment for '" + mEvent.getTitle() + "' " + mEvent.getStartDate());
			            	if (null != mEvent.getStartDate()) {
			            		outerThis.makeAppointment(mEvent.getTitle(), mEvent.getLocation(), mEvent.getStartDate(), mEvent.getEndDate());
			            	}
			                return ;
	        			} });
		        
		        m_forward.setOnClickListener(
		        		new View.OnClickListener() {
		        			public void onClick(View v) {
			            		Log.d("EventListDisplayFragment", "forward button unimplemented");
			            		MakeToast.makeToast(v.getContext(), "onCreate, start trace", MakeToast.LEVEL_DEBUG);
		        			} });
	       }	// finished with OnClickListeners 
		}   // end else clause (we have valid mId)
		
		Log.v("EventItemFragment:", "exit refreshView");

	//java.lang.RuntimeException: Unable to instantiate activity ComponentInfo{
	//	com.tssg.eventboss2/com.tssg.eventboss2.EventItemFragment}:
	//	java.lang.ClassCastException: 
	//	com.tssg.eventboss2.EventItemFragment cannot be cast to android.app.Activity
	//at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2121)
		
	}
	/** make calendar appointment. This should be a controller method.
	 * /
	void makeAppointment(String title, String location, Date start, Date end ) {
        Intent intent =  new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);
        long startL = 0, endL = 0;
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

		startActivity(intent);
		return ;
	}

} // ------- EventItemFragment
// ====== EventItemFragment
 */
