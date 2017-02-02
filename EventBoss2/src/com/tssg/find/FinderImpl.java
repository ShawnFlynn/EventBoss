/**
 * This class provides for searching through event lists for entries that match particular criteria.
 */
package com.tssg.find;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.ArrayList;

import android.util.Log;

import com.tssg.eventsource.BELEvent;
//import android.app.Activity;

//public class FinderImpl extends Activity implements Finder {
public class FinderImpl implements Finder {

	// logging level TODO
	public boolean LOG = true;
	public boolean LOGBAR = true;

	/**
	 * Constructor
	 */
	public FinderImpl() {
		super();
	}

	public enum FindKey {
		TYPE("type"), TITLE("title"), ORGANIZER("organizer"), LOCATION(
				"location"), DESCRIPTION("description"), LONGDESCRIPTION(
				"longDescription");

		private final String key;

		FindKey(String key) {
			this.key = key;
		}

		// return string version of enumeration.
		private String key() {
			return key;
		}

		@Override
		public// return string version of enumeration.
		String toString() {
			return this.key();
		}
	}

	List<BELEvent> targetEventList = null;

	/**
	 * 
	 * Parse a comma separated list and return elements in an array.
	 * 
	 * @param types
	 * @return an array of type strings
	 */

//	private String[] parseTypes(String types) {
//		String[] t = types.split(",");
//		String hdr = null;
//		// remove "Event type:" header from beginning of the string
//		hdr = new String(t[0]);
//		int index = hdr.indexOf(':');
//		t[0] = (hdr.substring(index + 1));
//
//		// remove whitespace from each element
//		for (int i = 0; i < t.length; i++) {
//			t[i] = t[i].trim();
//		}
//		return t;
//	}

	/**
	 * Return a list of BELEvents that have a event date that matches the value
	 * of date.
	 * 
	 * @param input  List of BELEvents.
	 * @param date   A criteria sought after in the List.
	 * 
	 * @return a List of BELEvents that match.  An empty list is returned if there are
	 *  no matches.
	 *  
	 *  If future is true, all events on or after date are returned, sorted, by date
	 * 
	 */
	public List<BELEvent> matchDate(List<BELEvent> input, long date, boolean bFutureEvents) {
		List<BELEvent> matchedList = new ArrayList<BELEvent>();
		final String TAG = "FinderImpl";

		String eventDateStr = null;
		Date eventDate = null;
		Calendar cal = Calendar.getInstance();
		Calendar fieldCal = Calendar.getInstance();
		fieldCal.setTimeInMillis(date);
//		fieldCal.setTime(date);
		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm",Locale.US);

		TreeMap<Long, List<BELEvent>> dateSortedMap = null;

		if (bFutureEvents == true){
			dateSortedMap = new TreeMap<Long, List<BELEvent>>();
		}

		// create calendar set date parameter

		Log.i(TAG, "matchDate inputsize:" + input.size());
		//Log.i(TAG, "matchDate target date:" + date.toString());
		// Iterate over input BELEvents
		Iterator<BELEvent> iterator = input.iterator();
		while (iterator.hasNext()) {
			pos = new ParsePosition(0);
			BELEvent n = iterator.next();
			eventDateStr = n.getStartTime(); // The Date, in string form, from BEL list
			Log.i(TAG, "eventDateStr:" + eventDateStr);
			eventDate = formatter.parse(eventDateStr, pos);
			Log.i(TAG, "eventDate:" + eventDate.toString());
			cal.setTime(eventDate);

			// match the day-of-year and the actual year to determine a match of
			// dates.
			if ((fieldCal.get(Calendar.DAY_OF_YEAR) == cal
					.get(Calendar.DAY_OF_YEAR) && (fieldCal.get(Calendar.YEAR) == cal
					.get(Calendar.YEAR))))
				// date and year matches
				matchedList.add(n);
			
			else if(bFutureEvents == true){
				// event date is after the target date
				if (cal.getTimeInMillis() > date){	
					matchedList.add(n);
					addToSortedMap(cal.getTimeInMillis(), n, dateSortedMap);
				}
			}
		}
		
		if(bFutureEvents == true){
			//replace matchedList with contents of dateSortedMap
			matchedList = convertMap(dateSortedMap);
		}
	return matchedList;	
		
	}


