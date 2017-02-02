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
	Cursor mCursor;
	SimpleCursorAdapter mAdapter;
	DatabaseHelper dbh;
	LayoutInflater mLayoutInflater;
	ViewGroup mViewGroup;
//	TextView mHeader;
	ListView mLV;
	public static TextView mListHeader;  //

	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mCursor = dbh.getCursorAllEvents();
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DatabaseHelper.KEY_TITLE,
							DatabaseHelper.KEY_STARTTIME,
							DatabaseHelper.KEY_ENDTIME,
							DatabaseHelper.KEY_LOCATION};
		
		mLV = getListView();
		mLV.setHeaderDividersEnabled(true); 
		mLV.setDividerHeight(5);				// = divider between list items


		// The TextView in simple_list_item_1
		int[] toViews = { R.id.title, R.id.time, R.id.endtime, R.id.location};
		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.eventlist_row, mCursor, fromColumns, toViews, 0);
		mAdapter = adapter;
		
		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null); 
		mLV.addHeaderView(mListHeader);

		EB2MainActivity.updateListHeader( "Reading: " );

	}
	



	@Override
	public void onDestroyView() {
		mCursor.close();
		super.onDestroyView();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.v("CurrentSectionFragment: (75)", "---> eventFragmentCoordinator ->displayEventDetails (false)");
		Log.v("onListItemClick", "Listview=" + l + ": View=" + v + ":Position=" + position + ":Id=" + id);
		eventFragmentCoordinator.displayEventDetails(Long.toString(id), false);
	}

	// also see eventDetailActivity
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(" OnCreate:", "--- CurrentSectionFragment");
		super.onCreate(savedInstanceState);
		dbh = new DatabaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mViewGroup = container;
		mLayoutInflater = inflater;
		// Do we need this?
		Log.v("CurrentSectionFragment:onCreateView:", "container"+container);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	public void updateList() {
		mCursor.close();
		mCursor = dbh.getCursorAllEvents();
		mAdapter.swapCursor(mCursor);
		setListAdapter(mAdapter);
		// TODO Do I really need the setListAdapter call???
	}

}
