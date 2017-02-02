package com.tssg.eventboss2;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tssg.eventboss2.EB2MainActivity;  /// BelEvent
import com.tssg.eventsource.BELEvent;

/**
 * A fragment representing a single Event detail screen. This fragment is either
 * contained in a {@link EventListActivity} in two-pane mode (on tablets) or a
 * {@link EventDetailActivity} on handsets.
 */
public class EventDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The event content this fragment is presenting.
	 */
//	private EventContent.BELEvent mEvent;			
//	private CurrentSectionFragment mEvent = EB2MainActivity.currentData;			// store an EventItem
	/////     this should be ????????????????
	BELEvent mEvent = EB2MainActivity.currentData.mEvent;			// ? from what is now xxx.event

	
	
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventDetailFragment() {
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the event content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
//			mEvent = EventContent.EVENT_ITEM_MAP.get(getArguments().getString(
//					ARG_ITEM_ID));   //###################### get this from the currentData
/////			int position = mEvent(ARG_ITEM_ID);
//			mEvent = (EventListDisplayActivity.m_webEventsList.get(position).getId());
			////// really??? (ARG_ITEM_ID));
			int position =1;
			mEvent = (EventListDisplayActivity.m_webEventsList.get(position));
			////???????????????????
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_event_detail,
				container, false);

		// Show the event content as text in TextViews. 
														
		if (mEvent != null) {
			/*
			((TextView) rootView.findViewById(R.id.event_title))
                        .setText(mEvent.getTitle());
			((TextView) rootView.findViewById(R.id.event_time))
                        .setText(mEvent.getStartTime());
			((TextView) rootView.findViewById(R.id.event_end_time))
	                    .setText(mEvent.getEndTime());
			((TextView) rootView.findViewById(R.id.event_type))
                        .setText(mEvent.getEventType());
			((TextView) rootView.findViewById(R.id.event_url))
                        .setText(mEvent.getLinkToGroup());
			((TextView) rootView.findViewById(R.id.event_organizer))
                        .setText(mEvent.getOrganizer());
			((TextView) rootView.findViewById(R.id.event_location))
                        .setText(mEvent.getLocation());
			((TextView) rootView.findViewById(R.id.event_short_desc))
                        .setText(mEvent.getDescription());
//	        ((TextView) rootView.findViewById(R.id.event_detail))
//			            .setText(mEvent.mLongDescription);
 * */

			((TextView) rootView.findViewById(R.id.startText))
            		.setText(mEvent.getStartTime());
			((TextView) rootView.findViewById(R.id.endText))
            		.setText(mEvent.getEndTime());
			((TextView) rootView.findViewById(R.id.typeText))
            		.setText(mEvent.getEventType());
			((TextView) rootView.findViewById(R.id.linkText))
            		.setText(mEvent.getLinkToGroup());
			((TextView) rootView.findViewById(R.id.organizerText))
            		.setText(mEvent.getOrganizer());
			((TextView) rootView.findViewById(R.id.locationText))
            		.setText(mEvent.getLocation());
			((TextView) rootView.findViewById(R.id.descriptionText))
            		.setText(mEvent.getDescription());
//((TextView) rootView.findViewById(R.id.event_detail))
//            .setText(mEvent.mLongDescription);
		}   

		return rootView;
	}
}
