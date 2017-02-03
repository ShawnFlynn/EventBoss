package com.tssg.eventboss2;

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

/**
 * Fragment which contains the saved-list activity.
 * ========================================================
 */
public class SavedSectionFragment extends EventBossListFragment {

    static final String TAG = "SavedSectionFragment";  // log's tag

//	private SimpleCursorAdapter mAdapter;
//	private Cursor mCursor;
//	private DatabaseHelper dbh;
    Cursor mCursor;
	SimpleCursorAdapter mAdapter;
	DatabaseHelper dbh;
	LayoutInflater mLayoutInflater;
	ViewGroup mViewGroup;
	ListView mLV;
	public static TextView mListHeader;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.v(TAG, "OnCreate: --- CurrentSectionFragment");
		super.onCreate(savedInstanceState);
		dbh = new DatabaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mViewGroup = container;
		mLayoutInflater = inflater;
		Log.v(TAG, "onCreateView: container" +container);
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		
		mCursor = dbh.getCursorSavedEvents();
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DatabaseHelper.KEY_TITLE,
			    				 DatabaseHelper.KEY_STARTTIME,
			    				 DatabaseHelper.KEY_ENDTIME,
			    				 DatabaseHelper.KEY_LOCATION};

		int[] toViews = { R.id.title, R.id.time, R.id.endtime, R.id.location }; 
		
		mLV = getListView();
		mLV.setHeaderDividersEnabled(true); 
		mLV.setDividerHeight(5);				// = divider between list items
		
		// Create an empty adapter we will use to display the loaded data.		// <------------
		// We pass null for the cursor, then update it in onLoadFinished()
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.eventlist_row, mCursor, fromColumns, toViews, 0);
//		setListAdapter(adapter);  //???? what does this do exactly
		mAdapter = adapter;
		
		mListHeader = (TextView) mLayoutInflater.inflate(R.layout.listheader, null); 
		mLV.addHeaderView(mListHeader);

//        EB2MainActivity.updateListHeader(EB2MainActivity.mResources.getString(R.string.Reading));
        EB2MainActivity.updateListHeader("no"+" saved Events");	// does not show

	}
	
	@Override
	public void onLoadFinished() {
		// there is a constructor in EventBossListFragment
		setListAdapter(mAdapter);  //???? what does this do exactly
        EB2MainActivity.updateListHeader("have"+" saved Events");	

	}
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Log.v("SavedSectionFragment (60):", "---> eventFragmentCoordinator ->displayEventDetails (true)");
		Log.v("onListItemClick", "Listview=" + l
				+ ":View=" + v + ":Position=" + position + ":Id=" + id);
		eventFragmentCoordinator.displayEventDetails(Long.toString(id), true);
	}

	@Override
	public void onDestroyView() {
		mCursor.close();
		super.onDestroyView();
	}

	public void updateList() {
		mCursor.close();
		mCursor = dbh.getCursorSavedEvents();
		mAdapter.swapCursor(mCursor);
		setListAdapter(mAdapter);
	}
} // ------- end SavedSectionFragment
