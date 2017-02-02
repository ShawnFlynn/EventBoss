package com.tssg.eventboss2;


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

/** displays and handles the details of an event */
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
	 */
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