	/*
	 * Take a sorted map of lists and converts it into a single list.
	 */
	List<BELEvent> convertMap(TreeMap<Long, List<BELEvent>> dsm){
		
		List<BELEvent> l = new ArrayList<BELEvent>();
		if (dsm.isEmpty())
			return l;
		
		SortedSet<Long> ss = new TreeSet<Long>(dsm.keySet());
		Iterator <Long>iter = ss.iterator();
		List<BELEvent> dateBin = null;
		while (iter.hasNext()){
			dateBin = dsm.get( iter.next() );
			l.addAll(dateBin);
		}
		
		return l;
	}
	
	//
		void addToSortedMap(long l_time, BELEvent e, TreeMap< Long, List<BELEvent>> dsm ){
			// sort the list by date
			List<BELEvent> l = null;

			if ( dsm.containsKey(l_time) == true){
			// add BELEvent to list
				l = dsm.get(l_time);
				l.add(e);
			}
			else
			{	// add a new list to the map that contains the one event
				dsm.put((long)l_time, new ArrayList<BELEvent>());
				l = dsm.get(l_time);
				l.add(e);
			}
	}

	/**
	 * Return a list of BELEvents from the input that fall within the time
	 * specified by startDate and endDate.
	 * 
	 * @param input  List of BELEvents.
	 * @param startDate  Start of the time range.
	 * @param endDate	End date of the time range.
	 * @return a List of BELEvents that match.  An empty list is returned if there are
	 *  no matches.
	 * 
	 */
	//@Override
	public List<BELEvent> matchDateRange(List<BELEvent> input, Date startDate,
			Date endDate) {
		List<BELEvent> matchedList = new ArrayList<BELEvent>();
		final String TAG = "FinderImpl";

		// TODO Test parameters for startDate, + endDate.

		// TODO log bad input and/or throw some sort of exception
		if (startDate.after(endDate))
			return matchedList;
		// create a Date from the BELEvent

		String eventDateStr = null;
		Date eventDate = null;

		Calendar eventCal = Calendar.getInstance();
		Calendar startCal = Calendar.getInstance();
		Calendar endCal   = Calendar.getInstance();

		startCal.setTime(startDate);
		endCal.setTime(endDate);

		ParsePosition pos = new ParsePosition(0);
		SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy - HH:mm",Locale.US);

		Log.i(TAG, "target start date: " + startDate.toString());
		Log.i(TAG, "target end date  : " + endDate.toString());

		Iterator<BELEvent> iterator = input.iterator();
		while (iterator.hasNext()) {
			pos = new ParsePosition(0);
			BELEvent n = iterator.next();
			eventDateStr = n.getStartTime();
			// Turn a string into a date
			eventDate = formatter.parse(eventDateStr, pos);
			eventCal.setTime(eventDate);
			Log.i(TAG, "current event Date:" + eventDateStr);

			// match the day-of-year and the actual year to determine a match of
			// dates.

			// Case of date range in a single calendar year:
			if ((endCal.get(Calendar.YEAR) == startCal.get(Calendar.YEAR))
					&& (eventCal.get(Calendar.DAY_OF_YEAR) >= startCal
							.get(Calendar.DAY_OF_YEAR))
					&& (eventCal.get(Calendar.DAY_OF_YEAR) <= endCal
							.get(Calendar.DAY_OF_YEAR))) {
				matchedList.add(n);
				Log.i(TAG, " *** current title is a match: " + n.getTitle()
						+ " " + n.getStartTime());
				break;
			}

			// Checking that events are within range based on the year alone.
			if (((eventCal.get(Calendar.YEAR) >= startCal.get(Calendar.YEAR)) && (eventCal
					.get(Calendar.YEAR) <= endCal.get(Calendar.YEAR)))
					&&

					// handle case of event falling on the first year in time
					// range
					(((eventCal.get(Calendar.YEAR) == startCal
							.get(Calendar.YEAR)) && (eventCal
							.get(Calendar.DAY_OF_YEAR) >= startCal
							.get(Calendar.DAY_OF_YEAR)))

							||

							// handle an event falling on the final year in time
							// range
							((eventCal.get(Calendar.YEAR) == endCal
									.get(Calendar.YEAR)) && (eventCal
									.get(Calendar.DAY_OF_YEAR) <= endCal
									.get(Calendar.DAY_OF_YEAR)))

					||

					// other cases fall through to here
					((eventCal.get(Calendar.YEAR) > startCal.get(Calendar.YEAR)) && (eventCal
							.get(Calendar.YEAR) < endCal.get(Calendar.YEAR)))

					)

			)

			{
				matchedList.add(n);
				Log.i(TAG, " Current event is a match. Title: " + n.getTitle()
						+ " " + n.getStartTime());
				Log.i(TAG, "");
			}
		}

		return matchedList;

	}

