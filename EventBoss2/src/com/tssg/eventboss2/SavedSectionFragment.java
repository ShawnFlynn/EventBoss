package com.tssg.eventboss2;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tssg.datastore.DatabaseHelper;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment which contains the saved-list activity.
 * ========================================================
 */
public class SavedSectionFragment extends EventBossListFragment {

	protected final String TAG = getClass().getSimpleName();

	public static int mListType = 1;		// -> saved list
	public static TextView mListHeader;
	public static int mPosition = -1;
	public static long mId;

	private static DatabaseHelper dbh = null;
	static int mEventItemCount;

	Cursor mCursor = null;
	SimpleCursorAdapter mAdapter;
	LayoutInflater mLayoutInflater;
	ViewGroup mViewGroup;

	ListView mLV;

	// Get the EB2 Interface
	EB2Interface EB2 = new EB2MainActivity();

	// Local EB2 resources
	private Resources mResources = EB2.getEB2Resources();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "onCreate()");

		if ( dbh == null ) {
			dbh = new DatabaseHelper(getActivity());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {

		Log.i(TAG, "onCreateView()");

		mViewGroup = container;
		mLayoutInflater = inflater;

		Log.d(TAG, "container: " + container);

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i(TAG, "onActivityCreated()");

		Log.d(TAG, "before cursor, dbh: " + dbh);

		if (mCursor == null) {
			mCursor = dbh.getCursorSavedEvents();
		}

		Log.d(TAG, "after cursor");

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DatabaseHelper.KEY_TITLE,
								 DatabaseHelper.KEY_STARTTIME,
								 DatabaseHelper.KEY_ENDTIME,
								 DatabaseHelper.KEY_LOCATION};

		int[] toViews = { R.id.title, R.id.time, R.id.endtime, R.id.location };

		// Setup the list header
		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null);

		mEventItemCount = mCursor.getCount();
		mLV = getListView();
		mLV.setHeaderDividersEnabled(true);
		mLV.setDividerHeight(5);

		// Add the header to the view
		mLV.addHeaderView(mListHeader);

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(getActivity(),
											R.layout.eventlist_row,
											mCursor,
											fromColumns,
											toViews,
											0);

		// Update the Saved list
		updateList();

	}	//  end - OnActivityCreated()

	@Override
	public void onLoadFinished() {

		Log.i(TAG, "onLoadFinished()");

		setListAdapter(mAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.i(TAG, "onResume()");

		// Update the list in case there are additions/deletions
		updateList();
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Log.i(TAG, "onListItemClick()");

		Log.d(TAG, "Listview=" + l
					+ ":View=" + v
					+ ":Position=" + position
					+ ":Id=" + id);

		if( position > 0 ) {	// it's using only mId
			mPosition = position;
			mId = id;
			Log.d(TAG,  "Position=" +mPosition+
						":mId=" + mId);

			eventFragmentCoordinator.displayEventDetails(Long.toString(mId), 1);
		}
	}	//  end - onListItemClick()

	@Override
	public void onDestroyView() {

		Log.i(TAG, "onDestroyView()");

		mCursor.close();
		setListAdapter(null);

		super.onDestroyView();
	}

	public void updateList() {

		Log.i(TAG, "updateList()");

		mCursor = dbh.getCursorSavedEvents();
		mAdapter.swapCursor(mCursor);
		mEventItemCount = mCursor.getCount();

		Log.d(TAG, "count: " +mEventItemCount);
		updateListHeader(mResources.getString(R.string.title_activity_savelist_display));

		setListAdapter(mAdapter);
	}

	void updateListHeader( String extraText )  {

		Log.i(TAG, "updateListHeader(" +extraText+ ")");

		String tempEvents = null;

		SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm",
														Locale.getDefault() );
		Date m_channelDate = new Date(System.currentTimeMillis());
		String channelDate = m_channelDate == null?
				"--" : simpFormat.format(EB2.getCurrentDate());

		// Get "Event" or "Events" based on event count
		if (mEventItemCount == 1)
			tempEvents = mResources.getString(R.string.Event);
		else
			tempEvents = mResources.getString(R.string.Events);

		// This should be the current date or the date when data was saved into the database
		String tempString = extraText + " @ " +
							channelDate + ": " +
							mEventItemCount +
							" " + tempEvents;

		// Set the Saved list header text
		mListHeader.setText( tempString );

		// Log the modified header string
		Log.d(TAG, tempString);

	}	// end - updateListHeader()

}	//  end - SavedSectionFragment
