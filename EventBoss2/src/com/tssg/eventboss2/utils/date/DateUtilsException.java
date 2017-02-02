package com.tssg.eventboss2.utils.date;

import java.io.IOException;

/**
 * 
 * @author Kathy
 *
 */
public class DateUtilsException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DateUtilsException(String message, IOException e) {
		super(message, e);
	}

}
