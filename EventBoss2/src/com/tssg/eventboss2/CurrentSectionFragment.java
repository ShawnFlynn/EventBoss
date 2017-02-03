package com.tssg.eventboss2;

//import java.text.SimpleDateFormat;
//import java.util.Locale;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.database.Cursor;
import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentPagerAdapter;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.tssg.datastore.DatabaseHelper;

/*
 * A fragment for the view of the current list display.
 * ======================================================================================
 */
public class CurrentSectionFragment extends EventBossListFragment {

	protected final String TAG = getClass().getSimpleName();

	//	public static boolean mListType = false;		// not the saved list
	public static int mListType = 0;					// -> currentList
	public static TextView mListHeader;
	public static int mPosition = -1;	// probably not needed
	public static long mId;

	Cursor mCursor = null;
	SimpleCursorAdapter mAdapter;
	static int mEventItemCount;
	DatabaseHelper dbh = null;

	LayoutInflater mLayoutInflater;
	ViewGroup mViewGroup;
	ListView mLV;

	// also see eventDetailActivity
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "OnCreate()");
		super.onCreate(savedInstanceState);
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
		// Do we need this?
		Log.d(TAG, "onCreateView: container :" + container);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.i(TAG, "OnActivityCreated()");

		Log.d(TAG, "before cursor: "+ mCursor +", dbh: "+dbh);
		if (mCursor == null) {
			mCursor = dbh.getCursorAllEvents();
		}
 		Log.d(TAG, "after cursor: " + mCursor);
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = {DatabaseHelper.KEY_TITLE,
								DatabaseHelper.KEY_STARTTIME,
								DatabaseHelper.KEY_ENDTIME,
								DatabaseHelper.KEY_LOCATION};

		mLV = getListView();
		mLV.setHeaderDividersEnabled(true); 
		mLV.setDividerHeight(15);			// = divider between list items

		// The TextView in simple_list_item_1
		int[] toViews = { R.id.title, R.id.time, R.id.endtime, R.id.location};
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(getActivity(),
											R.layout.eventlist_row, mCursor,
											fromColumns, toViews, 0);

		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader,
															null);
		mLV.addHeaderView(mListHeader);

		updateListHeader(EB2MainActivity.mResources.getString(R.string.Reading));
	}

	@Override
	public void onDestroyView() {
		Log.i(TAG, "OnDestroyView()");
		super.onDestroyView();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.i(TAG, "onListItemClick(" + position + ")");

		Log.d(TAG, "(106)---> eventFragmentCoordinator ->displayEventDetails ("+mListType+")");
		Log.d(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + id);
		if( position > 0 ) {
			mPosition = position;
			mId = id;
			Log.d(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + mId);

			eventFragmentCoordinator.displayEventDetails(Long.toString(mId), 0);
		}
	}


	public void updateList() {
		Log.i(TAG, "updateList()");

		Log.d(TAG, "currentSection: getCursorAllEvents"); 
//		mCursor.close();
//		mCursor = dbh.getCursorAllEvents();
		mAdapter.swapCursor(mCursor);
//		Log.d(TAG, "currentSection getCursorAllEvents //Do I really need the setListAdapter call?");
		setListAdapter(mAdapter);
		EB2MainActivity.setTabLabel(EB2MainActivity.tab0Label);
	}

	void updateListHeader( String extraText )  {
		// Create a list-header (TextView) and add it to the list like this:
		// mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null);
		// mLV.addHeaderView(mListHeader);

		SimpleDateFormat simpFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm",
														Locale.getDefault() );
		Date m_channelDate = new Date(System.currentTimeMillis());
		String channelDate = m_channelDate == null?
					"--" : simpFormat.format(EB2MainActivity.m_channelDate);

		// this should be the current date or the date when data was saved into the database
		mListHeader.setText(extraText + "(" + channelDate + ") "
							+ mEventItemCount + " Events");
		Log.d(TAG, extraText +" "+ channelDate +","
					+ " "+  mEventItemCount + " Events");
	}

	/* save the Event( mId ) into the Saved database */
	//public void storeInSaved ( long mId )	{
	public void storeInSaved ()	{
		Log.i(TAG, "storeInSaved()");

		String strEvent = String.format(Locale.getDefault(), "%d", mId);

		Log.d(TAG, "currentSection strEvent: "+strEvent +" from mId :"+ mId);
		dbh.saveEvent( strEvent );
	}
}
