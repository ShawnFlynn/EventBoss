package com.tssg.eventboss2;

import android.content.Intent;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

import java.util.Date;	//import java.sql.Date; -old-

import com.tssg.eventsource.BELEvent;
//import com.tssg.eventboss2.utils.misc.MakeToast;


public class CalendarAppointment //extends FragmentActivity
	{
	public static Intent makeCalendarAppointment(BELEvent event) {

		final String TAG = "CalendarAppointment";   // log's tag

		Log.i(TAG, "makeClaendarAppointment()");

		BELEvent m_Event = event;
		Log.d(TAG, " item: " +  CurrentSectionFragment.mId);
		Intent intent =  new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);

		String title = m_Event.getTitle();
		String location = m_Event.getLocation();
		String start = m_Event.getStartTime();
		String end = m_Event.getEndTime();
		Log.d(TAG, ">>>> " + start + " / " + end);

//		Date  m_startDate = null;
//		Date  m_endDate = null;		// = java.util.Date
		Date startDate = (Date) event.getStartDate();
		Date endDate = (Date) event.getEndDate();
		Log.d(TAG, ">>>> " + startDate + " // " + endDate);

/*		event = start.getStartTime();
//		long startL, endL;
		if (null != start) {
//		tartL = (long) start.setStartDate();
//		startL = (long) start;

		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
		}
*/

/*		if (null != end) {
			endL = end.getTime();
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endL);
	}
*/

//		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);	// replace by next lines
//		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
		intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartDate());
		intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndDate());
//		either versions seem to use current date rather than the events date

		intent.putExtra(Events.TITLE, title);
		intent.putExtra(Events.ALL_DAY, false);
		intent.putExtra(Events.EVENT_LOCATION, location);

		Log.d(TAG, " " + intent);
		return intent;
//		startActivity(intent);

	}	//  end  - makeCalendarAppointment()

}	//  end - CalendarAppointment
