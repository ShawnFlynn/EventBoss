package com.tssg.eventsource;

/**
 * This is the data object returned to clients of the BELEventSource interface.
 *   It captures an event as it comes from the BEL feed.
 * THIS IS A PLACEHOLDER
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tssg.eventsource.BELEvent;
import com.tssg.eventboss2.utils.date.DateUtils;

/** encapsulate event. setters return self-reference for method chaining
 * @deprecated just using {@link BELEvent} now. */
@Deprecated
public class BELSourcedEvent implements Comparable<BELSourcedEvent>{
	private Integer id;
	private String title;
	private URL link;
	private String eventStartTime;
	private String eventEndTime;
	private String eventType;
	private String eventLink;
	private String eventLocation;
	private String eventOrganizer;
	private String description;
	private String longDescription;
	private Date startDate = null;
	private Date endDate = null;

	// getters and setters
	public Integer getId() {
		return id;
	}

	/** since id is based on hashcode, it should not be set until other fields are set */
	BELSourcedEvent setId() {
		this.id = hashCode();	// that makes a unique id
		return this;  
	}

	// getters and setters omitted for brevity 
	public String getTitle() {
		return title;
	}

	BELSourcedEvent setTitle(String title) {
		this.title = title.trim();
		return this;  
	}

	public URL getLink() {
		return link;
	}

	BELSourcedEvent setLink(String link) {
		try {
			this.link = new URL(link);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
		return this;  	
	}

	public String getEventOrganizer() {
		return eventOrganizer;
	}

	BELSourcedEvent setEventOrganizer(String organizer) {
		this.eventOrganizer = organizer;
		return this;  
	}

	public String getDescription() {
		return description;
	}

	BELSourcedEvent setDescription(String description) {
		this.description = description;
		return this;  
	}

	public String getEventStartTime() {
		return eventStartTime;
	}

	BELSourcedEvent setEventStartTime(String eventStartTime) {
		this.eventStartTime = eventStartTime.trim();
		this.setStartDate(this.eventStartTime);
		return this;  
	}

	public String getEventEndTime() {
		return eventEndTime;
	}

	BELSourcedEvent setEventEndTime(String eventEndTime) {
		String temp = eventEndTime.trim();
		if (null != temp) {
			this.eventEndTime = temp;
			this.setEndDate(this.eventEndTime);
		} else {
			this.eventEndTime = " "; // can't be null (NullPointerException)
		}
		return this;  
	}

	public String getEventType() {
		return eventType;
	}

	BELSourcedEvent setEventType(String eventType) {
		this.eventType = eventType.trim();
		return this;  
	}

	public String getEventLink() {
		return eventLink;
	}

	BELSourcedEvent setEventLink(String eventLink) {
		this.eventLink = eventLink.trim();
		return this;  
	}

	public String getEventLocation() {
		return eventLocation;
	}

	BELSourcedEvent setEventLocation(String eventLocation) {
		this.eventLocation = eventLocation.trim();
		return this;  
	}

	/**
	 * @return value of start date field, or null if date not set
	 */
	public Date getStartDate() {
		Date retval = null;
		if (null != this.startDate) {
			retval = new Date(this.startDate.getTime());	// return copy for safety
		}
		return retval;
	}

	/**
	 * @return value of end date field, or null if date not set
	 */
	public Date getEndDate() {
		Date retval = null;
		if (null != this.endDate) {
			retval = new Date(this.endDate.getTime());	// return copy for safety
		}
		return retval;
	}

	final SimpleDateFormat simpDateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US );

	BELSourcedEvent setStartDate(String sdate) {
		if (sdate != null && !sdate.isEmpty()) {
			try {
				this.startDate = simpDateFormat.parse(sdate);
			} catch (ParseException excp) {
				this.startDate = DateUtils.parseDate(sdate);
			}
		}
		return this;  
	}

	BELSourcedEvent setEndDate(String edate) {
		if (edate != null && !edate.isEmpty()) {
			try {
				this.endDate = simpDateFormat.parse(edate);
			} catch (ParseException excp) {
				this.endDate = DateUtils.parseDate(edate);
			}
		}
		return this;  
	}
	
	public BELSourcedEvent copy(){
		BELSourcedEvent copy = new BELSourcedEvent();
		copy.id = id;
		copy.title = title;
		copy.link = link;
		copy.eventStartTime = eventStartTime;
		copy.eventEndTime = eventEndTime;
		copy.eventType = eventType;
		copy.eventLink = eventLink;
		copy.eventLocation = eventLocation;
		copy.eventOrganizer = eventOrganizer;
		copy.description = description;
		copy.longDescription = longDescription;
		copy.startDate = startDate;
		copy.endDate = endDate;
		return copy;
	}

