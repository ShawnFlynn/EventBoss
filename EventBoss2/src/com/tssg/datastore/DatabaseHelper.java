
package com.tssg.datastore;

import java.util.List;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tssg.eventboss2.EB2MainActivity;
import com.tssg.eventboss2.EB2MainActivity.event_list;
import com.tssg.eventsource.BELEvent;

/*
 *  Does the heavy lifting with the database. Handles database
 *  creation, insertion, and queries.
 */
public class DatabaseHelper extends SQLiteOpenHelper
{

	static SQLiteDatabase mDb = null;

	// Version of database structure to force onUpdate() to be called 
	// when the version is bumped on an "older" database version
	private static final int DATABASE_VERSION = 3;

	static final String LOG_TAG = "DatabaseHelper";

	// note: this is the key to the database table;
	// it is distinct from the eventId field.
	static final String KEY_ROWID = "_id";	// database row_id

	public static final String KEY_EVENTID         = "eventId";	// event fields
	public static final String KEY_FEEDID          = "feedId";
	public static final String KEY_TITLE           = "title";
	public static final String KEY_STARTTIME       = "startTime";
	public static final String KEY_ENDTIME         = "endTime";
	public static final String KEY_TYPE            = "type";
	public static final String KEY_LINK            = "link";
	public static final String KEY_ORGANIZER       = "organizer";
	public static final String KEY_LOCATION        = "location";
	public static final String KEY_DESCRIPTION     = "description";
	public static final String KEY_LONGDESCRIPTION = "longDescription";


	/**
	 * Database creation SQL statement
	 */
	static final String DATABASE_WEB   	 = "webEvents";		//m_webEventsList
	static final String DATABASE_SAVED   = "savedEvents";	//m_savedEventsList

	// "Search" database name
	static private String DATABASE_SEARCH = DATABASE_WEB;
	// Last database loaded
	public static event_list LAST_DB = event_list.Current;

	// Overall database name
	// contains both Current and Saved tables
	static final String DATABASE_NAME = "EventStore";

	static final String TABLE_DEF  = 
			 "( " + KEY_ROWID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ "eventId Integer ,"
			+ "feedId Integer,"
			+ "title TEXT NOT NULL,"
			+ "startTime TEXT , "
			+ "endTime TEXT , "
			+ "type TEXT , "
			+ "organizer TEXT , "
			+ "link TEXT , "
			+ "location TEXT ,"
			+ "description TEXT ,"
			+ "longDescription TEXT );";

	// the web events table
	String CREATE_WEB_TABLE   = "CREATE TABLE "
								+  DATABASE_WEB
								+ " "
								+ TABLE_DEF;

	// the saved events table
	String CREATE_SAVED_TABLE = "CREATE TABLE "
								+  DATABASE_SAVED
								+ " "
								+ TABLE_DEF;
	
	static final String DROP_WEB_TABLE   = "DROP TABLE IF EXISTS "
											+ DATABASE_WEB + ";"; 