	//@Override
	/**
	 * Current implementation based on Larry's code.
	 * Returns a list of events that match criteria set by parameters field and value.
	 * 
	 * @param input list of BELEvents to search through.
	 * @param field denotes the key to search.
	 * @param value denotes the value of the key that indicates a match.
	 * 
	 */
	public List<BELEvent> match(List<BELEvent> input, String field, String value) {
		List<BELEvent> matchedList = new ArrayList<BELEvent>();
		final String TAG = "FinderImpl";
		// TODO error check parameters.
		
		String mField = field;
		String mValue = value;
		String testValue = null;

		if (LOG) {
			Log.i(TAG, "seeking match on field: " + mField);
			Log.i(TAG, "seeking match on value: " + mValue);
		}

		if (input == null || input.isEmpty() || field == null
				|| field.length() == 0 || value == null || value.length() == 0) {
			return matchedList;
		}
		

		// Iterate over input BELEvents
		Iterator<BELEvent> iterator = input.iterator();
		while (iterator.hasNext()) {
			BELEvent n = iterator.next();
			try {
				// Test various fields, as strings

				if ( mField.compareTo(Constants.EVENT_DESC)== 0 ){
					testValue = n.getDescription();
				}
				else if ( mField.compareTo(Constants.EVENT_LOC)== 0 ){
					testValue = n.getLocation();
				}
				else if ( mField.compareTo(Constants.EVENT_ORG)== 0 ){
					testValue = n.getOrganizer();
				}
				else if ( mField.compareTo(Constants.EVENT_TITLE)== 0 ){
					testValue = n.getTitle();
				}
				else if ( mField.compareTo(Constants.EVENT_TYPE)== 0 ){
					testValue = n.getEventType();
				}
				else if ( mField.compareTo(Constants.EVENT_DATE)== 0 ){
				// TODO implement a getDate function, investigate getStartTime
					testValue = n.getStartTime();
				}
					
				
				if ( testValue.toLowerCase(Locale.getDefault()).contains(value.toLowerCase(Locale.getDefault()) ))
				{
					matchedList.add(n);
					if (LOG)
						Log.i(TAG, "Match found for Find_Key: " + mField + "Find_Value: " + mValue);
				}		
			
			} catch (Exception e) {
				if (LOG)
					Log.w(TAG, e);
			}
		}
		Log.i(TAG, matchedList.size() + " matches found for " + mField + ", " + mValue);
		return matchedList;
	}

	//@Override
	public List<BELEvent> matchDate(List<BELEvent> input, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	
}
