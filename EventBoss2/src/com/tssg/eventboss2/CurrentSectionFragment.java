package com.tssg.eventboss2;

//import java.text.SimpleDateFormat;
//import java.util.Locale;

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

    static final String TAG = "CurrentSectionFragment";  // log's tag
//	public static boolean mListType = false;			 // not the saved list
	public static int mListType = 0;			 	 	 // -> currentList
	public static TextView mListHeader;
	public static int mPosition = -1;	// probably not needed
	public static long mId;

    Cursor mCursor;
	SimpleCursorAdapter mAdapter;
	DatabaseHelper dbh = null;
	LayoutInflater mLayoutInflater;

	ViewGroup mViewGroup;
	ListView mLV;



	// also see eventDetailActivity
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "OnCreate: --- CurrentSectionFragment");
		super.onCreate(savedInstanceState);
        if ( dbh == null ) {
            dbh = new DatabaseHelper(getActivity());
        }
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		mViewGroup = container;
		mLayoutInflater = inflater;
		// Do we need this?
		Log.v(TAG, "onCreateView: container" +container);
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Log.v(TAG, "( CurrentSectionFragment.onActivityCreated: "+"before cursor, dbh:"+dbh);
		mCursor = dbh.getCursorAllEvents();
		Log.v(TAG, "( CurrentSectionFragment.onActivityCreated: "+"after cursor");
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = {DatabaseHelper.KEY_TITLE,
							    DatabaseHelper.KEY_STARTTIME,
							    DatabaseHelper.KEY_ENDTIME,
							    DatabaseHelper.KEY_LOCATION};

		mLV = getListView();
		mLV.setHeaderDividersEnabled(true); 
		mLV.setDividerHeight(15);				// = divider between list items

		// The TextView in simple_list_item_1
		int[] toViews = { R.id.title, R.id.time, R.id.endtime, R.id.location};
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.eventlist_row, mCursor, fromColumns, toViews, 0);

		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null); 
		mLV.addHeaderView(mListHeader);

        EB2MainActivity.updateListHeader(EB2MainActivity.mResources.getString(R.string.Reading));
	}

	@Override
	public void onDestroyView() {
        mCursor.close();
		super.onDestroyView();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.v(TAG, "(106)---> eventFragmentCoordinator ->displayEventDetails ("+mListType+")");
		Log.v(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + id);
        if( position > 0 ) {
			mPosition = position;
			mId = id;
			Log.v(TAG, "onListItemClick: Position=" + mPosition + ":mId=" + mId);

			eventFragmentCoordinator.displayEventDetails(Long.toString(mId), 0); // 0 = current
        }
	}


	public void updateList() {
		Log.v(TAG, "currentSection: getCursorAllEvents"); 
		mCursor.close();
		mCursor = dbh.getCursorAllEvents();
		mAdapter.swapCursor(mCursor);
        // TODO Do I really need the setListAdapter call???
//		Log.v(TAG, "currentSection getCursorAllEvents //Do I really need the setListAdapter call?");
 		setListAdapter(mAdapter);
        EB2MainActivity.setTabLabel(EB2MainActivity.tab0Label);
	}

	/* save the Event( mId ) into the Saved database */
	//public void storeInSaved ( long mId )	{
	public void storeInSaved ()	{
		String strEvent = String.format("%d", mId); // also "" + long

		Log.v(TAG, "currentSection strEvent: "+strEvent +" from mId :"+ mId);

		dbh.saveEvent( strEvent );

	}
}