	static final String DROP_SAVED_TABLE = "DROP TABLE IF EXISTS "
											+ DATABASE_SAVED + ";"; 


	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		Log.i(LOG_TAG, "DatabaseHelper()");
}


	@Override
	public void onCreate(SQLiteDatabase db)
	{
		Log.i(LOG_TAG, "onCreate()");
		db.execSQL(CREATE_SAVED_TABLE);
		db.execSQL(CREATE_WEB_TABLE);
	}

	@Override
	public void onOpen(SQLiteDatabase db)
	{
		Log.i(LOG_TAG, "onOpen()");
	}

	// This is not tested
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(LOG_TAG, "upGrade()");
		Log.d(LOG_TAG,  "from version " +oldVersion+ " to " +newVersion);

		db.execSQL(DROP_WEB_TABLE);
		db.execSQL(DROP_SAVED_TABLE);
		onCreate(db);
	}


	/**
	 * Returns an opened database. If the database is not opened,
	 * calls
	 * getWritableDatabase() which will open an existing db or create
	 * a new one.  Call this method whenever you need the database; do
	 * not use the variable that holds the reference to the database.
	 * This approach keeps from having to open the database
	 * explicitly; rather it only opens it the 1st time it is needed.
	 * 
	 * @return the database
	 * 
	 * @throws DatastoreException if the database could be neither opened or created
	 */
	SQLiteDatabase getDatabase() throws DatastoreException {

		// Check if we need a new DB
		if (EB2MainActivity.getLast_list() != LAST_DB)
			if (mDb != null)
				closeDB();

		// Set the specific database name
		if (EB2MainActivity.getLast_list() == event_list.Current)
			DATABASE_SEARCH = DATABASE_WEB;
		else
			DATABASE_SEARCH = DATABASE_SAVED;

		Log.i(LOG_TAG, "getDatabase(" +DATABASE_SEARCH+ ")");

		// Create or open the database for read+write.
		// The database is cached when it is opened.
		// It may fail to open or to write to database
		//  but with retry it may succeed.
		if ( mDb == null )
		{
			try
			{
				// Get the specified database
				mDb = getWritableDatabase();

				// Save the current database type
				if (EB2MainActivity.getLast_list() == event_list.Current)
					LAST_DB = event_list.Current;
				else
					LAST_DB = event_list.Saved;
			}
			catch( SQLiteException exp )
			{
				DatastoreException dsExp = new DatastoreException
						("Could not open/create database", exp);

				throw( dsExp );
			}
		}

		return mDb;
	}


	/*
	 * Removes all rows in the Events table.
	 */
	void deleteAllSavedEvents() throws DatastoreException
	{
		Log.i(LOG_TAG, "deleteAllSavedEvents()");

		getDatabase().delete( DATABASE_SAVED, null, null );
	}

	
	//TODO pass database name as an argument
	public static void deleteDatabase( Context context )
	{
		Log.i(LOG_TAG, "deleteDatabase(" + DATABASE_NAME + ")");

		boolean bSucceeded = false;
		try
		{
			bSucceeded = context.deleteDatabase( DatabaseHelper.DATABASE_NAME );
		}
		catch( SQLException exp )
		{
			Log.e( LOG_TAG, "caught SQLException: ", exp );
		}

		Log.d( LOG_TAG, "database deletion "
				+ ( bSucceeded ? "SUCCEEDED" : "FAILED" ) );

	}


	/** 
	 * Retrieve content from database at cursor position, then create and
	 * return BELEvent object
	 * @param Cursor c of database
	 * @param int rowId id of event to retrieve
	 * @return a BELEvent object
	 */
	BELEvent buildBELEvent( final Cursor cursor )
	{
		// Find out what column hold what data
		int eventIdId         = cursor.getColumnIndex(KEY_EVENTID);
		int feedIdId		  = cursor.getColumnIndex(KEY_FEEDID);
		int titleId           = cursor.getColumnIndex(KEY_TITLE);
		int startTimeId       = cursor.getColumnIndex(KEY_STARTTIME);
		int endTimeId         = cursor.getColumnIndex(KEY_ENDTIME);
		int typeId            = cursor.getColumnIndex(KEY_TYPE);
		int linkId            = cursor.getColumnIndex(KEY_LINK);
		int organizerId       = cursor.getColumnIndex(KEY_ORGANIZER);
		int locationId        = cursor.getColumnIndex(KEY_LOCATION);
		int descriptionId     = cursor.getColumnIndex(KEY_DESCRIPTION);
		int longDescriptionId = cursor.getColumnIndex(KEY_LONGDESCRIPTION);

		BELEvent belEvent = new BELEvent(
						cursor.getInt(eventIdId),
						cursor.getInt(feedIdId),
						cursor.getString(titleId),
						cursor.getString(startTimeId),
						cursor.getString(endTimeId),
						cursor.getString(typeId),
						cursor.getString(linkId),
						cursor.getString(organizerId),
						cursor.getString(locationId),
						cursor.getString(descriptionId),
						cursor.getString(longDescriptionId)
				);
			
		
		return belEvent;
	}


	void insert( String tableName, ContentValues initialValues ) throws DatastoreException
	{
		SQLiteDatabase db = getDatabase();

		long newRowId=77; // why 77
		Log.i(LOG_TAG,
				"insert( " 
					+initialValues.getAsString(DatabaseHelper.KEY_EVENTID)+
				" )");

		List<BELEvent> eventList = getAllStoredEvents();
		boolean doIt = true;
		for (BELEvent belEvent: eventList)
		{
			Log.d(LOG_TAG, "#######  event Id= " + belEvent.getId());
			if ( belEvent.getId().equals(initialValues.getAsInteger(DatabaseHelper.KEY_EVENTID)) ) {
				Log.d(LOG_TAG, ">>>>>> Duplicate event Id = " + belEvent.getId());
				doIt = false;
			}
		}

		if( doIt ) {
			// We should always be able to insert so use the throw version to catch errors.
			try
			{
				newRowId = db.insertOrThrow( tableName, null, initialValues);
			}
			catch( SQLException exp )
			{
				throw new DatastoreException("An unexpected SQLException error occured", exp );
			}
			Log.d(LOG_TAG, "$$$ DatabaseHelper.insert  newRowId = " + newRowId);
		}
	}
	

	void delete( String tableName, String eventID ) throws DatastoreException
	{
		int numDelRows = -1;

		Log.i(LOG_TAG, "delete( " +tableName+ ", " +eventID+ " )");

		SQLiteDatabase db = getDatabase();

		List<BELEvent> eventList = getAllStoredEvents();

		Log.d(LOG_TAG, "DatabaseHelper.delete eventlistSize() = " + eventList.size());

		for (BELEvent belEvent: eventList) {
			Log.d(LOG_TAG, "@@@@@@  event Id = " + belEvent.getId());
		}
		// We should only be able to delete if we have a valid, existing row id but will use the throw
		// version to catch errors anyway.
		Log.d(LOG_TAG, "@@@@ after printing all event id's");
		try
		{
			Log.d(LOG_TAG, "@@@@  before db.delete ");
			numDelRows = db.delete( DATABASE_SAVED, KEY_EVENTID+"=?",
									new String [] { eventID } );
			Log.d(LOG_TAG, "@@@@  after db.delete ");
		}
		catch( SQLException exp )
		{
			throw new DatastoreException(
				"An unexpected SQLException error occured in delete", exp );
		}
		Log.d(LOG_TAG, "DatabaseHelper.delete numDelRows = " + numDelRows);

	}

	public static void closeDB() {
		Log.i(LOG_TAG, "closeDB()");

		mDb.close();
		mDb = null; // Required to force open of a new database instance.
		return;
	}
	
	// Copy Event in current list with this id into the saved list
	public void saveEvent(String id) {
		Log.i(LOG_TAG, "saveEvent( " +id+ " )");
		
		try {
			SQLiteDatabase db = getDatabase();
			Cursor cursor = db.query(DATABASE_WEB,null,KEY_ROWID+" = ?",
					new String[]{id},null,null,KEY_ROWID+" ASC",null);
			cursor.moveToFirst();
			ContentValues values = new ContentValues();
			Log.d(LOG_TAG,
				"Saving Event: Database id = " +id+ 
				":Cursor row count = " +cursor.getCount());
			DatabaseUtils.cursorRowToContentValues(cursor, values);
			db.insert(DATABASE_SAVED, null, values);
			Log.d(LOG_TAG, "Added saved value from Database id = " +id);
			cursor.close();
		} catch (DatastoreException dataExcp) {
			Log.e(LOG_TAG,
					">>>>>  saveEvent: failed to retrieve the database",
					dataExcp);
		}
	}
	

	// Copy Event in current list with this id into the saved list
	public void deleteSavedEvent(String id) {
		Log.i(LOG_TAG, "deleteSavedEvent( " +id+ " )");

		try {
			SQLiteDatabase db = getDatabase();
			db.delete(DATABASE_SAVED, KEY_ROWID+" = ?", new String[]{id});
		} catch (DatastoreException dataExcp) {
			Log.e(LOG_TAG,
					">>>>>  deleteSavedEvent: failed to purge the database",
					dataExcp);
		}
	}
	
	// Get a cursor to fill a listview with the data from the database
	public Cursor getCursorAllEvents() {
		Log.i(LOG_TAG, "getCursorAllEvents()");
		
		try {
			SQLiteDatabase db = getDatabase();
			Cursor cursor = db.query(DATABASE_WEB,
									 null,null,null,null,null,
									 KEY_ROWID + " ASC", null);
			return cursor;
		} catch (DatastoreException dataExcp) {
			Log.e(LOG_TAG,
					">>>>>  DatabaseHelper:getCursorAllEvents: failed to retrieve the webEvents",
					dataExcp);
			return null;
		}
	}
	
	public Cursor getCursorSavedEvents() {
		Log.i(LOG_TAG, "getCursorSavedEvents()");

		try {
			SQLiteDatabase db = getDatabase();
			Cursor cursor = db.query(DATABASE_SAVED,
									 null,null,null,null,null,
									 KEY_ROWID + " ASC"
									 ,null);
			return cursor;
		} catch (DatastoreException dataExcp) {
			Log.e(LOG_TAG,
					">>>>>  DatabaseHelper:getCursorSavedEvents: failed to retrieve the savedEvents",
					dataExcp);
			return null;
		}
	}

	public Cursor getCursorSearchEvents(String keyword) {
		Log.i(LOG_TAG, "getCursorSearchEvents()");

		try {
			SQLiteDatabase db = getDatabase();
			keyword = "%"+keyword+"%";
			Cursor cursor;
			if ((keyword == null) || (keyword.equals(""))) {
				cursor = db.query(DATABASE_SEARCH,
									null,null,null,null,null,
									KEY_ROWID + " ASC", null);
			} else {
				cursor = db.query(DATABASE_SEARCH, null,
						KEY_TITLE+" LIKE ? OR "+
						KEY_STARTTIME+" LIKE ? OR "+
						KEY_ENDTIME+" LIKE ? OR "+
						KEY_LOCATION+" LIKE ? OR "+
						KEY_LONGDESCRIPTION+" LIKE ?",
						new String[]{keyword,keyword,keyword,keyword,keyword},
						null,null,KEY_ROWID + " ASC", null);
			}
			return cursor;
		} catch (DatastoreException dataExcp) {

			Log.e(DatabaseHelper.LOG_TAG,
				">>>>>  DatabaseHelper:getCursorSearchEvents: failed to retrieve the database",
				dataExcp);
			return null;
		}
	}
	
	public BELEvent getEventById(String id) {
		Log.i(LOG_TAG, "getEventByID( " +id+ " )");

		try {
			SQLiteDatabase db = getDatabase();
			Cursor cursor = db.query(DATABASE_WEB,null,KEY_ROWID + " = ?",
					new String[]{id}, null,null,KEY_ROWID + " ASC", null);
			cursor.moveToFirst();
			BELEvent result = buildBELEvent(cursor);
			cursor.close();
			return result;
		} catch (DatastoreException e) {
			Log.e(DatabaseHelper.LOG_TAG,
				">>>>>  DatabaseHelper:getCursorAllEvents: failed to retrieve the database",
				e);
			return null;
		}
	}

	public BELEvent getSavedEventById(String id) {
		Log.i(LOG_TAG, "getSavedEventByID( " +id+ " )");
	
		try {
			SQLiteDatabase db = getDatabase();
			Cursor cursor = db.query(DATABASE_SAVED,null,KEY_ROWID+" = ?",
									 new String[]{id},
									 null,null,
									 KEY_ROWID + " ASC",
									 null);
			cursor.moveToFirst();
			BELEvent result = buildBELEvent(cursor);
			cursor.close();
			return result;
		} catch (DatastoreException e) {
			Log.e(LOG_TAG,
				">>>>>  DatabaseHelper:getCursorAllEvents: failed to retrieve the database",
				e);
			return null;
		}
	}

	/*
	 *  Gets all events from the database and returns them in a list
	 *  of BELEVents which may be empty.
	 * @return the list of events.
	 */
	public List<BELEvent> getAllStoredEvents() throws SQLException {
	
		Log.i(LOG_TAG, "getAllStoredEvents()");
	
		// CB: I'm not sure if my changes in this method reflect the best way
		// to handle the DatastoreException thrown by the 'getDatabase()' method.
		// I Had to add the getDatabase() method to make sure the database member 
		// exists before we try to access it. 
		// My other option for handling the exception was to add the exception to
		// the declaration of this method but that would require us to change all
		// the methods in which this method is called to place a try/catch around it, I think.

		// CB: moved the below line outside of the try block
		//create the BELEvent to store the result to return.
		List<BELEvent> belEventList = new Vector<BELEvent>();

		try
		{
			SQLiteDatabase db = getDatabase();

			Cursor cursor = db.query(DATABASE_SAVED, new String[]
				{
					KEY_ROWID,
					KEY_EVENTID,
					KEY_FEEDID,
					KEY_TITLE,
					KEY_STARTTIME,
					KEY_ENDTIME, 
					KEY_TYPE,
					KEY_LINK,
					KEY_ORGANIZER,
					KEY_LOCATION,
					KEY_DESCRIPTION,
					KEY_LONGDESCRIPTION
				},
				null,null,null,null,null,null);

			// With the cursor get the count of events in the database;
			// if we didn't get anything
			if ( cursor.getCount() <= 0 )
			{
				cursor.close();
				return belEventList;
			}

			// Move the cursor to the first event in the database.
			boolean bFirst = cursor.moveToFirst();

			if (bFirst != true) {
				Log.d(LOG_TAG, "Can't move the cursor to the first event");
			}

			while( !cursor.isAfterLast() )
			{
				BELEvent oneEvent = buildBELEvent( cursor );

				belEventList.add( oneEvent );

				cursor.moveToNext();
			}
			cursor.close();
		}
		catch (DatastoreException dataExcp)
		{
			// CB: need to make a better error message and probably output it to the Android screen
			Log.e(LOG_TAG,
				">>>>>  DatabaseHelper:getAllStoredEvents: failed to retrieve the database",
				dataExcp);
		}
		return belEventList;
	}


	public Integer getNumDBRows() throws DatastoreException {
		Log.i(LOG_TAG, "getNumDBRows()");

		SQLiteDatabase db = getDatabase();

		Integer numRows = 0;

		if ( db != null )
		{
			Cursor cursor = db.query(DATABASE_SAVED, new String[]
					{
						KEY_ROWID,
						KEY_EVENTID,
						KEY_FEEDID,
						KEY_TITLE,
						KEY_STARTTIME,
						KEY_ENDTIME, 
						KEY_TYPE,
						KEY_LINK,
						KEY_ORGANIZER,
						KEY_LOCATION,
						KEY_DESCRIPTION,
						KEY_LONGDESCRIPTION
					},
					null,null,null,null,null,null);

			numRows = cursor.getCount();
			cursor.close();
		} else {
			Log.d(LOG_TAG, ">>>>>  getNumDBRows: db == null");
		}

		Log.d(LOG_TAG, ">>>>>  getNumDBRows: numRows == " + numRows);

		return (numRows);
	}

	public Integer getNumSelectDBRows(String title, String startTime) throws DatastoreException {
		Log.i(LOG_TAG, "getNumSelectDBRows()");

		SQLiteDatabase db = getDatabase();

		Integer numRows = 0;
		if ( db != null )
		{
			Cursor cursor = db.query(DATABASE_SAVED, new String[]
					{
						KEY_ROWID,
						KEY_EVENTID,
						KEY_FEEDID,
						KEY_TITLE,
						KEY_STARTTIME,
						KEY_ENDTIME, 
						KEY_TYPE,
						KEY_LINK,
						KEY_ORGANIZER,
						KEY_LOCATION,
						KEY_DESCRIPTION,
						KEY_LONGDESCRIPTION
					},
					KEY_TITLE + "=?" + " and " + KEY_STARTTIME + "=?",
					new String[]
					{
						title,
						startTime
					}
					,null,null,null,null);

			numRows = cursor.getCount();
			cursor.close();	
		} else {
			Log.d(LOG_TAG, ">>>>>  getNumSelectDBRows: db == null");
		}

		Log.d(LOG_TAG, ">>>>>  getNumSelectDBRows: numRows == " + numRows);

		return (numRows);
	}

	
	/*********************************************************
	 * 	WebEvents table.
	 *********************************************************/

	/*
	 * Retrieves all rows in the WebEvents table.
	 */
	public List<BELEvent> getAllWebEvents() throws SQLException
	{
	
		Log.i(LOG_TAG, "getAllWebEvents()");

		// CB: I'm not sure if my changes in this method reflect the best way
		// to handle the DatastoreException thrown by the 'getDatabase()' method.
		// I Had to add the getDatabase() method to make sure the database member 
		// exists before we try to access it. 
		// My other option for handling the exception was to add the exception to
		// the declaration of this method but that would require us to change all
		// the methods in which this method is called to place a try/catch around it, I think.

		// CB: moved the below line outside of the try block
		//create the BELEvent to store the result to return.
		List<BELEvent> belEventList = new Vector<BELEvent>();

		try
		{
			SQLiteDatabase db = getDatabase();

			Cursor cursor = db.query(DATABASE_WEB, new String[]
				{
					KEY_ROWID,
					KEY_EVENTID,
					KEY_FEEDID,
					KEY_TITLE,
					KEY_STARTTIME,
					KEY_ENDTIME, 
					KEY_TYPE,
					KEY_LINK,
					KEY_ORGANIZER,
					KEY_LOCATION,
					KEY_DESCRIPTION,
					KEY_LONGDESCRIPTION
				},
				null,null,null,null,null,null);

			// With the cursor get the count of events in the database;
			// if we didn't get anything
			if ( cursor.getCount() <= 0 )
			{
				cursor.close();
				return belEventList;
			}

			// Move the cursor to the first event in the database.
			boolean bFirst = cursor.moveToFirst();

			if (bFirst != true) {
				Log.d( LOG_TAG, "Can't move the cursor to the first event");
			}

			while( !cursor.isAfterLast() )
			{
				BELEvent oneEvent = buildBELEvent( cursor );

				belEventList.add( oneEvent );

				cursor.moveToNext();
			}
			cursor.close();
		}
		catch (DatastoreException dataExcp)
		{
			// CB: need to make a better error message and probably output it to the Android screen
			Log.e(LOG_TAG,
				">>>>>  DatabaseHelper:getAllStoredEvents: failed to retrieve the database",
				dataExcp);
		}
		return belEventList;	
	}

	/*
	 * Removes all rows in the WebEvents table.
	 */
	void deleteAllWebEvents() throws DatastoreException
	{
		Log.i(LOG_TAG, "deleteAllWebEvents()");

		getDatabase().delete( DATABASE_WEB, null, null );
	}

	// Not public, used for testing
	SQLiteDatabase getExistingDatabase() {
		Log.i(LOG_TAG, "getExistingDatabase()");

		return mDb;
	}

}	//	end - DatabaseHelper class
