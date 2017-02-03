package com.tssg.eventboss2;

import android.annotation.SuppressLint;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "OnCreate()");

		if ( dbh == null ) {
			dbh = new DatabaseHelper(getActivity());
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
								Bundle savedInstanceState) {

		Log.i(TAG, "OnCreateView()");

		mViewGroup = container;
		mLayoutInflater = inflater;
		Log.d(TAG, "container: " + container);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@SuppressLint("InflateParams")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i(TAG, "OnActivityCreated()");

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

		mEventItemCount = mCursor.getCount();
		mLV = getListView();
		mLV.setHeaderDividersEnabled(true); 
		mLV.setDividerHeight(5);			// = divider between list items
		
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.eventlist_row, mCursor, fromColumns, toViews, 0);
		setListAdapter(mAdapter);

		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader,
															null);
		mLV.addHeaderView(mListHeader);

		updateList();   // can not implement in SavedSectionFragment

	}	//  end - OnActivityCreated()

	@Override
	public void onLoadFinished() {

		Log.i(TAG, "OnLoadFinished()");

		// there is a constructor in EventBossListFragment
		setListAdapter(mAdapter);
		updateListHeader("have " + mEventItemCount + " saved Events");
	}

	@Override
	public void onResume() {
		super.onResume();

		Log.i(TAG, "OnResume()");

		// update the list  in case there an addition/deletion
		updateList();
	}
	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {

		Log.i(TAG, "OnListItemClick()");

		Log.d(TAG, "Listview=" + l
					+ ":View=" + v
					+ ":Position=" + position
					+ ":Id=" + id);

		if( position > 0 ) {	// it's using only mId
			mPosition = position;
			mId = id;
			Log.d(TAG, "Position=" + mPosition
						+ ":mId=" + mId);

			eventFragmentCoordinator.displayEventDetails(Long.toString(mId), 1);
		}
	}	//  end - onListItemClick()

	@Override
	public void onDestroyView() {

		Log.i(TAG, "OnDestroyView()");

		mCursor.close();
		super.onDestroyView();
	}

	public void updateList() {

		Log.i(TAG, "updateList()");

		mCursor = dbh.getCursorSavedEvents();
		mAdapter.swapCursor(mCursor);
		setListAdapter(mAdapter);
		mEventItemCount = mCursor.getCount();
		Log.d(TAG, "count: " + mEventItemCount);
		updateListHeader("update saved list-header");
	}

	void updateListHeader( String extraText )  {

		Log.i(TAG, "updateListHeader(" + extraText + ")");

		SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm",
														Locale.getDefault() );
		Date m_channelDate = new Date(System.currentTimeMillis());
		String channelDate = m_channelDate == null?
					"--" : simpFormat.format(EB2MainActivity.m_channelDate);

		// this should be the current date or the date when data was saved into the database
		mListHeader.setText( extraText + "(" + channelDate + ") "
										+ mEventItemCount + " Events");
		Log.d(TAG, extraText + " " 
				 + channelDate + ", " 
				 +  mEventItemCount + " Events");
	}

}	//  end - SavedSectionFragment
