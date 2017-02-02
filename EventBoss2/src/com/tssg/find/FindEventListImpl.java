package com.tssg.find;

import com.tssg.eventsource.*;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

import android.app.Activity;
import android.util.Log;

/** implementer of {@link FindEventList} */
public class FindEventListImpl extends Activity implements FindEventList {
	public boolean LOG = true;
	public boolean LOGBAR = false;
	
	public FindEventListImpl() {
		super();
	}

	/**
	 * match List<BELEvent> if field contains value
	 * 
	 * @return mutable List
	 */
	public List<BELEvent> match(List<BELEvent> input, String field, String value) {
		ArrayList<BELEvent> matchedList = new ArrayList<BELEvent>();
		final String TAG = "FindEventListImpl";
		
		// Parameters ok?
		if (input == null || input.isEmpty()
				|| field == null || field.length() == 0
				|| value == null || value.length() == 0) {
			return matchedList;
		}
		
		Iterator<BELEvent> iterator = input.iterator();
		while (iterator.hasNext()) {
			BELEvent n = iterator.next();
			try {
				// Test for various fields, as strings
				if (LOGBAR) {
					Log.i(TAG, "type is "+n.getEventType());
					Log.i(TAG, "title is "+n.getTitle());
				}
				if (((field.compareToIgnoreCase("type") == 0) 
						&& (n.getEventType().toLowerCase(Locale.US).matches(".*"+value.toLowerCase(Locale.US)+".*")))
					|| (((field.compareToIgnoreCase("title") == 0) 
						&& (n.getTitle().toLowerCase(Locale.US).matches(".*"+value.toLowerCase(Locale.US)+".*"))))) {
					// Append BELEvent to matchedList
					matchedList.add(n);
					if (LOGBAR) Log.i(TAG, "BELEvent "+n.getEventType()+" "+n.getTitle());
				}				
			} catch(Exception e) {
				if (LOG) Log.w(TAG, e);
			}
		}
		matchedList.trimToSize();
		return matchedList;
	}
}