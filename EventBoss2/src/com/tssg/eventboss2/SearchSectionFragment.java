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
import com.tssg.eventsource.BELEvent;

/*
 *
 * Fragment which contains the search activity.
 * ======================================================================================
 */
public class SearchSectionFragment extends EventBossListFragment implements TextWatcher{

    static final String TAG = "SearchSectionFragment";  // log's tag
	public static final String ARG_SECTION_NUMBER = "section_number";
	public static int mListType = 2;			 	 	// -> SearchList
	private EditText mSearchText;
	private String mSearch= "";
	
	public static long mId;
	public BELEvent mEvent;
	private DatabaseHelper dbh;
	private Cursor mCursor;
	private SimpleCursorAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		dbh = new DatabaseHelper(getActivity());
		Log.e(TAG,"onCreate: --- in Search");
		Log.v(TAG, "( SearchSectionFragment.onCreateView: "+"before cursor, dbh:"+dbh+", search string: "+mSearch);
		mCursor = dbh.getCursorSearchEvents(mSearch);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG,"onCreateView: --- in Search");
		View rootView = inflater.inflate(R.layout.fragment_section_launchpad,
				container, false);
//		Bundle args = getArguments();
		mSearchText = (EditText) rootView.findViewById(R.id.searchText);
		mSearchText.addTextChangedListener(this);
		// MakeToast.makeToast(MainActivity.context, "---in DummySection",
		// MakeToast.LEVEL_USER);
		// view =/= context
		Log.v("SearchSectionFragment:search_section_text:layout", " "+ rootView);

//		Log.v(TAG, "( SearchSectionFragment.onCreateView: "+"before cursor, dbh:"+dbh+", search string: "+mSearch);
//		mCursor = dbh.getCursorSearchEvents(mSearch);
		Log.v(TAG, "( SearchSectionFragment.onActivityCreated: "+"after, mCursor: "+mCursor);

		// For the cursor adapter, specify which columns go into which views
		String[] fromColumns = { DatabaseHelper.KEY_TITLE,
								 DatabaseHelper.KEY_DESCRIPTION,
								 DatabaseHelper.KEY_EVENTID};
		int[] toViews = { R.id.title, R.id.description }; // The TextView a simple_list_item_1

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
		Log.v("SearchdSectionFragment (onListItemClick):", "---> eventFragmentCoordinator ->displayEventDetails (2)");
		Log.v("SearchSectionFragment", "Listview=" + l
				+ ":View=" + v + ":Position=" + position + ":Id=" + id);
		
		// here we need to get the Id of the original record
		// id is selection in the now updated list (search result)
		// it contains the id of the event in the list (loaded from current list)
		mId = id;
		Log.v("SearchSectionFragment", "onListItemClick:Id= " + id+" mId= "+ mId);
		String idStr = Long.toString(mId);	
		mEvent = dbh.getEventById( idStr );
		Log.v("SearchSectionFragment", "onListItemClick:Id= " + id+" mId= "+ mId+" : "+ mEvent);

		eventFragmentCoordinator.displayEventDetails(Long.toString(id), 2);
	}
	
	public void updateSearch() {
		mCursor.close();
		Log.v(TAG, "( SearchSectionFragment.updateSearch: "+"before cursor, dbh:"+dbh);
		Log.v(TAG, "( SearchSectionFragment: "+"search term: "+mSearch);
		mCursor = dbh.getCursorSearchEvents(mSearch);
		Log.v(TAG, "( SearchSectionFragment.updateSearch: "+"after cursor, dbh:"+dbh);
		mAdapter.swapCursor(mCursor);
		Log.v(TAG, "( SearchSectionFragment: "+"after cursor swap");
		setListAdapter(mAdapter);

	}
	
	public void afterTextChanged(Editable text) {
		// TODO Auto-generated method stub
		mSearch = text.toString();
		Log.v(TAG, "( SearchSectionFragment:afterTextChanged: "+mCursor);
		updateSearch();
	}

	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
			int arg3) {
		// TODO Auto-generated method stub
		Log.v(TAG, "( SearchSectionFragment:beforeTextChanged: "+"cursor: "+ mCursor+", dbh:"+dbh);
	}

	public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		Log.v(TAG, "( SearchSectionFragment:onTextChanged: "+"cursor: "+ mCursor+", dbh:"+dbh);
	}
	

	@Override
	public void onDestroyView() {
//		mCursor.close();
//		Log.v(TAG, "( SearchSectionFragment.onDestroyView: "+"cursor is closed, dbh:"+dbh);
		super.onDestroyView();
	}

} // ------- end SearchSectionFragment

