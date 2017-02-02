package com.tssg.eventsource;

import com.tssg.eventboss2.EB2MainActivity;
import com.tssg.eventsource.BELEvent;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**   called to create the current List of BELEvents.
 *   The function getBELEventlist()  opens an
 *   IOStream and reads from the Boston Events List's RSS feed 
 *   (if it can open a stream and read from it).
 *   
 *   It returns a list of BELEvents (or an empty list if the read fails).
 */
public class BELEventlist {

	public boolean bLOGGING = false;					// enable/disable logging
	public static final String TAG = "BELEventsList";	// log's tag
	private EB2MainActivity.ExecFeedReader currentTask = null;
	
	//TODO  feedUrl will is set in main activity; user selectable in settings
	public String feedUrl = EB2MainActivity.mURLString;		// a URL
	public String feedName = EB2MainActivity.mRSSString;	// in English
	// private static final int maxDescr = 100;		// limit short description to 100 chars
	private List<BELEvent> rssEventList;

	/** @param currentTask is needed to publish [progress callback */
	public BELEventlist(EB2MainActivity.ExecFeedReader currentTask) {
		super();
		this.currentTask = currentTask;
	}
	
	/** Loads the RSS feed 
	 * @return EMPTY_LIST on error
	 */
	public List<BELEvent> getBELEventlist(){
		// open the RSS feed:
		// reads, formats Events and returns a list of Events or
		// (if there were exceptions) it returns an empty list
		try {
			List<BELEvent> events = loadFeed( feedUrl );
			return events;						// the list of events
		} catch (RuntimeException e) {
			Log.e(TAG,e.getMessage(),e);
			return Collections.<BELEvent>emptyList();	// an empty list
		}
	}

	/**
	 * Load all the events from the RSS feed
	 * @param feedUrl the URL for the RSS feed
	 * @return List<BELEventList> The list of events
	 * @throws RuntimeException on any error
	 */
	private List<BELEvent> loadFeed( String feedUrl ) { 
		try {
			BELSourceForEvents eventSource = new BELSourceForEventsImpl(feedUrl, this.currentTask);
			long start = System.currentTimeMillis();
			rssEventList = eventSource.getCurrentEventList();	// here the parser makes BELSourcedEvent msg
			long duration = System.currentTimeMillis() - start;
			Log.i(TAG, "Parser duration=" + duration + ", size=" + rssEventList.size());
		} catch (Exception e){
			Log.e(TAG,e.getMessage(),e);
			throw new RuntimeException( e );
		}
		return rssEventList;
	}	// LoadFeed

}
