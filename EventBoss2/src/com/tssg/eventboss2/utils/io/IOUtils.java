/**
 * 
 */
package com.tssg.eventboss2.utils.io;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.tssg.eventboss2.utils.io.IOUtilsException;

/**
 * Class containing various IO methods. This class implements the Singleton 
 * design pattern for IO access.
 * 
 * @author Kathy
 * @deprecated appears to be unused 5/2014
 */
@Deprecated
public final class IOUtils {
	
	private static IOUtils instance = null;
	
	/**
	 * Private constructor for Singleton class
	 */
	private IOUtils() {
		
	}
	
	public static IOUtils getInstance() {
		if (instance == null) {
			return new IOUtils();
		}
		return instance;
	}
			
	/**
	 * Create a {@link URL} from a text string
	 * @param feedUrl String the URL path
	 * @return URL
	 * @throws IOUtilsException on error
	 */
	public URL createUrl(String feedUrl) throws IOUtilsException{
		try {
			return new URL(feedUrl);
		} catch (MalformedURLException e) {
			String message = "Failed to create URL: " + feedUrl + " Exception: " + e.toString();
			//Log.e( "IOUtils.createUrl(String)", message );
			throw new IOUtilsException(message, e);
		}
	} // createUrl
	
	/**
	 * Open a {@link URLConnection} 
	 * @param feedUrl URL The RSS path to open
	 * @return URLConnection
	 * @throws IOUtilsException on error
	 */
	public URLConnection openUrlConnection(URL feedUrl) throws IOUtilsException {
		try {
			return feedUrl.openConnection();
		} catch (IOException e) {
			String message = "Unable to open URL connection: " + feedUrl +" Exception: " + e.toString();
			//Log.e( "IOUtils.openUrlConnection(URL)", message );
			throw new IOUtilsException( message, e);
		}
	}	// openUrlConnection

	/**
	 * Create an {@link InputStream} that the events will be read from.
	 * @param connection The {@link URLConnection} to read from 
	 * @return InputStream
	 * @throws IOUtilsException on error
	 */
	public InputStream getInputStream(URLConnection connection) throws IOUtilsException {
		try {
			return connection.getInputStream();
		} catch (IOException e) {
			String message = "Failed to get input stream: " + e.toString();
			//Log.e( "IOUtils.getInputStream(URLconnection)", message );
			throw new IOUtilsException( message, e);
		}
	}	// getInputStream
	
	/**
	 * Opens a file somewhere on the device and returns the InputStream pointer
	 * @param fileName - the path to the file to be opened
	 * @return InputStream - the stream that is opened
	 * @throws IOUtilsException on error
	 */
	public InputStream getInputStream(String fileName) throws IOUtilsException {

		try {
			FileInputStream fileStream = new FileInputStream (fileName);
			return fileStream;

		} catch (IOException e) {
			String message = "Failed to get input stream: " + e.toString();
			//Log.e( "IOUtils.getInputStream(String)", message );
			throw new IOUtilsException( message, e);
		}
	}	
}