	@Override
	public String toString() {
		// id is not included
		StringBuilder sb = new StringBuilder();
		Date thisDate = this.getStartDate();
		sb.append("Title: ");
		sb.append(title);
		sb.append('\n');
		sb.append("Link: ");
		sb.append(link);				// the URL? link
		sb.append('\n');
		if ( null != thisDate ) {
			sb.append("Date: ");
			sb.append(thisDate.toString());
		}
		sb.append('\n');
		sb.append("Event Start Time: ");
		sb.append(eventStartTime);
		sb.append('\n');
		sb.append("Event End Time: ");
		sb.append(eventEndTime);
		sb.append('\n');
		sb.append("Event Type: ");
		sb.append(eventType);
		sb.append('\n');
		sb.append("Event Link: ");
		sb.append(eventLink);			// ?
		sb.append('\n');
		sb.append("Event Location: ");
		sb.append(eventLocation);
		sb.append('\n');
		sb.append("Organizer: ");
		sb.append(eventOrganizer);
		sb.append('\n');
		sb.append("Description: ");
		sb.append(description);
		sb.append('\n');
		sb.append("Long Description: ");
		sb.append(longDescription);
		sb.append('\n');
		return sb.toString();
	}

	@Override
	public int hashCode() {
		// hashCode() returns the sum hash for those elements which
		// make two events equal(or different) for the purpose of event display. 
		final int prime = 31;
		int result = 1;
		// id itself is not included
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
		result = prime * result
				+ ((eventStartTime == null) ? 0 : eventStartTime.hashCode());
		result = prime * result
				+ ((eventEndTime == null) ? 0 : eventEndTime.hashCode());
		result = prime * result
				+ ((eventType == null) ? 0 : eventType.hashCode());
		result = prime * result
				+ ((eventLink == null) ? 0 : eventLink.hashCode());
		result = prime * result
				+ ((eventLocation == null) ? 0 : eventLocation.hashCode());
		result = prime * result
				+ ((eventOrganizer == null) ? 0 : eventOrganizer.hashCode());
		// short description not also included (it's a truncated longDescription)
		result = prime * result
				+ ((longDescription == null) ? 0 : longDescription.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		// this is not a true equal function 
		// * since (short) description represents a part of the long description
		//   it is here not considered.
		// * id is 
		if (this == obj)
			return true;
		if (! (obj instanceof BELSourcedEvent) )
			return false;

		BELSourcedEvent other = (BELSourcedEvent) obj;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (startDate == null) {
			if (other.startDate != null)
				return false;
		} else if (!startDate.equals(other.startDate))
			return false;
		if (eventStartTime == null) {
			if (other.eventStartTime != null)
				return false;
		} else if (!eventStartTime.equals(other.eventStartTime))
			return false;
		if (eventEndTime == null) {
			if (other.eventEndTime != null)
				return false;
		} else if (!eventEndTime.equals(other.eventEndTime))
			return false;
		if (eventType == null) {
			if (other.eventType != null)
				return false;
		} else if (!eventType.equals(other.eventType))
			return false;
		if (eventLink == null) {
			if (other.eventLink != null)
				return false;
		} else if (!eventLink.equals(other.eventLink))
			return false;
		if (eventLocation == null) {
			if (other.eventLocation != null)
				return false;
		} else if (!eventLocation.equals(other.eventLocation))
			return false;
		if (eventOrganizer == null) {
			if (other.eventOrganizer != null)
				return false;
		} else if (!eventOrganizer.equals(other.eventOrganizer))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;

		return true;
	}

	public boolean contains(BELEvent otherEvent) {
//		Iterator<BELEvent> iterator = currentEvents.iterator();	//??
//		Iterator<BELEvent> iterator = List<BELEvent>.iterator();//??
//		while (iterator.hasNext()) {
//			BELEvent e1 = iterator.next();
//			if( e1.getId().equals(otherEvent.getId()) )
//			{ 
//				Log.i( "BELSourcedEvent.contains: ", "onCreate: event( "  + otherEvent.getId() +  " == " + e1.getId() );
//				return true;
//			}
//		} 
		return false; 
	}

	/** is the event definitely in the past? 
	 * Note that start and end date may have incorrect timezones, so we need a big fudge-factor.
	 * @return true if end time or start time of event was  more than 24 hours ago.
	 */
	public boolean isPast() {
		long now = System.currentTimeMillis(), timeDiff;
		if (endDate != null) {
			timeDiff = now - endDate.getTime();
		} else if (startDate != null) {
			timeDiff = now - startDate.getTime();
		} else return false;	// fallback
		return timeDiff > 86400*1000L;	// 1 day in past
	}
	
	public int compareTo(BELSourcedEvent another) {
		if (another == null) {
			return 1;
		}
		return another.startDate.compareTo(startDate);
	}
}


