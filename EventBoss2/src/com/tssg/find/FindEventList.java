package com.tssg.find;

import com.tssg.eventsource.*;

import java.util.List;

/**
 * Just has method {@link #match(List, String, String)}
 *  
 * @author Larry Medwin
 * April 7, 2011
 */
public interface FindEventList {
	/**
	 * match List<BELEvent> against a specific field, with a specific value.
	 * @param input List<BELEvent> input event list
	 * @param field String that indicates which part of BELEvent to match
	 * @param value String that indicates value to match
	 * @return List<BELEvent> consisting of matched events
	 */
	public List<BELEvent> match(List<BELEvent> input, String field, String value);
}