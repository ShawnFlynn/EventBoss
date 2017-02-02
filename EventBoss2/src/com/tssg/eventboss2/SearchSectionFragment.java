package com.tssg.eventboss2;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.tssg.datastore.DatabaseHelper;

/*
 *
 * Fragment which contains the search activity.
 * ======================================================================================
 */
public class SearchSectionFragment extends EventBossListFragment implements TextWatcher{

	public static final String ARG_SECTION_NUMBER = "section_number";
	private EditText mSearchText;
	private String mSearch= "";
	private DatabaseHelper dbh;
	private Cursor mCursor;
	private SimpleCursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e("MainActivity: ", "---in Search");
		View rootView = inflater.inflate(R.layout.fragment_section_launchpad,
				container, false);
		// TODO make a fragment for search
//		Bundle args = getArguments();
		mSearchText = (EditText) rootView.findViewById(R.id.searchText);
		mSearchText.addTextChangedListener(this);
		// MakeToast.makeToast(MainActivity.context, "---in DummySection",
		// MakeToast.LEVEL_USER);
		// view =/= context
		Log.v("SearchSectionFragment:search_section_text:layout", " "
				+ rootView);
		
		mCursor = dbh.getCursorSearchEvents(mSearch);
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

		return rootView;
	}


	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// do something with the data
		Log.v("SearchdSectionFragment (60):", "---> eventFragmentCoordinator ->displayEventDetails (false)");
		Log.v("SearchSectionFragment", "Listview=" + l
				+ ":View=" + v + ":Position=" + position + ":Id=" + id);
		eventFragmentCoordinator.displayEventDetails(Long.toString(id), false);
	}

	@Override
	public void onDestroyView() {
		mCursor.close();
		super.onDestroyView();
	}

	
	public void updateSearch() {
		mCursor.close();
		mCursor = dbh.getCursorSearchEvents(mSearch);
		mAdapter.swapCursor(mCursor);
		setListAdapter(mAdapter);
	}
	
	public void afterTextChanged(Editable text) {
		// TODO Auto-generated method stub
		mSearch = text.toString();
		updateSearch();
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		
	}
} // ------- end SearchSectionFragment

