/**
 * 
 */
package com.tssg.eventsource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for parsing various pieces of Boston Event List (BEL) events.
 * @deprecated In 2014 this appears to be unused.
 * @author Kathy
 */
@Deprecated
public class BELEventParser {
	private static String NEWLINE = System.getProperty("line.separator");

	/**
	 * Find the event start time in the xml text and return it .
	 * This is what I see in the feed: 
	 * &lt;p class="pDate"&gt;Time : 2014-05-13 2000-01-01 14:00:00 UTC	  Ending :   - Eastern Time (US &amp; Canada)
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String The event start time
	 */
	public String parseEventStartTime(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pDate\">");
		Matcher m = p.matcher(xmlString);
		int start = 0;
		if (m.find()){
			start = m.end();
		} else {
			return "(A) parseEventStartTime() Failure";
		}

		String s = xmlString.substring(start, 80);

		p = Pattern.compile("Time : ");
		m = p.matcher(s);
		start = 0;
		if (m.find()){
			start = m.end();
		} else {
			return "(B) parseEventStartTime() Failure";
		}

		p = Pattern.compile("UTC");
		m = p.matcher(s);
		int end = 0;
		if (m.find()) {
			end = m.start();
		} else {
			return "(c) parseEventStartTime() Failure";
		}
		s = s.substring(start, end);		//"Time : 2012-01-20 Sat Jan 01 08:20:00 UTC 2000 Ending : "...
		String s1 = s.substring(5,7) + "/";	 	//month
		s1 = s1 + s.substring(8,10) + "/";		//day
		s1 = s1 + s.substring(0,4) + " ";		//year
		s1 = s1 + s.substring(22,27) + " ";		//dd:mm
		return s1;
	}



	/**
	 * Find the event end time in the xml text and return it 
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String The event end time
	 */
	public String parseEventEndTime(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pDate\">");
		Matcher m = p.matcher(xmlString);
		int start = 0;
		if (m.find()){
			start = m.end();
		} else {
			return "(A) parseEventEndTime() Failure";
		}

		p = Pattern.compile("Ending : ");
		m = p.matcher(xmlString);
		start = 0;
		if (m.find()){
			start = m.end();
		} else {
			return "(B) parseEventEndTime() Failure";
		}

		String s = xmlString.substring(start, start + 40);

		p = Pattern.compile("-");
		m = p.matcher(s);
		int finish = 0;
		if (m.find()) {
			finish = m.start();			
		} else {
			return "(C) parseEventEndTime() Failure";
		}

		// if we have " - " and "Eastern": there is no ending date
		// if we have "2012" it is the beginning of a date string 
		//    and the " - " is at the end 
		if (finish < 3) {
			return " ";	//
		} else {
			s = xmlString.substring(start, start+30); //..."Ending : 2012-01-20 Sat Jan 01 10:20:00 UTC 2000 - Eastern Time ..." 
			String s1 = s.substring(5,7) + "/";	 	//month
			s1 = s1 + s.substring(8,10) + "/";		//day
			s1 = s1 + s.substring(0,4) + " ";		//year
			s1 = s1 + s.substring(22,27) + " ";		//dd:mm
			return s1;
		}
	}

	/**
	 * Find the event type in the xml text and return it 
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String the Event type
	 */
	public String parseEventType(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pContent\">Event Type : ");   
		Matcher m = p.matcher(xmlString);
		int start = 0;
		if (m.find()){
			start = m.end();
		} else { 
			return "(A) parseEventType() Failure"; 
		}

		String s = xmlString.substring(start, xmlString.length());

		p = Pattern.compile("</p>");		// this should be the end
		m = p.matcher(s);
		if (m.find()) {
			s = s.substring(0, m.start());
		} else {
			return "(B) parseEventType() Failure";
		}


		s = s.replace(NEWLINE, " ");	//	replace newline by a space
		s = s.replaceAll(" ",""); 		//	remove all spaces
		return s;
	}

	/**
	 * Parse the event link
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String The web link to the event
	 */
	public String parseEventLink(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pContent\"><b>Link to website/contact:</b>");
		Matcher m = p.matcher(xmlString);

		int start = 0;
		if (m.find()){
			start = m.end();
		} else {
			return "(A) parseEventLink() Failure";
		}

		String s = xmlString.substring(start, xmlString.length());

		p = Pattern.compile(">");
		m = p.matcher(s);
		if (m.find()) {
			start = m.end();
		} else { 
			return "(B) parseEventLink() Failure"; 
		}

		String s2 = s.substring(start, s.length());
		p = Pattern.compile("</a>");
		m = p.matcher(s2);
		if (m.find()) {	
			return s2.substring(0, m.start());
		} else {
			return "(C) parseEventLink() Failure";
		}

	}


	/**
	 *  Parse the event location
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String The event location
	 */
	public String parseEventLocation(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pContent\"><b>Location:</b>");
		Matcher m = p.matcher(xmlString);
		int start = 0;
		if (m.find()){
			start = m.end();
		} else { 
			return "(A) parseEventLocation() Failure"; 
		}

		String s = xmlString.substring(start, xmlString.length());

		p = Pattern.compile("</p>");
		m = p.matcher(s);
		if (m.find()) {
			return s.substring(0, m.start());			
		} else {
			return "(B) parseEventLocation() Failure";
		}

	}


	/**
	 * Extract the organizer of the event
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String the event organizer
	 */
	public String parseEventOrganizer(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pContent\"><b>Organizer:</b>");
		Matcher m = p.matcher(xmlString);
		int start = 0;
		if (m.find()){
			start = m.end();
		} else { 
			return "(A) parseEventOrganizer() Failure"; 
		}

		String s = xmlString.substring(start, xmlString.length());

		p = Pattern.compile("</p>");
		m = p.matcher(s);
		if (m.find()) {
			return s.substring(0, m.start());			
		} else {
			return "(B) parseEventOrganizer() Failure";
		}

	}


	//***** Description
	/**
	 * Extract the event description
	 * @param xmlString TODO: Need description of what's in this param
	 * @return String the event description
	 */
	public String parseEventDescription(String xmlString) {
		Pattern p = Pattern.compile("<p class=\"pContentDescText\">");
		Matcher m = p.matcher(xmlString);
		int start = 0;
		if (m.find()){
			start = m.end();
		} else { 
			return "(A) parseEventDescription() Failure"; 
		}

		String s = xmlString.substring(start, xmlString.length());
		start = 0;

		p = Pattern.compile("<p class=");
		m = p.matcher(s);
		if (m.find()) {
			s = s.substring(0, m.start());
		} else {
			return "(B) parseEventDescription() Failure";
		}
		s = s.replace("<br />", NEWLINE);	//	remove 'break'
		s = s.replace("</p>", NEWLINE);		//	remove 'p-break'
		return s;			

	}

}
