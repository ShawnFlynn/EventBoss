package com.tssg.eventboss2;

import android.support.v4.app.FragmentActivity;
import android.content.Intent;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.util.Log;

import java.util.Date;	//import java.sql.Date; -old-
import java.util.Locale;

import com.tssg.eventsource.BELEvent;
//import com.tssg.eventboss2.utils.misc.MakeToast;


public class CalendarAppointment //extends FragmentActivity
	{
//	public void makeAppointment(String title, String location, Date start, Date end ) {  // -old-
	public static Intent makeCalendarAppointment(BELEvent event) {
		
		final String TAG = "CalendarAppointment";   // log's tag
		
		BELEvent m_Event = event;
        Log.d(TAG, " item: " +  CurrentSectionFragment.mId);  // TODO or from SavedSectionFragment
		Intent intent =  new Intent(Intent.ACTION_INSERT, Events.CONTENT_URI);

     	String title = m_Event.getTitle();
     	String location = m_Event.getLocation();
     	String start = m_Event.getStartTime();
     	String end = m_Event.getEndTime();
    Log.d(TAG, ">>>> " + start + " / " + end);

//		Date  m_startDate = null;
//    	Date  m_endDate = null;		// = java.util.Date
     	Date startDate = (Date) event.getStartDate();
     	Date endDate = (Date) event.getEndDate();
	Log.d(TAG, ">>>> " + startDate + " // " + endDate);
     	
/*     	event = start.getStartTime();
//    	long startL, endL;
    	if (null != start) {
//        	startL = (long) start.setStartDate();
//        	startL = (long) start;

        	intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);
    	}
*/
    	
/*    	if (null != end) {
            endL = end.getTime();
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endL);
        }
*/
        
//    	intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start);	// replace by next lines
//    	intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
    	intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, event.getStartDate());
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, event.getEndDate());
// either versions seem to use current date rather than the events date
        
        intent.putExtra(Events.TITLE, title);
        intent.putExtra(Events.ALL_DAY, false);
        intent.putExtra(Events.EVENT_LOCATION, location);
        
		Log.d(TAG, " " + intent);
		return intent;
//        startActivity(intent);
        
	}	//  end CalendarAppointment


}
