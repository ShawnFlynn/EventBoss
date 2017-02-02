package com.tssg.eventboss2;

public class Constants {

	public static final String INTENT_ACTION_FILTER_CRITERIA = "tssg.eventboss.ui.FilterCriteria";
	public static final String FIND_KEY = "find_key";
	public static final String FIND_VALUE = "key_value";

	public static final String EVENT_TYPE 			  = "Event Type";
	public static final String EVENT_TITLE            = "Title";
	public static final String EVENT_DATE             = "Date";
	public static final String EVENT_ORG              = "Organizer";
	public static final String EVENT_LOC              = "Location";
	public static final String EVENT_DESC             = "Description";
	public static final String EVENT_LONG_DESC        = "Long Description";
	public static final String EVENT_UNKNOWN          = "Unknown";
	
// TODO: These are not used and should be removed. 
	// If that happens, the entire class can be removed -- kathyr
	
//	public static final String INTENT_ACTION_FILTER_CRITERIA = "tssg.eventboss.ui.FilterCriteria";
//	public static final String FIND_KEY = "find_key";
//	public static final String FIND_VALUE = "key_value";
//
//	public static final String EVENT_TYPE 			  = "Event Type";
//	public static final String EVENT_TITLE            = "Title";
//	public static final String EVENT_DATE             = "Date";
//	public static final String EVENT_ORG              = "Organizer";
//	public static final String EVENT_LOC              = "Location";
//	public static final String EVENT_DESC             = "Description";
//	public static final String EVENT_LONG_DESC        = "Long Description";
//	public static final String EVENT_UNKNOWN          = "Unknown";
//
//	//	public static final int RESULT_OK = 1;
	public static final int FIND_REQ = 1;
	public static final int FIND_TEXT_REQ = 2;
	public static final int FIND_TYPE_REQ = 3;
	public static final int FIND_DATE_REQ = 4;

	public static final String DATE_FORMAT = "MM/dd/yyyy - HH:mm"; // rob: See FindCriteria date selector

	/*
	 * This is equivalent to the enum in the FinderImpl class.
	 */

	// TODO decide on how to manage enum, or whether to use string resources.
	public enum FindKeyEnum {
		TYPE("type"), TITLE("title"), ORGANIZER("organizer"), LOCATION(
				"location"), DESCRIPTION("description"), LONGDESCRIPTION(
						"longDescription"), DATE("date");

		private final String key;

		FindKeyEnum(String key) {
			this.key = key;
		}

		// return string version of enumeration.
		private String key() {
			return key;
		}

		@Override
		// return string version of enumeration.
		public String toString() {
			return this.key();
		}
	}

}
