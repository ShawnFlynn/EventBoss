package com.tssg.eventboss2;

import android.database.Cursor;
import android.os.Bundle;

import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.tssg.datastore.DatabaseHelper;

/**
 * Fragment which contains the saved-list activity.
 * ==============================
 * ========================================================
 */
public class SavedSectionFragment extends EventBossListFragment {

	private SimpleCursorAdapter mAdapter;
	private Cursor mCursor;
	private DatabaseHelper dbh;
	
	
	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return super.onCreateView(inflater, container, savedInstanceState);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mCursor = dbh.getCursorSavedEvents();
		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DatabaseHelper.KEY_TITLE,
				DatabaseHelper.KEY_DESCRIPTION };
		int[] toViews = { R.id.title, R.id.description }; // The TextView in
															// simple_list_item_1

		// Create an empty adapter we will use to display the loaded data.
		// We pass null for the cursor, then update it in onLoadFinished()
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
				R.layout.eventlist_row, mCursor, fromColumns, toViews, 0);
		setListAdapter(adapter);
		mAdapter = adapter;
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
